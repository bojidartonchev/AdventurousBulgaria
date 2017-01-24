package com.codeground.wanderlustbulgaria.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import com.codeground.wanderlustbulgaria.Fragments.CalendarTravellersFragment;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Adapters.CalendarTravellersAdapter;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseTraveller;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener, View.OnClickListener, CalendarPickerController {

    private AgendaCalendarView mCalendar;
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

        mCalendar = (AgendaCalendarView) findViewById(R.id.calendarView);
        initCalendar();

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

    private void initCalendar(){
        final List<CalendarEvent> eventList = new ArrayList<>();

        ParseRelation following = ParseUser.getCurrentUser().getRelation("following");
        ParseQuery<ParseTraveller> query = ParseQuery.getQuery("Traveller");
        query.whereMatchesQuery("origin_user", following.getQuery());
        
        query.findInBackground(new FindCallback<ParseTraveller>() {
            @Override
            public void done(List<ParseTraveller> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    final ParseObject object = objects.get(i);

                    final Calendar startTime = Calendar.getInstance();

                    Date departureDate = object.getDate("travel_date");
                    startTime.setTime(departureDate);

                    String originUserName = object.getString("origin_user_name");
                    String toLocationName = object.getString("to_location_name");
                    String title = originUserName + " travels to " + toLocationName;

                    BaseCalendarEvent event = new BaseCalendarEvent(title, "A wonderful journey!", object.getString("from_city"),
                            ContextCompat.getColor(getApplicationContext(), R.color.menuColor1), startTime, startTime, false);
                    eventList.add(event);

                }

                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();

                minDate.add(Calendar.MONTH, -2);
                minDate.set(Calendar.DAY_OF_MONTH, 1);
                maxDate.add(Calendar.YEAR, 1);
                mCalendar.init(eventList, minDate, maxDate, Locale.getDefault(), CalendarActivity.this);
            }
        });
    }

    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}
