package com.usb.camera.android;

/**
 * Created by jw362j on 11/18/2014.
 */
public abstract class UsbCameraBase extends CameraBase{
    public abstract int prepareCameraWithBase(int videoid, int videobase);
    public abstract void processCamera();
    public abstract void processRBCamera(int lrmode);

}
