package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

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
import java.util.Locale;

public class LandmarkActivity extends AppCompatActivity{
    private ParseLocation mCurrLocation;
    private ViewPager mPager;
    private ViewPager mPagerImages;
    private LocationPagerAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private LocationDescriptionFragment mDesc;
    private LocationCommentsFragment mComments;
    private double mLat;
    private double mLong;
    private ArrayList<ParseFile> mImages;
    private boolean mPicking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mPagerImages = (ViewPager) findViewById(R.id.images_pager);

        Intent intent = getIntent();
        String locId = null;

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            locId = uri.getQueryParameter("locationId");
        }else{
            locId = getIntent().getStringExtra("locationId");
        }

        if(locId == null){
            finish();
        }

        mPagerAdapter = new LocationPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mPager);
        LocationPagerAdapter curr = (LocationPagerAdapter) mPager.getAdapter();
        mDesc = (LocationDescriptionFragment) curr.instantiateItem(mPager, 0);
        mComments = (LocationCommentsFragment) mPager.getAdapter().instantiateItem(mPager, 1);
        TabLayout dots = (TabLayout) findViewById(R.id.tabDots);
        dots.setupWithViewPager(mPagerImages, true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImages = new ArrayList<>();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.db_location_dbname));
        query.whereEqualTo("objectId", locId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mCurrLocation = (ParseLocation) objects.get(0);

                    getSupportActionBar().setTitle(mCurrLocation.getName());

                    if (mDesc != null) {
                        mDesc.setDescription(mCurrLocation.getDescription());
                        mDesc.setCity(mCurrLocation.getCity());
                    }

                    if (mComments != null) {
                        LocationCommentsAdapter commentsAdapter = new LocationCommentsAdapter(getApplicationContext(), mCurrLocation.getComments());
                        mComments.setCurrLocation(mCurrLocation);
                        mComments.setCommentsAdapter(commentsAdapter);
                        mPagerAdapter.notifyDataSetChanged();
                    }

                    mLat = mCurrLocation.getLocation().getLatitude();
                    mLong = mCurrLocation.getLocation().getLongitude();

                    mImages = mCurrLocation.getPhotos();

                    if (mImages != null && mImages.size() > 0) {
                        ImagesAdapter adapter = new ImagesAdapter(getApplicationContext(), mImages);
                        mPagerImages.setAdapter(adapter);
                    }

                } else {
                    // error
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
            case R.id.gmap_btn:
            {
                String address = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", mLat, mLong, mCurrLocation.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
                return true;
            }
            case R.id.menu_item_share:
            {
                onShareClick();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.landmark_menu, menu);

        // Return true to display menu
        return true;
    }

    public void onShareClick() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
        //        "://" + getApplicationContext().getResources().getResourcePackageName(R.mipmap.ic_launcher)
        //        + '/' + getApplicationContext().getResources().getResourceTypeName(R.mipmap.ic_launcher)
        //        + '/' + getApplicationContext().getResources().getResourceEntryName(R.mipmap.ic_launcher) );
        intent.putExtra(Intent.EXTRA_TITLE, "Wanderlust Bulgaria");
        intent.putExtra(Intent.EXTRA_TEXT, "https://fb.me/282963612159586?locationId=" + mCurrLocation.getObjectId());
        //intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(intent, "Share via"));
    }
}
