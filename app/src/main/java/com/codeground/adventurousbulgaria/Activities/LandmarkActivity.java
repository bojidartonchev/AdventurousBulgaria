package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class LandmarkActivity extends AppCompatActivity {
    private TextView mTitle;
    private TextView mDescription;
    private TextView mLocation;
    private ImageView mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mTitle = (TextView)findViewById(R.id.landmark_title);
        mDescription = (TextView)findViewById(R.id.landmark_description);
        mLocation = (TextView)findViewById(R.id.landmark_location);
        mPhoto = (ImageView) findViewById(R.id.monument_picture);

        String currentLandmarkName = getIntent().getStringExtra("locationName");
        String currentLandmarkDesc = getIntent().getStringExtra("locationDescription");
        String currentLandmarkCity = getIntent().getStringExtra("locationCity");
        File currentLandmarkPhoto = (File)getIntent().getExtras().get("locationPhoto");

        if(mTitle!=null){
            mTitle.setText(currentLandmarkName);
        }
        if(mDescription!=null){
            mDescription.setText(currentLandmarkDesc);
        }
        if(mLocation!=null){
            mLocation.setText(currentLandmarkCity);
        }
        if(mPhoto!=null){
            //Load the icon
            Picasso.with(this).load(currentLandmarkPhoto).into(mPhoto);
        }

    }
}
