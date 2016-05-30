package com.cartracker.mobile.android.ui.review;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.*;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.data.beans.FileViewItemData;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.ui.widget.timepicker.StrericWheelAdapter;
import com.cartracker.mobile.android.ui.widget.timepicker.WheelView;
import com.cartracker.mobile.android.util.PathGenerator;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.FileListsRefreshListener;
import com.cartracker.mobile.android.util.handler.InterfaceGen.TimePickerHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jw362j on 9/28/2014.
 */
public class ReViewActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, TimePickerHandler, FileListsRefreshListener {
    private MyTitlebar rvt;
    private View contentView;
    private Spinner camera_id;
    private ArrayAdapter<String> adapter;
    private static final String[] cameraLists = {"0", "1", "2", "3"};//最终会为每一个镜头取一个别名存放在sp中 暂且以序号替代
    private int camera_seected = 0;
    private String time_selected = "";//通过time_selected的值和camera_seected的值可以确定需要回放的视频位于哪个目录
    private ListView video_record_review_file_lists;
    private List<FileViewItemData> fileViewItemDatas;
    private FileReviewListViewAdapter fileReviewListViewAdapter;
    private String currentVideoFolderPath;//当前查看的目录路径值


    //===================for time_picker=====================
    private int minYear = 1970;
    private int fontSize = 13;
    private WheelView yearWheel, monthWheel, dayWheel, hourWheel, minuteWheel, secondWheel;
    public static String[] yearContent = null;
    public static String[] monthContent = null;
    public static String[] dayContent = null;
    public static String[] hourContent = null;
    public static String[] minuteContent = null;
    public static String[] secondContent = null;
    private TextView result_show_tv;
    private Button pick_time_bt;
//===================for time_picker=====================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(ReViewActivity.this, R.layout.record_review, null);
        setContentView(contentView);
        LinearLayout detect_view_title = (LinearLayout) findViewById(R.id.record_view_titlebar);
        initTitle();
        detect_view_title.addView(rvt.getRecord_view_titlebar());
        result_show_tv = (TextView) contentView.findViewById(R.id.result_show_tv);
        pick_time_bt = (Button) contentView.findViewById(R.id.pick_time_bt);
        camera_id = (Spinner) contentView.findViewById(R.id.camera_id);
        video_record_review_file_lists = (ListView) contentView.findViewById(R.id.video_record_review_file_lists);

        video_record_review_file_lists.setItemsCanFocus(true);
        video_record_review_file_lists.setFocusable(false);
        video_record_review_file_lists.setAddStatesFromChildren(true);
        video_record_review_file_lists.setFocusableInTouchMode(false);
        video_record_review_file_lists.setVerticalFadingEdgeEnabled(false);
        video_record_review_file_lists.setDivider(getResources().getDrawable(
                R.drawable.divider_new));
        video_record_review_file_lists.setFooterDividersEnabled(false);
        video_record_review_file_lists.setCacheColorHint(0);

        //origin_action
        Intent intent = this.getIntent();        //获取已有的intent对象
        if (intent != null) {
            Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
            if (bundle != null) {
                String origin_action = bundle.getString("origin_action");
                if ("dir_clean".equals(origin_action)) {
                    //说明此事应该提示用户选择日期以清理sd卡
                    SystemUtil.MyToast(getResources().getString(R.string.reviewactivity_file_clean_toast_tips_text));
                }
            }
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cameraLists);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        camera_id.setAdapter(adapter);
        //添加事件Spinner事件监听
        camera_id.setOnItemSelectedListener(this);
        //设置默认值
        camera_id.setVisibility(View.VISIBLE);
        pick_time_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(ReViewActivity.this);
            }
        });
        initTimePicker();
    }

    private void initTimePicker() {
        yearContent = new String[10];
        for (int i = 0; i < 10; i++)
            yearContent[i] = String.valueOf(i + 2013);
        monthContent = new String[12];
        for (int i = 0; i < 12; i++) {
            monthContent[i] = String.valueOf(i + 1);
            if (monthContent[i].length() < 2) {
                monthContent[i] = "0" + monthContent[i];
            }
        }

        dayContent = new String[31];
        for (int i = 0; i < 31; i++) {
            dayContent[i] = String.valueOf(i + 1);
            if (dayContent[i].length() < 2) {
                dayContent[i] = "0" + dayContent[i];
            }
        }
        hourContent = new String[24];
        for (int i = 0; i < 24; i++) {
            hourContent[i] = String.valueOf(i);
            if (hourContent[i].length() < 2) {
                hourContent[i] = "0" + hourContent[i];
            }
        }

        minuteContent = new String[60];
        for (int i = 0; i < 60; i++) {
            minuteContent[i] = String.valueOf(i);
            if (minuteContent[i].length() < 2) {
                minuteContent[i] = "0" + minuteContent[i];
            }
        }
        secondContent = new String[60];
        for (int i = 0; i < 60; i++) {
            secondContent[i] = String.valueOf(i);
            if (secondContent[i].length() < 2) {
                secondContent[i] = "0" + secondContent[i];
            }
        }
    }

    private void pickTime(final TimePickerHandler timePickerHandler) {
        View view = View.inflate(getActivity(), R.layout.time_picker, null);

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int curMonth = calendar.get(Calendar.MONTH) + 1;
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        int curSecond = calendar.get(Calendar.SECOND);

        yearWheel = (WheelView) view.findViewById(R.id.yearwheel);
        monthWheel = (WheelView) view.findViewById(R.id.monthwheel);
        dayWheel = (WheelView) view.findViewById(R.id.daywheel);
        hourWheel = (WheelView) view.findViewById(R.id.hourwheel);
        minuteWheel = (WheelView) view.findViewById(R.id.minutewheel);
        secondWheel = (WheelView) view.findViewById(R.id.secondwheel);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
        yearWheel.setCurrentItem(curYear - 2013);
        yearWheel.setCyclic(true);
        yearWheel.setInterpolator(new AnticipateOvershootInterpolator());


        monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

        monthWheel.setCurrentItem(curMonth - 1);

        monthWheel.setCyclic(true);
        monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

        dayWheel.setAdapter(new StrericWheelAdapter(dayContent));
        dayWheel.setCurrentItem(curDay - 1);
        dayWheel.setCyclic(true);
        dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

        hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
        hourWheel.setCurrentItem(curHour);
        hourWheel.setCyclic(true);
        hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

        minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
        minuteWheel.setCurrentItem(curMinute);
        minuteWheel.setCyclic(true);
        minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

        secondWheel.setAdapter(new StrericWheelAdapter(secondContent));
        secondWheel.setCurrentItem(curSecond);
        secondWheel.setCyclic(true);
        secondWheel.setInterpolator(new AnticipateOvershootInterpolator());

        builder.setTitle(getResources().getString(R.string.review_video_pick_time));
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                StringBuffer sb = new StringBuffer();
                sb.append(yearWheel.getCurrentItemValue()).append("-")
                        .append(monthWheel.getCurrentItemValue()).append("-")
                        .append(dayWheel.getCurrentItemValue());

                sb.append(" ");
                sb.append(hourWheel.getCurrentItemValue())
                        .append(":").append(minuteWheel.getCurrentItemValue())
                        .append(":").append(secondWheel.getCurrentItemValue());
                if (timePickerHandler != null) {
                    timePickerHandler.pickTime(sb.toString());
                }
                dialog.cancel();
            }
        });

        builder.show();

    }


    private void initTitle() {
        rvt = new MyTitlebar(ReViewActivity.this);
        rvt.setTitle(getResources().getString(R.string.title_video_review));
        rvt.setLeftBtnShow(true);
        rvt.setLeftBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void stopAll() {

    }


    @Override
    public void pickTime(String timeValue) {
        //得到当前选择的时间
        if (!"".equals(timeValue)) timeValue = timeValue.trim();
        time_selected = timeValue;
        openVideoList();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        得到当前选择的镜头
        camera_seected = position;
        openVideoList();
    }

    private void openVideoList() {
        if (!"".equals(time_selected) && !"".equals(camera_seected)) {
            //镜头数据和时间数据都有值
            SystemUtil.log(time_selected + "," + camera_seected);

            currentVideoFolderPath = PathGenerator.GeneratorPathFromPicker(time_selected) + camera_seected;
            SystemUtil.log("my path is:" + currentVideoFolderPath);
            File f = new File(currentVideoFolderPath);
            String time_show = time_selected.split(":")[0];

            if (f != null && f.exists()) {
                if (f.isDirectory()) {
                    if (f.listFiles().length > 0) {
                        //此时迭代出所有的录像文件然后现实中爱listview中
                        File[] files = f.listFiles();
                        SystemUtil.log("文件总数为:" + files.length);
                        result_show_tv.setText(getResources().getString(R.string.reviewactivity_time_select) + time_show +
                                getResources().getString(R.string.reviewactivity_camera_select) + cameraLists[camera_seected] + getResources().getString(R.string.reviewactivity_file_total_count) + files.length);
                        result_show_tv.setVisibility(View.VISIBLE);

                        //为adapter构建数据
                        initFileItems(files);
                        fileReviewListViewAdapter = new FileReviewListViewAdapter(getActivity(), fileViewItemDatas, ReViewActivity.this);
                        video_record_review_file_lists.setAdapter(fileReviewListViewAdapter);

                    } else {
                        SystemUtil.MyToast(getResources().getString(R.string.reviewactivity_no_file_tips_2));
                    }
                } else {
                    SystemUtil.MyToast(getResources().getString(R.string.reviewactivity_no_file_tips_3));
                }
            } else {

                result_show_tv.setText(getResources().getString(R.string.reviewactivity_time_select) + time_show +
                        getResources().getString(R.string.reviewactivity_camera_select) + cameraLists[camera_seected] + getResources().getString(R.string.reviewactivity_file_total_count) + 0);
                result_show_tv.setVisibility(View.VISIBLE);
                fileViewItemDatas = new ArrayList<FileViewItemData>();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fileReviewListViewAdapter != null) {
                            fileReviewListViewAdapter.dataReload(fileViewItemDatas);
                            fileReviewListViewAdapter.notifyDataSetChanged();
                        }

                    }
                });
                SystemUtil.MyToast(getResources().getString(R.string.reviewactivity_no_file_tips_1));
            }

        }
    }

    private void initFileItems(File[] folderPaths) {
        if (fileViewItemDatas != null) fileViewItemDatas.clear();
        fileViewItemDatas = new ArrayList<FileViewItemData>();
        if (folderPaths != null && folderPaths.length > 0) {
            for (File filepath : folderPaths) {
                fileViewItemDatas.add(new FileViewItemData(filepath.getAbsolutePath(), filepath.getName(), filepath.length()));
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void fileRefresh() {
        File f_currentVideoFolderPath = new File(currentVideoFolderPath);
        if (f_currentVideoFolderPath.isDirectory()) {
            if (f_currentVideoFolderPath.listFiles().length > 0) {
                String time_show = time_selected.split(":")[0];
                result_show_tv.setText(getResources().getString(R.string.reviewactivity_time_select) + time_show +
                        getResources().getString(R.string.reviewactivity_camera_select) + cameraLists[camera_seected] + getResources().getString(R.string.reviewactivity_file_total_count) + f_currentVideoFolderPath.listFiles().length);
                result_show_tv.setVisibility(View.VISIBLE);
                File[] files = f_currentVideoFolderPath.listFiles();
                SystemUtil.log("文件总数为:" + files.length);
                //为adapter构建数据
                initFileItems(files);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        ((BaseAdapter)video_record_review_file_lists.getAdapter()).notifyDataSetChanged();
//                        video_record_review_file_lists.setAdapter(new FileReviewListViewAdapter(getActivity(),fileViewItemDatas,ReViewActivity.this));
                        fileReviewListViewAdapter.dataReload(fileViewItemDatas);
                        fileReviewListViewAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                File[] files = f_currentVideoFolderPath.listFiles();
                SystemUtil.log("文件总数为:" + files.length);
                //为adapter构建数据
                initFileItems(files);
                SystemUtil.MyToast(getResources().getString(R.string.reviewactivity_no_file_tips_2));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        ((BaseAdapter)video_record_review_file_lists.getAdapter()).notifyDataSetChanged();
//                        video_record_review_file_lists.setAdapter(new FileReviewListViewAdapter(getActivity(),null,ReViewActivity.this));
                        fileReviewListViewAdapter.dataReload(fileViewItemDatas);
                        fileReviewListViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        } else {
            SystemUtil.MyToast(getResources().getString(R.string.reviewactivity_no_file_tips_3));
        }

    }
}
