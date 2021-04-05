package com.example.imageupload;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class Login extends AppCompatActivity {

    EditText ed_id, ed_pw;
    String sid, spw;
    public String rslt, nickname, sendid;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_id = (EditText) findViewById(R.id.id);
        ed_pw = (EditText) findViewById(R.id.pw);

    }

    public void login(View view) {
        sid = ed_id.getText().toString();
        spw = ed_pw.getText().toString();
        Lgin lg = new Lgin();
        lg.execute();
    }

    //두번 눌러서 종료
    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        else {

            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }

    public void gore(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public class Lgin extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(sid).append("&");
                buffer.append("pw").append("=").append(spw).append("&");
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

                Log.e("aaaA", sid + spw + rslt);
                Log.e("RECV DATA", data);
                pars(rslt);
            } catch (ParseException e1) {
                e1.printStackTrace();

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {

            iosetting();
        }

    }
    public void iosetting() {
        if (rslt.contains("401")) {
            Toast.makeText(getApplicationContext(), "ID 오류", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("411")) {
            Toast.makeText(getApplicationContext(), "비밀번호 오류", Toast.LENGTH_LONG).show();
            ed_pw.setText("");
        } else if (rslt.contains("500")) {
            Toast.makeText(getApplicationContext(), "데이터베이스 오류", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Main.class);
            intent.putExtra("nickname", nickname);
            intent.putExtra("sendid", sendid);/*송신*/
            reset();

            startActivity(intent);
        }
    }

    public void pars(String jsonString) throws ParseException, JSONException {
        JSONParser parser = new JSONParser();

        JSONObject univ = (JSONObject) parser.parse(jsonString);
        nickname = (String)univ.get("nic");
        sendid = (String)univ.get("id");
    }
    public void reset(){
        /*ed_id.setText("");*/
        ed_pw.setText("");
    }

}