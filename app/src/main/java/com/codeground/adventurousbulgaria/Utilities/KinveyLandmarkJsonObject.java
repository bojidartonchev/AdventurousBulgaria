package com.codeground.adventurousbulgaria.Utilities;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class KinveyLandmarkJsonObject extends GenericJson {
    @Key("_id")
    private String id;

    @Key("Name")
    private String name;

    @Key("Description")
    private String description;

    @Key("Location")
    private String locationName;

    @Key("Latitude")
    private double latitude;

    @Key("Longitude")
    private double longitude;

    @Key("IconURL")
    private String iconUrl;

    @Key("Photo")
    private String photoUrl;

    public KinveyLandmarkJsonObject() {
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIconURL() {
        return iconUrl;
    }

    public String getPhotoURL() {
        return photoUrl;
    }
}