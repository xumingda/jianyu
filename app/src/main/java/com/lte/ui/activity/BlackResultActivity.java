package com.lte.ui.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.utils.LETLog;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.ImsiData;
import com.lte.data.table.BlackListTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.ui.adapter.DataReportOneAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.Constants;
import com.lte.utils.DateUtils;
import com.lte.utils.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.lte.utils.AppUtils.dataParams;

/**
 * Created by chenxiaojun on 2018/2/27.
 */

public class BlackResultActivity extends BaseActivity implements DataReportOneAdapter.CheckListener {

    private static final int PLAY_SOUND = 1;
    private RecyclerView imsi_list;
    private DataReportOneAdapter mAdapter;
    private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
    private int loadId;
    private Vibrator vibrator;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PLAY_SOUND:
                    boolean isRingOn = SharedPreferencesUtil.getBooleanConfig(BlackResultActivity.this, "sniffer", "ringOn", true);
                    if (isRingOn && soundPool != null) {
                        soundPool.play(loadId, 1, 1, 1, 0, 1f);//参数：1、M取值 2、当前音量 3、最大音量 4、优先级 5、重播次数 6、播放速度
                    }
                    boolean isVibrate = SharedPreferencesUtil.getBooleanConfig(BlackResultActivity.this, "sniffer", "isVibrate", true);
                    if (vibrator != null && isVibrate) {
                        vibrator.vibrate(1000);//振动1秒
                    }
                    break;
            }

        }
    };
    private TitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_result_activity);
        init();

    }

    private void init() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(R.string.black_record);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        EventBus.getDefault().register(this);
        imsi_list = (RecyclerView) findViewById(R.id.imsi_list);
        mAdapter = new DataReportOneAdapter(this, DataManager.getInstance().findBlackImsiData(App.get().userInfo.getImsipreClearTime()), this, imsi_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        imsi_list.setLayoutManager(linearLayoutManager);
        imsi_list.setAdapter(mAdapter);
        loadId = soundPool.load(this, R.raw.beep, 1);
        // 震动效果的系统服务
        vibrator = (Vibrator)getSystemService(Activity.VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View view, ImsiDataTable imsiData) {

    }

    @Override
    public void onLongClick(View view, ImsiDataTable imsiData) {

    }
    /**
     * 接收数据上报
     */
    @Subscribe
    public void onUpdate(MessageEvent event) {
//        LETLog.d("onUpdate "+(event != null && event.imsiData != null) + " --"+(App.get().DevNumber != null));
        if (event != null && event.imsiData != null) {
            for (BlackListTable blackListTable : App.get().blackListTables) {
                if (TextUtils.equals(blackListTable.getImsi(), event.imsiData.getImsi())) {
                    event.imsiData.setIsblackList(1);
                    App.get().insert(event.imsiData);
                    handler.sendEmptyMessage(PLAY_SOUND);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
