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
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//
public class Main extends AppCompatActivity {

    public String id, nick;
    TextView nic;
    Button image;
    Button codi;
    Button logout;
    Button list;
    public Integer i;
    public String crslt, bag,top,bottom,shoes,coat;
    public String cbag,ctop,cbottom,cshoes,ccoat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        id = intent.getExtras().getString("sendid");
        nick = intent.getExtras().getString("nickname");

        nic = (TextView)findViewById(R.id.nicView);

        nic.setText(nick+"님 환영합니다!");

        image = (Button)findViewById(R.id.btnImage);
        //이미지를 띄울 위젯
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Home.class);
                intent.putExtra("sendid", id);/*송신*/
                startActivity(intent);
            }
        });
        codi = (Button)findViewById(R.id.btnCodi);
        //이미지를 띄울 위젯
        codi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Codi.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("nickname", nick);

                startActivity(intent);
                /*Cody cd = new Cody();
                cd.execute();*/
            }
        });

        list = (Button)findViewById(R.id.btnList);
        //이미지를 띄울 위젯
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, ClothChoice.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("nick", nick);
                startActivity(intent);
            }
        });
        logout = (Button)findViewById(R.id.btnlgout);
        //이미지를 띄울 위젯
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Login.class);
                startActivity(intent);
            }
        });
    }
    @Override public void onBackPressed() {
        //super.onBackPressed();
        }

    public class Cody extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/cloth_filter");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(id);
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
                crslt = buff.toString().trim();

                Log.e("test", "crslt는" + crslt);
                Log.e("RECV DATA", data);
                pars(crslt);

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
        Intent intent = new Intent(Main.this, Codi.class);
        intent.putExtra("sendid", id);/*송신*/
        intent.putExtra("nickname", nick);
        intent.putExtra("bag", bag);
        intent.putExtra("top", top);
        intent.putExtra("pants", bottom);
        intent.putExtra("shoes", shoes);
        intent.putExtra("coat", coat);
        intent.putExtra("cbag", cbag);
        intent.putExtra("ctop", ctop);
        intent.putExtra("cpants", cbottom);
        intent.putExtra("cshoes", cshoes);
        intent.putExtra("ccoat", ccoat);

        startActivity(intent);
    }

    public void pars(String jsonString) throws ParseException, JSONException {

        JSONParser parser = new JSONParser();

        JSONObject univ = (JSONObject) parser.parse(jsonString);
        JSONArray arr = (org.json.simple.JSONArray) univ.get("result");

        i = arr.size();
        Random rand = new Random();
        Integer randomNum = rand.nextInt(i);

        JSONObject tmp = (JSONObject)arr.get(randomNum);

        JSONArray toparr = (org.json.simple.JSONArray) tmp.get("top");
        JSONArray pantsarr = (org.json.simple.JSONArray) tmp.get("pants");
        JSONArray shoesarr = (org.json.simple.JSONArray) tmp.get("shoes");
        JSONArray bagarr = (org.json.simple.JSONArray) tmp.get("bag");
        JSONArray coatarr = (org.json.simple.JSONArray) tmp.get("coat");

        top = (String)toparr.get(0);
        bottom = (String)pantsarr.get(0);
        shoes = (String)shoesarr.get(0);
        bag = (String)bagarr.get(0);
        coat = (String)coatarr.get(0);

        ctop = (String)toparr.get(1);
        cbottom = (String)pantsarr.get(1);
        cshoes = (String)shoesarr.get(1);
        cbag = (String)bagarr.get(1);
        ccoat = (String)coatarr.get(1);
    }


}
