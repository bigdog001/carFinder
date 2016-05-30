package com.cartracker.mobile.android.util.appBroadcast;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;

/**
 * Created by jw362j on 10/6/2014.
 */
public class DevicePermissionHandler extends BaseBroadCastRcv implements Runnable{
    private  UsbDevice device;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onSafeReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (VariableKeeper.APP_CONSTANT.ACTION_USB_PERMISSION.equals(action)&&VariableKeeper.VERSION_SDK_INT >= VariableKeeper.APP_CONSTANT.minAndroidVersion) {
            //
            synchronized (this) {
                //device即为本次接入的usb设备
                device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                    if(device != null){
                        //call method to set up device communication
                        SystemUtil.MyToast("本次授权成功,"+device.getDeviceName()+" !");
                        //授权成功后再次对此设备进行0666授权
                        //new Thread(DevicePermissionHandler.this).start();
                    }
                }else{
                    SystemUtil.log( "permission denied for device " + device);
                }
            }
        }
    }

    @Override
    public void run() {

    }
}
