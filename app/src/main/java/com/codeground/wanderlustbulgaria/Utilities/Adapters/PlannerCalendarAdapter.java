package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlannerCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Date> dateList;
    private IOnItemClicked mCb;
    private int selectedItem = -1;

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        public TextView mDayLabel;
        public TextView mDateLabel;
        public TextView mMonthLabel;

        private int mPosition;

        public CalendarViewHolder(View view) {
            super(view);
            mDayLabel = (TextView) view.findViewById(R.id.day_of_week);
            mDateLabel = (TextView) view.findViewById(R.id.date_label);
            mMonthLabel = (TextView) view.findViewById(R.id.month_label);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCb.onItemClicked(mPosition);
                    selectedItem = mPosition;
                    notifyDataSetChanged();
                }
            });
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }
    }

    public PlannerCalendarAdapter(List<Date> dates, IOnItemClicked cb) {
        this.dateList = dates;
        this.mCb = cb;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item_row, parent, false);

        return new CalendarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        CalendarViewHolder viewHolder = (CalendarViewHolder)holder;

        Date date = dateList.get(position);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = dayFormat.format(date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String dateLabelText = dateFormat.format(date);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        String monthLabelText = monthFormat.format(date);

        viewHolder.mDayLabel.setText(Character.toString(Character.toUpperCase(dayOfTheWeek.charAt(0))));
        viewHolder.mDateLabel.setText(dateLabelText);
        viewHolder.mMonthLabel.setText(monthLabelText);

        viewHolder.setPosition(position);

        if(position == selectedItem) {
            viewHolder.mDayLabel.setTextColor(Color.WHITE);
            viewHolder.mDateLabel.setTextColor(Color.WHITE);
            viewHolder.mMonthLabel.setTextColor(Color.WHITE);
        } else {
            viewHolder.mDayLabel.setTextColor(Color.parseColor("#a1a1a1"));
            viewHolder.mDateLabel.setTextColor(Color.parseColor("#a1a1a1"));
            viewHolder.mMonthLabel.setTextColor(Color.parseColor("#a1a1a1"));
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
