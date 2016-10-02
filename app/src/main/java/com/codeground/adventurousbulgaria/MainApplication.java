package com.codeground.adventurousbulgaria;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kinvey.android.Client;

public class MainApplication extends Application {
    //TODO Declare Kinvey IDs using a properties file if needed at some point
    private static final String KINVEY_APP_ID = "kid_r1TpUSAT";
    private static final String KINVEY_APP_SECRET = "34d8d77db3ad4a74a4c3d49627f648ef";

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
    }

    public Client getKinveyClient() {
        return mKinveyClient;
    }
}
