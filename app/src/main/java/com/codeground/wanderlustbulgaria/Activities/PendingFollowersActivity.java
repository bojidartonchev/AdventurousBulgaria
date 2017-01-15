package com.codeground.wanderlustbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.PendingFollowersAdapter;
import com.parse.ParseQueryAdapter;

public class PendingFollowersActivity extends AppCompatActivity {
    private ParseQueryAdapter mAdapter;
    private ListView mResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pending_followers);

        mResults=(ListView) findViewById(R.id.pending_users_list);

        mAdapter = new PendingFollowersAdapter(this);
        mAdapter.setTextKey("title");
        mResults.setAdapter(mAdapter);
    }
}
