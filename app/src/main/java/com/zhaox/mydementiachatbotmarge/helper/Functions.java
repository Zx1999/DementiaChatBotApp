package com.zhaox.mydementiachatbotmarge.helper;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.zhaox.mydementiachatbotmarge.widgets.ProgressBarDialog;

import java.util.Objects;

import okhttp3.MediaType;


/**
 *
 */

public class Functions {

    //Main URL
    private static String MAIN_URL = "http://47.116.142.235:5000/";

    // Login URL
    public static String LOGIN_URL = MAIN_URL + "login";

    // Register URL
    public static String REGISTER_URL = MAIN_URL + "register";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
//    public void logoutUser(Context context){
//        DatabaseHandler db = new DatabaseHandler(context);
//        db.resetTables();
//    }

    /**
     *  Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 手机号
     */
    public static boolean isValidUsername(String phone) {
        if(phone.length() == 11) return true;
        return false;

    }

    /**
    * 验证码
    */
    public static boolean isValidCode(String code) {
        return true;
    }

    /**
     *  Hide Keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public static void showProgressDialog(Context context, String title) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        DialogFragment newFragment = ProgressBarDialog.newInstance(title);
        newFragment.show(fm, "dialog");
    }

    public static void hideProgressDialog(Context context) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
