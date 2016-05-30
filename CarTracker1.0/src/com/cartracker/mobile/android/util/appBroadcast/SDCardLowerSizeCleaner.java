package com.cartracker.mobile.android.util.appBroadcast;

import android.content.Context;
import android.content.Intent;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.beans.VideoFileBean;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.comm.VideoFileBeanComparator;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jw362j on 9/26/2014.
 */
public class SDCardLowerSizeCleaner extends BaseBroadCastRcv implements Runnable, appStop {
    private ArrayList<VideoFileBean> filelists;
    private boolean isCleaningNow = false;//多个线程可能在相隔几个毫秒左右的时间内同时发出清理sd卡的请求 此值只要保证一次清理即可 此标志位表示当前是否正处于清理模式 则后面到来的广播将不被执行

    public SDCardLowerSizeCleaner() {
        VariableKeeper.addStop(this);
        filelists = new ArrayList<VideoFileBean>();
    }

    //负责整理sd卡以腾出空闲空间
    @Override
    public void onSafeReceive(Context context, Intent intent) {
        //sd卡的清理可能比较耗时 另起一个线程来完成
        if(VariableKeeper.isCardExist&&!isCleaningNow){
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        if (VariableKeeper.isCardExist&&!isCleaningNow) {
            isCleaningNow = true;
            //开始删除sd卡上旧的录像文件 且只在VariableKeeper.isVideoOverWrite = true 的时候执行自动删除操作 否则只是做吐司提示

            if (VariableKeeper.mSp.getBoolean(VariableKeeper.APP_CONSTANT.sp_name_video_rewrite,false)) {
                if (VariableKeeper.sdCard_CurrentFreeSize < VariableKeeper.APP_CONSTANT.sdCard_minFreeSize) {
                    deleteOldFile();
                    if (filelists != null) {
                        filelists.clear();//清空所迭代的文件路径序列.
                        filelists = null;
                        filelists = new ArrayList<VideoFileBean>();
                    }
                    //结束清理操作 即改变标志位的值 以准备接收并执行下次清理动作
                    isCleaningNow = false;

                    //清理完毕 发出VariableKeeper.CLEAN_SDSIZE_COMPLETE这样的广播 告知其它组件开始工作
        VariableKeeper.context.sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.CLEAN_SDSIZE_COMPLETE));
                }
            } else {
                //toast提示用户手工整理删除sd卡上的旧内容,不进行自动整理
                SystemUtil.MyToast(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.system_sdcard_current_size_infor_lower) + VariableKeeper.sdCard_CurrentFreeSize + "MB," + VariableKeeper.context.getResources().getString(R.string.system_sdcard_clean_remind_msg));
            }
        }else {
            SystemUtil.MyToast(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.system_sdcard_status_nosdcard));
        }
    }

    private void deleteOldFile() {
        //toast提示用户系统正在自动执行整理删除sd卡上的旧内容的操作
        SystemUtil.MyToast(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.system_sdcard_clean_automatic_clean_msg));
        //现在的策略是在VariableKeeper.VideoFolderName 目录下搜寻时间最为陈旧的文件,将要被删除的文件总数不可能超过1000个
        String cleanFolder = VariableKeeper.system_file_save_BaseDir + VariableKeeper.VideoFolderName + File.separator;
        SystemUtil.log("开始清理存储空间...deleteOldFile from :" + cleanFolder);
        refreshFileList(cleanFolder);//获取当前所有的视频录像文件
        SystemUtil.log("file recorded count 当前目录下被刻录的文件总数为:" + filelists.size());
        for (VideoFileBean vfb : filelists) {
            SystemUtil.log(vfb.toString());
        }
        //将filelists中的对象按照lastModify的值进行排序
        Collections.sort(filelists, new VideoFileBeanComparator());
        SystemUtil.log("after sorted:" + filelists.get(0));
        //开始真正的文件删除 从filelists的第一个位置开始依次往后不断删除 每删除一个重新测算一次sd的大小 然后决定是否停止删除操作 每次删除操作将为系统预留出VariableKeeper.sdCard_cleanSize的工作空间
        for (VideoFileBean vf : filelists) {
            File f = new File(vf.getFilePath());
            f.delete();
            //将删除的文件信息存储在数据库以便与做日志显示和统计,持久化的操作作为该类的功能设计之一 不再另开发目标对象
            //TODO
            if (SystemUtil.getSDFreeSize() > VariableKeeper.APP_CONSTANT.sdCard_cleanSize) {
                //此时已经达到预期的腾出一些sd卡空间的目的
                break;
            }
        }
    }

    private void refreshFileList(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                refreshFileList(files[i].getAbsolutePath());
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
//                SystemUtil.log("循环迭代到文件:"+strFileName);
                if (filelists != null)
                    filelists.add(new VideoFileBean(files[i].getAbsolutePath(), files[i].lastModified(), files[i].length()));
            }
        }
    }


    @Override
    public void stopAll() {
        if (filelists != null) {
            filelists.clear();//清空所迭代的文件路径序列.
            filelists = null;
        }
    }
}
