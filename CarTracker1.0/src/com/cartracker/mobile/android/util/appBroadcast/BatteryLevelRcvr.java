package com.cartracker.mobile.android.util.appBroadcast;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.UsbCableHarwareListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;

/**
 * Created by jw362j on 10/2/2014.
 */
public class BatteryLevelRcvr extends BaseBroadCastRcv implements appStop {
    private Context mConext;
    @Override
    public void onSafeReceive(Context context, Intent intent) {
        mConext = context;
        checkBatteryStatus(intent);

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            /**
             * 此机制只能不断的检测到是否有线缆插入usb口 但是并不能感知到是否有usb影响设备attach到系统中,目前唯一的用途就是在CameraSettingGuideActivity中显示接线状态
             *0：usb接口线缆断开
             *1：usb接口线缆连上设备
             *2：连上了充电器
             * 此时只要不为0 就说明usb接口有线缆插入 可能是充电 也可能是相机信号线
             */
            int plugged = intent.getIntExtra("plugged", 0);
            VariableKeeper.USB_CABLE_CONNECT_STATUS = plugged;//更新线缆状态
            if(VariableKeeper.usbCableHarwareListeners != null){
                for(UsbCableHarwareListener usbCableHarwareListener:VariableKeeper.usbCableHarwareListeners){
                    usbCableHarwareListener.onCableChange(plugged,null);//通知其它组件usb线的状态
                }
            }
            SystemUtil.log("usb cable status:"+plugged);
        }
    }

    private void checkBatteryStatus(Intent intent){
        StringBuilder sb = new StringBuilder();
        int rawlevel = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);
        int status = intent.getIntExtra("status", -1);
        int health = intent.getIntExtra("health", -1);
        int level = -1; // percentage, or -1 for unknown
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale;
        }
        sb.append("The phone");
        if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
            sb.append("'s battery feels very hot!");
        } else {
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    sb.append("no battery.");
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    sb.append("'s battery");
                    if (level <= 33)
                        sb.append(" is charging, battery level is low" + "[" + level + "]");
                    else if (level <= 84)
                        sb.append(" is charging." + "[" + level + "]");
                    else
                        sb.append(" will be fully charged.");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    if (level == 0)
                        sb.append(" needs charging right away.");
                    else if (level > 0 && level <= 33)
                        sb.append(" is about ready to be recharged, battery level is low"+ "[" + level + "]");
                    else
                        sb.append("'s battery level is" + "[" + level + "]");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    sb.append(" is fully charged.");
                    break;
                default:
                    sb.append("'s battery is indescribable!");
                    break;
            }
            /**
             * 此时得到了电池的完整电量信息
             * 1: 电量低于5%的时候自动停止刻录
             */
            if (level < VariableKeeper.stop_min_battery_level) {
                mConext.sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.ACTION_USB_CAMERA_COMPLETE_STOP));
                for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {

                    if (VariableKeeper.videoCaptures[i] != null) {
                        VariableKeeper.videoCaptures[i].video_record_flag = 1;
                        VariableKeeper.videoCaptures[i].Stop();

                    }
                    if (VariableKeeper.bitMapHolderInits[i] != null) VariableKeeper.bitMapHolderInits[i].setThread_flag(0);
                }
                SystemUtil.recordStatusMonitor();
                SystemUtil.MyToast(mConext.getString(R.string.desktop_toast_recordstop_lowbattery_msg));
            }
        }
        sb.append(' ');
        SystemUtil.log("my batter status is:"+sb.toString());
    }

    @Override
    public void stopAll() {

    }
}
