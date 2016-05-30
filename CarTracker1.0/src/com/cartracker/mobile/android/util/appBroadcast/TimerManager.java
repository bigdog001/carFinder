package com.cartracker.mobile.android.util.appBroadcast;

import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.impl.DevScannerTimerAction;
import com.cartracker.mobile.android.util.handler.impl.SystemGCManager;
import com.cartracker.mobile.android.util.handler.impl.fileSwitchTimerAction;
import com.cartracker.mobile.android.util.handler.impl.recordStatusTimerAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw362j on 10/12/2014.
 */


//=====================================十分重要的类 ，系统的核心心脏 每2秒都会运行 不能阻塞=====================================
public class TimerManager extends BaseBroadCastRcv {
    private List<InterfaceGen.timerAction> timerActions;

    public TimerManager() {
        timerActions = new ArrayList<InterfaceGen.timerAction>();
        initActions();
    }

    private void initActions() {
        //监视刻录线程的文件切换
        timerActions.add(new fileSwitchTimerAction());
        //检测刻录线程是否在工作
        timerActions.add(new recordStatusTimerAction());
        //不断扫描dev下的video*是否具备0666 或者0777权限
        timerActions.add(new DevScannerTimerAction());
        timerActions.add(new SystemGCManager());
    }

    //负责接收系统的时钟脉冲 以决定什么时候发生什么事件
    @Override
    public void onSafeReceive(Context context, Intent intent) {

        SystemUtil.log("脉冲到......");
        for (InterfaceGen.timerAction action : timerActions) {
            if (action != null) {
                action.onTime(context, 0, null);
            }
        }

    }


}
