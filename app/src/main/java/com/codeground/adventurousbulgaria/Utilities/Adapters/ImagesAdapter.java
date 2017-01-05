package com.codeground.adventurousbulgaria.Utilities.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImagesAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Bitmap> sliderImages = new ArrayList<>();

    public ImagesAdapter(Context context, ArrayList<Bitmap> images) {
        this.mContext = context;
        this.sliderImages=images;
    }

    @Override
    public int getCount() {
        return sliderImages.size();
    }



    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == ((ImageView) obj);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setImageBitmap(sliderImages.get(i));
        ((ViewPager) container).addView(mImageView, 0);
        return mImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((ImageView) obj);
    }
}