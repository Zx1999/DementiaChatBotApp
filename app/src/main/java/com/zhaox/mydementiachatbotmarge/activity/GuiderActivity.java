package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.zhaox.mydementiachatbotmarge.R;
import com.zhaox.mydementiachatbotmarge.widgets.TitleBarView;


public class GuiderActivity extends AppCompatActivity {

    private MaterialButton btnInfoDementia, btnConfirm;
    private RelativeLayout relativeLayoutSend1, relativeLayoutSend2, relativeLayoutReceive2, relativeLayoutReceive3;
    private Bundle bundle;
    private TitleBarView titleBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guider);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        btnInfoDementia = findViewById(R.id.info_dementia);
        btnConfirm = findViewById(R.id.confirm);
        relativeLayoutSend1 = findViewById(R.id.send_chat_content_1);
        relativeLayoutSend2 = findViewById(R.id.send_chat_content_2);
        relativeLayoutReceive2 = findViewById(R.id.receive_chat_content_2);
        relativeLayoutReceive3 = findViewById(R.id.receive_chat_content_3);
        titleBarView = findViewById(R.id.title);

        bundle = getIntent().getExtras();

        init();
    }



    private void init() {
        btnInfoDementia.setOnClickListener(view -> {
            Intent intent = new Intent(GuiderActivity.this, InfoActivity.class);
            startActivity(intent);
        });
        btnConfirm.setOnClickListener(view -> {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    relativeLayoutSend2.setVisibility(View.VISIBLE);
                }
            }, 500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    relativeLayoutReceive3.setVisibility(View.VISIBLE);
                }
            }, 1500);
        });

        titleBarView.setOnViewClick(new TitleBarView.onViewClick() {
            @Override
            public void leftClick() {
            }
            @Override
            public void rightClick() {
                Intent i = new Intent(GuiderActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.w("onResume-------------", "onResume");
        super.onResume();
        bundle = getIntent().getExtras();
        if(bundle != null) {
            Boolean submitted = bundle.getBoolean("submitted");
            Log.w("Guider----submitted", String.valueOf(submitted));
            if(submitted) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayoutSend1.setVisibility(View.VISIBLE);
                    }
                }, 500);//1秒后发送
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayoutReceive2.setVisibility(View.VISIBLE);
                    }
                }, 1500);//1秒后发送
            }
        }

    }



}