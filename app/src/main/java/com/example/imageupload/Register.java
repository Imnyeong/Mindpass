package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;

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

public class Register extends AppCompatActivity {

    EditText ed_id, ed_nic, ed_name, ed_tel, ed_pw, ed_pw2;
    String sid, snic, sname, stel, spw,spw2;
    public String rslt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed_id = (EditText) findViewById(R.id.id);
        ed_nic = (EditText) findViewById(R.id.nic);
        ed_name = (EditText) findViewById(R.id.name);
        ed_tel = (EditText) findViewById(R.id.tel);
        ed_pw = (EditText) findViewById(R.id.pw);
        ed_pw2 = (EditText) findViewById(R.id.pw2);
    }

    public void regist(View view) {
        sid = ed_id.getText().toString();
        snic = ed_nic.getText().toString();
        sname= ed_name.getText().toString();
        stel= ed_tel.getText().toString();
        spw = ed_pw.getText().toString();
        spw2 = ed_pw2.getText().toString();
        Regist rg = new Regist();
        rg.execute();
    }

    public void golo(View view) {
        this.finish();
    }

    public class Regist extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {

            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/register");
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
                buffer.append("nic").append("=").append(snic).append("&");
                buffer.append("name").append("=").append(sname).append("&");
                buffer.append("tel").append("=").append(stel).append("&");
                buffer.append("pwCheck").append("=").append(spw2);
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
        if (rslt.contains("410")) {
            Toast.makeText(getApplicationContext(), "빈 칸이 있습니다", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("411")) {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인하세요", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("500")) {
            Toast.makeText(getApplicationContext(), "데이터베이스 오류", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("501")) {
            Toast.makeText(getApplicationContext(), "잘못된 ID입니다.", Toast.LENGTH_LONG).show();
        } else if (rslt.contains("210")) {
            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,Login.class);
            reset();
            startActivity(intent);
        }
    }
    public void reset(){
        ed_id.setText("");
        ed_pw.setText("");
        ed_nic.setText("");
        ed_name.setText("");
        ed_tel.setText("");
        ed_pw2.setText("");
    }

}
