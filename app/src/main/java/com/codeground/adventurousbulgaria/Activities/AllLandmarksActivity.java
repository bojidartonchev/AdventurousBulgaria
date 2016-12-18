package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codeground.adventurousbulgaria.Interfaces.IOnItemClicked;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.LandmarksAdapter;
import com.codeground.adventurousbulgaria.Utilities.ParseLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;
public class AllLandmarksActivity extends AppCompatActivity implements IOnItemClicked {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseLocation> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_landmarks);

        mRecyclerView = (RecyclerView)findViewById(R.id.all_landmarks_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ParseQuery<ParseLocation> query = ParseQuery.getQuery(ParseLocation.class);

        query.findInBackground(new FindCallback<ParseLocation>() {
            @Override
            public void done(List<ParseLocation> results, ParseException e) {
                mData = results;
                mAdapter = new LandmarksAdapter(results, AllLandmarksActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onItemClicked(int pos) {
        if(mData!=null){
            Intent intent = new Intent(getApplicationContext(), LandmarkActivity.class);
            ParseLocation currentLocation = mData.get(pos);
            if(currentLocation!=null){
                intent.putExtra("locationId", currentLocation.getObjectId());

                startActivity(intent);
            }

        }
    }
}
