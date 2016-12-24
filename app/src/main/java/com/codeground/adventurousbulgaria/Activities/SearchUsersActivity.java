package com.codeground.adventurousbulgaria.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.Interfaces.IOnParseItemClicked;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.SearchedResultsAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener, IOnParseItemClicked {

    private Button mSearchBtn;
    private EditText mSearchField;
    private ListView mResults;

    private ParseQueryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_users);

        mSearchBtn = (Button) findViewById(R.id.search_btn);
        mSearchField = (EditText) findViewById(R.id.search_query);
        mResults=(ListView) findViewById(R.id.searched_users);

        mSearchBtn.setOnClickListener(this);
    }

    void search(){
        String userToSearch = mSearchField.getText().toString().toLowerCase();
        mAdapter = new SearchedResultsAdapter(this, this, userToSearch);
        mAdapter.setTextKey("title");
        mResults.setAdapter(mAdapter);
    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.search_btn){
            search();
        }

    }

    @Override
    public void onItemClicked(ParseObject user) {
        if(user!=null){
            followUser(user);
        }
    }

    private void followUser(ParseObject user) {
        if(ParseUser.getCurrentUser() != null){
            ParseRelation<ParseObject> relation = ParseUser.getCurrentUser().getRelation(getString(R.string.db_user_following));
            relation.add(user);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e!=null){
                        Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    //mAdapter.loadObjects();
                }
            });
        }
    }
}
