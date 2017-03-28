package com.hjchengfeng.reversi_20.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.hjchengfeng.reversi_20.R;

public class RuleActivity extends Activity {

    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        back =(Button)findViewById(R.id.rback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
