package com.codeground.adventurousbulgaria.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.codeground.adventurousbulgaria.R;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton mMountainBtn;
    private ImageButton mEcopathBtn;
    private ImageButton mCampingBtn;
    private ImageButton mCycleBtn;
    private ImageButton mLakesBtn;
    private ImageButton mFishingBtn;
    private ImageButton mBeachBtn;
    private ImageButton mWaterfallBtn;
    private ImageButton mCaveBtn;
    private ImageButton mFortressBtn;
    private ImageButton mFestivalBtn;
    private ImageButton mFoodBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mMountainBtn = (ImageButton)findViewById(R.id.cat_mountains);
        mEcopathBtn = (ImageButton)findViewById(R.id.cat_ecopath);
        mCampingBtn = (ImageButton)findViewById(R.id.cat_camping);
        mCycleBtn = (ImageButton)findViewById(R.id.cat_cycling);
        mLakesBtn = (ImageButton)findViewById(R.id.cat_lakes);
        mFishingBtn = (ImageButton)findViewById(R.id.cat_fishing);
        mBeachBtn = (ImageButton)findViewById(R.id.cat_beach);
        mWaterfallBtn = (ImageButton)findViewById(R.id.cat_waterfalls);
        mCaveBtn = (ImageButton)findViewById(R.id.cat_caves);
        mFortressBtn = (ImageButton)findViewById(R.id.cat_fortress);
        mFestivalBtn = (ImageButton)findViewById(R.id.cat_festival);
        mFoodBtn = (ImageButton)findViewById(R.id.cat_food);

        mMountainBtn.setOnClickListener(this);
        mEcopathBtn.setOnClickListener(this);
        mCampingBtn.setOnClickListener(this);
        mCycleBtn.setOnClickListener(this);
        mLakesBtn.setOnClickListener(this);
        mFishingBtn.setOnClickListener(this);
        mBeachBtn.setOnClickListener(this);
        mWaterfallBtn.setOnClickListener(this);
        mCaveBtn.setOnClickListener(this);
        mFortressBtn.setOnClickListener(this);
        mFestivalBtn.setOnClickListener(this);
        mFoodBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), AllLandmarksActivity.class);

        if(v.getId()==R.id.cat_mountains){
            i.putExtra("Category",getString(R.string.cat_mountain));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_lakes){
            i.putExtra("Category",getString(R.string.cat_lakes));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_camping){
            i.putExtra("Category",getString(R.string.cat_camping));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_ecopath){
            i.putExtra("Category",getString(R.string.cat_ecopath));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_cycling){
            i.putExtra("Category",getString(R.string.cat_cycling));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_fishing){
            i.putExtra("Category",getString(R.string.cat_fishing));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_beach){
            i.putExtra("Category",getString(R.string.cat_beach));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_waterfalls){
            i.putExtra("Category",getString(R.string.cat_waterfall));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_caves){
            i.putExtra("Category",getString(R.string.cat_caves));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_fortress){
            i.putExtra("Category",getString(R.string.cat_fortress));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_festival){
            i.putExtra("Category",getString(R.string.cat_festival));
            startActivity(i);
        }
        else if(v.getId()==R.id.cat_food){
            i.putExtra("Category",getString(R.string.cat_food));
            startActivity(i);
        }


    }
}
