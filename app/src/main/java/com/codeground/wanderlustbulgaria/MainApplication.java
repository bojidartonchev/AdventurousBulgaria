package com.codeground.wanderlustbulgaria;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.codeground.wanderlustbulgaria.Utilities.LifecycleHandler;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.orm.SugarContext;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install((this));
        //Parse Initialization
        ParseObject.registerSubclass(ParseLocation.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                        .applicationId(getString(R.string.parse_app_id))
                        .clientKey(getString(R.string.parse_client_key))
                        .server(getString(R.string.parse_server)).build());

        SugarContext.init(this);

        ParseFacebookUtils.initialize(this);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "838294079504");
        installation.saveInBackground();

        registerActivityLifecycleCallbacks(new LifecycleHandler());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
