package com.codeground.adventurousbulgaria.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.codeground.adventurousbulgaria.R;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mMountainBtn;
    private Button mEcopathBtn;
    private Button mCampingBtn;
    private Button mCycleBtn;
    private Button mLakesBtn;
    private Button mFishingBtn;
    private Button mBeachBtn;
    private Button mWaterfallBtn;
    private Button mCaveBtn;
    private Button mFortressBtn;
    private Button mFestivalBtn;
    private Button mFoodBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mMountainBtn = (Button)findViewById(R.id.cat_mountains);
        mEcopathBtn = (Button)findViewById(R.id.cat_ecopath);
        mCampingBtn = (Button)findViewById(R.id.cat_camping);
        mCycleBtn = (Button)findViewById(R.id.cat_cycling);
        mLakesBtn = (Button)findViewById(R.id.cat_lakes);
        mFishingBtn = (Button)findViewById(R.id.cat_fishing);
        mBeachBtn = (Button)findViewById(R.id.cat_beach);
        mWaterfallBtn = (Button)findViewById(R.id.cat_waterfalls);
        mCaveBtn = (Button)findViewById(R.id.cat_caves);
        mFortressBtn = (Button)findViewById(R.id.cat_fortress);
        mFestivalBtn = (Button)findViewById(R.id.cat_festival);
        mFoodBtn = (Button) findViewById(R.id.cat_food);

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
