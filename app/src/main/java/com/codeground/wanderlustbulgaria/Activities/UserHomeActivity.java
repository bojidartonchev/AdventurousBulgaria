package com.codeground.wanderlustbulgaria.Activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.Enums.UnfollowActions;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.LandmarksAdapter;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.VisitedLandmarksAdapter;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseUtilities;
import com.codeground.wanderlustbulgaria.Utilities.ProfileManager;
import com.codeground.wanderlustbulgaria.Utilities.RoundedParseImageView;
import com.devspark.appmsg.AppMsg;
import com.google.android.gms.vision.text.Text;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.List;

import static com.codeground.wanderlustbulgaria.Enums.UnfollowActions.REMOVE_PENDING_FROM_USER;
import static com.codeground.wanderlustbulgaria.Enums.UnfollowActions.UNFOLLOW_USER;
import static com.codeground.wanderlustbulgaria.Utilities.ProfileManager.PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED;
import static com.codeground.wanderlustbulgaria.Utilities.ProfileManager.PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ParseQueryAdapter.OnQueryLoadListener{

    private ParseUser mUser;
    private String mUserID;
    private RoundedParseImageView mProfilePicture;
    private TextView mFollowers;
    private TextView mFollowing;
    private TextView mName;
    private ImageButton mFollowBtn;
    private ListView mVisitedLocations;

    private ParseQueryAdapter mAdapter;
    private TextView mVisitedCount;
    private TextView mNoItemsLabel;
    private TextView mNoAccessLabel;
    private ProgressBar mVisitedProgress;
    private boolean alreadyFollowed = false;
    private Activity mActivity;

    private static final int CAMERA_TAKE_PHOTO = 1338;
    private static final int CAMERA_REQUEST = 1339;
    private static final int SELECT_FROM_GALLERY = 1340;
    private static final int STORAGE_REQUEST = 1341;

    private static final String[] CAMERA_PERMS = {
            Manifest.permission.CAMERA
    };
    private static final String[] STORAGE_PERMS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int THUMBSIZE = 128;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_home);
        mActivity = this;
        mProfilePicture = (RoundedParseImageView) findViewById(R.id.user_profile_photo);
        mProfilePicture.setOnClickListener(this);
        mName = (TextView) findViewById(R.id.name);
        mFollowing = (TextView) findViewById(R.id.following);
        mFollowers = (TextView) findViewById(R.id.followers);
        mVisitedLocations = (ListView) findViewById(R.id.visited_landmarks_list) ;
        mFollowBtn = (ImageButton) findViewById(R.id.follow_btn);
        mFollowBtn.setOnClickListener(this);

        mNoItemsLabel = (TextView) findViewById(R.id.visited_no_items_label);
        mNoAccessLabel = (TextView) findViewById(R.id.visited_no_access_label);
        mVisitedProgress = (ProgressBar) findViewById(R.id.visited_progress);
        mVisitedCount = (TextView) findViewById(R.id.visited_count);

        mUserID=getIntent().getStringExtra("userID");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_profile));

        if(ParseUser.getCurrentUser().getObjectId().equals(mUserID)) {

            ViewGroup parent = (ViewGroup) mFollowBtn.getParent();
            if (parent != null) {
                parent.removeView(mFollowBtn);
            }
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId",mUserID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mUser=(ParseUser)objects.get(0);
                    mName.setText(mUser.getString("first_name") +" "+ mUser.getString("last_name"));
                    mNoAccessLabel.setText(String.format(getString(R.string.visited_no_access_label), mUser.getString("first_name")));

                    //Init visited locations
                    loadVisitedLocations(mUser.getRelation("visited_locations"));

                    loadPicture();
                    getFollowingCount();
                    getFollowersCount();
                } else {
                    // error
                }
            }
        });

        loadFollowButton();
    }


    private void getFollowingCount() {
        ParseQuery followingQuery = mUser.getRelation("following").getQuery();
        followingQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e==null){
                    mFollowing.setText(String.valueOf(count));
                } else {
                    mFollowing.setText(0);
                }
            }
        });
    }

    private void getFollowersCount() {
        ParseQuery followersQuery = mUser.getRelation("followers").getQuery();
        followersQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e==null){
                    mFollowers.setText(String.valueOf(count));
                } else {
                    mFollowers.setText(0);
                }
            }
        });
    }

    private void loadFollowButton() {
        if(mFollowBtn!=null){
            //Check if is already followed
            ParseQuery followingQuery = ParseUser.getCurrentUser().getRelation("following").getQuery();
            followingQuery.whereContains("objectId", mUserID);
            followingQuery.countInBackground(new CountCallback() {
                @Override
                public void done(int count, ParseException e) {
                    if(count > 0){
                        //Already followed
                        mFollowBtn.setImageResource(R.drawable.user_already_added_icon);
                        mFollowBtn.setTag("Followed");
                        alreadyFollowed = true;
                    }else{
                        ParseQuery pendingQuery = ParseUser.getCurrentUser().getRelation("pending_following").getQuery();
                        pendingQuery.whereContains("objectId", mUserID);
                        pendingQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int count, ParseException e) {
                                if(count > 0){
                                    //Pending follow
                                    if(mFollowBtn!=null){
                                        mFollowBtn.setImageResource(R.drawable.add_user_icon);
                                        mFollowBtn.setTag("Requested");

                                        final Animation animation = new AlphaAnimation(1, 0);
                                        animation.setDuration(1000);
                                        animation.setInterpolator(new LinearInterpolator());
                                        animation.setRepeatCount(Animation.INFINITE);
                                        animation.setRepeatMode(Animation.REVERSE);
                                        mFollowBtn.startAnimation(animation);
                                    }
                                }else{
                                    mFollowBtn.setTag("Follow");
                                    mFollowBtn.setImageResource(R.drawable.add_user_icon);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void loadPicture(){
        ParseFile pic = (ParseFile) mUser.get("profile_picture");

        if(pic != null){
            mProfilePicture.setParseFile(pic);
            mProfilePicture.loadInBackground();
        }
    }

    private void loadVisitedLocations(ParseRelation<ParseObject> visitedRelation){
        mAdapter = new VisitedLandmarksAdapter(getApplicationContext(), visitedRelation);
        mAdapter.addOnQueryLoadListener(this);
        mVisitedLocations.setOnItemClickListener(this);
        mVisitedLocations.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.follow_btn){
            ImageButton followBtn = (ImageButton) v.findViewById(R.id.follow_btn);
            if(followBtn.getTag().toString().equals(getString(R.string.follow_btn_text))){
                ProfileManager.followUser(mUser, new FunctionCallback<Integer>() {
                    @Override
                    public void done(Integer result, ParseException e) {
                        if (e == null){
                            if(mFollowBtn != null){
                                if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED){
                                    mFollowBtn.setImageResource(R.drawable.user_already_added_icon);
                                    mFollowBtn.setTag("Followed");
                                    getFollowersCount();
                                    alreadyFollowed = true;
                                    mAdapter.loadObjects();
                                }else if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED){
                                    mFollowBtn.setTag("Requested");
                                    alreadyFollowed = false;
                                    final Animation animation = new AlphaAnimation(1, 0);
                                    animation.setDuration(1000);
                                    animation.setInterpolator(new LinearInterpolator());
                                    animation.setRepeatCount(Animation.INFINITE);
                                    animation.setRepeatMode(Animation.REVERSE);
                                    mFollowBtn.startAnimation(animation);

                                    NotificationsManager.showDropDownNotification(mActivity, String.format(getString(R.string.requested_notification), mUser.get("first_name")), AppMsg.STYLE_INFO);
                                }
                            }
                        }else{
                            //error
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else if(followBtn.getTag().toString().equals(getString(R.string.followed_btn_text))){
                promptUnfollow(UNFOLLOW_USER);
            }else if(followBtn.getTag().toString().equals(getString(R.string.pending_follow_btn_text))){
                promptUnfollow(REMOVE_PENDING_FROM_USER);
            }
        }
        if(v.getId() == R.id.user_profile_photo){
            selectPictureOption();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void promptUnfollow(final UnfollowActions action){
        LayoutInflater factory = LayoutInflater.from(this);
        final View unfollowDialogView = factory.inflate(R.layout.custom_dialog, null);
        final AlertDialog unfollowDialog = new AlertDialog.Builder(this).create();
        unfollowDialog.setView(unfollowDialogView);
        TextView textField = (TextView)unfollowDialogView.findViewById(R.id.text_dialog);
        textField.setText(String.format(getString(R.string.unfollow_prompt_text), mUser.getString("first_name")));
        unfollowDialogView.findViewById(R.id.btn_dialog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                ProfileManager.unfollowUser(mUser, action, new FunctionCallback<Integer>() {
                    @Override
                    public void done(Integer object, ParseException e) {
                        if (e == null){
                            if(mFollowBtn != null) {
                                mFollowBtn.setTag("Follow");
                                mFollowBtn.setImageResource(R.drawable.add_user_icon);
                                mFollowBtn.setAnimation(null);
                                alreadyFollowed = false;

                                mVisitedCount.setVisibility(View.GONE);
                                mVisitedLocations.setVisibility(View.GONE);
                                mNoItemsLabel.setVisibility(View.GONE);
                                mNoAccessLabel.setVisibility(View.VISIBLE);
                            }

                            getFollowersCount();
                        }else{
                            //error
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                unfollowDialog.dismiss();
            }
        });
        unfollowDialogView.findViewById(R.id.btn_dialog_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollowDialog.dismiss();
            }
        });

        unfollowDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_TAKE_PHOTO && data!=null) {
                Bitmap bitmap = ProfileManager.getCroppedBitmap((Bitmap) data.getExtras().get("data"));
                if(bitmap!=null) {
                    mProfilePicture.setImageBitmap(bitmap);
                    ParseUtilities.uploadProfilePicture(bitmap);
                }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

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
        mNoAccessLabel.setVisibility(View.GONE);
        mVisitedProgress.setVisibility(View.VISIBLE);
        mNoItemsLabel.setVisibility(View.GONE);
    }

    @Override
    public void onLoaded(List objects, Exception e) {
        if(mVisitedLocations !=null){
            mVisitedProgress.setVisibility(View.GONE);

            if(shouldPrivacyShow()) {
                mVisitedCount.setVisibility(View.VISIBLE);
                mVisitedCount.setText(String.format(getString(R.string.profile_visited_count), objects.size()));
                if(objects.size()>0)
                {
                    mVisitedLocations.setVisibility(View.VISIBLE);
                }else{
                    mNoItemsLabel.setVisibility(View.VISIBLE);
                }

            }else{
                mNoAccessLabel.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean shouldPrivacyShow() {
        return ParseUser.getCurrentUser().getObjectId().equals(mUserID) || mUser.getBoolean("is_follow_allowed") || alreadyFollowed;
    }
}
