package com.codeground.wanderlustbulgaria.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Activities.SubmitTravellerActivity;
import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.PlannerCalendarAdapter;
import com.codeground.wanderlustbulgaria.Utilities.NotificationsManager;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.codeground.wanderlustbulgaria.Utilities.RoundedParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlannerFragment extends Fragment implements ParseQueryAdapter.OnQueryLoadListener,View.OnClickListener, IOnItemClicked {

    private List<Date> dateList = new ArrayList<>();
    private RecyclerView mCalendarView;
    private FloatingActionButton mAddBtn;
    private PlannerCalendarAdapter mAdapter;
    private View mContainer;
    private View mProgress;
    private RelativeLayout mNoItemsLabel;
    private Button mAddPlanButton;

    private ParseQueryAdapter mResultsAdapter;
    private ListView mResults;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_planner, container, false);
        mCalendarView = (RecyclerView)v.findViewById(R.id.calendarView);

        mContainer = v.findViewById(R.id.list_container);
        mProgress = v.findViewById(R.id.list_progress);
        mNoItemsLabel = (RelativeLayout)v.findViewById(R.id.plan_no_items_label);
        mAddPlanButton = (Button)mNoItemsLabel.findViewById(R.id.alternative_add_plan_btn);
        mAddPlanButton.setOnClickListener(this);

        mAdapter = new PlannerCalendarAdapter(dateList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mCalendarView.setLayoutManager(mLayoutManager);
        mCalendarView.setItemAnimator(new DefaultItemAnimator());
        mCalendarView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(mCalendarView.getContext(),
                LinearLayoutManager.HORIZONTAL);
        mCalendarView.addItemDecoration(decoration);

        initDates();

        mAddBtn = (FloatingActionButton) v.findViewById(R.id.add_new_traveller_btn);
        mAddBtn.setOnClickListener(this);

        mResults = (ListView) v.findViewById(R.id.list);

        Calendar today = Calendar.getInstance();
        mResultsAdapter = new PlanAdapter(getActivity(), today.getTime());
        mResultsAdapter.setTextKey("title");
        mResultsAdapter.setImageKey("photo");
        mResultsAdapter.addOnQueryLoadListener(this);
        mResults.setAdapter(mResultsAdapter);
        return v;
    }

    public static PlannerFragment newInstance() {
        PlannerFragment f = new PlannerFragment();
        return f;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_new_traveller_btn || v.getId() == R.id.alternative_add_plan_btn){
            Intent i = new Intent(getActivity(), SubmitTravellerActivity.class);
            startActivity(i);
        }
    }

    private void initDates(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -15);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        for (Date i = startDate; i.before(endDate);){
            dateList.add(i);

            //increment i
            calendar.setTime(i);
            calendar.add(Calendar.DATE, 1);
            i = calendar.getTime();
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(int pos) {
        Date selectedDate = dateList.get(pos);
        mResultsAdapter = new PlanAdapter(getActivity(), selectedDate);
        mResultsAdapter.setTextKey("title");
        mResultsAdapter.setImageKey("photo");
        mResultsAdapter.addOnQueryLoadListener(this);
        mResults.setAdapter(mResultsAdapter);
    }

    @Override
    public void onLoading() {
        mProgress.setVisibility(View.VISIBLE);

        mResults.setVisibility(View.GONE);
        mNoItemsLabel.setVisibility(View.GONE);
    }

    @Override
    public void onLoaded(List objects, Exception e) {
        mProgress.setVisibility(View.GONE);

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
    }

    private class PlanAdapter extends ParseQueryAdapter
    {

        public PlanAdapter(Context context, final Date date)
        {
            super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
                public ParseQuery create() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);

                    ParseQuery<ParseLocation> query = ParseQuery.getQuery("Traveller");
                    query.whereGreaterThanOrEqualTo("travel_date", calendar.getTime());

                    calendar.add(Calendar.DATE, 1);
                    query.whereLessThan("travel_date", calendar.getTime());

                    return query;
                }
            });
        }

        @Override
        public View getItemView(final ParseObject object, View v, ViewGroup parent) {
            if (v == null)
                v = getActivity().getLayoutInflater().inflate(R.layout.plan_item_row, null);

            super.getItemView(object, v, parent);

            RoundedParseImageView imgView = (RoundedParseImageView) v.findViewById(android.R.id.icon);
            //ParseFile imageFile = object.getParseFile("profile_picture");
            if (false) { //imageFile != null
                imgView.setParseFile(null);
                imgView.loadInBackground();
            } else {
                imgView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }

            TextView lbl = (TextView) v.findViewById(R.id.profile_name);

            lbl.setText(object.getString("origin_user_name") + " " + object.getString("to_location_name"));


            return v;
        }

    }
}