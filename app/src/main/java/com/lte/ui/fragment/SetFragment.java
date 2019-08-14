package com.lte.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.lte.R;
import com.lte.data.CdmaConfig;
import com.lte.data.DataManager;
import com.lte.data.GsmConfig;
import com.lte.data.StationInfo;
import com.lte.data.table.SceneTable;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.activity.DeviceRegisterActivity;
import com.lte.ui.activity.DeviceTypeActivity;
import com.lte.ui.activity.HttpSetActivity;
import com.lte.ui.activity.PointSetActivity;
import com.lte.ui.activity.PoweramplifierActivity;
import com.lte.ui.activity.PoweramplifierActivity1;
import com.lte.ui.activity.RedirectActivity;
import com.lte.ui.activity.VerActivity;
import com.lte.ui.adapter.SceneDialogAdapter;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.SwitchButton;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.DateUtils;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ToastUtils;

import java.util.ArrayList;

import io.realm.RealmResults;

import static com.lte.utils.Constants.TYPE;
import static com.communication.utils.DateUtil.getOpera1;
import static com.lte.utils.DateUtils.getVersionName;

/**
 * Created by chenxiaojun on 2018/3/5.
 */

public class SetFragment extends BaseMainFragment implements View.OnClickListener, SceneDialogAdapter.CheckListener, CompoundButton.OnCheckedChangeListener {

    private TextView tv_appver;

    //    private TextView cache;
    private TitleBar titleBar;
    private SweetAlertDialog mDialog;
    private RecyclerView scene_list;
    private RealmResults<SceneTable> mSceneList;
    private SceneDialogAdapter sceneAdapter;
    private TextView delete_all;
    private SweetAlertDialog mDialog1;
    private SweetAlertDialog mDialog2;
    private SweetAlertDialog mDialog3;
    SwitchButton sb_position_vibrate;

    SwitchButton sb_white_vibrate;

    SwitchButton sb_black_vibrate;
    private SweetAlertDialog mDialog8;

    public static SetFragment newInstance() {
        SetFragment fragment = new SetFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(View view) {
        String temp = getResources().getString(R.string.copyright_ver);
//        String tabCurrentUser = String.format(temp, getVersionName(_mActivity));
        //xmd修改版本发布日期
        String ver=getVersionName(_mActivity);
        String time= DateUtils.milliToSimpleDateYear(System.currentTimeMillis());

        String tabCurrentUser = String.format(temp, time+ver.substring(10,ver.length()));

        tv_appver = (TextView) view.findViewById(R.id.tv_appver);

        tv_appver.setText(tabCurrentUser);

        RelativeLayout relativeLayout2 = (RelativeLayout) view.findViewById(R.id.layout_two);
        relativeLayout2.setOnClickListener(this);
        RelativeLayout relativeLayout1 = (RelativeLayout) view.findViewById(R.id.layout_one);
        relativeLayout1.setOnClickListener(this);
        RelativeLayout relativeLayout3 = (RelativeLayout) view.findViewById(R.id.layout_three);
        relativeLayout3.setOnClickListener(this);
        RelativeLayout relativeLayout4 = (RelativeLayout) view.findViewById(R.id.layout_four);
        relativeLayout4.setOnClickListener(this);
        RelativeLayout relativeLayout5 = (RelativeLayout) view.findViewById(R.id.layout_five);
        relativeLayout5.setOnClickListener(this);

        RelativeLayout relativeLayout8 = (RelativeLayout) view.findViewById(R.id.layout_eight);
        relativeLayout8.setOnClickListener(this);

        RelativeLayout relativeLayout9 = (RelativeLayout) view.findViewById(R.id.layout_night);
        relativeLayout9.setOnClickListener(this);

        RelativeLayout relativeLayout10 = (RelativeLayout) view.findViewById(R.id.layout_ten);
        relativeLayout10.setOnClickListener(this);

        RelativeLayout relativeLayout11 = (RelativeLayout) view.findViewById(R.id.layout_11);
        relativeLayout11.setOnClickListener(this);

        SwitchButton sbtLineControl = (SwitchButton) view.findViewById(R.id.sb_settings_ring);
        boolean isRingOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "ringOn", true);
        sbtLineControl.setCheckedImmediately(isRingOn);
        sbtLineControl.setOnCheckedChangeListener(this);

        SwitchButton sb_settings_vibrate = (SwitchButton) view.findViewById(R.id.sb_settings_vibrate);
        boolean isVibrate = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "isVibrate", true);
        sb_settings_vibrate.setCheckedImmediately(isVibrate);
        sb_settings_vibrate.setOnCheckedChangeListener(this);

//        SwitchButton sbgsmControl = (SwitchButton) view.findViewById(R.id.sb_gsm_vibrate);
//        boolean isgsmOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "gsm", true);
//        sbgsmControl.setCheckedImmediately(isgsmOn);
//        sbgsmControl.setOnCheckedChangeListener(this);
//
//        SwitchButton sb_cdma_vibrate = (SwitchButton) view.findViewById(R.id.sb_cdma_vibrate);
//        boolean isCdmaOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "cdma", true);
//        sb_cdma_vibrate.setCheckedImmediately(isCdmaOn);
//        sb_cdma_vibrate.setOnCheckedChangeListener(this);

        sb_black_vibrate = (SwitchButton) view.findViewById(R.id.sb_black_on);
        boolean isblackOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "blackOn", true);
        sb_black_vibrate.setCheckedImmediately(isblackOn);
        sb_black_vibrate.setOnCheckedChangeListener(this);

        sb_white_vibrate = (SwitchButton) view.findViewById(R.id.sb_white_on);
        boolean iswhiteOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "whiteOn", false);
        sb_white_vibrate.setCheckedImmediately(iswhiteOn);
        sb_white_vibrate.setOnCheckedChangeListener(this);

//        sb_position_vibrate = (SwitchButton) view.findViewById(R.id.sb_position_vibrate);
////        boolean isPositionOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "position", false);
//        sb_position_vibrate.setCheckedImmediately(false);
//        sb_position_vibrate.setOnCheckedChangeListener(this);

        initClearDialog();
    }
    boolean isClick;
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean on) {
        int i = buttonView.getId();
        if (i == R.id.sb_settings_ring) {
            SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "ringOn", on);
        } else if (i == R.id.sb_settings_vibrate) {
            SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "isVibrate", on);
//        }
//        else if (i == R.id.sb_gsm_vibrate) {
//            SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "gsm", on);
//        }
//        else if (i == R.id.sb_cdma_vibrate) {
//            SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "cdma", on);
//        } else if (i == R.id.sb_position_vibrate) {
//            if (on) {
//                ArrayList<String> item = new ArrayList<>();
//                for (BlackListTable blackListTable : App.get().blackListTables) {
//                    if(blackListTable.getImsi() != null){
//                        item.add(blackListTable.getImsi());
//                    }
//                }
//                items =  item.toArray(new String[item.size()]);
//                if (items.length == 1) {
//                    App.get().selectImsi = item;
//                    showSureDialog(items[0]);
//                } else {
//                    if (items.length == 0) {
//                        sb_position_vibrate.setChecked(false);
//                        isClick = true;
//                        ToastUtils.showToast(_mActivity, "请先设定黑名单", Toast.LENGTH_LONG);
//                    } else {
//                        showMultiChoiceDialog();
//                    }
//                }
//            } else {
//                if(!isClick){
//                    showCloseSureDialog();
//                }
//            }
        }else if(i == R.id.sb_black_on){
//            if(on){
//                if(sb_white_vibrate.isChecked()){
//                    sb_white_vibrate.setChecked(false);
//                    SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "whiteOn", false);
//                }
//            }
            App.get().isBlcakOn = on;
            SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "blackOn", on);
        }else if(i == R.id.sb_white_on){
//            if(on){
//                if(sb_black_vibrate.isChecked()){
//                    sb_black_vibrate.setChecked(false);
//                    SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "blackOn", false);
//                }
//            }
            App.get().isWhitOn = on;
            SharedPreferencesUtil.setConfig(getActivity(), "sniffer", "whiteOn", on);
        }
    }
    /**
     * 多选对话框
     */
    ArrayList<String> yourChoices = new ArrayList<>();
    String[] items;

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

        multiChoiceDialog.setPositiveButton(getString(R.string.sure),
                new DialogInterface.OnClickListener() {
                    public byte[] data;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(yourChoices.size() == 0){
                            isClick = true;
                            sb_position_vibrate.setChecked(false);
                            return;
                        }
                        boolean isContainLianTong = false;
                        boolean isContainDianXin = false;
                        for (String yourChoice : yourChoices) {
                            if(getOpera1(yourChoice) ==2){
                                isContainLianTong = true;
                            }else if(getOpera1(yourChoice) ==3){
                                isContainDianXin = true;
                            }
                        }
                        if(isContainLianTong && isContainDianXin){
                            ToastUtils.showToast(_mActivity, "目标IMSI不能同时包含电信IMSI和联通IMSI", Toast.LENGTH_LONG);
                            isClick = true;
                            sb_position_vibrate.setChecked(false);
                            return;
                        }
                        App.get().selectImsi = yourChoices;
//                        for (StationInfo stationInfo : App.get().getmList()) {
//                            if(stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && stationInfo.getType() ==4){
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
        mDialog2 = new SweetAlertDialog.Builder(_mActivity)
                .setTitle( R.string.open_position )
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    public byte[] data;

                    @Override
                    public void onClick(Dialog dialog, int which) {
//                        for (StationInfo stationInfo : App.get().getmList()) {
//                            if(stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && stationInfo.getType() == 4){
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
                        mDialog2.dismiss();
                        isClick = true;
                        sb_position_vibrate.setChecked(false);
                    }
                })
                .create();
        mDialog2.show();
    }

    private void showCloseSureDialog() {
        mDialog3 = new SweetAlertDialog.Builder(_mActivity)
                .setTitle(R.string.close_position)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        TcpManager.getInstance().setPositionOFF();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_one: {
                Intent intent = new Intent(_mActivity, HttpSetActivity.class);
                intent.putExtra(TYPE, 1);
                startActivity(intent);
                break;
            }
            case R.id.layout_two: {
                Intent intent = new Intent(_mActivity, VerActivity.class);
                intent.putExtra(TYPE, 2);
                startActivity(intent);
                break;
            }
            case R.id.layout_three:
                mDialog.show();
                break;
            case R.id.layout_four: {
                Intent intent = new Intent(_mActivity, DeviceRegisterActivity.class);
                intent.putExtra(TYPE, 4);
                startActivity(intent);
                break;
            }
            case R.id.layout_five: {
                Intent intent = new Intent(_mActivity, DeviceTypeActivity.class);
                intent.putExtra(TYPE, 5);
                startActivity(intent);
                break;
            }
            case R.id.layout_eight: {
                showUpdateSureDialog();
                break;
            }
            case R.id.layout_night: {
                Intent intent = new Intent(_mActivity, PoweramplifierActivity1.class);
                intent.putExtra(TYPE, 6);
                startActivity(intent);
                break;
            }
            case R.id.layout_ten: {
                Intent intent = new Intent(_mActivity, PointSetActivity.class);
                intent.putExtra(TYPE, 10);
                startActivity(intent);
                break;
            }
            case R.id.layout_11: {
                Intent intent = new Intent(_mActivity, RedirectActivity.class);
                intent.putExtra(TYPE, 11);
                startActivity(intent);
                break;
            }
        }
    }

    private void initClearDialog() {
        mDialog = new SweetAlertDialog.Builder(_mActivity)
                .setTitle(R.string.delete_scene)
                .setHasTwoBtn(false)
                .setOneButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.addContentView(R.layout.dialog_delete_scene_list);
        scene_list = (RecyclerView) mDialog.findView(R.id.scene_list);
        delete_all = (TextView) mDialog.findView(R.id.delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                showDeleteSureDialog(null, getString(R.string.delete_all), true);
            }
        });
        mSceneList = DataManager.getInstance().findSceneList();
        sceneAdapter = new SceneDialogAdapter(_mActivity, mSceneList, this, scene_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);
        scene_list.setLayoutManager(linearLayoutManager);
        scene_list.setAdapter(sceneAdapter);
    }

    private void showDeleteSureDialog(final SceneTable sceneTable, String str, final boolean isDeleteAll) {
        mDialog1 = new SweetAlertDialog.Builder(_mActivity)
                .setMessage(str)
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog1.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (isDeleteAll) {
                            DataManager.getInstance().removeAll();
                        } else {
                            DataManager.getInstance().deleteScene(sceneTable);
                        }
                        App.get().initData();
                    }
                })
                .create();
        mDialog1.show();
    }
    private void showUpdateSureDialog() {
        mDialog8 = new SweetAlertDialog.Builder(_mActivity)
                .setMessage(getString(R.string.init_update))
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog8.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        for (StationInfo stationInfo : App.get().getMList()) {
                            if(stationInfo.getType() == 4){
                                stationInfo.setIp(stationInfo.getIp(),true);
                                DataManager.getInstance().createOrUpdateStation(stationInfo);
                            }else if (stationInfo.getType() == 21 || stationInfo.getType() == 22){
                                GsmConfig gsmConfig = new GsmConfig();
                                gsmConfig.id = 1l;
                                gsmConfig.setCMD();
                                DataManager.getInstance().crateOrUpdate(gsmConfig);
                            }else if(stationInfo.getType() == 23){
                                CdmaConfig cdmaConfig = new CdmaConfig();
                                cdmaConfig.id = 1l;
                                cdmaConfig.setCMD();
                                DataManager.getInstance().crateOrUpdate(cdmaConfig);
                            }
                        }
                    }
                })
                .create();
        mDialog8.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view, SceneTable imsiData) {
        showDeleteSureDialog(imsiData, String.format(getString(R.string.delete_scene_sure), imsiData.getName()), false);
        mDialog.dismiss();
    }
}

