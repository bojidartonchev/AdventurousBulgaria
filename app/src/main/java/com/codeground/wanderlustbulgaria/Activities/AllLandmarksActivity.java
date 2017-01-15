package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.LandmarksAdapter;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
public class AllLandmarksActivity extends AppCompatActivity implements IOnItemClicked {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ParseLocation> mData;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_landmarks);

        mRecyclerView = (RecyclerView)findViewById(R.id.all_landmarks_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        boolean allCompletedOnly = getIntent().getBooleanExtra("allCompletedOnly", false);

        if(allCompletedOnly){
            ParseQuery<ParseObject> query = ParseUser.getCurrentUser().getRelation("visited_locations").getQuery();
            DialogWindowManager.show(this);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> results, ParseException e) {
                    //Hack... refactor later!!!
                    List<ParseLocation> tempLocationsList = (List<ParseLocation>)(List<?>) results;
                    mAdapter = new LandmarksAdapter(tempLocationsList, AllLandmarksActivity.this,getApplicationContext());
                    mRecyclerView.setAdapter(mAdapter);
                    DialogWindowManager.dismiss();
                }
            });
        }
        else{
            mCategory = getIntent().getStringExtra("Category");
            ParseQuery<ParseLocation> query = ParseQuery.getQuery(ParseLocation.class);
            query.whereEqualTo("category",mCategory);
            DialogWindowManager.show(this);
            query.findInBackground(new FindCallback<ParseLocation>() {
                @Override
                public void done(List<ParseLocation> results, ParseException e) {
                    mData = results;
                    mAdapter = new LandmarksAdapter(results, AllLandmarksActivity.this,getApplicationContext());
                    mRecyclerView.setAdapter(mAdapter);
                    DialogWindowManager.dismiss();
                }
            });
        }
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
