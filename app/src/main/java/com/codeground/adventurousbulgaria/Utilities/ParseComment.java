package com.codeground.adventurousbulgaria.Utilities;


import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Comments")
public class ParseComment extends ParseObject {
    public String getCreator() {
        return getString("creator");
    }

    public String getContent() {
        return getString("content");
    }




}
