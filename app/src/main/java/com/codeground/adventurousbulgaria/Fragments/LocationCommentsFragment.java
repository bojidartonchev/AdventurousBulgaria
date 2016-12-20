package com.codeground.adventurousbulgaria.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.LocationCommentsAdapter;
import com.codeground.adventurousbulgaria.Utilities.ParseLocation;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class LocationCommentsFragment extends Fragment implements View.OnClickListener {

    private ListView mComments;
    private Button mSubmitBtn;
    private ParseLocation mCurrLocation;
    private EditText mCommentField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_comments, container, false);

        mComments = (ListView) v.findViewById(R.id.location_comments);
        mCommentField = (EditText) v.findViewById(R.id.comment_field);
        mSubmitBtn = (Button) v.findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(this);
        return v;
    }

    public static LocationCommentsFragment newInstance() {

        LocationCommentsFragment f = new LocationCommentsFragment();


        return f;
    }
    public void setCommentsAdapter(LocationCommentsAdapter adapter){
        mComments.setAdapter(adapter);
    }
    public void setCurrLocation(ParseLocation loc){
        mCurrLocation=loc;
    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.submit_btn){
            submitComment();
        }
    }

    private void submitComment() {
        String content = mCommentField.getText().toString();
        final ParseObject comment = new ParseObject(getString(R.string.db_commments_dbname));

        comment.put(getString(R.string.db_commments_creator), ParseUser.getCurrentUser().getEmail());
        comment.put(getString(R.string.db_commments_content), content);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseRelation<ParseObject> relation = mCurrLocation.getRelation(getString(R.string.db_location_comments));
                relation.add(comment);
                mCurrLocation.saveInBackground();
            }

        });



    }
}