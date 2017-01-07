package com.codeground.adventurousbulgaria.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class SettingsManager {
    public static void updatePrefsProfileSettings(Context ctx) {
        boolean isPrivate = false;
        if(ParseUser.getCurrentUser().has("is_follow_allowed")){
            isPrivate = !ParseUser.getCurrentUser().getBoolean("is_follow_allowed");
        }
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        prefs.putBoolean("toggle_private_profile", isPrivate);
        prefs.commit();
    }

    public static void updateDeviceInstallationInfo(){
        if(ParseUser.getCurrentUser()!=null) {
            ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
            currentInstall.put("username", ParseUser.getCurrentUser().get("username"));
            currentInstall.saveInBackground();
        }
    }
}
