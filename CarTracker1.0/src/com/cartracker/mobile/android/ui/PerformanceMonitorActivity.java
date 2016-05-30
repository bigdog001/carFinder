package com.cartracker.mobile.android.ui;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.util.SysEnvMonitor.MonitorHandler;
import com.cartracker.mobile.android.util.SystemUtil;

public class PerformanceMonitorActivity extends BaseActivity implements MonitorHandler {
    private TextView tv0, tv1, tv2, tv3, tv_now;
    private TextView tv0_, tv1_, tv2_, tv3_;
    private TextView tv10, tv11, tv12, tv13,mem_info;
    private long total_memery;
    private Button monite_parameters;
    private Button monitor_mem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_monitor_main);
        init();
    }

    private void init() {
        tv0 = (TextView) findViewById(R.id.tv0);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv0_ = (TextView) findViewById(R.id.tv0_);
        tv1_ = (TextView) findViewById(R.id.tv1_);
        tv2_ = (TextView) findViewById(R.id.tv2_);
        tv3_ = (TextView) findViewById(R.id.tv3_);
        tv10 = (TextView) findViewById(R.id.tv10);
        tv11 = (TextView) findViewById(R.id.tv11);
        tv12 = (TextView) findViewById(R.id.tv12);
        tv13 = (TextView) findViewById(R.id.tv13);
        mem_info= (TextView) findViewById(R.id.mem_info);


        monite_parameters= (Button) findViewById(R.id.monite_parameters);
        monitor_mem= (Button) findViewById(R.id.monitor_mem);


        monite_parameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
                    if (VariableKeeper.videoCaptures[i] != null) VariableKeeper.videoCaptures[i].setUiMonitorHandler(PerformanceMonitorActivity.this);
                }
            }
        });

        monitor_mem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime myRun = Runtime.getRuntime();

                String meminfor = "最大内存:" + (myRun.maxMemory())/(1024*1024)+"M,已用内存:" + (myRun.totalMemory())/(1024*1024)+"M,可用内存:" + (myRun.freeMemory())/(1024*1024)+"M";
                mem_info.setText(meminfor);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
            if (VariableKeeper.videoCaptures[i] != null) VariableKeeper.videoCaptures[i].setUiMonitorHandler(null);
        }
        SystemUtil.log("on destroy was called ,ui monitor is null");
    }

    @Override
    public void stopAll() {
        finish();
    }

    @Override
    public void Track(Context context, Object data) {
        final int[] x = (int[]) data;
        switch (x[1]) {
            case 0:
                tv_now = tv0;
                break;
            case 1:
                tv_now = tv1;
                break;
            case 2:
                tv_now = tv2;
                break;
            case 3:
                tv_now = tv3;
                break;
            default:
                break;
        }
        if (tv_now != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_now.setText(x[0] + "");

                    for (int y = 0; y < VariableKeeper.APP_CONSTANT.size_num_cam; y++) {
                        if (y == 0) {
                            tv0_.setText(VariableKeeper.bitMapHolders[y].getSize() + "");
                            if (VariableKeeper.bitMapHolderInits[y] != null) {
                                tv10.setText(VariableKeeper.bitMapHolderInits[y].getTotal_video_frame() + "");
                            }
                        }
                        if (y == 1) {
                            tv1_.setText(VariableKeeper.bitMapHolders[y].getSize() + "");
                            if (VariableKeeper.bitMapHolderInits[y] !=null ) {
                                tv11.setText(VariableKeeper.bitMapHolderInits[y].getTotal_video_frame() + "");
                            }
                        }
                        if (y == 2) {
                            tv2_.setText(VariableKeeper.bitMapHolders[y].getSize() + "");
                            if (VariableKeeper.bitMapHolderInits[y] !=null ) {
                                tv12.setText(VariableKeeper.bitMapHolderInits[y].getTotal_video_frame() + "");
                            }
                        }
                        if (y == 3) {
                            tv3_.setText(VariableKeeper.bitMapHolders[y].getSize() + "");
                            if (VariableKeeper.bitMapHolderInits[y] !=null ) {
                                tv13.setText(VariableKeeper.bitMapHolderInits[y].getTotal_video_frame() + "");
                            }
                        }
                    }
                }
            });
        }
    }
}
