package com.cartracker.mobile.android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.UsbCameraData;
import com.cartracker.mobile.android.util.appBroadcast.BaseBroadCastRcv;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;

/**
 * Created by jw362j on 9/19/2014.
 */
public class RecordManager implements appStop {
    private Thread[] threads;
    private BitMapHolder[] bitMapHolders;
    private int camera_num;
    private BroadcastReceiver br_sdcard_cleaner_complete;//此广播接受者只运行一次后即消亡

    public RecordManager() {
        VariableKeeper.addStop(this);
        if (VariableKeeper.isCardExist) {
            if (VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_video_rewrite,false)) {
                if(VariableKeeper.sdCard_CurrentFreeSize  > VariableKeeper.APP_CONSTANT.sdCard_minFreeSize){
                    record();
                }else {
                    //可以通知组件 SDCardLowerSizeCleaner 擦除旧数据 当组件擦除完毕后会发出广播
                    SystemUtil.log("当前为档案重写模式且sd卡空间不够 稍后重启刻录线程");

                    br_sdcard_cleaner_complete = new BaseBroadCastRcv() {
                        @Override
                        public void onSafeReceive(Context context, Intent intent) {
                            VariableKeeper.context.unregisterReceiver(br_sdcard_cleaner_complete);
                            br_sdcard_cleaner_complete = null;
                            SystemUtil.log("sd卡空间清理完毕 开始启动刻录线程的工作,sd卡当前的空闲空间为:"+VariableKeeper.sdCard_CurrentFreeSize+"MB");
                            VariableKeeper.context.unregisterReceiver(br_sdcard_cleaner_complete);
                            record();//存储空间已经被腾出 可以继续刻录,因为清理线程会保证有足够的空间供使用
                        }
                    };
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(VariableKeeper.APP_CONSTANT.CLEAN_SDSIZE_COMPLETE);
                    VariableKeeper.context.registerReceiver(br_sdcard_cleaner_complete,intentFilter);
                    VariableKeeper.context.sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.LOWER_FREE_SDSIZE));
                }

            }else {
                //检查sd卡剩余空间以决定是否开启刻录
                if(VariableKeeper.sdCard_CurrentFreeSize  > VariableKeeper.APP_CONSTANT.sdCard_minFreeSize){
                    record();
                }else {
                    //根据用户设定来决定是否做出声音警报
                    //TODO
                    //提示用户当前存储设备空间太低
                    if (VariableKeeper.getmCurrentActivity() != null) {
                        VariableKeeper.getmCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //弹出对话框告知用户无法录像 不仅仅是toast的提示级别 需要用户手工dismiss
                                final Dialog dialog = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity()).setTitle(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.system_sdcard_current_size_infor_title))
                                        .setMessage(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.system_sdcard_current_size_infor_lower)+VariableKeeper.sdCard_CurrentFreeSize+"MB,"+VariableKeeper.context.getResources().getString(R.string.system_sdcard_clean_remind_msg))
                                        .setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                dialog.dismiss();
                                                dialog = null;
                                            }
                                        }).create();
                                dialog.show();
                            }
                        });
                    }
                }
            }
        }else {
            SystemUtil.MyToast(VariableKeeper.context.getResources().getString(R.string.system_sdcard_status_nosdcard));
            SystemUtil.logOnFile("无SD卡");
        }
    }

    private void record(){
        if (VariableKeeper.isCardExist) {
            this.camera_num = VariableKeeper.APP_CONSTANT.size_num_cam;
            this.threads = new Thread[camera_num];
            this.bitMapHolders = VariableKeeper.bitMapHolders;
            //此时只用启动镜头处理类 然后由镜头来决定如何启动刻录机
            new Thread(new UsbCameraData(VariableKeeper.context)).start();
        }else {
            SystemUtil.MyToast("SDCard not Exist...");
        }
    }

    @Override
    public void stopAll() {

    }


}
