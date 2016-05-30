package com.cartracker.mobile.android.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.UsbCameraDevice;
import com.cartracker.mobile.android.ui.VideoPreview.UsbCameraMain;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.ui.root.CameraSettingGuideActivity;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RootResultListener;
import com.cartracker.mobile.android.util.libUsb.LibUsbConnector;
import com.usb.camera.android.SingleUsbCameraActivity;

import java.util.Map;

/**
 * Created by jw362j on 9/29/2014.
 */
public class NotifucationTest extends BaseActivity implements RootResultListener, View.OnClickListener {
    private  int notifycation_ID = 1;
    private  NotificationManager notificationManager;
    private  Notification notification;
    private Button send;
    private Button cancel;
    private Button root;
//    private Button usb_device;
    private Button usb_camera_setting,usb_single_devices;
    private Button usb_camera_justsee,performance_tuning;
    private Button usb_global_video_devices,extend_usb_devices,usb_extention_video_devices,usb_extention_LibUsbConnector_devices;
    private TextView test_msg_tips;
    private String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifycation);
        send = (Button)findViewById(R.id.send);
        cancel = (Button)findViewById(R.id.cancel);
        root = (Button)findViewById(R.id.root);
        usb_camera_setting = (Button)findViewById(R.id.usb_camera_setting);
        usb_camera_justsee = (Button)findViewById(R.id.usb_camera_justsee);
        usb_extention_LibUsbConnector_devices = (Button)findViewById(R.id.usb_extention_LibUsbConnector_devices);
       
        test_msg_tips = (TextView)findViewById(R.id.test_msg_tips);

        usb_single_devices = (Button)findViewById(R.id.usb_single_devices);
        usb_global_video_devices = (Button)findViewById(R.id.usb_global_video_devices);
        extend_usb_devices = (Button)findViewById(R.id.extend_usb_devices);
        usb_extention_video_devices = (Button)findViewById(R.id.usb_extention_video_devices);
        performance_tuning = (Button)findViewById(R.id.performance_tuning);

        usb_single_devices.setOnClickListener(NotifucationTest.this);
        performance_tuning.setOnClickListener(NotifucationTest.this);
        notificationManager = (NotificationManager) getActivity().getSystemService( getActivity().NOTIFICATION_SERVICE);
        usb_global_video_devices.setOnClickListener(NotifucationTest.this);
        extend_usb_devices.setOnClickListener(NotifucationTest.this);
        usb_extention_video_devices.setOnClickListener(NotifucationTest.this);
        send.setOnClickListener(NotifucationTest.this);
        cancel.setOnClickListener(NotifucationTest.this);
        root.setOnClickListener(NotifucationTest.this);
        usb_camera_setting.setOnClickListener(NotifucationTest.this);
        usb_camera_justsee.setOnClickListener(NotifucationTest.this);
        usb_extention_LibUsbConnector_devices.setOnClickListener(NotifucationTest.this);
        
    }

    private void send(){
        // 2.初始化Notification对象
        notification = new Notification();
        // 设置通知的icon
        notification.icon = R.drawable.notification_upload;
        // 设置通知在状态栏上显示的滚动信息
        notification.tickerText = "开始录像";
        // 设置通知的时间
        notification.when = System.currentTimeMillis();

        Intent intent = new Intent(getActivity(), NotifucationPView.class);
        PendingIntent pi = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        notification.setLatestEventInfo(getActivity(), "通知标题", "通知内容", pi);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        // 4.发送通知
        notificationManager.notify(notifycation_ID, notification);
    }

    private void cancel(){
        notificationManager.cancel(notifycation_ID);
    }
    private void rootprocess(){
        SystemUtil.log("in root detecting...");
        SystemUtil.getRootStatus(NotifucationTest.this);
    }


    @Override
    public void stopAll() {

    }

    @Override
    public void onRootResult(int execCode, Object data) {
        SystemUtil.log("root status is:"+execCode);
        SystemUtil.MyToast(execCode+"");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){           
            case R.id.usb_extention_LibUsbConnector_devices:
                LibUsbConnector usbConnector = new LibUsbConnector();
                usbConnector.ListUsbDevices(null);
                break;
            case R.id.usb_camera_justsee:
                Intent intent = new Intent(getActivity(), UsbCameraMain.class);
                startActivity(intent);
                break;
            case R.id.usb_camera_setting:
                Intent rootIntent = new Intent(NotifucationTest.this, CameraSettingGuideActivity.class);
                startActivity(rootIntent);
                break;
            case R.id.root:
                rootprocess();
                break;
            case R.id.cancel:
                cancel();
                break;
            case R.id.send:
                send();
                break;
            case R.id.usb_extention_video_devices:
                if(VariableKeeper.extendedUsbCameraDevices != null) {
                    for (UsbCameraDevice t:VariableKeeper.extendedUsbCameraDevices) {
                        if(t!=null)result+=t.getUsbMountPath().toString()+"\n";
                    }
                    test_msg_tips.setText(result);
                    result = "";
                }
                break;
            case R.id.extend_usb_devices:
                if(VariableKeeper.usbDevices_All != null){
                    for(Map.Entry<String,UsbCameraDevice> t:VariableKeeper.usbDevices_All.entrySet()){
                        result+=t.getValue().getUsbMountPath().toString()+"\n";
                    }
                    test_msg_tips.setText(result);
                    result = "";
                }
                break;
            case R.id.usb_global_video_devices:
                if(VariableKeeper.SystemGlobalVideoDevices != null) {
                    for (UsbCameraDevice t:VariableKeeper.SystemGlobalVideoDevices) {
                        result+=t.getUsbMountPath().toString()+"\n";
                    }
                    test_msg_tips.setText(result);
                    result = "";
                }
                break;
            case R.id.usb_single_devices:
                startActivity(new Intent(getActivity(), SingleUsbCameraActivity.class));
                break;
            case R.id.performance_tuning:
                startActivity(new Intent(getActivity(), PerformanceMonitorActivity.class));
                break;
        }
    }
}
