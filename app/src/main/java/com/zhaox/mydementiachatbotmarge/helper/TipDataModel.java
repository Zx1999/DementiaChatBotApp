package com.zhaox.mydementiachatbotmarge.helper;

import com.zhaox.tagview.bean.SimpleTitleTip;
import com.zhaox.tagview.bean.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30 0030.
 */
public class TipDataModel {
    private static String[] dragTips ={};
    private static String[] addTips ={"探亲","访友","客人来访","生病","锻炼身体","状态良好","逛公园"};
    public static List<Tip> getDragTips(){
        List<Tip> result = new ArrayList<>();
        for(int i=0;i<dragTips.length;i++){
            String temp =dragTips[i];
            SimpleTitleTip tip = new SimpleTitleTip();
            tip.setTip(temp);
            tip.setId(i);
            result.add(tip);
        }
        return result;
    }
    public static List<Tip> getAddTips(){
        List<Tip> result = new ArrayList<>();
        for(int i=0;i<addTips.length;i++){
            String temp =addTips[i];
            SimpleTitleTip tip = new SimpleTitleTip();
            tip.setTip(temp);
            tip.setId(i+dragTips.length);
            result.add(tip);
        }
        return result;
    }
}
