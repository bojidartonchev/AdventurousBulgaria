package com.codeground.wanderlustbulgaria.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleUtils {
    public static void changeLang(Context ctx, String lang){
        Locale myLocale = new Locale(lang);
        Resources res = ctx.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
    }

    public static void saveLocale(Context ctx, String lang)
    {
        String langPref = "pref_key_language_preference";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public static void loadLocale(Context ctx)
    {
        String langPref = "pref_key_language_preference";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String language = prefs.getString(langPref, "en");
        changeLang(ctx, language);
    }
}
