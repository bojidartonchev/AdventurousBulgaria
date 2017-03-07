package com.codeground.wanderlustbulgaria.Utilities;

import android.app.Activity;

import com.devspark.appmsg.AppMsg;
import com.sdsmdg.tastytoast.TastyToast;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationsManager {
    public static void showDropDownNotification(Activity activity, String msg, AppMsg.Style style){
        if(activity != null){
            AppMsg.makeText(activity, msg, style).show();
        }
    }

    public static void showToast(String msg, int style){
        TastyToast.makeText(getApplicationContext(), msg, TastyToast.LENGTH_LONG, style);
    }
}
