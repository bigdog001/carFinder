package com.cartracker.mobile.android.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.VideoFrame;

public class CameraConnect extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private static final boolean DEBUG=true;
	private static final String TAG="DoubleWebCam";
	protected Context context;
	private SurfaceHolder holder;
    Thread mainLoop = null;
    private VideoFrame [] videoFrames = new VideoFrame[VariableKeeper.APP_CONSTANT.size_num_cam];
//	private boolean[] cameraExists=new boolean[2];
	
	private boolean shouldStop=false;
	
	// Size of each image. This definition also exists in jni/cartracker.h
	public static final int IMG_WIDTH= VariableKeeper.APP_CONSTANT.IMG_WIDTH;
	public static final int IMG_HEIGHT=VariableKeeper.APP_CONSTANT.IMG_HEIGHT;
    
	public int winWidth=0;
	public int winHeight=0;
	public Rect rect1, rect2;
	
    public native void pixeltobmp(Bitmap bitmap1, Bitmap bitmap2);
    public native void pixeltobmpByName(String [] mountPath,Bitmap[]bitmaps);
    public native void pixeltobmpByID(int videoID,Bitmap bitmap);
    // prepareCamera selects the device automatically. please set videoid=0
    public native int prepareCamera(int videoid);
    // prepareCameraWithBase is used if you want to specify the device manually.
    // e.g., for /dev/video[1,2], use prepareCameraWithBase(0,1).
    // please set videoid=0
    public native int prepareCameraWithBase(int videoid, int videobase);
    public native void processCamera();
    public native void processRBCamera(int lrmode);
    public native void stopCamera();

    static {
    	System.loadLibrary("jpeg");
        System.loadLibrary("cartracker");
    }
	public CameraConnect(Context context) {
		super(context);
        init(context);
	}

	public CameraConnect(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context);
	}

    private  void init(Context context){
        this.context = context;
        if(DEBUG) Log.d(TAG,"CameraPreview constructed");
        setFocusable(true);

        for (int x= 0 ;x<VariableKeeper.APP_CONSTANT.size_num_cam;x++) {
            videoFrames[x] = new VideoFrame();
            if(prepareCamera(x) == 0){
                videoFrames[x].setCameraExsit(true);
                videoFrames[x].setCameraID(x);
            }else {
                videoFrames[x].setCameraID(x);
            }

            if(videoFrames[x].getBitmap()==null){
                videoFrames[x].setBitmap( Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888) )  ;
            }

        }

        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(DEBUG) Log.d(TAG, "surfaceCreated");

        // /dev/video[0,1] are used.
        // In some omap devices, /dev/video[0..3] are used by system,
        // and, in such a case, /dev/video[4,5] are selected automatically.
        mainLoop = new Thread(this);
        mainLoop.start();
    }
	
    @Override
    public void run() {
        while (true && (videoFrames[0].isCameraExsit()||videoFrames[1].isCameraExsit()||videoFrames[2].isCameraExsit()||videoFrames[3].isCameraExsit())) {
        	processCamera();
            for (int x= 0 ;x<videoFrames.length;x++) {
                pixeltobmpByID(x,videoFrames[x].getBitmap());
            }

            //线程启动并且采集完数据就将数据丢入对应的缓冲区VariableKeeper.bitMapHolders[i]中

        	Canvas canvas = getHolder().lockCanvas();
            if (canvas != null)
            {
            	if(winWidth==0){
            		winWidth=this.getWidth();
            		winHeight=this.getHeight();
            		rect1 = new Rect(0, 0, winWidth/2-1, winWidth*3/4/2-1);
            		rect2 = new Rect(winWidth/2,0,winWidth-1, winWidth*3/4/2-1);
            	}

        		canvas.drawBitmap(videoFrames[0].getBitmap(),null,rect1,null);
        		canvas.drawBitmap(videoFrames[1].getBitmap(),null,rect2,null);

            	getHolder().unlockCanvasAndPost(canvas);
            }
            if(shouldStop){
            	shouldStop = false;  
            	break;
            }

        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(DEBUG) Log.d(TAG, "surfaceChanged");
    }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(DEBUG) Log.d(TAG, "surfaceDestroyed");
		if((videoFrames[0].isCameraExsit()||videoFrames[1].isCameraExsit()||videoFrames[2].isCameraExsit()||videoFrames[3].isCameraExsit())){
			shouldStop = true;
			for(int i=0 ; i<10 ; i++){
				try{ 
					Thread.sleep(100); // wait for thread stopping
				}catch(Exception e){}
				if(!shouldStop){
					break;
				}
			}
			stopCamera();
		}
	}
}
