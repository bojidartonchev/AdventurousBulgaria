package com.codeground.adventurousbulgaria.Utilities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.Interfaces.IOnItemClicked;
import com.codeground.adventurousbulgaria.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LandmarksAdapter extends RecyclerView.Adapter<LandmarksAdapter.ViewHolder> {


    private List<Landmark> mAdapterData;
    private static IOnItemClicked mCaller;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mLocation;
        public ImageView mIcon;
        public RelativeLayout mParent;

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mLocation = (TextView) itemView.findViewById(R.id.location);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mParent = (RelativeLayout) itemView.findViewById(R.id.row_parent);

            mParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCaller != null) {
                        mCaller.onItemClicked(position);
                    }
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    public LandmarksAdapter(List<Landmark> data, IOnItemClicked caller) {
        this.mAdapterData = data;
        this.mCaller = caller;
    }

    @Override
    public int getItemCount() {
        return mAdapterData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_landmark_row, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder != null) {
            holder.setPosition(position);
            holder.mTitle.setText(mAdapterData.get(position).getName());
            holder.mLocation.setText(mAdapterData.get(position).getLocationName());

            //Load the icon
            Picasso.with(holder.mIcon.getContext()).load(mAdapterData.get(position).getIconURL()).into(holder.mIcon);

            //TODO CHECK IF THIS IS COMPLETED
            //holder.mParent.setBackgroundColor(Color.parseColor("#00FF00"));
        }
    }
}
