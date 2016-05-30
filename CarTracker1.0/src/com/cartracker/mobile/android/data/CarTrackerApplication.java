package com.cartracker.mobile.android.data;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;
public class CarTrackerApplication  extends Application implements appStop,Thread.UncaughtExceptionHandler  {
	 private static CarTrackerApplication mCarTrackerApplication;
	    private Handler mHandler;
	    private AlarmManager alarmManager;
	    private PendingIntent pi;

	    @Override
	    public void onCreate() {
	        super.onCreate();
	        init();
	    }

	    private void init() {

            Thread.setDefaultUncaughtExceptionHandler(this);
            mCarTrackerApplication=this;
	        mHandler=new Handler() ;
	        VariableKeeper.addStop(this);
	        VariableKeeper.init(this);
	        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	        Intent intent = new Intent();
	        intent.setAction(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_UNIT_NAME);
	        pi = PendingIntent.getBroadcast(this, 0, intent, 0);
	        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
	                0, VariableKeeper.APP_CONSTANT.INTERVAL_UNIT, pi);
	    }


	    public  Handler getHandler() {
	        return mHandler;
	    }

	    public static CarTrackerApplication getTrackerAppContext() {
	        return mCarTrackerApplication;
	    }



	    @Override
	    public void onLowMemory() {
	        super.onLowMemory();
            SystemUtil.log("very low memery...onLowMemory");
	    }

	    @Override
	    public void onTerminate() {
	        super.onTerminate();
            SystemUtil.log("very low memery...onTerminate");
	    }

	    @Override
	    public void stopAll() {
//	        mCarTrackerApplication = null;
//	        mHandler = null;
	        alarmManager.cancel(pi);
	        alarmManager = null;
	        pi = null;
	    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        SystemUtil.log("uncaughtException:"+ex.getMessage());
        String crash_log_file_path;
        crash_log_file_path = SystemUtil.saveCrashInfo2File(ex);
        //将此崩溃日志上传服务器或者另行处理
        //......TODO
    }
}
