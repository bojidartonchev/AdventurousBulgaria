package com.codeground.wanderlustbulgaria.BroadcastReceivers;

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
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.codeground.wanderlustbulgaria.R.string.app_name;

public class ParsePushCustomReceiver extends ParsePushBroadcastReceiver {

    private Class DEFAULT_ACTIVITY = SplashActivity.class;
    private final int NOTIFICATION_ID = 237;
    private static int value = 0;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json;
                json = new JSONObject(jsonData);

                String pushTitle = context.getString(app_name);

                if(json.has("title")){
                    pushTitle = json.getString("title");
                }

                String pushContent = context.getString(app_name);

                if(json.has("alert")){
                    pushTitle = json.getString("alert");
                }

                Intent i = new Intent(context, DEFAULT_ACTIVITY);

                if(json.has("target_activity")){
                    String activityName = json.getString("target_activity");

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

                if(json.has("extras")){
                    JSONObject extrasJson = json.getJSONObject("extras");

                    try {
                        Iterator<String> temp = extrasJson.keys();
                        while (temp.hasNext()) {
                            String key = temp.next();
                            String value = extrasJson.getString(key);

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

                // Sets an ID for the notification
                int mNotificationId = 001;
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
