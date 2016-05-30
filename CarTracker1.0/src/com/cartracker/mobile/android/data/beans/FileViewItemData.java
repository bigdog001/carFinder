package com.cartracker.mobile.android.data.beans;


/**
 * Created by jw362j on 9/30/2014.
 */
public class FileViewItemData {
    private String filePath;
    private String infor2Show;
    private long file_size;

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public FileViewItemData(String filePath, String infor2Show,long size) {
        this.file_size = size;
        this.filePath = filePath;
        this.infor2Show = infor2Show;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getInfor2Show() {
        return infor2Show;
    }

    public void setInfor2Show(String infor2Show) {
        this.infor2Show = infor2Show;
    }
}
