package com.hjchengfeng.reversi_20.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hjchengfeng.reversi_20.R;

public class MainActivity extends Activity {

    private Button shuangren;
    private Button renji;
    private Button guize;
    private Button tuichu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shuangren =(Button)findViewById(R.id.shuangren);
        renji =(Button)findViewById(R.id.renji);
        guize =(Button)findViewById(R.id.guize);
        tuichu =(Button)findViewById(R.id.tuichu);

        //对四个按钮实现监听
        shuangren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(MainActivity.this,"双人游戏！",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(MainActivity.this,PvPActivity.class);
                startActivity(intent);
            }
        });
        renji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(MainActivity.this,"人机对战！",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(MainActivity.this,PvEActivity.class);
                startActivity(intent);
            }
        });
        guize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(MainActivity.this,"游戏规则！",Toast.LENGTH_SHORT).show();
                Intent intent =new Intent(MainActivity.this,RuleActivity.class);
                startActivity(intent);
            }
        });
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(MainActivity.this,"退出游戏！",Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
            }
        });
    }

}
