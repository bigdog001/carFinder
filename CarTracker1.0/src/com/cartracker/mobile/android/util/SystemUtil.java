package com.cartracker.mobile.android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.data.CarTrackerApplication;
import com.cartracker.mobile.android.data.beans.UsbCameraDevice;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.util.handler.InterfaceGen;
import com.cartracker.mobile.android.util.handler.InterfaceGen.PermissionFileHandlerListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RecordThreadStatusListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.RootResultListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.ShellExeListener;
import com.googlecode.javacv.cpp.opencv_core;

import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Created by jw362j on 9/19/2014.
 */
public class SystemUtil {

    public static void log(String msg) {
        if (VariableKeeper.logMode) {
            Log.d("bigdog", msg);
        }
    }

    public static void MyToast(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CarTrackerApplication.getTrackerAppContext().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VariableKeeper.getmCurrentActivity(), msg, 0).show();
                    }
                });
            }
        }).start();
    }

    public static void recycleBitmap(Bitmap img) {
        if (img != null && !img.isRecycled()) {
            img.recycle();
        }
    }

    /**
     * 调用系统浏览器
     *
     * @param url
     */
    public static void runBrowser(String url) {
        if (VariableKeeper.getmCurrentActivity() != null) {
//            Uri uri = Uri.parse(url);
            Uri uri = Uri.parse("http://shouji.baidu.com/s?wd=android%20root%B9%A4%BE%DF%26data_type=app");
            Intent intent = new Intent();
            intent.setAction(intent.ACTION_VIEW);
            intent.setData(uri);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (VariableKeeper.getmCurrentActivity() != null)
                VariableKeeper.getmCurrentActivity().startActivity(intent);
        }
    }

    //在当前activity上显示对话框 仅仅只做提示用户用 不能处理任何逻辑
    /*public static void dialogJust4TipsShow(final String title, final String msg) {
        if (VariableKeeper.getmCurrentActivity() != null) {
            VariableKeeper.getmCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                    builder.setTitle(title).setMessage(msg).setPositiveButton(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }*/

    //在当前activity上显示对话框 仅仅只做提示用户用 不能处理任何逻辑
    private static Dialog dialog = null;

    public static void dialogJust4TipsShow(final String title, final String msg) {
        if (VariableKeeper.getmCurrentActivity() != null) {
            VariableKeeper.getmCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View view_title = View.inflate(VariableKeeper.getmCurrentActivity(), R.layout.emergency_help_menu_setting_title, null);
                    View view_body = View.inflate(VariableKeeper.getmCurrentActivity(), R.layout.dialog_body_tips, null);
                    TextView id_tv_tips_msg = (TextView) view_body.findViewById(R.id.id_tv_tips_msg);
                    Button id_btn_tips_body = (Button) view_body.findViewById(R.id.id_btn_tips_body);
                    TextView id_tv_tips_title = (TextView) view_title.findViewById(R.id.id_tv_tips_title);
                    id_tv_tips_title.setText(title);
                    id_tv_tips_msg.setText(msg);
                    TextView menu_emergency_help_settings_send_frequence_title_close = (TextView) view_title.findViewById(R.id.menu_emergency_help_settings_send_frequence_title_close);
                    menu_emergency_help_settings_send_frequence_title_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    id_btn_tips_body.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(VariableKeeper.getmCurrentActivity());
                    builder.setCustomTitle(view_title).setView(view_body);
                    dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }

    public static void LowerVersionWarning() {
        SystemUtil.dialogJust4TipsShow(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.dialog_tips_title),
                VariableKeeper.getmCurrentActivity().getResources().getString(R.string.desktop_sdk_too_low_msg_1) +
                        VariableKeeper.Version_RELEASE +
                        VariableKeeper.getmCurrentActivity().getResources().getString(R.string.desktop_sdk_too_low_msg_2)
        );
    }

    public static void getRootStatus(RootResultListener rootResultListener) {
        Process process = null;
        OutputStream out = null;
        int value = -1;
        try {
            process = Runtime.getRuntime().exec("su");
            out = process.getOutputStream();
            out.write("exit\n".getBytes());         //Linux命令，注意加上换行符
            out.flush();
            process.waitFor();
            value = process.exitValue();          //value = 0则有root权限；不等于0，则未取得root权限
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rootResultListener != null) rootResultListener.onRootResult(value, null);
            if (value == 0) {
                VariableKeeper.isRooted = true;
            } else {
                VariableKeeper.isRooted = false;
            }
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void logOnFile(String string) {

        if (!VariableKeeper.logMode) {
            return;
        }


        try {
            String sdFilePath = VariableKeeper.system_file_save_BaseDir + File.separator + VariableKeeper.LogFolderName + File.separator;
            String fileName = sdFilePath + "log" + ".txt";
            File filePath = new File(sdFilePath);
            if (!filePath.exists()) {
                filePath.mkdir();
            }
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (file.length() > 100 * 1000) {
                try {
                    file.delete();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String a = "\r\n" + string;
            FileOutputStream fos;
            fos = new FileOutputStream(file, true);
            fos.write(a.getBytes());
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static boolean isAvaiableSpace(int sizeMb) {

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            String sdcard = Environment.getExternalStorageDirectory().getPath();
            StatFs statFs = new StatFs(sdcard);
            long blockSize = statFs.getBlockSize();
            long blocks = statFs.getAvailableBlocks();
            long availableSpare = (blocks * blockSize) / (1024 * 1024);
//			Log.v("wt","availableSpare = " + availableSpare);
            if (sizeMb > availableSpare) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static String convertDateToString(Date dateTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(dateTime);
    }

    public static Date convertStrToDate(String strDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = format.parse(strDate, pos);
        return strtodate;
    }

    public static Date LongToDate(long date) {
        Date result = null;
        new Date(date);
        return result;
    }




    public static IplImage processImage(byte[] data) {
         int width = VariableKeeper.APP_CONSTANT.IMG_WIDTH;
         int height=VariableKeeper.APP_CONSTANT.IMG_HEIGHT;

        int f = 4;// SUBSAMPLING_FACTOR;

        // First, downsample our image and convert it into a grayscale IplImage
        IplImage grayImage = IplImage.create(width / f, height / f, IPL_DEPTH_8U, 1);

        int imageWidth = grayImage.width();
        int imageHeight = grayImage.height();
        int dataStride = f * width;
        int imageStride = grayImage.widthStep();
        ByteBuffer imageBuffer = grayImage.getByteBuffer();
        for (int y = 0; y < imageHeight; y++) {
            int dataLine = y * dataStride;
            int imageLine = y * imageStride;
            for (int x = 0; x < imageWidth; x++) {
                imageBuffer.put(imageLine + x, data[dataLine + f * x]);
            }
        }

        return grayImage;
    }

    public static Bitmap createImage(byte[] imgData) {
        Bitmap img = null;
        try {
            img = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
            // BitmapFactory.Options options=new BitmapFactory.Options();
            // options.inSampleSize = 2;
            // img = BitmapFactory.decodeByteArray(imgData, 0,
            // imgData.length,options);
        } catch (Exception e) {
            recycleBitmap(img);
            img = null;
            // System.gc();
            SystemUtil.log(e.getMessage());
        } catch (OutOfMemoryError e) {
            recycleBitmap(img);
            img = null;
            // System.gc();
            SystemUtil.log(e.getMessage());
        }
        return img;
    }


    /**
     * 将bitmap转化为byte[]
     */
    public static byte[] Bitmap2Bytes(Bitmap bm, int quality) {
        byte[] imgByteArray = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        imgByteArray = bos.toByteArray();
        // Log.v("kxy", "--- compressed image size: " + imgByteArray.length +
        // ", quality: " + quality);
        closeQuietly(bos);
        return imgByteArray;
    }

    public static Bitmap getImageFromAssetsFile(String filename) {
        Bitmap image = null;
        AssetManager am = VariableKeeper.context.getResources().getAssets();
        try {
            InputStream is = am.open(filename);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);//此时不解码并分配存储空间只是计算出长度和宽度并存放在options变量中
            options.inSampleSize = computeSampleSize(options, -1, 128 * 128);
            options.inJustDecodeBounds = false;
            image = BitmapFactory.decodeStream(is, null, options);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }







    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    public static void reNamaeFile(String source, String dest) {
        File f = new File(source);
        if (f.exists()) {
            File dest_file = new File(dest);
            f.renameTo(dest_file);
        }
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e("error", "close failed");
                e.printStackTrace();
            }
        }
    }

    public static boolean fitApiLevel(int level) {
        try {
            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
            if (sdkVersion >= level) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        //此处应该将设备信息概况收集起来
      /*  for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }*/

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");


        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = VariableKeeper.system_file_save_BaseDir + VariableKeeper.LogFolderName;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            log("an error occured while writing file..." + e.getMessage());
        }
        return null;
    }


    /**
     * 查看SD卡的剩余空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        //返回SD卡空闲大小
        //  freeBlocks * blockSize;  //单位Byte
        //  (freeBlocks * blockSize)/1024;   //单位KB
        long free_size = (freeBlocks * blockSize) / 1024 / 1024;//单位MB
        VariableKeeper.sdCard_CurrentFreeSize = free_size;
        return free_size;
    }


    /**
     * 查看SD卡总容量
     *
     * @return
     */
    public static long getSDAllSize() {
        //取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        //获取所有数据块数
        long allBlocks = sf.getBlockCount();
        //返回SD卡大小
        //return allBlocks * blockSize; //单位Byte
        //return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; //单位MB
    }

    public static boolean isFileExistInSDRoot(String filename) {
        boolean isexist = false;
        if (new File(VariableKeeper.sdCard_mountRootPath + filename).exists()) isexist = true;
        return isexist;
    }

    /**
     * 执行 shell 脚本命令
     */
    public static void exe(String cmd, ShellExeListener shellListener) {
        /* 获取执行工具 */
        Process process = null;
        /* 存放脚本执行结果 */
        List<String> list = new ArrayList<String>();
        try {
        	/* 获取运行时环境 */
            Runtime runtime = Runtime.getRuntime();
        	/* 执行脚本 */
            process = runtime.exec(cmd);
            /* 获取脚本结果的输入流 */
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            /* 逐行读取脚本执行结果 */
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (shellListener != null) shellListener.onExec(list);
    }

    /**
     * 检测 /data/data/com.cartracker.mobile.android/files/目录下是否有指定的文件
     */
    public static boolean varifyFile(Context context, String fileName) {
        boolean exist = false;
        	/* 查看文件是否存在, 如果不存在就会走异常中的代码 */
//        	context.openFileInput(fileName);
        String[] filelists = context.fileList();
        if (filelists != null && filelists.length > 0) {
            for (String s : filelists) {
                if (s != null && s.contains(fileName)) {
                    exist = true;//如果文件已经存在 那么就忽略拷贝操作 说明用户以前已经拷贝过此文件了
                    break;
                }
            }
        }
        return exist;
    }


    /**
     * 将文件从assets目录中拷贝到app安装目录的files目录下
     */
    public static void copyFromAssets2Data(Context context, String source,
                                           String destination) throws IOException {
		/* 获取assets目录下文件的输入流 */
        InputStream is = context.getAssets().open(source);
		/* 获取文件大小 */
        int size = is.available();
		/* 创建文件的缓冲区 */
        byte[] buffer = new byte[size];
		/* 将文件读取到缓冲区中 */
        is.read(buffer);
		/* 关闭输入流 */
        is.close();
		/* 打开app安装目录文件的输出流 */
        FileOutputStream output = context.openFileOutput(destination, Context.MODE_PRIVATE);
		/* 将文件从缓冲区中写出到内存中 */
        output.write(buffer);
		/* 关闭输出流 */
        output.close();
    }


    /**
     * 将文件从assets目录中拷贝到系统任意目录下,前提是有root权限
     */
    public static void copyFromAssets2AnyDir(Context context, String source,
                                             String destination, PermissionFileHandlerListener permissionFileHandlerListener) {
        SystemUtil.log("copyFromAssets2AnyDir,source:" + source + ",destination:" + destination);
        try {
            /* 获取assets目录下文件的输入流 */
            InputStream inStream = context.getAssets().open(source);
            int byteread = 0;
            FileOutputStream fs_out = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs_out.write(buffer, 0, byteread);
            }
            fs_out.flush();
            inStream.close();
            if (permissionFileHandlerListener != null) permissionFileHandlerListener.onPermission(true, null);
        } catch (Exception e) {
            if (permissionFileHandlerListener != null)
                permissionFileHandlerListener.onPermission(false, e.getMessage());
        }
    }


    public static void giveThree6(final String filePath) {
        String cmd_changeMode = "";
        if (VariableKeeper.isUsingBusyBox) {
            cmd_changeMode = VariableKeeper.app_path + VariableKeeper.busybox_cmd_name + " chmod 0666 " + filePath;
        } else {
            cmd_changeMode = "chmod 0666 " + filePath;
        }
        SystemUtil.exe(cmd_changeMode, new ShellExeListener() {
            @Override
            public void onExec(List<String> execResult) {
                SystemUtil.log("give the file :" + filePath + ",results are:" + execResult);
            }
        });
    }

    //探测文件是否有666权限
    public static boolean isThree6(List<String> results) {
        boolean res = false;
        for (String t : results) {
            if (t.contains(VariableKeeper.three6_permisssion_string)) {
                res = true;
                break;
            }
        }
        return res;
    }

    //如果设备没有666权限 则给予666权限 即文件权限控制在这里 只要外接usb设备挂接上来并且手机有root 那么所挂接的设备就会自动具备0666权限
    public static void verifyDevice(final UsbCameraDevice ucd_tmp) {
        String cmd_tmp = "";
        if (VariableKeeper.isUsingBusyBox) {
            cmd_tmp = VariableKeeper.app_path + VariableKeeper.busybox_cmd_name + " ls -l " + ucd_tmp.getDeviceName();
        } else {
            cmd_tmp = "ls -l " + ucd_tmp.getDeviceName();
        }
        //所有挂载上的设备 自动将权限改为0666
        SystemUtil.exe(cmd_tmp, new ShellExeListener() {
            @Override
            public void onExec(List<String> execResult) {
                boolean is666 = SystemUtil.isThree6(execResult);
                SystemUtil.MyToast(ucd_tmp.getDeviceName() + " is:" + is666);
                if (!is666) {
                    //赋予文件666权限
                    SystemUtil.giveThree6(ucd_tmp.getDeviceName());
                }
            }
        });
        //修改成功后将设备列表按照venderid和productid拼成串后存入sp 方便下次对比以发现是否有新设备接入系统
        String old_devices = VariableKeeper.mSp.getString(VariableKeeper.three6_permission_device_sp_name, "");
        String new_device_infor = ucd_tmp.getVendorId() + ":" + ucd_tmp.getProductId();
        if (!"".equals(old_devices)) {
            //判断旧的记录中是否有此设备的记录值
            if (!old_devices.contains(new_device_infor)) {
                old_devices = old_devices + ",";
                new_device_infor = old_devices + new_device_infor;
                SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
                editor.putString(VariableKeeper.three6_permission_device_sp_name, new_device_infor);
                editor.commit();
            }
        } else {
            SharedPreferences.Editor editor = VariableKeeper.mSp.edit();
            editor.putString(VariableKeeper.three6_permission_device_sp_name, new_device_infor);
            editor.commit();
        }
    }


    public static byte[] toByteArray(InputStream input) throws Exception {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = null;
        byte[] result = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 100];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            result = output.toByteArray();
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
        return result;
    }

    public static void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static opencv_core.IplImage getIp1ImageFromBitmap(Bitmap mBitmap) {
        opencv_core.IplImage grabbedImage = opencv_core.IplImage.create(mBitmap.getWidth(), mBitmap.getHeight(), IPL_DEPTH_8U, 4);
        mBitmap.copyPixelsToBuffer(grabbedImage.getByteBuffer());
        return grabbedImage;
    }

    public static void MyGC() {
        if ((VariableKeeper.system_lastGC_ExecuteTime - System.currentTimeMillis()) > VariableKeeper.APP_CONSTANT.system_lastGC_DeltaTime) {
            VariableKeeper.system_lastGC_ExecuteTime = System.currentTimeMillis();
            SystemUtil.log("gc is running....");
            System.gc();
        }
    }


    public static  Bitmap convertFileToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    public static void recordStatusMonitor() {
        //每2个时间脉冲检测一次刻录线程是否在工作
        int flag = 0;
        if (VariableKeeper.videoCaptures != null) {
            for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
                if (VariableKeeper.videoCaptures[i] != null) {
                    if (VariableKeeper.videoCaptures[i].isStarted()) {
                        flag = 1;
                    }
                    for (RecordThreadStatusListener listener : VariableKeeper.threadStatusListeners) {
                        if (listener != null) {
                            listener.onStatus(i, flag, null);
                        }
                    }
                    flag = 0;
                } else {
                    for (RecordThreadStatusListener listener : VariableKeeper.threadStatusListeners) {
                        if (listener != null) {
                            listener.onStatus(i, flag, null);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < VariableKeeper.APP_CONSTANT.size_num_cam; i++) {
                for (RecordThreadStatusListener listener : VariableKeeper.threadStatusListeners) {
                    if (listener != null) {
                        listener.onStatus(i, flag, null);
                    }
                }
            }
        }
    }

    public static boolean phoneNumberCheck(String phone) {
        return true;
    }


    public static void sendSms(String cellphoneNumber, String smsContent) {
        SmsManager smsManager = SmsManager.getDefault();
        String sms_content = VariableKeeper.getmCurrentActivity().getResources().getString(R.string.emergency_sms_test_text);
        if (!"".equals(cellphoneNumber) && cellphoneNumber != null && phoneNumberCheck(cellphoneNumber)) {
            if (sms_content.length() > 70) {
                List<String> contents = smsManager.divideMessage(sms_content);
                for (String sms : contents) {
                    smsManager.sendTextMessage(cellphoneNumber, null, sms, null, null);
                }
            } else {
                smsManager.sendTextMessage(cellphoneNumber, null, sms_content, null, null);
            }


            SystemUtil.MyToast(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.emergency_sms_test_sucess));
        } else {
            SystemUtil.MyToast(VariableKeeper.getmCurrentActivity().getResources().getString(R.string.emergency_sms_no_phonenumber_text));
        }

    }

    //判断设备是否具备通话能力即是手机还是平板电脑
    public static boolean isTabletDevice() {
        TelephonyManager telephony = (TelephonyManager) VariableKeeper.context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();
        boolean result = false;
        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    public static void setDensity(BaseActivity activity) {

        if (VariableKeeper.density != 0 || activity == null) {
            return;
        }
        DisplayMetrics metric = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        VariableKeeper.density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
//		Log.v("kxy", "-------------------------------screen width: " + metric.widthPixels + ", screen height: " + metric.heightPixels);
        VariableKeeper.screenResolution = metric.widthPixels * metric.heightPixels;
        VariableKeeper.screenWidth = metric.widthPixels;
        VariableKeeper.screenHeight = metric.heightPixels;
        VariableKeeper.screen = "" + metric.widthPixels + "*" + metric.heightPixels;

    }

    //如果是手机 则判断sim卡的状态即是否有sim卡
    public static boolean isSimCardAvaiable(){
        boolean haveSim = false;
        TelephonyManager tm = (TelephonyManager)VariableKeeper.context.getSystemService(Context.TELEPHONY_SERVICE);//取得相关系统服务
        StringBuffer sb = new StringBuffer();
        switch(tm.getSimState()){ //getSimState()取得sim的状态  有下面6中状态
            case TelephonyManager.SIM_STATE_ABSENT :sb.append("无卡");break;
            case TelephonyManager.SIM_STATE_UNKNOWN :sb.append("未知状态");break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED :sb.append("需要NetworkPIN解锁");break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED :sb.append("需要PIN解锁");break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED :sb.append("需要PUK解锁");break;
            case TelephonyManager.SIM_STATE_READY :sb.append("良好");haveSim = true;break;
        }

        if(tm.getSimSerialNumber()!=null){
            sb.append("@" + tm.getSimSerialNumber().toString());
        }else{
            sb.append("@无法取得SIM卡号");
        }

        if(tm.getSimOperator().equals("")){
            sb.append("@无法取得供货商代码");
        }else{
            sb.append("@" + tm.getSimOperator().toString());
        }

        if(tm.getSimOperatorName().equals("")){
            sb.append("@无法取得供货商");
        }else{
            sb.append("@" + tm.getSimOperatorName().toString());
        }

        if(tm.getSimCountryIso().equals("")){
            sb.append("@无法取得国籍");
        }else{
            sb.append("@" + tm.getSimCountryIso().toString());
        }

        if (tm.getNetworkOperator().equals("")) {
            sb.append("@无法取得网络运营商");
        } else {
            sb.append("@" + tm.getNetworkOperator());
        }
        if (tm.getNetworkOperatorName().equals("")) {
            sb.append("@无法取得网络运营商名称");
        } else {
            sb.append("@" + tm.getNetworkOperatorName());
        }
        if (tm.getNetworkType() == 0) {
            sb.append("@无法取得网络类型");
        } else {
            sb.append("@" + tm.getNetworkType());
        }
        log("sim 卡的状态:"+sb.toString());
        return  haveSim;
    }



    public static long getDirSize(File file) {

        //  freeBlocks * blockSize;  //单位Byte
        //  (freeBlocks * blockSize)/1024;   //单位KB
//        long free_size = (freeBlocks * blockSize) / 1024 / 1024;//单位MB
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                long size = file.length();
                return size;
            }
        } else {
            log("文件或者文件夹不存在，请检查路径是否正确！");
            return 0;
        }
    }

    //探测dev下所有的视频设备 将权限更新为0666
    public static void DevScanner() {
        if (!VariableKeeper.isRooted) {
            SystemUtil.log("设备未root 拒绝扫描dev目录");
            return;
        }

        if ( VariableKeeper.isVideoDirScannable = false) {
            SystemUtil.log("上次扫描dev目录后后无新设备到来 本次不扫描...");
            return;
        }

        //只能是成root后的设备才能运行以下代码
        //==================监视/dev/video*=================
        String cmd_video_all = " ls /dev/";
        SystemUtil.exe(cmd_video_all, new InterfaceGen.ShellExeListener() {
            @Override
            public void onExec(List<String> execResult) {
                //更新VariableKeeper.SystemGlobalUsbVideoDevices中的设备列表
//                SystemUtil.log("execResult size:"+execResult.size()+","+execResult);
                List<UsbCameraDevice> usbCameraDevices = new ArrayList<UsbCameraDevice>();
                for (String vid : execResult) {
                    if (vid.contains("video")) {
                        usbCameraDevices.add(new UsbCameraDevice(0, "", 0, 0, "/dev/" + vid, 0));

                        //将所有的video设备都变成666的权限
                        List<String> commnandList = new ArrayList<String>();
                        commnandList.add("chmod 0666 " + "/dev/" + vid);
                        ShellUtils.CommandResult result = ShellUtils.execCommand(commnandList, true);
                        SystemUtil.MyToast("/dev/" + vid + " is 666 already!");
                    }
                }

                if (usbCameraDevices.size() > 0) {
                    VariableKeeper.SystemGlobalVideoDevices = null;
                    VariableKeeper.SystemGlobalVideoDevices = new UsbCameraDevice[usbCameraDevices.size()];//此处数组的长度不可确定 所有需要动态确定
                    for (int i = 0; i < VariableKeeper.SystemGlobalVideoDevices.length; i++) {
                        VariableKeeper.SystemGlobalVideoDevices[i] = usbCameraDevices.get(i);
                    }

                }

            }
        });
        VariableKeeper.isVideoDirScannable = false;
        //==================监视/dev/video*=================
    }


    public static void save2Cach(String path, byte[] image_jpg) {
        if ("".equals(path) || path == null) {
            return;
        }
        if (image_jpg == null || image_jpg.length <= 0) {
            return;
        }
        File f = new File(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
            bos.write(image_jpg, 0, image_jpg.length);
            bos.flush();
            bos.close();
            image_jpg = null;
        } catch (FileNotFoundException e) {
            SystemUtil.log(e.getMessage());
        } catch (IOException e) {
            log(e.getMessage());
        }

    }

    public static boolean[] Int2BolArray(int x) {
        if (x<0||x>15) {
            return null;
        }
        boolean[] exsits = new boolean[4];
        String arr_tmp = Integer.toBinaryString(x);
        if (!"".equals(arr_tmp)&&arr_tmp!= null) {
            if (arr_tmp.length() != 4 && arr_tmp.length()>0) {
                //此时在位数的最左端补0
                int i = 4- arr_tmp.length();
                for (int j = 0;j< i ; j++){
                    arr_tmp = "0"+arr_tmp;
                }
            }
            SystemUtil.log("camera int:"+arr_tmp);
            for (int i = 0;i<arr_tmp.length();i++) {
                if("1".equals( (arr_tmp.charAt(i)+"") ) ){
                    exsits[i] = true;
                }else {
                    exsits[i] = false;
                }
            }
        }

        return exsits;
    }

    public static void logArray(boolean [] objs) {
        if (objs == null) {
            return;
        }
        if (objs.length == 0) {
            return;
        }
        String result = "";
        for (Object o:objs) {
            result+= (o.toString()+",");
        }
        log(result);
    }


}
