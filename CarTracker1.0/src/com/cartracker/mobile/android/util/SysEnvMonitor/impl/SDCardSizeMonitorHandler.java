package com.cartracker.mobile.android.util.SysEnvMonitor.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SysEnvMonitor.MonitorHandler;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.Initializer;

import java.io.File;

/**
 * Created by jw362j on 9/21/2014.
 */
public class SDCardSizeMonitorHandler implements MonitorHandler ,Initializer {

    @Override
    public void Track(Context context,Object data) {
         //将此处的定时广播过来的信号转给专门的sd卡剩余空间处理类
        if(VariableKeeper.context !=null){
            VariableKeeper.sdCard_CurrentFreeSize = SystemUtil.getSDFreeSize();
            if(VariableKeeper.sdCard_CurrentFreeSize  < VariableKeeper.APP_CONSTANT.sdCard_minFreeSize){
                VariableKeeper.context.sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.LOWER_FREE_SDSIZE));
            }
        }
    }

    //初始化与sd卡相关的参数和动作
    @Override
    public void init(Context context) {
       //系统启动的时候会在此自动运行此函数以检查sd卡的状态
        VariableKeeper.isCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)?true:false;

        if (VariableKeeper.isCardExist) {
            //初始化sd卡的根路径
            VariableKeeper.sdCard_mountRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            //初始化该应用程序文件存储的根路径
            VariableKeeper.system_file_save_BaseDir = VariableKeeper.sdCard_mountRootPath+ VariableKeeper.APP_CONSTANT.base_folder_name+File.separator;
//            SystemUtil.MyToast(context.getResources().getString(R.string.system_sdcard_status_ok));
            //此时检查sd卡的空闲空间 最低2GB
            VariableKeeper.sdCard_CurrentFreeSize = SystemUtil.getSDFreeSize();
            if(VariableKeeper.sdCard_CurrentFreeSize  > VariableKeeper.APP_CONSTANT.sdCard_minFreeSize){
                SystemUtil.log("sd卡当前剩余空间:"+VariableKeeper.sdCard_CurrentFreeSize+"MB,约"+VariableKeeper.sdCard_CurrentFreeSize/(1024)+"GB");
                //系统可以正常使用sd卡进行刻录
                //TODO
            }else {
                SystemUtil.MyToast(context.getResources().getString(R.string.system_sdcard_current_size_infor_lower) + VariableKeeper.sdCard_CurrentFreeSize + "MB");
                //发出清理sd卡的广播消息
                VariableKeeper.context.sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.LOWER_FREE_SDSIZE));
            }
        }else {
            //弹出吐司告知用户系统无sd卡,此时只是提示用户 当用户点击录像的时候提示无存储设备 不能刻录只能预览
            SystemUtil.MyToast(context.getResources().getString(R.string.system_sdcard_status_nosdcard));
            SystemUtil.logOnFile("无SD卡");
        }
    }
}
