package com.codeground.wanderlustbulgaria.Activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.Enums.UnfollowActions;
import com.codeground.wanderlustbulgaria.R;
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
    private TextView mName;
    private Button mFollowBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_home);

        mProfilePicture = (ImageView) findViewById(R.id.user_profile_photo);
        mName = (TextView) findViewById(R.id.name);
        mFollowing = (TextView) findViewById(R.id.following);
        mFollowers = (TextView) findViewById(R.id.followers);
        mFollowBtn = (Button) findViewById(R.id.follow_btn);
        mFollowBtn.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.actionbar_profile));


        mUserID=getIntent().getStringExtra("userID");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId",mUserID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mUser=(ParseUser)objects.get(0);
                    mName.setText(mUser.getString("first_name") +" "+ mUser.getString("last_name") );
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
}
