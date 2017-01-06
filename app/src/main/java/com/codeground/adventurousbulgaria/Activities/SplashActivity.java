package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.AllLocationsManager;

public class SplashActivity extends Activity implements OnCompletionListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //make activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        PreferenceManager.setDefaultValues(this, R.xml.fragment_preference_settings, false);

        VideoView video = (VideoView) findViewById(R.id.videoView);
        video.setVideoPath("android.resource://com.codeground.adventurousbulgaria/raw/" + R.raw.splash);
        video.start();
        video.setOnCompletionListener(this);

        AllLocationsManager.getInstance().loadLocations();
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}