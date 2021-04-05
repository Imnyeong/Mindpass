package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Result extends AppCompatActivity {

    public Button button1, button2, button3, button4, button5;
    public String  id, rslt, num1, num2, num3, num4, num5, cloth, color;
    TextView colorrslt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        /*SpannableStringBuilder ssb = new SpannableStringBuilder(result);
        ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, color.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Style
        ssb.setSpan(new ForegroundColorSpan(Color.BLACK), 0, color.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Color
        ssb.setSpan(new AbsoluteSizeSpan(100), 0, color.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Size*/


        Intent intent = getIntent();
        num1 = intent.getExtras().getString("num1");
        num2 = intent.getExtras().getString("num2");
        num3 = intent.getExtras().getString("num3");
        num4 = intent.getExtras().getString("num4");
        num5 = intent.getExtras().getString("num5");
        id = intent.getExtras().getString("id");
        color = intent.getExtras().getString("color");

        colorrslt=(TextView)findViewById(R.id.textView);

        button1 = (Button)findViewById(R.id.btnRank1);
        button2 = (Button)findViewById(R.id.btnRank2);
        button3 = (Button)findViewById(R.id.btnRank3);
        button4 = (Button)findViewById(R.id.btnRank4);
        button5 = (Button)findViewById(R.id.btnRank5);

        colorrslt.setText("전송된 옷의 색상은 " + "\"" +color + "\"" + " 입니다." + "\n" + "어떤 옷에 가까운지 선택해주세요.");
        button1.setText(String.valueOf(num1));
        button2.setText(String.valueOf(num2));
        button3.setText(String.valueOf(num3));
        button4.setText(String.valueOf(num4));
        button5.setText(String.valueOf(num5));

    }//end of onCreate()

    public void button1(View view) {
        cloth = num1;
        Sendchoice chce = new Sendchoice();
        chce.execute();
    }
    public void button2(View view) {
        cloth = num2;
        Sendchoice chce = new Sendchoice();
        chce.execute();
    }
    public void button3(View view) {
        cloth = num3;
        Sendchoice chce = new Sendchoice();
        chce.execute();
    }
    public void button4(View view) {
        cloth = num4;
        Sendchoice chce = new Sendchoice();
        chce.execute();
    }
    public void button5(View view) {
        cloth = num5;
        Sendchoice chce = new Sendchoice();
        chce.execute();
    }
    public class Sendchoice extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {

            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/cloth_insert");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(id).append("&");
                buffer.append("cloth").append("=").append(cloth).append("&");
                buffer.append("color").append("=").append(color);
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
                Log.e("RECV DATA", data);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ioregsetting();
        }

    }
    public void ioregsetting() {

        Toast.makeText(getApplicationContext(), "정보 전송 성공했습니다.", Toast.LENGTH_LONG).show();
        this.finish();
        /*Intent intent = new Intent(this,Home.class);
        intent.putExtra("id", id);
        startActivity(intent);*/
    }
    public void goetc(View view) {
        Intent intent = new Intent(this, EtcChoice.class);
        intent.putExtra("sendid", id);/*송신*/

        startActivity(intent);
    }
}

