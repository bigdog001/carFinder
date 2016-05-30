package com.cartracker.mobile.android.util.SysEnvMonitor;

import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SysEnvMonitor.impl.BitMapHolderMonitor;
import com.cartracker.mobile.android.util.SysEnvMonitor.impl.MemeryMonitorHandler;
import com.cartracker.mobile.android.util.SysEnvMonitor.impl.SDCardSizeMonitorHandler;
import com.cartracker.mobile.android.util.SysEnvMonitor.impl.VideoCaptureThreadMonitor;
import com.cartracker.mobile.android.util.appBroadcast.BaseBroadCastRcv;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw362j on 9/21/2014.
 */
public class MyAppMonitor extends BaseBroadCastRcv implements appStop {

    private List<MonitorHandler> monitorHandlers;

    public MyAppMonitor() {
        VariableKeeper.addStop(this);
        monitorHandlers = new ArrayList<MonitorHandler>();
        initMonitorHandler();
    }

    @Override
    public void onSafeReceive(Context context, Intent intent) {
        for (MonitorHandler monitorHandler:monitorHandlers) {
            if(monitorHandler != null) monitorHandler.Track(VariableKeeper.context,null);
        }
    }

    private void initMonitorHandler(){
        monitorHandlers.add(new SDCardSizeMonitorHandler());
        monitorHandlers.add(new MemeryMonitorHandler());
        monitorHandlers.add(new VideoCaptureThreadMonitor());
        monitorHandlers.add(new BitMapHolderMonitor());

    }

    @Override
    public void stopAll() {
        if (monitorHandlers != null) {
            for (MonitorHandler mh :monitorHandlers) {
                mh = null;
            }
            monitorHandlers.clear();
            monitorHandlers = null;
        }
    }


}
