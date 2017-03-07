package com.codeground.wanderlustbulgaria.BroadcastReceivers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.codeground.wanderlustbulgaria.Activities.SplashActivity;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.LifecycleHandler;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.devspark.appmsg.AppMsg;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.codeground.wanderlustbulgaria.R.string.app_name;

public class ParsePushCustomReceiver extends ParsePushBroadcastReceiver {

    private Class DEFAULT_ACTIVITY = SplashActivity.class;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            try {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json;
                json = new JSONObject(jsonData);

                if(LifecycleHandler.isApplicationInForeground()){
                    sendInAppNotification(LifecycleHandler.getCurrentActivity(), json);
                }else{
                    sendPushNotification(context, json);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendInAppNotification(Activity activity, JSONObject data){
        String pushContent = activity.getString(app_name);

        if (data.has("alert")) {
            try {
                pushContent = data.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        NotificationsManager.showDropDownNotification(activity, pushContent, AppMsg.STYLE_ALERT);
    }

    private void sendPushNotification(Context context, JSONObject data){
        try {
            if (data != null) {
                String pushTitle = context.getString(app_name);

                if(data.has("title")){
                    pushTitle = data.getString("title");
                }

                String pushContent = context.getString(app_name);

                if(data.has("alert")){
                    pushContent = data.getString("alert");
                }

                Intent i = new Intent(context, DEFAULT_ACTIVITY);

                if(data.has("target_activity")){
                    String activityName = data.getString("target_activity");

                    Class<?> c = null;
                    if(activityName != null) {
                        try {
                            c = Class.forName(activityName);

                            i = new Intent(context, c);
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                // Sets an ID for the notification
                int mNotificationId = 001;

                if(data.has("extras")){
                    JSONObject extrasJson = data.getJSONObject("extras");

                    try {
                        Iterator<String> temp = extrasJson.keys();
                        while (temp.hasNext()) {
                            String key = temp.next();
                            String value = extrasJson.getString(key);

                            if(key.equals("username"))
                            {
                                //message notification
                                //just for testing
                                mNotificationId = value.length();
                            }

                            i.putExtra(key, value);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                context,
                                0,
                                i,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle(pushTitle)
                                .setContentText(pushContent)
                                .setContentIntent(resultPendingIntent)
                                .setAutoCancel(true);

                mBuilder.setLights(Color.CYAN, 500, 500);

                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);

                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
}
