package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.ProfileManager;
import com.kinvey.android.Client;
import com.kinvey.java.User;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView mDateCreated;
    private TextView mTravelledKm;
    private TextView mUserName;
    private ImageView mUserPicture;
    private Button mLogoutButton;
    private User mCurrentUser;

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

        mCurrentUser = ((MainApplication) getApplication()).getKinveyClient().user();

        mUserName.setText(mCurrentUser.get("Name").toString());
        mDateCreated.setText(mCurrentUser.get("DateCreated").toString());
        mTravelledKm.setText("0.00km");

        ProfileManager.loadProfilePicture(mCurrentUser,mUserPicture,this);
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
        Client mKinveyClient = ((MainApplication) getApplication()).getKinveyClient();
        mKinveyClient.user().logout().execute();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();

            try {
                final Bitmap bitmap = ProfileManager.getCroppedBitmap(MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage));
                if(bitmap!=null){
                    mUserPicture.setImageBitmap(bitmap);

                    AsyncTask.execute(new Runnable()  {
                        @Override
                        public void run() {

                            //Saving bitmap to internal storage
                            ProfileManager.savePictureToStorage(bitmap, mCurrentUser);
                            //Saving bitmap to kinvey
                            ProfileManager.savePictureToKinvey(bitmap, mCurrentUser, UserHomeActivity.this);
                        }
                    });
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}
