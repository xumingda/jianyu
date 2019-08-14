package com.lte.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;

import com.lte.R;
import com.lte.data.InitConfig;
import com.lte.data.StationInfo;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.fragment.CellConfigFragment;
import com.lte.ui.fragment.DbmFragment;
import com.lte.ui.fragment.InitConfigFragment;
import com.lte.ui.fragment.ScanResultFragment;
import com.lte.ui.fragment.ScanSetFragment;
import com.lte.ui.fragment.StationFragment;
import com.lte.ui.fragment.second.AnalyCollisionFragment;
import com.lte.ui.fragment.second.AnalyFollowFragment;
import com.lte.ui.fragment.second.AnalyFragment;
import com.lte.ui.fragment.second.AnalyHistoryFragment;
import com.lte.ui.fragment.second.AnalyStatisticFragment;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.ui.listener.OnItemClickListener;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportActivity;

import static com.lte.utils.Constants.TYPE;

/**
 * Created by chenxiaojun on 2017/12/5.
 */

public class DataProcessActivity extends BaseActivity {

    private TitleBar titleBar;

    private AnalyFollowFragment analyFollowFragment;

    private AnalyCollisionFragment analyCollisionFragment;

    private AnalyStatisticFragment analyStatisticFragment;

    private AnalyHistoryFragment analyHistoryFragment;

    private int type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_process_activity);
        init();
        Intent intent = getIntent();
        type = intent.getIntExtra(TYPE,0);
        initRootFragment(type);
        EventBus.getDefault().register(this);
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
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {

        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getTopFragment() instanceof AnalyFragment){
                    finish();
                }else {
                    onBackPressedSupport();
                }
            }
        });

    }

    private void initRootFragment(int type) {
        switch (type){
            case 1:
                analyFollowFragment = findFragment(AnalyFollowFragment.class);
                if (analyFollowFragment == null) {
                    analyFollowFragment = AnalyFollowFragment.newInstance();
                    loadRootFragment(R.id.fl_container, analyFollowFragment);
                }
                titleBar.setTitle(getString(R.string.follow));
                break;
            case 2:
                analyCollisionFragment = findFragment(AnalyCollisionFragment.class);
                if (analyCollisionFragment == null) {
                    analyCollisionFragment = AnalyCollisionFragment.newInstance();
                    loadRootFragment(R.id.fl_container, analyCollisionFragment);
                }
                titleBar.setTitle(getString(R.string.collision));
                break;
            case 3:
                analyStatisticFragment = findFragment(AnalyStatisticFragment.class);
                if (analyStatisticFragment == null) {
                    analyStatisticFragment = AnalyStatisticFragment.newInstance();
                    loadRootFragment(R.id.fl_container, analyStatisticFragment);
                }
                titleBar.setTitle(getString(R.string.statistic));
                break;
            case 4:
                analyHistoryFragment = findFragment(AnalyHistoryFragment.class);
                if (analyHistoryFragment == null) {
                    analyHistoryFragment = AnalyHistoryFragment.newInstance();
                    loadRootFragment(R.id.fl_container, analyHistoryFragment);
                }
                titleBar.setTitle(getString(R.string.history));
                break;
        }
    }
}
