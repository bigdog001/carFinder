package com.cartracker.mobile.android.data.beans;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.cartracker.mobile.android.R;

/**
 * Created by jw362j on 9/30/2014.
 */
public class FileViewItemHolder {
    private View root;
    private TextView tv_infor2Show;
    private TextView tv_filesize;
    private ImageView play_btn,delete_btn;
    private ImageView imageView_thumb;

    public FileViewItemHolder(Context context){
        root = View.inflate(context, R.layout.video_review_filelist_item,null);
        tv_infor2Show = (TextView) root.findViewById(R.id.tv_infor2Show);
        tv_filesize = (TextView) root.findViewById(R.id.tv_filesize);
        play_btn = (ImageView) root.findViewById(R.id.play_btn);
        delete_btn = (ImageView) root.findViewById(R.id.delete_btn);
        imageView_thumb = (ImageView) root.findViewById(R.id.imageView_thumb);
    }

    public View getRoot() {
        return root;
    }

    public void setRoot(View root) {
        this.root = root;
    }

    public TextView getTv_infor2Show() {
        return tv_infor2Show;
    }

    public void setTv_infor2Show(TextView tv_infor2Show) {
        this.tv_infor2Show = tv_infor2Show;
    }

    public ImageView getPlay_btn() {
        return play_btn;
    }

    public void setPlay_btn(ImageView play_btn) {
        this.play_btn = play_btn;
    }

    public ImageView getDelete_btn() {
        return delete_btn;
    }

    public void setDelete_btn(ImageView delete_btn) {
        this.delete_btn = delete_btn;
    }

    public ImageView getImageView_thumb() {
        return imageView_thumb;
    }

    public void setImageView_thumb(ImageView imageView_thumb) {
        this.imageView_thumb = imageView_thumb;
    }

    public TextView getTv_filesize() {
        return tv_filesize;
    }

    public void setTv_filesize(TextView tv_filesize) {
        this.tv_filesize = tv_filesize;
    }
}
