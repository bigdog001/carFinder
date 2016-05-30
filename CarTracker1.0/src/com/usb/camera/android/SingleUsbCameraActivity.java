package com.usb.camera.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.cartracker.mobile.android.R;

public class SingleUsbCameraActivity extends Activity {

    SingleCameraPreview cp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_singlecamera_test);
		cp = (SingleCameraPreview) findViewById(R.id.cp);
	}
	
}
