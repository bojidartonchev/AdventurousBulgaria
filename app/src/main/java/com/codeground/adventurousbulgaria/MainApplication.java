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
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Parse Initialization
        ParseObject.registerSubclass(ParseLocation.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId(getString(R.string.parse_app_id))
                        .clientKey(getString(R.string.parse_client_key))
                        .server(getString(R.string.parse_server)).build());

        //Facebook SDK Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ParseFacebookUtils.initialize(this);

        if(ParseUser.getCurrentUser()!=null) {
            ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
            currentInstall.put("username", ParseUser.getCurrentUser().get("username"));
            currentInstall.saveInBackground();
        }
    }
}
