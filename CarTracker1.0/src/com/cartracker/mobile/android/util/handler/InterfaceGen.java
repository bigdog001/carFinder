package com.cartracker.mobile.android.util.handler;

import android.content.Context;
import android.graphics.Bitmap;
import com.cartracker.mobile.android.data.beans.AVSingnelFrame;
import com.cartracker.mobile.android.data.beans.RecordStatus;
import com.cartracker.mobile.android.ui.VideoPreview.DisplayModule;
import com.cartracker.mobile.android.util.VideoCapture;

import java.util.List;

/**
 * Created by jw362j on 10/13/2014.
 */
public class InterfaceGen {
    public interface appStop {
        public void stopAll();
    }

    public interface Initializer {
        public void init(Context context);
    }

    public interface FileListsRefreshListener {
        public void fileRefresh();
    }

    public interface CameraPreviewListener {
        public void listen(int channelID);
    }

    public interface PermissionFileHandlerListener {
        public void onPermission(boolean flag, Object data);//  /system/etc/permissions/目录下的文件处理结果, flag=true意味着处理成功,否则可以从data中拿到对应的错误
    }

    //监视视频刻录对象
    public interface RecordThreadStatusListener {
        public void onStatus(int id, int i, Object data);//1代表正在刻录 0代表刻录停止 id代表线程编号
    }

    //监听机器是否被root的接口
    public interface RootResultListener {
        public void onRootResult(int execCode, Object data);
    }

    //执行shell命令的结果回调接口
    public interface ShellExeListener {
        public void onExec(List<String> execResult);
    }

    //日期选择控件在选定日期后的自动回调
    public interface TimePickerHandler {
        public void pickTime(String timeValue);
    }


    public interface UsbCableHarwareListener {
        //此函数式子啊广播接受者中发起调用的 切勿进行大的耗时运算
        public void onCableChange(int cableStatus, Object data);
    }

    public interface VideoRecordListener {
        public void onStartRecord(RecordStatus recordStatus);
        public void onStopRecord(RecordStatus recordStatus);
        public void onRecordException(Object exception);
        public void setVideoCapture(VideoCapture videoCapture);
    }

    public interface itemsRefreshListener {
        public void onRefresh(int flag, Object data);//所有列表显示的刷新 在adaptor中调用 在ui中刷新listview或其他的显示列表
    }

    public interface onSpeedChange {
       public void onSpeed();
    }


    public interface timerAction{
        public void onTime(Context c,int flag,Object data);
    }

    public interface cameraStartListener{
        public void onStart(int flag,Object data);
    }

    public interface VideoFrameListener{
        public void onVideoFrame(DisplayModule [] displayModules);
    }

    public interface VideoFrameSplitter {
        public Bitmap[] onSplit(AVSingnelFrame avSingnelFrame);
    }

}
