package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 10/21/2014.
 */
public class DevScannerTimerAction implements InterfaceGen.timerAction {

    //不断扫描dev下的video*是否具备0666 或者0777权限
    @Override
    public void onTime(Context c, int flag, Object data) {
        SystemUtil.DevScanner();
    }
}
