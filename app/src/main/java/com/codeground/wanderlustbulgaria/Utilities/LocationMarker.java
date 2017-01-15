package com.codeground.wanderlustbulgaria.Utilities;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.parse.ParseFile;

public class LocationMarker implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final ParseFile mIcon;

    public LocationMarker(double lat, double lng, String title, ParseFile icon) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mIcon = icon;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public ParseFile getIcon() {
        return mIcon;
    }
}
