package com.cartracker.mobile.android.ui.root;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.ui.review.MyTitlebar;
import com.cartracker.mobile.android.util.SystemUtil;

/**
 * Created by jw362j on 10/2/2014.
 */
public class RootGuideActivity extends BaseActivity implements View.OnClickListener{
    private View contentViewRootGuideActivity;
    private MyTitlebar rvt;
    private Button device_root_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//titlebar_container
        super.onCreate(savedInstanceState);
        contentViewRootGuideActivity = View.inflate(RootGuideActivity.this, R.layout.root_guide, null);
        setContentView(contentViewRootGuideActivity);
        LinearLayout detect_view_title = (LinearLayout) findViewById(R.id.record_view_titlebar);
        initTitle();
        detect_view_title.addView(rvt.getRecord_view_titlebar());
        initViewRootActivity();
    }

    private void initViewRootActivity() {
        device_root_btn = (Button) contentViewRootGuideActivity.findViewById(R.id.device_root_btn);
        device_root_btn.setTag("device_root_btn");
        device_root_btn.setOnClickListener(RootGuideActivity.this);
    }

    private void initTitle(){
        rvt = new MyTitlebar(RootGuideActivity.this);
        rvt.setTitle(getResources().getString(R.string.title_root_guide));
        rvt.setLeftBtnShow(true);
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

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (!"".equals(tag) && tag != null) {
            tag = tag.trim();
            if("device_root_btn".equals(tag)){
                //启动下载root工具的网页
                SystemUtil.runBrowser(getResources().getString(R.string.root_software_download_url));
            }
        }
    }
}
