package com.lte.ui.fragment.second;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.communication.utils.Constant;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.lte.R;
import com.lte.service.MyOrientationListener;
import com.lte.ui.activity.NewBaseActivity;
import com.lte.ui.fragment.BaseFragment;
import com.lte.ui.listener.OnFragmentSelectListener;
import com.lte.ui.widget.PhilText;
import com.lte.utils.BluetoothCtrl;
import com.lte.utils.BluetoothUtils;
import com.lte.utils.CHexConver;
import com.lte.utils.DialogUtils;
import com.lte.utils.SendCommendHelper;
import com.lte.utils.SpeechUtils;
import com.lte.utils.ThreadUtils;
import com.lte.utils.ToastUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * Created by chenxiaojun on 2017/9/20.
 * 场强仪
 */

public class CQYFragment extends BaseFragment implements View.OnClickListener {

    private Dialog loadingDialog;
    private OnFragmentSelectListener mActivityListener;
    private static final int SINGLECHART = 1;
    private static final int TOTALCHART = 2;
    //    private LinearLayout layout_chart;
    private Button btn_single;
    private Button btn_total;
    private Button connectDevice;
    //    private ChartView chartView;
    private BluetoothUtils mBT;
    private PhilText fieldStrength;
    private TextView connectStatus;
    public int commandTag = 0;
    //0默认，1在设置pa，2设置cha，3设置pci，-1，-2，-3各自错误
    public static int item = 0;
    public boolean isRequestSyn=false;
    private TextView synState;
    private TextView batteryTv;
    private TextView channelTv;
    private ImageView speakImg;
    private ImageView batteryImg;
    private long lastClick = 0L;
    private long synLastClick = 0L;
    private int num1 = 0;
    private int num2 = 1;
    private boolean isSend;

    Boolean selected = false;


    private ReceiveDataThread mReceiveDataThread;

    /**
     * 方向传感器的监听器
     */
    private MyOrientationListener myOrientationListener;
    /**
     * 方向传感器X方向的值
     */
    private int mXDirection;
    private String disPlayTag = "DisPlayTag";

    private Timer mTimer;
    private boolean mbKeyboardMode;
    private boolean isStartSpeaking = false;
    private Button btn_yuan, btn_near;
    private static String[] cmdTag = {"0B0100", "0B0101", "0B0200", "0B0201", "0B0202", "0B0203", "0B0204", "0B0205", "0B0206", "0B0207", "0B03", "0B04", "0B0500", "0B0501", "0B0208", "0B0209", "0B02"};
    private long interval = 4000L;
    private static final String TAG = "CQYFragment";
    //false近，true远
    private boolean type=false;
    //新场强仪发数据
    private String sendDate;
    Runnable sendCmdFail = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(100);
        }
    };
    //xmd删除条形图布局
    public static CQYFragment newInstance() {
        CQYFragment fragment = new CQYFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cqy_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //接收蓝牙数据
        startReceiveData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mReceiveDataThread != null) {
            mReceiveDataThread.stopRunnable();
            mReceiveDataThread = null;
        }
    }

    private void init(View view) {
        Log.e("加载", "加载:" + commandTag);
        loadingDialog = DialogUtils.createLoadDialog(getContext(), true);
        InitLoad();

        //接收蓝牙数据
        startReceiveData();
        mBT = new BluetoothUtils();
        // 初始化传感器
        initOritationListener();
        // 开启方向传感器
        myOrientationListener.start();

        num1 = 0;
        num2 = 2;


        //0是老版本,1是新蓝牙
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.e("没有连接", "开启" + isConnect() + "pattern:" + App.pattern);
                    // TODO Auto-generated method stub
                    if (isConnect()) {
                        //xmd设置连接成功
                        mHandler.sendEmptyMessage(1);
                        //&& !isSend暂时不写
                        if (App.type == 1) {
//                            isSend = true;
                            if (!TextUtils.isEmpty(App.pattern)&& item == 0) {
                                item =1;
                                sendDate = App.pattern + App.channel + App.pci + "000000000000000021";
                                sendCommand(sendDate);
                                mHandler.removeCallbacks(sendCmdFail);
                                mHandler.postDelayed(sendCmdFail, interval);
                            }

                        } else {
                            //设置制式
                            if (!TextUtils.isEmpty(App.pattern) && item == 0) {

                                //显示选择框
                                mHandler.sendEmptyMessage(520);
                                //再次开启设置远近模式
                                isRequestSyn=false;
                                item = 1;
                                commandTag = 1;
                                sendCommand(App.pattern);
                                mHandler.removeCallbacks(sendCmdFail);
                                mHandler.postDelayed(sendCmdFail, interval);
                                Log.e("蓝牙设置数据", "xmd蓝牙设置数据pattern:" + App.pattern);
                            }
                            //同步频点
                            if (!TextUtils.isEmpty(App.channel) && item == 1) {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                item = 2;
                                commandTag = 11;
                                sendCommand(App.channel);
                                mHandler.removeCallbacks(sendCmdFail);
                                mHandler.postDelayed(sendCmdFail, interval);
                                Log.e("蓝牙设置数据", "xmd蓝牙设置数据channel:" + App.channel);
                            }
                            //PCI
                            if (!TextUtils.isEmpty(App.pci) && item == 2) {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                item = 3;
                                commandTag = 12;
                                sendCommand(App.pci);
                                Log.e("蓝牙设置数据", "xmd蓝牙设置数据pci:" + App.pci);
                                mHandler.removeCallbacks(sendCmdFail);
                                mHandler.postDelayed(sendCmdFail, interval);
                            }
                            if(item == 4&&!isRequestSyn){
                                isRequestSyn=true;
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                commandTag = 17;
                                sendCommand(SendCommendHelper.setSyn() + CHexConver.toHex(1));
                                mHandler.removeCallbacks(sendCmdFail);
                            }
                            //power请求,9之后才开始请求功率
                            if (item == 9) {
                                num1++;
                                num2++;
                                if (num1 % 2 == 0 && Constant.isSpeak) {
                                    mHandler.removeCallbacks(sendCmdFail);
                                    mHandler.postDelayed(speakData, 500);
                                }
                                if (num2 == 5) {
                                    num2 = 0;
                                    Log.i(TAG, "-----发送命令2-----");
                                    sendCommand1(SendCommendHelper.getElectricity());
                                    lastClick = System.currentTimeMillis();
                                }

                                if (System.currentTimeMillis() - lastClick > 1000) {
                                    Log.i(TAG, "-----发送命令1-----");
                                    sendCommand1(SendCommendHelper.getPower());
                                }
                            }

                        }
                    } else {
                        Log.e("没有连接", "没有连接" + item);
                        mHandler.sendEmptyMessage(4);
                    }
                }
            }, 500, 1300);
        }



//        layout_chart = (LinearLayout) view.findViewById(R.id.chartArea);
        btn_near = (Button) view.findViewById(R.id.btn_near);
        btn_yuan = (Button) view.findViewById(R.id.btn_yuan);
        btn_near.setBackground(getResources().getDrawable(R.drawable.shape_normal));
        btn_yuan.setBackground(getResources().getDrawable(R.drawable.shape_select));
        btn_single = (Button) view.findViewById(R.id.singleChart);
        btn_total = (Button) view.findViewById(R.id.totalChart);
//        chartView = new ChartView(getActivity(), SINGLECHART, layout_chart.getWidth(), layout_chart.getHeight());
//        layout_chart.removeAllViews();
//        layout_chart.addView(chartView);
        btn_single.setOnClickListener(this);
        btn_total.setOnClickListener(this);
        btn_near.setOnClickListener(this);
        btn_yuan.setOnClickListener(this);
        fieldStrength = (PhilText) view.findViewById(R.id.FieldStrength);
        connectStatus = (TextView) view.findViewById(R.id.ConnectStatus);
        synState = (TextView) view.findViewById(R.id.SynState);
        connectDevice = (Button) view.findViewById(R.id.ConnectDevice);
        batteryTv = (TextView) view.findViewById(R.id.batteryTv);
        channelTv = (TextView) view.findViewById(R.id.ChannelTv);
        channelTv.setText(Constant.channel);
        batteryImg = (ImageView) view.findViewById(R.id.BatteryImg);
        speakImg = (ImageView) view.findViewById(R.id.SpeakImg);
        speakImg.setOnClickListener(this);
        if (Constant.isSpeak) {
            speakImg.setBackgroundResource(R.drawable.speaker_on);
        } else {
            speakImg.setBackgroundResource(R.drawable.speaker_off);
        }
        if (isConnect()) {
            String str = getResources().getString(R.string.msg_connect_ok) + "\r\n";
            connectStatus.setText(str);
        }
        connectDevice.setOnClickListener(this);
        Constant.display = getString(R.string.display_state2);


        if (Constant.curr_value != 0) {
            DecimalFormat df = new DecimalFormat("#.0");
//            String fieldStrengthPow = "" + df.format((Constant.curr_value+118) /118);
            String fieldStrengthPow = "" + (int) ((118 - Constant.curr_value / 100) / 1.18);
            String fieldStrengthValue = (255 - 2 * Constant.curr_value / 100) + "";
            fieldStrength.setText(Constant.curr_value+"");

        }

        if (Constant.curr_battery != 0) {
            setBatteryImg(Constant.curr_battery);
            batteryTv.setText(Constant.curr_battery + "%");
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivityListener = (OnFragmentSelectListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IConnectionFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityListener = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int which = 0;
        Constant.POWTAG = 2;
//        Log.i(TAG, "-----layout_chart.getHeight----" + layout_chart.getHeight());
//        Log.i(TAG, "-----layout_chart.getWidth----" + layout_chart.getWidth());
        Log.i(TAG, "-----Constant.SCREEN_WIDTH----" + Constant.SCREEN_WIDTH);
        Log.i(TAG, "-----Constant.SCREEN_HEIGHT----" + Constant.SCREEN_HEIGHT);
        if (id == R.id.singleChart) {
            btn_single.setBackgroundResource(R.drawable.cancel_pressed);
            btn_total.setBackgroundResource(R.drawable.save_normal);
            which = SINGLECHART;
//            chartView = new ChartView(getActivity(), which, layout_chart.getWidth(), layout_chart.getHeight());
//            layout_chart.removeAllViews();
//            layout_chart.addView(chartView);

        } else if (id == R.id.totalChart) {
            btn_single.setBackgroundResource(R.drawable.cancel_normal);
            btn_total.setBackgroundResource(R.drawable.save_pressed);
            which = TOTALCHART;
//            chartView = new ChartView(getActivity(), which, layout_chart.getWidth(), layout_chart.getHeight());
//            layout_chart.removeAllViews();
//            layout_chart.addView(chartView);

        } else if (id == R.id.ConnectDevice) {
            mbKeyboardMode = false;
            onBlueToothConnection();
        } else if (id == R.id.SpeakImg) {

            if (Constant.isSpeak) {
                speakImg.setBackgroundResource(R.drawable.speaker_off);
                Constant.isSpeak = false;
            } else {
                speakImg.setBackgroundResource(R.drawable.speaker_on);
                Constant.isSpeak = true;
            }
        } else if (id == R.id.btn_near) {
            if(!type){
                btn_near.setBackground(getResources().getDrawable(R.drawable.shape_select));
                btn_yuan.setBackground(getResources().getDrawable(R.drawable.shape_normal));
                type=true;
            }
            if (System.currentTimeMillis() - synLastClick > 2000) {
                synLastClick = System.currentTimeMillis();
                if (item == 4) {
                    commandTag = 17;
                    mHandler.sendEmptyMessage(520);
                    sendCommand(SendCommendHelper.setSyn() + CHexConver.toHex(1));
                    mHandler.removeCallbacks(sendCmdFail);
                    mHandler.postDelayed(sendCmdFail, interval);
                } else if (item == 9) {
                    item = 4;
                    //切换远近模式，停1秒再设置
                    try {
//                        Thread.sleep(1000);
                        commandTag = 17;
                        mHandler.sendEmptyMessage(520);
                        sendCommand(SendCommendHelper.setSyn() + CHexConver.toHex(1));
                        mHandler.removeCallbacks(sendCmdFail);
                        mHandler.postDelayed(sendCmdFail, interval);
                    } catch (Exception e) {

                    }

                }else{
                    ToastUtils.show(getActivity(), "自动配置的参数还未配置完，请稍等！");
                }

            }else{
                ToastUtils.show(getActivity(), "请不要连续点击！");
            }

        } else if (id == R.id.btn_yuan) {
            if(type){
                btn_yuan.setBackground(getResources().getDrawable(R.drawable.shape_select));
                btn_near.setBackground(getResources().getDrawable(R.drawable.shape_normal));
                type=false;
            }
            if (System.currentTimeMillis() - synLastClick > 2000) {
                synLastClick = System.currentTimeMillis();
                if (item == 4) {
                    commandTag = 17;
                    mHandler.sendEmptyMessage(520);
                    sendCommand(SendCommendHelper.setSyn() + CHexConver.toHex(0));
                    mHandler.removeCallbacks(sendCmdFail);
                    mHandler.postDelayed(sendCmdFail, interval);
                } else if (item == 9) {
                    item = 4;
                    //切换远近模式，停1秒再设置
                    try {
//                        Thread.sleep(1000);
                        commandTag = 17;
                        mHandler.sendEmptyMessage(520);
                        sendCommand(SendCommendHelper.setSyn() + CHexConver.toHex(1));
                        mHandler.removeCallbacks(sendCmdFail);
                        mHandler.postDelayed(sendCmdFail, interval);
                    } catch (Exception e) {

                    }

                }else{
                    ToastUtils.show(getActivity(), "自动配置的参数还未配置完，请稍等！");
                }
            }else{
                ToastUtils.show(getActivity(), "请不要连续点击！");
            }
        }
    }

    //处理蓝牙通讯数据
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 521:{
                    if(loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                    break;
                }
                case 520:{
                    if(!loadingDialog.isShowing()){
                        loadingDialog.show();
                    }
                    break;
                }
                case 20: {
                    String data = msg.getData().getString("str");
                    Log.e("蓝牙", "蓝牙收：" + data);
                    if (App.type == 1) {
                        byte[] bBuf = (byte[]) msg.obj;
                        String newdate = bytesToHexString(bBuf);
                        if (isConnect()&&sendDate.length()>30) {
                            if (!TextUtils.isEmpty(newdate)&&newdate.endsWith("21")){
                                //判断设置成功
                                if(newdate.substring(4,10).equalsIgnoreCase(sendDate.substring(4,10))){
                                    Toast.makeText(getActivity(), getString(R.string.SettingSuccess), Toast.LENGTH_SHORT).show();
                                }
                                //接受长度在8到30，还有大于30
                                if(newdate.length()>30){
                                    fieldStrength.setText("" + Integer.parseInt(newdate.substring(24, 26), 16));
                                }else if(newdate.length()>8&&newdate.length()<=30){
                                    fieldStrength.setText("" + Integer.parseInt(newdate.substring(newdate.length() - 8, newdate.length() - 6), 16) );
                                }
                            }
                        }

                        Log.e("蓝牙接收数据","蓝牙接收数据:"+newdate);
                    } else {
                        if (isConnect() && data.length() >= 4 && commandTag > 0) {
                            if (commandTag == 11 || commandTag == 12 || commandTag == 17 || commandTag == 2) {
                                if (data.substring(0, 4).equals(cmdTag[commandTag - 1])) {
                                    //xmd成功连接频点，设置为11
                                    if (commandTag == 11) {
                                        item = 2;
                                        ToastUtils.show(getActivity(), "频点" + getString(R.string.SettingSuccess));
                                        mHandler.removeCallbacks(sendCmdFail);
                                    }
                                    //xmd成功连接PCI，设置为12
                                    else if (commandTag == 12) {
                                        item = 4;
                                        ToastUtils.show(getActivity(), "PCI" + getString(R.string.SettingSuccess));
                                        mHandler.removeCallbacks(sendCmdFail);
                                    } else if (commandTag == 17) {

                                        mHandler.sendEmptyMessage(521);
                                        item = 9;
                                        ToastUtils.show(getActivity(), "远近模式" + getString(R.string.SettingSuccess));
                                        mHandler.removeCallbacks(sendCmdFail);
                                    } else {
                                        mHandler.removeCallbacks(sendCmdFail);
                                        ToastUtils.show(getActivity(), getString(R.string.SettingSuccess));
                                    }
                                    Log.e("item", "itemcg:" + item);

                                }
                            } else {
                                if (data.length() >= 6) {
                                    if (data.substring(0, 6).equals(cmdTag[commandTag - 1])) {
                                        //xmd成功连接制式，设置为2
                                        if (commandTag == 1) {
                                            item = 2;
                                            ToastUtils.show(getActivity(), "制式" + getString(R.string.SettingSuccess));
                                            mHandler.removeCallbacks(sendCmdFail);
                                        } else {
                                            ToastUtils.show(getActivity(), getString(R.string.SettingSuccess));
                                            mHandler.removeCallbacks(sendCmdFail);
                                        }

                                    }
                                }
                            }
                        }


                    }
                    Log.i(TAG, "----version--data: " + data);

                    break;
                }
                case 1:
                    if (isAdded()) {
                        String str = getResources().getString(R.string.msg_connect_ok) + "\r\n";
                        connectStatus.setText(str);
                    }
                    break;
                case 2:
                    String data = msg.getData().getString("str");
                    Log.i(TAG, "----fieldStrength--data: " + data);
                    if (App.type == 0) {
                        if (isConnect() && data.length() >= 6) {
                            if (data.substring(0, 4).equals("0C01") && data.length() >= 8) {

                                int value = Integer.parseInt(data.substring(4, 6), 16) + 256 * Integer.parseInt(data.substring(6, 8), 16);
                                DecimalFormat df = new DecimalFormat("#.0");
//                                String fieldStrengthPow = "" + df.format((value+118) /118);
                                String fieldStrengthPow = "" + (int) ((118 - value / 100) / 1.18);
                                String fieldStrengthValue = (255 - 2 * value / 100) + "";
//                            value = 11830;
                                //Constant.curr_value = (int) ((118 - value / 100) / 1.18);
                                Constant.curr_value = (int)(255 - 2 * value / 100);
                                Log.i(TAG, "----isStartSpeaking: " + isStartSpeaking);
                                Log.i(TAG, "----Constant.isSpeak: " + Constant.isSpeak);
                                if (Constant.isSpeak) {

                                }

                                Log.i(TAG, "----fieldStrength: " + fieldStrengthPow);
                                fieldStrength.setText(fieldStrengthValue);
//                                updateData((float) (255 - 2 * value / 100), (float) mXDirection);
//                                    chartView.setFieldStrength((255 - 2 * value / 100) + "", mXDirection);

                            } else if (data.substring(0, 4).equals("0C02")) {
                                int battertValue = Integer.parseInt(data.substring(4, 6), 16);
                                Constant.curr_battery = battertValue;
                                setBatteryImg(battertValue);
                                batteryTv.setText(battertValue + "%");
                            } else if (data.substring(1, 5).equals("0C03") && data.length() >= 7) {
                                if (data.substring(5, 7).equals("00")) {
                                    synState.setText(Constant.synchro + "失步");
                                } else {
                                    synState.setText(Constant.synchro + "同步");
                                }
                            } else if (data.substring(0, 4).equals("0C03")) {
                                if (data.substring(4, 6).equals("00")) {
                                    synState.setText(Constant.synchro + "失步");
                                } else {
                                    synState.setText(Constant.synchro + "同步");
                                }
                            }
                        }
                    } else {


                    }


                    break;
                case 3:
//                    fieldStrength.setText("");
//                    try {
//                        Thread.sleep(400);
                        fieldStrength.setText(msg.getData().getString("str"));
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    break;
                case 4:
                    if (isAdded()) {
                        //重置重新配置
                        item = 0;
                        App.pattern="";
                        App.pci="";
                        App.channel="";
                        String str = getResources().getString(R.string.msg_Bluetooth_conn_lost) + "\r\n";
                        connectStatus.setText(str);
                        synState.setText(Constant.synchro + "失步");
                    }
                    break;
                case 100:
                    if (isAdded()) {
                        if (item == 1) {
                            item = 0;
                            if(App.type==0) {
                                ToastUtils.show(getActivity(), "制式" + getResources().getString(R.string.SettingFail));
                            }else{
                                ToastUtils.show(getActivity(),  getResources().getString(R.string.SettingFail));
                            }
                        } else if (item == 2) {
                            item = 1;
                            ToastUtils.show(getActivity(), "频点" + getResources().getString(R.string.SettingFail));
                        } else if (item == 3) {
                            item = 2;
                            ToastUtils.show(getActivity(), "PCI" + getResources().getString(R.string.SettingFail));
                        } else if (item == 4) {
                            if (commandTag == 17) {
                                mHandler.sendEmptyMessage(521);
                                isRequestSyn=false;
                                ToastUtils.show(getActivity(), "远近距离模式" + getResources().getString(R.string.SettingFail));
                            }
                        } else {
                            ToastUtils.show(getActivity(), getResources().getString(R.string.SettingFail));
                        }

                    }
                    break;
            }
        }
    };


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    Runnable speakData = new Runnable() {
        @Override
        public void run() {
            SpeechUtils.getInstance(getActivity()).speakText( Constant.curr_value + "");
        }
    };

    private void setBatteryImg(int value) {
        if (value >= 80) {
            batteryImg.setBackgroundResource(R.drawable.battery100);
        } else if (value >= 60 && value < 80) {
            batteryImg.setBackgroundResource(R.drawable.battery80);
        } else if (value >= 40 && value < 60) {
            batteryImg.setBackgroundResource(R.drawable.battery60);
        } else if (value >= 20 && value < 40) {
            batteryImg.setBackgroundResource(R.drawable.battery40);
        } else if (value > 0 && value < 20) {
            batteryImg.setBackgroundResource(R.drawable.battery20);
        } else {
            batteryImg.setBackgroundResource(R.drawable.battery20);
        }
    }

    public void onActivityResult(int i, int j, Intent intent) {

        if (3 == j) {
//            finish();
            return;
        }
        if (i == 1 && j == -1) {
            BluetoothDevice bluetoothdevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (bluetoothdevice.getBondState() == BluetoothDevice.BOND_NONE) {
                try {
                    Log.i(TAG, "----onActivityResult-----");
                    BluetoothCtrl.createBond(bluetoothdevice);
                    ToastUtils.show(getActivity(), getString(R.string.msg_actDiscovery_Bluetooth_Bond_msg));
                } catch (Exception exception) {
                    ToastUtils.show(getActivity(), getString(R.string.msg_actDiscovery_Bluetooth_Bond_fail));
                }
            } else {
                //xmd这里通过蓝牙名字区分，连接了哪个场强仪
                msBluetoothMAC = bluetoothdevice.getAddress();
                NewBaseActivity.msBluetoothMAC = bluetoothdevice.getAddress();
//                if (bluetoothdevice.getName().startsWith("hc05") || bluetoothdevice.getName().startsWith("HC05")) {
//                    //老蓝牙
//                    App.type = 0;
//                } else if (bluetoothdevice.getName().startsWith("hc06") || bluetoothdevice.getName().startsWith("HC06")) {
//                    //新蓝牙
//                    App.type = 1;
//                }
                if (!isConnect()) {
                    createBluetoothConnect();
                }
                startReceiveData();
            }
        }
    }

    private void sendCommand1(String str) {
        String cmd = CHexConver.str2HexStr(str) + "0D0A";
        Log.i(TAG, "----每隔3s发查询命令-sendCommand-----");
        if (isConnect()) {
            mHandler.sendEmptyMessage(1);
            int i;
            if (cmd.length() <= 0)
                return; /* Loop/switch isn't completed */
            i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
            if (i < 0) {
                Log.i(TAG, "-----SendBytesErr-----");
            }
        } else {
            mHandler.sendEmptyMessage(4);
        }

    }

    private void sendCommand(String str) {
        if (App.type == 0) {
            String cmd = CHexConver.str2HexStr(str) + "0D0A";
            if (isConnect()) {

                int i;
                if (cmd.length() <= 0)
                    return; /* Loop/switch isn't completed */
                if (mOutputMode != 0) {
                    Log.i(TAG, "-----mOutputMode != 0-----");
                    byte byte0 = mOutputMode;
                    i = 0;
                    if (1 == byte0) {
                        Log.i(TAG, "-----1 == byte0-----");
                        if (CHexConver.checkHexStr(cmd)) {

                            i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                        } else {
                            i = 0;
                        }
                    }
                } else {
                    Log.i(TAG, "-----connect_ok---SendData--");
                    i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                }
                if (i < 0) {
                    Log.i(TAG, "-----SendBytesErr-----");
                }
            } else {
                ToastUtils.show(getActivity(), getString(R.string.msg_Bluetooth_conn_lost));
            }
        } else {
//            Log.e("数据","str:"+str+"    CHexConver:"+CHexConver.hexStringToBytes(str.toUpperCase()).length);
//            for (int i=0;i<CHexConver.hexStringToBytes(str.toUpperCase()).length;i++){
//                Log.e("数据","CHexConver:"+CHexConver.hexStringToBytes(str.toUpperCase())[i]);
//                System.out.printf("%x\n",CHexConver.hexStringToBytes(str.toUpperCase())[i]);//按16进制输
//            }
            String cmd = str;
            if (isConnect()) {

                int i;
                if (cmd.length() <= 0)
                    return; /* Loop/switch isn't completed */
                if (mOutputMode != 0) {
                    Log.i(TAG, "-----mOutputMode != 0-----");
                    byte byte0 = mOutputMode;
                    i = 0;
                    if (1 == byte0) {
                        Log.i(TAG, "-----1 == byte0-----");
                        if (CHexConver.checkHexStr(cmd)) {

                            i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                            Log.e("数据", "CHexConver:" + i);
                        } else {
                            i = 0;
                        }
                    }
                } else {
                    Log.i(TAG, "-----connect_ok---SendData--");
                    i = SendData(CHexConver.hexStringToBytes(cmd.toUpperCase()));
                }
                if (i < 0) {
                    Log.i(TAG, "-----SendBytesErr-----");
                }
            } else {
                ToastUtils.show(getActivity(), getString(R.string.msg_Bluetooth_conn_lost));
            }
        }

    }


    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(getActivity().getApplication());
        myOrientationListener
                .setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        if (x != 0) {
                            mXDirection = (int) x;
                            Log.i("BDLocationListener", "---mXDirection = " + mXDirection);
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "-----onDestroy-----");
        mHandler.removeCallbacks(speakData);

        // 关闭方向传感器
        myOrientationListener.stop();

        if (mReceiveDataThread != null) {
            mReceiveDataThread.stopRunnable();
            mReceiveDataThread = null;
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer.purge();

       ;

        }


//        SpeechUtils.getInstance(getActivity()).closeVoice();
    }

    private void startReceiveData() {
        if (mReceiveDataThread == null) {
            mReceiveDataThread = new ReceiveDataThread();
            mReceiveDataThread.startRunable();
            mReceiveDataThread.start();
        }
    }

    private class ReceiveDataThread extends Thread {
        boolean isStopRunnable = false;

        public ReceiveDataThread() {
        }

        public void startRunable() {
            isStopRunnable = false;
        }

        public void stopRunnable() {
            isStopRunnable = true;
        }

        @Override
        public void run() {
            if (isConnect()) {

                if (App.type == 1) {
                    byte[] bufRecv = new byte[1024 * 2];
                    int nRecv = 0;
                    while (!isStopRunnable) {
                        try {
                            nRecv = BaseFragment.misIn.read(bufRecv);
                            if (nRecv < 1) {
                                Thread.sleep(100);
                                continue;
                            }

                            byte[] nPacket = new byte[nRecv];
                            System.arraycopy(bufRecv, 0, nPacket, 0, nRecv);
                            mHandler.obtainMessage(20,
                                    nRecv, -1, nPacket).sendToTarget();
                            Thread.sleep(100);
                        } catch (Exception e) {
                            break;
                        }
                    }
                } else {
                    Log.e("开启", "开启收");
                    int i;
                    byte bytebuf[];
                    bytebuf = new byte[1024 * 4];
                    String str;
                    while (!isStopRunnable) {
                        i = ReceiveData(bytebuf);
                        if (i <= 0)
                            break;
                        if (mInputMode == 0) {
                            str = new String(bytebuf, 0, i);
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("str", str);
                            message.setData(bundle);
                            Log.e("item", "item:" + item);
                            //xmd5,7获取的是版本信息，9获取功率
                            if ( item == 9) {
                                message.what = 2;
                            } else {
                                message.what = 20;
                            }
                            mHandler.sendMessage(message);
                        } else if (1 == mInputMode) {
                            str = (new StringBuilder(String.valueOf(CHexConver.byte2HexStr(bytebuf, i)))).append(" ").toString();
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("str", str);
                            message.setData(bundle);//bundle传值
                            message.what = 3;
                            mHandler.sendMessage(message);

                        }
                    }
                }


            }

        }
    }

    public void onBlueToothConnection() {
        Log.i(TAG, "-----onMenuConnection---");
        if (mBT.isBluetoothOpen()) {
            Log.i(TAG, "-----isConnect()----" + isConnect());
            if (isConnect())
                ToastUtils.show(getActivity(), getString(R.string.msg_re_connect));
            else {
                Log.i(TAG, "-----isConnect-false----");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if (msBluetoothMAC != null) {
                    builder.setTitle(getString(R.string.menu_main_Connection));
                    builder.setMessage(getString(R.string.msg_connect_history));

                    builder.setPositiveButton(R.string.btn_connect, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialoginterface, int i) {
                            //连接已连过的蓝牙设备，开启数据接收
                            createBluetoothConnect();
                            if (mReceiveDataThread != null) {
                                mReceiveDataThread.stopRunnable();
                                mReceiveDataThread = null;
                            }
                            startReceiveData();
                        }
                    });

                    builder.setNegativeButton(R.string.btn_reSearch, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialoginterface, int i) {
                            //重连，默认不配置
                            item = 0;
                            App.pattern="";
                            App.pci="";
                            App.channel="";
                            showBluetootchDiscovery();//搜索蓝牙设备
                        }
                    });
                    builder.create().show();
                } else
                    showBluetootchDiscovery();//搜索蓝牙设备
            }
        } else
            openButetooth();
    }



}