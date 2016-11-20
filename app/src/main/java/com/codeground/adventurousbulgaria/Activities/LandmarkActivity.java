package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Landmark;
import com.squareup.picasso.Picasso;

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

        Landmark currentLandmark = getIntent().getParcelableExtra("landmark");

        if(currentLandmark!=null){
            if(mTitle!=null){
                mTitle.setText(currentLandmark.getName());
            }
            if(mDescription!=null){
                mDescription.setText(currentLandmark.getDescription());
            }
            if(mLocation!=null){
                mLocation.setText(currentLandmark.getLocationName());
            }
            if(mPhoto!=null){
                //Load the icon
                Picasso.with(this).load(currentLandmark.getPhotoURL()).into(mPhoto);
            }
        }
    }
}