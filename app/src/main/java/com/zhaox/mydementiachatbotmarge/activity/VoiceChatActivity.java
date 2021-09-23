package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.zhaox.mydementiachatbotmarge.R;
import com.zhaox.mydementiachatbotmarge.helper.SessionManager;
import com.zhaox.mydementiachatbotmarge.utils.VoiceUtil;
import com.zhaox.mydementiachatbotmarge.widgets.TitleBarView;

import static android.content.ContentValues.TAG;

public class VoiceChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Toast mToast;
    private TextView userText, chattyText;
    private VoiceUtil voiceUtil;
    private TitleBarView titleBarView;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vioce_chat);
        voiceUtil = new VoiceUtil(this);

        //隐藏标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        userText = (TextView) findViewById(R.id.tv_user);
        chattyText = (TextView) findViewById(R.id.tv_chatty);

        mToast = Toast.makeText(VoiceChatActivity.this, "", Toast.LENGTH_SHORT);

        // session manager
        session = new SessionManager(this);

        // check user is already logged in
        if (!session.isLoggedIn()) {
            Intent i = new Intent(VoiceChatActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        findViewById(R.id.start_speak).setOnClickListener(VoiceChatActivity.this);
        titleBarView = findViewById(R.id.title);
        titleBarView.setOnViewClick(new TitleBarView.onViewClick() {
            @Override
            public void leftClick() {
            }
            @Override
            public void rightClick() {
                Intent i = new Intent(VoiceChatActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 点击按钮开始语音听写
            case R.id.start_speak:
                showTip("请讲话");
                voiceUtil.voice2string(new VoiceUtil.OnUserListener() {
                    @Override
                    public void result(String result) {
                        if (null == result) {
                            voiceUtil.string2voice("不好意思，我没有听清你在讲什么，可以在请你讲一遍吗");
                            return;
                        }
                        Log.d(TAG, "result:-------------"  + result + "---------------result");
                        userText.setText(result);
                    }
                });

        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}