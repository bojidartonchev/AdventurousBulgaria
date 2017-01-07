package com.codeground.adventurousbulgaria.Utilities.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;
import com.codeground.adventurousbulgaria.Utilities.ParseUtils.ParseComment;
import com.codeground.adventurousbulgaria.Utilities.ParseUtils.ParseTraveller;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

public class CalendarTravellersAdapter extends ParseQueryAdapter{

    public CalendarTravellersAdapter(Context context, final Date date)
    {
        super(context, new QueryFactory<ParseComment>() {
            public ParseQuery create() {
                ParseRelation following = ParseUser.getCurrentUser().getRelation("following");
                ParseQuery<ParseTraveller> query = ParseQuery.getQuery("Traveller");
                query.whereMatchesQuery("origin_user", following.getQuery());
                query.whereGreaterThanOrEqualTo("travel_date", date);

                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, 1);
                query.whereLessThan("travel_date", c.getTime());
                return query;
            }
        });
    }

    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.list_view_traveller_row, null);
        }

        super.getItemView(object, v, parent);

        //Comment creator
        TextView originUser = (TextView) v.findViewById(R.id.profile_name);
        originUser.setText("tete");

        //Comment creator
        TextView from = (TextView) v.findViewById(R.id.from_location);
        from.setText("tete");

        //Comment content
        TextView to = (TextView) v.findViewById(R.id.to_location);
        to.setText("tete");

        return v;
    }
}
