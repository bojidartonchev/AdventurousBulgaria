package com.codeground.adventurousbulgaria;

import android.app.Application;
import android.content.ContextWrapper;

import com.codeground.adventurousbulgaria.Utilities.Landmark;
import com.codeground.adventurousbulgaria.Utilities.ParseLocation;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.orm.SugarContext;
import com.orm.SugarDb;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import java.io.File;

public class MainApplication extends Application {
    private static final String LANDMARKS_DATABASE_NAME = "landmarks_database.db";

    @Override
    public void onCreate() {
        super.onCreate();

        //Parse Initialization
        ParseObject.registerSubclass(ParseLocation.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId("Aev0cw9ckqsWq9BGiGfnPXACPbLTHypE0ZpejrPQ")
                        .clientKey("6pLQaBu3BcEceHbTXGkOyWZs4J4qVe8EVwEUlfkN")
                        .server("https://parseapi.back4app.com/").build());

        //Facebook SDK Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ParseFacebookUtils.initialize(this);

        SugarContext.init(getApplicationContext());

        //TODO REMOVE IN RELEASE VERSION
        if(doesDatabaseExists(this,LANDMARKS_DATABASE_NAME)){

            SugarDb sugarDB = new SugarDb(getApplicationContext());
            new File(sugarDB.getDB().getPath()).delete();
        }
    }

    private boolean doesDatabaseExists(ContextWrapper context, String dbName){
        File dbFile =  context.getDatabasePath(dbName);

        return dbFile.exists();
    }

    public void visitLandmark(final Landmark landmark){
       // KinveyReference landmarkRef = new KinveyReference("landmarks", landmark.getKinveyId());
//
       // Object visitedObj = mKinveyClient.user().get("visitedLandmarks");
       // ArrayList<KinveyReference> list = new ArrayList<>();
//
       // if(visitedObj != null){
       //     list = (ArrayList<KinveyReference>) visitedObj;
       // }
//
       // if(list!=null && !list.contains(landmarkRef)){
       //     list.add(landmarkRef);
//
       //     updateKinveyUser("visitedLandmarks", list, new KinveyUserCallback() {
       //         @Override
       //         public void onSuccess(User user) {
       //             Resources res = getResources();
       //             String title = res.getString(R.string.push_notification_title_unlock_landmark);
       //             String content = String.format(res.getString(R.string.push_notification_content_unlock_landmark), landmark.getName());
//
       //             PushNotificationData mData = new PushNotificationData(title, content);
//
       //             //Start async task to send the notification
       //             new SendPushNotificationTask(getApplicationContext()).execute(mData);
       //         }
//
       //         @Override
       //         public void onFailure(Throwable throwable) {
//
       //         }
       //     });
       // }
    }


}
