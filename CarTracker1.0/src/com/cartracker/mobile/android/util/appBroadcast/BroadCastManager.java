package com.cartracker.mobile.android.util.appBroadcast;

import android.content.BroadcastReceiver;
import android.hardware.usb.UsbManager;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.BroadcastReceiverModule;
import com.cartracker.mobile.android.util.SysEnvMonitor.MyAppMonitor;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;
import com.cartracker.mobile.android.util.handler.impl.HardWareUsbHostPermissionFile;
import com.cartracker.mobile.android.util.handler.impl.SystemGlobalUsbVideoDevicesHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw362j on 9/26/2014.
 */
public class BroadCastManager implements appStop {
    private static BroadcastReceiver br_SDCardLowerSizeCleaner;
    private static List<BroadcastReceiverModule> broadcastReceiverModules = new ArrayList<BroadcastReceiverModule>();

    public BroadCastManager() {
        VariableKeeper.addStop(this);
        InitBroadCast();
    }

    //将所有的应用层广播接受者都注册在这里统一管理
    public void InitBroadCast() {

        //系统的核心心脏
        List<String> intents4Timer = new ArrayList<String>();
        intents4Timer.add(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_UNIT_NAME);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new TimerManager(),intents4Timer,0));

        //网络状态每变化一次就调用一次
        List<String> intents4ConnectChange = new ArrayList<String>();
        intents4ConnectChange.add(VariableKeeper.APP_CONSTANT.ACTION_CONN_CHANGE);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new AutoStarter(),intents4ConnectChange,0));

        //负责清理sd卡以腾出空间
        List<String> intents4SDCardLowerSizeCleaner = new ArrayList<String>();
        intents4SDCardLowerSizeCleaner.add(VariableKeeper.APP_CONSTANT.LOWER_FREE_SDSIZE);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new SDCardLowerSizeCleaner(),intents4SDCardLowerSizeCleaner,0));//此类在清理完毕sd卡后会发出VariableKeeper.CLEAN_SDSIZE_COMPLETE这样的广播 告知其它组件开始工作


        //监视切换刻录文件的时候发出的广播,所有与该事件相关的动作都在该接收器中处理 最终会产生较多的代码
        //当切换问价的时候刻录线程估计运行很长时间了，这个状态下执行MyAppMonitor中所有的动作
        List<String> intents4MyAppMonitor = new ArrayList<String>();
        intents4MyAppMonitor.add(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_FILE_SWITCH);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new MyAppMonitor(),intents4MyAppMonitor,0));


        //监视系统电池电力
        List<String> intents4batteryChange = new ArrayList<String>();
        intents4batteryChange.add(VariableKeeper.APP_CONSTANT.ACTION_BATTERY_CHANGED);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new BatteryLevelRcvr(),intents4batteryChange,0));

        //监视usb设备的插拔情况
        List<String> intents4SystemGlobalUsbVideoDevices = new ArrayList<String>();
        intents4SystemGlobalUsbVideoDevices.add(VariableKeeper.APP_CONSTANT.USB_GLOBAL_VIDEO_DEVICE_QUERY);
        intents4SystemGlobalUsbVideoDevices.add(VariableKeeper.APP_CONSTANT.USB_DEVICE_QUERY);
        intents4SystemGlobalUsbVideoDevices.add(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intents4SystemGlobalUsbVideoDevices.add(UsbManager.ACTION_USB_DEVICE_DETACHED);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new SystemGlobalUsbVideoDevicesHandler(),intents4SystemGlobalUsbVideoDevices,0));

        //镜头授权
        List<String> intents4DevicePermission = new ArrayList<String>();
        intents4DevicePermission.add(VariableKeeper.APP_CONSTANT.ACTION_USB_PERMISSION);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new DevicePermissionHandler(),intents4DevicePermission,0));

        //监听手机是否root了
        // 检测/system/etc/permissions目录下是否有约定的文件 以及文件中是否有约定的内容
        List<String> intents4root = new ArrayList<String>();
        intents4root.add(VariableKeeper.APP_CONSTANT.ACTION_SYSTEM_ROOT_NOTIFY);
        broadcastReceiverModules.add(new BroadcastReceiverModule(new HardWareUsbHostPermissionFile(),intents4root,0));


        //如果sim卡可用就注册短信监视器
        if (SystemUtil.isSimCardAvaiable()) {
            List<String> intents4sms = new ArrayList<String>();
            intents4sms.add(VariableKeeper.APP_CONSTANT.ACTION_SMS_RCV);
            broadcastReceiverModules.add(new BroadcastReceiverModule(new SmsBroadcastRcv(),intents4sms,1000));
        }
        regist();
    }

    private void regist() {
        for(BroadcastReceiverModule brm:broadcastReceiverModules){
            VariableKeeper.context.registerReceiver(brm.getBr(),brm.getIntentFilter());
        }
    }


    @Override
    public void stopAll() {
        for(BroadcastReceiverModule brm:broadcastReceiverModules){
            VariableKeeper.context.unregisterReceiver(brm.getBr());
        }
    }
}
