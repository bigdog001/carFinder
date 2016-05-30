package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.Initializer;

/**
 * Created by jw362j on 10/4/2014.
 */
public class BinFilesInit implements Initializer ,Runnable{
    private Context mContext;
    @Override
    public void init(Context context) {
        this.mContext = context;
        new Thread(BinFilesInit.this).start();
    }


    @Override
    public void run() {
        if(VariableKeeper.isUsingBusyBox && !SystemUtil.varifyFile(mContext,VariableKeeper.busybox_cmd_name)){
            SystemUtil.log("busy box 文件不存在 立即拷贝 之后修改权限");
            try {
                SystemUtil.copyFromAssets2Data(mContext, VariableKeeper.busybox_cmd_name, VariableKeeper.busybox_cmd_name);
                String script = "chmod 0777 " + VariableKeeper.app_path+VariableKeeper.busybox_cmd_name ;
                SystemUtil.exe(script,null);
            } catch (Exception e) {
                SystemUtil.log("busy box exception "+e.getMessage());
                SystemUtil.log(e.getMessage());
            }
        }
    }
}
