package com.codeground.wanderlustbulgaria.Activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;

public class AboutActivity extends AppCompatActivity {

    private TextView versionName;
    private TextView aboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        versionName = (TextView) findViewById(R.id.version_name);
        aboutText = (TextView) findViewById(R.id.about_text);

        aboutText.setMovementMethod(new ScrollingMovementMethod());

        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionName.setText("v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            versionName.setEnabled(false);
            versionName.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.pref_about_title);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
