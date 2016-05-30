package com.cartracker.mobile.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.ui.review.MyTitlebar;

/**
 * Created by jw362j on 10/17/2014.
 */
public class RemoteControlActivity extends BaseActivity {
    private View contentView;
    private MyTitlebar rvt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(RemoteControlActivity.this, R.layout.remote_control, null);
        setContentView(contentView);
        LinearLayout detect_view_title = (LinearLayout) findViewById(R.id.record_view_titlebar);
        initTitle();
        detect_view_title.addView(rvt.getRecord_view_titlebar());
        initView();
    }

    private void initView() {

    }

    private void initTitle() {
        rvt = new MyTitlebar(RemoteControlActivity.this);
        rvt.setLeftBtnShow(true);
        rvt.setTitle(getResources().getString(R.string.remote_control_bar_title));
        rvt.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void stopAll() {

    }
}
