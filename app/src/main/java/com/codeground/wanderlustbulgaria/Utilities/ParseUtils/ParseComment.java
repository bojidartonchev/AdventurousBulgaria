package com.codeground.wanderlustbulgaria.Utilities.ParseUtils;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Comments")
public class ParseComment extends ParseObject {
    public ParseUser getCreator() {
        return getParseUser("creator");
    }

    public String getContent() {
        return getString("content");
    }




}
