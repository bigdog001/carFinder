package com.cartracker.mobile.android.util.handler.impl;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Vibrator;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 10/14/2014.
 */
public class MySensorEventListener implements SensorEventListener {
    private Vibrator vibrator = null;
    private Context context;

    public MySensorEventListener(Context c) {
        this.context = c;
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        VariableKeeper.speedChanges.add(new InterfaceGen.onSpeedChange() {
            @Override
            public void onSpeed() {
                vibrator.vibrate(VariableKeeper.APP_CONSTANT.sensor_vibrate_duringTime);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > VariableKeeper.APP_CONSTANT.sensor_vibrate_Level ||
                    Math.abs(values[1]) > VariableKeeper.APP_CONSTANT.sensor_vibrate_Level ||
                    Math.abs(values[2]) > VariableKeeper.APP_CONSTANT.sensor_vibrate_Level)) {
                SystemUtil.log("sensor  ============ values[0] = " + values[0]);
                SystemUtil.log("sensor  ============ values[1] = " + values[1]);
                SystemUtil.log("sensor  ============ values[2] = " + values[2]);
//                        vibrator.vibrate( new long[]{500,200,500,200}, -1); //第一个｛｝里面是节奏数组， 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
                boolean ison = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, false);
                if (ison) {
                    if(VariableKeeper.speedChanges != null) {
                        for (InterfaceGen.onSpeedChange speed:VariableKeeper.speedChanges) {
                            if(speed != null)speed.onSpeed();
                        }
                    }
                }

            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
