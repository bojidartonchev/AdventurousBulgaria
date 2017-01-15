package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseComment;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;

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
            v = View.inflate(getContext(), R.layout.list_view_comment_row, null);
        }

        super.getItemView(object, v, parent);

        //Comment creator
        TextView commentCreator = (TextView) v.findViewById(R.id.comment_creator);
        commentCreator.setText(object.getString("creator"));

        //Comment content
        TextView commentContent = (TextView) v.findViewById(R.id.comment_content);
        commentContent.setText(object.getString("content"));

        return v;
    }
}
