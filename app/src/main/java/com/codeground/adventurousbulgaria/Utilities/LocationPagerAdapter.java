package com.codeground.adventurousbulgaria.Utilities;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codeground.adventurousbulgaria.Fragments.LocationCommentsFragment;
import com.codeground.adventurousbulgaria.Fragments.LocationDescriptionFragment;


public class LocationPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Description", "Comments"};

    public LocationPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch(pos) {

            case 0: return LocationDescriptionFragment.newInstance();
            case 1: return LocationCommentsFragment.newInstance();
            default: return LocationDescriptionFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
