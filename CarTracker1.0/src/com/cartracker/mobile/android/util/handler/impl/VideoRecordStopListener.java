package com.cartracker.mobile.android.util.handler.impl;

import com.cartracker.mobile.android.data.beans.RecordStatus;
import com.cartracker.mobile.android.util.PathGenerator;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.VideoCapture;
import com.cartracker.mobile.android.util.handler.InterfaceGen.VideoRecordListener;

/**
 * Created by jw362j on 9/19/2014.
 */
public class VideoRecordStopListener implements VideoRecordListener {
    private VideoCapture videoCapture;

    public VideoRecordStopListener(VideoCapture videoCapture) {
        this.videoCapture = videoCapture;
    }

    public VideoCapture getVideoCapture() {
        return videoCapture;
    }

    public void setVideoCapture(VideoCapture videoCapture) {
        this.videoCapture = videoCapture;
    }

    @Override
    public void onStartRecord(RecordStatus recordStatus) {
    	SystemUtil.log("starting to take the video now ......video file name is:"+recordStatus.getOld_fileName());
    }

    @Override
    public void onStopRecord(RecordStatus recordStatus) {
        String newFileName = recordStatus.getOld_path_dir()+recordStatus.getOld_fileName()+"=="+ PathGenerator.GeneratorCurrentName()+".mp4";
        SystemUtil.reNamaeFile(recordStatus.getOld_path_dir()+recordStatus.getOld_fileName(),newFileName);
        SystemUtil.log("take the video complete now ,the new video file name is :"+newFileName);
    }

    @Override
    public void onRecordException(Object exception) {
        //刻录线程发生异常的时候被调用 可以在此决定是否自我繁衍

    }

}
