package com.codeground.adventurousbulgaria.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.MainApplication;
import com.codeground.adventurousbulgaria.R;
import com.kinvey.java.Query;
import com.kinvey.java.User;
import com.kinvey.java.core.DownloaderProgressListener;
import com.kinvey.java.core.MediaHttpDownloader;
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

import static com.facebook.FacebookSdk.getApplicationContext;


public class ProfileManager {

    public static Boolean savePictureToStorage(Bitmap bitmap,User currentUser) {
        //check bitmap for null
        Context ctx = getApplicationContext();
        ContextWrapper cw = new ContextWrapper(ctx);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, currentUser.getId()+"_picture");
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
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(currentUser.getId()+"_picture", directory.getAbsolutePath()).apply();
        return true;
    }

    public static void savePictureToKinvey(Bitmap bitmap, User currentUser,Activity activity){
        KinveyMetaData.AccessControlList acl = new KinveyMetaData.AccessControlList();
        acl.setGloballyReadable(true);
        FileMetaData mMetaData = new FileMetaData(currentUser.getId());  //create the FileMetaData object
        mMetaData.setPublic(true);  //set the file to be pubicly accesible
        mMetaData.setAcl(acl); //allow all users to see this file
        mMetaData.setFileName(currentUser.getId()+"_picture");

        File kinveyFile = new File(getApplicationContext().getCacheDir(),currentUser.getId()+"_picture");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(kinveyFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Failed to save to phone", Toast.LENGTH_SHORT).show();
        }
        ((MainApplication) activity.getApplication()).getKinveyClient().file().upload(mMetaData, kinveyFile, new UploaderProgressListener() {
            @Override
            public void progressChanged(MediaHttpUploader mediaHttpUploader) throws IOException {



            }
            @Override
            public void onSuccess(FileMetaData fileMetaData) {
                Toast.makeText(getApplicationContext(), "Profile picture uploaded to Kinvey.", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(getApplicationContext(), "Failed to upload picture to Kinvey.", Toast.LENGTH_SHORT).show();

            }

        });
    }

    public static void loadProfilePicture(final User currentUser, final ImageView userPicture, Activity activity) {
        Context ctx = getApplicationContext();

        //Load picture from phone if available
        if(PreferenceManager.getDefaultSharedPreferences(ctx).contains(currentUser.getId()+"_picture")) {
            String picturePath = getApplicationContext().getCacheDir() +"/" +currentUser.getId()+"_picture";
            try {
                File f = new File(picturePath);
                Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f));
                userPicture.setImageBitmap(bmp);
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //Load picture from kinvey if available
        final File picture =new File(getApplicationContext().getCacheDir(),currentUser.getId()+"_picture");
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(picture);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Query q = new Query();
        q.equals("_filename",currentUser.getId()+"_picture");

        ((MainApplication) activity.getApplication()).getKinveyClient().file().download(q, fos, new DownloaderProgressListener() {
            @Override
            public void onSuccess(Void result) {
                Log.i("KinveyPicture", "successfully downloaded file");
                try {
                    Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(picture));
                    userPicture.setImageBitmap(bmp);
                    savePictureToStorage(bmp,currentUser);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Throwable error) {
                Log.e("KinveyPicture", "failed to download file.", error);
            }
            @Override
            public void progressChanged(MediaHttpDownloader downloader) throws IOException {
                Log.i("KinveyPicture", "progress updated: "+downloader.getDownloadState());
                // any updates to UI widgets must be done on the UI thread
            }
        });

    }
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
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


}