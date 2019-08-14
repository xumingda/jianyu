package com.lte.ui.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.lte.ui.event.SystemOutEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.Semaphore;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by chenxiaojun on 2018/2/9.
 */

public class BaseActivity extends SupportActivity {
    private static final String TAG = "BaseActivity";
    private PowerManager.WakeLock mWakeLock;
    private PowerManager pManager;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        pManager = ((PowerManager) getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(null != mWakeLock){
            mWakeLock.release();
        }
    }
    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent){
        if(outEvent.isOut()){
            this.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
