package com.codeground.adventurousbulgaria.Utilities;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;


@ParseClassName("Comments")
public class ParseComment extends ParseObject {
    public String getCreator() {
        return getString("creator");
    }

    public String getContent() {
        return getString("content");
    }




}
