package com.cartracker.mobile.android.data.beans;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import java.util.List;

/**
 * Created by jw362j on 9/26/2014.
 */
public class BroadcastReceiverModule {
    private BroadcastReceiver br;
    private IntentFilter intentFilter ;



    public BroadcastReceiverModule(BroadcastReceiver br, List<String> intentFilter_strs,int priority) {
        this.br = br;
        this.intentFilter = new IntentFilter();
        if(priority != 0)this.intentFilter.setPriority(priority);
        if(intentFilter_strs != null){
            for(String intent_str:intentFilter_strs){
                this.intentFilter.addAction(intent_str);
            }
        }
    }

    public BroadcastReceiver getBr() {
        return br;
    }

    public void setBr(BroadcastReceiver br) {
        this.br = br;
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public void setIntentFilter(IntentFilter intentFilter) {
        this.intentFilter = intentFilter;
    }
}
