package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.parse.ParseUser;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView mDateCreated;
    private TextView mTravelledKm;
    private TextView mUserName;
    private ImageView mUserPicture;
    private ListView mProfileData;
    private Button mLogoutButton;
    private ParseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        mLogoutButton = (Button) findViewById(R.id.logout_btn);
        mDateCreated = (TextView) findViewById(R.id.membersince_date);
        mTravelledKm = (TextView) findViewById(R.id.travelled_km);
        mUserName = (TextView) findViewById(R.id.person_name);
        mUserPicture = (ImageView) findViewById(R.id.profile_picture);
        mUserPicture.setOnLongClickListener(this);
        mLogoutButton.setOnClickListener(this);

        mCurrentUser = ParseUser.getCurrentUser();
        mUserName.setText(mCurrentUser.getString("first_name"));
        mTravelledKm.setText("0.00km");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn) {
            logout();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.profile_picture) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
        }
        return true;
    }

    private void logout() {
        mCurrentUser.logOut();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {



        }
    }


}
