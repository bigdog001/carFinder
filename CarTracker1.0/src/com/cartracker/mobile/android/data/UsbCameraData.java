package com.cartracker.mobile.android.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.VideoFrame;
import com.cartracker.mobile.android.ui.VideoPreview.DisplayModule;
import com.cartracker.mobile.android.util.PathGenerator;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.VideoCapture;
import com.cartracker.mobile.android.util.appBroadcast.BaseBroadCastRcv;
import com.cartracker.mobile.android.util.handler.impl.VideoRecordStopListener;
import com.usb.camera.android.CameraBase;
import com.usb.camera.android.CameraUtil;

/**
 * Created by jw362j on 10/24/2014.
 * 镜头通讯调度核心类
 */

public class UsbCameraData implements Runnable {
    private CameraBase cameraUtil;
    private int thread_flag = 1;
    private long total_video_frame;
    private DisplayModule displayModules[];
    private VideoFrame[] videoFrames;
    private VideoCapture[] videoCaptures;
    private int [] videoCapture_Lock;
    private Context mContext;
    private BroadcastReceiver receiver4Stop;
    private int camera_flag = 4;//默认的镜头采集策略 默认采集全屏画面 即有几个镜头就采集几个 假定4个镜头都存在
    private int current_camera_flag ;//镜头每次返回的画面帧的真实采集策略情况，当所有镜头都工作正常的时候此值应该和camera_flag一致
    private Bitmap[] splited;
    public UsbCameraData(Context context) {
        mContext = context;
        if (cameraUtil == null) {
            cameraUtil = new CameraUtil();
//            cameraUtil = new AvCameraUtil();
        }
        //初始化显示模型
        displayModules = new DisplayModule[VariableKeeper.APP_CONSTANT.size_num_cam];
        videoFrames = new VideoFrame[VariableKeeper.APP_CONSTANT.size_num_cam];
        videoCaptures = new VideoCapture[VariableKeeper.APP_CONSTANT.size_num_cam];
        videoCapture_Lock = new int[VariableKeeper.APP_CONSTANT.size_num_cam];
        for (int x = 0; x < VariableKeeper.APP_CONSTANT.size_num_cam; x++) {
            displayModules[x] = new DisplayModule(null, null);
            videoFrames[x] = new VideoFrame();
            videoCapture_Lock[x] =0;
        }
        receiver4Stop = new BaseBroadCastRcv() {
            @Override
            public void onSafeReceive(Context context, Intent intent) {
                thread_flag = 0;
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(100); // wait for thread stopping
                    } catch (Exception e) {
                        SystemUtil.log(e.getMessage());
                    }
                }
                for (Bitmap bm : cameraUtil.getBmps_Source()) {
                    if (bm != null && !bm.isRecycled()) bm.recycle();
                }
                cameraUtil.stopCamera();
                context.unregisterReceiver(receiver4Stop);
                receiver4Stop = null;
                onStopCamera();
            }
        };
        context.registerReceiver(receiver4Stop, new IntentFilter(VariableKeeper.APP_CONSTANT.ACTION_USB_CAMERA_COMPLETE_STOP));
    }

    @Override
    public void run() {
        while (thread_flag == 1 && (cameraUtil.getCameraExists()[0] || cameraUtil.getCameraExists()[1] || cameraUtil.getCameraExists()[2] || cameraUtil.getCameraExists()[3])) {
            SystemUtil.log("当前摄像头的存在值为:");
            SystemUtil.logArray(cameraUtil.getCameraExists());
            //cameraUtil.processCamera();

            current_camera_flag = cameraUtil.takeImage(cameraUtil.getBmps_Source()[0], null, camera_flag);//取画面完毕,所有的画面均被置入cameraUtil.getBmps_Source()[0]中 ,这个画面是最原始的从镜头过来的
            cameraUtil.setCameraFlag(current_camera_flag);//分割器会按照这个策略来切割图片
            splited = cameraUtil.getSplitedBitmap();
            //根据每帧的真实情况current_camera_flag这个值来分割画面

            if (current_camera_flag != camera_flag) {
                //此时说明我们想取的画面没有正常的返回过来 比如我想取4画面 但是却返回了2画面,换句话说 入参camera_flag只是代表我们的画面索取意图 而current_camera_flag才是真正从硬件镜头上出来的画面分布情况
                //处理完后将实际的镜头数目赋值给camera_flag，以便下次正常取值
                camera_flag = current_camera_flag;
            }
            if(splited == null){
                SystemUtil.log("切割后的图片为空....重新获取画面...");
                continue;
            }
            SystemUtil.log("camera status cameraUtil.cameraExists[0]:"+cameraUtil.getCameraExists()[0]+",cameraUtil.cameraExists[1]:"+cameraUtil.getCameraExists()[1]);

            for(int x = 0 ;x < splited.length;x++) {
                if (splited[x] != null && cameraUtil.getCameraExists()[x]) {
                    videoFrames[x].setTimestampt(System.currentTimeMillis());
                    videoFrames[x].setFregment_cach_path(VariableKeeper.system_file_save_BaseDir + VariableKeeper.cachFolderName + x+"_" + videoFrames[x].getTimestampt() + ".jpg");//不缓存画面数据的话其实Fregment_cach_path变量没有意义
                    videoFrames[x].setBitmap(splited[x]);
                    //SystemUtil.save2Cach(videoFrames[x].getFregment_cach_path(), SystemUtil.Bitmap2Bytes(cameraUtil.bmp[x], VariableKeeper.video_record_quality));//录像清晰度调整
                    VariableKeeper.bitMapHolders[x].add(new VideoFrame(videoFrames[x].getTimestampt(), videoFrames[x].getFregment_cach_path(),SystemUtil.getIp1ImageFromBitmap(videoFrames[x].getBitmap())));//数据入队列暂存,那么缓冲队列中的videoFrames只有时间戳和数据的路径,所有此时缓冲区是一个轻量级的东西了
                    //启动对应的刻录组件
                    if (videoCaptures[x] == null && videoCapture_Lock[x] == 0 && VariableKeeper.recordStatuses[x].equals(CameraRecordStatus.START)) {
                        videoCaptures[x] = new VideoCapture(PathGenerator.GeneratorCurrentPath(), x ,  VariableKeeper.bitMapHolders[x],null);
                        videoCaptures[x].setRecordListener(new VideoRecordStopListener(videoCaptures[x]));
                        videoCaptures[x].start();
                        videoCapture_Lock[x] = 1;
                    }
                }
            }

            //判断是否应该准备显示模型
            if (VariableKeeper.videoFrameListener != null) {
                //预览模块将自己注册上来了 此情况下需要给预览模块准备数据,即填充displayModules中的数据
                SystemUtil.log("预览界面已经启动..................");
                for (int i = 0; i < displayModules.length; i++) {
                    displayModules[i].setVideoFrame(videoFrames[i]);
                }
                VariableKeeper.videoFrameListener.onVideoFrame(displayModules);
            } else {
                SystemUtil.log("预览界面已经关闭..................需要立即销毁bitmap显示对象");
                for (VideoFrame vf:videoFrames) {
                    if (vf != null && vf.getBitmap()!=null) {
                        if(!vf.getBitmap().isRecycled()){
                            vf.getBitmap().recycle();
                            vf.setBitmap(null);
                        }
                    }
                }
            }
            SystemUtil.log("one more video frame:" + total_video_frame++ + ":" + cameraUtil.getBmps_Source()[0]);
        }

        SystemUtil.log("停机事件发生或者镜头一个都不存在.....");
        if (receiver4Stop != null) {

        }






    }


    private void onStopCamera(){
            cameraUtil = null;
            displayModules = null;
            videoFrames = null;
    }
}



