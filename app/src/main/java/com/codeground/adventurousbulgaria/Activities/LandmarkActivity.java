package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.Fragments.LocationCommentsFragment;
import com.codeground.adventurousbulgaria.Fragments.LocationDescriptionFragment;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.LocationCommentsAdapter;
import com.codeground.adventurousbulgaria.Utilities.LocationPagerAdapter;
import com.codeground.adventurousbulgaria.Utilities.ParseLocation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class LandmarkActivity extends AppCompatActivity{
    private TextView mTitle;
    private TextView mDescription;
    private TextView mLocation;
    private ImageView mPhoto;
    private ParseLocation mCurrLocation;
    private EditText mCommentField;
    private Button mCommentBtn;
    private ViewPager mPager;
    private LocationPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private LocationDescriptionFragment mDesc;
    private LocationCommentsFragment mComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        mTitle = (TextView) findViewById(R.id.landmark_title);

        final String locId = getIntent().getStringExtra("locationId");
        mPagerAdapter = new LocationPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mPager);
        LocationPagerAdapter curr = (LocationPagerAdapter) mPager.getAdapter();
        mDesc =(LocationDescriptionFragment) curr.instantiateItem(mPager, 0);
        mComments =(LocationCommentsFragment) mPager.getAdapter().instantiateItem(mPager, 1);

        String currentLandmarkName = getIntent().getStringExtra("locationName");

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

                } else {
                    // error
                }
            }
        });

    }
}
