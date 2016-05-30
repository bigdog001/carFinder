package com.cartracker.mobile.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.GrideViewAdapter;
import com.cartracker.mobile.android.ui.VideoPreview.VideoPreviewActivity;
import com.cartracker.mobile.android.ui.review.ReViewActivity;
import com.cartracker.mobile.android.ui.root.CameraSettingGuideActivity;
import com.cartracker.mobile.android.util.RecordManager;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RecordThreadStatusListener;

/**
 * Created by jw362j on 9/29/2014.
 */
public class FeaturesFragment extends Fragment implements  RecordThreadStatusListener {
    private GridView desktop_grideview;
    private ImageView record_status_0,record_status_1,record_status_2,record_status_3;
    private Animation mAnimation ;

//    private LinearLayout record_statu_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.desktop_features, container, false);
        desktop_grideview = (GridView) view.findViewById(R.id.desktop_grideview);
        initGridView();
        if (!VariableKeeper.threadStatusListeners.contains(FeaturesFragment.this)) {
            VariableKeeper.threadStatusListeners.add(FeaturesFragment.this);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (!VariableKeeper.threadStatusListeners.contains(FeaturesFragment.this)) {
            VariableKeeper.threadStatusListeners.add(FeaturesFragment.this);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
      /*  if (VariableKeeper.threadStatusListeners.contains(FeaturesFragment.this)) {
            VariableKeeper.threadStatusListeners.remove(FeaturesFragment.this);
        }*/
    }

    private void initGridView() {
        LinearLayout[] gridViewItems = initGridViewItems();
        mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.record_item_alpha);
        desktop_grideview.setAdapter(new GrideViewAdapter(gridViewItems));
        desktop_grideview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag = (String) view.getTag();
                if ("record".equals(tag)) {
                    //检查相机状态并开启录像进程
                    /**
                     * 1:判断系统是否大于3.1 然后检查是否root过 如果未root则提示无法录像
                     * 2:询问用户是否已经连接摄像头
                     *      2.1 :如果用户点击未连接则提示取消刻录
                     *      2.2 :如果用户点击已经连接镜头则出现开始检测镜头的弹出框 带loading条
                     *           2.2.1:如果未检测到镜头则提示当前没有检测到镜头 请退出程序重新检查连线后再试
                     *           2.2.2:如果检测到镜头则显示当前探测到的镜头设备列表（只是让用户看一眼而已）,同时显示启动镜头中------>镜头动后（同时往对应的画面缓冲区放入画面）  此刻 刻录同时启动 ，自动进入画面4屏的监视模式,在进入监视界面前又2秒的loading条出现
                     * 3:成功进入监视画面后整个刻录的用户使用用例完成，当用户从4屏监视界面退出回到桌面的时候录像依然在进行  相机对应的4个点做对应的闪烁
                     */
                    new RecordManager();
                    SystemUtil.recordStatusMonitor();
                } else if ("preview".equals(tag)) {
                    Intent intent = new Intent(getActivity(), VideoPreviewActivity.class);
                    startActivity(intent);
                } else if ("stop".equals(tag)) {
                    getActivity().sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.ACTION_USB_CAMERA_COMPLETE_STOP));
                    //====================一旦退出 说明此时镜头画面采集完成 应该对桌面上闪烁的四个红点做停止闪烁的动作===============================================
                    for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
                        if (VariableKeeper.videoCaptures[i] != null) {
                            VariableKeeper.videoCaptures[i].video_record_flag = 1;
                            VariableKeeper.videoCaptures[i].Stop();
                        }
                        if (VariableKeeper.bitMapHolderInits[i] != null) VariableKeeper.bitMapHolderInits[i].setThread_flag(0);//停掉信号发生器 此时已经作废
                    }
                    SystemUtil.recordStatusMonitor();
                    SystemUtil.MyToast(getActivity().getResources().getString(R.string.desktop_camera_stop_toast_msg));
                    //====================一旦退出 说明此时镜头画面采集完成 应该对桌面上闪烁的四个红点做停止闪烁的动作===============================================


                } else if ("switch_file".equals(tag)) {
                    getActivity().sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_FILE_SWITCH));
                    VariableKeeper.system_last_FILE_SWITCH_Time = System.currentTimeMillis();
                    SystemUtil.MyToast(getResources().getString(R.string.desktop_toast_switch_msg));
                } else if("setup_camera".equals(tag)){
                    Intent rootIntent = new Intent(getActivity(), CameraSettingGuideActivity.class);
                    startActivity(rootIntent);
                }else if ("exit".equals(tag)) {
                    Dialog dialog = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getActivity().getResources().getString(R.string.desktop_feature_exit_title)).setMessage(getActivity().getResources()
                            .getString(R.string.desktop_feature_exit_msg))
                            .setPositiveButton(getActivity().getResources().getString(R.string.confirm),new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dialog = null;
                                    VariableKeeper.stopApp();
                                }
                            }).setNegativeButton(getActivity().getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }  else if ("review".equals(tag)) {
                    Intent intent = new Intent(getActivity(), ReViewActivity.class);
                    startActivity(intent);
                } else if ("test".equals(tag)) {
                    //TODO 测试
                    Intent notifycation = new Intent(FeaturesFragment.this.getActivity(),NotifucationTest.class);
                    startActivity(notifycation);
                }
            }
        });
    }

    private LinearLayout[] initGridViewItems() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout record = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(record, R.drawable.desktop_record, getResources().getString(R.string.desktop_record),1);
        record.setTag("record");

        LinearLayout preview = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(preview, R.drawable.desktop_preview, getResources().getString(R.string.desktop_preview),0);
        preview.setTag("preview");

        LinearLayout stop = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(stop, R.drawable.desktop_record_pause, getResources().getString(R.string.desktop_stop),0);
        stop.setTag("stop");

        LinearLayout switch_file = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(switch_file, R.drawable.desktop_button_switch, getResources().getString(R.string.desktop_switch_file),0);
        switch_file.setTag("switch_file");


        LinearLayout setup_camera = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(setup_camera, R.drawable.desktop_camera_refresh, getResources().getString(R.string.setup_camera_string),0);
        setup_camera.setTag("setup_camera");



        LinearLayout exit = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(exit, R.drawable.menu_icon_exit, getResources().getString(R.string.desktop_exit),0);
        exit.setTag("exit");


        LinearLayout review = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
        setTitleIcon(review, R.drawable.desktop_video_review, getResources().getString(R.string.desktop_review),0);
        review.setTag("review");


        LinearLayout[] linearLayouts ;
        if(SystemUtil.isFileExistInSDRoot(VariableKeeper.APP_CONSTANT.DEBUG_SWITCHER_FILENAME)){
            LinearLayout test = (LinearLayout) inflater.inflate(R.layout.desktop_shortcut, null, false);
            setTitleIcon(test, R.drawable.desktop_function_test, "测试",0);
            test.setTag("test");
            linearLayouts = new LinearLayout[]{record, preview, stop, switch_file, review,setup_camera,exit,test};
        }else {
            linearLayouts = new LinearLayout[]{record, preview, stop, switch_file, review,setup_camera,exit};
        }
        return linearLayouts;
    }

    private void setTitleIcon(LinearLayout shortcutView, int iconResId, String title,int flag) {
        if(flag == 1){
            record_status_0 = (ImageView) shortcutView.findViewById(R.id.record_status_0);
            record_status_1 = (ImageView) shortcutView.findViewById(R.id.record_status_1);
            record_status_2 = (ImageView) shortcutView.findViewById(R.id.record_status_2);
            record_status_3 = (ImageView) shortcutView.findViewById(R.id.record_status_3);
            ((LinearLayout) shortcutView.findViewById(R.id.record_statu_layout)).setVisibility(View.VISIBLE);
            TextView titleView = (TextView) shortcutView.findViewById(R.id.desktop_shortcut_notice_title);
            titleView.setText(title);
            ImageView iconView = (ImageView) shortcutView.findViewById(R.id.desktop_shortcut_notice_icon);
            iconView.setImageResource(iconResId);
        }else {
            TextView titleView = (TextView) shortcutView.findViewById(R.id.desktop_shortcut_notice_title);
            titleView.setText(title);
            ImageView iconView = (ImageView) shortcutView.findViewById(R.id.desktop_shortcut_notice_icon);
            iconView.setImageResource(iconResId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            desktop_grideview.setNumColumns(5);
            desktop_grideview.invalidateViews();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            desktop_grideview.setNumColumns(3);
            desktop_grideview.invalidateViews();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStatus(int id, int i, Object data) {
        int istarted_0 = 0 ,istarted_1 = 0 ,istarted_2 = 0 ,istarted_3 = 0 ;
        switch (id){
            case 0:
               if(i == 1) {
                   record_status_0.setBackgroundColor(Color.RED);
                   if(istarted_0 == 0){
                       record_status_0.startAnimation(mAnimation);
                       istarted_0 = 1;
                   }
               }else {
                   record_status_0.setBackgroundColor(Color.BLACK);
                   record_status_0.clearAnimation();
               }
                break;

            case 1:
                if(i == 1){
                    record_status_1.setBackgroundColor(Color.RED);
                    if(istarted_1 == 0){
                        record_status_1.startAnimation(mAnimation);
                        istarted_1 =1;
                    }
                }else {
                    record_status_1.setBackgroundColor(Color.BLACK);
                    record_status_1.clearAnimation();
                }
                break;

            case 2:
                if(i == 1){
                    record_status_2.setBackgroundColor(Color.RED);
                    if(istarted_2 == 0){
                        record_status_2.startAnimation(mAnimation);
                        istarted_2 = 1;
                    }
                }else {
                    record_status_2.setBackgroundColor(Color.BLACK);
                    record_status_2.clearAnimation();
                }
                break;

            case 3:
                if(i == 1){
                    record_status_3.setBackgroundColor(Color.RED);
                    if(istarted_3 == 0){
                        record_status_3.startAnimation(mAnimation);
                        istarted_3 = 1;
                    }
                }else {
                    record_status_3.setBackgroundColor(Color.BLACK);
                    record_status_3.clearAnimation();
                }
                break;

        }
    }
}
