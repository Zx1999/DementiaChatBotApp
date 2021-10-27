package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static com.zhaox.mydementiachatbotmarge.helper.Functions.JSON;
import static com.zhaox.mydementiachatbotmarge.helper.Functions.LOGIN_URL;
import static com.zhaox.mydementiachatbotmarge.helper.Functions.USER_ANSWER_URL;
import static com.zhaox.mydementiachatbotmarge.utils.VoiceUtil.buffer;
import static com.zhaox.mydementiachatbotmarge.utils.VoiceUtil.mIatResults;

public class VoiceChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Toast mToast;
    private TextView userText, chattyText;
    private VoiceUtil voiceUtil;
    private TitleBarView titleBarView;
    private SessionManager session;
    private SharedPreferences sharedPreferences;
    private String currentSentence;
    private String user_answer_parameter;
    private String parameter;
    private String property;
    private int locNumber;
    // 记录用户是否是第一次登录
    SharedPreferences pref;

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

        // 检查用户是否是第一次进入，如果是，令locNumber为0
        sharedPreferences = getSharedPreferences("UsernameAndPassword", MODE_PRIVATE);
        boolean isFirstEnter = sharedPreferences.getBoolean("isFirstEnter", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 进入后修改为false，这里为了方便测试依然用true
        editor.putBoolean("isFirstEnter", true);
        editor.apply();
        if(isFirstEnter) {
            locNumber = 0;
        }
        currentSentence = null;
        user_answer_parameter = null;
        parameter = null;
        property = null;
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
        postAnswerToServer("开始聊天");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 点击按钮开始语音听写
            case R.id.start_speak:
                showTip("请讲话");
                buffer.setLength(0);
                mIatResults.clear();
                userText.setText(null);
                voiceUtil.voice2string(new VoiceUtil.OnUserListener() {
                    @Override
                    public void result(String result) {
                        if (null == result) {
                            voiceUtil.string2voice("不好意思，我没有听清你在讲什么，可以在请你讲一遍吗");
                            return;
                        }
                        Log.d(TAG, "result:-------------"  + result + "---------------result");
                        userText.setText(result);

                        postAnswerToServer(result);
                    }
                });

        }
    }

    /**
     * 将识别结果上传至DM服务器
     */
    private void  postAnswerToServer(final String userAnswer) {
        String username = sharedPreferences.getString("username", "");
        OkHttpClient client = new OkHttpClient();
        String json = "{" + "\"userAnswer\":" + "\"" + userAnswer + "\"" + ","
                +"\"username\":" + "\"" + username + "\"" + ","
                +"\"number\":" + "" + locNumber + "" + ","
                +"\"property\":" + "\"" + property + "\"" + ","
                +"\"parameter\":" + "\"" + parameter + "\"" + ","
                +"\"user_answer_parameter\":" + "\"" + user_answer_parameter + "\"" +"}";
        Log.w("json--------",json);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(USER_ANSWER_URL)
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

            // 客户端解析json返回值
            // 服务器返回chatbot下一问、对接下来客户端答句的操作及操作对象：需要抽取的实体、需要连接的三元组
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 服务器返回chatBot的回复内容
                final String res = response.body().string();
                Log.w("res---------", res);
                try{
                    JSONObject jsonObject = new JSONObject(res);
                    currentSentence = jsonObject.getString("currentSentence");
                    locNumber = jsonObject.getInt("skip_number");
                    if(jsonObject.has("user_answer_parameter")) {
                        user_answer_parameter = jsonObject.getString("user_answer_parameter");
                    } else {
                        user_answer_parameter = null;
                    }
                    if(jsonObject.has("link_parameter")) {
                        JSONObject link_parameter = jsonObject.getJSONObject("link_parameter");
                        parameter = link_parameter.getString("parameter");
                        property = link_parameter.getString("property");
                    } else {
                        parameter = null;
                        property = null;
                    }

                } catch (JSONException  e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        voiceUtil.string2voice(currentSentence);
                        chattyText.setText(currentSentence);
                    }
                });
            }
        });
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}