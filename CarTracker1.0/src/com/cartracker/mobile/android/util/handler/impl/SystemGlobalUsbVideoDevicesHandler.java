package com.cartracker.mobile.android.util.handler.impl;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.UsbCameraDevice;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.appBroadcast.BaseBroadCastRcv;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.InterfaceGen.UsbCableHarwareListener;

import java.util.Map;

/**
 * Created by jw362j on 10/4/2014.
 */

//探测系统中所有的usb视频设备所挂载的目录 当软件一启动立即会探测出来并保存在VariableKeeper.SystemGlobalUsbVideoDevices中
public class SystemGlobalUsbVideoDevicesHandler extends BaseBroadCastRcv implements Runnable,InterfaceGen.appStop {

    private UsbDevice usbDevice;
    private UsbManager mUsbManager;
    private String deviceName_now;
    private int deviceid_now;
    private int vendorId_now;
    private int productId_now;
    private String action;
    private PendingIntent mPermissionIntent;

    @Override
    public void stopAll() {
        if (usbDevice != null) usbDevice = null;
        if (mUsbManager != null) mUsbManager = null;
    }

    //用户点击按钮发出的usb设备检测请求也由此接受者来处理 此时应该区分哪些是系统发出的 哪些是用户点击后手工发出的
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onSafeReceive(Context context, Intent intent) {
        action = intent.getAction();
//        监视/dev/video*
        detectAllUsbVideoDevices();
        //在VariableKeeper.minAndroidVersion =12的版本以下的话 此函数中的api无法使用
        if (VariableKeeper.VERSION_SDK_INT > VariableKeeper.APP_CONSTANT.minAndroidVersion) {
            mPermissionIntent = PendingIntent.getBroadcast(VariableKeeper.getmCurrentActivity(), 0, new Intent(VariableKeeper.APP_CONSTANT.ACTION_USB_PERMISSION), 0);
            //系统中仅仅只维持四台usb视频设备的引用 如果插入第五台摄像头则不做处理 提示用户第五台摄像头废弃
            SystemUtil.log("usb action:" + action);
            mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

            //=======================此次插入或者拔出的设备信息=======================
            usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (usbDevice != null) {
                deviceName_now = usbDevice.getDeviceName();
                deviceid_now = usbDevice.getDeviceId();
                vendorId_now = usbDevice.getVendorId();
                productId_now = usbDevice.getProductId();
            }
            if (usbDevice != null && UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);//对发现的设备进行用户授权
            //=======================此次插入或者拔出的设备信息=======================

            new Thread(SystemGlobalUsbVideoDevicesHandler.this).start();
        }else {
            if (VariableKeeper.usbCableHarwareListeners != null) {
                for (UsbCableHarwareListener listener : VariableKeeper.usbCableHarwareListeners) {
                    if (listener != null) listener.onCableChange(4, "系统版本过低!");
                }
            }
        }
    }

    private void detectAllUsbVideoDevices(){
       new Thread(new Runnable() {
           @Override
           public void run() {
               //==================监视/dev/video*=================
               SystemUtil.DevScanner();
               if (VariableKeeper.usbCableHarwareListeners != null) {
                   //通知哪些在监听/dev/video*的组件更新
                   for (InterfaceGen.UsbCableHarwareListener uchl : VariableKeeper.usbCableHarwareListeners) {
                       if (uchl != null) uchl.onCableChange(3, null);
                   }
               }
               //==================监视/dev/video*=================
           }
       }).start();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void detectCurrentUsbDevices() {
        //检测出系统当前所接的所有usb设备
        for (UsbDevice device : mUsbManager.getDeviceList().values()) {
            final String deviceName = device.getDeviceName();
            int deviceid = device.getDeviceId();
            int vendorId = device.getVendorId();
            int productId = device.getProductId();

            if (!VariableKeeper.usbDevices_All.containsKey(vendorId + "" + productId)) {
                //收纳
                VariableKeeper.usbDevices_All.put(vendorId + "" + productId,  new UsbCameraDevice(0, "", vendorId, productId, deviceName, deviceid));
            }
        }
    }


    private void cleanUsbVideoDevices() {
        if (VariableKeeper.usbDevices_All != null)
        for (Map.Entry<String, UsbCameraDevice> entry : VariableKeeper.usbDevices_All.entrySet()) {
            final UsbCameraDevice ucd_tmp = entry.getValue();
            if (ucd_tmp.getDeviceName().contains("video")) {
                if (!isCurrentlyExistVideoDevice(ucd_tmp)) {
                    //超过四台usb相机后 剩余的无法再纳入到VariableKeeper.usbCameraDevices中了
                    addCameraToCurrentVideoDeviceLists(ucd_tmp);
                }
            }
        }
        //至此已经迭代出所有的视频设备 从其中选出四台存入VariableKeeper.usbCameraDevices中,直至存满
    }


    @Override
    public void run() {
        if (VariableKeeper.APP_CONSTANT.USB_DEVICE_QUERY.equals(action)) {
            //检测出系统当前所接的所有usb设备
            //用户点击按钮发出的usb设备检测请求
            detectCurrentUsbDevices();
            //至此 所有的usb设备都已经被成功收纳,现在开始整理出所有的视频设备 即usb摄像头
            cleanUsbVideoDevices();
        } else {
            //系统发出的intent 证明usb设备发生变动
            //显示当前系统插入USB设备
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                VariableKeeper.isVideoDirScannable = true;//这样下次新的单位时间到来的时候就会自动扫描这个usb的/dev/video*目录 更改其权限为0666
                detectCurrentUsbDevices();
                //至此 所有的usb设备都已经被成功收纳,现在开始整理出所有的视频设备 即usb摄像头
                cleanUsbVideoDevices();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (VariableKeeper.usbDevices_All.containsKey(vendorId_now + "" + productId_now)) {
                    VariableKeeper.usbDevices_All.remove(vendorId_now + "" + productId_now);
                }
                cleanUsbVideoDevices();
            }

        }


    }


    private void addCameraToCurrentVideoDeviceLists(UsbCameraDevice usbCameraDevice) {
        for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
            if (VariableKeeper.extendedUsbCameraDevices[i] == null) {
                usbCameraDevice.setId(i);
                VariableKeeper.extendedUsbCameraDevices[i] = usbCameraDevice;
            }
        }
    }

    private boolean isCurrentlyExistVideoDevice(UsbCameraDevice usbCameraDevice) {
        boolean result = false;
        for (UsbCameraDevice ucd : VariableKeeper.extendedUsbCameraDevices) {
            if (ucd.getVendorId() == usbCameraDevice.getVendorId() && ucd.getProductId() == usbCameraDevice.getProductId()) {
                result = true;
                break;
            }
        }
        return result;
    }
}
