package com.cartracker.mobile.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.ui.review.ReViewActivity;
import com.cartracker.mobile.android.util.SystemUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by jw362j on 9/29/2014.
 */
public class DataFragment extends Fragment implements View.OnClickListener {
    private TextView id_sdcard_size_tv,id_record_path_tv,id_record_dir_size_tv;
    private TextView id_sdcard_abs_size_tv,id_sdcard_already_use_size_tv,app_start_time;
    private long all_size ;
    private long free_size ;
    private ImageView iv_sdcard_all_id,id_sdcard_used_size_iv;
    private Button btn_data_clean_record_dir;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.desktop_data_fragment, container, false);
        initView(view);
        return view;
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    private void initView(View view) {
        btn_data_clean_record_dir = (Button) view.findViewById(R.id.btn_data_clean_record_dir);
        id_sdcard_used_size_iv = (ImageView) view.findViewById(R.id.id_sdcard_used_size_iv);
        iv_sdcard_all_id = (ImageView) view.findViewById(R.id.iv_sdcard_all_id);
        id_record_path_tv = (TextView) view.findViewById(R.id.id_record_path_tv);
        id_record_dir_size_tv = (TextView) view.findViewById(R.id.id_record_dir_size_tv);
        id_sdcard_size_tv = (TextView) view.findViewById(R.id.id_sdcard_size_tv);
        app_start_time = (TextView) view.findViewById(R.id.app_start_time);
        id_sdcard_abs_size_tv = (TextView) view.findViewById(R.id.id_sdcard_abs_size_tv);
        id_sdcard_already_use_size_tv = (TextView) view.findViewById(R.id.id_sdcard_already_use_size_tv);
        all_size = SystemUtil.getSDAllSize();
        free_size = SystemUtil.getSDFreeSize();
        float use_rate = (all_size - free_size)/(float)all_size;

        btn_data_clean_record_dir.setOnClickListener(DataFragment.this);

        SystemUtil.log("wwwwwwww"+getScreenWidth());

        id_sdcard_used_size_iv.getLayoutParams().width = (int) (getScreenWidth()*use_rate);
        id_sdcard_size_tv.setText(all_size+"MB");

        float used_G = (all_size - free_size)/1024f;
        float all_G =  all_size /1024f;
        id_sdcard_abs_size_tv.setText(new DecimalFormat("0.00").format(used_G) +"GB/"+new DecimalFormat("0.00").format(all_G)+"GB");
        id_sdcard_already_use_size_tv.setText(new DecimalFormat("0.00").format(use_rate * 100) +"%");
        id_record_path_tv.setText(VariableKeeper.sdCard_mountRootPath+VariableKeeper.APP_CONSTANT.base_folder_name+ File.separator+VariableKeeper.VideoFolderName+File.separator);
        id_record_dir_size_tv.setText((SystemUtil.getDirSize(new File(VariableKeeper.sdCard_mountRootPath+VariableKeeper.APP_CONSTANT.base_folder_name+ File.separator+VariableKeeper.VideoFolderName+File.separator))/1024/1024)+"MB");
        app_start_time.setText(new Date(VariableKeeper.system_startupTime).toLocaleString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        int view_id = v.getId();
        switch (view_id) {
            case R.id.btn_data_clean_record_dir:
                Intent intent = new Intent(getActivity(), ReViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("origin_action", "dir_clean");     //标记清理动作
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
