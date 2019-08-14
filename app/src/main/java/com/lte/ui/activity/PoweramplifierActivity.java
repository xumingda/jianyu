package com.lte.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.communication.request.RequestHelper;
import com.communication.tcp.TcpMsgCallback;
import com.communication.tcp.TcpService;
import com.communication.utils.LETLog;
import com.lte.R;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.SwitchButton;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.CrcUtil;
import com.lte.utils.ViewUtil;

/**
 * Created by chenxiaojun on 2018/4/2.
 */

public class PoweramplifierActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private SwitchButton sb_settings_vibrate1;//功放开关

    private SwitchButton sb_settings_vibrate3;//功放开关

    private SwitchButton sb_settings_vibrate38;//功放开关

    private SwitchButton sb_settings_vibrate39;//功放开关

    private SwitchButton sb_settings_vibrate40;//功放开关

    private SwitchButton sb_scan_vibrate;//扫频开关

    private Button query1;//查询开关

    private Button query3;//查询开关

    private Button query38;//查询开关

    private Button query39;//查询开关

    private Button query40;//查询开关

    private Button query_scan;//查询开关

    private TextView state1;//开关状态

    private TextView state3;//开关状态

    private TextView state38;//开关状态

    private TextView state39;//开关状态

    private TextView state40;//开关状态

    private TextView state_scan;//开关状态

    private Button query_tem;//功放温度

    private TextView tem;//功放温度

    private Button query_ver;//功放版本

    private TextView ver;//功放版本

    private Button set_address;//设置模块地址

    private Button query_address;//查询模块地址

    private TextView address;//模块地址

    private Button set_b1att;//设置Att

    private Button set_b3att;//设置Att

    private Button set_b38att;//设置Att

    private Button set_b39att;//设置Att

    private Button set_b40att;//设置Att

    private Button query_b1att;//查询ATT

    private Button query_b3att;//查询ATT

    private Button query_b38att;//查询ATT

    private Button query_b39att;//查询ATT

    private Button query_b40att;//查询ATT

    private TextView b1att;//上行Att

    private TextView b3att;//上行Att

    private TextView b38att;//上行Att

    private TextView b39att;//上行Att

    private TextView b40att;//上行Att

    private Button query_b1power;//功率检测

    private Button query_b3power;//功率检测

    private Button query_b38power;//功率检测

    private Button query_b39power;//功率检测

    private Button query_b40power;//功率检测

    private TextView b1power;//下行功率

    private TextView b3power;//下行功率

    private TextView b38power;//下行功率

    private TextView b39power;//下行功率

    private TextView b40power;//下行功率

    private Button query_alarm;//告警状态

    private TextView alarm;

    private Button set_relese;

    private TcpService tcpService;
    private TitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power_amplifier_activity);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String ip = AppUtils.intToIp(AppUtils.getWifiGateIP(this));
        tcpService = new TcpService(ip, 5000);
        LETLog.d("TcpService " + "ip :" + ip);
        RequestHelper.getInstance().createSocket(tcpService);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RequestHelper.getInstance().releaseSocket(tcpService);
    }

    private void setListener() {
        sb_settings_vibrate1.setOnCheckedChangeListener(this);
        sb_settings_vibrate3.setOnCheckedChangeListener(this);
        sb_settings_vibrate38.setOnCheckedChangeListener(this);
        sb_settings_vibrate39.setOnCheckedChangeListener(this);
        sb_settings_vibrate40.setOnCheckedChangeListener(this);
        sb_scan_vibrate.setOnCheckedChangeListener(this);

        query1.setOnClickListener(this);
        query3.setOnClickListener(this);
        query38.setOnClickListener(this);
        query39.setOnClickListener(this);
        query40.setOnClickListener(this);
        query_scan.setOnClickListener(this);

        query_tem.setOnClickListener(this);
        query_ver.setOnClickListener(this);
        set_address.setOnClickListener(this);
        query_address.setOnClickListener(this);

        set_b1att.setOnClickListener(this);
        set_b3att.setOnClickListener(this);
        set_b38att.setOnClickListener(this);
        set_b39att.setOnClickListener(this);
        set_b40att.setOnClickListener(this);

        query_b1att.setOnClickListener(this);
        query_b3att.setOnClickListener(this);
        query_b38att.setOnClickListener(this);
        query_b39att.setOnClickListener(this);
        query_b40att.setOnClickListener(this);

        query_alarm.setOnClickListener(this);
        set_relese.setOnClickListener(this);

        query_b1power.setOnClickListener(this);
        query_b3power.setOnClickListener(this);
        query_b38power.setOnClickListener(this);
        query_b39power.setOnClickListener(this);
        query_b40power.setOnClickListener(this);
    }

    private void init() {
        sb_settings_vibrate1 = findView(R.id.sb_settings_vibrate1);   //  开关
        sb_settings_vibrate3 = findView(R.id.sb_settings_vibrate3);   //  开关
        sb_settings_vibrate38 = findView(R.id.sb_settings_vibrate38);   //  开关
        sb_settings_vibrate39 = findView(R.id.sb_settings_vibrate39);   //  开关
        sb_settings_vibrate40 = findView(R.id.sb_settings_vibrate40);   //  开关
        sb_scan_vibrate = findView(R.id.sb_scan_vibrate);   //  开关

        query1 = findView(R.id.query1);//查询
        query3 = findView(R.id.query3);//查询
        query38 = findView(R.id.query38);//查询
        query39 = findView(R.id.query39);//查询
        query40 = findView(R.id.query40);//查询
        query_scan = findView(R.id.query_scan);//查询

        state1 = findView(R.id.state1); //状态
        state3 = findView(R.id.state3); //状态
        state38 = findView(R.id.state38); //状态
        state39 = findView(R.id.state39); //状态
        state40 = findView(R.id.state40); //状态
        state_scan = findView(R.id.state_scan); //状态

        query_tem = findView(R.id.query_tem);//温度

        tem = findView(R.id.tem);

        query_ver = findView(R.id.query_ver);

        ver = findView(R.id.ver);

        set_address = findView(R.id.set_address);

        query_address = findView(R.id.query_address);

        address = findView(R.id.address);

        set_b1att = findView(R.id.set_b1att);
        set_b3att = findView(R.id.set_b3att);
        set_b38att = findView(R.id.set_b38att);
        set_b39att = findView(R.id.set_b39att);
        set_b40att = findView(R.id.set_b40att);

        query_b1att = findView(R.id.query_b1att);
        query_b3att = findView(R.id.query_b3att);
        query_b38att = findView(R.id.query_b38att);
        query_b39att = findView(R.id.query_b39att);
        query_b40att = findView(R.id.query_b40att);

        b1att = findView(R.id.b1att);
        b3att = findView(R.id.b3att);
        b38att = findView(R.id.b38att);
        b39att = findView(R.id.b39att);
        b40att = findView(R.id.b40att);

        query_b1power = findView(R.id.query_b1power);
        query_b3power = findView(R.id.query_b3power);
        query_b38power = findView(R.id.query_b38power);
        query_b39power = findView(R.id.query_b39power);
        query_b40power = findView(R.id.query_b40power);

        b1power = findView(R.id.b1power);
        b3power = findView(R.id.b3power);
        b38power = findView(R.id.b38power);
        b39power = findView(R.id.b39power);
        b40power = findView(R.id.b40power);


        query_alarm = findView(R.id.query_alarm);
        alarm = findView(R.id.alarm);


        set_relese = findView(R.id.set_relese);

        titleBar = findView(R.id.titlebar);

        titleBar.setTitle(R.string.power_amplifier);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private <T extends View> T findView(int resId) {
        return (T) findViewById(resId);
    }

    private TcpMsgCallback callback = new TcpMsgCallback() {
        @Override
        public void receiveMsg(String data) {
            LETLog.d("socketReceive:" + data);
            if (data.length() > 7) {
                int length = Integer.parseInt(data.substring(7, 9),16);
                switch (data.substring(3, 7)){
                    case "0402":
                        if(data.length() > 10){
                            if(TextUtils.equals(data.substring(9, 11),"0")){
                                state1.setText("关");
                            }else {
                                state1.setText("开");
                            }
                        }
                        break;
                    case "0403":
                        if(data.length() > 10){
                            if(TextUtils.equals(data.substring(9, 11),"0")){
                                state3.setText("关");
                            }else {
                                state3.setText("开");
                            }
                        }
                        break;
                    case "0404":
                        if(data.length() > 10){
                            if(TextUtils.equals(data.substring(9, 11),"0")){
                                state38.setText("关");
                            }else {
                                state38.setText("开");
                            }
                        }
                        break;
                    case "0405":
                        if(data.length() > 10){
                            if(TextUtils.equals(data.substring(9, 11),"0")){
                                state39.setText("关");
                            }else {
                                state39.setText("开");
                            }
                        }
                        break;
                    case "0406":
                        if(data.length() > 10){
                            if(TextUtils.equals(data.substring(9, 11),"0")){
                                state40.setText("关");
                            }else {
                                state40.setText("开");
                            }
                        }
                        break;
                    case "0501":
                        if(data.length() > 10){
                            tem.setText((Integer.parseInt(data.substring(9, 11),16)+""));
                        }
                        break;
                    case "F000":
                        if(data.length() > (9+(length*2))){
                            String height  = byteToBit((byte) Integer.parseInt(data.substring(9, 11),16));
                            String low  = byteToBit((byte) Integer.parseInt(data.substring(11, 13),16));
                            int yearNumber = decodeBinaryString(height.substring(0,4));
                            int month = decodeBinaryString(height.substring(4,8));
                            int day = decodeBinaryString(low.substring(0,5));
                            int number = decodeBinaryString(low.substring(5,8));
                            int year = 2016+(yearNumber -6);
                            ver.setText(year+""+month+""+day+""+number);
                        }
                        break;
                    case "F001":
                        if(data.length() > 10){
                            address.setText(("0x"+data.substring(9, 11)));
                        }
                        break;
                    case "FA10":
                        if(data.length() > 10){
                            b1att.setText((Integer.parseInt(data.substring(9, 11),16)+""));
                        }
                        break;
                    case "FA11":
                        if(data.length() > 10){
                            b3att.setText((Integer.parseInt(data.substring(9, 11),16)+""));
                        }
                        break;
                    case "FA12":
                        if(data.length() > 10){
                            b38att.setText((Integer.parseInt(data.substring(9, 11),16)+""));
                        }
                        break;
                    case "FA13":
                        if(data.length() > 10){
                            b39att.setText((Integer.parseInt(data.substring(9, 11),16)+""));
                        }
                        break;
                    case "FA14":
                        if(data.length() > 10){
                            b40att.setText((Integer.parseInt(data.substring(9, 11),16)+""));
                        }
                        break;
                    case "FA21":
                        if(data.length() > 12){
                            b1power.setText(((Integer.parseInt(data.substring(9, 13),16)/10f)+""));
                        }
                        break;
                    case "FA22":
                        if(data.length() > 12){
                            b3power.setText(((Integer.parseInt(data.substring(9, 13),16)/10f)+""));
                        }
                        break;
                    case "FA23":
                        if(data.length() > 12){
                            b38power.setText(((Integer.parseInt(data.substring(9, 13),16)/10f)+""));
                        }
                        break;
                    case "FA24":
                        if(data.length() > 12){
                            b39power.setText(((Integer.parseInt(data.substring(9, 13),16)/10f)+""));
                        }
                        break;
                    case "FA25":
                        if(data.length() > 12){
                            b40power.setText(((Integer.parseInt(data.substring(9, 13),16)/10f)+""));
                        }
                        break;
                    case "FA30":
                        if(data.length() > 12){
                            b3power.setText(((Integer.parseInt(data.substring(9, 13),16)/10f)+""));
                        }
                        break;
                    case "FAA0":
                        if(data.length() > 10){
                            if(TextUtils.equals(data.substring(9, 11),"0")){
                                alarm.setText("正常");
                            }else {
                                state40.setText("告警");
                            }
                        }
                        break;
                }
            }
        }

        @Override
        public void onFailed(Exception e) {

        }
    };
    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }
    /**
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sb_settings_vibrate1:
                tcpService.sendMsg(get03Cmd("11", "0402", isChecked ? 1 : 0), null);
                break;
            case R.id.sb_settings_vibrate3:
                tcpService.sendMsg(get03Cmd("11", "0403", isChecked ? 1 : 0), null);
                break;
            case R.id.sb_settings_vibrate38:
                tcpService.sendMsg(get03Cmd("11", "0407", isChecked ? 4 : 0), null);
                break;
            case R.id.sb_settings_vibrate39:
                tcpService.sendMsg(get03Cmd("11", "0407", isChecked ? 5 : 0), null);
                break;
            case R.id.sb_settings_vibrate40:
                tcpService.sendMsg(get03Cmd("11", "0407", isChecked ? 6 : 0), null);
                break;
            case R.id.sb_scan_vibrate:
                tcpService.sendMsg(get03Cmd("11", "FA30", isChecked ? 1 : 0), null);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query1:
                tcpService.sendMsg(get01Cmd("11", "0402", 1), callback);
                break;
            case R.id.query3:
                tcpService.sendMsg(get01Cmd("11", "0403", 1), callback);
                break;
            case R.id.query38:
                tcpService.sendMsg(get01Cmd("11", "0404", 1), callback);
                break;
            case R.id.query39:
                tcpService.sendMsg(get01Cmd("11", "0405", 1), callback);
                break;
            case R.id.query40:
                tcpService.sendMsg(get01Cmd("11", "0406", 1), callback);
                break;
            case R.id.query_tem:
                tcpService.sendMsg(get01Cmd("11", "0501", 1), callback);
                break;
            case R.id.query_ver:
                tcpService.sendMsg(get01Cmd("11", "F000", 2), callback);
                break;
            case R.id.query_address:
                tcpService.sendMsg(get01Cmd("11", "F001", 1), callback);
                break;
            case R.id.query_b1att:
                tcpService.sendMsg(get01Cmd("11", "FA10", 1), callback);
                break;
            case R.id.query_b3att:
                tcpService.sendMsg(get01Cmd("11", "FA20", 1), callback);
                break;
            case R.id.query_b38att:
                tcpService.sendMsg(get01Cmd("11", "FA30", 1), callback);
                break;
            case R.id.query_b39att:
                tcpService.sendMsg(get01Cmd("11", "FA40", 1), callback);
                break;
            case R.id.query_b40att:
                tcpService.sendMsg(get01Cmd("11", "FA50", 1), callback);
                break;
            case R.id.query_b1power:
                tcpService.sendMsg(get01Cmd("11", "FA21", 1), callback);
                break;
            case R.id.query_b3power:
                tcpService.sendMsg(get01Cmd("11", "FA22", 1), callback);
                break;
            case R.id.query_b38power:
                tcpService.sendMsg(get01Cmd("11", "FA23", 1), callback);
                break;
            case R.id.query_b39power:
                tcpService.sendMsg(get01Cmd("11", "FA24", 1), callback);
                break;
            case R.id.query_b40power:
                tcpService.sendMsg(get01Cmd("11", "FA25", 1), callback);
                break;
            case R.id.query_alarm:
                tcpService.sendMsg(get01Cmd("11", "FA30", 1), callback);
                break;
            case R.id.set_relese:
                tcpService.sendMsg(get03Cmd("11", "FAA0", 1), null);
                break;
            case R.id.set_address:
                showDailog("设置功放模块地址",1);
                break;
            case R.id.set_b1att:
                showDailog("设置ATT",2);
                break;
            case R.id.set_b3att:
                showDailog("设置ATT",3);
                break;
            case R.id.set_b38att:
                showDailog("设置ATT",4);
                break;
            case R.id.set_b39att:
                showDailog("设置ATT",5);
                break;
            case R.id.set_b40att:
                showDailog("设置ATT",6);
                break;
        }
    }
    private void showDailog(String title, final int type){
        LinearLayout container = new LinearLayout(PoweramplifierActivity.this);
        container.setOrientation(LinearLayout.VERTICAL);
        final EditText txtInput = new EditText(PoweramplifierActivity.this);
        container.addView(txtInput);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).topMargin = ViewUtil.dip2px(PoweramplifierActivity.this, 10);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).bottomMargin = ViewUtil.dip2px(PoweramplifierActivity.this, 10);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).height = ViewUtil.dip2px(PoweramplifierActivity.this, 55);
        txtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        switch (type){
            case 1:
                txtInput.setHint("范围1~254");
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                txtInput.setHint("范围 0~15");
                break;
        }

        txtInput.setSingleLine();
        txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        DialogManager.showDialog(PoweramplifierActivity.this, title, container, "确定", new DialogManager.IClickListener() {
            public boolean click(Dialog dlg, View view) {
                String newTitle = txtInput.getText().toString();
                if ((newTitle == null || newTitle.trim().length() == 0)) {
                    AppUtils.showToast(PoweramplifierActivity.this, "数据不能为空");
                    return false;
                }
                switch (type){
                    case 1:
                        tcpService.sendMsg(get03Cmd("11", "F001",Integer.parseInt(newTitle )), null);
                        break;
                    case 2:
                        tcpService.sendMsg(get03Cmd("11", "FA10",Integer.parseInt(newTitle )), null);
                        break;
                    case 3:
                        tcpService.sendMsg(get03Cmd("11", "FA11",Integer.parseInt(newTitle )), null);
                        break;
                    case 4:
                        tcpService.sendMsg(get03Cmd("11", "FA12",Integer.parseInt(newTitle )), null);
                        break;
                    case 5:
                        tcpService.sendMsg(get03Cmd("11", "FA13",Integer.parseInt(newTitle )), null);
                        break;
                    case 6:
                        tcpService.sendMsg(get03Cmd("11", "FA14",Integer.parseInt(newTitle )), null);
                        break;
                }
                AppUtils.hideInputKeyboard(PoweramplifierActivity.this, txtInput);
                return true;
            }
        }, "取消", new DialogManager.IClickListener() {
            public boolean click(Dialog dlg, View view) {
                AppUtils.hideInputKeyboard(PoweramplifierActivity.this, txtInput);
                return true;
            }
        }, null);
    }
    private String get01Cmd(String targetAddress, String address, int length) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(targetAddress);
        stringBuilder.append("21");
        stringBuilder.append(address);
        if (length < 10) {
            stringBuilder.append("0").append(length);
        } else {
            stringBuilder.append(length);
        }
        String crc = CrcUtil.GetCRC(stringBuilder.toString());

        return ":" +
                stringBuilder +
                Constants.crcId +
                crc +
                "\r\n";
    }

    private String get03Cmd(String targetAddress, String address, int length) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(targetAddress);
        stringBuilder.append("23");
        stringBuilder.append(address);
        String l = Integer.toHexString(length);
        stringBuilder.append("0").append(l.length());
        if (l.length() < 2) {
            stringBuilder.append("0").append(l);
        } else {
            stringBuilder.append(l);
        }
        String crc = CrcUtil.GetCRC(stringBuilder.toString());

        return ":" +
                stringBuilder +
                Constants.crcId +
                crc +
                "\r\n";
    }
}
