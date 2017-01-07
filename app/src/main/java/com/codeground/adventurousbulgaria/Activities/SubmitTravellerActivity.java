package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.DialogWindowManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class SubmitTravellerActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PLACE_PICKER_REQUEST = 1;

    private TextView mLatitudeField;
    private TextView mLongitudeField;
    private EditText mCityField;
    private LocationManager mLocationManager;
    private Double mLongitude;
    private Double mLatitude;
    private String mCity;

    private Button mSubmitBtn;
    private Button mMapBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_submit_traveller);

        mCityField = (EditText) findViewById(R.id.travellers_city_field);

        mLatitudeField = (TextView) findViewById(R.id.travellers_latitude);
        mLongitudeField = (TextView) findViewById(R.id.travellers_longitude);

        mSubmitBtn = (Button) findViewById(R.id.travellers_submit_btn);
        mMapBtn = (Button) findViewById(R.id.travellers_location_btn);

        mSubmitBtn.setOnClickListener(this);
        mMapBtn.setOnClickListener(this);

        initLocation();
    }

    private void initLocation() {
        Location currLocation = getLastKnownLocation();

        mLongitude =  currLocation.getLongitude();
        mLatitude =  currLocation.getLatitude();
        mLongitudeField.setText("Longitude: "+mLongitude);
        mLatitudeField.setText("Latitude: "+mLatitude);

        updateCity();
    }

    private void updateCity() {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(mLatitude, mLongitude, 1);

            if (addresses.size() > 0)
            {
                mCity = addresses.get(0).getLocality();
            }
            else
            {
                mCity = getString(R.string.label_undefined_city);
            }

            if(mCityField!=null){
                mCityField.setText(mCity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.travellers_location_btn){
            DialogWindowManager.show(this);
            pickLocation();
        }
        if(v.getId()==R.id.travellers_submit_btn){
            submitTraveller();
        }
    }

    private void submitTraveller() {
        ParseUser user = ParseUser.getCurrentUser();

        ParseGeoPoint point = new ParseGeoPoint(mLatitude,mLongitude);
        ParseObject traveller = new ParseObject(getString(R.string.db_traveller_dbname));

        traveller.put("origin_user", user);
        traveller.put("from_location", point);
        traveller.put("from_city", mCity);
        //traveller.put("to_location", );

        DialogWindowManager.show(this);
        traveller.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.d("Save",e.getMessage());
                }
                DialogWindowManager.dismiss();
                finish();
            }
        });
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            } catch (SecurityException e){

            }

        }
        return bestLocation;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                    Place place = PlacePicker.getPlace(getApplicationContext(),data);
                    LatLng coords = place.getLatLng();
                    mLatitude = coords.latitude;
                    mLongitude = coords.longitude;
                    mLatitudeField.setText("Latitude: "+ mLatitude);
                    mLongitudeField.setText("Longitude: "+ mLongitude);
                    updateCity();
                    DialogWindowManager.dismiss();
            }
        }
        else {
            DialogWindowManager.dismiss();
        }
    }

    private void pickLocation(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }





}
