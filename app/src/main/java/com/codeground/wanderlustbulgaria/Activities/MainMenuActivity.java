package com.codeground.wanderlustbulgaria.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.BroadcastReceivers.BootReceiver;
import com.codeground.wanderlustbulgaria.Fragments.ProfileFragment;
import com.codeground.wanderlustbulgaria.Interfaces.IOnFacebookProfileImageUploadCompleted;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.AllLocationsManager;
import com.codeground.wanderlustbulgaria.Utilities.LocationMarker;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseUtilities;
import com.codeground.wanderlustbulgaria.Utilities.ProfileManager;
import com.codeground.wanderlustbulgaria.Utilities.SettingsManager;
import com.codeground.wanderlustbulgaria.Utilities.UploadFacebookProfileImageTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.CountCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        IOnFacebookProfileImageUploadCompleted,ClusterManager.OnClusterClickListener<LocationMarker>,
        ClusterManager.OnClusterInfoWindowClickListener<LocationMarker>,
        ClusterManager.OnClusterItemClickListener<LocationMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<LocationMarker>  {

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

    private Button mNewsFeedBtn;
    private Button mAllLandmarksBtn;
    private Button mNearByBtn;
    private Button mCalendarByBtn;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private TextView mPersonName;
    private NavigationView mProfileView;
    private ImageView mProfilePicture;
    private TextView mPendingFollowersBadge;

    private ClusterManager mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_menu);

        //Fetch all profile settings
        SettingsManager.updatePrefsProfileSettings(this);

        checkForPermissions();

        //Create profile fragment
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().
                beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
        //mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        mProfileView = (NavigationView) findViewById(R.id.profile_view);
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

        mNewsFeedBtn = (Button) findViewById(R.id.news_feed_btn);
        mNewsFeedBtn.setOnClickListener(this);

        mNearByBtn = (Button) findViewById(R.id.near_by_button);
        mNearByBtn.setOnClickListener(this);

        mCalendarByBtn = (Button) findViewById(R.id.calendar_button);
        mCalendarByBtn.setOnClickListener(this);

        mAllLandmarksBtn = (Button) findViewById(R.id.landmarks_btn);
        mAllLandmarksBtn.setOnClickListener(this);
        loadProfilePicture();
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

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.news_feed_btn) {
            Intent intent = new Intent(this, NewsFeedActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.calendar_button) {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
        }
        if(v.getId() == R.id.profile_image){
            selectPictureOption();
        }
        if (v.getId() == R.id.landmarks_btn) {
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.near_by_button) {
            Intent intent = new Intent(this, NearByActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case INITIAL_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    onConnected(null);
                    //startLocationService();
                    BootReceiver.scheduleAlarm(this);
                    if(mMap !=null){
                        //noinspection MissingPermission you kidding me?
                        mMap.setMyLocationEnabled(true);
                    }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mClusterManager = new ClusterManager<LocationMarker>(this, googleMap);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            loadMarkersOnMap();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        //Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapLoaded() {
        if (mLastLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 8));
        }
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

        if(id == R.id.nav_last_visited_locations){
            Intent i = new Intent(getApplicationContext(), AllLandmarksActivity.class);
            i.putExtra("allCompletedOnly", true);
            startActivity(i);
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

    private void loadMarkersOnMap(){
        if(AllLocationsManager.getInstance().isLoaded()){
            List<ParseLocation> locations = AllLocationsManager.getInstance().getLocations();
            if(mMap != null){
                for (ParseLocation location : locations) {
                    Location loc = new Location("");
                    loc.setLatitude(location.getLocation().getLatitude());
                    loc.setLongitude(location.getLocation().getLongitude());
                    LocationMarker marker = new LocationMarker(loc.getLatitude(), loc.getLongitude(), location.getName(), location.getIcon());
                    mClusterManager.addItem(marker);
                }
            }
        }
    }

    @Override
    public void onProfileImageUploadCompleted() {
        this.loadProfilePicture();
    }

    @Override
    public boolean onClusterClick(Cluster<LocationMarker> cluster) {
        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<LocationMarker> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(LocationMarker item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return true;
    }

    @Override
    public void onClusterItemInfoWindowClick(LocationMarker item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }
}