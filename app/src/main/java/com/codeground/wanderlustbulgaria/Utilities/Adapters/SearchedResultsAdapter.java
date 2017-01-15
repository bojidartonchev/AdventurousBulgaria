package com.codeground.wanderlustbulgaria.Utilities.Adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchedResultsAdapter extends ParseQueryAdapter{

    public SearchedResultsAdapter(Context context, final String userToSearch)
    {
        super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereContains("search_match", userToSearch);
                return query;
            }
        });
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_profile_row, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.activity_image);
        ParseFile imageFile = object.getParseFile("profile_picture");
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }

        // Add the title view
        TextView titleTextView = (TextView) v.findViewById(R.id.profile_name);
        titleTextView.setText(object.getString("first_name") + " " + object.getString("last_name") );

        // Add a reminder of how long this item has been outstanding
        TextView timestampView = (TextView) v.findViewById(R.id.timestamp);
        SimpleDateFormat fr = new SimpleDateFormat("dd/MM/yyyy");
        Date dat = object.getCreatedAt();
        String time = fr.format(dat);
        timestampView.setText("Member since " +time);

        return v;
    }
}
