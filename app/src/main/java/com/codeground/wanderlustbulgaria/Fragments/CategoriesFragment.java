package com.codeground.wanderlustbulgaria.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeground.wanderlustbulgaria.Activities.AllLandmarksActivity;
import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.CategoriesAdapter;
import com.codeground.wanderlustbulgaria.Utilities.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment implements IOnItemClicked {
    private List<Category> categoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoriesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categories, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        mAdapter = new CategoriesAdapter(categoryList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareCategoriesData();
        return v;
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment f = new CategoriesFragment();

        return f;
    }

    private void prepareCategoriesData() {
        Category cat = new Category(getString(R.string.cat_mountain), 22, R.drawable.mountain);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_mountain), 22, R.drawable.mountain);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_mountain), 22, R.drawable.mountain);
        categoryList.add(cat);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int pos) {
        Category selected = categoryList.get(pos);

        Intent i = new Intent(getActivity(), AllLandmarksActivity.class);
        i.putExtra("Category",selected.getIntentId());

        startActivity(i);
    }
}