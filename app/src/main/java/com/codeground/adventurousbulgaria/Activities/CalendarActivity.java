package com.codeground.adventurousbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import com.codeground.adventurousbulgaria.Fragments.CalendarTravellersFragment;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Adapters.CalendarTravellersAdapter;

import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener, View.OnClickListener {

    private CalendarView mCalendar;
    private CalendarTravellersFragment mFragment;
    private FloatingActionButton mAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mFragment = (CalendarTravellersFragment) getSupportFragmentManager().findFragmentById(R.id.travellers_fragment);

        //Load travellers for today
        CalendarTravellersAdapter commentsAdapter = new CalendarTravellersAdapter(getApplicationContext(), new Date());
        mFragment.setTravellersAdapter(commentsAdapter);

        mCalendar = (CalendarView) findViewById(R.id.calendarView);
        mCalendar.setOnDateChangeListener(this);

        mAddBtn = (FloatingActionButton) findViewById(R.id.add_new_traveller_btn);
        mAddBtn.setOnClickListener(this);
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, dayOfMonth);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        CalendarTravellersAdapter commentsAdapter = new CalendarTravellersAdapter(getApplicationContext(), cal.getTime());

        mFragment.setTravellersAdapter(commentsAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_new_traveller_btn){
            Intent i = new Intent(this, SubmitTravellerActivity.class);
            startActivity(i);
        }
    }
}
