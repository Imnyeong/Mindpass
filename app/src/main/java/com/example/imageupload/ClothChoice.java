package com.example.imageupload;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClothChoice extends AppCompatActivity {

    Button top, pants, shoes, bag, coat;
    String id, nick;
    TextView nic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_choice);

        Intent intent = getIntent();
        id = intent.getExtras().getString("sendid");
        nick = intent.getExtras().getString("nick");

        nic = (TextView)findViewById(R.id.nickView);

        nic.setText(nick+"님의 옷장입니다.");

        top = (Button)findViewById(R.id.btnTop);
        //이미지를 띄울 위젯
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClothChoice.this, ClothList.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("big", "top");
                startActivity(intent);
            }
        });

        pants = (Button)findViewById(R.id.btnPants);
        //이미지를 띄울 위젯
        pants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClothChoice.this, ClothList.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("big", "pants");
                startActivity(intent);
            }
        });

        shoes = (Button)findViewById(R.id.btnShoes);
        //이미지를 띄울 위젯
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClothChoice.this, ClothList.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("big", "shoes");
                startActivity(intent);
            }
        });

        bag = (Button)findViewById(R.id.btnBag);
        //이미지를 띄울 위젯
        bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClothChoice.this, ClothList.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("big", "bag");
                startActivity(intent);
            }
        });

       coat = (Button)findViewById(R.id.btnCoat);
        //이미지를 띄울 위젯
        coat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClothChoice.this, ClothList.class);
                intent.putExtra("sendid", id);/*송신*/
                intent.putExtra("big", "coat");
                startActivity(intent);
            }
        });
    }
}
