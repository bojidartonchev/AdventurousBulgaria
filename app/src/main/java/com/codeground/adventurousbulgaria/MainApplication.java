package com.codeground.adventurousbulgaria;

import android.app.Application;
import android.util.Log;

import com.codeground.adventurousbulgaria.Utilities.ParseLocation;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Parse Initialization
        ParseObject.registerSubclass(ParseLocation.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId("Aev0cw9ckqsWq9BGiGfnPXACPbLTHypE0ZpejrPQ")
                        .clientKey("6pLQaBu3BcEceHbTXGkOyWZs4J4qVe8EVwEUlfkN")
                        .server("https://parseapi.back4app.com/").build());
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        //Facebook SDK Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ParseFacebookUtils.initialize(this);

        // Update Installation
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "838294079504");
        installation.saveInBackground();

        ParsePush.subscribeInBackground("visit_channel", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                    Log.d("Parse","Success");
                else
                    Log.d("Parse","Failed");
            }
        });
    }
}
