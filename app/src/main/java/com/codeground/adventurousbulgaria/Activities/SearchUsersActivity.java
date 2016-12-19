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
    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.search_btn){
            search();
        }

    }
}
