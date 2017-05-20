package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseUtilities;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class SubmitLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PICTURE_ONE = 1;
    private final int PICTURE_TWO = 2;
    private final int PICTURE_THREE = 3;
    private final int PLACE_PICKER_REQUEST = 4;


    private EditText mNameField;
    private EditText mDescField;
    private EditText mCityField;
    private ImageView mPhoto1Field;
    private ImageView mPhoto2Field;
    private ImageView mPhoto3Field;
    private ParseFile mPhoto1;
    private ParseFile mPhoto2;
    private ParseFile mPhoto3;
    private TextView mAlertField;

    private LocationManager mLocationManager;
    private Double mLongitude;
    private Double mLatitude;

    private Button mSubmitBtn;
    private Button mMapBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_submit_location);

        mNameField = (EditText) findViewById(R.id.name_field);
        mDescField = (EditText) findViewById(R.id.desc_field);
        mCityField = (EditText) findViewById(R.id.city_field);
        mPhoto1Field = (ImageView) findViewById(R.id.photo1);
        mPhoto2Field = (ImageView) findViewById(R.id.photo2);
        mPhoto3Field = (ImageView) findViewById(R.id.photo3);
        mPhoto3Field = (ImageView) findViewById(R.id.photo3);
        mAlertField= (TextView) findViewById(R.id.alert);
        mSubmitBtn = (Button) findViewById(R.id.submit_btn);
        mMapBtn = (Button) findViewById(R.id.location_btn);

        mAlertField.setText(getString(R.string.alert_location_current_location));

        mSubmitBtn.setOnClickListener(this);
        mMapBtn.setOnClickListener(this);
        mPhoto1Field.setOnClickListener(this);
        mPhoto2Field.setOnClickListener(this);
        mPhoto3Field.setOnClickListener(this);

        mPhoto1=null;
        mPhoto2=null;
        mPhoto3=null;

        initLocation();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.submit_location);
    }

    private void initLocation() {
        Location currLocation = getLastKnownLocation();
        if(currLocation!=null) {
            mLongitude = currLocation.getLongitude();
            mLatitude = currLocation.getLatitude();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.location_btn){
            DialogWindowManager.show(this);
            pickLocation();
        }
        if(v.getId()==R.id.submit_btn){
            submitLocation();
        }
        if(v.getId()==R.id.photo1){
            loadImage(1);
        }
        if(v.getId()==R.id.photo2){
            loadImage(2);
        }
        if(v.getId()==R.id.photo3){
            loadImage(3);
        }
    }

    private void loadImage(int i) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), i);
    }

    private void submitLocation() {
        if(validateData()) {

            ParseUser user = ParseUser.getCurrentUser();
            String mail = user.getEmail();


            ParseGeoPoint point = new ParseGeoPoint(mLatitude, mLongitude);
            ParseObject location = new ParseObject(getString(R.string.db_pendinglocation_dbname));

            location.put(getString(R.string.db_pendinglocation_name), mNameField.getText().toString());
            location.put(getString(R.string.db_pendinglocation_city), mCityField.getText().toString());
            location.put(getString(R.string.db_pendinglocation_description), mDescField.getText().toString());
            location.put(getString(R.string.db_pendinglocation_location), point);
            location.put(getString(R.string.db_pendinglocation_user_email), mail);
            if (mPhoto1 != null) {
                location.put(getString(R.string.db_pendinglocation_photol), mPhoto1);
            }
            if (mPhoto2 != null) {
                location.put(getString(R.string.db_pendinglocation_photo2), mPhoto2);
            }
            if (mPhoto2 != null) {
                location.put(getString(R.string.db_pendinglocation_photo3), mPhoto3);
            }
            DialogWindowManager.show(this);
            location.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.d("Save", e.getMessage());
                    }
                    DialogWindowManager.dismiss();
                    finish();
                }
            });
        }
    }

    private boolean validateData() {

        if(mNameField.getText().toString().length()<3){
            Toast.makeText(this, getString(R.string.alert_location_name_short), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mCityField.getText().toString().length()<4){
            Toast.makeText(this, getString(R.string.alert_location_city_short), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mDescField.getText().toString().length()<15){
            Toast.makeText(this, getString(R.string.alert_location_desc_short), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mPhoto1==null && mPhoto2==null && mPhoto3==null){
            Toast.makeText(this, getString(R.string.alert_location_no_photo), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mLatitude==null || mLongitude==null){
            Toast.makeText(this, getString(R.string.alert_location_no_latlng), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

            Uri selectedImageUri = data.getData();

            if (requestCode == PICTURE_ONE) {

                if (null != selectedImageUri) {
                    mPhoto1= ParseUtilities.createParseFile(selectedImageUri,"photo.jpg");
                    mPhoto1Field.setImageURI(selectedImageUri);

                }
            }
            if (requestCode == PICTURE_TWO) {

                if (null != selectedImageUri) {
                    mPhoto2= ParseUtilities.createParseFile(selectedImageUri,"photo.jpg");
                    mPhoto2Field.setImageURI(selectedImageUri);
                }
            }
            if (requestCode == PICTURE_THREE) {

                if (null != selectedImageUri) {
                    mPhoto3= ParseUtilities.createParseFile(selectedImageUri,"photo.jpg");
                    mPhoto3Field.setImageURI(selectedImageUri);
                }
            }
            if (requestCode == PLACE_PICKER_REQUEST) {
                    Place place = PlacePicker.getPlace(getApplicationContext(),data);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
