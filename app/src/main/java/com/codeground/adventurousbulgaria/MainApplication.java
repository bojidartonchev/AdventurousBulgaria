package com.codeground.adventurousbulgaria;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import com.codeground.adventurousbulgaria.Activities.LoginActivity;
import com.codeground.adventurousbulgaria.Interfaces.IOnDataBaseInitialized;
import com.codeground.adventurousbulgaria.Tasks.SendPushNotificationTask;
import com.codeground.adventurousbulgaria.Utilities.KinveyLandmarkJsonObject;
import com.codeground.adventurousbulgaria.Utilities.Landmark;
import com.codeground.adventurousbulgaria.Utilities.PushNotificationData;
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

    public void updateKinveyUser(String column, Object value, KinveyUserCallback callback){
        if(mKinveyClient!=null){
            mKinveyClient.user().put(column,value);

            if(callback!=null){
                mKinveyClient.user().update(callback);
            }else{
                mKinveyClient.user().update(new KinveyUserCallback() {
                    @Override
                    public void onSuccess(User user) {

                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });
            }

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

    public void visitLandmark(final Landmark landmark){
        KinveyReference landmarkRef = new KinveyReference("landmarks", landmark.getKinveyId());

        Object visitedObj = mKinveyClient.user().get("visitedLandmarks");
        ArrayList<KinveyReference> list = new ArrayList<>();

        if(visitedObj != null){
            list = (ArrayList<KinveyReference>) visitedObj;
        }

        if(list!=null && !list.contains(landmarkRef)){
            list.add(landmarkRef);

            updateKinveyUser("visitedLandmarks", list, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Resources res = getResources();
                    String title = res.getString(R.string.push_notification_title_unlock_landmark);
                    String content = String.format(res.getString(R.string.push_notification_content_unlock_landmark), landmark.getName());

                    PushNotificationData mData = new PushNotificationData(title, content);

                    //Start async task to send the notification
                    new SendPushNotificationTask(getApplicationContext()).execute(mData);
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }
    }
}
