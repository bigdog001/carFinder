package com.cartracker.mobile.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import com.cartracker.mobile.android.ui.base.BaseActivity;

/**
 * Created by jw362j on 9/29/2014.
 */
public class NotifucationPView extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(getActivity());
        tv.setText("看到通知了");
        setContentView(tv);
    }

    @Override
    public void stopAll() {

    }
}
