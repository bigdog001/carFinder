package com.usb.camera.android;

import android.graphics.Bitmap;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.AVSingnelFrame;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.impl.AvFrameProcesser;

public class CameraUtil extends UsbCameraBase{
	private static final String TAG="myusbcamera";
	public Bitmap[] bmps ;
	public boolean[] cameraExists ;
    public static final int IMG_WIDTH= VariableKeeper.APP_CONSTANT.IMG_WIDTH;
    public static final int IMG_HEIGHT=VariableKeeper.APP_CONSTANT.IMG_HEIGHT;
    private int camera_flag;


    public native int prepareCamera(int moduel);//采集卡做准备工作,做初始化,只执行一次,当此方法被调用后才会在取每一帧画面前调用takeImage以取新画面
    public native int checkCamera();//系统工作时每次要感知当前有多少摄像头的时候调用（此方法一定会在prepareCamera方法之后调用,会调用一次或多次）返回值代表镜头分布状态 8(1000)表示只有第一个镜头存在,  4(0100)表示只有第二个镜头存在  2(0010)表示只有第三个镜头存在  1(0001)表示只有最后一台镜头存在 ，0(0000)表示无镜头存在, 15(1111)表示四台镜头都存在,规则:四位二进制 如果某一位为1表示对应的镜头存在 为0表示对应的镜头不存在
    public native int takeImage(Bitmap bitmap1, Bitmap bitmap2,int flag);//每次取新画面都执行 每秒执行约20次即20帧.从镜头取拼接好的画面 4合一或者单一,channel_flag的值一共5种情况, 0 代表只有单屏第一路信号显示, 1代表只有单屏第二路信号显示, 2代表只有单屏第三路信号显示, 3代表只有单屏第四路信号显示  4代表4屏共四路信号拼接显示
    public native void stopCamera();//关闭采集卡 程序退出




    public native int prepareCameraWithBase(int videoid, int videobase);
    public native void processCamera();
    public native void processRBCamera(int lrmode);


    @Override
    public void setCameraFlag(int flag) {
        //此方法在每取到一帧画面后都会被调用 可以动态的感知镜头的分布状态
        this.camera_flag = flag;
        //当镜头工作正常后需要打开这行 以便于实时感知镜头的状态
//        setCameraExists(SystemUtil.Int2BolArray(this.camera_flag));
    }


    @Override
    public Bitmap[] getBmps_Source() {
        return bmps;
    }


    @Override
    public void setCameraExists(boolean[] currentCamera) {
        this.cameraExists = currentCamera;
    }

    @Override
    public Bitmap[] getSplitedBitmap() {
        //原封不动的返回bmps数组
//        return bmps;
        return splitter.onSplit(new AVSingnelFrame(System.currentTimeMillis(),cameraExists,bmps[0]));
    }


    @Override
    public boolean[] getCameraExists() {
        return cameraExists;
    }

    static {
    	System.loadLibrary("jpeg");
        System.loadLibrary("mycamera");
    }
    private InterfaceGen.VideoFrameSplitter splitter; //图像分割器
	public CameraUtil() {
        bmps  = new Bitmap[VariableKeeper.APP_CONSTANT.size_num_cam];
		for(int i=0 ; i<VariableKeeper.APP_CONSTANT.size_num_cam ; i++){
			if(bmps[i]==null){
				bmps[i] = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
			}
		}
        int ret = prepareCamera(0);//镜头硬件做准备，并探测镜头的分布状态
        SystemUtil.log("摄像头的存在状态数:"+ret);
        setCameraExists(SystemUtil.Int2BolArray(ret));
        splitter = new AvFrameProcesser();//对画面进行分割
		cameraExists[0] = ((ret>>0)&0x1) == 1;
		cameraExists[1] = ((ret>>0)&0x1) == 1;
		cameraExists[2] = ((ret>>0)&0x1) == 1;
		cameraExists[3] = ((ret>>0)&0x1) == 1;

	}

   
	    
}
