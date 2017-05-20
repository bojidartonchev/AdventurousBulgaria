package com.codeground.wanderlustbulgaria.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sdsmdg.tastytoast.TastyToast;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class SubmitTravellerActivity extends AppCompatActivity implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private final int PLACE_PICKER_REQUEST = 1;

    private LocationManager mLocationManager;
    private Double mLongitude;
    private Double mLatitude;
    private String mCity;
    private Date mDepartureDate;
    private ArrayAdapter<String> autoCompleteAdapter = null;

    private Button mSubmitBtn;
    private ImageView mToLocationImage;
    private ImageView mFromLocationImage;
    private ImageView mDateImage;
    private ImageView mTimeImage;
    private TextView mStartLocationLabel;
    private TextView mEndLocationLabel;
    private TextView mDateTextField;
    private TextView mTimeTextField;

    private ParseLocation mTargetLocation;

    private TreeMap<Integer, Boolean> blinkingMap = new TreeMap<>();
    private ImageView currentBlinkingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_submit_traveller);

        mToLocationImage = (ImageView)findViewById(R.id.to_image);
        mToLocationImage.setOnClickListener(this);
        mFromLocationImage = (ImageView)findViewById(R.id.from_image);
        mFromLocationImage.setOnClickListener(this);

        mDateImage = (ImageView)findViewById(R.id.date_image);
        mDateImage.setOnClickListener(this);
        mTimeImage = (ImageView)findViewById(R.id.time_image);
        mTimeImage.setOnClickListener(this);

        mSubmitBtn = (Button) findViewById(R.id.travellers_submit_btn);

        mSubmitBtn.setOnClickListener(this);

        initAutoComplete();

        mStartLocationLabel = (TextView) findViewById(R.id.start_loc_label);
        mEndLocationLabel = (TextView) findViewById(R.id.end_loc_label);

        mDateTextField = (TextView) findViewById(R.id.date_label);
        mTimeTextField = (TextView) findViewById(R.id.time_label);

        initLocation();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_plan_title);

        initBlinkingStack();
    }

    private void initBlinkingStack() {
        blinkingMap.put(mTimeImage.getId(), false);
        blinkingMap.put(mDateImage.getId(), false);
        blinkingMap.put(mToLocationImage.getId(), false);
        blinkingMap.put(mFromLocationImage.getId(), false);

        currentBlinkingView = mFromLocationImage;
        setBlinking(currentBlinkingView);
    }

    private void proceedBlinking(){
        if(blinkingMap.get(currentBlinkingView.getId())){
            switch (currentBlinkingView.getId()){
                case R.id.from_image:
                    currentBlinkingView = mToLocationImage;
                    setBlinking(currentBlinkingView);
                    proceedBlinking();
                    return;
                case R.id.to_image:
                    currentBlinkingView = mDateImage;
                    setBlinking(currentBlinkingView);
                    proceedBlinking();
                    return;
                case R.id.date_image:
                    currentBlinkingView = mTimeImage;
                    setBlinking(currentBlinkingView);
                    proceedBlinking();
                    return;
                case R.id.time_image:
                    mSubmitBtn.setEnabled(true);
                    mSubmitBtn.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.main_color_green));
                    return;
            }
        }
    }

    private void initAutoComplete() {
        ParseQuery<ParseLocation> locationQuery = ParseQuery.getQuery(ParseLocation.class);
        locationQuery.findInBackground(new FindCallback<ParseLocation>() {
            public void done(List<ParseLocation> parseLocations, ParseException e) {
                if (e == null) {
                    ParseLocation[] data = parseLocations.toArray(new ParseLocation[parseLocations.size()]);
                    String[] strings = new String[data.length];
                    for (int i = 0; i < data.length; i++) {
                        strings[i] = data[i].getName();
                    }
                    // Test to see if it was correctly printing out the array I wanted.
                    // System.out.println(Arrays.toString(strings));
                    autoCompleteAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.search_result_traveller_target_location_row, strings);
                } else {
                    Log.d("users", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void initLocation() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Location currLocation = getLastKnownLocation();

                if(currLocation != null){
                    mLongitude =  currLocation.getLongitude();
                    mLatitude =  currLocation.getLatitude();
                    updateCity(true);
                }
            }
        });
    }

    private void updateCity(final boolean isAutolocated) {
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

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(mStartLocationLabel!=null){
                        String cityText = mCity + (isAutolocated ? "\n(Current location)" : String.format(Locale.ENGLISH, "\n(%.2f, %.2f)", mLatitude, mLongitude));
                        mStartLocationLabel.setText(cityText);
                        setCompleted(mFromLocationImage);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.to_image){
            if(autoCompleteAdapter != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(getString(R.string.submit_plan_to_location_dialog_title));
                View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_to_location, (ViewGroup) v.getParent(),false);
                final AutoCompleteTextView input = (AutoCompleteTextView) viewInflated.findViewById(R.id.input_to_location);

                if(autoCompleteAdapter.getCount() < 40){
                    input.setThreshold(1);
                }
                else {
                    input.setThreshold(2);
                }

                input.setAdapter(autoCompleteAdapter);
                input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });

                builder.setView(viewInflated);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String locationName = input.getText().toString();
                        ParseQuery<ParseLocation> currentLocation = ParseQuery.getQuery(ParseLocation.class);
                        currentLocation.whereEqualTo("name", locationName);
                        currentLocation.getFirstInBackground(new GetCallback<ParseLocation>() {
                            @Override
                            public void done(ParseLocation currentLocationObj, ParseException e) {
                                if(e==null && currentLocationObj != null){
                                    mTargetLocation = currentLocationObj;
                                    mEndLocationLabel.setText(currentLocationObj.getName().replaceAll(" ", "\n"));
                                    setCompleted(mToLocationImage);
                                }else{
                                    NotificationsManager.showToast(String.format("Invalid location: %s", locationName), TastyToast.ERROR);
                                }
                            }
                        });

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }else{
                NotificationsManager.showToast("Error while loading target list!", TastyToast.ERROR);
            }
        }

        if(v.getId()==R.id.from_image){
            DialogWindowManager.show(this);
            pickLocation();
        }
        if(v.getId()==R.id.travellers_submit_btn){
            submitTraveller();
        }

        if(v.getId()== R.id.date_image){
            showDatePicker();
        }

        if(v.getId()== R.id.time_image){
            showTimePicker();
        }
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.vibrate(false);
        dpd.show(getFragmentManager(), "Pick a date:");
    }

    private void showTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), true
        );
        tpd.vibrate(false);
        tpd.show(getFragmentManager(), "Pick a time:");
    }

    private void submitTraveller() {
        ParseUser user = ParseUser.getCurrentUser();

        final ParseObject traveller = new ParseObject(getString(R.string.db_traveller_dbname));

        traveller.put("origin_user", user);
        traveller.put("origin_user_name", user.getString("first_name") + " " + user.getString("last_name"));

        if(mLatitude !=null && mLongitude != null){
            ParseGeoPoint point = new ParseGeoPoint(mLatitude,mLongitude);
            traveller.put("from_location", point);
        }else if(mCity == null || mCity.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please choose a departure location or city", Toast.LENGTH_SHORT).show();
            return;
        }

        traveller.put("from_city", mCity);

        if(mTargetLocation!=null){
            traveller.put("to_location", mTargetLocation);
            traveller.put("to_location_name", mTargetLocation.getName());
        }else{
            Toast.makeText(getApplicationContext(), "Invalid location", Toast.LENGTH_SHORT).show();
        }

        if(mDepartureDate != null){
            traveller.put("travel_date", mDepartureDate);
        }else{
            Toast.makeText(getApplicationContext(), "Please set date and time for your travel!", Toast.LENGTH_SHORT).show();
        }

        DialogWindowManager.show(this);
        traveller.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    NotificationsManager.showToast(e.getMessage(), TastyToast.ERROR);
                }

                DialogWindowManager.dismiss();
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
                updateCity(false);
                DialogWindowManager.dismiss();

                setCompleted(mFromLocationImage);
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
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        if(mDepartureDate!=null) {
            cal.setTime(mDepartureDate);
        }
        cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
        cal.set(Calendar.MINUTE,minute);
        cal.set(Calendar.SECOND,second);

        mDepartureDate = cal.getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        mTimeTextField.setText(dateFormatter.format(mDepartureDate));

        setCompleted(mTimeImage);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        if(mDepartureDate!=null) {
            newDate.setTime(mDepartureDate);
        }

        newDate.set(year, monthOfYear, dayOfMonth);
        mDepartureDate = newDate.getTime();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        mDateTextField.setText(dateFormatter.format(mDepartureDate));

        setCompleted(mDateImage);
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

    private void setBlinking(View v){
        if(v!=null && blinkingMap.get(v.getId()) == false){
            final Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(1000);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            v.startAnimation(animation);
        }
    }

    private void setCompleted(ImageView v){
        if(v!=null){
            v.setAnimation(null);
            v.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.main_color_green));
            if(v.getId() == R.id.from_image || v.getId() == R.id.to_image){
                v.setImageResource(R.drawable.tick);
            }
            blinkingMap.put(v.getId(), true);
            proceedBlinking();
        }
    }
}
