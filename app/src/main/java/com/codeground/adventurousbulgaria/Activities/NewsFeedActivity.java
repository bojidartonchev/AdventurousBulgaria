package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Adapters.NewsFeedAdapter;
import com.parse.ParseQueryAdapter;

public class NewsFeedActivity extends AppCompatActivity {
    private ParseQueryAdapter mAdapter;
    private ListView mResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_feed);

        mResults=(ListView) findViewById(R.id.pending_users_list);

        mAdapter = new NewsFeedAdapter(this);
        mAdapter.setTextKey("title");
        mResults.setAdapter(mAdapter);
    }
}
