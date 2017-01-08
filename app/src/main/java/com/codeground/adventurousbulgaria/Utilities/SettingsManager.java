package com.codeground.adventurousbulgaria.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class SettingsManager {
    public static void updatePrefsProfileSettings(final Context ctx) {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject user, ParseException e) {
                boolean isPrivate = false;
                if(user.has("is_follow_allowed")){
                    isPrivate = !user.getBoolean("is_follow_allowed");
                }
                SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
                prefs.putBoolean("toggle_private_profile", isPrivate);
                prefs.commit();
            }
        });

    }

    public static void updateDeviceInstallationInfo(){
        if(ParseUser.getCurrentUser()!=null) {
            ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
            currentInstall.put("username", ParseUser.getCurrentUser().get("username"));
            currentInstall.saveInBackground();
        }
    }
}
