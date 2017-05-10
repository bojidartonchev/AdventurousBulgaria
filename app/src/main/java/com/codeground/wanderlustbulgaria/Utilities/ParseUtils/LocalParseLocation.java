package com.codeground.wanderlustbulgaria.Utilities.ParseUtils;

import android.location.Location;

import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.codeground.wanderlustbulgaria.Utilities.UpdateLocalDatabase;
import com.orm.SugarRecord;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LocalParseLocation extends SugarRecord {

    private double mLatitude;
    private double mLongitude;
    private String mCategory;
    private Date mUpdatedAt;
    private String mName;
    private String mDescription;
    private String mObjectId;

    public LocalParseLocation(){
    }

    public LocalParseLocation(ParseLocation parseLoc){
        updateLocation(parseLoc);
    }

    public Location getLocation() {
        Location loc = new Location("");
        loc.setLatitude(mLatitude);
        loc.setLongitude(mLongitude);
        return loc;
    }

    public Date getUpdatedAt() {
        return mUpdatedAt;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getObjectId() {
        return mObjectId;
    }

    public void updateLocation(ParseLocation parseLoc){
        this.mLatitude = parseLoc.getLocation().getLatitude();
        this.mLongitude = parseLoc.getLocation().getLongitude();

        this.mUpdatedAt = parseLoc.getUpdatedAt();
        this.mCategory = parseLoc.getCategory();
        this.mName = parseLoc.getName();
        this.mDescription = parseLoc.getDescription();
        this.mObjectId = parseLoc.getObjectId();
    }

    public static void updateDatabaseIfNeeded(){
        ParseQuery<ParseLocation> query = ParseQuery.getQuery(ParseLocation.class);
        query.selectKeys(Arrays.asList("location", "updatedAt", "category", "name", "description"));
        query.findInBackground(new FindCallback<ParseLocation>() {
            @Override
            public void done(List<ParseLocation> objects, ParseException e) {
                if(e==null) {
                    new UpdateLocalDatabase().execute(objects);
                }else{
                    NotificationsManager.showToast(e.getMessage(), TastyToast.ERROR);
                }

            }
        });
    }

    public static long getCategoryCount(String category){
        String[] vals = {category};
        return LocalParseLocation.count(LocalParseLocation.class, "m_category = ?", vals);
    }
}