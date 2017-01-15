package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.ParseUtils.ParseLocation;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LandmarksAdapter extends RecyclerView.Adapter<LandmarksAdapter.ViewHolder> {

    private List<ParseLocation> mAdapterData;

    private static IOnItemClicked mCaller;

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mLocation;
        public ImageView mIcon;
        public TextView mCompletedHolder;
        public RelativeLayout mParent;


        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mLocation = (TextView) itemView.findViewById(R.id.location);
            mIcon = (ImageView) itemView.findViewById(R.id.activity_image);
            mParent = (RelativeLayout) itemView.findViewById(R.id.row_parent);
            mCompletedHolder = (TextView) itemView.findViewById(R.id.completed_container);

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

    public LandmarksAdapter(List<ParseLocation> data, IOnItemClicked caller,Context ctx) {
        this.mContext = ctx;
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
            holder.mLocation.setText(mAdapterData.get(position).getCity());
            if(position%2!=0)

                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.menuColor3));
            else if(position%2==0)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.menuColor5));

            //Load the icon
            try {
                Picasso.with(holder.mIcon.getContext()).load(mAdapterData.get(position).getIcon().getFile()).into(holder.mIcon);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}