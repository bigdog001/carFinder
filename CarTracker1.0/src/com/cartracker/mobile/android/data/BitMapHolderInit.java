package com.cartracker.mobile.android.data;

import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.VideoFrame;
import com.cartracker.mobile.android.util.BitMapHolder;
import com.cartracker.mobile.android.util.SystemUtil;

/**
 * Created by jw362j on 9/21/2014.
 */
public class BitMapHolderInit implements Runnable {
    private BitMapHolder bitMapHolder;
    private int thread_flag = 1;
    private int index = 1;
    private int thread_id;
    private long total_video_frame;

    public BitMapHolderInit(BitMapHolder bitMapHolder,int tid) {
        this.bitMapHolder = bitMapHolder;
        this.thread_id = tid;
        VariableKeeper.bitMapHolderInits[thread_id] = this;
    }

    public int getThread_flag() {
        return thread_flag;
    }

    public void setThread_flag(int thread_flag) {
        this.thread_flag = thread_flag;
    }

    public long getTotal_video_frame() {
        return total_video_frame;
    }

    @Override
    public void run() {
        while (thread_flag == 1) {
            synchronized (bitMapHolder.getLocker()) {
                if (bitMapHolder.getSize() < VariableKeeper.APP_CONSTANT.BitMapHolderMax) {

                        total_video_frame++;
                        bitMapHolder.add(new VideoFrame(System.currentTimeMillis(), SystemUtil.getImageFromAssetsFile(thread_id+"_"+index + ".jpg")));
//                        Thread.sleep(VariableKeeper.video_break_time);

                } else {
                    try {
                        bitMapHolder.getLocker().wait();
                        continue;
                    } catch (InterruptedException e) {
                        SystemUtil.log(e.getMessage());
                        e.printStackTrace();
                    }
                }

                if (index + 1 > 21) {
                    index = 1;
                } else {
                    index++;
                }
            }
        }

    }
}
