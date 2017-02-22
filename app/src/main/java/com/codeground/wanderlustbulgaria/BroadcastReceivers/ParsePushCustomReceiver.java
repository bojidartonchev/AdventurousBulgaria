package com.codeground.wanderlustbulgaria.BroadcastReceivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;


public class ParsePushCustomReceiver extends ParsePushBroadcastReceiver {
    protected  static  String pushTitle="";

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);

        pushTitle="";
        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json;
                json = new JSONObject(jsonData);
                pushTitle = json.getString("title");
                String pushContent = json.getString("alert");

                String activityName = json.getString("target_activity");

                Class<?> c = null;
                if(activityName != null) {
                    try {
                        c = Class.forName(activityName );

                        Intent i = new Intent(context, c);
                        i.putExtras(intent.getExtras());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
}
