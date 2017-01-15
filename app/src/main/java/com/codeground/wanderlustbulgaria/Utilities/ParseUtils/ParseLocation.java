package com.codeground.wanderlustbulgaria.Utilities.ParseUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import java.util.ArrayList;

@ParseClassName("Location")
public class ParseLocation extends ParseObject {
    public String getName() {
        return getString("name");
    }

    public String getDescription() {
        return getString("description");
    }

    public String getCity() {
        return getString("city");
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint("location");
    }

    public ParseFile getIcon(){
        return getParseFile("icon");
    }

    public ArrayList<ParseFile> getPhotos()
    {
        ArrayList<ParseFile> toReturn = new ArrayList<>();
        if(has("photo1")){
            toReturn.add(getParseFile("photo1"));
        }
        if(has("photo2")){
            toReturn.add(getParseFile("photo2"));
        }
        if(has("photo3")){
            toReturn.add(getParseFile("photo3"));
        }

        return toReturn;
    }

    public String getCategory() { return getString("category"); }

    public ParseRelation getComments() { return getRelation("comments"); }

}
