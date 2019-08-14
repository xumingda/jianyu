package com.lte.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.App;
import com.communication.utils.LETLog;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.lte.R;
import com.lte.data.StationInfo;
import com.lte.data.TargetBean;
import com.lte.data.table.BlackListTable;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.activity.StationManagerActivity;
import com.lte.ui.adapter.BbuAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.event.RestartEvent;
import com.lte.ui.event.TargetListMessage;
import com.lte.ui.widget.NavigationPageView;
import com.lte.ui.widget.RecyclerViewDivider;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.SwitchButton;
import com.lte.utils.Constants;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ToastUtils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.lte.utils.Constants.UPGRADE;
import static com.communication.utils.DateUtil.getOpera1;




/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class DeviceFragment extends BaseMainFragment implements BbuAdapter.CheckListener, CompoundButton.OnCheckedChangeListener {



    public static final int CHECK_SWITCH_YD = 2;
    public static final int CHECK_SWITCH_LT = 3;
    public static final int CHECK_SWITCH_DX = 4;

    private RecyclerView recyclerView;

    private BbuAdapter mAdapter;


    private List<StationInfo> tempList;
    private SweetAlertDialog mDialog;
    private SweetAlertDialog mDialog1;
    private SweetAlertDialog mDialog2;
    private SweetAlertDialog mDialog4;
    private IndicatorSeekBar seekBar;
    private SwitchButton sb_position_vibrate;
    private SweetAlertDialog mDialog3;
    private SweetAlertDialog mDialog5;
    private SwitchButton sb_data_vibrate;

    protected ExpandableLinearLayout mExpandableLayout;
    protected RelativeLayout mExpandButton;
    protected View mRotateView;
    protected NavigationPageView mNavPageView;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private SwitchButton sb_point_vibrate;
    private SwitchButton sb_point_vibrate1;
    private SwitchButton sb_point_vibrate2;

    private int seekBarProcess;


//    private StationInfo stationInfo;
//    private OnItemClickListener mActivityListener;

    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPGRADE:
                    mAdapter.setNewData(App.get().getOnLineList());
                    break;
                case CHECK_SWITCH_YD: {
                    String s = (String) msg.obj;
                    if (TextUtils.equals(s, "true")) {
                        sb_point_vibrate.setCheckedImmediately(true);
                    } else {
                        sb_point_vibrate.setCheckedImmediately(false);
                    }
                }
                    break;

                case CHECK_SWITCH_LT: {
                    String s1 = (String) msg.obj;
                    if (TextUtils.equals(s1, "true")) {
                        sb_point_vibrate1.setCheckedImmediately(true);
                    } else {
                        sb_point_vibrate1.setCheckedImmediately(false);
                    }
                }
                    break;

                case CHECK_SWITCH_DX:{
                    String s2 = (String) msg.obj;
                    if (TextUtils.equals(s2, "true")) {
                        sb_point_vibrate2.setCheckedImmediately(true);
                    } else {
                        sb_point_vibrate2.setCheckedImmediately(false);
                    }
                }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        seekBar.setProgress(seekBarProcess);
    }

    @Subscribe
    public void onUpdate(MessageEvent event) {
        if (event.isUpDate) {
            handler.sendEmptyMessage(UPGRADE);
        }

        if(event.point_vibrate!=null){
            LETLog.d("DeviceFragment onUpdate:point_vibrate="+event.point_vibrate);
            if(event.point_vibrate.equals("false")){
                LETLog.d("DeviceFragment set point_vibrate false");
                //sb_point_vibrate.setChecked(false);
                Message msg=handler.obtainMessage();
                msg.what=CHECK_SWITCH_YD;
                msg.obj="false";
                handler.sendMessageDelayed(msg,100L);
                //sb_point_vibrate.setCheckedImmediately(false);
            }
            else{
                LETLog.d("DeviceFragment set point_vibrate true");
                //sb_point_vibrate.setChecked(true);
                //sb_point_vibrate.setCheckedImmediately(true);

                Message msg=handler.obtainMessage();
                msg.what=CHECK_SWITCH_YD;
                msg.obj="true";
                handler.sendMessageDelayed(msg,100L);

            }
        }

        if(event.point_vibrate1!=null){
            LETLog.d("DeviceFragment onUpdate:point_vibrate1="+event.point_vibrate1);
            if(event.point_vibrate1.equals("false")){
                LETLog.d("DeviceFragment set point_vibrate1 false");
                //sb_point_vibrate1.setChecked(false);
                //sb_point_vibrate1.setCheckedImmediately(false);

                Message msg=handler.obtainMessage();
                msg.what=CHECK_SWITCH_LT;
                msg.obj="false";
                handler.sendMessageDelayed(msg,100L);
            }
            else{
                LETLog.d("DeviceFragment set point_vibrate1 true");
                //sb_point_vibrate1.setChecked(true);
                //sb_point_vibrate1.setCheckedImmediately(true);
                Message msg=handler.obtainMessage();
                msg.what=CHECK_SWITCH_LT;
                msg.obj="true";
                handler.sendMessageDelayed(msg,100L);
            }
        }

        if(event.point_vibrate2!=null){
            LETLog.d("DeviceFragment onUpdate:point_vibrate2="+event.point_vibrate2);
            if(event.point_vibrate2.equals("false")){
                LETLog.d("DeviceFragment set point_vibrate2 false");
                //sb_point_vibrate2.setCheckedImmediately(false);
                Message msg=handler.obtainMessage();
                msg.what=CHECK_SWITCH_DX;
                msg.obj="false";
                handler.sendMessageDelayed(msg,100L);
            }
            else{
                LETLog.d("DeviceFragment set point_vibrate2 true");
                //sb_point_vibrate2.setCheckedImmediately(true);
                Message msg=handler.obtainMessage();
                msg.what=CHECK_SWITCH_DX;
                msg.obj="true";
                handler.sendMessageDelayed(msg,100L);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            handler.sendEmptyMessage(UPGRADE);
        }
    }

    private void init(final View view) {

        EventBus.getDefault().register(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.device_list);
        recyclerView.addItemDecoration(new RecyclerViewDivider(
                _mActivity, LinearLayoutManager.HORIZONTAL, 4, ContextCompat.getColor(_mActivity, R.color.gray_c8)));
        tempList = new ArrayList<>();

//        for (StationInfo stationInfo : App.get().getmList()) {
//            try {
//                StationInfo stationInfo1 = stationInfo.clone();
//                tempList.add(stationInfo1);
//            } catch (CloneNotSupportedException e) {
//                e.printStackTrace();
//            }
//        }

        mAdapter = new BbuAdapter(App.get().getOnLineList(), this);

//        mAdapter.setSeekBarChangListener(new BbuAdapter.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(IndicatorSeekBar seekBar, int progress, boolean fromUser, StationInfo stationInfo) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(IndicatorSeekBar seekBar, StationInfo stationInfo) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(IndicatorSeekBar seekBar, StationInfo stationInfo) {
//
//            }
//        });

        recyclerView.setLayoutManager(new LinearLayoutManager(_mActivity));

        recyclerView.setAdapter(mAdapter);

        seekBar = (IndicatorSeekBar) view.findViewById(R.id.seekBar);

        //seekBar.setProgress(App.get().mShared.getInt("PowerConfig",5));
        seekBar.setProgress(5);
        seekBarProcess=seekBar.getProgress();
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                seekBarProcess=seekBar.getProgress();
                TcpManager.getInstance().setOpenDbm(seekBar.getProgress());
                //保存发射功率配置到配置文件
                //App.get().mSharedEditor.putInt("PowerConfig",seekBar.getProgress());
                //App.get().mSharedEditor.commit();


            }
        });





        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        final Button bt_lock = (Button) view.findViewById(R.id.bt_lock);
        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLock) {
                    bt_lock.setText(getString(R.string.lock1));
                    seekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                } else {
                    bt_lock.setText(R.string.unlock);
                    seekBar.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                }
                isLock = !isLock;
            }
        });

        sb_position_vibrate = (SwitchButton) view.findViewById(R.id.sb_position_vibrate);
//        boolean isPositionOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "position", false);
        sb_position_vibrate.setCheckedImmediately(false);
        sb_position_vibrate.setOnCheckedChangeListener(this);
        sb_data_vibrate = (SwitchButton) view.findViewById(R.id.sb_data_vibrate);
//        boolean isPositionOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "position", false);
        sb_data_vibrate.setCheckedImmediately(false);
        sb_data_vibrate.setOnCheckedChangeListener(this);


        sb_point_vibrate = (SwitchButton) view.findViewById(R.id.sb_point_vibrate);
//        boolean isPositionOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "position", false);
        sb_point_vibrate.setCheckedImmediately(false);
        sb_point_vibrate.setOnCheckedChangeListener(this);

        sb_point_vibrate1 = (SwitchButton) view.findViewById(R.id.sb_point_vibrate1);
//        boolean isPositionOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "position", false);
        sb_point_vibrate1.setCheckedImmediately(false);
        sb_point_vibrate1.setOnCheckedChangeListener(this);

        sb_point_vibrate2 = (SwitchButton) view.findViewById(R.id.sb_point_vibrate2);
//        boolean isPositionOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "position", false);
        sb_point_vibrate2.setCheckedImmediately(false);
        sb_point_vibrate2.setOnCheckedChangeListener(this);


        mExpandableLayout = (ExpandableLinearLayout) view.findViewById(R.id.expandableLayout);
        mRotateView = view.findViewById(R.id.expand_rotate);
        mExpandButton = (RelativeLayout) view.findViewById(R.id.expand_button);
        mExpandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(mRotateView, 0f, 180f).start();
                expandState.put(1, true);
                seekBar.setProgress(seekBarProcess);
            }

            @Override
            public void onPreClose() {
                createRotateAnimator(mRotateView, 180f, 0f).start();
                expandState.put(1, false);
                seekBar.setProgress(seekBarProcess);
            }
        });
        mRotateView.setRotation(expandState.get(1) ? 180f : 0f);
        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                boolean isAllConfig = true;
                for (StationInfo info : App.get().getMList()) {
                    if (info.getType() == 4) {
                        if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                            if (!info.isCellConfig()) {
                                isAllConfig = false;
                            }
                        }
                    }
                }
                if (isAllConfig) {
                    mExpandableLayout.toggle();
                }
            }
        });

    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    /**
     * 多选对话框
     */
    ArrayList<String> yourChoices = new ArrayList<>();
    String[] items;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean on) {
        int i = buttonView.getId();
        if (on) {
            boolean isAllConfig = true;
            for (StationInfo info : App.get().getMList()) {
                if (info.getType() != 23) {
                    if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                        if (!info.isCellConfig()) {
                            isAllConfig = false;
                            LETLog.d(info.toString());
                        }
                    }
                }
            }
            if (!isAllConfig) {
                buttonView.setChecked(false);
                ToastUtils.showToast(_mActivity, "请先等待配置完成", Toast.LENGTH_LONG);
                return;
            }
        }
        if (i == R.id.sb_data_vibrate) {
            for (StationInfo info : App.get().getMList()) {
                if (info.getType() == 4) {
                    info.setConfigDBM(true);
                }
            }
            if (on) {
                if (sb_position_vibrate.isChecked()) {
                    ToastUtils.showToast(_mActivity, "请先关闭定位", Toast.LENGTH_LONG);
                    sb_data_vibrate.setChecked(false);
                    return;
                }
                //TcpManager.getInstance().setOpenDbm(5);
                TcpManager.getInstance().setOpenDbm(seekBar.getProgress());
                TcpManager.getInstance().setGsmSetting();
                App.get().openGSM();

            } else {
                TcpManager.getInstance().setOpenDbm(3);
                TcpManager.getInstance().setGsmClose();
            }
            SharedPreferencesUtil.setConfig(_mActivity, "sniffer", "data", on);
        } else if (i == R.id.sb_position_vibrate) {
            if (on) {
                if (sb_data_vibrate.isChecked()) {
                    ToastUtils.showToast(_mActivity, "请先关闭侦码", Toast.LENGTH_LONG);
                    sb_position_vibrate.setChecked(false);
                    return;
                }
                ArrayList<String> item = new ArrayList<>();
                Log.d("App", "App :" + App.get().blackListTables.size());
                for (BlackListTable blackListTable : App.get().blackListTables) {
                    if (blackListTable.getImsi() != null) {
                        item.add(blackListTable.getImsi());
                    }
                }
                items = item.toArray(new String[item.size()]);
                if (items.length == 1) {
                    App.get().selectImsi = item;
                    showSureDialog(items[0]);
                } else {
                    if (items.length == 0) {
                        sb_position_vibrate.setChecked(false);
                        isClick = true;
                        ToastUtils.showToast(_mActivity, "请先设定黑名单", Toast.LENGTH_LONG);
                    } else {
                        showMultiChoiceDialog();
                    }
                }
            } else {
                if (!isClick) {
                    showCloseSureDialog();
                }
            }
        }else if(i == R.id.sb_point_vibrate){
            if(!sb_position_vibrate .isChecked() && !sb_data_vibrate.isChecked()){
                return;
            }
            TcpManager.getInstance().setUpDatePoint(on,sb_data_vibrate.isChecked());
        }else if(i == R.id.sb_point_vibrate1){
            if(!sb_position_vibrate .isChecked() && !sb_data_vibrate.isChecked()){
                return;
            }
            TcpManager.getInstance().setUpDatePoint1(on,sb_data_vibrate.isChecked());
        }
        else if(i == R.id.sb_point_vibrate2){
            if(!sb_position_vibrate .isChecked() && !sb_data_vibrate.isChecked()){
                return;
            }
            TcpManager.getInstance().setUpDatePoint2(on,sb_data_vibrate.isChecked());
        }
    }

    private void showMultiChoiceDialog() {
        yourChoices.clear();
        AlertDialog.Builder multiChoiceDialog = new AlertDialog.Builder(_mActivity);
        // 设置默认选中的选项，全为false默认均未选中
        final boolean initChoiceSets[] = new boolean[items.length];
        multiChoiceDialog.setTitle("请选择需要采集位置的目标IMSI");
        multiChoiceDialog.setMultiChoiceItems(items, initChoiceSets, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    yourChoices.add(items[which]);
                } else {
                    yourChoices.remove(items[which]);
                }
            }
        });

        multiChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public byte[] data;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (yourChoices.size() == 0) {
                            isClick = true;
                            sb_position_vibrate.setChecked(false);
                            return;
                        }
                        boolean isContainLianTong = false;
                        boolean isContainDianXin = false;
                        for (String yourChoice : yourChoices) {
                            if (getOpera1(yourChoice) == 2) {
                                isContainLianTong = true;
                            } else if (getOpera1(yourChoice) == 3) {
                                isContainDianXin = true;
                            }
                        }
                        if (isContainLianTong && isContainDianXin) {
                            ToastUtils.showToast(_mActivity, "目标IMSI不能同时包含电信IMSI和联通IMSI", Toast.LENGTH_LONG);
                            isClick = true;
                            sb_position_vibrate.setChecked(false);
                            return;
                        }
                        App.get().selectImsi = yourChoices;
//                        for (StationInfo stationInfo : App.get().getmList()) {
//                            if (stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && stationInfo.getType() == 4) {
                        TcpManager.getInstance().setPositionOn();
                        SharedPreferencesUtil.setConfig(_mActivity, "sniffer", "position", true);
//                                break;
//                            }
//                        }
                        for (StationInfo stationInfo : App.get().getMList()) {
                            switch (stationInfo.getType()) {
                                case 21:
                                    Log.d("setUP", "关闭 BBU8");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    break;
                                case 22:
                                    Log.d("setUP", "关闭 BBU9");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 1, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    break;
                                case 23:
                                    Log.d("setUP", "关闭 BBU10");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
                                    TcpManager.getInstance().sendCmdaUdpMsg(data);
                                    break;
                            }
                        }
                        isClick = false;
                    }
                });
        multiChoiceDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClick = true;
                sb_position_vibrate.setChecked(false);
            }
        });
        multiChoiceDialog.show();

    }

    private void showSureDialog(final String imsi) {
        mDialog5 = new SweetAlertDialog.Builder(_mActivity)
                .setTitle(R.string.open_position)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    public byte[] data;

                    @Override
                    public void onClick(Dialog dialog, int which) {
//                        for (StationInfo stationInfo : App.get().getmList()) {
//                            if (stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && stationInfo.getType() == 4) {
                        TcpManager.getInstance().setPositionOn();
//                                break;
//                            }
//                        }
                        for (StationInfo stationInfo : App.get().getMList()) {
                            switch (stationInfo.getType()) {
                                case 21:
                                    Log.d("setUP", "关闭 BBU8");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    break;
                                case 22:
                                    Log.d("setUP", "关闭 BBU9");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 1, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    break;
                                case 23:
                                    Log.d("setUP", "关闭 BBU10");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
                                    TcpManager.getInstance().sendCmdaUdpMsg(data);
                                    break;
                            }
                        }
                        isClick = false;
                    }
                })
                .setNegativeButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog5.dismiss();
                        isClick = true;
                        sb_position_vibrate.setChecked(false);
                    }
                })
                .create();
        mDialog5.show();
    }

    private void showCloseSureDialog() {
        mDialog3 = new SweetAlertDialog.Builder(_mActivity)
                .setTitle(R.string.close_position)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        TcpManager.getInstance().setPositionOFF();
                        EventBus.getDefault().post(new TargetListMessage(new TargetBean(),true));
                        SharedPreferencesUtil.setConfig(_mActivity, "sniffer", "position", false);

                    }
                })
                .setNegativeButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog3.dismiss();
                    }
                })
                .create();
        mDialog3.show();
    }

    boolean isClick;
    private boolean isLock = true;
    public boolean isClose = false;
    private StationInfo stationInfo;

    @Override
    public void onClick(View view, StationInfo stationInfo) {
        Log.d("onClick", "stationInfo :" + stationInfo);
        if (view.getId() == R.id.open_dbm) {
//            Toast.makeText(_mActivity,"手动扫频暂不可用",Toast.LENGTH_SHORT).show();
            if (stationInfo.getType() == 4) {
                if (!stationInfo.isCellConfig() || stationInfo.getConnectionStatus() != StationInfo.ConnectionStatus.CONNECTED) {
                    Toast.makeText(_mActivity, getString(R.string.scan_can_not_use), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    boolean isOpen = false;
                    for (StationInfo info : App.get().getMList()) {
                        if (info.isConfigDBM()) {
                            isOpen = true;
                        }
                    }
                    if (isOpen) {
                        showDialog(stationInfo);
                    }
                }
            } else {
                this.stationInfo = stationInfo;
                isClose = false;
                showCloseDialog();
            }

        } else if (view.getId() == R.id.close_dbm) {
            this.stationInfo = stationInfo;
            isClose = true;
            showCloseDialog();

        }
//        else if (view.getId() == R.id.carrier_restart) {
//            this.stationInfo = stationInfo;
//            showCarrierRestartDialog();
//        }
        else if (view.getId() == R.id.system_restart) {
            this.stationInfo = stationInfo;
            showSystemRestartDialog();
        } else if (view.getId() == R.id.scan) {
            this.stationInfo = stationInfo;
            showScanDialog();
        }
//        else if(view.getId() == R.id.close_dbm1){
////            showCloseDialog(stationInfo,true, (byte) 1);
//        }else if(view.getId() == R.id.carrier_restart1){
//            showCarrierRestartDialog(stationInfo);
//        }else if(view.getId() == R.id.open_dbm1){
////            showCloseDialog(stationInfo,false, (byte) 1);
//        }else if(view.getId() == R.id.scan){
//            showScanDialog(stationInfo,true);
//        }else if(view.getId() == R.id.scan1){
//            showScanDialog(stationInfo,false);
//        }
        else {
//            if(stationInfo.getType() == 4){
            Intent intent = new Intent(_mActivity, StationManagerActivity.class);
            Bundle bundle = new Bundle();
            Log.d("Station", "stationInfo onClick:" + stationInfo.toString());
            bundle.putParcelable(Constants.STATION, stationInfo);
            if (stationInfo.getScanSet() != null) {
                bundle.putIntegerArrayList(Constants.SCANSET_PCI, stationInfo.getScanSet().getPciList());
                bundle.putIntegerArrayList(Constants.EARFCH, stationInfo.getScanSet().getEarfchList());
            }
            if (stationInfo.getCellConfig() != null) {
                bundle.putIntegerArrayList(Constants.PCI, stationInfo.getCellConfig().getPciList());
                bundle.putIntegerArrayList(Constants.PLMN, stationInfo.getCellConfig().getPlmn());
                bundle.putIntegerArrayList(Constants.PILOT, stationInfo.getCellConfig().getPilot_frequency_list());
            }
            intent.putExtra(Constants.BUNDLE, bundle);
            startActivity(intent);
//            }else if(stationInfo.getType() == 4){
//                Intent intent = new Intent(_mActivity, StationManagerActivity.class);
//                Bundle bundle = new Bundle();
//                Log.d("scanSet", "stationInfo onClick:" + stationInfo.toString());
//                bundle.putParcelable(Constants.STATION, stationInfo);
//                if (stationInfo.getScanSet() != null) {
//                    bundle.putIntegerArrayList(Constants.SCANSET_PCI, stationInfo.getScanSet().getPciList());
//                    bundle.putIntegerArrayList(Constants.EARFCH, stationInfo.getScanSet().getEarfchList());
//                }
//                if (stationInfo.getCellConfig() != null) {
//                    bundle.putIntegerArrayList(Constants.PCI, stationInfo.getCellConfig().getPciList());
//                    bundle.putIntegerArrayList(Constants.PLMN, stationInfo.getCellConfig().getPlmn());
//                    bundle.putIntegerArrayList(Constants.PILOT, stationInfo.getCellConfig().getPilot_frequency_list());
//                }
//                intent.putExtra(Constants.BUNDLE, bundle);
//                startActivity(intent);
//            }
        }
    }

    @Subscribe
    public void onReStart(RestartEvent restartEvent) {
        if (restartEvent != null) {
            handler.sendEmptyMessage(UPGRADE);
        }
    }

    private void showScanDialog() {
        if (mDialog4 == null) {
            mDialog4 = new SweetAlertDialog.Builder(getActivity())
                    .setHasTwoBtn(true)
                    .setNegativeButton(R.string.cancel)
                    .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            byte[] data;
                            switch (stationInfo.getType()) {
                                case 21:
                                    Log.d("setUP", "扫描 BBU8");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 03, 0, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    break;
                                case 22:
                                    Log.d("setUP", "扫描 BBU9");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 03, 1, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    break;
                                case 23:
                                    Log.d("setUP", "扫描 BBU10");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 03, 0, 0};
                                    TcpManager.getInstance().sendCmdaUdpMsg(data);
                                    break;
                            }
                        }
                    }).create();
        }
        switch (stationInfo.getType()) {
            case 21:
                mDialog4.setMessage(getString(R.string.scan));
                break;
            case 22:
                mDialog4.setMessage(getString(R.string.scan1));
                break;
            case 23:
                mDialog4.setMessage(getString(R.string.scan2));
                break;
        }

        mDialog4.show();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void showDialog(final StationInfo stationInfo) {
        mDialog = new SweetAlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.close_dbm_tip))
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel)
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        stationInfo.setisshoudong(true);
//                        LETLog.d("stationInfo"+(stationInfo.isIsshoudong() && !stationInfo.isShoudongSend() && stationInfo.getScanResult() != null));
                    }
                }).create();
        mDialog.show();
    }

    private void showCloseDialog() {
        if (mDialog == null) {
            mDialog = new SweetAlertDialog.Builder(getActivity())
                    .setHasTwoBtn(true)
                    .setNegativeButton(R.string.cancel)
                    .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            byte[] data;
                            if (isClose) {
                                switch (stationInfo.getType()) {
                                    case 21:
                                        Log.d("setUP", "关闭 BBU8");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
                                        TcpManager.getInstance().sendGsmUdpMsg(data);
                                        break;
                                    case 22:
                                        Log.d("setUP", "关闭 BBU9");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 1, 0};
                                        TcpManager.getInstance().sendGsmUdpMsg(data);
                                        break;
                                    case 23:
                                        Log.d("setUP", "关闭 BBU10");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
                                        TcpManager.getInstance().sendCmdaUdpMsg(data);
                                        break;
                                }
                            } else {
                                switch (stationInfo.getType()) {
                                    case 21:
                                        Log.d("setUP", "打开 BBU8");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 0, 0};
                                        TcpManager.getInstance().sendGsmUdpMsg(data);
                                        break;
                                    case 22:
                                        Log.d("setUP", "打开 BBU9");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 1, 0};
                                        TcpManager.getInstance().sendGsmUdpMsg(data);
                                        break;
                                    case 23:
                                        Log.d("setUP", "打开 BBU10");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 0, 0};
                                        TcpManager.getInstance().sendCmdaUdpMsg(data);
                                        break;
                                }
                            }

                        }
                    }).create();
        }
        mDialog.setMessage(getString(!isClose ? R.string.open_dbm_tip : R.string.close_tip));
        mDialog.show();
    }

    private void showCarrierRestartDialog() {
        if (mDialog1 == null) {
            mDialog1 = new SweetAlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.carrier_restart))
                    .setHasTwoBtn(true)
                    .setNegativeButton(R.string.cancel)
                    .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            byte[] data;
                            switch (stationInfo.getType()) {
                                case 21:
                                    Log.d("setUP", "复位 BBU8");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 9, 0, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    TcpManager.getInstance().addQueryMsg();
                                    break;
                                case 22:
                                    Log.d("setUP", "复位 BBU9");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 9, 1, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    TcpManager.getInstance().addQueryMsg();
                                    break;
                                case 23:
                                    Log.d("setUP", "复位 BBU10");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 9, 0, 0};
                                    TcpManager.getInstance().sendCmdaUdpMsg(data);
                                    TcpManager.getInstance().addCmdaQueryMsg();
                                    break;
                            }

                        }
                    }).create();
        }
        mDialog1.show();
    }

    private void showSystemRestartDialog() {
        if (mDialog2 == null) {
            mDialog2 = new SweetAlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.system_restart))
                    .setHasTwoBtn(true)
                    .setNegativeButton(R.string.cancel)
                    .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                        @Override
                        public void onClick(Dialog dialog, int which) {
                            byte[] data;
                            switch (stationInfo.getType()) {
                                case 21:
                                    Log.d("setUP", "重启 BBU8");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 7, 0, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    TcpManager.getInstance().addQueryMsg();
                                    break;
                                case 22:
                                    Log.d("setUP", "重启 BBU9");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 7, 1, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    TcpManager.getInstance().addQueryMsg();
                                    break;
                                case 23:
                                    Log.d("setUP", "重启 BBU10");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 7, 0, 0};
                                    TcpManager.getInstance().sendCmdaUdpMsg(data);
                                    TcpManager.getInstance().addCmdaQueryMsg();
                                    break;
                            }
                            stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                            handler.sendEmptyMessage(UPGRADE);
                        }
                    }).create();
        }
        mDialog2.show();
    }

}
