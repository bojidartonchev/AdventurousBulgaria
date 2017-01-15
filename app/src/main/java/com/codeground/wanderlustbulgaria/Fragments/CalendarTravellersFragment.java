package com.codeground.wanderlustbulgaria.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.CalendarTravellersAdapter;

public class CalendarTravellersFragment extends Fragment {

    private ListView mTravellers;
    private CalendarTravellersAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar_travellers, container, false);
        mTravellers = (ListView) v.findViewById(R.id.travellers_list);
        return v;
    }

    public void setTravellersAdapter(CalendarTravellersAdapter adapter){
        this.mAdapter = adapter;
        mAdapter.setTextKey("title");
        mTravellers.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}