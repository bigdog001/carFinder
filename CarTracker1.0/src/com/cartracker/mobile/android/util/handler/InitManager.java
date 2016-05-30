package com.cartracker.mobile.android.util.handler;

import android.content.Context;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SysEnvMonitor.impl.SDCardSizeMonitorHandler;
import com.cartracker.mobile.android.util.handler.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw362j on 9/21/2014.
 */
public class InitManager implements InterfaceGen.appStop {
    private Context mContext;

    private List<InterfaceGen.Initializer> initializers;
    public InitManager(Context mContext) {
        VariableKeeper.addStop(this);
        this.mContext = mContext;
        initializers = new ArrayList<InterfaceGen.Initializer>();
        setUpInitializers();
        runInitializers();
    }


    private void setUpInitializers(){
        initializers.add(new VariableInit());
        initializers.add(new BinFilesInit());
        initializers.add(new SDCardSizeMonitorHandler());
        initializers.add(new FolderStructureHandler());

        initializers.add(new ExtenalUsbDevicesInitHandler());//系统已启动就应该发送一次查询usb设备的广播 以防止用户在启动软件前就插入了usb线而出现无法及时检测到usb设备的情况 这样程序一启动 VariableKeeper.usbDevices_All中就会被当前所有接入的usb设备列表初始化
        initializers.add(new DeskShortCutInit());//创建快捷方式
        initializers.add(new SensorInitializer());//初始化系统中的各种传感器
    }

    private void runInitializers() {
        for (InterfaceGen.Initializer initializer:initializers) {
            if(initializer != null){
                initializer.init(mContext);
                initializer = null;
            }
        }
        initializers.clear();
        initializers = null;
    }

    @Override
    public void stopAll() {

    }
}
