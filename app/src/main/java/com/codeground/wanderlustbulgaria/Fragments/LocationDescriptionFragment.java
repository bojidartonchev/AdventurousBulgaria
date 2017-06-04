package com.codeground.wanderlustbulgaria.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;


public class LocationDescriptionFragment extends Fragment {

    private TextView mDescription;
    private TextView mCity;
    private ProgressBar mDetailsProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_description, container, false);

        mDescription = (TextView) v.findViewById(R.id.location_description);
        mCity = (TextView) v.findViewById(R.id.location_city);
        mDetailsProgress = (ProgressBar) v.findViewById(R.id.details_progress);

        return v;
    }

    public static LocationDescriptionFragment newInstance() {

        LocationDescriptionFragment f = new LocationDescriptionFragment();

        return f;
    }

    public void setDescription(String text){
        mDescription.setVisibility(View.VISIBLE);
        mDescription.setText(text);
        mDetailsProgress.setVisibility(View.GONE);
    }
    public void setCity(String text){
        mCity.setText(text);
    }
}