package com.codeground.adventurousbulgaria.Tasks;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.codeground.adventurousbulgaria.Activities.LoginActivity;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.PushNotificationData;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SendPushNotificationTask extends AsyncTask<PushNotificationData, Void, NotificationCompat.Builder> {

    private Context mCtx;

    public SendPushNotificationTask(Context ctx) {
        this.mCtx = ctx;
    }

    @Override
    protected void onPostExecute(NotificationCompat.Builder mBuilder) {
        Intent resultIntent = new Intent(mCtx, LoginActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mCtx, 0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mMgr = (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);
        mMgr.notify(mNotificationId,mBuilder.build());

        super.onPostExecute(mBuilder);
    }

    @Override
    protected NotificationCompat.Builder doInBackground(PushNotificationData... data) {
        PushNotificationData pushNotificationData = data[0];
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(pushNotificationData.getTitle())
                .setContentText(pushNotificationData.getContent())
                .setSound(alarmSound);

        return mBuilder;
    }
}
