package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.ShellUtils;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.appBroadcast.BaseBroadCastRcv;
import com.cartracker.mobile.android.util.handler.InterfaceGen.PermissionFileHandlerListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RootResultListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.ShellExeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw362j on 10/7/2014.
 */
public class HardWareUsbHostPermissionFile extends BaseBroadCastRcv implements  Runnable, RootResultListener {
    private PermissionFileHandlerListener permissionFileHandlerListener;
    private Context mContext;
    private Object locker;
    private boolean isXmlExist = false;

    public void setPermissionFileHandlerListener(PermissionFileHandlerListener permissionFileHandlerListener) {
        this.permissionFileHandlerListener = permissionFileHandlerListener;
    }

    /**
     * 1:put the file android.hardware.usb.host.xml into directory :/system/etc/permissions
     * 2:insert the content: <feature name="android.hardware.usb.host" />  ,
     * into the file (handheld_core_hardware.xml or tablet_core_hardware.xml) if they do not have the content value.
     * 3:then restart the device
     *
     */

    //进入软件桌面 发现手机被root后会立即收到此消息
    @Override
    public void onSafeReceive(Context context, Intent intent) {
        this.mContext = context;
        this.locker = new Object();
        this.permissionFileHandlerListener = new PermissionFileHandlerListener() {
            @Override
            public void onPermission(boolean flag, Object data) {
                SystemUtil.log("copyFromAssets2AnyDir result:" + flag + ",msg:" + data);
            }
        };
        //此线程非常危险 有可能会损坏设备 所有的步骤要确保正确
        new Thread(HardWareUsbHostPermissionFile.this).start();
    }



    @Override
    public void run() {
        synchronized (locker) {
            //这些工作能够进行的前提是设备具备root权限
            SystemUtil.getRootStatus(HardWareUsbHostPermissionFile.this);
        }
    }

    @Override
    public void onRootResult(int execCode, Object data) {
        //此函数被调用的唯一场景是Desktop类中的onRootResult得到0值 即系统一定是root的才会调用这个函数
        //此函数也是在子线程中被调用的 可以做大运算
           if(VariableKeeper.APP_CONSTANT.system_permission_host_fileName_copy_flag) {
               usbHostXmlHandler();
               handHeldCoreXmlHandler();
           }
    }

    //确保/system/etc/permissions/目录下是有android.hardware.usb.host.xml文件,如果不存在这个文件 那么就将assets目录下的android.hardware.usb.host.xml文件拷贝到/system/etc/permissions/目录下
    private void usbHostXmlHandler() {
//        File f_host = new File(VariableKeeper.system_permission_directory + VariableKeeper.system_permission_host_fileName);
        String ls_cmd = "";
        if (VariableKeeper.isUsingBusyBox) {
            ls_cmd = VariableKeeper.app_path + VariableKeeper.busybox_cmd_name + " ls " + VariableKeeper.APP_CONSTANT.system_permission_directory ;
        } else {
            ls_cmd = "ls " + VariableKeeper.APP_CONSTANT.system_permission_directory;
        }
        SystemUtil.exe(ls_cmd,new ShellExeListener() {
            @Override
            public void onExec(List<String> execResult) {
                for(String line:execResult){
                    if(line != null ){
                        if(line.contains(VariableKeeper.APP_CONSTANT.system_permission_host_fileName)){
                            isXmlExist = true;
                            break;
                        }
                    }
                }
                if (!isXmlExist) {
                    SystemUtil.log("文件:" + VariableKeeper.APP_CONSTANT.system_permission_directory + VariableKeeper.APP_CONSTANT.system_permission_host_fileName + "不存在,开始拷贝!");
                    //在应用层代码中直接做拷贝会有(Read-only file system)的异常产生,所以应该先把文件释放到sd卡 然后再通过操作系统命令来将其间接地置入/system/etc/permissions/目录下
                    String tmp_permissionXml = VariableKeeper.sdCard_mountRootPath + VariableKeeper.APP_CONSTANT.base_folder_name + File.separator + VariableKeeper.APP_CONSTANT.system_permission_host_fileName;
                    //先把文件释放到sd卡
                    SystemUtil.copyFromAssets2AnyDir(mContext, VariableKeeper.APP_CONSTANT.system_permission_host_fileName, tmp_permissionXml, permissionFileHandlerListener);
                    //通过操作系统命令来将其间接地置入/system/etc/permissions/目录下,命令执行完毕后删除 tmp_permissionXml
                    /**
                     *  cat /sdcard/cartracker/android.hardware.usb.host.xml > /system/etc/permissions/android.hardware.usb.host.xml &
                     *  dd if=/etc/inittab of=/opt/inittab.bak
                     */
                    List<String> commnandList = new ArrayList<String>();
                    commnandList.add("cat "+tmp_permissionXml +" > "+VariableKeeper.APP_CONSTANT.system_permission_directory + VariableKeeper.APP_CONSTANT.system_permission_host_fileName +" &");
                    ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
                    File f = new File(tmp_permissionXml);
                    if (f.exists()) f.delete();
                    f = null;
                }else {
                    SystemUtil.log("文件:" + VariableKeeper.APP_CONSTANT.system_permission_directory + VariableKeeper.APP_CONSTANT.system_permission_host_fileName + "存在,开始分析内容是否合乎规范!");
                    //TODO
                }

            }
        });

    }

    /**
     * 确保/system/etc/permissions/handheld_core_hardware.xml 或者 /system/etc/permissions/tablet_core_hardware.xml文件中存在内容:"<feature name="android.hardware.usb.host" />"
     */
    private void handHeldCoreXmlHandler() {

    }
}
