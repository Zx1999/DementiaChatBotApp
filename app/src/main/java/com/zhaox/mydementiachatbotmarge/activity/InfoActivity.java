package com.zhaox.mydementiachatbotmarge.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhaox.mydementiachatbotmarge.R;
import com.zhaox.mydementiachatbotmarge.helper.TipDataModel;
import com.zhaox.tagview.EasyTipDragView;
import com.zhaox.tagview.bean.SimpleTitleTip;
import com.zhaox.tagview.bean.Tip;
import com.zhaox.tagview.widget.TipItemView;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private EasyTipDragView easyTipDragView;
    private Boolean submitted;
    boolean isFirstEnter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        submitted = false;
        SharedPreferences sharedPreferences = getSharedPreferences("UsernameAndPassword", MODE_PRIVATE);
        isFirstEnter = sharedPreferences.getBoolean("isFirstEnter", true);
        easyTipDragView =(EasyTipDragView)findViewById(R.id.easy_tip_drag_view);
        //设置已包含的标签数据
        easyTipDragView.setAddData(TipDataModel.getAddTips());
        //设置可以添加的标签数据
        easyTipDragView.setDragData(TipDataModel.getDragTips());
        //在easyTipDragView处于非编辑模式下点击item的回调（编辑模式下点击item作用为删除item）
        easyTipDragView.setSelectedListener(new TipItemView.OnSelectedListener() {
            @Override
            public void onTileSelected(Tip entity, int position, View view) {
                toast(((SimpleTitleTip) entity).getTip());
            }
        });
        //设置每次数据改变后的回调（例如每次拖拽排序了标签或者增删了标签都会回调）
        easyTipDragView.setDataResultCallback(new EasyTipDragView.OnDataChangeResultCallback() {
            @Override
            public void onDataChangeResult(ArrayList<Tip> tips) {
                Log.i("heheda", tips.toString());
            }
        });
        //设置点击“确定”按钮后最终数据的回调
        easyTipDragView.setOnCompleteCallback(new EasyTipDragView.OnCompleteCallback() {
            @Override
            public void onComplete(ArrayList<Tip> tips) {
                toast("最终数据：" + tips.toString());
                Log.i("最终数据", tips.toString());
                // 检查用户是否是第一次进入，如果是，令locNumber为0

                if(isFirstEnter) {
                    submitToGuider();
                } else {
                    Intent i = new Intent(InfoActivity.this, HomeActivity.class);
                }

                //   btn.setVisibility(View.VISIBLE);
            }
        });
        easyTipDragView.open();
    }

    private void submitToGuider() {
        submitted = true;
        Bundle b = new Bundle();
        b.putBoolean("submitted", submitted);
        Intent i = new Intent(InfoActivity.this, GuiderActivity.class);
        Log.w("Info----submitted", String.valueOf(submitted));
        i.putExtras(b);
        startActivity(i);
    }

    public void toast(String str){
        Toast.makeText(InfoActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            //点击返回键
            case KeyEvent.KEYCODE_BACK:
                if(isFirstEnter) {
                    submitToGuider();
                } else {
                    Intent i = new Intent(InfoActivity.this, HomeActivity.class);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
