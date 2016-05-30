package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 11/20/2014.
 */
public class SystemGCManager implements InterfaceGen.timerAction {
    @Override
    public void onTime(Context c, int flag, Object data) {
        //就清理内存而已
        SystemUtil.MyGC();
    }
}
