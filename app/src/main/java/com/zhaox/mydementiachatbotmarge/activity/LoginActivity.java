package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.zhaox.mydementiachatbotmarge.R;
import com.zhaox.mydementiachatbotmarge.helper.Functions;
import com.zhaox.mydementiachatbotmarge.helper.SessionManager;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zhaox.mydementiachatbotmarge.helper.Functions.JSON;
import static com.zhaox.mydementiachatbotmarge.helper.Functions.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton btnLogin, btnLinkToRegister, btnForgotPass;
    private TextInputLayout inputUsername, inputPassword;
    private MaterialCheckBox checkBoxRemember;

    private SessionManager session;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.edit_phone);
        inputPassword = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.button_login);
        btnLinkToRegister = findViewById(R.id.button_register);
        btnForgotPass = findViewById(R.id.button_reset);
        checkBoxRemember = findViewById(R.id.button_remember_password);

        // session manager
        session = new SessionManager(this);

        // check user is already logged in
        if (session.isLoggedIn()) {
            Intent i = new Intent(LoginActivity.this, GuiderActivity.class);
            startActivity(i);
            finish();
        }

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("UsernameAndPassword", MODE_PRIVATE);

        boolean choseRemember =sharedPreferences.getBoolean("checkBoxRemember", true);
        Objects.requireNonNull(inputUsername.getEditText()).
                setText(sharedPreferences.getString("username", ""));

        if(choseRemember){
            Objects.requireNonNull(inputPassword.getEditText()).
                    setText(sharedPreferences.getString("password", ""));
            checkBoxRemember.setChecked(true);
        } else {
            checkBoxRemember.setChecked(false);
        }
        // Login button Click Event
        btnLogin.setOnClickListener(view -> {
            // Hide Keyboard
            Functions.hideSoftKeyboard(LoginActivity.this);

            String username = Objects.requireNonNull(inputUsername.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();

            // Check for empty data in the form
            if (!username.isEmpty() && !password.isEmpty()) {
                if (Functions.isValidUsername(username)) {
                    // login user
                    loginProcess(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), "请输入正确手机号", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Prompt user to enter credentials
                Toast.makeText(getApplicationContext(), "请输入账号和密码!", Toast.LENGTH_LONG).show();
            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        // Forgot Password Dialog
        btnForgotPass.setOnClickListener(v -> forgotPasswordDialog());
    }

    private void loginProcess(final String username, final String password) {
        OkHttpClient client = new OkHttpClient();
        String json = "{" + "\"username\":" + "\"" + username + "\"" + "\"password\":" + "\"" + password + "\"" + "}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w("exception", e);
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
                            Toast.makeText(getApplicationContext(), "该手机号未注册", Toast.LENGTH_SHORT).show();
                        } else if(res.equals("1")){
                            Toast.makeText(getApplicationContext(), "密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                        } else {
                            sharedPreferences = getSharedPreferences("UsernameAndPassword", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("password", password);
                            if(checkBoxRemember.isChecked()){
                                editor.putBoolean("checkBoxRemember", true);
                            }else{
                                editor.putBoolean("checkBoxRemember", false);
                            }
                            editor.apply();
                            session.setLogin(true);
                            // 登录成功 跳转至语音聊天页
                            Intent upanel = new Intent(LoginActivity.this, VoiceChatActivity.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                        }
                    }
                });
            }
        });
    }

    private void forgotPasswordDialog() {
        Functions.hideSoftKeyboard(LoginActivity.this);
        Toast.makeText(getApplicationContext(), "请联系管理员", Toast.LENGTH_SHORT).show();
    }

}