package com.codeground.wanderlustbulgaria.Activities;


import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.Enums.UnfollowActions;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseUtilities;
import com.codeground.wanderlustbulgaria.Utilities.ProfileManager;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.codeground.wanderlustbulgaria.Enums.UnfollowActions.REMOVE_PENDING_FROM_USER;
import static com.codeground.wanderlustbulgaria.Enums.UnfollowActions.UNFOLLOW_USER;
import static com.codeground.wanderlustbulgaria.Utilities.ProfileManager.PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED;
import static com.codeground.wanderlustbulgaria.Utilities.ProfileManager.PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ParseUser mUser;
    private String mUserID;
    private ImageView mProfilePicture;
    private TextView mFollowers;
    private TextView mFollowing;
    private TextView mVisitedLocations;
    private TextView mProfileDesc;
    private TextView mName;
    private Button mFollowBtn;

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

        mProfilePicture = (ImageView) findViewById(R.id.user_profile_photo);
        mProfilePicture.setOnClickListener(this);
        mName = (TextView) findViewById(R.id.name);
        mFollowing = (TextView) findViewById(R.id.following);
        mFollowers = (TextView) findViewById(R.id.followers);
        mVisitedLocations = (TextView) findViewById(R.id.visited_locations);
        mProfileDesc = (TextView) findViewById(R.id.profile_desc);
        mFollowBtn = (Button) findViewById(R.id.follow_btn);
        mFollowBtn.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_profile));

        mUserID=getIntent().getStringExtra("userID");

        if(ParseUser.getCurrentUser().getObjectId().equals(mUserID)) {
            mProfileDesc.setOnClickListener(this);

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
                    mProfileDesc.setText(mUser.getString("profile_description"));
                    loadPicture();
                    getFollowingCount();
                    getFollowersCount();
                    getVisitedCount();

                } else {
                    // error
                }
            }
        });

        loadFollowButton();

    }


    private void getVisitedCount() {
        ParseQuery followingQuery = mUser.getRelation("visited_locations").getQuery();
        followingQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e==null){
                    mVisitedLocations.setText(String.valueOf(count));
                } else {
                    mVisitedLocations.setText(0);
                }
            }
        });
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
                        mFollowBtn.setText(R.string.followed_btn_text);
                    }else{
                        ParseQuery pendingQuery = ParseUser.getCurrentUser().getRelation("pending_following").getQuery();
                        pendingQuery.whereContains("objectId", mUserID);
                        pendingQuery.countInBackground(new CountCallback() {
                            @Override
                            public void done(int count, ParseException e) {
                                if(count > 0){
                                    //Pending follow
                                    mFollowBtn.setText(R.string.pending_follow_btn_text);
                                }else{
                                    mFollowBtn.setText(R.string.follow_btn_text);
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
    public void onClick(View v) {
        if(v.getId()==R.id.follow_btn){
            Button followBtn = (Button) v.findViewById(R.id.follow_btn);
            if(followBtn.getText().toString().equals(getString(R.string.follow_btn_text))){
                ProfileManager.followUser(mUser, new FunctionCallback<Integer>() {
                    @Override
                    public void done(Integer result, ParseException e) {
                        if (e == null){
                            if(mFollowBtn != null){
                                if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED){
                                    mFollowBtn.setText(R.string.followed_btn_text);
                                    getFollowersCount();
                                }else if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED){
                                    mFollowBtn.setText(R.string.pending_follow_btn_text);
                                }
                            }
                        }else{
                            //error
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else if(followBtn.getText().toString().equals(getString(R.string.followed_btn_text))){
                promptUnfollow(UNFOLLOW_USER);
            }else if(followBtn.getText().toString().equals(getString(R.string.pending_follow_btn_text))){
                promptUnfollow(REMOVE_PENDING_FROM_USER);
            }
        }
        if(v.getId() == R.id.user_profile_photo){
            selectPictureOption();
        }
        if(v.getId() == R.id.profile_desc){
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(getString(R.string.profile_description));
            View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_input_description, (ViewGroup) v.getParent(),false);
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            input.setText(mProfileDesc.getText());

            builder.setView(viewInflated);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ParseUser currUser = ParseUser.getCurrentUser();
                    currUser.put("profile_description", input.getText().toString());
                    currUser.saveInBackground();
                    mProfileDesc.setText(input.getText());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
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
                            if(mFollowBtn != null){
                                mFollowBtn.setText(R.string.follow_btn_text);
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

}
