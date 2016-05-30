package com.cartracker.mobile.android.util.handler.impl;

import android.content.Context;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.handler.InterfaceGen.Initializer;

import java.io.File;

/**
 * Created by jw362j on 9/21/2014.
 */
public class FolderStructureHandler implements Initializer {

    @Override
    public void init(Context context) {
        //=====init the folder========
        File video_folder = new File(VariableKeeper.system_file_save_BaseDir+VariableKeeper.VideoFolderName);
        if (!video_folder.exists())video_folder.mkdirs();
        video_folder = null;


        File log_folder = new File(VariableKeeper.system_file_save_BaseDir+VariableKeeper.LogFolderName);
        if (!log_folder.exists())log_folder.mkdirs();
        log_folder = null;


        File cach_folder = new File(VariableKeeper.system_file_save_BaseDir+VariableKeeper.cachFolderName);
        if (!cach_folder.exists())cach_folder.mkdirs();
        cach_folder = null;


    }
}
