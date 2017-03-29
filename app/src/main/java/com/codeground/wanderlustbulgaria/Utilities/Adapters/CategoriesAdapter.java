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

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {
    private List<Category> categoryList;
    private IOnItemClicked mCb;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mCount;
        public ImageView mIcon;

        private int mPosition;

        public MyViewHolder(View view) {
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

    public CategoriesAdapter(List<Category> categoryList, IOnItemClicked cb) {
        this.categoryList = categoryList;
        this.mCb = cb;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categories_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category cat = categoryList.get(position);
        holder.mName.setText(cat.getName());
        holder.mCount.setText(Integer.toString(21));

        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
