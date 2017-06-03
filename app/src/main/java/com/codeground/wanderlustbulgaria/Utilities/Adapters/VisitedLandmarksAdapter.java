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
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Arrays;

public class VisitedLandmarksAdapter extends ParseQueryAdapter{

    public VisitedLandmarksAdapter(Context context, final ParseRelation<ParseObject> visitedRelation)
    {
        super(context, new QueryFactory<ParseUser>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = visitedRelation.getQuery();
                query.selectKeys(Arrays.asList("photo1","name","city"));

                return query;
            }
        });
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_landmark_row, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image
        ParseImageView locImage = (ParseImageView) v.findViewById(R.id.photo);
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
