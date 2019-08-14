package com.lte.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
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
import android.widget.Toast;

import com.App;
import com.communication.request.RequestHelper;
import com.communication.tcp.TcpMsgCallback;
import com.communication.tcp.TcpService;
import com.communication.utils.LETLog;
import com.lte.R;
import com.lte.data.HttpResult;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.SwitchButton;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.CrcUtil;
import com.lte.utils.ThreadUtils;
import com.lte.utils.ToastUtils;
import com.lte.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.lte.utils.AppUtils.bytesToIntLittle;
import static com.lte.utils.AppUtils.decodeBinaryString;
import static com.lte.utils.AppUtils.hexString2binaryString;
import static com.lte.utils.AppUtils.parseHex4;
import static com.lte.utils.AppUtils.replaceBeginAndEnd;
import static com.lte.utils.CrcUtil.HexString2Bytes;
import static com.lte.utils.CrcUtil.hexStringToByte;
import static com.lte.utils.DateUtils.getTime;

/**
 * Created by chenxiaojun on 2018/4/2.
 */

public class PoweramplifierActivity1 extends BaseActivity implements View.OnClickListener {

    private static final int UPDATE = 2;
    private AppCompatImageView entry_item_icon_iv_1;

    private AppCompatImageView entry_item_icon_iv_2;

    private AppCompatImageView entry_item_icon_iv_3;

    private AppCompatImageView entry_item_icon_iv_4;

    private AppCompatImageView entry_item_icon_iv_5;

    private AppCompatImageView entry_item_icon_iv_6;

    private TextView tem;//功放温度

    private TextView ver;

    private TextView address;

//    private TextView b1att_tv;
//
//    private TextView b3att_tv;
//
//    private TextView b38att_tv;
//
//    private TextView b39att_tv;
//
//    private TextView b40att_tv;

    private TextView b1power_tv;

    private TextView b3power_tv;

    private TextView b38power_tv;

    private TextView b39power_tv;

    private TextView b40power_tv;

    private Button btn1;

    private Button btn2;

    private Button btn3;

    private Button btn4;

    private Button btn5;

    private Button btn6;

    private Button btn7;

    private Button btn8;

    private Button btn9;

    private Button relese;

    private Button btn_scan;

    private Button update;

    private TitleBar titleBar;

    private long upDateTime = 0l;

    private boolean b1On = false;

    private boolean b2On = false;

    private boolean btn_scanOn = false;

    private boolean b3On = false;

    private boolean b4On = false;

    private boolean b5On = false;

    private boolean b6On = false;

    private boolean b7On = false;

    private boolean b8On = false;

    private boolean b9On = false;
    private AtomicReference<String> localProgressFlag;

    private static final int TIME_OUT = 1;
    private MUiHandler mUiHandler;

    private static class MUiHandler extends Handler {


        private WeakReference<PoweramplifierActivity1> reference;


        MUiHandler(PoweramplifierActivity1 poweramplifierActivity1) {
            reference = new WeakReference<>(poweramplifierActivity1);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_OUT:
                    try {
                        DialogManager.closeDialog(reference.get().localProgressFlag.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CommonToast.show(reference.get(), R.string.update_fail);
                    break;
                case UPDATE:
                    reference.get().upDate();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power_amplifier_activity1);
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upDateTime = 0;
        if (App.get().tcpService != null) {
            localProgressFlag = new AtomicReference<>();
            localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
            mUiHandler = new MUiHandler(this);
            mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
            upDate();
            //lph 重新创建tcpserver
            //EventBus.getDefault().post(new MessageEvent(true));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setListener() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
        relese.setOnClickListener(this);
        update.setOnClickListener(this);
    }

    private void init() {

        entry_item_icon_iv_1 = findView(R.id.entry_item_icon_iv_1);

        entry_item_icon_iv_2 = findView(R.id.entry_item_icon_iv_2);

        entry_item_icon_iv_3 = findView(R.id.entry_item_icon_iv_3);

        entry_item_icon_iv_4 = findView(R.id.entry_item_icon_iv_4);

        entry_item_icon_iv_5 = findView(R.id.entry_item_icon_iv_5);

        entry_item_icon_iv_6 = findView(R.id.entry_item_icon_iv_6);

        tem = findView(R.id.tem);

        ver = findView(R.id.ver);

        address = findView(R.id.address);

//        b1att_tv = findView(R.id.b1att_tv);
//
//        b3att_tv = findView(R.id.b3att_tv);
//
//        b38att_tv = findView(R.id.b38att_tv);
//
//        b39att_tv = findView(R.id.b39att_tv);
//
//        b40att_tv = findView(R.id.b40att_tv);

        b1power_tv = findView(R.id.b1power_tv);

        b3power_tv = findView(R.id.b3power_tv);

        b38power_tv = findView(R.id.b38power_tv);

        b39power_tv = findView(R.id.b39power_tv);

        b40power_tv = findView(R.id.b40power_tv);

        btn1 = findView(R.id.btn1);

        btn2 = findView(R.id.btn2);

        btn3 = findView(R.id.btn3);

        btn4 = findView(R.id.btn4);

        btn5 = findView(R.id.btn5);

        btn6 = findView(R.id.btn6);

        btn7 = findView(R.id.btn7);

        btn8 = findView(R.id.btn8);

        btn9 = findView(R.id.btn9);

        relese = findView(R.id.relese);

        btn_scan = findView(R.id.btn_scan);

        update = findView(R.id.update);

        titleBar = findView(R.id.titlebar);

        titleBar.setTitle(R.string.power_amplifier);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mUiHandler = new MUiHandler(this);
    }

    private <T extends View> T findView(int resId) {
        return (T) findViewById(resId);
    }
    Map<String, String> map = new HashMap<>();
    private TcpMsgCallback callback = new TcpMsgCallback() {
        @Override
        public void receiveMsg(final String data) {
            mUiHandler.post(new Runnable(){
                @Override
                public void run() {
                    LETLog.d("TcpService:" + data);
                    map.clear();
                    mUiHandler.removeCallbacksAndMessages(null);
                    try {
                        DialogManager.closeDialog(localProgressFlag.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (data.length() > 3) {
                        String substring = data.substring(3,data.length()- 8);
                        while (substring.length() > 4) {
                            String tag = substring.substring(0, 4);
                            int length = Integer.parseInt(substring.substring(4, 6), 16);
                            String value = substring.substring(6, 6 + (length * 2));
                            map.put(tag, value);
                            substring = substring.substring(6 + (length * 2));
                        }
                        for (String i : map.keySet()) {
                            switch (i) {
                                case "0402":
                                    if (TextUtils.equals(map.get(i), "00")) {
                                        entry_item_icon_iv_1.setImageResource(R.mipmap.ic_circle_red);
                                        b1On = false;
                                        btn1.setText(getString(R.string.open_btn1));
                                    } else {
                                        entry_item_icon_iv_1.setImageResource(R.mipmap.ic_circle_green);
                                        b1On = true;
                                        btn1.setText(getString(R.string.close_btn1));
                                    }
                                    break;
                                case "0403":
                                    if (TextUtils.equals(map.get(i), "00")) {
                                        entry_item_icon_iv_2.setImageResource(R.mipmap.ic_circle_red);
                                        b2On = true;
                                        btn2.setText(getString(R.string.open_btn2));
                                    } else {
                                        entry_item_icon_iv_2.setImageResource(R.mipmap.ic_circle_green);
                                        b2On = true;
                                        btn2.setText(getString(R.string.close_btn2));
                                    }
                                    break;
                                case "0407":
                                    if (TextUtils.equals(map.get(i), "00")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
                                    } else if (TextUtils.equals(map.get(i), "01")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_green);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_green);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
                                    } else if (TextUtils.equals(map.get(i), "02")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_green);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_green);
                                    } else if (TextUtils.equals(map.get(i), "03")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_green);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_green);
                                    } else if (TextUtils.equals(map.get(i), "04")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_green);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
                                    } else if (TextUtils.equals(map.get(i), "05")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_green);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
                                    } else if (TextUtils.equals(map.get(i), "06")) {
                                        entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
                                        entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_green);
                                    }
                                    break;
                                case "0501":
                                    tem.setText(String.format(getString(R.string.tem1), Integer.parseInt(map.get(i), 16) + ""));
                                    break;
                                case "F000":
                                    String s = hexString2binaryString(replaceBeginAndEnd(map.get(i)));
                                    int number = decodeBinaryString(s.substring(0, 3));
                                    int day = decodeBinaryString(s.substring(3, 8));
                                    int month = decodeBinaryString(s.substring(8, 12));
                                    int yearNumber = decodeBinaryString(s.substring(12, 16));
                                    int year = 2016 + (yearNumber - 6);
                                    if(day <10 && month <10){
                                        ver.setText(String.format(getString(R.string.ver), year + "0" + month + "0" + day + "" + number));
                                    }else if(month <10){
                                        ver.setText(String.format(getString(R.string.ver), year + "0" + month + "" + day + "" + number));
                                    }else if(day <10){
                                        ver.setText(String.format(getString(R.string.ver), year + "" + month + "0" + day + "" + number));
                                    }
                                    break;
                                case "F001":
                                    address.setText(String.format(getString(R.string.address), ("0x" + map.get(i))));
                                    break;
//                                case "FA10":
//                                    b1att_tv.setText(String.format(getString(R.string.b1att), (Integer.parseInt(map.get(i), 16) + "")));
//                                    break;
//                                case "FA11":
//                                    b3att_tv.setText(String.format(getString(R.string.b3att), (Integer.parseInt(map.get(i), 16) + "")));
//                                    break;
//                                case "FA12":
//                                    b38att_tv.setText(String.format(getString(R.string.b38att), (Integer.parseInt(map.get(i), 16) + "")));
//                                    break;
//                                case "FA13":
//                                    b39att_tv.setText(String.format(getString(R.string.b39att), (Integer.parseInt(map.get(i), 16) + "")));
//                                    break;
//                                case "FA14":
//                                    b40att_tv.setText(String.format(getString(R.string.b40att), (Integer.parseInt(map.get(i), 16) + "")));
//                                    break;
                                case "FA21": {
                                    DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                    float power = parseHex4(replaceBeginAndEnd(map.get(i)))/10f;
                                    String distanceString = decimalFormat.format(power);
                                    b1power_tv.setText(String.format(getString(R.string.b1power), distanceString));
                                    break;
                                }
                                case "FA22":{
                                    DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                    float power = parseHex4(replaceBeginAndEnd(map.get(i)))/10f;
                                    String distanceString = decimalFormat.format(power);
                                    b3power_tv.setText(String.format(getString(R.string.b3power), distanceString));
                                    break;
                                }
                                case "FA23": {
                                    DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                    float power = parseHex4(replaceBeginAndEnd(map.get(i)))/10f;
                                    String distanceString = decimalFormat.format(power);
                                    b38power_tv.setText(String.format(getString(R.string.b38power), distanceString));
                                    break;
                                }
                                case "FA24":{
                                    DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                    float power = parseHex4(replaceBeginAndEnd(map.get(i)))/10f;
                                    String distanceString = decimalFormat.format(power);
                                    b39power_tv.setText(String.format(getString(R.string.b39power), distanceString));
                                    break;
                                }
                                case "FA25":{
                                    DecimalFormat decimalFormat = new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                    float power = parseHex4(replaceBeginAndEnd(map.get(i)))/10f;
                                    String distanceString = decimalFormat.format(power);
                                    b40power_tv.setText(String.format(getString(R.string.b40power), distanceString));
                                    break;
                                }
                                case "FA30":
                                    if (TextUtils.equals(map.get(i), "00")) {
                                        entry_item_icon_iv_6.setImageResource(R.mipmap.ic_circle_red);
                                        btn_scanOn = false;
                                        btn_scan.setText(getString(R.string.open_btn_scan));
                                    } else {
                                        entry_item_icon_iv_6.setImageResource(R.mipmap.ic_circle_green);
                                        btn_scanOn = true;
                                        btn_scan.setText(getString(R.string.colse_btn_scan));
                                    }
                                    break;
                            }
                        }
                    }
                }
            });
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
//    private void dtaa(String data){
//        LETLog.d("TcpService:" + data);
//        map.clear();
//        mUiHandler.removeCallbacksAndMessages(null);
//        try {
//            DialogManager.closeDialog(localProgressFlag.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (data.length() > 3) {
//            String substring = data.substring(3, data.length() - 8);
//            LETLog.d("TcpService: substring " + substring);
//            while (substring.length() > 4) {
//                String tag = substring.substring(0, 4);
//                LETLog.d("TcpService: tag " + tag);
//                int length = Integer.parseInt(substring.substring(4, 6), 16);
//                String value = substring.substring(6, 6 + (length * 2));
//                LETLog.d("TcpService: value " + value);
//                map.put(tag, value);
//                substring = substring.substring(6 + (length * 2));
//                LETLog.d("TcpService: substring1 " + map.size() + "-----" + substring);
//            }
//            LETLog.d("TcpService: " + map.size());
//            for (String i : map.keySet()) {
//                LETLog.d("TcpService: i " + i);
//                switch (i) {
//                    case "0402":
//                        if (TextUtils.equals(map.get(i), "00")) {
//                            entry_item_icon_iv_1.setImageResource(R.mipmap.ic_circle_red);
//                            b1On = false;
//                            btn1.setText(getString(R.string.open_btn1));
//                        } else {
//                            entry_item_icon_iv_1.setImageResource(R.mipmap.ic_circle_green);
//                            b1On = true;
//                            btn1.setText(getString(R.string.close_btn1));
//                        }
//                        break;
//                    case "0403":
//                        if (TextUtils.equals(map.get(i), "00")) {
//                            entry_item_icon_iv_2.setImageResource(R.mipmap.ic_circle_red);
//                            b2On = true;
//                            btn2.setText(getString(R.string.open_btn2));
//                        } else {
//                            entry_item_icon_iv_2.setImageResource(R.mipmap.ic_circle_green);
//                            b2On = true;
//                            btn2.setText(getString(R.string.close_btn2));
//                        }
//                        break;
//                    case "0407":
//                        if (TextUtils.equals(map.get(i), "00")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
//                        } else if (TextUtils.equals(map.get(i), "01")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_green);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_green);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
//                        } else if (TextUtils.equals(map.get(i), "02")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_green);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_green);
//                        } else if (TextUtils.equals(map.get(i), "03")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_green);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_green);
//                        } else if (TextUtils.equals(map.get(i), "04")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_green);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
//                        } else if (TextUtils.equals(map.get(i), "05")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_green);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_red);
//                        } else if (TextUtils.equals(map.get(i), "06")) {
//                            entry_item_icon_iv_3.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_4.setImageResource(R.mipmap.ic_circle_red);
//                            entry_item_icon_iv_5.setImageResource(R.mipmap.ic_circle_green);
//                        }
//                        break;
//                    case "0501":
//                        tem.setText(String.format(getString(R.string.tem1), Integer.parseInt(map.get(i), 16) + ""));
//                        break;
//                    case "F000":
//                        String height = byteToBit((byte) Integer.parseInt(map.get(i).substring(0, 2), 16));
//                        String low = byteToBit((byte) Integer.parseInt(map.get(i).substring(2, 4), 16));
//                        int yearNumber = decodeBinaryString(height.substring(0, 4));
//                        int month = decodeBinaryString(height.substring(4, 8));
//                        int day = decodeBinaryString(low.substring(0, 5));
//                        int number = decodeBinaryString(low.substring(5, 8));
//                        int year = 2016 + (yearNumber - 6);
//                        ver.setText(String.format(getString(R.string.ver), year + "" + month + "" + day + "" + number));
//                        break;
//                    case "F001":
//                        address.setText(String.format(getString(R.string.address), ("0x" + map.get(i))));
//                        break;
//                    case "FA10":
//                        b1att_tv.setText(String.format(getString(R.string.b1att), (Integer.parseInt(map.get(i), 16) + "")));
//                        break;
//                    case "FA11":
//                        b3att_tv.setText(String.format(getString(R.string.b3att), (Integer.parseInt(map.get(i), 16) + "")));
//                        break;
//                    case "FA12":
//                        b38att_tv.setText(String.format(getString(R.string.b38att), (Integer.parseInt(map.get(i), 16) + "")));
//                        break;
//                    case "FA13":
//                        b39att_tv.setText(String.format(getString(R.string.b39att), (Integer.parseInt(map.get(i), 16) + "")));
//                        break;
//                    case "FA14":
//                        b40att_tv.setText(String.format(getString(R.string.b40att), (Integer.parseInt(map.get(i), 16) + "")));
//                        break;
//                    case "FA21":
//                        b1power_tv.setText(String.format(getString(R.string.b1power), ((Integer.parseInt(map.get(i), 16) / 10f) + "")));
//                        break;
//                    case "FA22":
//                        b3power_tv.setText(String.format(getString(R.string.b3power), ((Integer.parseInt(map.get(i), 16) / 10f) + "")));
//                        break;
//                    case "FA23":
//                        b38power_tv.setText(String.format(getString(R.string.b38power), ((Integer.parseInt(map.get(i), 16) / 10f) + "")));
//                        break;
//                    case "FA24":
//                        b39power_tv.setText(String.format(getString(R.string.b39power), ((Integer.parseInt(map.get(i), 16) / 10f) + "")));
//                        break;
//                    case "FA25":
//                        b40power_tv.setText(String.format(getString(R.string.b40power), ((Integer.parseInt(map.get(i), 16) / 10f) + "")));
//                        break;
//                    case "FA30":
//                        if (TextUtils.equals(map.get(i), "00")) {
//                            entry_item_icon_iv_6.setImageResource(R.mipmap.ic_circle_red);
//                            btn_scanOn = false;
//                            btn_scan.setText(getString(R.string.open_btn_scan));
//                        } else {
//                            entry_item_icon_iv_6.setImageResource(R.mipmap.ic_circle_green);
//                            btn_scanOn = true;
//                            btn_scan.setText(getString(R.string.colse_btn_scan));
//                        }
//                        break;
//                }
//            }
//        }
//    }

    private String get01Cmd(String targetAddress, String address) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(targetAddress);
        stringBuilder.append("21");
        stringBuilder.append(address);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                if (App.get().tcpService != null) {
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler = new MUiHandler(this);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                    upDate();
                }
                break;
            case R.id.btn1:
                if (b1On) {
                    if (App.get().tcpService != null) {
                        App.get().tcpService.sendMsg(get03Cmd("11", "0402", 0), null);
                        localProgressFlag = new AtomicReference<>();
                        localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                        mUiHandler = new MUiHandler(this);
                        mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                        mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                    }
                } else {
                    if (App.get().tcpService != null) {
                        App.get().tcpService.sendMsg(get03Cmd("11", "0402", 1), null);
                        localProgressFlag = new AtomicReference<>();
                        localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                        mUiHandler = new MUiHandler(this);
                        mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                        mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                    }
                }
                break;
            case R.id.btn2:
                if (b2On) {
                    if (App.get().tcpService != null) {
                        App.get().tcpService.sendMsg(get03Cmd("11", "0403", 0), null);
                        localProgressFlag = new AtomicReference<>();
                        localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                        mUiHandler = new MUiHandler(this);
                        mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                        mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                    }
                } else {
                    if (App.get().tcpService != null) {
                        App.get().tcpService.sendMsg(get03Cmd("11", "0403", 1), null);
                        localProgressFlag = new AtomicReference<>();
                        localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                        mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                        mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                    }
                }
                break;
            case R.id.btn3:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 4), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn4:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 5), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn5:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 6), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn6:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 0), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn7:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 1), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn8:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 2), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn9:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "0407", 3), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.btn_scan:
                if (App.get().tcpService != null) {
                    if (btn_scanOn) {
                        App.get().tcpService.sendMsg(get03Cmd("11", "FA30", 0), null);
                    } else {
                        App.get().tcpService.sendMsg(get03Cmd("11", "FA30", 1), null);
                    }
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
            case R.id.relese:
                if (App.get().tcpService != null) {
                    App.get().tcpService.sendMsg(get03Cmd("11", "FA30", 1), null);
                    localProgressFlag = new AtomicReference<>();
                    localProgressFlag.set(DialogManager.showProgressDialog(this, "更新状态中，请稍候..",true));
                    mUiHandler.sendEmptyMessageDelayed(UPDATE, 5 * 1000L);
                    mUiHandler.sendEmptyMessageDelayed(TIME_OUT, 20 * 1000L);
                }
                break;
        }
    }

    private void upDate() {
        if (upDateTime != 0 && System.currentTimeMillis() -upDateTime< 5 * 1000L) {
            ToastUtils.showToast(this, getString(R.string.query_error), Toast.LENGTH_SHORT);
            return;
        }
        upDateTime = System.currentTimeMillis();
        if (App.get().tcpService != null) {
            App.get().tcpService.sendMsg(get01Cmd("11", "040201040301040701050101F00002F00101FA1001" +
                    "FA1101FA1201FA1301FA1401FA2102FA2202FA2302FA2402FA2502FA3001"), callback);
        }
    }
}
