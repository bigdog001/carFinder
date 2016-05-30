package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 10/21/2014.
 */
public class fileSwitchTimerAction implements InterfaceGen.timerAction {

    @Override
    public void onTime(Context c,int flag,Object data) {
        fileSwitch(c);
    }

    private void fileSwitch(Context context) {
        //每隔 INTERVAL_FILESWITCH  的时间就发送TIMER_BROADCAST_FILE_SWITCH广播
        if (System.currentTimeMillis() - VariableKeeper.system_last_FILE_SWITCH_Time > VariableKeeper.APP_CONSTANT.INTERVAL_FILESWITCH) {
            VariableKeeper.system_last_FILE_SWITCH_Time = System.currentTimeMillis();
            context.sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_FILE_SWITCH));
        }
    }
}
