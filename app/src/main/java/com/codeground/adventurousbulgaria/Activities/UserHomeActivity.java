package com.codeground.adventurousbulgaria.Activities;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class UserHomeActivity extends AppCompatActivity{

    private ParseUser mUser;
    private String mUserID;
    private ImageView mProfilePicture;
    private TextView mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_home);

        mProfilePicture = (ImageView) findViewById(R.id.user_profile_photo);
        mName = (TextView) findViewById(R.id.name);

        mUserID=getIntent().getStringExtra("userID");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId",mUserID);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mUser=(ParseUser)objects.get(0);
                    mName.setText(mUser.getString("first_name") +" "+ mUser.getString("last_name") );
                    loadPicture();


                } else {
                    // error
                }
            }
        });
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
}
