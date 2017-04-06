package com.codeground.wanderlustbulgaria.Utilities.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class LandmarksAdapter extends ParseQueryAdapter{

    public LandmarksAdapter(Context context, final String category)
    {
        super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery create() {
                ParseQuery<ParseLocation> query = ParseQuery.getQuery("Location");
                query.whereContains("category", category);
                return query;
            }
        });
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_landmark_row, null);
        }

        // Add and download the image
        ParseImageView locImage = (ParseImageView) v.findViewById(R.id.location_list_row_image);
        ParseFile imageFile = object.getParseFile("photo1");
        locImage.setScaleType(ImageView.ScaleType.FIT_XY);
        if (imageFile != null) {
            locImage.setParseFile(imageFile);
            locImage.loadInBackground();
        }
        TextView locTitle = (TextView) v.findViewById(R.id.title);
        TextView locPlace = (TextView) v.findViewById(R.id.location);
        locTitle.setText(object.getString("name"));
        locPlace.setText(object.getString("city"));
        locTitle.setBackgroundColor(Color.argb(200,91,82,82));
        locPlace.setBackgroundColor(Color.argb(200,91,82,82));

        return v;
    }
}
