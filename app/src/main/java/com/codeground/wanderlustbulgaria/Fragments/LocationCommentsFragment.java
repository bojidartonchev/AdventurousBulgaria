package com.codeground.wanderlustbulgaria.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.LocationCommentsAdapter;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
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
    private LocationCommentsAdapter mAdapter;


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
        mAdapter = adapter;
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
        if(content.length()<10){
            Toast.makeText(getContext(), getString(R.string.alert_comment_length), Toast.LENGTH_SHORT).show();
            return;
        }
        final ParseObject comment = new ParseObject(getString(R.string.db_commments_dbname));

        DialogWindowManager.show(getContext());
        comment.put(getString(R.string.db_commments_creator), ParseUser.getCurrentUser().getEmail());
        comment.put(getString(R.string.db_commments_content), content);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseRelation<ParseObject> relation = mCurrLocation.getRelation(getString(R.string.db_location_comments));
                relation.add(comment);
                mCurrLocation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        DialogWindowManager.dismiss();
                        mAdapter.loadObjects();
                    }
                });
            }

        });



    }
}