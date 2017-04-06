package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseComment;
import com.codeground.wanderlustbulgaria.Utilities.RoundedParseImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class LocationCommentsAdapter extends ParseQueryAdapter{

    public LocationCommentsAdapter(Context context,final ParseRelation comments)
    {
        super(context, new ParseQueryAdapter.QueryFactory<ParseComment>() {
            public ParseQuery create() {

                ParseQuery query = comments.getQuery();
                query.orderByDescending("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.comment_item, null);
        }

        super.getItemView(object, v, parent);

        ParseUser creator = object.getParseUser("creator");

        //Comment creator
        final TextView commentCreator = (TextView) v.findViewById(R.id.comment_creator);

        //Comment creator
        final RoundedParseImageView commentCreatorImage = (RoundedParseImageView) v.findViewById(R.id.creator_pic);

        creator.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    commentCreator.setText(object.getString("first_name") + object.getString("last_name"));

                    commentCreatorImage.setParseFile(object.getParseFile("profile_picture"));
                    commentCreatorImage.loadInBackground();
                }
            }
        });

        //Comment content
        TextView commentContent = (TextView) v.findViewById(R.id.comment_content);
        commentContent.setText(object.getString("content"));

        return v;
    }
}
