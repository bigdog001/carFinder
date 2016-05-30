package com.cartracker.mobile.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.ui.activity.RemoteControlActivity;
import com.cartracker.mobile.android.util.SystemUtil;

/**
 * Created by jw362j on 9/29/2014.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    private CheckBox deskto_setting_video_rewrite_cb;//是否开启自动复写视频录像文件的模式 即当sd卡空间不够的时候自动删除旧的视频文件以腾出空间来刻录新的视频监控 sp中的变量存储名称 VariableKeeper.sp_name_video_rewrite
    private CheckBox desktop_setings_sound_alert_cb;//是否开启声音告警机制 当一些时间发生的时候以语音的形式告知用户 sp中的变量存储名称VariableKeeper.sp_name_sound_alert
    private FrameLayout setting_emergency_layout;//紧急援助
    private FrameLayout setting_frameLayout_sound_alert;
    private FrameLayout setting_frameLayout_video_rewrite;
    private FrameLayout setting_remote_start_layout;//远程通过短信激活镜头 并开始录像
    private FrameLayout setting_video_quality_layout;//录像画面质量
    private TextView id_desktop_setings_emergency_status_tip;
    private Dialog dialog_ = null;
    private SeekBar video_quality_changer;
    private TextView setting_tv_video_quality_currentvalue;

    private  int video_quality_now;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.desktop_settings_fragment, container, false);
        intiSettingViews(view);
        return view;

    }

    private void intiSettingViews(View view) {
        deskto_setting_video_rewrite_cb = (CheckBox) view.findViewById(R.id.desktop_settings_video_rewrite_cb);
        deskto_setting_video_rewrite_cb.setEnabled(false);
        desktop_setings_sound_alert_cb = (CheckBox) view.findViewById(R.id.desktop_setings_sound_alert_cb);
        desktop_setings_sound_alert_cb.setEnabled(false);
        setting_emergency_layout = (FrameLayout) view.findViewById(R.id.setting_emergency_layout);
        setting_frameLayout_sound_alert = (FrameLayout) view.findViewById(R.id.setting_frameLayout_sound_alert);
        setting_frameLayout_video_rewrite = (FrameLayout) view.findViewById(R.id.setting_frameLayout_video_rewrite);
        setting_remote_start_layout = (FrameLayout) view.findViewById(R.id.setting_remote_start_layout);
        setting_video_quality_layout = (FrameLayout) view.findViewById(R.id.setting_video_quality_layout);
        id_desktop_setings_emergency_status_tip = (TextView) view.findViewById(R.id.id_desktop_setings_emergency_status_tip);

        if(VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_video_rewrite,false)){
            deskto_setting_video_rewrite_cb.setChecked(true);
        }
        if(VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_sound_alert,false)){
            desktop_setings_sound_alert_cb.setChecked(true);
        }
        setting_emergency_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当发现汽车紧急颠簸 比如碰撞时候自动启动对应的机制
                getActivity().startActivity(new Intent(getActivity(),EmergencyActivity.class));
            }
        });
        setting_frameLayout_video_rewrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.desktop_setings_video_rewrite_dialog_title)).setMessage(getResources().getString(R.string.desktop_setings_video_rewrite_dialog_msg)).setPositiveButton(getResources().getString(R.string.confirm),new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean video_rewrite_status = deskto_setting_video_rewrite_cb.isChecked();
                        if(video_rewrite_status){
                            deskto_setting_video_rewrite_cb.setChecked(false);
                        }else {
                            deskto_setting_video_rewrite_cb.setChecked(true);
                        }
                        SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                        editor.putBoolean(VariableKeeper.APP_CONSTANT.sp_name_video_rewrite,deskto_setting_video_rewrite_cb.isChecked());
                        editor.commit();
                        dialog.dismiss();
                        dialog = null;
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
        setting_frameLayout_sound_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sound_alert_status = desktop_setings_sound_alert_cb.isChecked();
                if (sound_alert_status) {
                    desktop_setings_sound_alert_cb.setChecked(false);
                }else {
                    desktop_setings_sound_alert_cb.setChecked(true);
                }
                SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                editor.putBoolean(VariableKeeper.APP_CONSTANT.sp_name_sound_alert,desktop_setings_sound_alert_cb.isChecked());
                editor.commit();
            }
        });

        setting_remote_start_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), RemoteControlActivity.class));
            }
        });

        setting_video_quality_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框让用户设置画面录制质量
                video_quality_now = VariableKeeper.mSp.getInt(VariableKeeper.APP_CONSTANT.video_record_quality_sp_name,100);//默认画面高清

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View view_title = View.inflate(getActivity(), R.layout.custom_dialog_title_view, null);
                final View view_body = View.inflate(getActivity(), R.layout.setting_video_record_quality_body, null);

                Button setting_confirm = (Button) view_body.findViewById(R.id.setting_confirm);
                setting_tv_video_quality_currentvalue = (TextView) view_body.findViewById(R.id.setting_tv_video_quality_currentvalue);
                setting_tv_video_quality_currentvalue.setText(video_quality_now+"");
                video_quality_changer = (SeekBar) view_body.findViewById(R.id.video_quality_changer);
                video_quality_changer.setProgress(video_quality_now);
                video_quality_changer.setOnSeekBarChangeListener(SettingsFragment.this);

                TextView id_tv_tips_title = (TextView) view_title.findViewById(R.id.id_tv_tips_title);
                TextView video_quality_setting_title_close = (TextView) view_title.findViewById(R.id.video_quality_setting_title_close);
                id_tv_tips_title.setText(getResources().getString(R.string.setting_video_quality_title));

                setting_confirm.setOnClickListener(SettingsFragment.this);
                video_quality_setting_title_close.setOnClickListener(SettingsFragment.this);
                builder.setCustomTitle(view_title);
                builder.setView(view_body);
                dialog_ = builder.create();
                dialog_.show();
            }
        });
    }

    private void closeSettingDialog() {
        if (dialog_ != null && dialog_.isShowing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog_.dismiss();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn,false)) {
            id_desktop_setings_emergency_status_tip.setText(getResources().getString(R.string.desktop_setings_emergency_status_tip_on));
        }else {
            id_desktop_setings_emergency_status_tip.setText(getResources().getString(R.string.desktop_setings_emergency_status_tip_off));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.video_quality_setting_title_close :
                closeSettingDialog();
                break;
            case R.id.setting_confirm:
                //保存值
                SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                editor.putInt(VariableKeeper.APP_CONSTANT.video_record_quality_sp_name,video_quality_now);
                editor.commit();
                VariableKeeper.video_record_quality = video_quality_now;
                closeSettingDialog();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (progress > VariableKeeper.video_record_quality_lowest) {
            setting_tv_video_quality_currentvalue.setText(progress + "");
            video_quality_now = progress;
        }else {
            SystemUtil.MyToast(getResources().getString(R.string.setting_video_quality_too_low_tips_text)+VariableKeeper.video_record_quality_lowest);
            video_quality_now = VariableKeeper.video_record_quality_lowest;
            setting_tv_video_quality_currentvalue.setText(video_quality_now + "");
            seekBar.setProgress(VariableKeeper.video_record_quality_lowest);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
