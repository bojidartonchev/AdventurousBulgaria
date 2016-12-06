package com.codeground.adventurousbulgaria.Tasks;


import android.location.Location;
import android.os.AsyncTask;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.Utilities.Landmark;

import java.util.Collections;
import java.util.List;

public class CheckLocationTask extends AsyncTask<Location, Integer, Landmark> {
    private final int ALLOWED_DISTANCE_TO_COMPLETE = 1000; //meters

    private List<Landmark> mData;
    private MainApplication mApp;

    public CheckLocationTask(MainApplication app) {
        this.mApp = app;
    }

    @Override
    protected Landmark doInBackground(Location[] locations) {
        int count = locations.length;

        for (int i = 0; i < count; i++) {
            Location myLocation = locations[i];

            for (Landmark landmark : mData) {
                Location currentLandmarkLocation = new Location("");
                currentLandmarkLocation.setLatitude(landmark.getLatitude());
                currentLandmarkLocation.setLongitude(landmark.getLongitude());

                //Check the distance
                if(currentLandmarkLocation.distanceTo(myLocation) <= ALLOWED_DISTANCE_TO_COMPLETE){
                    return landmark;
                }

                if (isCancelled()){
                    break;
                }
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        this.mData = Collections.synchronizedList(Landmark.listAll(Landmark.class));

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Landmark aLandmark) {
        if(aLandmark!=null){
            mApp.visitLandmark(aLandmark);
        }

        super.onPostExecute(aLandmark);
    }
}
