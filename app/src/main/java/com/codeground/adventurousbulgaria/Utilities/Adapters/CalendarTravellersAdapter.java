package com.codeground.adventurousbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.ParseUtils.ParseComment;
import com.codeground.adventurousbulgaria.Utilities.ParseUtils.ParseTraveller;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarTravellersAdapter extends ParseQueryAdapter{

    public CalendarTravellersAdapter(Context context, final Date date)
    {
        super(context, new QueryFactory<ParseComment>() {
            public ParseQuery create() {
                ParseRelation following = ParseUser.getCurrentUser().getRelation("following");
                ParseQuery<ParseTraveller> query = ParseQuery.getQuery("Traveller");
                query.whereMatchesQuery("origin_user", following.getQuery());
                query.whereGreaterThanOrEqualTo("travel_date", date);

                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, 1);
                query.whereLessThan("travel_date", c.getTime());
                return query;
            }
        });
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_traveller_row, null);
        }

        super.getItemView(object, v, parent);

        //Creator
        final TextView originUser = (TextView) v.findViewById(R.id.profile_name);
        final ParseImageView profileImage = (ParseImageView) v.findViewById(R.id.activity_image);
        object.getParseUser("origin_user").fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e==null){
                    originUser.setText(user.getString("first_name") + " " + user.getString("last_name"));

                    // Add and download the image
                    ParseFile imageFile = user.getParseFile("profile_picture");
                    if (imageFile != null) {
                        profileImage.setParseFile(imageFile);
                        profileImage.loadInBackground();
                    }
                }

            }
        });


        //From location
        TextView from = (TextView) v.findViewById(R.id.from_location);
        from.setText(object.getString("from_city"));

        //To location
        final TextView to = (TextView) v.findViewById(R.id.to_location);
        object.getParseObject("to_location").fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject toLocation, ParseException e) {
                if(e==null){
                    to.setText(toLocation.getString("name"));
                }

            }
        });

        //Set departure time
        TextView departureTime = (TextView) v.findViewById(R.id.departure_time);
        SimpleDateFormat fr = new SimpleDateFormat("HH:mm");
        Date departureDate = object.getDate("travel_date");
        String timeString = fr.format(departureDate);
        departureTime.setText(timeString);

        return v;
    }
}
