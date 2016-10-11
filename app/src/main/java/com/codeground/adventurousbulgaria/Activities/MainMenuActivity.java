package com.codeground.adventurousbulgaria.Activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.Interfaces.IOnLocationChange;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Services.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, IOnLocationChange,OnMapReadyCallback {

    private static final int INITIAL_REQUEST=1337;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private Button mProfileBtn;
    private Button mAllLandmarksBtn;

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mProfileBtn=(Button) findViewById(R.id.profile_btn);
        mProfileBtn.setOnClickListener(this);

        mAllLandmarksBtn=(Button) findViewById(R.id.landmarks_btn);
        mAllLandmarksBtn.setOnClickListener(this);

        startLocationService();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
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

    private void startLocationService(){
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
            ComponentName myService = startService(new Intent(this, LocationService.class));
            bindService(new Intent(this, LocationService.class), conn, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case INITIAL_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)&& (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    ComponentName myService = startService(new Intent(this, LocationService.class));
                    bindService(new Intent(this, LocationService.class), conn, BIND_AUTO_CREATE);
                }
                else{
                    Toast.makeText(this,"Sorry, you must allow access to GPS to use this app",Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService currentService = ((LocationService.LocationServiceBinder)service).getService();
            currentService.setCallback(MainMenuActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void OnLocationChange(Location loc) {
        //Set our current location
        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("My location")
                .snippet("You are here");
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();

        Marker markerFinal = mGoogleMap.addMarker(markerOptions);
        markerFinal.showInfoWindow();//the marker comes with balloon already open
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mGoogleMap.setMyLocationEnabled(true);
        }
    }
}
