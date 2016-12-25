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
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.HashMap;


public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener, IOnParseItemClicked {
    public static final int PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED = 0;
    public static final int PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED = 1;

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
    public void onItemClicked(ParseObject user, View v) {
        if(user!=null){
            followUser(user, v);
        }
    }

    private void followUser(ParseObject user, final View v) {
        if(ParseUser.getCurrentUser() != null && user != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("targetUserId", user.getObjectId());
            ParseCloud.callFunctionInBackground("followUser", params, new FunctionCallback<Integer>() {
                public void done(Integer result, ParseException e) {
                    if (e == null){
                        if(v != null){
                            Button followBtn = (Button) v.findViewById(R.id.follow_btn);
                            if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED){
                                if(followBtn != null){
                                    followBtn.setText("Followed");
                                }
                            }else if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED){
                                if(followBtn != null){
                                    followBtn.setText("Pending...");
                                }
                            }
                        }
                    }else{
                        //error
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
