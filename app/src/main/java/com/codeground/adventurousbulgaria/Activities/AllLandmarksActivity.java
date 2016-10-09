package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codeground.adventurousbulgaria.Interfaces.IOnItemClicked;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Landmark;
import com.codeground.adventurousbulgaria.Utilities.LandmarksAdapter;

import java.util.List;


public class AllLandmarksActivity extends AppCompatActivity implements IOnItemClicked {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Landmark> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_landmarks);

        mRecyclerView = (RecyclerView)findViewById(R.id.all_landmarks_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Get all Landmarks from the localDB;
        mData = Landmark.listAll(Landmark.class);
        mAdapter = new LandmarksAdapter(mData, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClicked(int pos) {
        if(mData!=null){
            Intent intent = new Intent(getApplicationContext(), LandmarkActivity.class);
            intent.putExtra("landmark", mData.get(pos));
            startActivity(intent);
        }
    }
}
