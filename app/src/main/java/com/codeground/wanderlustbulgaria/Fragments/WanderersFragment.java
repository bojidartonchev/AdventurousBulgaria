package com.codeground.wanderlustbulgaria.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Activities.ChatActivity;
import com.codeground.wanderlustbulgaria.Activities.SearchUsersActivity;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.codeground.wanderlustbulgaria.Utilities.RoundedParseImageView;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;


public class WanderersFragment extends Fragment implements ParseQueryAdapter.OnQueryLoadListener, AdapterView.OnItemClickListener, View.OnClickListener{

    /** The Chat list. */
    private ArrayList<ParseUser> uList;

    /** The user. */
    public static ParseUser user;

    private View mContainer;
    private View mProgress;
    private RelativeLayout mNoItemsLabel;
    private Button mFindFriendButton;

    private ParseQueryAdapter mAdapter;
    private ListView mResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wanderers, container, false);

        mContainer = v.findViewById(R.id.list_container);
        mProgress = v.findViewById(R.id.list_progress);
        mNoItemsLabel = (RelativeLayout)v.findViewById(R.id.chat_no_items_label);
        mFindFriendButton = (Button)mNoItemsLabel.findViewById(R.id.chat_find_user_btn);
        mFindFriendButton.setOnClickListener(this);

        user = ParseUser.getCurrentUser();

        mResults = (ListView) v.findViewById(R.id.list);
        mResults.setOnItemClickListener(this);

        mAdapter = new UserAdapter(getActivity());
        mAdapter.setTextKey("title");
        mAdapter.setImageKey("photo");
        mAdapter.addOnQueryLoadListener(this);
        mResults.setAdapter(mAdapter);

        return v;
    }

    public static WanderersFragment newInstance() {

        WanderersFragment f = new WanderersFragment();

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.loadObjects();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(List objects, Exception e) {
        mProgress.setVisibility(View.GONE);

        if(e==null){
            if(objects!=null && objects.size() > 0){
                mContainer.setVisibility(View.VISIBLE);
                mNoItemsLabel.setVisibility(View.GONE);
            }else{
                mNoItemsLabel.setVisibility(View.VISIBLE);
            }
        }else{
            mNoItemsLabel.setVisibility(View.VISIBLE);
            NotificationsManager.showToast(e.getMessage(), TastyToast.ERROR);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ParseUser entry = (ParseUser) parent.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("username", entry.getUsername());
        intent.putExtra("full_name", entry.getString("first_name") + " " + entry.getString("last_name"));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.chat_find_user_btn){
            Intent intent = new Intent(getContext(), SearchUsersActivity.class);
            startActivity(intent);
        }
    }

    private class UserAdapter extends ParseQueryAdapter
    {

        public UserAdapter(Context context)
        {
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
                public ParseQuery create() {
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseRelation<ParseUser> followingRelation = user.getRelation("following");
                    ParseQuery<ParseUser> q = followingRelation.getQuery();

                    return q;
                }
            });
        }

        @Override
        public View getItemView(final ParseObject object, View v, ViewGroup parent) {
            if (v == null)
                v = getActivity().getLayoutInflater().inflate(R.layout.chat_item, null);

            super.getItemView(object, v, parent);

            RoundedParseImageView imgView = (RoundedParseImageView) v.findViewById(android.R.id.icon);
            ParseFile imageFile = object.getParseFile("profile_picture");
            if (imageFile != null) {
                imgView.setParseFile(imageFile);
                imgView.loadInBackground();
            } else {
                imgView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }

            TextView lbl = (TextView) v.findViewById(R.id.profile_name);

            lbl.setText(object.getString("first_name") + " " + object.getString("last_name"));

            if (object.getBoolean("online")) {
                imgView.setBorderColor(Color.parseColor("#32CD32"));
            } else {
                imgView.setBorderColor(Color.GRAY);
            }

            return v;
        }

    }
}