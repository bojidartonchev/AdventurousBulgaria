package com.codeground.wanderlustbulgaria.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.support.v7.app.AlertDialog;
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
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseUtilities;
import com.codeground.wanderlustbulgaria.Utilities.ProfileManager;
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
    private static final int CAMERA_TAKE_PHOTO = 1338;
    private static final int CAMERA_REQUEST = 1339;
    private static final int SELECT_FROM_GALLERY = 1340;
    private static final int STORAGE_REQUEST = 1341;

    private static final int THUMBSIZE = 128;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final String[] CAMERA_PERMS = {
            Manifest.permission.CAMERA
    };
    private static final String[] STORAGE_PERMS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
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
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mPagerAdapter.getPageTitle(mPager.getCurrentItem()));
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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
    public void onClick(View v) {
        if(v.getId() == R.id.profile_image){
            selectPictureOption();
        }
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
            case CAMERA_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    startCamera();
                } else {
                    Toast.makeText(this, getString(R.string.alert_no_camera), Toast.LENGTH_LONG).show();
                }
                break;
            case  STORAGE_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                   startGallery();
                } else {
                    Toast.makeText(this, getString(R.string.alert_no_storage), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void checkGalleryPerms(){
        if (ContextCompat.checkSelfPermission(this,STORAGE_PERMS[0])
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    STORAGE_PERMS,
                    STORAGE_REQUEST);
        } else {
            startGallery();
        }
    }

    private void checkCameraPerms(){
        if (ContextCompat.checkSelfPermission(this,
                CAMERA_PERMS[0])
                != PackageManager.PERMISSION_GRANTED){

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    CAMERA_PERMS,
                    CAMERA_REQUEST);

        }else{
           startCamera();
        }
    }

    private void startGallery(){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_FROM_GALLERY);
    }

    private void startCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_TAKE_PHOTO);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_TAKE_PHOTO && data!=null) {
                Bitmap bitmap = ProfileManager.getCroppedBitmap((Bitmap) data.getExtras().get("data"));
                mProfilePicture.setImageBitmap(bitmap);
                ParseUtilities.uploadProfilePicture(bitmap);
            } else if(requestCode == SELECT_FROM_GALLERY && data!=null){

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bmp = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(picturePath),
                        THUMBSIZE,
                        THUMBSIZE);

                mProfilePicture.setImageBitmap(ProfileManager.getCroppedBitmap(bmp));
                ParseUtilities.uploadProfilePicture(bmp);
            }
        }
    }

    private void selectPictureOption(){
        final CharSequence[] items = { getString(R.string.menu_take_photo), getString(R.string.menu_choose_library),
                getString(R.string.menu_cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.menu_choose_option));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.menu_take_photo))) {
                    checkCameraPerms();

                } else if (items[item].equals(getString(R.string.menu_choose_library))) {
                    checkGalleryPerms();
                } else if (items[item].equals(getString(R.string.menu_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void logout() {
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
    public void onProfileImageUploadCompleted() {
        this.loadProfilePicture();
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
        user.put("online", online);
        user.saveEventually();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus(false);
    }
}