package com.codeground.wanderlustbulgaria.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.BroadcastReceivers.BootReceiver;
import com.codeground.wanderlustbulgaria.Fragments.ProfileFragment;
import com.codeground.wanderlustbulgaria.Interfaces.IOnFacebookProfileImageUploadCompleted;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.MainMenuPagerAdapter;
import com.codeground.wanderlustbulgaria.Utilities.SettingsManager;
import com.codeground.wanderlustbulgaria.Utilities.UploadFacebookProfileImageTask;
import com.parse.CountCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        IOnFacebookProfileImageUploadCompleted,
        TabLayout.OnTabSelectedListener,
        View.OnClickListener{
    private static final int INITIAL_REQUEST = 1337;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private TextView mPersonName;
    private NavigationView mProfileView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView mProfilePicture;
    private TextView mPendingFollowersBadge;

    private MainMenuPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        updateUserStatus(true);

        //Fetch all profile settings
        SettingsManager.updatePrefsProfileSettings(this);

        checkForPermissions();

        //actionbar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.accept,  /* "open drawer" description */
                R.string.afternoon  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mPagerAdapter.getPageTitle(mPager.getCurrentItem()));
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Create profile fragment
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().
                beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
        //mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        mProfileView = (NavigationView) findViewById(R.id.profile_view);
        mDrawerLayout = (DrawerLayout)  findViewById(R.id.drawer_layout);
        mPendingFollowersBadge = (TextView) MenuItemCompat.getActionView(mProfileView.getMenu().findItem(R.id.pending_followers));
        mPersonName = (TextView) mProfileView.getHeaderView(0).findViewById(R.id.profile_name);
        mProfilePicture = (ImageView) mProfileView.getHeaderView(0).findViewById(R.id.profile_image);
        mProfilePicture.setOnClickListener(this);

        if(getIntent().hasExtra("facebookImageUrl")){
            String facebookUrl = getIntent().getStringExtra("facebookImageUrl");
            new UploadFacebookProfileImageTask(this).execute(facebookUrl);
        }

        String currUserName = ParseUser.getCurrentUser().get(getString(R.string.db_user_firstname)).toString()+" "+
                ParseUser.getCurrentUser().get(getString(R.string.db_user_lastname)).toString();
        mPersonName.setText(currUserName);
        mProfileView.setNavigationItemSelectedListener(this);

        mPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.main_menu_viewpager);
        mPager.setAdapter(mPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_menu_tabs);
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(this);
        mPager.setCurrentItem(1);
        mPager.setOffscreenPageLimit(3);

        loadProfilePicture();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProfileImageUploadCompleted() {
        this.loadProfilePicture();
    }


    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                INITIAL_PERMS[0])
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        INITIAL_PERMS[1])
                        != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    INITIAL_PERMS,
                    INITIAL_REQUEST);

        }else{
            BootReceiver.scheduleAlarm(this);
        }
    }

    private void loadProfilePicture() {
        ParseUser currUser = ParseUser.getCurrentUser();
        ParseFile pic = (ParseFile) currUser.get("profile_picture");

        if(pic != null){
            pic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        mProfilePicture.setImageBitmap(bmp);
                    } else {
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //This method will initialize the count value
        initializeCountDrawer();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case INITIAL_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    BootReceiver.scheduleAlarm(this);
                } else {
                    Toast.makeText(this, getString(R.string.alert_no_gps), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_users) {
            Intent intent = new Intent(getApplicationContext(), SearchUsersActivity.class);

            startActivity(intent);
        }
        if (id == R.id.submit_location) {
            Intent intent = new Intent(getApplicationContext(), SubmitLocationActivity.class);

            startActivity(intent);
        }

        if (id == R.id.logout) {
            logout();
        }

        if(id == R.id.nav_my_profile){
            Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
            intent.putExtra("userID", ParseUser.getCurrentUser().getObjectId());
            startActivity(intent);
        }

        if(id == R.id.nav_manage){
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }

        if(id == R.id.pending_followers){
            Intent intent = new Intent(getApplicationContext(), PendingFollowersActivity.class);

            startActivity(intent);
        }

        return false;
    }


    private void logout() {
        updateUserStatus(false);
        ParseUser.getCurrentUser().logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }

    private void initializeCountDrawer() {
        //Gravity property aligns the text
        mPendingFollowersBadge.setGravity(Gravity.CENTER_VERTICAL);
        mPendingFollowersBadge.setTypeface(null, Typeface.BOLD);
        mPendingFollowersBadge.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        ParseQuery followingQuery = ParseUser.getCurrentUser().getRelation("pending_followers").getQuery();
        followingQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(count > 0 && count <= 99){
                    //Already followed
                    mPendingFollowersBadge.setText(Integer.toString(count));
                }else if(count > 99){
                    mPendingFollowersBadge.setText("99+");
                }
            }
        });
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        getSupportActionBar().setTitle(tab.getText());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void updateUserStatus(boolean online)
    {
        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null){
            user.put("online", online);
            user.saveEventually();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus(false);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.profile_image){
            Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
            intent.putExtra("userID", ParseUser.getCurrentUser().getObjectId());
            startActivity(intent);
        }
    }
}