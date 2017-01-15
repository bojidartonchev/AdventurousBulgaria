package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Activities.LandmarkActivity;
import com.codeground.wanderlustbulgaria.Activities.UserHomeActivity;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseTraveller;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NewsFeedAdapter extends ParseQueryAdapter {

    private Context ctx;

    public NewsFeedAdapter(Context context)
    {
        super(context, new QueryFactory<ParseTraveller>() {
            public ParseQuery create() {
                ParseRelation following = ParseUser.getCurrentUser().getRelation("following");
                ParseQuery<ParseTraveller> query = ParseQuery.getQuery("Activity");
                query.whereMatchesQuery("origin_user", following.getQuery());
                return query;
            }
        });
        ctx = context;
    }

    @Override
    public View getItemView(final ParseObject object, View v, final ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_news_feed_activity_row, null);
        }

        super.getItemView(object, v, parent);

        // Add the title view
        final TextView titleUsernameView = (TextView) v.findViewById(R.id.profile_name);

        object.getParseUser("origin_user").fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser user, ParseException e) {
                if(e==null){
                    titleUsernameView.setText(user.getString("first_name") + " " + user.getString("last_name"));

                    titleUsernameView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);

                            intent.putExtra("userID", user.getObjectId());
                            ctx.startActivity(intent);
                        }
                    });
                }

            }
        });

        //Add the content
        final TextView content = (TextView) v.findViewById(R.id.content);
        String type = object.getString("type");
        Resources res = getApplicationContext().getResources();
        String contentText = String.format(res.getString(R.string.action_content_text), type);

        if(content!=null){
            content.setText(contentText);
        }


        final TextView titleLocationView = (TextView) v.findViewById(R.id.location_name);
        object.getParseObject("target_location").fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject locObject, ParseException e) {
                if(e==null){
                    if(locObject!= null){
                        titleLocationView.setText(locObject.getString("name"));

                        titleLocationView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), LandmarkActivity.class);
                                intent.putExtra("locationId", locObject.getObjectId());

                                ctx.startActivity(intent);
                            }
                        });
                    }
                }
            }
        });

        // Add and download the image
        ParseImageView activityImage = (ParseImageView) v.findViewById(R.id.activity_image);
        ParseFile imageFile = object.getParseFile("photo");
        if (imageFile != null) {
            activityImage.setParseFile(imageFile);
            activityImage.loadInBackground();
        }

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.timestamp);
        timestampView.setText(object.getCreatedAt().toString());

        return v;
    }
}
