package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class EtcChoice extends AppCompatActivity {
        ArrayAdapter<CharSequence> adspinbig, adspincloth, adspincolor; //어댑터를 선언했습니다. adspint1(서울,인천..) adspin2(강남구,강서구..)
        public String big = "";
        public String cloth = "";
        public String color = "";
        public String id, rslt;

        //검색시 선택된 매세지를 띄우기 위한 선언하였습니다. 그냥 선언안하고 인자로 넘기셔도 됩니다.
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_etc_choice);
            final Spinner spinbig = (Spinner) findViewById(R.id.spinbig);
            final Spinner spincolor = (Spinner) findViewById(R.id.spincolor);
            final Spinner spincloth = (Spinner) findViewById(R.id.spincloth);
            Button sndbtn = (Button) findViewById(R.id.etcsend);

            Intent intent = getIntent();
            id = intent.getExtras().getString("sendid");

            adspincolor =ArrayAdapter.createFromResource(this,R.array.spinner_color, R.layout.spinner_item);
            adspincolor.setDropDownViewResource(R.layout.spinner_dropdown);
            spincolor.setAdapter(adspincolor);
            spincolor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //첫번째 spinner 클릭시 이벤트 발생입니다. setO 정도까지 치시면 자동완성됩니다. 뒤에도 마찬가지입니다.
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                color = adspincolor.getItem(i).toString();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });

//xml과 class에 변수들을 연결해줍니다. final를 사용한 이유는 spin2가 함수안에서 사용되기 때문에 코딩전체로 선언한 것입니다.
            adspinbig = ArrayAdapter.createFromResource(this, R.array.spinner_big,  R.layout.spinner_item);
//처번째 어댑터에 값을 넣습니다. this=는 현재class를 의미합니다. R.array.spinner_do는 이곳에 도시를 다 쓸 경우 코딩이 길어지기 때문에 value->string.xml에 따로 String값들을 선언해두었습니다.
//R.layout.simple_~~~는 안드로이드에서 기본제공하는 spinner 모양입니다. 다른것도 있는데 비슷합니다.
            adspinbig.setDropDownViewResource(R.layout.spinner_dropdown);
//이부분이 정확히 말로 설명을 못하겠습니다. 아무튼 필요합니다. 헤헤 고수분들 도와주세요.
            spinbig.setAdapter(adspinbig);
//어댑터에 값들을 spinner에 넣습니다. 여기까지 하시면 첫번째 spinner에 값들이 들어갈 것입니다.
            spinbig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //첫번째 spinner 클릭시 이벤트 발생입니다. setO 정도까지 치시면 자동완성됩니다. 뒤에도 마찬가지입니다.
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//제대로 자동완성하셨다면 이부분이 자동으로 만들어 질 것입니다. int i는 포지션이라 하여 제가 spinner에 몇번째걸 선택했는지 값이 들어갑니다. 필요하겠죠? ㅎㅎ
                    if (adspinbig.getItem(i).equals("상의")) {
//spinner에 값을 가져와서 i 보이시나요 제가 클릭 한것이 서울인지 확인합니다.
                        big = "상의";//버튼 클릭시 출력을 위해 값을 넣었습니다.
                        adspincloth = ArrayAdapter.createFromResource(EtcChoice.this, R.array.spinner_top,  R.layout.spinner_item);
//서울일 경우에 두번째 spinner에 값을 넣습니다.
//그냥 this가 아닌 Main~~~인 이유는 그냥 this는 메인엑티비티인 경우만 가능합니다.
//지금과 같이 다른 함수안이나 다른 클래스에서는 꼭 자신의 것을 넣어주어야 합니다.
//혹시나 다른 class -> Public View밑에서 작업하시는 분은 view명.getContext()로 해주셔야 합니다.
//예로 View rootView =~~ 선언하신 경우에는 rootView.getContext()써주셔야합니다. this가 아니라요.
                        adspincloth.setDropDownViewResource(R.layout.spinner_dropdown);
                        spincloth.setAdapter(adspincloth);
//두번째 어댑터값을 두번째 spinner에 넣었습니다.
                        spincloth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            //저희는 두번째 선택된 값도 필요하니 이안에 두번째 spinner 선택 이벤트를 정의합니다.
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                cloth = adspincloth.getItem(i).toString();
//두번째 선택된 값을 choice_se에 넣습니다.
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
//아무것도 선택안될시 부분입니다. 자동완성됩니다.
                            }
                        });
                    } else if (adspinbig.getItem(i).equals("하의")) {
//똑같은 소스에 반복입니다. 인천부분입니다.
                        big = "하의";
                        adspincloth = ArrayAdapter.createFromResource(EtcChoice.this, R.array.spinner_pants,  R.layout.spinner_item);
                        adspincloth.setDropDownViewResource(R.layout.spinner_dropdown);
                        spincloth.setAdapter(adspincloth);
                        spincloth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                cloth = adspincloth.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } else if (adspinbig.getItem(i).equals("신발")) {
//똑같은 소스에 반복입니다. 인천부분입니다.
                        big = "신발";
                        adspincloth = ArrayAdapter.createFromResource(EtcChoice.this, R.array.spinner_shoes,  R.layout.spinner_item);
                        adspincloth.setDropDownViewResource(R.layout.spinner_dropdown);
                        spincloth.setAdapter(adspincloth);
                        spincloth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                cloth = adspincloth.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } else if (adspinbig.getItem(i).equals("가방")) {
//똑같은 소스에 반복입니다. 인천부분입니다.
                        big = "가방";
                        adspincloth = ArrayAdapter.createFromResource(EtcChoice.this, R.array.spinner_bag,  R.layout.spinner_item);
                        adspincloth.setDropDownViewResource(R.layout.spinner_dropdown);
                        spincloth.setAdapter(adspincloth);
                        spincloth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                cloth = adspincloth.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } else if (adspinbig.getItem(i).equals("외투")) {
//똑같은 소스에 반복입니다. 인천부분입니다.
                        big = "외투";
                        adspincloth = ArrayAdapter.createFromResource(EtcChoice.this, R.array.spinner_coat,  R.layout.spinner_item);
                        adspincloth.setDropDownViewResource(R.layout.spinner_dropdown);
                        spincloth.setAdapter(adspincloth);
                        spincloth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                cloth = adspincloth.getItem(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            sndbtn.setOnClickListener(new View.OnClickListener() {
                //버튼 클릭시 이벤트입니다.
                @Override
                public void onClick(View view) {
                    Sendetc etc = new Sendetc();
                    etc.execute();
                    Log.e("Test", id + "" + cloth + "" + color );
                }
            });
        }
    public class Sendetc extends AsyncTask<Void, Integer, Void> {

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
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("sendid", id);/*송신*/
        startActivity(intent);
        /*Intent intent = new Intent(this,Home.class);
        intent.putExtra("id", id);
        startActivity(intent);*/
    }
}

