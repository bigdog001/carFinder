package com.cartracker.mobile.android.util.SysEnvMonitor.impl;

import android.content.Context;
import com.cartracker.mobile.android.util.SysEnvMonitor.MonitorHandler;

/**
 * Created by jw362j on 9/21/2014.
 */
public class BitMapHolderMonitor implements MonitorHandler {
    @Override
    public void Track(Context context,Object data) {
        //监视系统中的数据缓冲对象 VariableKeeper.bitMapHolders
    }
}
