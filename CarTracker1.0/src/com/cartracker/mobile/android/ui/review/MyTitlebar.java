package com.cartracker.mobile.android.ui.review;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cartracker.mobile.android.R;

/**
 * Created by jw362j on 9/28/2014.
 */
public class MyTitlebar {
    private Context mContext;
    private View record_view_titlebar;
    private TextView tv_title;
    private Button titlebar_left_btn;
    private Button titlebar_right_btn;

    public MyTitlebar(Context mContext) {
        this.mContext = mContext;
        record_view_titlebar = View.inflate(mContext, R.layout.view_titlebar, null);
        tv_title = (TextView) record_view_titlebar.findViewById(R.id.title_value);
        titlebar_left_btn = (Button) record_view_titlebar.findViewById(R.id.titlebar_left_btn);
        titlebar_right_btn = (Button) record_view_titlebar.findViewById(R.id.titlebar_right_btn);
    }

    public View getRecord_view_titlebar() {
        return record_view_titlebar;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setLeftBtnShow(boolean isshow) {
        if (isshow) {
            titlebar_left_btn.setVisibility(View.VISIBLE);
        } else {
            titlebar_left_btn.setVisibility(View.GONE);
        }
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public void setTextRightBtn(String text){
        titlebar_right_btn.setText(text);
    }

    public void setRightBtnOnclickListener(View.OnClickListener click){
        titlebar_right_btn.setOnClickListener(click);
    }

    public void setShowRightBtn(boolean isshow) {
        if (isshow) {
            titlebar_right_btn.setVisibility(View.VISIBLE);
        }else {
            titlebar_right_btn.setVisibility(View.GONE);
        }
    }




    public void setLeftBtnOnclickListener(View.OnClickListener click) {
        titlebar_left_btn.setOnClickListener(click);
    }

}
