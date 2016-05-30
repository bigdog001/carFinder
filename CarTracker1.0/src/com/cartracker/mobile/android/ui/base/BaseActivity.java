package com.cartracker.mobile.android.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.cartracker.mobile.android.config.VariableKeeper;
import com.cartracker.mobile.android.util.SystemUtil;
import com.cartracker.mobile.android.util.handler.InterfaceGen.appStop;

/**
 * Created by jw362j on 9/19/2014.
 */
public abstract class BaseActivity extends Activity implements appStop {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SystemUtil.setDensity(BaseActivity.this);
        VariableKeeper.setmCurrentActivity(this);
        VariableKeeper.addStop(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (VariableKeeper.activityStack != null) {
            if (VariableKeeper.activityStack.contains(this)) {
                VariableKeeper.activityStack.remove(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VariableKeeper.activityStack != null) {
            if (!VariableKeeper.activityStack.contains(this)) {
                VariableKeeper.activityStack.push(this);
            }
        }

    }

    @Override
    protected void onDestroy() {
        VariableKeeper.activityStack.remove(this);
        super.onDestroy();
    }
    
    protected Activity getActivity(){
    	return BaseActivity.this;
    }


}
