package com.cartracker.mobile.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.ui.adapter.DesktopFragmentPagerAdapter;
import com.cartracker.mobile.android.ui.root.RootGuideActivity;
import com.cartracker.mobile.android.util.SysEnvMonitor.MonitorHandler;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RootResultListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.UsbCableHarwareListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;

import java.util.ArrayList;

public class Desktop extends FragmentActivity implements MonitorHandler, ViewPager.OnPageChangeListener, View.OnClickListener, appStop,RootResultListener ,UsbCableHarwareListener {

    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine_0;
    private ImageView ivBottomLine_1;
    private ImageView ivBottomLine_2;
    private TextView tv_tab_features, tv_tab_settings, tv_tab_data;
    private ProgressDialog progress;
    private int currIndex = 0;
    private TextView tv_titlebar;

    /**
     * 桌面启动后先要判断手机是否有root权限 若无责弹出 下载root软件的对话框提示用户下载软件并自行root
     * root操作成功后后判断usb镜头的状态 看每一个镜头是否有766的文件读写权限 若无责修改镜头的权限(弹出镜头权限修改进度条)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//在4.0以及以上的系统上使home键有效
        setContentView(R.layout.desktop_console);
        VariableKeeper.setmCurrentActivity(Desktop.this);
        VariableKeeper.addStop(Desktop.this);
        SystemUtil.log("create desktop .....");
        VariableKeeper.usbCableHarwareListeners.add(Desktop.this);
        Init();
//     界面初始化完毕后启动检查手机root以及各项权限的动作
        permissionProcess();

    }
    private void permissionProcess(){
        if(VariableKeeper.VERSION_SDK_INT < VariableKeeper.APP_CONSTANT.minAndroidVersion){
            //提示用户当前系统版本太低 最低只支持3.0的系统
            SystemUtil.LowerVersionWarning();
        }else {
            //提示用户root操作,可能会耗时,单起一个线程
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemUtil.getRootStatus(Desktop.this);
                }
            }).start();

        }
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

    private void Init() {
        this.progress = new ProgressDialog(this);
        this.progress.setCancelable(false);//不可取消 强制等待usb线接入后自动取消
        this.progress.setCanceledOnTouchOutside(false);
        this.progress.setMessage("Loading....");
        ivBottomLine_0 = (ImageView) findViewById(R.id.iv_bottom_line_0);
        ivBottomLine_1 = (ImageView) findViewById(R.id.iv_bottom_line_1);
        ivBottomLine_2 = (ImageView) findViewById(R.id.iv_bottom_line_2);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;

        tv_tab_features = (TextView) findViewById(R.id.tv_tab_features);
        tv_tab_settings = (TextView) findViewById(R.id.tv_tab_settings);
        tv_tab_data = (TextView) findViewById(R.id.tv_tab_data);
        tv_titlebar = (TextView) findViewById(R.id.title_value);
        tv_tab_features.setTag("features");
        tv_tab_settings.setTag("settings");
        tv_tab_data.setTag("data");
        tv_tab_features.setOnClickListener(this);
        tv_tab_settings.setOnClickListener(this);
        tv_tab_data.setOnClickListener(this);


        mPager = (ViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();
        Fragment featuresFragment = new FeaturesFragment();
        Fragment settingsFragment = new SettingsFragment();
        Fragment dataFragment = new DataFragment();
        fragmentsList.add(featuresFragment);
        fragmentsList.add(settingsFragment);
        fragmentsList.add(dataFragment);
        mPager.setAdapter(new DesktopFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void stopAll() {
      if(!this.isFinishing()) finish();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        VariableKeeper.setmCurrentActivity(this);
        //当用户首次点击下载root工具的时候 浏览器会打开 于是屏幕失去焦点 当几分钟用户root完毕重新打开应用的时候系统需要再次自动探测当前的root状态
        permissionProcess();
    }



    @Override
    public void Track(Context context, Object data) {

    }



    @Override
    public void onPageSelected(int arg0) {
        switch (arg0) {
            case 0:
                if (currIndex == 1) {
                    tv_tab_settings.setTextColor(getResources().getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    tv_tab_data.setTextColor(getResources().getColor(R.color.lightwhite));
                }
                selectCurrent(0);
                tv_titlebar.setText(getResources().getString(R.string.desktop_title_features));
                tv_tab_features.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                if (currIndex == 0) {
                    tv_tab_features.setTextColor(getResources().getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    tv_tab_data.setTextColor(getResources().getColor(R.color.lightwhite));
                }
                selectCurrent(1);
                tv_titlebar.setText(getResources().getString(R.string.desktop_title_settings));
                tv_tab_settings.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                if (currIndex == 0) {
                    tv_tab_features.setTextColor(getResources().getColor(R.color.lightwhite));
                } else if (currIndex == 1) {
                    tv_tab_settings.setTextColor(getResources().getColor(R.color.lightwhite));
                }
                selectCurrent(2);
                tv_titlebar.setText(getResources().getString(R.string.desktop_title_data));
                tv_tab_data.setTextColor(getResources().getColor(R.color.white));
                break;


        }
        currIndex = arg0;
    }

    private void selectCurrent(int x){
        switch (x){
            case 0:
                ivBottomLine_0.setVisibility(View.VISIBLE);
                ivBottomLine_1.setVisibility(View.GONE);
                ivBottomLine_2.setVisibility(View.GONE);
                break;
            case 1:
                ivBottomLine_0.setVisibility(View.GONE);
                ivBottomLine_1.setVisibility(View.VISIBLE);
                ivBottomLine_2.setVisibility(View.GONE);
                break;
            case 2:
                ivBottomLine_0.setVisibility(View.GONE);
                ivBottomLine_1.setVisibility(View.GONE);
                ivBottomLine_2.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SystemUtil.log("keycode is:"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //如果处于刻录状态则发送通知栏消息
            SystemUtil.log("进入系统桌面主屏幕");
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if ("features".equals(tag)) {
            mPager.setCurrentItem(0);
        } else if ("settings".equals(tag)) {
            mPager.setCurrentItem(1);
        } else if ("data".equals(tag)) {
            mPager.setCurrentItem(2);
        }
    }

    @Override
    public void onRootResult(int execCode, Object data) {
        //此函数运行在子线程中 不能直接更新ui
        //当应用程序第一次进入桌面的时候 检查手机是否root
//        SystemUtil.MyToast(execCode+"");
        if(execCode == 0){
            sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.ACTION_SYSTEM_ROOT_NOTIFY));//通知组件手机已经被root 开始干活
            //先查看当前接入的摄像头设备是否具备0666权限 通过venderid和productid来判断 即和sp中存放的已经0666后的设别列表对比，如果发现有一台设备是新的，即转向镜头设置界面来对设备进行0666授权操作
            //设备具备root权限 已经成功root 此时检查usb相机的状态数目以及权限
            /**
             * 1:感知usb的接线状态
             * 2:探测到接线成功后提示用户usb设备连接概况 提示用户设备连接概况
             * 3:分析当前感知到的设备列表是否具备0666授权 如果没有则提示用户新设备到来 需要重新检查镜头 然后跳转到镜头检测界面 如果设备没有变动 并且在上一次启动的时候已经完成了0666授权 那么提示镜头正常工作并且用户可以全景刻录
             */

        }else {
           //设备具无root权限 引导用户去root向导界面
            showRootGuideChooseDialog();
        }
    }

    private void showRootGuideChooseDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                builder.setTitle(getResources().getString(R.string.desktop_root_remind_tips))
                        .setMessage(getResources().getString(R.string.desktop_root_remind_msg))
                        .setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent rootIntent = new Intent(Desktop.this, RootGuideActivity.class);
                                startActivity(rootIntent);
                                dialog.dismiss();
                                dialog = null;
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
        });
    }


    //此代码是在子线程中被调用的
    @Override
    public void onCableChange(int cableStatus, Object data) {
        //cableStatus 0为断开 非0为线缆接入
        if(cableStatus == 0){

        }else {
            //线缆接入
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (VariableKeeper.usbCableHarwareListeners != null &&VariableKeeper.usbCableHarwareListeners.contains(Desktop.this)) {
            VariableKeeper.usbCableHarwareListeners.remove(Desktop.this);
        }

    }
}
