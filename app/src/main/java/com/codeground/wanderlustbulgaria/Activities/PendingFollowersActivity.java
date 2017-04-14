package com.codeground.wanderlustbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.PendingFollowersAdapter;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.parse.ParseQueryAdapter;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.List;

public class PendingFollowersActivity extends AppCompatActivity implements ParseQueryAdapter.OnQueryLoadListener {
    private ParseQueryAdapter mAdapter;
    private ListView mResults;
    private TextView mNoItemsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pending_followers);

        mResults = (ListView) findViewById(R.id.pending_users_list);
        mNoItemsLabel = (TextView) findViewById(R.id.pending_followers_no_items_label);

        mAdapter = new PendingFollowersAdapter(this);
        mAdapter.setTextKey("title");
        mAdapter.addOnQueryLoadListener(this);
        mResults.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.pending_wanderers_title);
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

    @Override
    public void onLoading() {
        DialogWindowManager.show(this);
    }

    @Override
    public void onLoaded(List objects, Exception e) {

        if(e==null){
            if(objects!=null && objects.size() > 0){
                mResults.setVisibility(View.VISIBLE);
                mNoItemsLabel.setVisibility(View.GONE);
            }else{
                mNoItemsLabel.setVisibility(View.VISIBLE);
            }
        }else{
            mNoItemsLabel.setVisibility(View.VISIBLE);
            NotificationsManager.showToast(e.getMessage(), TastyToast.ERROR);
        }
        DialogWindowManager.dismiss();
    }
}
