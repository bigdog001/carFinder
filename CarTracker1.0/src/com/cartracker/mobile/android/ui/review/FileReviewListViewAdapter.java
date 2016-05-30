package com.cartracker.mobile.android.ui.review;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.data.beans.FileViewItemData;
import com.cartracker.mobile.android.data.beans.FileViewItemHolder;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.FileListsRefreshListener;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by jw362j on 9/30/2014.
 */
public class FileReviewListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<FileViewItemData> fileLists;
    private FileListsRefreshListener fileListsRefreshListener;

    public FileReviewListViewAdapter(Activity context, List<FileViewItemData> data,FileListsRefreshListener fileListsRefresh) {
        this.mActivity = context;
        this.fileLists = data;
        this.fileListsRefreshListener = fileListsRefresh;
    }

    public void dataReload(List<FileViewItemData> datas){
        fileLists = datas;
    }


    @Override
    public int getCount() {
        if(fileLists == null||fileLists.size()==0){
            return 0;
        }
        return fileLists.size();
    }

    @Override
    public Object getItem(int position) {
        return fileLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FileViewItemHolder holder = null;
        if(convertView == null){
            holder = new FileViewItemHolder(mActivity);
            convertView = holder.getRoot();
        } else {
            holder = (FileViewItemHolder) convertView.getTag();
        }
        convertView.setTag(holder);
        FileViewItemData item = this.fileLists.get(position);
//        holder.setTagId(item.hashCode());
//        item.rigisterObserver(holder);
        this.setData(holder, item);
        return holder.getRoot();
    }

    private void setData(FileViewItemHolder holder,final FileViewItemData item){
        if(holder.getTv_infor2Show() !=  null){
            holder.getTv_infor2Show().setText(item.getInfor2Show());
        }

        if (holder.getTv_filesize() != null) {
            float f_size = item.getFile_size()/(1024f*1024f);
            holder.getTv_filesize().setText(new DecimalFormat("0.00").format(f_size)+"M");
        }
        if(holder.getPlay_btn() != null){
            holder.getPlay_btn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //播放此文件
                    SystemUtil.log("play the file:"+item.getFilePath());
                    Intent play_intent = new Intent("android.intent.action.VIEW");
                    play_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Uri uri = Uri.fromFile(new File(item.getFilePath()));
                    play_intent.setDataAndType(uri,"video/*");
                    mActivity.startActivity(play_intent);
                }
            });
        }
        if (holder.getDelete_btn() != null) {
            holder.getDelete_btn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle(mActivity.getResources().getString(R.string.desktop_setings_video_rewrite_dialog_title)).setMessage(mActivity.getResources()
                            .getString(R.string.desktop_setings_video_rewrite_dialog_msg_delete))
                            .setPositiveButton(mActivity.getResources().getString(R.string.confirm),new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除此文件
                            SystemUtil.log("delete the file:"+item.getFilePath());
                            new File(item.getFilePath()).delete();
                            SystemUtil.MyToast(mActivity.getResources().getString(R.string.reviewactivity_file_delete));
                            dialog.dismiss();
                            dialog = null;
                            if(fileListsRefreshListener != null){
                                fileListsRefreshListener.fileRefresh();//刷新文件系统上的目录结构以得到最新的文件
                            }
                        }
                    }).setNegativeButton(mActivity.getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
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
    }
}
