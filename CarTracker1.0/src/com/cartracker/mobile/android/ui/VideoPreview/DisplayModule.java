package com.cartracker.mobile.android.ui.VideoPreview;

import android.graphics.Rect;
import com.cartracker.mobile.android.data.beans.VideoFrame;

/**
 * Created by jw362j on 9/21/2014.
 */
public class DisplayModule {
    private VideoFrame videoFrame;//一帧画面
    private Rect rect;//该画面的显示位置

    public DisplayModule(VideoFrame videoFrame, Rect rect) {
        this.videoFrame = videoFrame;
        this.rect = rect;
    }

    public VideoFrame getVideoFrame() {
        return videoFrame;
    }

    public void setVideoFrame(VideoFrame videoFrame) {
        this.videoFrame = videoFrame;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }
}
