package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Adapters.SearchedResultsAdapter;
import com.codeground.adventurousbulgaria.Utilities.DialogWindowManager;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ParseQueryAdapter.OnQueryLoadListener {

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
        mResults.setOnItemClickListener(this);

        mSearchBtn.setOnClickListener(this);
    }

    void search(){
        String userToSearch = mSearchField.getText().toString().toLowerCase();
        mAdapter = new SearchedResultsAdapter(this, userToSearch);
        mAdapter.setTextKey("title");
        mAdapter.addOnQueryLoadListener(this);
        mResults.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.search_btn){
            search();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
        ParseObject entry = (ParseObject) parent.getItemAtPosition(position);

        intent.putExtra("userID", entry.getObjectId());
        startActivity(intent);
    }

    @Override
    public void onLoading() {
        DialogWindowManager.show(this);
    }

    @Override
    public void onLoaded(List objects, Exception e) {
        DialogWindowManager.dismiss();
    }
}