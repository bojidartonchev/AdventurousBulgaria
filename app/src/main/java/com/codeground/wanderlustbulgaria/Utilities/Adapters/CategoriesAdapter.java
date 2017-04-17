package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codeground.wanderlustbulgaria.Interfaces.IOnItemClicked;
import com.codeground.wanderlustbulgaria.R;
import com.codeground.wanderlustbulgaria.Utilities.Category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Category> categoryList;
    private IOnItemClicked mCb;

    public class SpecifiedViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mCount;
        public ImageView mIcon;

        private int mPosition;

        public SpecifiedViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.category_name);
            mCount = (TextView) view.findViewById(R.id.category_count);
            mIcon = (ImageView) view.findViewById(R.id.category_icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCb.onItemClicked(mPosition);
                }
            });
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }
    }

    public class NearByViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public ImageView mIcon;

        private int mPosition;

        public NearByViewHolder(View view) {
            super(view);
            mName = (TextView) view.findViewById(R.id.category_name);
            mIcon = (ImageView) view.findViewById(R.id.category_icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCb.onItemClicked(mPosition);
                }
            });
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }
    }

    public CategoriesAdapter(List<Category> categoryList, IOnItemClicked cb) {
        this.categoryList = categoryList;
        this.mCb = cb;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categories_list_row, parent, false);

        switch (viewType){
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.categories_near_by_list_row, parent, false);
                return new NearByViewHolder(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.categories_list_row, parent, false);

                return new SpecifiedViewHolder(itemView);
            case 2:
                break;
        }

        return new SpecifiedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                NearByViewHolder viewHolder0 = (NearByViewHolder)holder;
                Category nearByCat = categoryList.get(position);
                viewHolder0.mName.setText(nearByCat.getName());
                viewHolder0.mIcon.setImageResource(nearByCat.getIcon());
                break;

            case 1:
                SpecifiedViewHolder viewHolder2 = (SpecifiedViewHolder)holder;

                Category cat = categoryList.get(position);
                viewHolder2.mName.setText(cat.getName());
                viewHolder2.mCount.setText(Long.toString(cat.getCount()));
                viewHolder2.mIcon.setImageResource(cat.getIcon());

                viewHolder2.setPosition(position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return categoryList.get(position).getType().getNumericType();
    }
}
