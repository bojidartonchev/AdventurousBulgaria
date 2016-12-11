package com.codeground.adventurousbulgaria.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codeground.adventurousbulgaria.R;

import java.util.List;

public class ProfileAdapter extends BaseAdapter {

    private List<ProfileInfo> mData;
    private Context mContext;
    private LayoutInflater inflator;

    public ProfileAdapter(Context context, List<ProfileInfo> data)
    {
        this.mContext=context;
        this.mData=data;
        this.inflator= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ProfileInfoHolder mHolder;
        View v = convertView;
        if (convertView == null)
        {
            mHolder = new ProfileInfoHolder();
            v = inflator.inflate(R.layout.list_view_profile_data_row, null);
            mHolder.mName=(TextView) v.findViewById(R.id.profile_data_name);
            mHolder.mValue=(TextView) v.findViewById(R.id.profile_data_value);
            v.setTag(mHolder);
        }
        else
        {
            mHolder = (ProfileInfoHolder) v.getTag();
        }
        mHolder.mName.setText(mData.get(position).mName);
        mHolder.mValue.setText(mData.get(position).mValue);

        return v;
    }

    private class ProfileInfoHolder{
        private TextView mName;
        private TextView mValue;
    }


}