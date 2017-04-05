package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.LandmarksAdapter;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;
public class AllLandmarksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ParseQueryAdapter.OnQueryLoadListener {

    private ListView mLocations;

    private String mCategory;
    private ParseQueryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_landmarks);

        mLocations = (ListView) findViewById(R.id.all_landmarks_list_view) ;

        mCategory = getIntent().getStringExtra("Category");
        mAdapter = new LandmarksAdapter(this, mCategory);
        mAdapter.setTextKey("title");
        mAdapter.setImageKey("photo");
        mAdapter.addOnQueryLoadListener(this);
        mLocations.setOnItemClickListener(this);
        mLocations.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mCategory);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getApplicationContext(), LandmarkActivity.class);

        ParseObject entry = (ParseObject) parent.getItemAtPosition(position);
        if(entry!=null){
            intent.putExtra("locationId", entry.getObjectId());
            startActivity(intent);
        }

    }

    @Override
    public void onLoading() {
        DialogWindowManager.show(this);
    }

    @Override
    public void onLoaded(List objects, Exception e) {
        DialogWindowManager.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
