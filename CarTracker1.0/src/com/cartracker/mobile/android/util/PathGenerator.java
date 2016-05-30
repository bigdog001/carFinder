package com.cartracker.mobile.android.util;

import com.cartracker.mobile.android.config.VariableKeeper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jw362j on 9/19/2014.
 */
public class PathGenerator {

    public static String  GeneratorCurrentPath(){
        String current_folder_path = "";
        current_folder_path = VariableKeeper.system_file_save_BaseDir+VariableKeeper.VideoFolderName + File.separator;
        Calendar calendar = Calendar.getInstance();
        int year =  calendar.get(Calendar.YEAR);
        int month =  calendar.get(Calendar.MONTH)+1;
        int day_of_year =  calendar.get(Calendar.DAY_OF_YEAR);
        int day_of_month =  calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        current_folder_path = current_folder_path+year+File.separator+month+File.separator+day_of_month+File.separator+hour+File.separator;
        VariableKeeper.current_video_folder_path = current_folder_path;
        return current_folder_path;
    }

    public static String  GeneratorPathFromPicker(String pickerDate){
        String current_folder_path = "";
        current_folder_path = VariableKeeper.system_file_save_BaseDir+VariableKeeper.VideoFolderName + File.separator;

        Date pikerTime = SystemUtil.convertStrToDate(pickerDate);
        SystemUtil.log("time now is :"+pikerTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pikerTime);
        int year =  calendar.get(Calendar.YEAR);
        int month =  calendar.get(Calendar.MONTH)+1;
        int day_of_year =  calendar.get(Calendar.DAY_OF_YEAR);
        int day_of_month =  calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);


        current_folder_path = current_folder_path+year+File.separator+month+File.separator+day_of_month+File.separator+hour+File.separator;
        VariableKeeper.current_video_folder_path = current_folder_path;
        return current_folder_path;
    }

    public static String GeneratorCurrentName(){
        String time_name = "";
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date d1=new Date(time);
        time_name=format.format(d1);
        return time_name;
    }



    public static String GeneratorCurrentName_CN(){
        String time_name = "";
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Date d1=new Date(time);
        time_name=format.format(d1);
        return time_name;
    }
}
