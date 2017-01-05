package com.codeground.adventurousbulgaria.Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.codeground.adventurousbulgaria.Interfaces.IOnFacebookProfileImageUploadCompleted;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFacebookProfileImageTask extends AsyncTask<String, Void, ParseFile> {

    private Exception exception;
    private IOnFacebookProfileImageUploadCompleted mCallback;

    public UploadFacebookProfileImageTask(IOnFacebookProfileImageUploadCompleted cb) {
        mCallback = cb;
    }

    protected ParseFile doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitm = BitmapFactory.decodeStream(input);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapBytes = stream.toByteArray();

            return new ParseFile("profile.jpg", bitmapBytes);
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    protected void onPostExecute(ParseFile fileImage) {
        if(this.exception==null){
            ParseUser user = ParseUser.getCurrentUser();
            user.put("profile_picture", fileImage);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    mCallback.onProfileImageUploadCompleted();
                }
            });
        }
    }
}