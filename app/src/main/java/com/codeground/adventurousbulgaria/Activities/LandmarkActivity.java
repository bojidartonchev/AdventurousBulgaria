package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Landmark;

public class LandmarkActivity extends AppCompatActivity {
    private TextView mTitle;
    private TextView mDescription;
    private TextView mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mTitle = (TextView)findViewById(R.id.landmark_title);
        mDescription = (TextView)findViewById(R.id.landmark_description);
        mLocation = (TextView)findViewById(R.id.landmark_location);

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
        }
    }
}
