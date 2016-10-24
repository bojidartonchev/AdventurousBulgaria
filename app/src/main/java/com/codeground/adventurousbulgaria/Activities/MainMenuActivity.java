package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Services.LocationUpdateService;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mProfileBtn;
    private Button mAllLandmarksBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);

        //We are logged in so we can init our DB
        ((MainApplication) getApplication()).initDB();

        startService(new Intent(this, LocationUpdateService.class));

        mProfileBtn=(Button) findViewById(R.id.profile_btn);
        mProfileBtn.setOnClickListener(this);

        mAllLandmarksBtn=(Button) findViewById(R.id.landmarks_btn);
        mAllLandmarksBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.profile_btn){
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
        }
        if(v.getId() == R.id.landmarks_btn){
            Intent intent = new Intent(this, AllLandmarksActivity.class);
            startActivity(intent);
        }
    }
}
