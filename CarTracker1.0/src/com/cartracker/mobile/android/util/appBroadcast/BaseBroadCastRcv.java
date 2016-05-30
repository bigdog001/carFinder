package com.cartracker.mobile.android.util.appBroadcast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.util.SystemUtil;
import java.util.Set;

/**
 * Created by jw362j on 11/13/2014.
 */
public abstract class BaseBroadCastRcv extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //检测广播的发送方是否是我所认可的,如果是则调用onSafeReceive逻辑 否则丢弃
        Set<String> cs = intent.getCategories();
        SystemUtil.log("Scheme is:"+intent.getScheme());
        ComponentName cn = intent.getComponent();
        if (cn != null) {
            SystemUtil.log("Package is:" + cn.getPackageName());
        }

        onSafeReceive(context, intent);
    }

    public abstract void onSafeReceive(Context context, Intent intent);
}
