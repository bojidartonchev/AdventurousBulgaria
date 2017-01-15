package com.codeground.wanderlustbulgaria.Utilities;

import android.graphics.Bitmap;

import com.codeground.wanderlustbulgaria.Enums.UnfollowActions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.HashMap;

import static com.codeground.wanderlustbulgaria.Enums.UnfollowActions.UNFOLLOW_USER;


public class ProfileManager {
    public static final int PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED = 0;
    public static final int PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED = 1;

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        if(bitmap==null){
            return null;
        }
        Bitmap output;
        if (bitmap.getWidth() >= bitmap.getHeight()){

            output = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        }else{

            output = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return output;
    }

    public static void followUser(ParseObject user, FunctionCallback<Integer> cb) {
        if(ParseUser.getCurrentUser() != null && user != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("targetUserId", user.getObjectId());
            ParseCloud.callFunctionInBackground("followUser", params, cb);
        }
    }

    public static void unfollowUser(ParseUser user, UnfollowActions action, FunctionCallback<Integer> cb) {
        boolean isFollowed = (action == UNFOLLOW_USER);
        if(ParseUser.getCurrentUser() != null && user != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("targetUserId", user.getObjectId());
            params.put("isFollowed", isFollowed);
            ParseCloud.callFunctionInBackground("unfollowUser", params, cb);
        }
    }

}