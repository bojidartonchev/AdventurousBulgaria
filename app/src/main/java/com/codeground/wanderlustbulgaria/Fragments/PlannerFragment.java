package com.codeground.wanderlustbulgaria.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeground.wanderlustbulgaria.Activities.SubmitTravellerActivity;
import com.codeground.wanderlustbulgaria.R;
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

public class PlannerFragment extends Fragment implements View.OnClickListener, CalendarPickerController {

    private AgendaCalendarView mCalendar;
    private FloatingActionButton mAddBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_planner, container, false);

        mCalendar = (AgendaCalendarView) v.findViewById(R.id.calendarView);
        initCalendar();

        mAddBtn = (FloatingActionButton) v.findViewById(R.id.add_new_traveller_btn);
        mAddBtn.setOnClickListener(this);
        return v;
    }

    public static PlannerFragment newInstance() {
        PlannerFragment f = new PlannerFragment();
        return f;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.add_new_traveller_btn){
            Intent i = new Intent(getActivity(), SubmitTravellerActivity.class);
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
                if(e!=null || objects ==null){
                    return;
                }

                for (int i = 0; i < objects.size(); i++) {
                    final ParseObject object = objects.get(i);

                    final Calendar startTime = Calendar.getInstance();

                    Date departureDate = object.getDate("travel_date");
                    startTime.setTime(departureDate);

                    String originUserName = object.getString("origin_user_name");
                    String toLocationName = object.getString("to_location_name");
                    String title = originUserName + " travels to " + toLocationName;

                    BaseCalendarEvent event = new BaseCalendarEvent(title, "A wonderful journey!", object.getString("from_city"),
                            ContextCompat.getColor(getActivity(), R.color.menuColor1), startTime, startTime, false);
                    eventList.add(event);

                }

                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();

                minDate.add(Calendar.MONTH, -2);
                minDate.set(Calendar.DAY_OF_MONTH, 1);
                maxDate.add(Calendar.YEAR, 1);
                mCalendar.init(eventList, minDate, maxDate, Locale.getDefault(), PlannerFragment.this);
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