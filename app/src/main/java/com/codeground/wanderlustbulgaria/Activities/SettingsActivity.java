package com.codeground.wanderlustbulgaria.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.codeground.wanderlustbulgaria.Fragments.SettingsFragment;
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
        }
    }
}
