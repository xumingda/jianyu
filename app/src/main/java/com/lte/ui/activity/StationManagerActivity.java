package com.lte.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.InitConfig;
import com.lte.data.StationInfo;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.fragment.CellConfigFragment;
import com.lte.ui.fragment.DbmFragment;
import com.lte.ui.fragment.GsmFragment;
import com.lte.ui.fragment.GsmFragment1;
import com.lte.ui.fragment.InitConfigFragment;
import com.lte.ui.fragment.SIBFiveScanResultFragment;
import com.lte.ui.fragment.ScanResultFragment;
import com.lte.ui.fragment.ScanSetFragment;
import com.lte.ui.fragment.StationFragment;
import com.lte.ui.fragment.CdmaFragment;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.ui.listener.OnItemClickListener;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by chenxiaojun on 2017/9/11.
 */

public class StationManagerActivity extends BaseActivity implements OnItemClickListener,OnBackPressedListener{

    private TitleBar titleBar;

    private StationFragment stationFragment;

    private StationInfo stationInfo;

    private InitConfig initConfig;
    private ArrayList<Integer> scanSetPciList;
    private ArrayList<Integer> scanSetEarfchList;
    private ArrayList<Integer> CellConfigPciList;
    private ArrayList<Integer> CellConfigPlmnList;
    private ArrayList<Integer> CellConfigPilotList;
    private SweetAlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_activity);
        init();
        getIntentData();
        initRootFragment();
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

        titleBar.setTitle(getString(R.string.config_station));

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getTopFragment() instanceof StationFragment){
                    finish();
                }else {
                    titleBar.setTitle(getString(R.string.config_station));
                    onBackPressedSupport();
                }
            }
        });
        titleBar.addAction(new TitleBar.TextAction("清除") {
            @Override
            public void performAction(View view) {
                showDialog();
            }
        });

    }
    private void showDialog() {
        mDialog = new SweetAlertDialog.Builder(this)
                .setTitle(R.string.clear_scanresult)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        DataManager.getInstance().clearScanResult(stationInfo);
                    }
                })
                .setNegativeButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.show();
    }
    private void initRootFragment() {
        stationFragment = findFragment(StationFragment.class);
        if (stationFragment == null) {
            Log.d("Station" ,"type :  "+ stationInfo.getType());
            stationFragment = StationFragment.newInstance(stationInfo.getType());
            loadRootFragment(R.id.fl_container, stationFragment);
        }
    }

    @Override
    public void onClick(View view, int position) {
        switch (position){
            //xmd，2之后多一项，往后加
            case 0:
                start(ScanSetFragment.newInstance(stationInfo,scanSetPciList,scanSetEarfchList));
                titleBar.setTitle(getString(R.string.scan_set));
                titleBar.setRightVisible(false);
                break;
            case 1:
                start(CellConfigFragment.newInstance(stationInfo,CellConfigPciList,CellConfigPlmnList,CellConfigPilotList));
                titleBar.setTitle(getString(R.string.cill_config));
                titleBar.setRightVisible(false);
                break;
            case 2:
                start(ScanResultFragment.newInstance(stationInfo.getId()));
                titleBar.setTitle(getString(R.string.scan_result1));
                titleBar.setRightVisible(true);
                break;
            case 3:
                start(SIBFiveScanResultFragment.newInstance(stationInfo.getId()));
                titleBar.setTitle(getString(R.string.sib5_scan_result1));
                titleBar.setRightVisible(true);
                break;
            case 4:
                start(InitConfigFragment.newInstance(initConfig,stationInfo));
                titleBar.setTitle(getString(R.string.init_config));
                titleBar.setRightVisible(false);
                break;
            case 5:
                start(DbmFragment.newInstance(stationInfo));
                titleBar.setTitle(getString(R.string.dbm_set));
                titleBar.setRightVisible(false);
                break;
            case 6:
                start(GsmFragment.newInstance(stationInfo.getId()));
                titleBar.setTitle(getString(R.string.gsm));
                titleBar.setRightVisible(false);
                break;
            case 7:
                start(GsmFragment1.newInstance(stationInfo.getId()));
                titleBar.setTitle(getString(R.string.gsm));
                titleBar.setRightVisible(false);
                break;
            case 8:
                start(CdmaFragment.newInstance(stationInfo.getId()));
                titleBar.setTitle(getString(R.string.wcda));
                titleBar.setRightVisible(false);
                break;
        }
    }

    public void getIntentData() {
        Bundle bundle = getIntent().getBundleExtra(Constants.BUNDLE);
        stationInfo = bundle.getParcelable(Constants.STATION);
        initConfig = stationInfo.getInitConfig();
        scanSetPciList = bundle.getIntegerArrayList(Constants.SCANSET_PCI);
        scanSetEarfchList = bundle.getIntegerArrayList(Constants.EARFCH);
        CellConfigPciList = bundle.getIntegerArrayList(Constants.SCANSET_PCI);
        CellConfigPlmnList = bundle.getIntegerArrayList(Constants.SCANSET_PCI);
        CellConfigPilotList = bundle.getIntegerArrayList(Constants.SCANSET_PCI);
        Log.d("Station","initConfig"+ (initConfig == null) + (stationInfo.toString()));
    }

    @Override
    public void onBack() {
        if(getTopFragment() instanceof StationFragment){
            finish();
        }else {
            onBackPressedSupport();
            titleBar.setTitle(getString(R.string.config_station));
        }
    }
}
