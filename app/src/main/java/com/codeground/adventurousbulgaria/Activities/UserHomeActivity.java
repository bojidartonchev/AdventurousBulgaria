package com.codeground.adventurousbulgaria.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kinvey.android.Client;
import com.kinvey.java.User;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView mDateCreated;
    private TextView mTravelledKm;
    private TextView mPersonName;
    private ImageView mPersonPicture;
    private Button mLogoutButton;
    private User mCurrentUser;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        mLogoutButton = (Button) findViewById(R.id.logout_btn);
        mDateCreated = (TextView) findViewById(R.id.membersince_date);
        mTravelledKm = (TextView) findViewById(R.id.travelled_km);
        mPersonName = (TextView) findViewById(R.id.person_name);
        mPersonPicture = (ImageView) findViewById(R.id.profile_picture);

        mPersonPicture.setOnLongClickListener(this);
        mLogoutButton.setOnClickListener(this);

        mCurrentUser = ((MainApplication) getApplication()).getKinveyClient().user();

        mPersonName.setText(mCurrentUser.get("Name").toString());
        mDateCreated.setText(mCurrentUser.get("DateCreated").toString());
        mTravelledKm.setText("0.00km");

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).build();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logout_btn) {
            logout();
        }
    }

    private void logout() {
        Client mKinveyClient = ((MainApplication) getApplication()).getKinveyClient();
        mKinveyClient.user().logout().execute();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.profile_picture) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
        }
        return true;
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
            mPersonPicture.setImageBitmap(bitmap);
        }
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }


}
