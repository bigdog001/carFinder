package com.cartracker.mobile.android.ui.VideoPreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.VideoFrame;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.InterfaceGen.CameraPreviewListener;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, CameraPreviewListener, android.view.View.OnTouchListener, InterfaceGen.VideoFrameListener {

    // Size of each image. This definition also exists in jni/ImageProc.h
    private static final boolean DEBUG = true;
    private static int camera_num = VariableKeeper.APP_CONSTANT.size_num_cam;
    public int winWidth = 0;
    public int winHeight = 0;
    protected Context context;
    private SurfaceHolder holder;
    private DisplayModule displayModules[];
    private Bitmap [] bitmaps_previous;//已经显示过了的上一帧画面
    private Rect[] rects;//多屏显示的矩阵参数
    private VideoFrame[] videoFrames;
//    private BitMapHolderInit[] bitMapHolderInits;
    private Rect rect_singleDisplay;//单屏显示的矩阵参数
    private boolean is_previewing = true;
    private int currentChannel = 0;//当前的显示的模式 单屏还是四屏
    private Object threadLocker = new Object();
    private Bitmap bitMap_test_screen_no_signal;//电视信号测试图

    public CameraPreview(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public int getCurrentChannel() {
        return currentChannel;
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setFocusable(true);
        VariableKeeper.isCurrent_preview = 1;//置为预览模式此时刻录线程不负责销毁当前的位图信号，由预览线程在绘制屏幕后自行销毁
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
//        bitMapHolderInits = VariableKeeper.bitMapHolderInits;
        bitmaps_previous = new Bitmap[VariableKeeper.APP_CONSTANT.size_num_cam];
    }


    private void initPreview() {
        rects = new Rect[4];
        videoFrames = new VideoFrame[VariableKeeper.APP_CONSTANT.size_num_cam];
        //把屏幕分成四份，用于显示四路视频信号
        winWidth = this.getWidth();
        winHeight = this.getHeight();
        SystemUtil.log("winWidth is:" + winWidth + ",winHeight is:" + winHeight);
        rect_singleDisplay = new Rect(0, 0, winWidth, winHeight);
        rects[0] = new Rect(0, 0, winWidth / 2 - 1, winHeight / 2 - 1);
        rects[1] = new Rect(winWidth / 2, 0, winWidth - 1, winHeight / 2 - 1);
        rects[2] = new Rect(0, winHeight / 2, winWidth / 2 - 1, winHeight);
        rects[3] = new Rect(winWidth / 2, winHeight / 2, winWidth, winHeight);
        //默认是四路信号多屏显示
        switchChannelTo4();

    }


    /**
     * 画面采集模块会把4帧数据全部送过来 由预览界面来选择决定显示哪些画面
     *
     * @param ds
     */
    @Override
    public void onVideoFrame(DisplayModule[] ds) {
        if (ds != null) {
            for (int i = 0; i < ds.length; i++) {
                //只初始化画面 显示的位置数据沿用 displayModules中本身具有的
                if (ds[i] != null && ds[i].getVideoFrame() != null)
                    displayModules[i].setVideoFrame(ds[i].getVideoFrame());

            }
            //数据填充完毕开始刷新画面
            showVideoFrame();
            //将本次显示过的画面对象保存在bitmaps_previous中
            if (ds != null) {
                for (int i = 0; i < ds.length; i++) {
                    if (ds[i] != null && ds[i].getVideoFrame() != null)
                        bitmaps_previous[i] = ds[i].getVideoFrame().getBitmap();
                }
            }
        }
    }

    private void showVideoFrame() {
        synchronized (threadLocker) {
            //不停准备应该用的数据
            Canvas canvas = getHolder().lockCanvas();
            if (canvas != null) {
                if (winWidth == 0) {
                    initPreview();
                }
                if (currentChannel == 4) {
                    for (int i = 0; i < displayModules.length; i++) {
                        if (displayModules[i] != null) {
                            //判断当前是否有信号进入对应的缓冲区 如果无信号则显示 电视信号测试图
                            if (displayModules[i].getVideoFrame() == null || displayModules[i].getVideoFrame().getBitmap() == null) {
                                //无信号输入显示电视信号测试图
                                if (bitMap_test_screen_no_signal == null) {
                                    bitMap_test_screen_no_signal = SystemUtil.getImageFromAssetsFile("test_screen_no_signal.jpg");
                                }
                                videoFrames[i].setBitmap(bitMap_test_screen_no_signal);
                                displayModules[i].setVideoFrame(videoFrames[i]);
                                canvas.drawBitmap(displayModules[i].getVideoFrame().getBitmap(), null, displayModules[i].getRect(), null);
                            } else {
                                //有信号输入,正常刷新界面
                                canvas.drawBitmap(displayModules[i].getVideoFrame().getBitmap(), null, displayModules[i].getRect(), null);
                            }
                        } else {
                            //这种情况应该不会发生
                            SystemUtil.log("显示模型容器" + i + "中的数据为空.");
                        }
                    }
                } else {
                    //displayModules[0]一定不会为空 因为程序一起来就自动初始化displayModules数组了,此时只是选取displayModules[0]第一个作为显示模型
                    if (displayModules[0] != null) {
                        if (displayModules[currentChannel].getVideoFrame() == null || displayModules[currentChannel].getVideoFrame().getBitmap() == null) {
                            //无信号输入显示电视信号测试图
                            if (bitMap_test_screen_no_signal == null) {
                                bitMap_test_screen_no_signal = SystemUtil.getImageFromAssetsFile("test_screen_no_signal.jpg");
                            }
                            videoFrames[0].setBitmap(bitMap_test_screen_no_signal);
                            displayModules[0].setVideoFrame(videoFrames[0]);
                            canvas.drawBitmap(displayModules[0].getVideoFrame().getBitmap(), null, displayModules[0].getRect(), null);
                        } else {
                            //有信号输入,正常刷新界面
                            videoFrames[0].setBitmap(displayModules[currentChannel].getVideoFrame().getBitmap());
                            displayModules[0].setVideoFrame(videoFrames[0]);
                            canvas.drawBitmap(displayModules[0].getVideoFrame().getBitmap(), null, displayModules[0].getRect(), null);

                        }
                    } else {
                        //单通道displayModules[0]为空的情况应该不存在
                    }
                }
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
        //显示完毕后释放上一次显示过后的数据,因为在显示模式下 此类事唯一需要使用bitmap对象的地方  如果处于非显示模式下 那么由 UsbCameraData对象来释放对象
        for (Bitmap btm:bitmaps_previous) {
            if (btm != null&& !btm.isRecycled()) {
                btm.recycle();
                btm = null;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.setOnTouchListener(CameraPreview.this);
//        this.threads = new Thread[camera_num];
        //将自己注册到usbCameraData中以自动获取画面数据
        VariableKeeper.videoFrameListener = CameraPreview.this;
        //默认显示起始帧的操作
        showVideoFrame();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        SystemUtil.log("get out of preview......surfaceDestroyed");
        SystemUtil.log("preview screem is surfaceDestroyed," + is_previewing);
        VariableKeeper.videoFrameListener = null;
        is_previewing = false;
        VariableKeeper.isCurrent_preview = 0;
        if (bitMap_test_screen_no_signal != null) {
            //清理掉电视信号测试图
            if (!bitMap_test_screen_no_signal.isRecycled()) bitMap_test_screen_no_signal.recycle();
            bitMap_test_screen_no_signal = null;
        }
        holder.removeCallback(this);
    }


    public void onSurfaceViewRestart() {
        //预览界面突然回到手机桌面然后从手机桌面重新切换到预览界面时候防止黑屏(因为此时刷屏的线程已经消亡并中断屏幕刷新,surfaceview依然是正常工作的)，此时主要是重新启动刷新屏幕的线程
        is_previewing = true;
        VariableKeeper.isCurrent_preview = 1;
        VariableKeeper.videoFrameListener = CameraPreview.this;
        //默认显示起始帧的操作
        showVideoFrame();
    }

    @Override
    public void listen(int channelID) {
        //i=0 代表单屏第一路信号显示, 1代表单屏第二路信号显示, 2代表单屏第三路信号显示, 3代表单屏第四路信号显示  4代表多屏四路信号显示
        switch (channelID) {
            case 4:
                switchChannelTo4();
                break;
            case 0:
                switchChannelTo0();
                break;
            case 1:
                switchChannelTo1();
                break;
            case 2:
                switchChannelTo2();
                break;
            case 3:
                switchChannelTo3();
                break;
            default:
                break;
        }
    }




    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();// 得到坐标
        int y = (int) motionEvent.getY();
        SystemUtil.log("get out of preview onTouch ,x=" + x + ",y=" + y);
        if (currentChannel == 4) {
            for (int n = 0; n < rects.length; n++) {
                if (rects[n].contains(x, y)) {
                    synchronized (threadLocker) {
                        if (n == 0) {
                            switchChannelTo0();
                        } else if (n == 1) {
                            switchChannelTo1();
                        } else if (n == 2) {
                            switchChannelTo2();
                        } else if (n == 3) {
                            switchChannelTo3();
                        }
                        SystemUtil.log("some screem is showing..第" + n + "屏信号被选中.");
                        return false;
                    }
                }
            }
        } else {
            SystemUtil.log("从第" + currentChannel + "屏信号回到多屏模式.");
            switchChannelTo4();
        }


        return false;
    }


    private void switchChannelTo0() {
        this.currentChannel = 0;
        this.displayModules[0] = new DisplayModule(null, rect_singleDisplay);
        this.videoFrames[0] = new VideoFrame();
        this.displayModules[0].setVideoFrame(this.videoFrames[0]);
    }

    private void switchChannelTo1() {
        this.currentChannel = 1;
        this.displayModules[0] = new DisplayModule(null, rect_singleDisplay);
        this.videoFrames[0] = new VideoFrame();
        this.displayModules[0].setVideoFrame(this.videoFrames[0]);

    }

    private void switchChannelTo2() {
        this.currentChannel = 2;
        this.displayModules[0] = new DisplayModule(null, rect_singleDisplay);
        this.videoFrames[0] = new VideoFrame();
        this.displayModules[0].setVideoFrame(this.videoFrames[0]);
    }

    private void switchChannelTo3() {
        this.currentChannel = 3;
        this.displayModules[0] = new DisplayModule(null, rect_singleDisplay);
        this.videoFrames[0] = new VideoFrame();
        this.displayModules[0].setVideoFrame(this.videoFrames[0]);

    }

    private void switchChannelTo4() {
        this.currentChannel = 4;
        this.displayModules = new DisplayModule[VariableKeeper.APP_CONSTANT.size_num_cam];
        for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
            displayModules[i] = new DisplayModule(null, rects[i]);
            videoFrames[i] = new VideoFrame();
        }
    }

}
