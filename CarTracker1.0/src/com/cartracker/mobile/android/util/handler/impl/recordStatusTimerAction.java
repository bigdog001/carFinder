package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 10/21/2014.
 */
public class recordStatusTimerAction implements InterfaceGen.timerAction  {
    @Override
    public void onTime(Context c, int flag, Object data) {
        SystemUtil.recordStatusMonitor();//检测刻录线程是否在工作
    }
}
