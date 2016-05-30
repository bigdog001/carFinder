package com.cartracker.mobile.android.data.beans;

/**
 * Created by jw362j on 9/19/2014.
 */
public class RecordStatus {
    private String old_fileName;
    private String old_path_dir;
    private String threadID;
    private String recordDescribe;

    public RecordStatus( ) {

    }

    public RecordStatus(String old_path,String old_fileName) {
        this.old_fileName = old_fileName;
        this.old_path_dir = old_path;
    }

    public String getOld_path_dir() {
        return old_path_dir;
    }

    public void setOld_path_dir(String old_path_dir) {
        this.old_path_dir = old_path_dir;
    }

    public String getOld_fileName() {
        return old_fileName;
    }

    public void setOld_fileName(String old_fileName) {
        this.old_fileName = old_fileName;
    }

	public String getThreadID() {
		return threadID;
	}

	public void setThreadID(String threadID) {
		this.threadID = threadID;
	}

	public String getRecordDescribe() {
		return recordDescribe;
	}

	public void setRecordDescribe(String recordDescribe) {
		this.recordDescribe = recordDescribe;
	}
    
	
    
}
