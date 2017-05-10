package com.codeground.wanderlustbulgaria.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeground.wanderlustbulgaria.Activities.AllLandmarksActivity;
import com.codeground.wanderlustbulgaria.Activities.NearByActivity;
import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.Interfaces.IOnLocalDatabaseUpdated;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.CategoriesAdapter;
import com.codeground.wanderlustbulgaria.Utilities.Category;
import com.codeground.wanderlustbulgaria.Utilities.UpdateLocalDatabase;

import java.util.ArrayList;
import java.util.List;


public class CategoriesFragment extends Fragment implements IOnItemClicked, IOnLocalDatabaseUpdated {
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

        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        loadCategories();

        return v;
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment f = new CategoriesFragment();

        return f;
    }

    @Override
    public void onItemClicked(int pos) {
        Category selected = categoryList.get(pos);
        Category.Type selectedType = selected.getType();

        Intent i = new Intent(getActivity(), AllLandmarksActivity.class);

        switch (selectedType){
            case SPECIFIED:
                i.putExtra("Category",selected.getName());
                break;
            case FROM_USERS:
                break;
            case NEARBY:
                i = new Intent(getActivity(), NearByActivity.class);
                break;
        }

        startActivity(i);
    }

    private void loadCategories()
    {
        Category cat = new Category(getString(R.string.near_by_button), R.drawable.ic_location_marker, Category.Type.NEARBY);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_mountain), R.drawable.mountain, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_lakes), R.drawable.lake, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_cycling), R.drawable.bike, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_ecopath), R.drawable.path, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_food), R.drawable.gurme, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_fishing), R.drawable.fishing, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_beach), R.drawable.beach, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_waterfall), R.drawable.waterfall, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_caves), R.drawable.cave, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_fortress), R.drawable.fortress, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_festival), R.drawable.festival, Category.Type.SPECIFIED);
        categoryList.add(cat);

        cat = new Category(getString(R.string.cat_camping), R.drawable.camp, Category.Type.SPECIFIED);
        categoryList.add(cat);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLocalDatabaseUpdateCompleted() {
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpdateLocalDatabase.registerForNotification(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UpdateLocalDatabase.unregisterForNotification(this);
    }
}