package com.cartracker.mobile.android.data.beans;

import com.cartracker.mobile.android.util.SystemUtil;

import java.util.Date;

/**
 * Created by jw362j on 9/27/2014.
 */
public class VideoFileBean {
    private String filePath;
    private long lastModifyDate;
    private long fileSize;

    public VideoFileBean() {}

    public VideoFileBean(String filePath, long lastModifyDate, long fileSize) {
        this.filePath = filePath;
        this.lastModifyDate = lastModifyDate;
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    @Override
    public String toString() {
        return "VideoFileBean{" +
                "filePath='" + filePath + '\'' +
                ", lastModifyDate=" + SystemUtil.convertDateToString(new Date(lastModifyDate)) +
                ", fileSize=" + fileSize/(1024*1024)+"MB" +
                '}';
    }
}
