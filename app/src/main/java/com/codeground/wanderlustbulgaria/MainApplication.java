package com.codeground.wanderlustbulgaria;

import android.support.multidex.MultiDexApplication;

import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class MainApplication extends MultiDexApplication {

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
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        ParseFacebookUtils.initialize(this);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "838294079504");
        installation.saveInBackground();
    }
}
