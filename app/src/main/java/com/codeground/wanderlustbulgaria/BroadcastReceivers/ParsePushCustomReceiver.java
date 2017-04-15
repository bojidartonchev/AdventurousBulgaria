package com.codeground.wanderlustbulgaria.BroadcastReceivers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.codeground.wanderlustbulgaria.Activities.ChatActivity;
import com.codeground.wanderlustbulgaria.Activities.MainMenuActivity;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.LifecycleHandler;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.codeground.wanderlustbulgaria.R.string.app_name;

public class ParsePushCustomReceiver extends ParsePushBroadcastReceiver {

    private Class DEFAULT_ACTIVITY = MainMenuActivity.class;

    private int mNotificationId = 001;
    private Intent mIntent;
    private String pushTitle;
    private String pushContent;

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            try {
                String jsonData = extras.getString("com.parse.Data");
                JSONObject json;
                json = new JSONObject(jsonData);

                sendPushNotification(context, json);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPushNotification(final Context context, JSONObject data){
        boolean isChatNotification = false;
        String targetUsername = "";

        try {
            if (data != null) {
                pushTitle = context.getString(app_name);

                if(data.has("title")){
                    pushTitle = data.getString("title");
                }

                pushContent = context.getString(app_name);

                if(data.has("alert")){
                    pushContent = data.getString("alert");
                }

                mIntent = new Intent(context, DEFAULT_ACTIVITY);

                Class<?> c = null;
                if(data.has("target_activity")){
                    String activityName = data.getString("target_activity");

                    if(activityName != null) {
                        try {
                            c = Class.forName(activityName);

                            if(c == ChatActivity.class){
                                isChatNotification = true;
                            }

                            mIntent = new Intent(context, c);
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                if(data.has("extras")){
                    JSONObject extrasJson = data.getJSONObject("extras");

                    try {
                        Iterator<String> temp = extrasJson.keys();
                        while (temp.hasNext()) {
                            String key = temp.next();
                            String value = extrasJson.getString(key);

                            if(key.equals("username") && isChatNotification)
                            {
                                targetUsername = value;
                                //chat push received
                                if(areAlreadyChatting(c, value)){
                                    //user chat with that user already open
                                    return;
                                }

                                mNotificationId = value.length();
                            }

                            mIntent.putExtra(key, value);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(isChatNotification){
                    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                    userQuery.whereEqualTo("username", targetUsername);
                    userQuery.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser object, ParseException e) {
                            ParseFile img = object.getParseFile("profile_picture");

                            if(object!=null && e==null){
                                img.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                            buildNotification(context, mIntent, mNotificationId, pushTitle, pushContent, bitmap);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }else{
                    buildNotification(context, mIntent, mNotificationId, pushTitle, pushContent, null);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private boolean areAlreadyChatting(Class<?> c, String user){
        if(c != null && c == ChatActivity.class){
            Activity currentActivity = LifecycleHandler.getCurrentActivity();
            if(currentActivity != null && currentActivity.getClass() == ChatActivity.class){
                String currentChatUsername = currentActivity.getIntent().getStringExtra("username");

                if(currentChatUsername.equals(user)){
                    return true;
                }
            }
        }

        return false;
    }

    private void buildNotification(Context ctx, Intent i, int id, String title, String content, Bitmap image){
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DEFAULT_ACTIVITY);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(i);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);

        if(image!=null){
            mBuilder.setLargeIcon(image);
        }

        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);

        mBuilder.setLights(Color.CYAN, 500, 500);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(id, mBuilder.build());
    }
}
