package com.codeground.adventurousbulgaria.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.BroadcastReceivers.BootReceiver;
import com.codeground.adventurousbulgaria.Fragments.ProfileFragment;
import com.codeground.adventurousbulgaria.Interfaces.IOnDataBaseInitialized;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Landmark;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,IOnDataBaseInitialized {

    private static final int INITIAL_REQUEST = 1337;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Button mProfileBtn;
    private Button mAllLandmarksBtn;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private TextView mPersonName;
    private NavigationView mProfileView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);

        checkForPermissions();

        //Create profile fragment
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().
                beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
        //mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        mProfileView = (NavigationView) findViewById(R.id.profile_view);
        mPersonName = (TextView) mProfileView.getHeaderView(0).findViewById(R.id.profile_name);
        String currUserName = ParseUser.getCurrentUser().get("first_name").toString()+" "+ParseUser.getCurrentUser().get("last_name").toString();
        mPersonName.setText(currUserName);
        mProfileView.setNavigationItemSelectedListener(this);
        //mPersonName.setText(currUserName);





        mProfileBtn = (Button) findViewById(R.id.profile_btn);
        mProfileBtn.setOnClickListener(this);

        mAllLandmarksBtn = (Button) findViewById(R.id.landmarks_btn);
        mAllLandmarksBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        //if (v.getId() == R.id.profile_btn) {
        //    Intent intent = new Intent(this, UserHomeActivity.class);
        //    startActivity(intent);
        //}
        if (v.getId() == R.id.landmarks_btn) {
            Intent intent = new Intent(this, AllLandmarksActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case INITIAL_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    onConnected(null);
                    //startLocationService();
                    BootReceiver.scheduleAlarm(this);
                    if(mMap !=null){
                        //noinspection MissingPermission you kidding me?
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Sorry, you must allow access to GPS to use this app", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapLoaded() {
        if (mLastLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 8));
        }
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                INITIAL_PERMS[0])
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        INITIAL_PERMS[1])
                        != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    INITIAL_PERMS,
                    INITIAL_REQUEST);

        }else{
            BootReceiver.scheduleAlarm(this);
        }
    }

    @Override
    public void OnDBInit() {
        List<Landmark> mData = Landmark.listAll(Landmark.class);

        if(mMap == null){
            return;
        }

        for (Landmark landmark : mData) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(landmark.getLatitude(), landmark.getLongitude()))
                    .title(landmark.getName()));
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
        }

        return false;
    }

    private void logout() {
        ParseUser.getCurrentUser().logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }
}