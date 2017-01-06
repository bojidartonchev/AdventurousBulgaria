package com.codeground.adventurousbulgaria.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.codeground.adventurousbulgaria.R;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preference_settings);
    }
}
