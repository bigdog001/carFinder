package com.cartracker.mobile.android.util.appBroadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.http.INetRequest;
import com.cartracker.mobile.android.util.http.INetResponse;
import com.cartracker.mobile.android.util.json.JsonObject;
import com.cartracker.mobile.android.util.json.JsonValue;

/**
 * Created by jw362j on 12/5/2014.
 */
public class AutoStarter extends BaseBroadCastRcv {
//    private Context context;

    private boolean auto_start_activity;

    @Override
    public void onSafeReceive(Context context, Intent intent) {
        auto_start_activity =  VariableKeeper.mSp.getBoolean("auto_start_activity", false);
        if(!auto_start_activity){

            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();



                if (activeNetInfo != null && activeNetInfo.isConnected()) {
//                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                    INetResponse response = new INetResponse() {
                        @Override
                        public void response(INetRequest req, JsonValue obj) {
                            // TODO Auto-generated method stub
                            int result = (int) ((JsonObject) obj).getNum("result");
                            if (result == 1) {
                                SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                                editor.putBoolean("auto_start_activity", true);
                                editor.commit();
                            }
                        }
                    };
                    //网络可用的时候将客户端情况发送给服务器 ,目的是 客户端第一次在设备上启动的时候就将这个动作执行，告知服务器在一台新设备上被激活了 并附带设备的IMEI号
//                    ServiceProvider.activeClient(VariableKeeper.IMEI, 2, response, false);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
