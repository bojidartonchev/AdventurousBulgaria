package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.codeground.adventurousbulgaria.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


public class SubmitLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICTURE_ONE = 1;
    private static final int PICTURE_TWO = 2;
    private static final int PICTURE_THREE = 3;

    private EditText mNameField;
    private EditText mDescField;
    private EditText mCityField;
    private ImageView mPhoto1Field;
    private ImageView mPhoto2Field;
    private ImageView mPhoto3Field;
    private ParseFile mPhoto1;
    private ParseFile mPhoto2;
    private ParseFile mPhoto3;

    private LocationManager mLocationManager;

    private Button mSubmitBtn;

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
        mSubmitBtn = (Button) findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(this);
        mPhoto1Field.setOnClickListener(this);
        mPhoto2Field.setOnClickListener(this);
        mPhoto3Field.setOnClickListener(this);

        mPhoto1=null;
        mPhoto2=null;
        mPhoto3=null;


    }

    @Override
    public void onClick(View v) {
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
        ParseUser user = ParseUser.getCurrentUser();
        String mail = user.getEmail();
        Location currLocation = getLastKnownLocation();

        double longitude = currLocation.getLongitude();
        double latitude = currLocation.getLatitude();

        ParseGeoPoint point = new ParseGeoPoint(latitude,longitude);
        ParseObject location = new ParseObject(getString(R.string.db_pendinglocation_dbname));

        location.put(getString(R.string.db_pendinglocation_name), mNameField.getText().toString());
        location.put(getString(R.string.db_pendinglocation_city), mCityField.getText().toString());
        location.put(getString(R.string.db_pendinglocation_description), mDescField.getText().toString());
        location.put(getString(R.string.db_pendinglocation_location),point);
       location.put(getString(R.string.db_pendinglocation_user_email),mail);
       if(mPhoto1!=null) {
           location.put(getString(R.string.db_pendinglocation_photol), mPhoto1);
       }
       if(mPhoto2!=null) {
           location.put(getString(R.string.db_pendinglocation_photo2), mPhoto2);
       }
       if(mPhoto2!=null) {
           location.put(getString(R.string.db_pendinglocation_photo3),mPhoto3);
       }

        location.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    Log.d("Save",e.getMessage());
                }
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
            Uri selectedImageUri = data.getData();

            if (requestCode == PICTURE_ONE) {

                if (null != selectedImageUri) {
                    createFile(selectedImageUri,PICTURE_ONE);
                    mPhoto1Field.setImageURI(selectedImageUri);
                }
            }
            if (requestCode == PICTURE_TWO) {

                if (null != selectedImageUri) {
                    createFile(selectedImageUri,PICTURE_TWO);
                    mPhoto2Field.setImageURI(selectedImageUri);
                }
            }
            if (requestCode == PICTURE_THREE) {

                if (null != selectedImageUri) {
                    createFile(selectedImageUri,PICTURE_THREE);
                    mPhoto3Field.setImageURI(selectedImageUri);
                }
            }
        }
    }






    void createFile(Uri selectedImage,int id){
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            if(id==1){
                mPhoto1 = new ParseFile("mphoto.png",image);
                return;
            }
            if(id==2){
                mPhoto2 = new ParseFile("mphoto.png",image);
            }
            if(id==3){
                mPhoto3 = new ParseFile("mphoto.png",image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
