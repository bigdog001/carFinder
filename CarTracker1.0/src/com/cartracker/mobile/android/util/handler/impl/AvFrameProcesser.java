package com.cartracker.mobile.android.util.handler.impl;

import android.graphics.Bitmap;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.AVSingnelFrame;
import com.cartracker.mobile.android.util.handler.InterfaceGen;

/**
 * Created by jw362j on 11/14/2014.
 */
public class AvFrameProcesser implements InterfaceGen.VideoFrameSplitter {
    private Bitmap [] bitmaps;

    public AvFrameProcesser() {
        bitmaps = new Bitmap[VariableKeeper.APP_CONSTANT.size_num_cam];
    }

    /**
     *
     * @param avSingnelFrame
     */
    @Override
    public Bitmap[] onSplit(AVSingnelFrame avSingnelFrame) {
        //每次调用的时候都以第一幅bitmaps[0]为切割的标准 当切割函数运行完的时候来自第一幅的图片就分散到4个元素中了，第一幅中的图片内容也发生了改变
        Bitmap bmp = avSingnelFrame.getVideoData();
        if (bmp == null) {
            return null;
        }

        //根据avSingnelFrame.getChannel_flag()的值实现画面分割
        boolean[] camera_flag = avSingnelFrame.getChannel_flag();

        bitmaps[0] = Bitmap.createBitmap(bmp,0,0,bmp.getWidth()/2,bmp.getHeight()/2) ;
        bitmaps[1] = Bitmap.createBitmap(bmp,bmp.getWidth()/2,0,bmp.getWidth()/2,bmp.getHeight()/2) ;
        bitmaps[2] = Bitmap.createBitmap(bmp,0,bmp.getHeight()/2,bmp.getWidth()/2,bmp.getHeight()/2) ;
        bitmaps[3] = Bitmap.createBitmap(bmp,bmp.getWidth()/2,bmp.getHeight()/2,bmp.getWidth()/2,bmp.getHeight()/2) ;

        return bitmaps;
    }
}
