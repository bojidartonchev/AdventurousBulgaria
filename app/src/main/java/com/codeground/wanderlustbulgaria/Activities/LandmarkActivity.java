package com.codeground.wanderlustbulgaria.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Fragments.LocationCommentsFragment;
import com.codeground.wanderlustbulgaria.Fragments.LocationDescriptionFragment;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.ImagesAdapter;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.LocationCommentsAdapter;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.LocationPagerAdapter;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class LandmarkActivity extends AppCompatActivity{
    private TextView mTitle;
    private TextView mLocation;
    private TextView mCity;
    private ParseLocation mCurrLocation;
    private ViewPager mPager;
    private ViewPager mPagerImages;
    private LocationPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private LocationDescriptionFragment mDesc;
    private LocationCommentsFragment mComments;

    private ArrayList<ParseFile> mImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mTitle = (TextView) findViewById(R.id.landmark_title);
        mCity = (TextView) findViewById(R.id.landmark_location);
        mPagerImages = (ViewPager) findViewById(R.id.images_pager);

        final String locId = getIntent().getStringExtra("locationId");
        mPagerAdapter = new LocationPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mPager);
        LocationPagerAdapter curr = (LocationPagerAdapter) mPager.getAdapter();
        mDesc =(LocationDescriptionFragment) curr.instantiateItem(mPager, 0);
        mComments =(LocationCommentsFragment) mPager.getAdapter().instantiateItem(mPager, 1);
        TabLayout dots = (TabLayout) findViewById(R.id.tabDots);
        dots.setupWithViewPager(mPagerImages, true);

        mImages = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.db_location_dbname));
        query.whereEqualTo("objectId",locId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mCurrLocation=(ParseLocation)objects.get(0);

                    if (mTitle != null) {
                        mTitle.setText(mCurrLocation.getName());
                    }
                    if (mDesc != null) {
                       mDesc.setDescription(mCurrLocation.getDescription());

                    }
                    if (mComments != null) {
                        LocationCommentsAdapter commentsAdapter = new LocationCommentsAdapter(getApplicationContext(),mCurrLocation.getComments());
                        mComments.setCurrLocation(mCurrLocation);
                        mComments.setCommentsAdapter(commentsAdapter);
                        mPagerAdapter.notifyDataSetChanged();
                    }
                    if (mLocation != null) {
                        mLocation.setText(mCurrLocation.getCity());
                        mPagerAdapter.notifyDataSetChanged();
                    }
                    if(mCity!=null){
                        mCity.setText(mCurrLocation.getCity());
                    }

                    mImages = mCurrLocation.getPhotos();

                    if(mImages!=null && mImages.size() > 0){
                        ImagesAdapter adapter = new ImagesAdapter(getApplicationContext(), mImages);
                        mPagerImages.setAdapter(adapter);
                    }

                } else {
                    // error
                }
            }
        });

    }
}
