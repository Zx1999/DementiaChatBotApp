package com.zhaox.mydementiachatbotmarge.utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static android.content.ContentValues.TAG;

public class VoiceUtil {
    private Activity voiceChatActivity;
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();
    private String resultType = "plain";
    private StringBuffer buffer = new StringBuffer();
    private SpeechSynthesizer mTts;
    private OnUserListener userListener;

    public VoiceUtil(Activity voiceChatActivity) {
        this.voiceChatActivity = voiceChatActivity;
        SpeechUtility.createUtility(voiceChatActivity, SpeechConstant.APPID + "=c0c4b6e2");
    }

    public void voice2string(final OnUserListener userListener) {
        if(mTts != null && mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
        this.userListener = userListener;
        // 语音听写对象 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(voiceChatActivity, mInitListener);
        if (null == mIat) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            Log.d(TAG, "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }
        setParam();
        mIat.startListening(mRecognizerListener);
    }

    public void string2voice(String text) {
        mTts = SpeechSynthesizer.createSynthesizer(voiceChatActivity, null);
        // 发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");
        // 语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        // 音量
        mTts.setParameter(SpeechConstant.VOLUME, "70");
        int code = mTts.startSpeaking(text, null);
        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
                Log.d(TAG, "请安装语记!");
            } else {
                Log.d(TAG, "语音合成失败,错误码: " + code);
            }
        }
    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);
        //设置结果返回语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置结果返回口音
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        // 自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT,"1");

        // 设置音频保存路径，保存音频格式支持pcm、wav.
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
//                getExternalFilesDir("msc").getAbsolutePath() + "/iat.wav");
    }

    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.d(TAG, "初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.d(TAG, "开始说话");

        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            Log.d(TAG, "onError " + error.getPlainDescription(true));


        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.d(TAG, "结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            if (isLast) {
                Log.d(TAG, "onResult 结束");
            }
            if (resultType.equals("json")) {
                printResult(results);
                return;
            }
            if (resultType.equals("plain")) {
                buffer.append(results.getResultString());
                if (userListener != null) {
                    userListener.result(buffer.toString());
                    Log.d(TAG, "VoiceUtil result:-------------"  + buffer.toString() + "---------------result");
                }
//                mResultText.setText(buffer.toString());
//                mResultText.setSelection(mResultText.length());
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "当前正在说话，音量大小 = " + volume + " 返回音频数据 = " + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    /**
     * 显示结果
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        if (userListener != null) {
            userListener.result(buffer.toString());
        }
//        mResultText.setText(resultBuffer.toString());
//        mResultText.setSelection(mResultText.length());
    }

    public interface OnUserListener {
        public void result(String result);
    }
}
