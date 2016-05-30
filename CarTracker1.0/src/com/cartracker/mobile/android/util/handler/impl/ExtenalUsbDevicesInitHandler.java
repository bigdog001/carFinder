package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.handler.InterfaceGen.Initializer;

/**
 * Created by jw362j on 10/6/2014.
 */
public class ExtenalUsbDevicesInitHandler implements Initializer {
    @Override
    public void init(Context context) {
//        USB_GLOBAL_VIDEO_DEVICE_QUERY
        //进入相机检测模块
        Intent camera_check_intent = new Intent();
        camera_check_intent.setAction(VariableKeeper.APP_CONSTANT.USB_DEVICE_QUERY);
        context.sendBroadcast(camera_check_intent);
    }
}
