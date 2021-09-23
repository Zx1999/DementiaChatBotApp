package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zhaox.mydementiachatbotmarge.R;
import com.zhaox.mydementiachatbotmarge.helper.Functions;
import com.zhaox.mydementiachatbotmarge.helper.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zhaox.mydementiachatbotmarge.helper.Functions.JSON;
import static com.zhaox.mydementiachatbotmarge.helper.Functions.REGISTER_URL;


public class RegisterActivity extends AppCompatActivity {

    private MaterialButton btnRegister, btnLinkToLogin;
    private TextInputLayout inputUsername, inputPassword, inputCode;
    private SessionManager session;

    // Shared Preferences
    SharedPreferences pref;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputUsername = findViewById(R.id.edit_phone);
        inputPassword = findViewById(R.id.edit_password);
        inputCode = findViewById(R.id.edit_verification_code);
        btnLinkToLogin = findViewById(R.id.button_login);
        btnRegister = findViewById(R.id.button_register);

        session = new SessionManager(getApplicationContext());

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        // Register
        btnRegister.setOnClickListener(view -> {
            // Hide Keyboard
            Functions.hideSoftKeyboard(RegisterActivity.this);

            String code = Objects.requireNonNull(inputCode.getEditText()).getText().toString().trim();
            String username = Objects.requireNonNull(inputUsername.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();

            // Check for empty data in the form
            if (!code.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                if(Functions.isValidCode(code)) {
                    registerUser(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), "验证码输入不正确!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "请输入用户名和密码!", Toast.LENGTH_LONG).show();
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(view -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void registerUser(final String username, final String password) {
        showDialog();
        OkHttpClient client = new OkHttpClient();
        String json = "{" + "\"username\":" + "\"" + username + "\"" + "\"password\":" + "\"" + password + "\"" + "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        // 异步回调
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        Toast.makeText(getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res.equals("0")) {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "该手机号已被注册", Toast.LENGTH_SHORT).show();
                        } else {
                            hideDialog();
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                            pref = getSharedPreferences("UsernameAndPassword", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.apply();
                            session.setLogin(true);
                            //注册成功跳转至引导页
                            Intent i = new Intent(RegisterActivity.this, GuiderActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    }
                });
            }
        });
    }

    private void showDialog() {
        Functions.showProgressDialog(RegisterActivity.this, "正在注册 ...");
    }

    private void hideDialog() {
        Functions.hideProgressDialog(RegisterActivity.this);
    }
}
