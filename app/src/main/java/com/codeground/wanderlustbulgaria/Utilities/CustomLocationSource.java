package com.codeground.wanderlustbulgaria.Utilities;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.google.android.gms.maps.LocationSource;

@SuppressWarnings("MissingPermission")
public class CustomLocationSource implements LocationSource, LocationListener {

    private OnLocationChangedListener mListener;
    private OnLocationChangedListener mActivityListener;
    private LocationManager mLocationManager;

    public CustomLocationSource(LocationManager locationManager, OnLocationChangedListener activityListener){
        mLocationManager = locationManager;
        mActivityListener = activityListener;
    }

    @Override
    public void activate(OnLocationChangedListener listener){
        this.mListener = listener;
        LocationProvider gpsProvider = mLocationManager.getProvider(LocationManager.GPS_PROVIDER);
        if(gpsProvider != null) {
            mLocationManager.requestLocationUpdates(gpsProvider.getName(), 500, 5, this);
        }

        LocationProvider networkProvider = mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER);
        if(networkProvider != null) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 3, 0, this);
        }
    }

    @Override
    public void deactivate(){
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location){
         /* Push location updates to the registered listener..
            (this ensures that my-location layer will set the blue dot at the new/received location) */
        if(mListener != null && mActivityListener!=null && location != null){
            mListener.onLocationChanged(location);
            mActivityListener.onLocationChanged(location);
        }
    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub

    }
}
