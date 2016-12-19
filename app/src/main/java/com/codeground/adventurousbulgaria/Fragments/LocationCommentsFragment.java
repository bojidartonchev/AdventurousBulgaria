package com.codeground.adventurousbulgaria.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.LocationCommentsAdapter;


public class LocationCommentsFragment extends Fragment {

    ListView mComments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_comments, container, false);

        mComments = (ListView) v.findViewById(R.id.location_comments);


        return v;
    }

    public static LocationCommentsFragment newInstance() {

        LocationCommentsFragment f = new LocationCommentsFragment();


        return f;
    }
    public void setCommentsAdapter(LocationCommentsAdapter adapter){
        mComments.setAdapter(adapter);
    }


}