package com.zhaox.mydementiachatbotmarge.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.zhaox.mydementiachatbotmarge.R;
import com.zhaox.mydementiachatbotmarge.helper.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private MaterialButton btnChat, btnInfo, btnComm, btnQuit;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnChat = findViewById(R.id.voice_chat_btn);
        btnInfo = findViewById(R.id.info_dementia_btn);
        btnComm = findViewById(R.id.Recommended_topics_btn);
        btnQuit = findViewById(R.id.quit_btn);

        session = new SessionManager(this);

        init();
    }

    private void init() {
        btnChat.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, VoiceChatActivity.class);
            startActivity(i);
        });
        btnInfo.setOnClickListener(view -> {
            Intent i = new Intent(HomeActivity.this, InfoActivity.class);
            startActivity(i);
        });
        btnQuit.setOnClickListener(view -> {
            session.setLogin(false);
            Intent upanel = new Intent(HomeActivity.this, VoiceChatActivity.class);
            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);;
            startActivity(upanel); 
        });
    }
}