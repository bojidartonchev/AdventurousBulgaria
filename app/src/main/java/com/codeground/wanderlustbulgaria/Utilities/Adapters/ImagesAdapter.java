package com.codeground.wanderlustbulgaria.Utilities.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.ArrayList;

public class ImagesAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<ParseFile> sliderImages = new ArrayList<>();

    public ImagesAdapter(Context context, ArrayList<ParseFile> images) {
        this.mContext = context;
        this.sliderImages=images;
    }

    @Override
    public int getCount() {
        return sliderImages.size();
    }



    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ParseImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ParseImageView mImageView = new ParseImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setParseFile(sliderImages.get(i));
        mImageView.loadInBackground();
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}