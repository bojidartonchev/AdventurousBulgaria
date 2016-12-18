package com.codeground.adventurousbulgaria.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.SearchedResultsAdapter;
import com.parse.ParseQueryAdapter;



public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener {

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
        mAdapter = new SearchedResultsAdapter(this,userToSearch);
        mAdapter.setTextKey("title");
        mResults.setAdapter(mAdapter);

        //ParseQuery<ParseUser> query = ParseUser.getQuery();
        //query.whereContains("search_match", userToSearch);
        //query.findInBackground(new FindCallback<ParseUser>() {
        //    public void done(List<ParseUser> objList, ParseException e) {
        //        if (e == null) {
        //            Log.d("User search","@@@@Retrieved " + objList.size()+ " users");

        //        } else {
        //            Log.d("User search", "@@@Error: " + e.getMessage());
        //        }
        //    }
        //});


    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.search_btn){
            search();

        }

    }
}
