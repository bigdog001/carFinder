package com.cartracker.mobile.android.util.comm;

import com.cartracker.mobile.android.data.beans.VideoFileBean;

import java.util.Comparator;

/**
 * Created by jw362j on 9/27/2014.
 */
public class VideoFileBeanComparator implements Comparator<VideoFileBean> {
    @Override
    public int compare(VideoFileBean vfb1, VideoFileBean vfb2) {
        return (int)(vfb1.getLastModifyDate()- vfb2.getLastModifyDate());
    }
}
