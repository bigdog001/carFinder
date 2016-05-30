package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 10/14/2014.
 */
public class SensorInitializer implements InterfaceGen.Initializer, InterfaceGen.appStop {
    private SensorManager sensorManager = null;


    //sensorEventListener
    @Override
    public void init(Context context) {
        VariableKeeper.addStop(SensorInitializer.this);
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        VariableKeeper.sensorEventListener = new MySensorEventListener(context);
        sensorManager.registerListener(VariableKeeper.sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void stopAll() {
        sensorManager.unregisterListener(VariableKeeper.sensorEventListener);
    }
}
