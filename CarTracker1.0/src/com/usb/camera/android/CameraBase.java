package com.usb.camera.android;

import android.graphics.Bitmap;
import com.cartracker.mobile.android.config.VariableKeeper;

/**
 * Created by jw362j on 11/16/2014.
 */
public abstract class CameraBase {
    public static final int IMG_WIDTH= VariableKeeper.APP_CONSTANT.IMG_WIDTH;
    public static final int IMG_HEIGHT=VariableKeeper.APP_CONSTANT.IMG_HEIGHT;


    //==================public native===========================
    public abstract int prepareCamera(int moduel);//采集卡做准备工作,做初始化,只执行一次,当此方法被调用后才会在取每一帧画面前调用takeImage以取新画面,此时的返回值和checkCamera函数的返回值含义一致即反应出镜头的分布状态
    public abstract int checkCamera();//系统工作时每次要感知当前有多少摄像头的时候调用（此方法一定会在prepareCamera方法之后调用,会调用一次或多次）返回值代表镜头分布状态最大15（1111）四镜头都存在，最小0（0000）四镜头都不存在, 8(1000)表示只有第一个镜头存在,  4(0100)表示只有第二个镜头存在  2(0010)表示只有第三个镜头存在  1(0001)表示只有最后一台镜头存在 ，0(0000)表示无镜头存在, 15(1111)表示四台镜头都存在,规则:四位二进制 如果某一位为1表示对应的镜头存在 为0表示对应的镜头不存在
    public abstract int takeImage(Bitmap bitmap1, Bitmap bitmap2,int flag);//方法的返回值表示本次返回的画面的真实分布情况 如果4个镜头都工作完好，那么返回值应该和flag参数的值一致 每次取新画面都执行 每秒执行约20次即20帧.从镜头取拼接好的画面 4合一或者单一,channel_flag的值一共5种情况, 0 代表只有单屏第一路信号显示, 1代表只有单屏第二路信号显示, 2代表只有单屏第三路信号显示, 3代表只有单屏第四路信号显示  4代表4屏共四路信号拼接显示
    public abstract void stopCamera();//关闭采集卡 程序退出
    //==================public native===========================
    public abstract void setCameraFlag(int flag);//设置采集多少镜头的画面 默认4台



    public abstract boolean[] getCameraExists();//返回当前的镜头存在状态 并不真的去和硬件通信以获取准确值 只是将上一次探测到的结果返回而已
    public abstract void setCameraExists(boolean[] currentCamera);//设置当前的镜头存在状态, 和硬件通信以获取准确值后立即调用此函数设置新值以供下次获取
    public abstract Bitmap[]getSplitedBitmap();
    public abstract Bitmap[] getBmps_Source();//对于av镜头而言 bitmaps长度为1 因为它的图片是拼接好的

}
