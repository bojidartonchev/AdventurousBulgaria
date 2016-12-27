package com.codeground.adventurousbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.ParseUtils.ParseActivity;
import com.codeground.adventurousbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;


public class NewsFeedAdapter extends ParseQueryAdapter {

    public NewsFeedAdapter(Context context)
    {
        super(context, new QueryFactory<ParseActivity>() {
            public ParseQuery create() {
                ParseRelation following = ParseUser.getCurrentUser().getRelation("following");
                ParseQuery<ParseActivity> query = ParseQuery.getQuery("Activity");
                query.whereMatchesQuery("origin_user", following.getQuery());
                return query;
            }
        });
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_news_feed_activity_row, null);
        }

        super.getItemView(object, v, parent);

        ParseUser originUser = null;
        try {
            originUser = object.getParseUser("origin_user").fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseLocation targetLocation = null;
        try {
            targetLocation = object.getParseObject("target_location").fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Add and download the image
        ParseImageView activityImage = (ParseImageView) v.findViewById(R.id.activity_image);
        ParseFile imageFile = object.getParseFile("photo");
        if (imageFile != null) {
            activityImage.setParseFile(imageFile);
            activityImage.loadInBackground();
        }

        // Add the title view
        TextView titleUsernameView = (TextView) v.findViewById(R.id.profile_name);

        if(originUser != null){
            titleUsernameView.setText(originUser.getString("first_name") + " " + originUser.getString("last_name"));

            titleUsernameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO redirect to user profile page
                }
            });
        }

        //Add the content
        TextView content = (TextView) v.findViewById(R.id.content);

        //TODO Generate the text from type of the action
        if(object.getString("type").equals("visited") && targetLocation!= null){
            content.setText("has visited " + targetLocation.getString("name"));
        }

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.timestamp);
        timestampView.setText(object.getCreatedAt().toString());

        return v;
    }
}
