package com.cartracker.mobile.android.util.libUsb;

import com.cartracker.mobile.android.util.SystemUtil;

/**
 * Created by jw362j on 10/10/2014.
 */
public class LibUsbConnector {
    static {
        System.loadLibrary("usb");
        SystemUtil.log("usb moduel loaded...");
    }
    public native void ListUsbDevices(String [] devices);

}
