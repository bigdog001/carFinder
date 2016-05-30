package com.cartracker.mobile.android.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.cartracker.mobile.android.data.BitMapHolderInit;
import com.cartracker.mobile.android.data.CameraRecordStatus;
import com.cartracker.mobile.android.data.beans.UsbCameraDevice;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.util.BitMapHolder;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.VideoCapture;
import com.cartracker.mobile.android.util.appBroadcast.BroadCastManager;
import com.cartracker.mobile.android.util.handler.InitManager;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RecordThreadStatusListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.UsbCableHarwareListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;
import com.cartracker.mobile.android.util.handler.impl.MySensorEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by jw362j on 9/18/2014.
 */
public class VariableKeeper {
    public static Context context;
    public static String sdCard_mountRootPath = "";
    public static long system_lastGC_ExecuteTime;//上次gc执行的时间点
    public static long system_last_FILE_SWITCH_Time = System.currentTimeMillis();//  上次文件切换的时间
    public static long system_startupTime;//系统启动的时间点
    public static long sdCard_CurrentFreeSize = 0;//当前剩余空间
    public static String system_file_save_BaseDir = "";
    public static boolean logMode = true;
    public static boolean isCardExist;
    public static boolean isUsingBusyBox = false;
    public static boolean isVideoDirScannable = true; //决定是否对/dev/video*目录进行扫描的标记 默认扫描一次即程序启动后第一次而后即将其置false 当有新usb设备插上来的时候置true 扫描完毕再置false  或者用户手工想扫描时候也同样的原理
    private static Activity mCurrentActivity;
    public static Stack<BaseActivity> activityStack = new Stack<BaseActivity>();
    public static String mac = "000000";
    public static String LogFolderName = "logs" + File.separator;
    public static String cachFolderName = "cach" + File.separator;
    public static String VideoFolderName = "video";
    public static String current_video_folder_path = "";//目前正在刻录视频所存储的直接目录
    public static SharedPreferences mSp;
    //当前是否处于监控预览模式 ，0表示非预览，那么刻录线程可以将当前帧销毁 否则就不承担销毁任务，因为当前帧需要被预览界面用于绘制监控屏幕
    public static int isCurrent_preview = 0;
    private static List<appStop> stops = new ArrayList<appStop>();
    public static boolean isRooted = false;//是否被root过 当界面一启动就先对此值做初始化
    //系统当前的版本信息=========================
    public static int VERSION_SDK_INT;
    public static String VERSION_SDK;
    public static String Version_RELEASE;
    public static String Build_MODEL;
    //系统当前的版本信息=========================

    public static float density;// 屏幕密度（0.75 / 1.0 / 1.5）
    public static int screenResolution; //屏幕分辨率
    public static int screenWidth;
    public static int screenHeight;
    public static String screen = "";
    public static String fromid = "";
    public static String appVersionName = "";

    public static boolean simStatus;//true 为智能手机 false为平板电脑
    //用以盛装几个信号发生器以监控视频信号的产生情况
    public static BitMapHolderInit[] bitMapHolderInits = new BitMapHolderInit[VariableKeeper.APP_CONSTANT.size_num_cam];
    //用于承装当前正在执行刻录任务的n个VideoCapture对象实例,videoCaptures可以做监控系统工作状态的调整用
    public static VideoCapture[] videoCaptures = new VideoCapture[VariableKeeper.APP_CONSTANT.size_num_cam];
    public static BitMapHolder[] bitMapHolders = new BitMapHolder[VariableKeeper.APP_CONSTANT.size_num_cam];//视频信号缓冲区
    public static InterfaceGen.VideoFrameListener videoFrameListener;//监控显示屏的画面刷新监听器 当用户启动监控屏的时候CameraPreview会将自己注册到这来来 退出CameraPreview后自动将自己移除
    public static List<RecordThreadStatusListener> threadStatusListeners;//监视视频刻录对象
    public static List<UsbCableHarwareListener> usbCableHarwareListeners;
    public static List<InterfaceGen.onSpeedChange> speedChanges; //所有想监视震动事件的都将自己注册到这来
    public static UsbCameraDevice[] extendedUsbCameraDevices; //所有被感知探测到的外接usb相机设备都存入此容器,每发现一个视频设备就往此容器丢入一个直到容器满,usb相机画面读取程序从此读取需要的画面  一共四台
    public static Map<String, UsbCameraDevice> usbDevices_All; //所有被感知探测到的外接usb设备 包括相机 U盘键盘等
    public static UsbCameraDevice[] SystemGlobalVideoDevices;//所有被挂载到/dev/下的video* 设备
    public static int USB_CABLE_CONNECT_STATUS = 0;
    public static String app_path;//对应到目录/data/data/com.cartracker.mobile.android/files下
    //    public static String busybox_cmd;//对应到目录/data/data/com.cartracker.mobile.android/files/busybox命令下
    public static String busybox_cmd_name;
    public static String three6_permission_device_sp_name = "product_permission_infor";//存储已经获得0666权限的设备venderid和productid到sp中venderid1:productid1,venderid2:productid2
    public static String three6_permisssion_string = "rw-rw-rw";//0666
    public static String three7_permisssion_string = "rwxrwxrwx";//0777
    public static String three6_permisssion_code = "0666";
    public static MySensorEventListener sensorEventListener;
    public static int video_record_quality = 100;//画面的录制清晰度质量  此值从video_record_quality_sp_name变量对应的sp中读取 程序启动的时候会自动初始化此值
    public static int video_record_quality_lowest = 30;//画面的录制清晰度质量的最小值
    public static int stop_min_battery_level = 5;//电量低于此值的时候关闭镜头
    public static CameraRecordStatus[] recordStatuses;//在画面刻录前会依次判断对应的镜头采集到的画面是否将被刻录 如不被刻录即只在显示的时候使用  升级版本会采取关闭镜头的做法


    //=========================hardware variables======================
    public static String IMEI = "";//唯一的设备ID：   GSM手机的 IMEI 和 CDMA手机的 MEID.
    //=========================hardware variables======================

    public static final class APP_CONSTANT {
        public static final String ApiHost = "http://www.recorder360.cn/api.html";
        public static final long system_lastGC_DeltaTime = 10 * 1000 * 60;//两次gc执行的时间差最小值
        public static final long INTERVAL_FILESWITCH = 1000 * 60 * 20;//录制文件的切换频率,每20分钟切换一次
        public static final long INTERVAL_UNIT = 1000 * 3;//系统广播脉冲,每3秒一次
        public static final String sp_holder_filename = "cartracker";
        public static final String base_folder_name = "cartracker";
        public static final String DEBUG_SWITCHER_FILENAME = "ceshi.jpg";
        public static final int video_source_flag = 0; //0为程序模拟视频输入 1为摄像头
        public static final int size_num_cam = 4;
        public static final int size_num_cam_invalid_video = 3;//此时为3 则模拟一路电视信号测试图出来
        public static final int sampleAudioRateInHz = 44100; //视频采样率
        public static final int BitMapHolderMax = 10000; //视频帧缓冲区里的视频帧最大值
        public static final int BitMapHolderMin = 50; //视频帧缓冲区里的视频帧最小值
        public static final int IMG_WIDTH = 640;
        public static final int IMG_HEIGHT = 480;
        public static final String TIMER_BROADCAST_UNIT_NAME = "com.cartracker.mobile.android.TIMER_BROADCAST";
        public static final String TIMER_BROADCAST_FILE_SWITCH = "com.cartracker.mobile.android.FILE_SWITCH";
        public static final String ACTION_BATTERY_CHANGED = Intent.ACTION_BATTERY_CHANGED;
        public static final String LOWER_FREE_SDSIZE = "com.cartracker.mobile.android.sdcard_fresssize_toolow";//刻录模块发现当前低存储空间时发出此广播，将会被sd卡剩余空间管理器截获并处理
        public static final String CLEAN_SDSIZE_COMPLETE = "com.cartracker.mobile.android.sdcard_clean_complete";// 负责被sd卡存储空间整理工作的组件 SDCardLowerSizeCleaner 在整理完毕后发出此广播
        public static final String USB_DEVICE_QUERY = "com.cartracker.mobile.android.usb_device_query";// 检测当前手持设备上挂接的所有外接usb设备 涵盖外接usb uvc相机以及其它设备
        public static final String USB_GLOBAL_VIDEO_DEVICE_QUERY = "com.cartracker.mobile.android.usb_global_device_query";// 检测当前手持设备上挂接的所有usb设备 涵盖usb uvc相机以及其它设备 以及系统设备自带的前后摄像头/dev/video0 ,/dev/video1
        public static final String ACTION_USB_PERMISSION = "com.cartracker.mobile.android.USB_PERMISSION";//新接入的设备授权
        public static final String ACTION_SYSTEM_ROOT_NOTIFY = "com.cartracker.mobile.android.ROOT";//设备被root后发出广播通知对应的组件干活
        public static final String ACTION_USB_CAMERA_COMPLETE_STOP = "com.cartracker.mobile.android.CAMERA.ALL.STOP";//设备被root后发出广播通知对应的组件干活
        public static final String ACTION_SMS_RCV = "android.provider.Telephony.SMS_RECEIVED";//接收短信
        public static final String ACTION_CONN_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";//监听连接状态改变
        //    public static boolean isVideoOverWrite = true;//系统是否工作在复写模式下 默认为true 当sd卡存储空间快消耗完时自动删除最旧的文件 ,当为false的时候目的是为了尽可能多的保留视频资料 空间耗尽将停止刻录 现改为由sp中存储的变量来替代,默认为不自动覆盖旧录像
        private static final int record_MB_perMin = 20;//预计四台刻录设备每分钟一共消耗的存储空间 20MB 此值为预设值 在程序运行中会不断根据真实数据进行修正 以达到接近准确的范围
        //此值应该设定为够刻录线程工作十分钟之用的空间 最小空闲空间只能支持系统工作10分钟,此10分钟期间系统处于危机模式 即无法再手工点击录像(isVideoOverWrite = true模式除外),只能等最后的刻录线程退出则不再刻录
        public static final long sdCard_minFreeSize = record_MB_perMin * 10;//200MB,当存储设备只剩余五分钟期限时进入语音告警模式
        public static final long sdCard_cleanSize = record_MB_perMin * 30;//每次sd卡的删除操作时候将会给系统预留出30分钟的工作空间
        public static final int minAndroidVersion = 12;//系统版本最低android3.1,12对于3.1的系统
        //========================以下变量在SettingsFragment中使用========================
        public static final String sp_name_video_rewrite = "videoRewrite";
        public static final String sp_name_sound_alert = "soundAlert";
        //========================以下变量在SettingsFragment中使用========================
        public static final String system_permission_directory = "/system/etc/permissions/";
        public static final String system_permission_host_fileName = "android.hardware.usb.host.xml";
        public static final boolean system_permission_host_fileName_copy_flag = true;//是否自动在root条件下拷贝android.hardware.usb.host.xml文件到/system/etc/permissions下
        public static final String error_code_name = "error_code";
        public static final String error_msg_name = "error_msg";
        //    public static final String USB_DEVICE_STATUS = "android.hardware.usb.action.USB_STATE";// usb设备插拔的广播
//    public static final String USB_DEVICE_STATUS = "android.hardware.usb.action.USB_STATE";// usb设备插拔的广播
        public static final int sensor_vibrate_Level = 19;//震动事件发生时的动作级别 越大表示震动越强烈
        public static final int sensor_vibrate_duringTime = 150;//震动事件发生时手机震动150毫秒
        //========紧急短信发送配置参数=============
        public static final String sp_name_emergency_number = "emergencynumber";//所有的紧急联系人手机号码都以这个名字保存到sp中
        public static final String sp_name_emergency_isOn = "emergencyison";//在sp中存储是否开启紧急模式 即手机的此功能是否打开
        public static final String sms_send_frequence_sp_name = "smssendfrequence";//long型 含义为多少分钟 紧急求救短信的发送频率 默认每五分钟一次
        public static final String sms_stop_during_sp_name = "smsstopdure";//long型 含义为多少分钟 紧急短信从开始发送到停止发送解除紧急情况的一般时间 默认为5小时(300分钟)
        public static final String sms_send_lasttime_sp_name = "smslastsendtime";//long型 上次紧急求救短信的送出时间
        public static final String is_emergency_model_now_sp_name = "isemergencynow";//boolean型 紧急救援功能打开后 (危险已经发生 用户如果解除紧急救援模式则值false)判断当前是否仍然处于紧急救援模式
        //========紧急短信发送配置参数=============
        public static final String video_record_quality_sp_name = "videorecordquality";
    }



    public static void init(Context con) {
        SystemUtil.log("system init............");
        context = con;
        //初始化应用程序层广播
        BroadCastManager broadCastManager = new BroadCastManager();
        //系统初始化 在系统初始化前 各种广播必须注册上
        InitManager initManager = new InitManager(context);
    }

    public static Activity getmCurrentActivity() {
        return mCurrentActivity;
    }

    public static void setmCurrentActivity(Activity currentActivity) {
        mCurrentActivity = currentActivity;
    }

    public static void addStop(appStop stop) {
        if (!stops.contains(stop)) {
            stops.add(stop);
        }
    }

    public static void stopApp() {
        for (appStop stop : stops) {
            if (stop != null) {
                stop.stopAll();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }


}
