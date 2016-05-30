package com.usb.camera.android;

/**
 * Created by jw362j on 11/18/2014.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

class SingleCameraPreview extends SurfaceView implements SurfaceHolder.Callback,
        Runnable {

    private static final boolean DEBUG = true;
    private static final String TAG = "WebCam";
    protected Context context;
    private SurfaceHolder holder;
    Thread mainLoop = null;
    private Bitmap bmp = null;

    private boolean cameraExists = false;
    private boolean shouldStop = false;

    private cameraListener listner;

    // /dev/videox (x=cameraId+cameraBase) is used.
    // In some omap devices, system uses /dev/video[0-3],
    // so users must use /dev/video[4-].
    // In such a case, try cameraId=0 and cameraBase=4
    private int cameraId = 0;
    private int cameraBase = 0;

    // This definition also exists in ImageProc.h.
    // Webcam must support the resolution 640x480 with YUYV format.
    static final int IMG_WIDTH = 640;
    static final int IMG_HEIGHT = 480;

    // The following variables are used to draw camera images.
    private int winWidth = 0;
    private int winHeight = 0;
    private Rect rect;
    private int dw, dh;
    private float rate;

    private long start_time;
    private int counter = 0;

    // JNI functions
    public native int prepareCamera(int videoid);

    public native int prepareCameraWithBase(int videoid, int camerabase);

    public native void processCamera();

    public native void stopCamera();

    public native void pixeltobmp(Bitmap bitmap);

    static {
        System.loadLibrary("singlecamera");
    }

    public SingleCameraPreview(Context context) {
        super(context);
        this.context = context;
        if (DEBUG)
            Log.d(TAG, "CameraPreview constructed");
        setFocusable(true);

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    public SingleCameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (DEBUG)
            Log.d(TAG, "CameraPreview constructed");
        setFocusable(true);

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }



    public cameraListener getListner() {
        return listner;
    }

    public void setListner(cameraListener listner) {
        this.listner = listner;
    }

    @Override
    public void run() {
        start_time = System.currentTimeMillis();
        while (true && cameraExists) {
            // obtaining display area to draw a large image
            if (winWidth == 0) {
                winWidth = this.getWidth();
                winHeight = this.getHeight();

                if (winWidth * 3 / 4 <= winHeight) {
                    dw = 0;
                    dh = (winHeight - winWidth * 3 / 4) / 2;
                    rate = ((float) winWidth) / IMG_WIDTH;
                    rect = new Rect(dw, dh, dw + winWidth - 1, dh + winWidth
                            * 3 / 4 - 1);
                } else {
                    dw = (winWidth - winHeight * 4 / 3) / 2;
                    dh = 0;
                    rate = ((float) winHeight) / IMG_HEIGHT;
                    // rect = new Rect(dw,dh,dw+winHeight*4/3
                    // -1,dh+winHeight-1);
                    rect = new Rect(0, 0, winWidth / 2 - 1, winHeight / 2 - 1);
                }
            }

            if (System.currentTimeMillis() - start_time > 1000) {
                Log.d("bigdog", counter + ",in one second....");
                counter = 0;
                start_time = System.currentTimeMillis();
            } else {
                counter++;
            }

            // obtaining a camera image (pixel data are stored in an array in
            // JNI).
            processCamera();
            // camera image to bmp
            pixeltobmp(bmp);

            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                // draw camera bmp on canvas
                canvas.drawBitmap(bmp, null, rect, null);

                getHolder().unlockCanvasAndPost(canvas);
            }

            if (shouldStop) {
                shouldStop = false;
                break;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (DEBUG)
            Log.d(TAG, "surfaceCreated");
        if (bmp == null) {
            bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT,
                    Bitmap.Config.ARGB_8888);
        }
        // /dev/videox (x=cameraId + cameraBase) is used
        int ret = prepareCameraWithBase(cameraId, cameraBase);

        if (ret != -1)
            cameraExists = true;
        if (cameraExists) {
            mainLoop = new Thread(this);
            mainLoop.start();
        } else {
            if(listner  != null){
                listner.onCamera(0);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (DEBUG)
            Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (DEBUG)
            Log.d(TAG, "surfaceDestroyed");
        if (cameraExists) {
            shouldStop = true;
            while (shouldStop) {
                try {
                    Thread.sleep(100); // wait for thread stopping
                } catch (Exception e) {
                }
            }
        }
        stopCamera();
    }

    public interface cameraListener{
        public void onCamera(int i);
    }
}
