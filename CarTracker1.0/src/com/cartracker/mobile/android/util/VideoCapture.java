package com.cartracker.mobile.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.RecordStatus;
import com.cartracker.mobile.android.data.beans.VideoFrame;
import com.cartracker.mobile.android.util.SysEnvMonitor.MonitorHandler;
import com.cartracker.mobile.android.util.appBroadcast.BaseBroadCastRcv;
import com.cartracker.mobile.android.util.handler.InterfaceGen.VideoRecordListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;
import com.cartracker.mobile.android.util.record.MyFfmpegRecordor;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.FrameRecorder.Exception;
import com.googlecode.javacv.cpp.opencv_core;

import java.io.File;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

/**
 * convertFromBitmaptoVideo
 *
 * @author yanjiaqi  qq:985202568
 */
public class VideoCapture implements appStop {
    private int switcher = 0;//录像键
    private String filepath = "";
    private FrameRecorder recorder;
    private int start_flag = 0;

    //capture stop listener
    private VideoRecordListener recordListener;//负责监听此线程的刻录动作 比如开始刻录 结束刻录等
    private MonitorHandler uiMonitorHandler;
    private RecordStatus recordStatus;
    private BitMapHolder currentBitMapHolder;
    private Context mContext;
    private BroadcastReceiver receiver;
    private Thread videoCaptureThread;
    private VideoFrame frame;//线程正在刻录的当前帧
    private int current_id;
    private opencv_core.IplImage iplImage4record;
    private int recorded_frame_total = 0;
    private int[] tmp_ = new int[2];
    public int video_record_flag = 0;//0为录像文件切换 1为录像线程停止并exit 此变量标记停止状态且由外部控制(只是在停止录像刻录的一个单一时刻调用) 如果不标记那么停止后下一次再启动刻录的话会在单位刻录时间后自动停止  以后升级成多镜头单独控制的时候这个标记要改进

    public Thread getVideoCaptureThread() {
        return videoCaptureThread;
    }

    public void setUiMonitorHandler(MonitorHandler uiMonitorHandler) {
        this.uiMonitorHandler = uiMonitorHandler;
    }


    public VideoCapture(String path, final int id_thread, final BitMapHolder bitmapHolder, VideoRecordListener vrl) {
        VariableKeeper.addStop(this);
        current_id = id_thread;
        iplImage4record = opencv_core.IplImage.create(VariableKeeper.APP_CONSTANT.IMG_WIDTH, VariableKeeper.APP_CONSTANT.IMG_HEIGHT, IPL_DEPTH_8U, 4);
        this.recordListener = vrl;
        this.currentBitMapHolder = bitmapHolder;
        this.recordStatus = new RecordStatus();
        this.filepath = path;
        this.mContext = VariableKeeper.context;
        this.receiver = new BaseBroadCastRcv() {

            @Override
            public void onSafeReceive(Context context, Intent intent) {
                SystemUtil.log("switch the video file process,hahahha...." + current_id);
                recorderThreadRestart();//此函数一定要在Stop函数之前 否则线程的自我繁衍失败 只能工作一个单位脉冲时间
                Stop();
                mContext.unregisterReceiver(receiver);//后续即使有脉冲信号过来也不再重启自身 完全停掉刻录机
            }
        };
        this.mContext.registerReceiver(receiver, new IntentFilter(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_FILE_SWITCH));
        recordStatus.setOld_path_dir(filepath + id_thread + File.separator);
        File f = new File(recordStatus.getOld_path_dir());
        if (!f.exists()) f.mkdirs();
        recordStatus.setOld_fileName(id_thread + "_" + PathGenerator.GeneratorCurrentName());
        this.recorder = new MyFfmpegRecordor(recordStatus.getOld_path_dir() + recordStatus.getOld_fileName(), VariableKeeper.APP_CONSTANT.IMG_WIDTH, VariableKeeper.APP_CONSTANT.IMG_HEIGHT);
        if (VariableKeeper.videoCaptures[id_thread] != null) {
            VariableKeeper.videoCaptures[id_thread].stopAll();
        }
        VariableKeeper.videoCaptures[id_thread] = this;
    }

    private void recorderThreadRestart(){
        if (video_record_flag == 0) {//0为录像文件切换 1为录像线程停止并exit
            VideoCapture videoCapture_ = new VideoCapture(PathGenerator.GeneratorCurrentPath(), current_id, currentBitMapHolder, recordListener);
            videoCapture_.setUiMonitorHandler(uiMonitorHandler);
            videoCapture_.start();
        }
    }

    public void setRecordListener(VideoRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void start() {
        if (start_flag != 0) {
            SystemUtil.log("you guys can not start again.just only one time........");
            return;
        }
        switcher = 1;
        videoCaptureThread = new Thread() {
            public void run() {
                try {
                    recorder.start();
                    if (recordListener != null) {
                        recordListener.onStartRecord(recordStatus);
                    }
                } catch (Exception e) {
                    SystemUtil.log(e.getMessage());
                    e.printStackTrace();
                }
                SystemUtil.log("编号为VideoCapture" + current_id + "的刻录线程其threadid为" + getId() + ",其线程name是" + getName());
                try {
                    while (switcher != 0) {
                        if (currentBitMapHolder != null) {
                            frame = currentBitMapHolder.get();//这个frame里面其实什么也没有 只有时间戳和画面的路径地址值以及画面对应的IplImage实例
                            iplImage4record = frame.getIplImage4record();// convertFileToIp1Image(frame.getFregment_cach_path());
                            recordIt();
                        } else {
                            SystemUtil.log("bitMapHolder is null ,bye.....thread id is:" + recordStatus.getThreadID());
                            recordStatus.setRecordDescribe("bitMapHolder is null ,bye.....thread id is:" + recordStatus.getThreadID());
                            break;
                        }
                    }

                    /**
                     * 有2中情况会跳出这个循环 代码到达此处
                     * 1  文件切换 --->此时缓冲区会有剩余文件 这些文件代表的画面帧是安全的 会有后续的线程来继续工作
                     * 2  录像停止 --->此时缓冲区中的画面帧是危险的 线程停止后无后续处理 对这种情况 此处需要做收尾处理将缓冲区中的数据消耗完
                     */
                    while (video_record_flag == 1) {
                        //录像数据帧收尾
                        if (currentBitMapHolder.getSize() == 0) {
                            break;
                        }
                        frame = currentBitMapHolder.get();//这个frame里面其实什么也没有 只有时间戳和画面的路径地址值
                        iplImage4record = frame.getIplImage4record();
                        recordIt();
                    }
                    switcher = 0;
                    recorder.stop();
                    recorder.release();
                    if(iplImage4record != null) iplImage4record.release();
                    recordListener.onStopRecord(recordStatus);

                    recorder = null;
                    iplImage4record = null;

                } catch (Exception e) {
                    try {
                        recorder.stop();
                        recorder.release();
                    } catch (Exception e1) {
                        SystemUtil.log(e1.getMessage());
                    }finally {
                        recordListener.onStopRecord(recordStatus);
                        iplImage4record.release();
                        recordListener.onRecordException(null);
                        //异常发生后立即切断当前线程的运行 并重启自己
                        Stop();//线程彻底死亡 receiver被卸载
                        recorderThreadRestart();//线程此时其实是无法自我繁衍的 Stop函数已经将标记置为1了,后续线程重启的操作将会由专门的组件来执行
                    }
                    SystemUtil.log(e.getMessage());
                }
            }
        };
        videoCaptureThread.start();
    }

    private void recordIt() {

        if (iplImage4record != null) {
            try {
                recorder.record(iplImage4record);
            } catch (Exception e) {
                recordListener.onStopRecord(recordStatus);
                SystemUtil.log(e.getMessage());
                e.printStackTrace();
            }
           // new File(frame.getFregment_cach_path()).delete();
            recorded_frame_total++;
            tmp_[0] = recorded_frame_total;
            tmp_[1] = current_id;
            if (uiMonitorHandler != null) {
                uiMonitorHandler.Track(null, tmp_);
            }
            SystemUtil.log("record one more frame ........." + frame.getFregment_cach_path());
        } else {
            SystemUtil.log("画面畸形 帧的长度或宽度居然小于或者等于0");
        }
    }



    public void Stop() {
//        video_record_flag = 1;//标记让线程停掉不再重新启动自己,为了防止再次启动录像进程的时候只录一次便退出 则在每次刻录进行前将其值置0
        switcher = 0;//让正在工作的线程安全退出
    }

    public boolean isStarted() {
        return switcher == 1 ?true:false;
    }

    public VideoFrame getFrame() {
        //取刻录机上现在处理的当前帧
        return frame;
    }

    @Override
    public void stopAll() {
        Stop();
    }
}
