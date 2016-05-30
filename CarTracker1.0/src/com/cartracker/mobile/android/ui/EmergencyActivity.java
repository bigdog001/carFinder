package com.cartracker.mobile.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.EmergencyAdaptor;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.ui.review.MyTitlebar;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw362j on 10/13/2014.
 */
public class EmergencyActivity extends BaseActivity implements View.OnClickListener, InterfaceGen.itemsRefreshListener {
    private View contentView;
    private MyTitlebar rvt;
    private ListView user_emergency_phone_number_lists;
    private EditText user_emergency_phone_number;
    private Button emergency_sms_save;
    private EmergencyAdaptor emergencyAdaptor;
    private List<String> phone_numbers;
    private Dialog dialog_ = null;
    private Spinner sms_send_frequence_id, sms_stop_time_id;
    private Animation mAnimation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(EmergencyActivity.this, R.layout.emergency_help, null);
        setContentView(contentView);
        LinearLayout detect_view_title = (LinearLayout) findViewById(R.id.record_view_titlebar);
        initTitle();
        detect_view_title.addView(rvt.getRecord_view_titlebar());
        initView();
    }

    private void initView() {
        user_emergency_phone_number = (EditText) contentView.findViewById(R.id.user_emergency_phone_number);
        user_emergency_phone_number.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        emergency_sms_save = (Button) contentView.findViewById(R.id.emergency_sms_save);
        emergency_sms_save.setOnClickListener(EmergencyActivity.this);
        //判断用户之前是否设置过紧急救援联系人
        LoadNumber();
        if (phone_numbers == null || phone_numbers.size() == 0) {
            SystemUtil.dialogJust4TipsShow(getResources().getString(R.string.emergency_sms_number_empty_title_tips), getResources().getString(R.string.emergency_sms_no_phonenumber_text));
        }

        user_emergency_phone_number_lists = (ListView) contentView.findViewById(R.id.user_emergency_phone_number_lists);
        emergencyAdaptor = new EmergencyAdaptor(EmergencyActivity.this, phone_numbers, EmergencyActivity.this);
        user_emergency_phone_number_lists.setAdapter(emergencyAdaptor);

    }

    private void LoadNumber() {
        String cellphone_Numbers = VariableKeeper.mSp.getString(VariableKeeper.APP_CONSTANT.sp_name_emergency_number, null);
        if ("".equals(cellphone_Numbers) || cellphone_Numbers == null)
            return;
        if (cellphone_Numbers.contains(",")) {
            //说明有多个号码
            String[] numbers = cellphone_Numbers.split(",");
            if (numbers != null && numbers.length > 0) {
                phone_numbers = new ArrayList<String>();
                for (String number : numbers) {
                    phone_numbers.add(number);
                }
            }
        } else {
            //说明只有一个号码
            phone_numbers = new ArrayList<String>();
            phone_numbers.add(cellphone_Numbers);
        }

    }

    private void savePhoneNumbers() {
        if (phone_numbers == null || phone_numbers.size() == 0) {
            SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
            editor.putString(VariableKeeper.APP_CONSTANT.sp_name_emergency_number, "");
            editor.commit();
            return;
        }
        int size = phone_numbers.size();
        String target = "";
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                target = target + phone_numbers.get(i);
            } else {
                target = target + phone_numbers.get(i) + ",";
            }
        }

        SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
        editor.putString(VariableKeeper.APP_CONSTANT.sp_name_emergency_number, target);
        editor.commit();

    }

    private void savePhoneNumber(String number) {
        if ("".equals(number) || number == null) {
            SystemUtil.MyToast(getResources().getString(R.string.emergency_sms_number_empty));
            return;
        }
        String cellphone_Numbers = VariableKeeper.mSp.getString(VariableKeeper.APP_CONSTANT.sp_name_emergency_number, null);
        if ("".equals(cellphone_Numbers) || cellphone_Numbers == null) {
            //第一次设置紧急联络人
            SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
            editor.putString(VariableKeeper.APP_CONSTANT.sp_name_emergency_number, number);
            editor.commit();
            user_emergency_phone_number.setText("");
            SystemUtil.MyToast(number + getResources().getString(R.string.emergency_sms_save_sucess_exist));
        } else {
            if (cellphone_Numbers.contains(number)) {
                SystemUtil.MyToast(getResources().getString(R.string.emergency_sms_save_already_exist));
                return;
            }
            //取出旧值然后和新值拼接起来重新保存
            String newValues = cellphone_Numbers + "," + number;
            SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
            editor.putString(VariableKeeper.APP_CONSTANT.sp_name_emergency_number, newValues);
            editor.commit();
            user_emergency_phone_number.setText("");
            SystemUtil.MyToast(number + getResources().getString(R.string.emergency_sms_save_sucess_exist));
            SystemUtil.log("new emergency phone numbers:" + newValues);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id_selected = item.getItemId();
        switch (id_selected) {
            case R.id.menu_emergency_help_settings_send_frequence_id:
                //弹出设置短信发送频率和结束条件的对话框
                openSmsSendFrequenceSettingDialog();
                break;
            case R.id.menu_emergency_stop_id:
                //判断当前是否处于紧急危机遇险模式
                boolean is_emergency = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.is_emergency_model_now_sp_name, false);
                if (is_emergency) {
                    Dialog dialog = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyActivity.this);
                    builder.setTitle(getResources().getString(R.string.dialog_tips_title)).setMessage(getResources().getString(R.string.menu_emergency_stop_tips_text)).setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                            editor.putBoolean(VariableKeeper.APP_CONSTANT.is_emergency_model_now_sp_name, false);//false表示当前已经不处于紧急危险模式 用户成功脱险
                            editor.commit();
                            SystemUtil.MyToast(getResources().getString(R.string.menu_emergency_stop_success_toast_msg));
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                } else {
                    SystemUtil.MyToast(getResources().getString(R.string.menu_emergency_stop_error_text));
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSmsSendFrequenceSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyActivity.this);
        final View view_title = View.inflate(EmergencyActivity.this, R.layout.emergency_help_menu_setting_title, null);
        final View view_body = View.inflate(EmergencyActivity.this, R.layout.emergency_help_menu_setting_body, null);
        sms_send_frequence_id = (Spinner) view_body.findViewById(R.id.sms_send_frequence_id);
        sms_stop_time_id = (Spinner) view_body.findViewById(R.id.sms_stop_time_id);
        setUpSpinner();
        Button setting_sms_save = (Button) view_body.findViewById(R.id.setting_sms_save);
        Button setting_sms_cancel = (Button) view_body.findViewById(R.id.setting_sms_cancel);
        TextView menu_emergency_help_settings_send_frequence_title_close = (TextView) view_title.findViewById(R.id.menu_emergency_help_settings_send_frequence_title_close);

        setting_sms_save.setOnClickListener(EmergencyActivity.this);
        setting_sms_cancel.setOnClickListener(EmergencyActivity.this);
        menu_emergency_help_settings_send_frequence_title_close.setOnClickListener(EmergencyActivity.this);
        builder.setCustomTitle(view_title);
        builder.setView(view_body);
        dialog_ = builder.create();
        dialog_.show();
    }

    private void setUpSpinner() {
//        sms_send_frequence_id = (Spinner) view_body.findViewById(R.id.sms_send_frequence_id);
//        sms_stop_time_id = (Spinner) view_body.findViewById(R.id.sms_stop_time_id);
        //取出对应的设置
        long sms_send_frequence_time = VariableKeeper.mSp.getLong(VariableKeeper.APP_CONSTANT.sms_send_frequence_sp_name, 0);
        long sms_stop_time = VariableKeeper.mSp.getLong(VariableKeeper.APP_CONSTANT.sms_stop_during_sp_name, 0);
        if (sms_send_frequence_time == 0) {
            //默认每五分钟一次 即选中第0项
            sms_send_frequence_id.setSelection(0, true);
        } else if (sms_send_frequence_time == 5) {
            //默认每五分钟一次 即选中第0项
            sms_send_frequence_id.setSelection(0, true);
        } else if (sms_send_frequence_time == 10) {
            sms_send_frequence_id.setSelection(1, true);
        } else if (sms_send_frequence_time == 60) {
            sms_send_frequence_id.setSelection(2, true);
        } else if (sms_send_frequence_time == 120) {
            sms_send_frequence_id.setSelection(3, true);
        } else if (sms_send_frequence_time == 300) {
            sms_send_frequence_id.setSelection(4, true);
        }
        if (sms_stop_time == 0) {
            //默认为5小时后(300分钟) 即选中第2项
            sms_stop_time_id.setSelection(2, true);
        } else if (sms_stop_time == 20) {
            sms_stop_time_id.setSelection(0, true);
        } else if (sms_stop_time == 120) {
            sms_stop_time_id.setSelection(1, true);
        } else if (sms_stop_time == 300) {
            sms_stop_time_id.setSelection(2, true);
        } else if (sms_stop_time == 600) {
            sms_stop_time_id.setSelection(3, true);
        } else if (sms_stop_time == 888) {
            sms_stop_time_id.setSelection(4, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emergency_help_menu, menu);
        SystemUtil.log("menu is here in emergency...");
        return super.onCreateOptionsMenu(menu);
    }

    private void initTitle() {
        rvt = new MyTitlebar(EmergencyActivity.this);
        boolean ison = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, false);
        rvt.setShowRightBtn(true);
        rvt.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ison_ = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, false);
                if (ison_) {
                    //当前已经开启 进入关闭状态
                    Dialog dialog = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                    builder.setTitle(getResources().getString(R.string.emergency_sms_right_btn_emergency_off_title))
                            .setMessage(getResources().getString(R.string.emergency_sms_right_btn_emergency_off_msg))
                            .setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    dialog = null;

                                    rvt.setTextRightBtn(getResources().getString(R.string.emergency_sms_right_btn_on_tips));
                                    rvt.setTitle(getResources().getString(R.string.title_emergency_off));
                                    SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                                    editor.putBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, false);
                                    editor.commit();
                                }
                            }).setNegativeButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                } else {
                    //当前已经关闭 进入开启状态  首先要判断是否设定了紧急联络号码
                    LoadNumber();
                    if (phone_numbers == null || phone_numbers.size() == 0) {
                        SystemUtil.dialogJust4TipsShow(getResources().getString(R.string.emergency_sms_right_btn_emergency_on_error_titel), getResources().getString(R.string.emergency_sms_right_btn_emergency_on_error_msg));
                    } else if (!VariableKeeper.simStatus) {
                        SystemUtil.dialogJust4TipsShow(getResources().getString(R.string.dialog_tips_title), getResources().getString(R.string.emergency_sms_no_sim_error_msg));
                    } else {
                        rvt.setTextRightBtn(getResources().getString(R.string.emergency_sms_right_btn_off_tips));
                        rvt.setTitle(getResources().getString(R.string.title_emergency_on));
                        SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                        editor.putBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, true);
                        editor.commit();
                    }
                }
            }
        });
        if (ison) {
            //开启
            rvt.setTitle(getResources().getString(R.string.title_emergency_on));
            rvt.setTextRightBtn(getResources().getString(R.string.emergency_sms_right_btn_off_tips));

            //紧急救援模式如果正在运行时即用户当前遭遇危险 则标题闪动 提示用户当前已经在向亲人报告坐标位置了
//            mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.record_item_alpha)

        } else {
            //关闭
            rvt.setTitle(getResources().getString(R.string.title_emergency_off));
            rvt.setTextRightBtn(getResources().getString(R.string.emergency_sms_right_btn_on_tips));
        }

        rvt.setLeftBtnShow(true);
        rvt.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean ison = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, false);
        //紧急救援模式如果正在运行时即用户当前遭遇危险 则标题闪动 提示用户当前已经在向亲人报告坐标位置了
        TextView tv_title = null;
        if(ison){
            boolean isEmergency_now = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.is_emergency_model_now_sp_name, false);
            if(isEmergency_now){
                 tv_title = rvt.getTv_title();
                if (tv_title != null) {
                    mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.record_item_alpha);
                    tv_title.setTextColor(Color.RED);
                    tv_title.startAnimation(mAnimation);
                }else {
                    tv_title.setTextColor(Color.WHITE);
                    tv_title.clearAnimation();
                }
            }
        }else {
            tv_title = rvt.getTv_title();
            if (tv_title != null) {
                tv_title.setTextColor(Color.WHITE);
                tv_title.clearAnimation();
            }
        }
    }

    @Override
    public void stopAll() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.emergency_sms_save:
                String phoneNumer = user_emergency_phone_number.getText().toString();
                if (SystemUtil.phoneNumberCheck(phoneNumer)) {
                    savePhoneNumber(phoneNumer);
                    LoadNumber();
                    emergencyAdaptor.dataReload(phone_numbers);
                    emergencyAdaptor.notifyDataSetChanged();
                }
                break;
            case R.id.setting_sms_save:
                saveSmsSeting();
                closeSettingDialog();
                break;
            case R.id.setting_sms_cancel:
                closeSettingDialog();
                break;
            case R.id.menu_emergency_help_settings_send_frequence_title_close:
                closeSettingDialog();
                break;

        }
    }

    private void saveSmsSeting() {
        if (sms_stop_time_id != null && sms_send_frequence_id != null) {
            long send_setting_id = sms_send_frequence_id.getSelectedItemId();
            long stop_setting_id = sms_stop_time_id.getSelectedItemId();
            //send_setting_id ===> 5分钟 ,10分钟 ,60分钟, 120分钟, 300分钟
            //stop_setting_id ===> 20分钟, 120分钟, 300分钟, 600分钟

            if (send_setting_id == 0) {
                send_setting_id = 5;
            } else if (send_setting_id == 1) {
                send_setting_id = 10;
            } else if (send_setting_id == 2) {
                send_setting_id = 60;
            } else if (send_setting_id == 3) {
                send_setting_id = 120;
            } else if (send_setting_id == 4) {
                send_setting_id = 300;
            }
            if (stop_setting_id == 0) {
                stop_setting_id = 20;
            } else if (stop_setting_id == 1) {
                stop_setting_id = 120;
            } else if (stop_setting_id == 2) {
                stop_setting_id = 300;
            } else if (stop_setting_id == 3) {
                stop_setting_id = 600;
            } else if (stop_setting_id == 4) {
                stop_setting_id = 888;//此值表示用户会手工停止(以后会加入远程短信的方式来停止 即解除紧急救援模式)
            }

            SystemUtil.log("send_setting_id:" + send_setting_id + ",stop_setting_id:" + stop_setting_id);
            SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
            editor.putLong(VariableKeeper.APP_CONSTANT.sms_send_frequence_sp_name, send_setting_id);
            editor.putLong(VariableKeeper.APP_CONSTANT.sms_stop_during_sp_name, stop_setting_id);
            editor.commit();
            SystemUtil.MyToast(getResources().getString(R.string.emergency_help_settings_success));
        }
    }

    private void closeSettingDialog() {
        if (dialog_ != null && dialog_.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog_.dismiss();
                }
            });
        }
    }

    @Override
    public void onRefresh(final int position, Object data) {
        //刷新紧急联系人列表listview,flag为删除的是第几项

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                builder.setTitle(getResources().getString(R.string.dialog_tips_title))
                        .setMessage(getResources().getString(R.string.emergency_sms_phone_delete_confirm_msg))
                        .setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog = null;
                                boolean ison = VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_emergency_isOn, false);
                                if (ison) {
                                    if (phone_numbers.size() == 1) {
                                        SystemUtil.dialogJust4TipsShow(getResources().getString(R.string.dialog_tips_title), getResources().getString(R.string.emergency_sms_phone_delete_error_msg));
                                    } else {
                                        phone_numbers.remove(position);//更新数据
                                        savePhoneNumbers();
                                        emergencyAdaptor.dataReload(phone_numbers);
                                        emergencyAdaptor.notifyDataSetChanged();
                                    }
                                } else {
                                    phone_numbers.remove(position);//更新数据
                                    savePhoneNumbers();
                                    emergencyAdaptor.dataReload(phone_numbers);
                                    emergencyAdaptor.notifyDataSetChanged();
                                }
                            }
                        }).setNegativeButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialog = null;
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });


    }
}
