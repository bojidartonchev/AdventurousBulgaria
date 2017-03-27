package com.codeground.wanderlustbulgaria.Utilities.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codeground.wanderlustbulgaria.Fragments.CategoriesFragment;
import com.codeground.wanderlustbulgaria.Fragments.NearByFragment;
import com.codeground.wanderlustbulgaria.Fragments.PlannerFragment;


public class MainMenuPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Near By", "Categories", "Planner"};

    public MainMenuPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch(pos) {

            case 0: return NearByFragment.newInstance();
            case 1: return CategoriesFragment.newInstance();
            case 2: return PlannerFragment.newInstance();
            default: return CategoriesFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
