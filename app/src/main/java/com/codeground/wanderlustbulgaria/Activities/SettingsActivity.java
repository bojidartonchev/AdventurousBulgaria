package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codeground.wanderlustbulgaria.Fragments.SettingsFragment;
import com.codeground.wanderlustbulgaria.R;
import com.parse.ParseUser;


public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.pref_settings_title);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            case R.id.about_btn: {
                onAboutClick();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.settings_submenu, menu);

        Drawable drawable = menu.findItem(R.id.about_btn).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.white));
        menu.findItem(R.id.about_btn).setIcon(drawable);
        // Return true to display menu
        return true;
    }

    private void onAboutClick() {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }
}
