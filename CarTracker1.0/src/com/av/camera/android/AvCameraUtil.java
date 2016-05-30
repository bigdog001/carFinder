package com.av.camera.android;

import android.graphics.Bitmap;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.AVSingnelFrame;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.impl.AvFrameProcesser;

/**
 * 此时av镜头送过来的画面为一副拼接好的大图 不需要Bitmap[] bmp数组的盛装了
 */
public class AvCameraUtil extends AvCameraBase{
    private InterfaceGen.VideoFrameSplitter splitter; //图像分割器
    private int currenCamera;
	public Bitmap [] bmps ;
	public boolean[] cameraExists ;
    public static final int IMG_WIDTH= VariableKeeper.APP_CONSTANT.IMG_WIDTH;
    public static final int IMG_HEIGHT=VariableKeeper.APP_CONSTANT.IMG_HEIGHT;

    public native int prepareCamera(int moduel);//采集卡做准备工作,做初始化,只执行一次,当此方法被调用后才会在取每一帧画面前调用takeImage以取新画面
    public native int checkCamera();//系统工作时每次要感知当前有多少摄像头的时候调用（此方法一定会在prepareCamera方法之后调用,会调用一次或多次）返回值代表镜头分布状态最大15（1111），最小0（0000）, 8(1000)表示只有第一个镜头存在,  4(0100)表示只有第二个镜头存在  2(0010)表示只有第三个镜头存在  1(0001)表示只有最后一台镜头存在 ，0(0000)表示无镜头存在, 15(1111)表示四台镜头都存在,规则:四位二进制 如果某一位为1表示对应的镜头存在 为0表示对应的镜头不存在
    public native int takeImage(Bitmap bitmap1,Bitmap bitmap2,int channel_flag);//每次取新画面都执行 (bitmap1 为从av镜头取出的画面 bitmap2为不用的占位参数)每秒执行约20次即20帧.从镜头取拼接好的画面 4合一或者单一,channel_flag的值一共5种情况, 0 代表只有单屏第一路信号显示, 1代表只有单屏第二路信号显示, 2代表只有单屏第三路信号显示, 3代表只有单屏第四路信号显示  4代表4屏共四路信号拼接显示
    public native void stopCamera();//关闭采集卡 程序退出
    private int camera_flag;

    static {
        System.loadLibrary("avcamera");
    }


    @Override
    public void setCameraExists(boolean[] currentCamera) {
        this.cameraExists = currentCamera;
    }

    @Override
    public Bitmap[] getSplitedBitmap() {
        //av镜头的分割操作在这里进行,图片的分割源来自镜头采集到的数据  根据镜头数目以及分布情况开始分割画面,画面的分布情况存储在cameraExists数组中, 好处是可以随时感知到画面的变动以及时的变更切割策略
        return splitter.onSplit(new AVSingnelFrame(System.currentTimeMillis(),cameraExists,bmps[0]));
    }

    @Override
    public void setCameraFlag(int flag) {
        this.camera_flag = flag;
    }

    @Override
    public Bitmap[] getBmps_Source() {
        return bmps;
    }

    @Override
    public boolean[] getCameraExists() {
        return cameraExists;
    }

	public AvCameraUtil() {
        bmps  = new Bitmap[VariableKeeper.APP_CONSTANT.size_num_cam];
        //接收从采集卡过来的大幅拼接画面
        for(int i=0 ; i<VariableKeeper.APP_CONSTANT.size_num_cam ; i++){
            if(bmps[i]==null){
               bmps[i] = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);//只初始化第一副图 因为av镜头是个大的合成bitmap
            }
        }

        splitter = new AvFrameProcesser();//对画面进行分割
        int ret= prepareCamera(0);
        SystemUtil.log("摄像头的存在状态数:" + ret);
        setCameraExists(SystemUtil.Int2BolArray(ret));
	}

   
	    
}
