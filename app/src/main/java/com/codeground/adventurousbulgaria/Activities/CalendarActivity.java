package com.codeground.adventurousbulgaria.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;

import com.codeground.adventurousbulgaria.Fragments.CalendarTravellersFragment;
import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.Adapters.CalendarTravellersAdapter;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private CalendarView mCalendar;
    private CalendarTravellersFragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mFragment = (CalendarTravellersFragment) getSupportFragmentManager().findFragmentById(R.id.travellers_fragment);


        mCalendar = (CalendarView) findViewById(R.id.calendarView);
        mCalendar.setOnDateChangeListener(this);
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
}
