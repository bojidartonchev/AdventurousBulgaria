package com.codeground.wanderlustbulgaria.Utilities.ParseUtils;


import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ParseUtilities {

    public static ParseFile createParseFile(Uri selectedImage, String name){
        ParseFile output=null;
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            output = new ParseFile(name,image);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }
    public static ParseFile createParseFile(Bitmap bmp, String name){
        if(bmp==null){

            NotificationsManager.showToast("Failed to get photo.", TastyToast.CONFUSING);
        }
        ParseFile output=null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        output = new ParseFile(name,image);

        return output;
    }


    public static void uploadProfilePicture(Bitmap bmp){
        ParseFile pic = createParseFile(bmp, "profile.jpg");
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.put("profile_picture",pic);
        currUser.saveInBackground();
    }


}
