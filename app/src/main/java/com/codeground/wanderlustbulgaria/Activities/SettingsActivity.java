package com.codeground.wanderlustbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.codeground.wanderlustbulgaria.Fragments.SettingsFragment;
import com.codeground.wanderlustbulgaria.Utilities.LocaleUtils;
import com.parse.ParseUser;


public class SettingsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("toggle_private_profile")) {
            boolean isPrivate = sharedPreferences.getBoolean(key, false);

            //Update user
            ParseUser.getCurrentUser().put("is_follow_allowed", !isPrivate);
            ParseUser.getCurrentUser().saveInBackground();
        }else if (key.equals("pref_key_language_preference")) {
            String lang = sharedPreferences.getString(key, "en");

            LocaleUtils.changeLang(getBaseContext(), lang);

            Intent refresh = new Intent(this, SettingsActivity.class);
            startActivity(refresh);
            finish();
        }
    }
}
