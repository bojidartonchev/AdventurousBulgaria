package com.codeground.adventurousbulgaria.Utilities.ParseUtils;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Traveller")
public class ParseTraveller extends ParseObject {
    public ParseUser getOriginUser() {
        return getParseUser("origin_user");
    }

    public Date getTravellDate(){
        return getDate("travel_date");
    }

    public ParseGeoPoint getFromLocation()
    {
        return getParseGeoPoint("from_location");
    }

    public ParseObject getToLocation()
    {
        return getParseObject("to_location");
    }
}