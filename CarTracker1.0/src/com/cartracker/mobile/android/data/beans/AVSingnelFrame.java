package com.cartracker.mobile.android.data.beans;

import android.graphics.Bitmap;

/**
 * Created by jw362j on 11/14/2014.
 */
public class AVSingnelFrame {
    private long frameTimeStamp;
    private boolean[] channel_flag;//此值一共5种情况, 0 代表只有单屏第一路信号显示, 1代表只有单屏第二路信号显示, 2代表只有单屏第三路信号显示, 3代表只有单屏第四路信号显示  4代表4屏共四路信号拼接显示 (显示的模式组合非常多，现在只支持任意一路单画面和全画面这2中模式)
    private Bitmap videoData;

    public AVSingnelFrame(long frameTimeStamp,  boolean[] channel_flag, Bitmap videoData) {
        this.frameTimeStamp = frameTimeStamp;
        this.channel_flag = channel_flag;
        this.videoData = videoData;
    }

    public  boolean[] getChannel_flag() {
        return channel_flag;
    }

    public void setChannel_flag( boolean[] channel_flag) {
        this.channel_flag = channel_flag;
    }

    public long getFrameTimeStamp() {
        return frameTimeStamp;
    }

    public void setFrameTimeStamp(long frameTimeStamp) {
        this.frameTimeStamp = frameTimeStamp;
    }

    public Bitmap getVideoData() {
        return videoData;
    }

    public void setVideoData(Bitmap videoData) {
        this.videoData = videoData;
    }
}
