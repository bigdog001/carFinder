package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.CameraRecordStatus;
import com.cartracker.mobile.android.data.beans.UsbCameraDevice;
import com.cartracker.mobile.android.util.BitMapHolder;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.InterfaceGen.Initializer;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RecordThreadStatusListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jw362j on 10/4/2014.
 */
public class VariableInit implements Initializer {
    @Override
    public void init(Context context) {
        VariableKeeper.mSp = context.getSharedPreferences(VariableKeeper.APP_CONSTANT.sp_holder_filename, Context.MODE_PRIVATE) ;//初始化sp
        VariableKeeper.system_startupTime = System.currentTimeMillis();//初始化系统启动的时间点
        VariableKeeper.system_lastGC_ExecuteTime = VariableKeeper.system_startupTime;//假定在系统启动后的VariableKeeper.system_lastGC_DeltaTime时间长度后就允许执行gc

        VariableKeeper.app_path = context.getFilesDir().getAbsolutePath() + File.separator;
        VariableKeeper.busybox_cmd_name = "busybox";

        VariableKeeper.threadStatusListeners = new ArrayList<RecordThreadStatusListener>();
        VariableKeeper.speedChanges = new ArrayList<InterfaceGen.onSpeedChange>();
        SystemUtil.log("app path安装路径为:" + VariableKeeper.app_path);

        VariableKeeper.simStatus = SystemUtil.isSimCardAvaiable();

        VariableKeeper.usbCableHarwareListeners = new ArrayList<InterfaceGen.UsbCableHarwareListener>();
        VariableKeeper.extendedUsbCameraDevices = new UsbCameraDevice[VariableKeeper.APP_CONSTANT.size_num_cam];
        VariableKeeper.usbDevices_All = new HashMap<String, UsbCameraDevice>();
        VariableKeeper.fromid = VariableKeeper.context.getResources().getString(R.string.fromid);

        String packageName = VariableKeeper.context.getPackageName();
        PackageManager pm = VariableKeeper.context.getPackageManager();
        PackageInfo p_info = null;
        try {
            p_info = pm.getPackageInfo(packageName, 0);
            if(p_info != null) VariableKeeper.appVersionName  = p_info.versionName ;
        } catch (PackageManager.NameNotFoundException e) {
            SystemUtil.log(e.getMessage());
        }
        //探测android的各种版本参数以及手机的硬件参数 并初始化到VariableKeeper中==================
         /**
          *
       * Build.VERSION_CODES
    1 (0x00000001)           Android 1.0             BASE
    2 (0x00000002)           Android 1.1             BASE_1_1
    3 (0x00000003)           Android 1.5             CUPCAKE
    4 (0x00000004)           Android 1.6             DONUT
    5 (0x00000005)           Android 2.0             ECLAIR
    6 (0x00000006)           Android 2.0.1          ECLAIR_0_1
    7 (0x00000007)           Android 2.1             ECLAIR_MR1
    8 (0x00000008)           Android 2.2             FROYO
    9 (0x00000009)           Android 2.3             GINGERBREAD
    10 (0x0000000a)         Android 2.3.3          GINGERBREAD_MR1
    11 (0x0000000b)         Android 3.0             HONEYCOMB
    12 (0x0000000c)         Android 3.1             HONEYCOMB_MR1
    13 (0x0000000d)         Android 3.2             HONEYCOMB_MR2

    */
        VariableKeeper.VERSION_SDK_INT = android.os.Build.VERSION.SDK_INT;
        VariableKeeper.Version_RELEASE = android.os.Build.VERSION.RELEASE;
        VariableKeeper.Build_MODEL = android.os.Build.MODEL;
        VariableKeeper.VERSION_SDK = android.os.Build.VERSION.SDK;
        SystemUtil.log("os version:VERSION_SDK_INT:" + VariableKeeper.VERSION_SDK_INT +
                        ",Version_RELEASE:" + VariableKeeper.Version_RELEASE +
                        ",Build_MODEL:" + VariableKeeper.Build_MODEL +
                        ",VERSION_SDK:" + VariableKeeper.VERSION_SDK
        );
        //探测android的各种版本参数以及手机的硬件参数 并初始化到VariableKeeper中==================


        //初始化网卡地址
        if (context != null) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    VariableKeeper.mac = info.getMacAddress();
                }
            }
        }


        VariableKeeper.recordStatuses = new CameraRecordStatus[VariableKeeper.APP_CONSTANT.size_num_cam];
        for (int i = 0;i < VariableKeeper.APP_CONSTANT.size_num_cam;i++) {
            VariableKeeper.bitMapHolders[i] = new BitMapHolder();
            VariableKeeper.recordStatuses[i] = CameraRecordStatus.START;//在画面刻录前会依次判断对应的镜头采集到的画面是否将被刻录 如不被刻录即只在显示的时候使用  升级版本会采取关闭镜头的做法
        }

        //=====================初始化硬件参数=======================
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
         /*
          * 唯一的设备ID：
          * GSM手机的 IMEI 和 CDMA手机的 MEID.
          * Return null if device ID is not available.
          */
        VariableKeeper.IMEI = tm.getDeviceId();//String
        //=====================初始化硬件参数=======================

        //初始化画面录制的清晰度指标参数
        VariableKeeper.video_record_quality = VariableKeeper.mSp.getInt(VariableKeeper.APP_CONSTANT.video_record_quality_sp_name,100);//默认画面高清



    }
}
