package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ClothSee extends AppCompatActivity {

    ImageView imview;
    public String id, sendid, rslt;
    Bitmap bmp,nbmp;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_see);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        sendid = intent.getExtras().getString("cloth_id");
        imview = (ImageView) findViewById(R.id.imview);
        Log.e("Test", "아이디는" + id + "옷 아이디는" + sendid);

        See se = new See();
        se.execute();

        back = (Button) findViewById(R.id.back);
        //이미지를 띄울 위젯
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClothSee.this.finish();
            }
        });
    }

    public class See extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/cloth_image");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(id).append("&");
                buffer.append("cloth_id").append("=").append(sendid);
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                outStream.flush();
                outStream.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                rslt = buff.toString().trim();
                Log.e("Test", rslt);
                Log.e("RECV DATA", data);
                pars(rslt);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            imview.setImageBitmap(nbmp);
        }
    }

    public void pars(String jsonString) throws ParseException, JSONException {

        JSONParser parser = new JSONParser();
        JSONObject univ = (JSONObject) parser.parse(jsonString);

        String thirdParse = null;

        JSONObject imageob = (JSONObject) univ.get("image");
        thirdParse = imageob.get("data").toString();

        String[] l = thirdParse.replace("[", "").replace("]", "").split(",");
        int[] intarr = new int[l.length];
        String[] binarr = new String[l.length];
        byte[] decodedString = new byte[l.length];

        for (int i = 0; i < l.length; i++) {
            intarr[i] = Integer.parseInt(l[i]);
            binarr[i] = Integer.toBinaryString(intarr[i]);
            //decodedString[i] = (byte) (Integer.parseInt(l[i], 16) & 0xFF);
        }

        for (int i = 0; i < l.length; i++) {
            decodedString[i] = (byte) (Integer.parseInt(binarr[i], 2) & 0xFF);
        }

       BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;

        bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        Matrix rotateMatrix = new Matrix();
        rotateMatrix.postRotate(90); //-360~360

        nbmp = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(), bmp.getHeight(), rotateMatrix, false);

    }

}

