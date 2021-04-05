package com.example.imageupload;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//
public class Home extends AppCompatActivity {
    Button Cbutton = null;
    Button Gbutton = null;
    Button Sbutton = null;
    ImageView imageView = null;
    private final int REQ_CODE_SELECT_IMAGE = 100;
    private String img_path = new String();
    private String serverURL = "https://mp-domain-test.kro.kr:7777/mlcloth";  //<<서버주소
    private Bitmap image_bitmap_copy = null;
    private Bitmap image_bitmap = null;
    private String imageName = null;
    public String id;
    private File tempFile;
    public String rslt, num1, num2, num3, num4, num5, color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageView = (ImageView)findViewById(R.id.imageView);
        Intent intent = getIntent();
        id = intent.getExtras().getString("sendid");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        Cbutton = (Button)findViewById(R.id.Cbutton);
        //이미지를 띄울 위젯
        Cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        Gbutton = (Button)findViewById(R.id.Gbutton);
        //이미지를 띄울 위젯
        Gbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        });

        Sbutton = (Button) findViewById(R.id.Sbutton);
        //이미지 전송 버튼
        Sbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resulted rslted = new Resulted();
                rslted.execute();
                Toast.makeText(getApplicationContext(), "이미지 전송 성공", Toast.LENGTH_SHORT).show();
                Log.d("Send", "Success");
                imageView.setImageResource(android.R.color.transparent);
                Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }//end of onCreate()

    public class Resulted extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
                /* 서버연결 */
                DoFileUpload(serverURL, img_path);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            gores();
        }

    }
    public void gores() {
        if (rslt.contains("500")) {
            Toast.makeText(getApplicationContext(), "데이터베이스 오류", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, Result.class);
            intent.putExtra("num1", num1);
            intent.putExtra("num2", num2);
            intent.putExtra("num3", num3);
            intent.putExtra("num4", num4);
            intent.putExtra("num5", num5);
            intent.putExtra("id", id);
            intent.putExtra("color", color);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Toast.makeText(getBaseContext(), "resultCode : " + data, Toast.LENGTH_SHORT).show();*/

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    img_path = getImagePathToUri(data.getData()); //이미지의 URI를 얻어 경로값으로 반환.
                    /*Toast.makeText(getBaseContext(), "img_path : " + img_path, Toast.LENGTH_SHORT).show();*/
                    //이미지를 비트맵형식으로 반환
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    //사용자 단말기의 width , height 값 반환
                    int reWidth = (int) (getWindowManager().getDefaultDisplay().getWidth());
                    int reHeight = (int) (getWindowManager().getDefaultDisplay().getHeight());

                    int degree = getExifOrientation(img_path);
                    image_bitmap = getRotatedBitmap(image_bitmap, degree);

                    //image_bitmap 으로 받아온 이미지의 사이즈를 임의적으로 조절함. width: 280 , height: 400
                    image_bitmap_copy = Bitmap.createScaledBitmap(image_bitmap, 280, 400, true);
                    ImageView image = (ImageView) findViewById(R.id.imageView);  //이미지를 띄울 위젯 ID값
                    image.setImageBitmap(image_bitmap_copy);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }//end of onActivityResult()

    public String getImagePathToUri(Uri data) {
        //사용자가 선택한 이미지의 정보를 받아옴
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        //이미지의 경로 값
        String imgPath = cursor.getString(column_index);
        Log.d("test", imgPath);

        //이미지의 이름 값
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        /*Toast.makeText(Home.this, "이미지 이름 : " + imgName, Toast.LENGTH_SHORT).show();*/
        this.imageName = imgName;

        return imgPath;
    }//end of getImagePathToUri()

    public void DoFileUpload(String apiUrl, String absolutePath) {
        HttpFileUpload(apiUrl, "", absolutePath);
    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {

            FileInputStream mFileInputStream = new FileInputStream(fileName);
            URL connectUrl = new URL(urlString);
            Log.d("Test", "mFileInputStream  is " + mFileInputStream);

            // HttpURLConnection 통신
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data;name=\""+ id +".jpg\";filename=\""+fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.d("Test", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("Test", "File is written");
            mFileInputStream.close();
            dos.flush();
            // finish upload...

            InputStream is = null;
            BufferedReader in = null;
            String data = "";
            // get response
            is = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            String line = null;
            StringBuffer buff = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buff.append(line + "\n");
            }
            rslt = buff.toString().trim();

            Log.e("RECV DATA", data);

            pars(rslt);
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            // TODO: handle exception
        }

    } // end of HttpFileUpload()
    private int getExifOrientation(String filePath) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                }
            }
        }
        return 0;
    }
    private Bitmap getRotatedBitmap(Bitmap bitmap, int degree) {
        if (degree != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (bitmap != tmpBitmap) {
                    bitmap.recycle();
                    bitmap = tmpBitmap;
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
    public void pars(String jsonString) throws ParseException, JSONException {
        JSONParser parser = new JSONParser();

        JSONObject univ = (JSONObject) parser.parse(jsonString);
        JSONArray arr = (org.json.simple.JSONArray) univ.get("result");

        JSONObject tmp1 = (JSONObject)arr.get(0);
        JSONObject tmp2 = (JSONObject)arr.get(1);
        JSONObject tmp3 = (JSONObject)arr.get(2);
        JSONObject tmp4 = (JSONObject)arr.get(3);
        JSONObject tmp5 = (JSONObject)arr.get(4);

        color = (String)univ.get("color");

        num1 = (String)tmp1.get("cloth");
        num2 = (String)tmp2.get("cloth");
        num3 = (String)tmp3.get("cloth");
        num4 = (String)tmp4.get("cloth");
        num5 = (String)tmp5.get("cloth");

    }
} //end of class


