package com.codeground.wanderlustbulgaria.Utilities;


import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class AllLocationsManager {

    private static AllLocationsManager _instance;

    private List<ParseLocation> mLocations;
    private boolean isLoaded = false;

    private AllLocationsManager(){

    }

    public static AllLocationsManager getInstance(){
        if(_instance==null){
            _instance = new AllLocationsManager();
        }
        return _instance;
    }

    public void loadLocations(){
        ParseQuery<ParseLocation> query = ParseQuery.getQuery(ParseLocation.class);
        query.findInBackground(new FindCallback<ParseLocation>() {
            @Override
            public void done(List<ParseLocation> results, ParseException e) {
                if(e==null){
                    mLocations = results;
                    isLoaded = true;
                }
            }
        });
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public List<ParseLocation> getLocations() {
        return mLocations;
    }
}
