package com.cartracker.mobile.android.camera;

/**
 * Created by jw362j on 9/22/2014.
 */

/**
 * 探测每个摄像头的状态值,当系统目前的工作状摄像头数目低于3（4）的时候会不停得动态扫描是否有新摄像头就绪 如果有就将其加入到任务队列中
 */
public class CameraManager implements Runnable{



    static {
        //加载so库模块

    }

    public CameraManager() {
//         初始化c++层面的本地函数
        initJNI();
        //初始化摄像头 探测每个摄像头的状态值
        initCameras();

        //所有的工作搞定后启动自己开始工作
        Thread cameraTask = new Thread(this);
        cameraTask.start();

    }

    private void initJNI(){}
    private void initCameras(){}

    @Override
    public void run() {

    }
    //和摄像头通讯 将取得的画面送入对应的缓冲区
}
