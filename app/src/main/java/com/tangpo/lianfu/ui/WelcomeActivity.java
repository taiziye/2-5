package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class WelcomeActivity extends Activity{
    private static final String TAG = "WelcomeActivity";
    private boolean isStartGuide;
    private LocationManager locationManager = null;


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        // 在欢迎界面停留3秒
        new Handler().postDelayed(new Runnable() {
            public void run() {

                Tools.gotoActivity(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
