package com.codeground.wanderlustbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.LocalParseLocation;


public class SplashActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference_settings, false);

        LocalParseLocation.updateDatabaseIfNeeded();

        proceedToNextActivity();
    }

    private void proceedToNextActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}