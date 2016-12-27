package com.codeground.adventurousbulgaria.Utilities.ParseUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Activity")
public class ParseActivity extends ParseObject {
    public ParseUser getOriginUser() {
        return getParseUser("origin_user");
    }

    public String getType() {
        return getString("type");
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public Date getCreatedDate(){
        return getCreatedAt();
    }
}