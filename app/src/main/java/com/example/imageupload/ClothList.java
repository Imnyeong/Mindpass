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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

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
import java.util.Arrays;
import java.util.Random;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import android.view.View.OnClickListener;

import static java.util.Objects.isNull;

public class ClothList extends AppCompatActivity implements View.OnClickListener {

    public String id, arrayString, big;
    public Integer i, j, z, page, pg, tmppg, intid;
    private ListView m_oListView = null;
    Button see,delete;
    ArrayList<ItemData> oData = new ArrayList<>();
    Long[] idarr = new Long[100];
    public Long sendid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_list);
        Intent intent = getIntent();

        id = intent.getExtras().getString("sendid");
        big = intent.getExtras().getString("big");

        page = 1;
        List lst = new List();
        lst.execute();

        see = (Button)findViewById(R.id.btnSee);
        delete = (Button)findViewById(R.id.btnDelete);

    }

        @Override
        public void onClick(View v) {
            int nViewTag = Integer.parseInt((String)v.getTag());
            String strViewName = "";
            View oParentView = (View) v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
            String position = (String) oParentView.getTag();
            switch (nViewTag)
            {
                case 1: // 버튼

        /*AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);*/

                    intid = Integer.parseInt(position);
                    sendid = idarr[intid];

                    Intent intent = new Intent(this, ClothSee.class);
                    intent.putExtra("id", id);
                    intent.putExtra("cloth_id", sendid.toString());/*송신*/
                    Toast.makeText(getApplicationContext(), "잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    break;
                case 2: // 타이틀
                    //TextView oTextTitle = (TextView) oParentView.findViewById(R.id.textCloth);

        /*AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog);*/

                    intid = Integer.parseInt(position);
                    sendid = idarr[intid];

                    Delete dlt = new Delete();
                    dlt.execute();

        /*String strMsg = "선택한 아이템의 position 은 " + position + " 입니다.\nTitle 텍스트 :" + oTextTitle.getText();
        oDialog.setMessage(strMsg)
                .setPositiveButton("확인", null)
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();*/
                    break;
            }


    }

    public class List extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/cloth_list");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(id).append("&");
                buffer.append("page").append("=").append(page).append("&");
                buffer.append("cloth_big").append("=").append(big);
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
                String lrslt;
                lrslt = buff.toString().trim();
                //Log.e("Test", lrslt);
                Log.e("RECV DATA", data);
                pars(lrslt);
                parsr(arrayString);

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
            m_oListView = (ListView) findViewById(R.id.listView);
            ListAdapter oAdapter = new ListAdapter(oData);
            m_oListView.setAdapter(oAdapter);
        }

        public void pars(String jsonString) throws ParseException, JSONException {

            JSONParser parser = new JSONParser();

            JSONObject univ = (JSONObject) parser.parse(jsonString);
            JSONArray arr = (org.json.simple.JSONArray) univ.get("result");

            j = arr.size();
            String[] sendarr = new String[j];
            tmppg = ((Long) univ.get("total")).intValue();
            pg = (tmppg / 10) + 1;

            for (i = 0; i < j; i++) {

                JSONObject tmp = (JSONObject) arr.get(i);
                sendarr[i] = tmp.toString();

                arrayString = Arrays.toString(sendarr);
            }
            Log.e("arrayString", arrayString);
        }

        public void parsr(String jsonString) throws ParseException, JSONException {

            JSONParser parser = new JSONParser();

            if (jsonString == null) {

            } else {
                JSONArray arr = (JSONArray) parser.parse(jsonString);
                Log.e("arr", arr.toString());
                oData.clear();
                idarr = new Long[tmppg];
                for (z = 0; z < tmppg; ++z) {
                    ItemData oItem = new ItemData();
                    JSONObject tmp = (JSONObject) arr.get(z);
                    oItem.strCloth = (String) tmp.get("cloth");
                    oItem.strColor = (String) tmp.get("color");
                    idarr[z] = (Long) tmp.get("id");
                    oItem.donClickListener = ClothList.this;
                    oItem.sonClickListener = ClothList.this;
                    oData.add(oItem);
                }
            }
        }
    }

    public class Delete extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/cloth_delete");
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
                String lrslt;
                lrslt = buff.toString().trim();
                //Log.e("Test", lrslt);
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
            Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
