package com.codeground.adventurousbulgaria.Utilities;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.Interfaces.IOnParseItemClicked;
import com.codeground.adventurousbulgaria.R;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class SearchedResultsAdapter extends ParseQueryAdapter{
    private IOnParseItemClicked mListener;

    public SearchedResultsAdapter(Context context, IOnParseItemClicked listener, final String userToSearch)
    {
        super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
            public ParseQuery create() {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereContains("search_match", userToSearch);
                return query;
            }
        });
        mListener = listener;
    }

    @Override
    public View getItemView(final ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_profile_row, null);
        }

        super.getItemView(object, v, parent);

        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.icon);
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
        timestampView.setText(object.getCreatedAt().toString());

        // Follow button
        Button followBtn = (Button) v.findViewById(R.id.follow_btn);
        if(followBtn != null){
            followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.follow_btn){
                        if(mListener!=null){
                            mListener.onItemClicked(object);
                        }
                    }
                }
            });
        }

        return v;
    }
}
