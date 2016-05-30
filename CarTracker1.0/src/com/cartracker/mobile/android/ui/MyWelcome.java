package com.cartracker.mobile.android.ui;

import android.content.Intent;
import android.os.Bundle;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.ui.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jw362j on 12/12/2014.
 */
public class MyWelcome extends BaseActivity {
    private Timer timer ;
    private TimerTask task  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                Intent intent = new Intent(MyWelcome.this, Desktop.class);
                startActivity(intent);
                MyWelcome.this.finish();
            }
        };
        //加载欢迎画面
        setContentView(R.layout.welcome_screen);
        timer.schedule(task, 2000);
    }

    @Override
    public void stopAll() {

    }
}
