package com.codeground.adventurousbulgaria.Activities;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeground.adventurousbulgaria.Enums.UnfollowActions;
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

import static com.codeground.adventurousbulgaria.Enums.UnfollowActions.REMOVE_PENDING_FROM_USER;
import static com.codeground.adventurousbulgaria.Enums.UnfollowActions.UNFOLLOW_USER;


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
            Button followBtn = (Button) v.findViewById(R.id.follow_btn);
            if(followBtn.getText().toString().equals(getString(R.string.follow_btn_text))){
                followUser(user, followBtn);
            }else if(followBtn.getText().toString().equals(getString(R.string.followed_btn_text))){
                promptUnfollow(user, followBtn, UNFOLLOW_USER);
            }else if(followBtn.getText().toString().equals(getString(R.string.pending_follow_btn_text))){
                promptUnfollow(user, followBtn, REMOVE_PENDING_FROM_USER);
            }

        }
    }

    private void followUser(ParseObject user, final Button followBtn) {
        if(ParseUser.getCurrentUser() != null && user != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("targetUserId", user.getObjectId());
            ParseCloud.callFunctionInBackground("followUser", params, new FunctionCallback<Integer>() {
                public void done(Integer result, ParseException e) {
                    if (e == null){
                        if(followBtn != null){
                            if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOWED){
                                followBtn.setText(R.string.followed_btn_text);
                            }else if(result == PARSE_CLOUD_CODE_RESPONSE_CODE_FOLLOW_REQUESTED){
                                followBtn.setText(R.string.pending_follow_btn_text);
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

    private void unfollowUser(ParseObject user, final Button followBtn, UnfollowActions action) {
        boolean isFollowed = (action == UNFOLLOW_USER);
        if(ParseUser.getCurrentUser() != null && user != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("targetUserId", user.getObjectId());
            params.put("isFollowed", isFollowed);
            ParseCloud.callFunctionInBackground("unfollowUser", params, new FunctionCallback<Integer>() {
                public void done(Integer result, ParseException e) {
                    if (e == null){
                        if(followBtn != null){
                            followBtn.setText(R.string.follow_btn_text);
                        }
                    }else{
                        //error
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void promptUnfollow(final ParseObject user, final Button followBtn, final UnfollowActions action){
        LayoutInflater factory = LayoutInflater.from(this);
        final View unfollowDialogView = factory.inflate(R.layout.custom_dialog, null);
        final AlertDialog unfollowDialog = new AlertDialog.Builder(this).create();
        unfollowDialog.setView(unfollowDialogView);
        TextView textField = (TextView)unfollowDialogView.findViewById(R.id.text_dialog);
        textField.setText(String.format(getString(R.string.unfollow_prompt_text), user.getString("first_name")));
        unfollowDialogView.findViewById(R.id.btn_dialog_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                unfollowUser(user, followBtn, action);
                unfollowDialog.dismiss();
            }
        });
        unfollowDialogView.findViewById(R.id.btn_dialog_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollowDialog.dismiss();
            }
        });

        unfollowDialog.show();
    }
}
