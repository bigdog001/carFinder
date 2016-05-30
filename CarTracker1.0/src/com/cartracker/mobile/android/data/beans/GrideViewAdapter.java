package com.cartracker.mobile.android.data.beans;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by jw362j on 9/24/2014.
 */
public class GrideViewAdapter  extends BaseAdapter {

    LinearLayout[] shortcuts;

    public GrideViewAdapter(LinearLayout[] shortcuts) {
        this.shortcuts = shortcuts;
    }

    @Override
    public int getCount() {
        return shortcuts.length;
    }

    @Override
    public Object getItem(int position) {
        return shortcuts[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        return shortcuts[position];
    }

}