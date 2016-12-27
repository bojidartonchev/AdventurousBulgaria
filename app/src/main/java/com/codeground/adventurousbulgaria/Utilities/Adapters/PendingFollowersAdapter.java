package com.codeground.adventurousbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.Enums.FollowPendingAction;
import com.codeground.adventurousbulgaria.R;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;


public class PendingFollowersAdapter extends ParseQueryAdapter {
    PendingFollowersAdapter mThis;

    public PendingFollowersAdapter(Context context)
    {
        super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = ParseUser.getCurrentUser().getRelation("pending_followers").getQuery();
                return query;
            }
        });
        mThis = this;
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_pending_profile_row, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image
        ParseImageView profileImage = (ParseImageView) v.findViewById(R.id.activity_image);
        ParseFile imageFile = object.getParseFile("profile_picture");
        if (imageFile != null) {
            profileImage.setParseFile(imageFile);
            profileImage.loadInBackground();
        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.profile_name);
        titleTextView.setText(object.getString("first_name") + " " + object.getString("last_name") );

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.timestamp);
        timestampView.setText(object.getCreatedAt().toString());

        // Follow button
        final Button acceptBtn = (Button) v.findViewById(R.id.accept_btn);
        final Button rejectBtn = (Button) v.findViewById(R.id.reject_btn);

        if(acceptBtn != null){
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFollowAction(object, FollowPendingAction.ACCEPT_PENDING_FOLLOW, v);
                }
            });
        }

        if(rejectBtn != null){
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFollowAction(object, FollowPendingAction.REJECT_PENDING_FOLLOW, v);
                }
            });
        }

        return v;
    }

    private void doFollowAction(final ParseObject user, FollowPendingAction action, final View view){
        if(ParseUser.getCurrentUser() != null && user != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("targetUserId", user.getObjectId());
            boolean isAccept = action == FollowPendingAction.ACCEPT_PENDING_FOLLOW;
            params.put("isAccept", isAccept);
            ParseCloud.callFunctionInBackground("followRequestAction", params, new FunctionCallback<Integer>() {
                public void done(Integer result, ParseException e) {
                    if (e == null){
                        mThis.loadObjects();
                    }else{
                        //error
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}
