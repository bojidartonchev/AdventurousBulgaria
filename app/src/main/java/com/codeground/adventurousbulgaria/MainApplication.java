package com.codeground.adventurousbulgaria;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.codeground.adventurousbulgaria.Activities.LoginActivity;
import com.codeground.adventurousbulgaria.Interfaces.IOnDataBaseInitialized;
import com.codeground.adventurousbulgaria.Utilities.KinveyLandmarkJsonObject;
import com.codeground.adventurousbulgaria.Utilities.Landmark;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.model.KinveyReference;
import com.orm.SugarContext;
import com.orm.SugarDb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class MainApplication extends Application {
    //TODO Declare Kinvey IDs using a properties file if needed at some point
    private static final String KINVEY_APP_ID = "kid_r1TpUSAT";
    private static final String KINVEY_APP_SECRET = "34d8d77db3ad4a74a4c3d49627f648ef";
    private static final String LANDMARKS_DATABASE_NAME = "landmarks_database.db";

    //Reference
    //http://devcenter.kinvey.com/android/guides/getting-started
    private Client mKinveyClient;

    @Override
    public void onCreate() {
        super.onCreate();

        //Facebook SDK Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Kinvey SDK Initialization
        mKinveyClient = new Client.Builder(KINVEY_APP_ID, KINVEY_APP_SECRET
                , this.getApplicationContext()).build();

        SugarContext.init(getApplicationContext());

        //TODO REMOVE IN RELEASE VERSION
        if(doesDatabaseExists(this,LANDMARKS_DATABASE_NAME)){
            SugarDb sugarDB = new SugarDb(getApplicationContext());
            new File(sugarDB.getDB().getPath()).delete();
        }
    }

    public Client getKinveyClient() {
        return mKinveyClient;
    }

    public void sendPushNotification(String title, String body){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body);

        Intent resultIntent = new Intent(this, LoginActivity.class);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mMgr.notify(mNotificationId,mBuilder.build());
    }

    public void updateKinveyUser(String column, Object value){
        if(mKinveyClient!=null){
            mKinveyClient.user().put(column,value);
            mKinveyClient.user().update(new KinveyUserCallback() {
                @Override
                public void onFailure(Throwable e) {

                }
                @Override
                public void onSuccess(User u) {

                }
            });
        }
    }

    public void initDB(final IOnDataBaseInitialized cb){
        boolean dbExists = doesDatabaseExists(this,LANDMARKS_DATABASE_NAME);

        if(!dbExists){
            Landmark.findById(Landmark.class,(long) 1);

            //The EventEntity class is defined above
            //Refered from http://devcenter.kinvey.com/android/guides/datastore
            AsyncAppData<KinveyLandmarkJsonObject> myevents = mKinveyClient.appData("landmarks", KinveyLandmarkJsonObject.class);
            myevents.get(new KinveyListCallback<KinveyLandmarkJsonObject>() {
                @Override
                public void onSuccess(KinveyLandmarkJsonObject[] result) {
                    for (KinveyLandmarkJsonObject item : result) {
                        Landmark currentLandmark = new Landmark(item);
                        currentLandmark.save();
                    }
                    cb.OnDBInit();
                }
                @Override
                public void onFailure(Throwable error)  {
                    //TODO notify user and retry
                }
            });
        }
    }

    private boolean doesDatabaseExists(ContextWrapper context, String dbName){
        File dbFile =  context.getDatabasePath(dbName);

        return dbFile.exists();
    }

    public void visitLandmark(Landmark landmark){
        KinveyReference landmarkRef = new KinveyReference("landmarks", landmark.getKinveyId());

        Object visitedObj = mKinveyClient.user().get("visitedLandmarks");
        HashSet<KinveyReference> visitedLandmarks = new HashSet<>();

        if(visitedObj != null){
            visitedLandmarks.addAll(((ArrayList<KinveyReference>) visitedObj));
        }

        visitedLandmarks.add(landmarkRef);
        updateKinveyUser("visitedLandmarks", new ArrayList<>(visitedLandmarks));
    }
}
