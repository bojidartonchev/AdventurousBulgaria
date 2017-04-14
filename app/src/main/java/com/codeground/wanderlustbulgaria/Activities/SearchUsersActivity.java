package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.SearchedResultsAdapter;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.List;

public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ParseQueryAdapter.OnQueryLoadListener {

    private Button mSearchBtn;
    private EditText mSearchField;
    private ListView mResults;
    private TextView mNoResultsLabel;


    private ParseQueryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_users);

        mSearchBtn = (Button) findViewById(R.id.search_btn);
        mSearchField = (EditText) findViewById(R.id.search_query);
        mResults=(ListView) findViewById(R.id.searched_users);
        mNoResultsLabel = (TextView) findViewById(R.id.search_no_items_label);
        mResults.setOnItemClickListener(this);

        mSearchBtn.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_users_title);
    }

    void search(){
        String userToSearch = mSearchField.getText().toString().toLowerCase();
        mAdapter = new SearchedResultsAdapter(this, userToSearch);
        mAdapter.setTextKey("title");
        mAdapter.setImageKey("photo");
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
        if(e==null){
            if(objects!=null && objects.size() > 0){
                mResults.setVisibility(View.VISIBLE);
                mNoResultsLabel.setVisibility(View.GONE);
            }else{
                mNoResultsLabel.setVisibility(View.VISIBLE);
            }
        }else{
            mNoResultsLabel.setVisibility(View.VISIBLE);
            NotificationsManager.showToast(e.getMessage(), TastyToast.ERROR);
        }
        DialogWindowManager.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}