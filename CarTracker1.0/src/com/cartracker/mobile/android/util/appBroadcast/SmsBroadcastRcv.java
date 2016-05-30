package com.cartracker.mobile.android.util.appBroadcast;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import com.cartracker.mobile.android.util.SystemUtil;

/**
 * Created by jw362j on 12/5/2014.
 */
public class SmsBroadcastRcv extends BaseBroadCastRcv {
    @Override
    public void onSafeReceive(Context context, Intent intent) {
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        //每一项为一条短信
        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String body = smsMessage.getMessageBody();
            String sender = smsMessage.getOriginatingAddress();
            if ("#*loaction*#".equals(body)) {
                abortBroadcast();// 终止短信的广播事件.
                SystemUtil.log( "返回手机的位置.");
//                String lastloaction = GPSInfoProvider.getInstance(context) .getPhoneLocation();
                String lastloaction = "";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sender, null, "loaction:"
                        + lastloaction, null, null);
            } else if ("#*alarm*#".equals(body)) {
                abortBroadcast();// 终止短信的广播事件.
//                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
//                player.setVolume(1.0f, 1.0f);
//                player.start();
//
                SystemUtil.log( "播放报警音乐.");
            } else if ("#*wipedate*#".equals(body)) {
                abortBroadcast();// 终止短信的广播事件.
                SystemUtil.log( "清除数据.");
                // 判断 当前应用程序是否已经得到了超级管理员的权限

//                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//                if (dpm.isAdminActive(new ComponentName(context, MyAdmin.class)))dpm.wipeData(0);

            } else if ("#*lockscreen*#".equals(body)) {
                abortBroadcast();// 终止短信的广播事件.
                SystemUtil.log( "远程锁屏.");
               /* DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (dpm.isAdminActive(new ComponentName(context, MyAdmin.class))) {
                    dpm.resetPassword("321", 0);
                    dpm.lockNow();
                }else{
                    //todo:
                }*/
            }
        }
    }
}
