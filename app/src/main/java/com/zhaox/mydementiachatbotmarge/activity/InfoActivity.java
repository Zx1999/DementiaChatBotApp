package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.zhaox.mydementiachatbotmarge.R;

public class InfoActivity extends AppCompatActivity {

    private MaterialButton btnSubmit;
    private Boolean submitted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        btnSubmit = findViewById(R.id.button_submit);
        submitted = false;

        init();
    }

    private void init() {
        btnSubmit.setOnClickListener(view -> {
            submitted = true;
            Bundle b = new Bundle();
            b.putBoolean("submitted", submitted);
            Intent i = new Intent(InfoActivity.this, GuiderActivity.class);
            Log.w("Info----submitted", String.valueOf(submitted));
            i.putExtras(b);
            startActivity(i);
        });
    }
}
