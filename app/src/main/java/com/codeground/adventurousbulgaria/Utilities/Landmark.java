package com.codeground.adventurousbulgaria.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class Landmark extends SugarRecord implements Parcelable {
    private String kinveyId;
    private String name;
    private String description;
    private String locationName;
    private String iconURL;
    private double latitude;
    private double longitude;

    public Landmark() {

    }

    public Landmark(String name, String description, String locationName, double latitude, double longitude, String iconURL) {
        this.setName(name);
        this.setDescription(description);
        this.setLocationName(locationName);
        this.setLocation(latitude,longitude);
        this.setIconURL(iconURL);
    }

    public Landmark(KinveyLandmarkJsonObject template) {
        this.setName(template.getName());
        this.setDescription(template.getDescription());
        this.setLocationName(template.getLocationName());
        this.setLocation(template.getLatitude(),template.getLongitude());
        this.setIconURL(template.getIconURL());
        this.setKinveyId(template.getId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getKinveyId() {
        return kinveyId;
    }

    public void setKinveyId(String kinveyId) {
        this.kinveyId = kinveyId;
    }

    protected Landmark(Parcel in) {
        name = in.readString();
        description = in.readString();
        locationName = in.readString();
        iconURL = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        kinveyId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(locationName);
        dest.writeString(iconURL);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(kinveyId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Landmark> CREATOR = new Parcelable.Creator<Landmark>() {
        @Override
        public Landmark createFromParcel(Parcel in) {
            return new Landmark(in);
        }

        @Override
        public Landmark[] newArray(int size) {
            return new Landmark[size];
        }
    };

}