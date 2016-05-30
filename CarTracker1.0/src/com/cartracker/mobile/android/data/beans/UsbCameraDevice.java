package com.cartracker.mobile.android.data.beans;

/**
 * Created by jw362j on 10/3/2014.
 */
public class UsbCameraDevice {
    private int id;//usb相机对应的编号 其和几条刻录线程一一对应
    private int deviceID;//设备出厂的编号
    private String usbMountPath;//usb影像设备在系统中的挂载路径 如/dev/video4
    private String deviceName;//从设备实现上讲 deviceName就是设备在系统上的挂载点
    private int vendorId ;
    private int productId ;
    private String alias ;//设备别名 用户可以为其起名 车前车左 车右或车尾等

    public UsbCameraDevice(int id, String usbMountPath, int vendorId, int productId,String name,int deviceID) {
        this.id = id;
        this.usbMountPath = name;
        this.vendorId = vendorId;
        this.productId = productId;
        this.alias = id+"";
        this.deviceName = name;
        this.deviceID = deviceID;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", deviceID=" + deviceID +
                ", usbMountPath=" + usbMountPath  +
                ", deviceName=" + deviceName  +
                ", vendorId=" + vendorId +
                ", productId=" + productId +
                ", alias=" + alias ;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public String getUsbMountPath() {
        return usbMountPath;
    }

    public void setUsbMountPath(String usbMountPath) {
        this.usbMountPath = usbMountPath;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
