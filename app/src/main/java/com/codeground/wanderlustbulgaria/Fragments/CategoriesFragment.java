package com.codeground.wanderlustbulgaria.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeground.wanderlustbulgaria.R;


public class CategoriesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categories, container, false);

        return v;
    }

    public static CategoriesFragment newInstance() {

        CategoriesFragment f = new CategoriesFragment();

        return f;
    }
}