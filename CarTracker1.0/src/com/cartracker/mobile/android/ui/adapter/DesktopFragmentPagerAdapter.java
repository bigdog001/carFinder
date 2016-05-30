package com.cartracker.mobile.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by jw362j on 9/29/2014.
 */
public class DesktopFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;

    public DesktopFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public DesktopFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
