package com.codeground.wanderlustbulgaria.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Activities.LandmarkActivity;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.MarkerInfoWindowAdapter;
import com.codeground.wanderlustbulgaria.Utilities.AllLocationsManager;
import com.codeground.wanderlustbulgaria.Utilities.CustomLocationSource;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class NearByFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnInfoWindowClickListener,
        LocationSource.OnLocationChangedListener {

    private final int INITIAL_SEEK_BAR_PROGRESS = 10;

    private CustomLocationSource mLocationSource;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private SeekBar mDistanceSeekBar;
    private Circle mCircleRadius;
    private TextView mRadiusText;

    private ArrayList<Marker> mMarkers;

    boolean isZoomedIn = false;
    private LocationManager mLocationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_near_by, container, false);

        mLocationManager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        mLocationSource = new CustomLocationSource(mLocationManager, this);

        mDistanceSeekBar = (SeekBar) v.findViewById(R.id.range_seek_bar);
        mDistanceSeekBar.setOnSeekBarChangeListener(this);

        mDistanceSeekBar.setProgress(INITIAL_SEEK_BAR_PROGRESS);
        mMarkers = new ArrayList<>();
        mRadiusText = (TextView) v.findViewById(R.id.range_radius_label);

        return v;
    }

    public static NearByFragment newInstance() {

        NearByFragment f = new NearByFragment();

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkGPSAvailability();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationSource.deactivate();
    }

    private void connectGoogleApiClient() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onStop() {
        if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(getActivity()));
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setLocationSource(mLocationSource);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mCircleRadius != null){
            int radius = progress * 1000;
            updateMarkers(radius);
            mCircleRadius.setRadius(radius);
            mRadiusText.setText(String.format(getString(R.string.distance_label_text), progress));
        }
    }

    private void updateMarkers(int radius) {
        if (mMarkers != null) {
            for (Marker marker : mMarkers) {
                LatLng poss = marker.getPosition();
                Location loc = new Location("");
                loc.setLatitude(poss.latitude);
                loc.setLongitude(poss.longitude);
                double distance = mLastLocation.distanceTo(loc);
                marker.setVisible(distance <= radius);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void loadMarkersOnMap(int radius){
        if(AllLocationsManager.getInstance().isLoaded()){
            List<ParseLocation> locations = AllLocationsManager.getInstance().getLocations();

            for (ParseLocation location : locations) {
                Location loc = new Location("");
                loc.setLatitude(location.getLocation().getLatitude());
                loc.setLongitude(location.getLocation().getLongitude());
                double distance = mLastLocation.distanceTo(loc);

                if(mMap != null){
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .visible(distance <= radius)
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .title(location.getName())
                            .snippet(location.getDescription()));
                    marker.setTag(location.getObjectId());

                    mMarkers.add(marker);
                }

            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String id = (String) marker.getTag();

        Intent intent = new Intent(getActivity(), LandmarkActivity.class);
        intent.putExtra("locationId", id);
        startActivity(intent);
    }

    private void checkGPSAvailability(){
        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else{
            connectGoogleApiClient();
        }
    }

    private void buildAlertMessageNoGps() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View activateGPSDialogView = factory.inflate(R.layout.custom_dialog, null);
        final AlertDialog activateGPS = new AlertDialog.Builder(getActivity()).create();

        activateGPS.setView(activateGPSDialogView);
        TextView textField = (TextView)activateGPSDialogView.findViewById(R.id.text_dialog);
        textField.setText(getString(R.string.activate_gps_prompt_text));

        activateGPSDialogView.findViewById(R.id.btn_dialog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prompt user to activate gps...
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                activateGPS.dismiss();
            }
        });
        activateGPSDialogView.findViewById(R.id.btn_dialog_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateGPS.dismiss();
                //TODO Force Slide to tab 2
            }
        });

        activateGPS.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (mLastLocation != null) {
            if(!isZoomedIn){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 8));
                isZoomedIn = true;
            }

            //update circle's radius
            if(mCircleRadius == null){
                mCircleRadius = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                        .radius(1000 * INITIAL_SEEK_BAR_PROGRESS)
                        .strokeColor(R.color.menuColor2)
                        .fillColor(R.color.radius_fill_color));

                mRadiusText.setText(String.format(getString(R.string.distance_label_text), INITIAL_SEEK_BAR_PROGRESS));

                loadMarkersOnMap(1000 * INITIAL_SEEK_BAR_PROGRESS);
            }else{
                mCircleRadius.setCenter(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                updateMarkers((int) mCircleRadius.getRadius());
            }
        }
    }

    @Override
    public void onMapLoaded() {
        if(mLastLocation!=null){
            this.mLocationSource.onLocationChanged(mLastLocation);
        }
    }
}