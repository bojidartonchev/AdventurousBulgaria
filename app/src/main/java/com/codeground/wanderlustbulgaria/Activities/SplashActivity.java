package com.codeground.wanderlustbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.codeground.wanderlustbulgaria.Interfaces.IDatabaseInitializer;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.LocaleUtils;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.LocalParseLocation;


public class SplashActivity extends Activity implements IDatabaseInitializer
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference_settings, false);

        //Init locale language
        LocaleUtils.loadLocale(getBaseContext());

        LocalParseLocation.updateDatabaseIfNeeded(this);
    }

    private void proceedToNextActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void OnDatabaseUpdated(com.parse.ParseException e) {
        if(e==null){
            proceedToNextActivity();
        }else{
            //TODO notify for update error.
        }
    }
}