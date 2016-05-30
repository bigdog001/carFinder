package com.cartracker.mobile.android.ui.root;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.UsbCameraDevice;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.ui.review.MyTitlebar;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RecordThreadStatusListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.UsbCableHarwareListener;

/**
 * Created by jw362j on 10/2/2014.
 */
public class CameraSettingGuideActivity extends BaseActivity implements View.OnClickListener ,UsbCableHarwareListener ,RecordThreadStatusListener {
    private View cameraSettingView;
    private MyTitlebar rvt;
    private Button device_camera_setting_btn;
    private Button device_camera_flags[];
    private LinearLayout usb_cable_tips_layout;
    private TextView usb_cable_status_msg;
    private TextView usb_port_abstract_tips;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//titlebar_container
        super.onCreate(savedInstanceState);
        cameraSettingView = View.inflate(CameraSettingGuideActivity.this, R.layout.camera_setting_guide, null);
        setContentView(cameraSettingView);
        device_camera_flags = new Button[VariableKeeper.APP_CONSTANT.size_num_cam];
        LinearLayout detect_view_title = (LinearLayout) findViewById(R.id.record_view_titlebar);
        initTitle();
        detect_view_title.addView(rvt.getRecord_view_titlebar());
        initView();


    }

    private void initView() {
        device_camera_setting_btn = (Button) cameraSettingView.findViewById(R.id.device_camera_setting_btn);
        device_camera_setting_btn.setTag("device_camera_setting_btn");
        device_camera_setting_btn.setOnClickListener(CameraSettingGuideActivity.this);

        usb_cable_tips_layout = (LinearLayout) cameraSettingView.findViewById(R.id.usb_cable_tips_layout);
        usb_cable_status_msg = (TextView) cameraSettingView.findViewById(R.id.usb_cable_status_msg);
        usb_port_abstract_tips = (TextView) cameraSettingView.findViewById(R.id.usb_port_abstract_tips);
        device_camera_flags[0] = (Button) cameraSettingView.findViewById(R.id.device_camera_flag_0);
        device_camera_flags[1] = (Button) cameraSettingView.findViewById(R.id.device_camera_flag_1);
        device_camera_flags[2] = (Button) cameraSettingView.findViewById(R.id.device_camera_flag_2);
        device_camera_flags[3] = (Button) cameraSettingView.findViewById(R.id.device_camera_flag_3);

        this.progress = new ProgressDialog(this);
        this.progress.setCancelable(false);//不可取消 强制等待usb线接入后自动取消
        this.progress.setCanceledOnTouchOutside(false);
        this.progress.setMessage(getResources().getString(R.string.usb_camera_detect_loading_tips));
    }


    //need to run in runOnUiThread
    private void showProgress(String msg) {
        if(this.progress != null) {
            this.progress.setMessage(msg);
            this.progress.show();
        }
    }

    //need to run in runOnUiThread
    private void showProgress() {
        if(this.progress != null) {
            this.progress.show();
        }
    }

    //need to run in runOnUiThread
    private void dismissProgress() {
        if (this.progress != null) {
            try {
                this.progress.dismiss();
            } catch (Exception e) {
            }
        }
    }

    private void initTitle(){
        rvt = new MyTitlebar(CameraSettingGuideActivity.this);
        rvt.setTitle(getResources().getString(R.string.title_root_camera));
        rvt.setLeftBtnShow(true);
        rvt.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void stopAll() {

    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (!"".equals(tag) && tag != null) {
            tag = tag.trim();
            if("device_camera_setting_btn".equals(tag)){
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                builder.setTitle(getResources().getString(R.string.desktop_camera_setting_remind_tips))
                        .setMessage(getResources().getString(R.string.desktop_camera_setting_remind_msg))
                        .setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog = null;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgress();
                                    }
                                });
                                //进入相机检测模块
                                Intent camera_check_intent = new Intent(VariableKeeper.APP_CONSTANT.USB_DEVICE_QUERY);
                                sendBroadcast(camera_check_intent);
                                //TODO
                            }
                        }).setNegativeButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialog = null;
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onRestart();
        if (!VariableKeeper.threadStatusListeners.contains(CameraSettingGuideActivity.this)) {
            VariableKeeper.threadStatusListeners.add(CameraSettingGuideActivity.this);
        }
        //当重新启动的时候再次将自己注册到VariableKeeper中以接受usb连线信号的调用
        if(VariableKeeper.usbCableHarwareListeners != null)VariableKeeper.usbCableHarwareListeners.add(CameraSettingGuideActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (VariableKeeper.threadStatusListeners.contains(CameraSettingGuideActivity.this)) {
            VariableKeeper.threadStatusListeners.remove(CameraSettingGuideActivity.this);
        }
        if(VariableKeeper.usbCableHarwareListeners != null){
             if(VariableKeeper.usbCableHarwareListeners.contains(CameraSettingGuideActivity.this))VariableKeeper.usbCableHarwareListeners.remove(CameraSettingGuideActivity.this);
        }
    }

    @Override
    public void onCableChange(final int cableStatus, final Object data) {
        VariableKeeper.getmCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dismissProgress();
                //0为usb线断开 1：usb接口线缆连上设备2：连上了充电器  此时在UI界面上显示出usb线和手机的接线状态
                //此函数式子啊广播接受者中发起调用的 切勿进行大的耗时运算
                usb_cable_tips_layout.setVisibility(View.VISIBLE);
                if(cableStatus ==  0){
                    usb_cable_status_msg.setText(getResources().getString(R.string.desktop_usb_port_cablestatus_1));
                }else if(cableStatus ==  1){
                    usb_cable_status_msg.setText(getResources().getString(R.string.desktop_usb_port_cablestatus_2));
                }else if(cableStatus ==  2){
                    usb_cable_status_msg.setText(getResources().getString(R.string.desktop_usb_port_cablestatus_3));
                }else if(cableStatus == 4){
                    String exceptions = ( data == null ? "":data.toString());
                    if(!"".equals(exceptions)) SystemUtil.MyToast(exceptions);
                }

                for(int i = 0 ;i < VariableKeeper.APP_CONSTANT.size_num_cam;i++){
                    if(VariableKeeper.extendedUsbCameraDevices[i] != null){
                        device_camera_flags[i].setOnClickListener(null);
                        //如果此设备已经被探测到 将界面变色至绿色
                        device_camera_flags[i].setBackgroundColor(getResources().getColor(R.color.desktop_title_bg));
                    }
                }

                int currentVideoSize = 0;
                for(UsbCameraDevice ud:VariableKeeper.extendedUsbCameraDevices){
                    if(ud != null){
                        currentVideoSize++;
                    }
                }

                usb_port_abstract_tips.setText(getResources().getString(R.string.usb_port_abstract_tips_1)+VariableKeeper.usbDevices_All.size()+getResources().getString(R.string.usb_port_abstract_tips_2)+currentVideoSize+getResources().getString(R.string.usb_port_abstract_tips_3));
                usb_port_abstract_tips.setVisibility(View.VISIBLE);

            }
        });
    }


    @Override
    public void onStatus(int id, int i, Object data) {
        SystemUtil.log("record thread status ,id:"+id+",status:"+i);
    }
}
