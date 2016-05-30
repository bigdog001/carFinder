package com.cartracker.mobile.android.ui.VideoPreview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.ui.base.BaseActivity;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.CameraPreviewListener;

/**
 * Created by jw362j on 9/21/2014.
 */
public class VideoPreviewActivity extends BaseActivity {
    private CameraPreviewListener cameraPreviewListener;
    private Dialog dialog;
    private CharSequence [] charSequences;
    private CameraPreview cp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        charSequences= new CharSequence[]{
                getResources().getString(R.string.menu_preview_setting_channel_0),
                getResources().getString(R.string.menu_preview_setting_channel_1),
                getResources().getString(R.string.menu_preview_setting_channel_2),
                getResources().getString(R.string.menu_preview_setting_channel_3),
                getResources().getString(R.string.menu_preview_setting_channel_4)};
        cp = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreviewListener = cp;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_preview, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SystemUtil.log(".....==="+cp.getCurrentChannel());
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(cp.getCurrentChannel() != 4){
                //单屏大画面显示 则回到四屏模式
                cameraPreviewListener.listen(4);
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.menu_video_preview_save_now:
                //发送刻录文件切换的广播消息
                sendBroadcast(new Intent(VariableKeeper.APP_CONSTANT.TIMER_BROADCAST_FILE_SWITCH));
                break;
            case R.id.menu_preview_turnoff_preview:
                break;
            case R.id.menu_preview_setting_preview:
                //让用户选择当前显示哪一路信号
                showSettingDialog();
                break;
            default:
                break;
        }
        return true;
    }

    private void showSettingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoPreviewActivity.this);
        builder.setTitle(R.string.menu_preview_setting_title).setItems(charSequences,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which) {
                switch (which) {
                    case 0:
                        if(cameraPreviewListener != null)cameraPreviewListener.listen(0);
                        break;
                    case 1:
                        if(cameraPreviewListener != null)cameraPreviewListener.listen(1);
                        break;
                    case 2:
                        if(cameraPreviewListener != null)cameraPreviewListener.listen(2);
                        break;
                    case 3:
                        if(cameraPreviewListener != null)cameraPreviewListener.listen(3);
                        break;
                    case 4:
                        if(cameraPreviewListener != null)cameraPreviewListener.listen(4);
                        break;
                    default:
                        if(cameraPreviewListener != null)cameraPreviewListener.listen(4);
                        break;
                }
                dialog.dismiss();
                dialog = null;
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void stopAll() {
        if (dialog != null ) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }else {
                dialog = null;
            }
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SystemUtil.log("get out of preview......onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SystemUtil.log("get out of preview......onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SystemUtil.log("get out of preview......onRestart");
        cp.onSurfaceViewRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SystemUtil.log("get out of preview......onDestroy");
    }
}
