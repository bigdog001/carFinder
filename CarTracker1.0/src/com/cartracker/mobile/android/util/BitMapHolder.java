package com.cartracker.mobile.android.util;

import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.VideoFrame;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jw362j on 9/19/2014.
 */
public class BitMapHolder {

    private Object locker;
    //绘图线程(CameraPreview预览)根据这个值决定是停止刷新只显示默认画面还是动态的取出应该显示的正确帧
    private long size = 0;

    public BitMapHolder() {
        this.locker = new Object();
    }

    // VideoFrames
    private BlockingQueue<VideoFrame> holders = new LinkedBlockingQueue<VideoFrame>();

    public void add(VideoFrame data) {
        // add one frame into the holder
        if (size > VariableKeeper.APP_CONSTANT.BitMapHolderMax) {
            SystemUtil.log("the VideoFrame in the queue is too much ,have a nap now..... ");
            System.gc();
        } else {
            try {
                holders.put(data);
            } catch (InterruptedException e) {
                SystemUtil.log("error happens when adding the data to the dataholder" + e.getMessage());
            }

            size++;
        }
    }

    public VideoFrame get() {
        VideoFrame s = null;
        try {
            s = holders.take();
        } catch (InterruptedException e) {
            SystemUtil.log(e.getMessage());
            e.printStackTrace();
        }
        size--;
        return s;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Object getLocker() {
        return locker;
    }

    public void setLocker(Object locker) {
        this.locker = locker;
    }
}
