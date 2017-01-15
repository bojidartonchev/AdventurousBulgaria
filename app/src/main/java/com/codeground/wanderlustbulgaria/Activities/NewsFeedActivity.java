package com.codeground.wanderlustbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.NewsFeedAdapter;
import com.codeground.wanderlustbulgaria.Utilities.DialogWindowManager;
import com.parse.ParseQueryAdapter;

import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements ParseQueryAdapter.OnQueryLoadListener {
    private ParseQueryAdapter mAdapter;
    private ListView mResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_feed);

        mResults=(ListView) findViewById(R.id.pending_users_list);

        mAdapter = new NewsFeedAdapter(this);
        mAdapter.setTextKey("title");
        mAdapter.addOnQueryLoadListener(this);
        mResults.setAdapter(mAdapter);
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
