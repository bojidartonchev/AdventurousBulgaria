package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.kinvey.android.Client;
import com.kinvey.java.User;
import com.kinvey.java.core.MediaHttpUploader;
import com.kinvey.java.core.UploaderProgressListener;
import com.kinvey.java.model.FileMetaData;
import com.kinvey.java.model.KinveyMetaData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

        loadProfilePicture();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = getCroppedBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Saving bitmap to internal storage
            savePictureToStorage(bitmap);
            //Saving bitmap to kinvey
            KinveyMetaData.AccessControlList acl = new KinveyMetaData.AccessControlList();
            acl.setGloballyReadable(true);
            FileMetaData mMetaData = new FileMetaData(mCurrentUser.getId());  //create the FileMetaData object
            mMetaData.setPublic(true);  //set the file to be pubicly accesible
            mMetaData.setAcl(acl); //allow all users to see this file
            mMetaData.setFileName(mUserName.getText().toString()+"_picture");

            File kinveyFile = new File(getApplicationContext().getCacheDir(),mUserName.getText().toString()+"_picture");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(kinveyFile);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to save to phone", Toast.LENGTH_SHORT).show();
            }

            ((MainApplication) getApplication()).getKinveyClient().file().upload(mMetaData, kinveyFile, new UploaderProgressListener() {
                @Override
                public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {
                }
                @Override
                public void onSuccess(FileMetaData fileMetaData) {
                    Toast.makeText(getApplicationContext(), "Profile picture uploaded!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(Throwable error) {
                    Toast.makeText(getApplicationContext(), "Failed to upload picture.", Toast.LENGTH_SHORT).show();

                }

            });

            mUserPicture.setImageBitmap(bitmap);
        }
    }

    private Boolean savePictureToStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, R.string.profile_picture_title+".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(getString(R.string.profile_picture_title), directory.getAbsolutePath()).apply();
        return true;
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void loadProfilePicture() {
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).contains(getString(R.string.profile_picture_title))) {
            String mPicturePath = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(getString(R.string.profile_picture_title), "");
            try {
                File f = new File(mPicturePath, R.string.profile_picture_title+".png");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                mUserPicture.setImageBitmap(b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
