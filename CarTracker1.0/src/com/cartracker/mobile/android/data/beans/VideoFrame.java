package com.cartracker.mobile.android.data.beans;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.googlecode.javacv.cpp.opencv_core;

/**
 * Created by jw362j on 9/19/2014.
 */
public class VideoFrame  implements Parcelable {
    private long timestampt;
    private Bitmap bitmap;
    private int cameraID;//对应的镜头id 如果镜头不存在就为0
    private boolean isCameraExsit;//镜头是否存在
    private byte[] imageData;
    private String fregment_cach_path;
    private opencv_core.IplImage iplImage4record; //镜头采集到画面在放入缓冲区前会把bitmap信息转换为IplImage对象置入此字段 VariableKeeper.bitMapHolders[x].add(new VideoFrame(videoFrames[x].getTimestampt(), videoFrames[x].getFregment_cach_path(),SystemUtil.getIp1ImageFromBitmap(cameraUtil.bmp[x])));//数据入队列暂存,那么缓冲队列中的videoFrames只有时间戳和数据的路径,所有此时缓冲区是一个轻量级的东西了


     
    public VideoFrame(long timestampt, Bitmap bitmap) {
        this.timestampt = timestampt;
        this.bitmap = bitmap;
    }
    public VideoFrame(long timestampt,  byte[] data) {
        this.timestampt = timestampt;
        this.imageData = data;
    }

    public opencv_core.IplImage getIplImage4record() {
        return iplImage4record;
    }

    public void setIplImage4record(opencv_core.IplImage iplImage4record) {
        this.iplImage4record = iplImage4record;
    }

    public VideoFrame(long ts,String image_path,opencv_core.IplImage iplImage){
        this.timestampt = ts;
        this.fregment_cach_path = image_path;
        this.iplImage4record = iplImage;
    }

    public VideoFrame(){

    }

    public String getFregment_cach_path() {
        return fregment_cach_path;
    }

    public void setFregment_cach_path(String fregment_cach_path) {
        this.fregment_cach_path = fregment_cach_path;
    }



    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public long getTimestampt() {
        return timestampt;
    }

    public void setTimestampt(long timestampt) {
        this.timestampt = timestampt;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {



    }

    public int getCameraID() {
        return cameraID;
    }

    public void setCameraID(int cameraID) {
        this.cameraID = cameraID;
    }

    public boolean isCameraExsit() {
        return isCameraExsit;
    }

    public void setCameraExsit(boolean isCameraExsit) {
        this.isCameraExsit = isCameraExsit;
    }
}
