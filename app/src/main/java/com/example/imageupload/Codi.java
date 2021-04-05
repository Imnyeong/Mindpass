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
public class Codi extends AppCompatActivity {

    public String id, nick, rslt,bag,top,bottom,shoes,coat;
    public String cbag,ctop,cbottom,cshoes,ccoat;
    TextView bagv,topv,bottomv,shoesv,coatv;
    TextView bagc,topc,bottomc,shoesc,coatc;
    ImageView bagi,topi,bottomi,shoesi,coati;
    Button Another;
    Button Like;
    Bitmap tbmp, pbmp, sbmp, cbmp, bbmp;
    public String senddata;
    public Integer i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codi);

        Intent intent = getIntent();
        id = intent.getExtras().getString("sendid");
        nick = intent.getExtras().getString("nickname");

        /*bag = intent.getExtras().getString("bag");
        top = intent.getExtras().getString("top");
        bottom = intent.getExtras().getString("pants");
        shoes = intent.getExtras().getString("shoes");
        coat = intent.getExtras().getString("coat");

        cbag = intent.getExtras().getString("cbag");
        ctop = intent.getExtras().getString("ctop");
        cbottom = intent.getExtras().getString("cpants");
        cshoes = intent.getExtras().getString("cshoes");
        ccoat = intent.getExtras().getString("ccoat");*/

        Another = (Button)findViewById(R.id.Another);

        bagv = (TextView)findViewById(R.id.bagView);
        topv = (TextView)findViewById(R.id.clothtopView);
        bottomv = (TextView)findViewById(R.id.clothbottomView);
        shoesv = (TextView)findViewById(R.id.shoesView);
        coatv = (TextView)findViewById(R.id.coatView);

        bagc = (TextView)findViewById(R.id.bagColor);
        topc = (TextView)findViewById(R.id.topColor);
        bottomc = (TextView)findViewById(R.id.bottomColor);
        shoesc = (TextView)findViewById(R.id.shoesColor);
        coatc = (TextView)findViewById(R.id.coatColor);

        bagi = (ImageView) findViewById(R.id.bagimg);
        topi = (ImageView)findViewById(R.id.topimg);
        bottomi = (ImageView)findViewById(R.id.pantsimg);
        shoesi = (ImageView)findViewById(R.id.shoesimg);
        coati = (ImageView)findViewById(R.id.coatimg);

        NewCody ncd = new NewCody();
        ncd.execute();

        /*bagv.setText(bag);
        topv.setText(top);
        bottomv.setText(bottom);
        shoesv.setText(shoes);
        coatv.setText(coat);

        bagc.setText(cbag);
        topc.setText(ctop);
        bottomc.setText(cbottom);
        shoesc.setText(cshoes);
        coatc.setText(ccoat);*/

        //이미지를 띄울 위젯
        Another.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCody ncd = new NewCody();
                ncd.execute();
            }
        });

        Like = (Button)findViewById(R.id.Like);
        Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Like lk = new Like();
                lk.execute();
            }
        });
    }
    public class NewCody extends AsyncTask<Void, Integer, Void> {

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
                rslt = buff.toString().trim();
                Log.e("testda", "rslt = " + rslt);
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
            newchange();
        }

    }
    public void newchange() {
        bagv.setText(bag);
        topv.setText(top);
        bottomv.setText(bottom);
        shoesv.setText(shoes);
        coatv.setText(coat);

        bagc.setText(cbag);
        topc.setText(ctop);
        bottomc.setText(cbottom);
        shoesc.setText(cshoes);
        coatc.setText(ccoat);

        topi.setImageBitmap(tbmp);
        bottomi.setImageBitmap(pbmp);
        shoesi.setImageBitmap(sbmp);
        coati.setImageBitmap(cbmp);
        bagi.setImageBitmap(bbmp);

    }

    public void pars(String jsonString) throws ParseException, JSONException {
        JSONParser parser = new JSONParser();

        JSONObject univ = (JSONObject) parser.parse(jsonString);
        JSONArray arr = (org.json.simple.JSONArray) univ.get("result");

        i = arr.size();
        Random rand = new Random();
        Integer randomNum = rand.nextInt(i);

        JSONObject tmp = (JSONObject)arr.get(randomNum);

        senddata = tmp.toString();
        Log.e("testda", "senddata = " + senddata);

        JSONArray toparr = (org.json.simple.JSONArray) tmp.get("top");
        JSONArray pantsarr = (org.json.simple.JSONArray) tmp.get("pants");
        JSONArray shoesarr = (org.json.simple.JSONArray) tmp.get("shoes");
        JSONArray bagarr = (org.json.simple.JSONArray) tmp.get("bag");
        JSONArray coatarr = (org.json.simple.JSONArray) tmp.get("coat");

        JSONArray toptmp = (org.json.simple.JSONArray) toparr.get(0);
        JSONArray pantstmp = (org.json.simple.JSONArray) pantsarr.get(0);
        JSONArray shoestmp = (org.json.simple.JSONArray) shoesarr.get(0);
        JSONArray bagtmp = (org.json.simple.JSONArray) bagarr.get(0);
        JSONArray coattmp = (org.json.simple.JSONArray) coatarr.get(0);


        top = (String)toptmp.get(0);
        bottom = (String)pantstmp.get(0);
        shoes = (String)shoestmp.get(0);
        bag = (String)bagtmp.get(0);
        coat = (String)coattmp.get(0);

        ctop = (String)toptmp.get(1);
        cbottom = (String)pantstmp.get(1);
        cshoes = (String)shoestmp.get(1);
        cbag = (String)bagtmp.get(1);
        ccoat = (String)coattmp.get(1);

        Log.e("testda", "tmp = " + tmp.toString());
        Log.e("testda", "top = " + top + "topcolor" + ctop);
        Log.e("testda", "toparr = " + toparr.toString());

        String topParse = null;
        topParse = toptmp.get(2).toString();
        Log.e("testda", "thirdParse = " + topParse);

        String[] topl = topParse.replace("[", "").replace("]", "").split(",");
        int[] topintarr = new int[topl.length];
        String[] topbinarr = new String[topl.length];
        byte[] topdecodedString = new byte[topl.length];

        for (int i = 0; i < topl.length; i++) {
            topintarr[i] = Integer.parseInt(topl[i]);
            topbinarr[i] = Integer.toBinaryString(topintarr[i]);
            //decodedString[i] = (byte) (Integer.parseInt(l[i], 16) & 0xFF);
        }

        for (int i = 0; i < topl.length; i++) {
            topdecodedString[i] = (byte) (Integer.parseInt(topbinarr[i], 2) & 0xFF);
        }
        tbmp = BitmapFactory.decodeByteArray(topdecodedString, 0, topdecodedString.length);

        String pantsParse = null;
        pantsParse = pantstmp.get(2).toString();

        String[] pantsl = pantsParse.replace("[", "").replace("]", "").split(",");
        int[] pantsintarr = new int[pantsl.length];
        String[] pantsbinarr = new String[pantsl.length];
        byte[] pantsdecodedString = new byte[pantsl.length];

        for (int i = 0; i < pantsl.length; i++) {
            pantsintarr[i] = Integer.parseInt(pantsl[i]);
            pantsbinarr[i] = Integer.toBinaryString(pantsintarr[i]);
            //decodedString[i] = (byte) (Integer.parseInt(l[i], 16) & 0xFF);
        }

        for (int i = 0; i < pantsl.length; i++) {
            pantsdecodedString[i] = (byte) (Integer.parseInt(pantsbinarr[i], 2) & 0xFF);
        }
        pbmp = BitmapFactory.decodeByteArray(pantsdecodedString, 0, pantsdecodedString.length);

        String shoesParse = null;
        shoesParse = shoestmp.get(2).toString();

        String[] shoesl = shoesParse.replace("[", "").replace("]", "").split(",");
        int[] shoesintarr = new int[shoesl.length];
        String[] shoesbinarr = new String[shoesl.length];
        byte[] shoesdecodedString = new byte[shoesl.length];

        for (int i = 0; i < shoesl.length; i++) {
            shoesintarr[i] = Integer.parseInt(shoesl[i]);
            shoesbinarr[i] = Integer.toBinaryString(shoesintarr[i]);
            //decodedString[i] = (byte) (Integer.parseInt(l[i], 16) & 0xFF);
        }

        for (int i = 0; i < shoesl.length; i++) {
            shoesdecodedString[i] = (byte) (Integer.parseInt(shoesbinarr[i], 2) & 0xFF);
        }
        sbmp = BitmapFactory.decodeByteArray(shoesdecodedString, 0, shoesdecodedString.length);

        String coatParse = null;
        coatParse = coattmp.get(2).toString();

        String[] coatl = coatParse.replace("[", "").replace("]", "").split(",");
        int[] coatintarr = new int[coatl.length];
        String[] coatbinarr = new String[coatl.length];
        byte[] coatdecodedString = new byte[coatl.length];

        for (int i = 0; i < coatl.length; i++) {
            coatintarr[i] = Integer.parseInt(coatl[i]);
            coatbinarr[i] = Integer.toBinaryString(coatintarr[i]);
            //decodedString[i] = (byte) (Integer.parseInt(l[i], 16) & 0xFF);
        }

        for (int i = 0; i < coatl.length; i++) {
            coatdecodedString[i] = (byte) (Integer.parseInt(coatbinarr[i], 2) & 0xFF);
        }
        cbmp = BitmapFactory.decodeByteArray(coatdecodedString, 0, coatdecodedString.length);

        String bagParse = null;
        bagParse = bagtmp.get(2).toString();

        String[] bagl = bagParse.replace("[", "").replace("]", "").split(",");
        int[] bagintarr = new int[bagl.length];
        String[] bagbinarr = new String[bagl.length];
        byte[] bagdecodedString = new byte[bagl.length];

        for (int i = 0; i < bagl.length; i++) {
            bagintarr[i] = Integer.parseInt(bagl[i]);
            bagbinarr[i] = Integer.toBinaryString(bagintarr[i]);
            //decodedString[i] = (byte) (Integer.parseInt(l[i], 16) & 0xFF);
        }

        for (int i = 0; i < bagl.length; i++) {
            bagdecodedString[i] = (byte) (Integer.parseInt(bagbinarr[i], 2) & 0xFF);
        }
        bbmp = BitmapFactory.decodeByteArray(bagdecodedString, 0, bagdecodedString.length);
    }

    public class Like extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... unused) {
            StringBuffer buffer = new StringBuffer();

            try {
                /* 서버연결 */
                URL url = new URL(
                        "https://mp-domain-test.kro.kr:7777/like_insert");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                buffer.append("id").append("=").append(id).append("&");
                buffer.append("data").append("=").append(senddata);
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
            Toast.makeText(getApplicationContext(), nick + "님이 이 코디를 좋아합니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
