package com.cartracker.mobile.android.ui.VideoPreview;


import android.os.Bundle;
import com.cartracker.mobile.android.R;
import com.cartracker.mobile.android.camera.CameraConnect;
import com.cartracker.mobile.android.ui.base.BaseActivity;

public class UsbCameraMain extends BaseActivity{
	
	private CameraConnect cp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usb_camera_preview);
		cp = (CameraConnect) findViewById(R.id.cp);
	}

    @Override
    public void stopAll() {
       if(cp != null) cp = null;
    }
}
