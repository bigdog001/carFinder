package com.cartracker.mobile.android.util.record;

import com.googlecode.javacv.FFmpegFrameRecorder;

/**
 * Created by jw362j on 10/14/2014.
 */
public class MyFfmpegRecordor extends FFmpegFrameRecorder {
    public MyFfmpegRecordor(String filename, int imageWidth, int imageHeight) {
        super(filename, imageWidth, imageHeight);
        setUpParameters();
    }

    //每当位图被采集到的时候都标记上事件戳 这样就有利于形成连续的画面 各个画面间事件不能太短(35-45毫秒间) 如果太短就会报错
    private void setUpParameters() {
        this.setFormat("mp4");
        // this.setVideoCodec(13);
//        this.setSampleRate(VariableKeeper.sampleAudioRateInHz);
        this.setFrameRate(11.5f);//录像帧率 此值需要不断调节
    }
}
