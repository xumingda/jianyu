package com.lte.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.communication.request.RequestHelper;
import com.communication.tcp.TcpService;
import com.communication.utils.LETLog;
import com.google.gson.JsonElement;
import com.jzxiang.pickerview.TimePickerDialog;
import com.lte.R;
import com.lte.data.CdmaConfig;
import com.lte.data.DataManager;
import com.lte.data.GsmConfig;
import com.lte.data.ImsiData;
import com.lte.data.StationInfo;
import com.lte.data.table.BlackListTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.SceneTable;
import com.lte.data.table.WhiteListTable;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.adapter.DataReportOneAdapter;
import com.lte.ui.adapter.SceneDialogAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.event.RestartEvent;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleCheckView;
import com.lte.ui.widget.menu.MenuAttribute;
import com.lte.ui.widget.menu.MenuItemView;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.DateUtils;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.RealmResults;

import static com.lte.utils.AppUtils.copy;
import static com.lte.utils.AppUtils.dataParams;
import static com.lte.utils.AppUtils.dip2px;
import static com.communication.utils.DateUtil.formatTime;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * Created by chenxiaojun on 2018/1/4.
 */

public class FirstFragment extends BaseMainFragment implements View.OnClickListener, DataReportOneAdapter.CheckListener, SceneDialogAdapter.CheckListener {

    private TimePickerDialog mDialogAll;

    private static final int START_SEND = 1;

    private static final int PLAY_SOUND = 2;

    private Button bt_save;
    private Button bt_clear;
    private Button bt_start2;

    private Button bt_lock;

    protected long mBeginMillseconds = 0L;
    protected long mEndMillseconds = 0L;

    protected static final String BEGIN_TIME_TAG = "begin_time_tag";
    protected static final String END_TIME_TAG = "end_time_tag";

    protected TitleCheckView mBeginTimeTCV;
    protected TitleCheckView mEndTimeTCV;

    private SweetAlertDialog mDialog;

    private RecyclerView imsi_list;

    private ArrayList<ImsiData> mList = new ArrayList<>();

    private ArrayList<ImsiData> mSendList = new ArrayList<>();

    private DataReportOneAdapter mAdapter;

    private Vibrator vibrator;

    private int loadId;

    private TextView tv_scene;

    private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
    private EditText et_scene_name;

    private RealmResults<SceneTable> mSceneList;
    private Button btn_select_cancel;
    private AlertDialog sceneDialog;
    private RecyclerView scene_List;
    private SceneDialogAdapter sceneAdapter;
    private SweetAlertDialog mDialog1;
    private Button bt_start;
    private SweetAlertDialog mDialog2;
    private Button bt_restart;
    private SweetAlertDialog mDialog3;
    private PopupWindow popupWindow;

    private Long startTime;
    private CdmaConfig cdmaConfig;
    private GsmConfig gsmConfig;
    private boolean isLock;

    public FirstFragment() {
    }

    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    private HttpCallback callBack = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + "imsi ：上传 " + jsonObject.toString());
            if (jsonObject.toString().equals("0")) {
                synchronized (mList) {
                    if (mList.size() > 0) {
                        mList.removeAll(mSendList);
                        mSendList.clear();
                    }
                }

            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SEND:
                    synchronized (mList) {
                        if (mSendList.size() > 0) {
                            HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber), dataParams(mSendList), callBack);
                            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + "imsi ：上传 url" + Constants.getDataUrl(App.get().DevNumber) + "dataParams :" +
                                    dataParams(mSendList));
                        } else {
                            if (mList.size() > 0) {
                                for (ImsiData imsiData : mList) {
                                    imsiData.setReport(true);
                                    mSendList.add(imsiData);
                                }
                                if (mSendList.size() != 0) {
                                    if (App.get().DevNumber != null) {
                                        HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber), dataParams(mSendList), callBack);
                                        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + "imsi ：上传 url" + Constants.getDataUrl(App.get().DevNumber) + "dataParams :" +
                                                dataParams(mSendList));
                                    }
                                }
                            }
                        }
                        sendEmptyMessageDelayed(START_SEND, 3 * 1000L);
                        break;
                    }
                case PLAY_SOUND:
                    boolean isRingOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "ringOn", true);
                    if (isRingOn && soundPool != null) {
                        soundPool.play(loadId, 1, 1, 1, 0, 1f);//参数：1、M取值 2、当前音量 3、最大音量 4、优先级 5、重播次数 6、播放速度
                    }
                    boolean isVibrate = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "isVibrate", true);
                    if (vibrator != null && isVibrate) {
                        vibrator.vibrate(1000);//振动1秒
                    }
                    break;
                case 3:
                    TcpManager.getInstance().sendGsmUdpMsg(gsmConfig.cmd1);
                    break;
                case 4:
                    TcpManager.getInstance().sendGsmUdpMsg(gsmConfig.cmd2);
                    break;
            }

        }
    };
    boolean start = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_report_fragment1, container, false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(View view) {

        bt_save = (Button) view.findViewById(R.id.bt_save);

        bt_clear = (Button) view.findViewById(R.id.bt_clear);

        bt_lock = (Button) view.findViewById(R.id.bt_lock);

        bt_start = (Button) view.findViewById(R.id.bt_start);

        if (start) {
            bt_start.setText(R.string.stop);
        }
        bt_start.setOnClickListener(this);
        bt_restart = (Button) view.findViewById(R.id.bt_restart);

        bt_lock.setOnClickListener(this);
        bt_restart.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        EventBus.getDefault().register(this);
        imsi_list = (RecyclerView) view.findViewById(R.id.imsi_list);
        if (App.get().userInfo.getImsiStartTime() != 0) {
            mBeginMillseconds = App.get().userInfo.getImsiStartTime();
        } else {
            mBeginMillseconds = System.currentTimeMillis();
            App.get().userInfo.setImsiStartTime(mBeginMillseconds);
            DataManager.getInstance().crateOrUpdate(App.get().userInfo);
        }
        mSceneList = DataManager.getInstance().findSceneList();
        mAdapter = new DataReportOneAdapter(_mActivity, DataManager.getInstance().findImsiData(App.get().userInfo.getImsipreClearTime()), this, imsi_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);
        imsi_list.setLayoutManager(linearLayoutManager);
        imsi_list.setAdapter(mAdapter);
        loadId = soundPool.load(_mActivity, R.raw.beep, 1);
        // 震动效果的系统服务
        vibrator = (Vibrator) _mActivity.getSystemService(Activity.VIBRATOR_SERVICE);
        handler.sendEmptyMessage(START_SEND);
        initEditDialog();
    }

    private void showEditDialog() {
        mDialog.show();
    }

    private void initEditDialog() {
        mDialog = new SweetAlertDialog.Builder(_mActivity)

                .setMessage(getString(R.string.set_scene_name))
                .setHasTwoBtn(true)
                .setCancelable(false)
                .setOnRightNotDismiss(true)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
//                        if (type == 1) {
                        if (!TextUtils.isEmpty(et_scene_name.getText())) {
                            boolean isNameContains = false;//名字是否冲突标记
                            Long mBeginTime = null;//冲突的时间
                            Long mEndTime = null;//冲突的时间
                            String name = null;//冲突的场景名；
                            for (SceneTable sceneTable : mSceneList) {//遍历已保存场景，如果新添加场景与已保存场景名字冲突，提示用户重新输入
                                if (TextUtils.equals(sceneTable.getName(), et_scene_name.getText().toString())) {
                                    isNameContains = true;//名字冲突，跳出循环
                                    break;
                                }
                            }
                            if (isNameContains) {
                                AppUtils.showToast(_mActivity, getString(R.string.same_name_tips));
                                et_scene_name.setText("");
                                et_scene_name.setHint(getString(R.string.scene_name));
                                return;
                            }
                            DataManager.getInstance().createOrUpdateScene(et_scene_name.getText().toString(), mBeginMillseconds, mEndMillseconds);
                            CommonToast.show(_mActivity, R.string.save_success);
                            mBeginMillseconds = mEndMillseconds;
                            App.get().userInfo.setImsiStartTime(mBeginMillseconds);
                            DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                        }
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.addContentView(R.layout.scene_dialog);
        et_scene_name = (EditText) mDialog.findView(R.id.et_scene_name);
    }

    SweetAlertDialog sweetAlertDialog;

    private void showSameTimeDialog(String name, final Long id, final int type1) {
        sweetAlertDialog = new SweetAlertDialog.Builder(_mActivity)

                .setMessage(name)
                .setHasTwoBtn(true)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (type1 == 1) {
                            DataManager.getInstance().createOrUpdateScene(mBeginMillseconds, mEndMillseconds, id);
                            AppUtils.showToast(_mActivity, getString(R.string.upgrade_success));
                        } else if (type1 == 2) {
                            type = 2;
                            showEditDialog();
                        }
                        sweetAlertDialog.dismiss();
                    }
                })
                .create();
        sweetAlertDialog.show();
    }

    @Subscribe
    public void onStateUpdate(MessageEvent event) {
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("First", "onResume");
    }

    private void showClearDialog() {
        mDialog1 = new SweetAlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.clear_tip))
                .setHasTwoBtn(true)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        App.get().userInfo.setImsipreClearTime(System.currentTimeMillis());
                        App.get().userInfo.setImsiType(2);
                        DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                        LETLog.d(formatTime(System.currentTimeMillis()));
                        DataManager.getInstance().remove(System.currentTimeMillis());
                        upgrade(System.currentTimeMillis());
                    }
                })
                .create();
        mDialog1.show();
    }

    public void upgrade(Long time) {
        mAdapter.upDataList(DataManager.getInstance().findImsiData(time));
    }









    /**
     * 接收数据上报
     */
    @Subscribe
    public void onUpdate(MessageEvent event) {
//        LETLog.d("onUpdate "+(event != null && event.imsiData != null) + " --"+(App.get().DevNumber != null));
        if (event != null && event.imsiData != null) {
            boolean isWhiteContain = false;
            if (App.get().isBlcakOn) {
                Log.d("App", App.get().blackListTables.size() + "");
                for (BlackListTable blackListTable : App.get().blackListTables) {
                    if (TextUtils.equals(blackListTable.getImsi(), event.imsiData.getImsi())) {
                        event.imsiData.setIsblackList(1);
                        handler.sendEmptyMessage(PLAY_SOUND);
                        break;
                    } else if (blackListTable.getImei() != null && TextUtils.equals(blackListTable.getImei(), event.imsiData.getImei())) {
                        event.imsiData.setIsblackList(1);
                        handler.sendEmptyMessage(PLAY_SOUND);
                        break;
                    }
                }
//                if(!App.get().isWhitOn){
//                    App.get().insert(event.imsiData);
//                }
            }

            Log.d("App", App.get().whiteListTables.size() + "");
            for (WhiteListTable whiteListTable : App.get().whiteListTables) {
                if (TextUtils.equals(whiteListTable.getImsi(), event.imsiData.getImsi())) {
                    isWhiteContain = true;
                    break;
                }
                if (event.imsiData.getImei() != null && whiteListTable.getImei() != null && TextUtils.equals(whiteListTable.getImei(), event.imsiData.getImei())) {
                    isWhiteContain = true;
                    break;
                }
            }
            if (isWhiteContain) {
                if(!App.get().isWhitOn) {
                    event.imsiData.setIsblackList(2);
                }
            }

            if(!(isWhiteContain&&App.get().isWhitOn)){
                App.get().insert(event.imsiData);
                if(App.get().DevNumber != null && App.get().userInfo.getUrl() != null && App.get().userInfo.getImsiPort() != null){
                    mList.add(event.imsiData);//存储所有上报的数据，相当于一个队列，上传服务器后台后，清除上传完成的数据
                }
            }

        }
    }

//    public void onUpdate(MessageEvent event) {
////        LETLog.d("onUpdate "+(event != null && event.imsiData != null) + " --"+(App.get().DevNumber != null));
//        if (event != null && event.imsiData != null) {
//            boolean isWhiteContain = false;
//            if (App.get().isBlcakOn) {
//                Log.d("App", App.get().blackListTables.size() + "");
//                for (BlackListTable blackListTable : App.get().blackListTables) {
//                    if (TextUtils.equals(blackListTable.getImsi(), event.imsiData.getImsi())) {
//                        event.imsiData.setIsblackList(1);
//                        handler.sendEmptyMessage(PLAY_SOUND);
//                        break;
//                    } else if (blackListTable.getImei() != null && TextUtils.equals(blackListTable.getImei(), event.imsiData.getImei())) {
//                        event.imsiData.setIsblackList(1);
//                        handler.sendEmptyMessage(PLAY_SOUND);
//                        break;
//                    }
//                }
//                if(!App.get().isWhitOn){
//                    App.get().insert(event.imsiData);
//                }
//            }
//
//            if (App.get().isWhitOn) {
//                Log.d("App", App.get().whiteListTables.size() + "");
//                for (WhiteListTable whiteListTable : App.get().whiteListTables) {
//                    if (TextUtils.equals(whiteListTable.getImsi(), event.imsiData.getImsi())) {
//                        isWhiteContain = true;
//                        break;
//                    }
//                    if (event.imsiData.getImei() != null && whiteListTable.getImei() != null && TextUtils.equals(whiteListTable.getImei(), event.imsiData.getImei())) {
//                        isWhiteContain = true;
//                        break;
//                    }
//                }
//                if (isWhiteContain) {
//                    event.imsiData.setIsblackList(2);
//                } else {
//                    App.get().insert(event.imsiData);
//                }
//            }
//
//            if (!App.get().isWhitOn && !App.get().isBlcakOn) {
//                App.get().insert(event.imsiData);
//            }
//
//            if (!isWhiteContain && App.get().DevNumber != null && App.get().userInfo.getUrl() != null && App.get().userInfo.getImsiPort() != null) {
//                mList.add(event.imsiData);//存储所有上报的数据，相当于一个队列，上传服务器后台后，清除上传完成的数据
//            }
//        }
//    }

    TcpService tcpService;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
//                EventBus.getDefault().post(new MessageEvent(null,"false",null));
//                TcpManager.getInstance().testAdd4g();
//                tcpService = new TcpService("192.168.100.15", 5000);
//                LETLog.d("TcpService " + "ip :" + "192.168.100.15");
//                RequestHelper.getInstance().createSocket(tcpService);


                mEndMillseconds = System.currentTimeMillis();//没有选择结束时间，结束时间默认为当前时间
                showEditDialog();
                break;
            case R.id.bt_clear:
                showClearDialog();
                break;
            case R.id.bt_scene:
                sceneAdapter.upDataList(DataManager.getInstance().findSceneList());
                sceneDialog.show();
                break;
            case R.id.begin_time_input_value:
                mDialogAll.show(getFragmentManager(), BEGIN_TIME_TAG);
                break;
            case R.id.end_time_input_value:
                mDialogAll.show(getFragmentManager(), END_TIME_TAG);
                break;
            case R.id.tv_scene:
                if (ID == 0) {
                    type = 0;
                    showEditDialog();
                } else {
                    type = 2;
                    showSameTimeDialog(String.format(getString(R.string.upgrade_name), tv_scene.getText().toString()), 0L, 2);
                }
                break;
            case R.id.bt_start:
//                TcpManager.getInstance().testAdd4g();
                boolean isCellNotconfig = false;
                for (StationInfo stationInfo : App.get().getMList()) {
                    if (stationInfo.getType() == 4) {
                        if (!stationInfo.isCellConfig() && stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                            isCellNotconfig = true;
                        }
                    }
                }
                if (isCellNotconfig || App.get().getMList().size() == 0) {
                    return;
                }
                showStartDialog(start);
                break;
            case R.id.bt_restart:
//                TcpManager.getInstance().upDate("34545345543454355",7);
                showRestartDialog();
                break;
            case R.id.bt_lock:
                if (isLock) {
                    mAdapter.upDataList(DataManager.getInstance().findImsiData(App.get().userInfo.getImsipreClearTime()));
                    bt_lock.setText(getString(R.string.lock));
                } else {
                    mAdapter.upDataList(DataManager.getInstance().findImsiData(App.get().userInfo.getImsipreClearTime(), System.currentTimeMillis()));
                    bt_lock.setText(getString(R.string.release));
                }
                isLock = !isLock;
                break;
        }
    }

    private void showRestartDialog() {
        mDialog3 = new SweetAlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.restart_tip))
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog3.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        for (StationInfo stationInfo : App.get().getMList()) {
                            byte[] data;
                            switch (stationInfo.getType()) {
                                case 21:
                                    Log.d("setUP", "重启 BBU8");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 7, 0, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    TcpManager.getInstance().addQueryMsg();
                                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                    break;
                                case 22:
                                    Log.d("setUP", "重启 BBU9");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 7, 1, 0};
                                    TcpManager.getInstance().sendGsmUdpMsg(data);
                                    TcpManager.getInstance().addQueryMsg();
                                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                    break;
                                case 23:
                                    Log.d("setUP", "重启 BBU10");
                                    data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 7, 0, 0};
                                    TcpManager.getInstance().sendCmdaUdpMsg(data);
                                    TcpManager.getInstance().addCmdaQueryMsg();
                                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                    break;
                                case 4:
                                    TcpManager.getInstance().setRestart();
                                    break;
                            }
                        }
                    }
                })
                .create();
        mDialog3.show();
    }

    private void showStartDialog(final boolean isStart) {
        mDialog2 = new SweetAlertDialog.Builder(getActivity())
                .setMessage(getString(isStart ? R.string.stop_tip : R.string.start_tip))
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog2.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    public byte[] data;

                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (isStart) {
                            bt_start.setText(getString(R.string.start1));
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
                                    case 4:
                                        TcpManager.getInstance().setCloseDbm(stationInfo);
                                        break;
                                }
                            }
                        } else {
                            bt_start.setText(getString(R.string.stop));
                            for (StationInfo stationInfo : App.get().getMList()) {
                                switch (stationInfo.getType()) {
                                    case 21:
                                        Log.d("setUP", "打开 BBU8");
//                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 0, 0};
                                        gsmConfig = DataManager.getInstance().findGsmConfigFrist();
                                        gsmConfig.LAC1 = (Integer.parseInt(gsmConfig.LAC1) + 1) + "";
                                        gsmConfig.setCMD();
                                        handler.sendEmptyMessageDelayed(3, 2000L);
//                                        TcpManager.getInstance().sendGsmUdpMsg(data);
                                        break;
                                    case 22:
                                        Log.d("setUP", "打开 BBU9");
                                        gsmConfig = DataManager.getInstance().findGsmConfigFrist();
                                        gsmConfig.LAC2 = (Integer.parseInt(gsmConfig.LAC2) + 1) + "";
                                        gsmConfig.setCMD();
                                        handler.sendEmptyMessageDelayed(4, 2000L);
//                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 1, 0};
//                                        gsmConfig = DataManager.getInstance().findGsmConfigFrist();
//                                        TcpManager.getInstance().sendGsmUdpMsg(data);
                                        break;
                                    case 23:
                                        Log.d("setUP", "打开 BBU10");
                                        data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 0, 0};
                                        TcpManager.getInstance().sendCmdaUdpMsg(data);
                                        break;
                                    case 4:
                                        TcpManager.getInstance().setOpenDam(stationInfo);
                                        break;
                                }
                            }
                        }
                        FirstFragment.this.start = !FirstFragment.this.start;
                    }
                })
                .create();
        mDialog2.show();
    }

    private int type = 0;
    private Long ID = 0L;

    @Subscribe
    public void onReStart(RestartEvent restartEvent) {
        if (restartEvent != null) {
            ToastUtils.showToast(_mActivity, "正在重启系统，请稍后", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(final View view, final ImsiDataTable imsiData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(_mActivity.getColor(R.color.colorAccent));
        } else {
            view.setBackgroundColor(_mActivity.getResources().getColor(R.color.colorAccent));
        }
        if (imsiData.getIsBlackAndWhite() == 0) {
            MenuItemView menu = new MenuItemView(_mActivity) {
                @Override
                protected MenuAttribute initAttribute() {
                    MenuAttribute attribute = new MenuAttribute();
                    attribute.imsiDataTable = imsiData;
                    attribute.type = 1;
                    return attribute;
                }
            };
            menu.showMenu(false, null);
            menu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    view.setBackground(null);
                }
            });
        } else if (imsiData.getIsBlackAndWhite() == 2) {
            MenuItemView menu = new MenuItemView(_mActivity) {
                @Override
                protected MenuAttribute initAttribute() {
                    MenuAttribute attribute = new MenuAttribute();
                    attribute.imsiDataTable = imsiData;
                    attribute.type = 5;
                    return attribute;
                }
            };
            menu.showMenu(false, null);
            menu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    view.setBackground(null);
                }
            });
        } else {
            MenuItemView menu = new MenuItemView(_mActivity) {
                @Override
                protected MenuAttribute initAttribute() {
                    MenuAttribute attribute = new MenuAttribute();
                    attribute.imsiDataTable = imsiData;
                    attribute.type = 4;
                    return attribute;
                }
            };
            menu.showMenu(false, null);
            menu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    view.setBackground(null);
                }
            });
        }
    }

    @Override
    public void onLongClick(View view, ImsiDataTable imsiData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(_mActivity.getColor(R.color.red));
        } else {
            view.setBackgroundColor(_mActivity.getResources().getColor(R.color.red));
        }
        String text = null;
        switch (view.getId()) {
            case R.id.imsi1:
                Log.d("onLongClick", "imsiData :1" + imsiData.toString());
                text = imsiData.getImsi();
                break;
            case R.id.imei1:
                text = imsiData.getImei();
                Log.d("onLongClick", "imsiData :2" + imsiData.toString());
                break;
            case R.id.mobile:
                text = imsiData.getMobile();
                Log.d("onLongClick", "imsiData :3" + imsiData.toString());
                break;

        }
        getCopy(view, text);
    }

    /**
     * popuwindow 复制功能框
     */
    private void getCopy(final View v, final String text) {
        View popupWindowView = LayoutInflater
                .from(_mActivity.getApplicationContext()).inflate(
                        R.layout.popupwindow_copy, null);
        popupWindowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                copy(text, getApplicationContext());
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                ToastUtils.showToast(_mActivity, getString(R.string.copy_success), 1);
            }
        });
        // 播放动画有一个前提 就是窗体必须有背景
        popupWindow = new PopupWindow(popupWindowView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // popuWindow.setFocusable(true);//PopuWindow获得焦点，点击外面消失才会消失
        popupWindow.setOutsideTouchable(true);// 不能在没有焦点的时候使用

        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 获取控件Editext在手机中的位置，并设置popupwindow位置
        // 数组长度必须为2
        int[] locations = new int[2];

        v.getLocationInWindow(locations);
        // v.getLocationOnScreen(locations);
        int x = locations[0];// 获取组件当前位置的横坐标
        int y = locations[1];// 获取组件当前位置的纵坐标

        // 根据手机手机的分辨率 把200dip 转化成 不同的值 px

        int px = dip2px(getApplicationContext(), x / 2);

        popupWindow.showAtLocation(v, Gravity.TOP + Gravity.LEFT, px, y + 100);

        // 设置动画效果
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(200);
        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(200);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(sa);
        set.addAnimation(aa);
        popupWindowView.startAnimation(set);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                v.setBackground(null);
            }
        });
    }


    @Override
    public void onClick(View view, SceneTable sceneTable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(_mActivity.getColor(R.color.color_yellow));
        } else {
            view.setBackgroundColor(_mActivity.getResources().getColor(R.color.color_yellow));
        }
        if (sceneTable.getId() == 1) {
            type = 0;
            showEditDialog();
        } else {
            ID = sceneTable.getId();
            mBeginTimeTCV.setCheckText(formatTime(sceneTable.getmBeginMillseconds()));
            mEndTimeTCV.setCheckText(formatTime(sceneTable.getmEndMillseconds()));
            tv_scene.setText(sceneTable.getName());
            App.get().userInfo.setImsiStartTime(sceneTable.getmBeginMillseconds());
            App.get().userInfo.setImsiendTime(sceneTable.getmEndMillseconds());
            App.get().userInfo.setImsiType(0);
            App.get().userInfo.setSceneName(sceneTable.getName());
            DataManager.getInstance().crateOrUpdate(App.get().userInfo);
        }
        sceneDialog.dismiss();
    }
}
