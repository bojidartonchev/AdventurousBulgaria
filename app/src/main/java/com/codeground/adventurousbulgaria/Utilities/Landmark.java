package com.codeground.adventurousbulgaria.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

public class Landmark extends SugarRecord implements Parcelable {
    private String name;
    private String description;
    private String locationName;
    private Location location;
    private String iconURL;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setLocation(double latitude, double longitude) {
        this.location = new Location(latitude, longitude);
    }

    protected Landmark(Parcel in) {
        name = in.readString();
        description = in.readString();
        locationName = in.readString();
        location = (Location) in.readValue(Location.class.getClassLoader());
        iconURL = in.readString();
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
        dest.writeValue(location);
        dest.writeString(iconURL);
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