package com.lte.tcpserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.App;
import com.lte.R;
import com.lte.data.CdmaConfig;
import com.lte.data.CellConfig;
import com.lte.data.DataManager;
import com.lte.data.GsmConfig;
import com.lte.data.ImsiData;
import com.lte.data.MacData;
import com.lte.data.ScanResult;
import com.lte.data.StationInfo;
import com.lte.data.TargetBean;
import com.lte.data.table.BandTable;
import com.lte.data.table.RealmInteger;
import com.lte.ui.event.CellUpgradeEvent;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.event.RedireectEvent;
import com.lte.ui.event.RestartEvent;
import com.lte.ui.event.TargetListMessage;
import com.lte.ui.fragment.CellConfigFragment;
import com.lte.ui.listener.TcpListener;
import com.communication.utils.LETLog;
import com.lte.utils.AppUtils;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ThreadPoolManager;
import com.lte.utils.ThreadUtils;

import org.apache.mina.core.session.IoSession;
import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

import io.realm.RealmList;

import static com.lte.utils.AppUtils.bytesToHexString;
import static com.lte.utils.AppUtils.bytesToHexString1;
import static com.lte.utils.AppUtils.bytesToInt2;
import static com.lte.utils.AppUtils.bytesToInt4;
import static com.lte.utils.AppUtils.hexStr2Str;
import static com.communication.utils.DateUtil.formatTime;
import static com.communication.utils.DateUtil.getOpera1;

/**
 * Created by chenxiaojun on 2017/9/11.
 */
//与设备交互线程
public class TcpManager extends Thread {

    private static final String TAG = "TcpManager";
    private static final int SEND_CELL_MSG = 1;
    //    private static final int OPEN_DBM = 2;
    private static final int CLOSE_DBM = 3;
    private static final int QUERY_SYSYTEM = 4;
    private static final int SEND_RESULT = 5;
    private static final int SHOUDONG = 6;
    private static final int START_GET_STATE = 7;
    private static final int UPGRADE_STATE = 8;
    private static final int INSERT_IMEI_DATA = 9;
    private static final int INSERT_IMSI_DATA = 10;
    private static final int SESSION_CLOSED = 11;
    private static final int CELL_UPGRADE = 12;
    private static final int CELL_UPGRADE1 = 13;
    private static final int CLOSE_DBM1 = 14;
    private static final int OPEN_DBM1 = 15;
    private static final int RESTART = 16;
    private static final int RESTART_SCAN = 17;
    private static final int STOP_SCAN = 18;
    private static final int OPEN_POSITION = 19;
    private static final int CLOSE_POSITION = 20;
    private static final int OPEN_DBM2 = 22;
    private static final int ClOSE_DBM3 = 23;
    private static final int POINT = 24;
    private static final int POINT1 = 28;
    private static final int POINT2 = 29;
    private static final int CHECK_ONLINE = 25;
    private static final int CHECK_POSITION = 26;
    private static final int REDIRECT = 27;
    private static final int RXLEVMIN_UPGRADE=30;
    private static final int RXLEVMIN_UPGRADE_RESPONSE=31;
    private static final int CELL_CONFIG_SUCESS=32;
    private static TcpManager mSingleInstance;
    private WeakReference<Context> mContext;

    private List<ScanResult> mScanResultList;

    private TcpServer tcpSevice;

    private int seqNo = 0;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            tcpSevice = ((TcpServer.LocalBinder) rawBinder).getService();
            tcpSevice.setListener(getHandler());
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            tcpSevice = null;
        }
    };
    private TcpListener mHandler;
    private boolean isClose;
    private boolean isData;
    private boolean isUpGrade;
    private boolean isUpGrade1;
    private boolean isUpGrade2;

    private boolean isUpGradeFinish;
    private boolean isUpGradeFinish1;
    private boolean isUpGradeFinish2;

    private boolean isUpGradeStart;
    private boolean isUpGradeStart1;
    private boolean isUpGradeStart2;


    private synchronized TcpListener getHandler() {
        while (mHandler == null) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return mHandler;
    }

    @Override
    public void run() {
        // Just process the message loop.
        Looper.prepare();
        synchronized (this) {
            mHandler = new TcpHandler(this);
            notifyAll();
        }
        Looper.loop();
    }

    public static TcpManager getInstance() {
        if (mSingleInstance == null) {
            synchronized (TcpManager.class) {
                if (mSingleInstance == null) {
                    mSingleInstance = new TcpManager();
                    mSingleInstance.start();
                }
            }
        }
        return mSingleInstance;
    }


    private TcpManager() {
    }

    public void initInstance(Context context) {

        Log.i(TAG, "initInstance: " + getWIFILocalIpAdress(context));

        mContext = new WeakReference<>(context);
        Intent bindIntent = new Intent(mContext.get(), TcpServer.class);
        mContext.get().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    public String getWIFILocalIpAdress(Context context) {
        //获取wifi服务
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = mWifiInfo.getIpAddress();
        return formatIpAddress(ipAddress);
    }

    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }


    private Handler mSendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_CELL_MSG: {
                    IoSession ioSession = (IoSession) msg.obj;
                    seqNo++;
                    for (StationInfo info : App.get().getMList()) {
                        if (TextUtils.equals(info.getIp(), ioSession.getRemoteAddress().toString())) {
                            byte[] data = info.getCellConfig().getCmd();
                            byte[] headData = new byte[]{0x01, 0x0f, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            byte[] sendData = new byte[data.length + headData.length];
                            System.arraycopy(headData, 0, sendData, 0, headData.length);
                            System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
                            ioSession.write(sendData);
                            LETLog.d("下发小区配置参数");
                        }
                    }
                }
                break;

                case CELL_CONFIG_SUCESS:{

                    StationInfo stationInfo = (StationInfo) msg.obj;
                    LETLog.d(TAG, "小区配置响应 ：配置成功"+stationInfo.toString());
                    //小区配置成功
                    //小区配置成功后配置一次最小接收电平 lph20190520

                    LETLog.d("RxLevMin configure after cell config:start" );
                    if(DataManager.getInstance().findStatInfoById(stationInfo.getId())!=null)
                    {
                        LETLog.d("RxLevMin configure after cell config:null" );
                        stationInfo.setRxlevmin(DataManager.getInstance().findStatInfoById(stationInfo.getId()).getRxlevmin());
                        LETLog.d("RxLevMin configure after cell config:" + stationInfo.getRxlevmin());

                        TcpManager.getInstance().setRxLevMinUpGrade(stationInfo, new CellConfigFragment.rxLevMinConfigCompleteInterface() {
                            @Override
                            public void rxLevMinConfigCallback(int result) {
                                if (result == 0) {
                                    //配置完成
                                    LETLog.d("RxLevMin configure after cell config:配置成功");
                                } else {
                                    LETLog.d("RxLevMin configure after cell config:配置失败");
                                }
                            }
                        });
                    }
                    else{
                        LETLog.d("RxLevMin configure after cell config:value" );
//                        stationInfo.setRxlevmin(-126);
//                        LETLog.d("RxLevMin configure after cell config:" + -126);
//                        TcpManager.getInstance().setRxLevMinUpGrade(stationInfo, new CellConfigFragment.rxLevMinConfigCompleteInterface() {
//                            @Override
//                            public void rxLevMinConfigCallback(int result) {
//                                if (result == 0) {
//                                    //配置完成
//                                    LETLog.d("RxLevMin configure after cell config:配置成功");
//                                } else {
//                                    LETLog.d("RxLevMin configure after cell config:配置失败");
//                                }
//                            }
//                        });
                    }
                }
                break;

//                case OPEN_DBM: {
////                    LETLog.d(TAG, "小区配置成功响应 ：" + App.get().getmList().size());
//                    //小区配置响应,开启射频
//                    boolean isConfig = true;
//                    for (StationInfo info : App.get().getMList()) {
//                        LETLog.d(TAG, "AppMlist..." + info.getSession() + " :" + info.isCellConfig());
//                        if (info.getType() == 4) {
//                            if (!info.isCellConfig() && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
//                                isConfig = false;
//                            }
//                        }
//                    }
//                    if (isConfig) {
//                        for (StationInfo info : App.get().getMList()) {
//                            if (info.getType() == 4) {
//                                if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && !info.isConfigDBM()) {
//                                    info.setOpen(true);
//                                    //小区配置成功，开启射频
//                                    byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, info.getDBM()};
//                                    seqNo++;
//                                    info.setConfigState(StationInfo.ConfigState.OPEN_DBM);
//                                    info.getSession().write(headData);
//                                    info.setConfigDBM(true);
//                                    LETLog.d(TAG, "发送消息..." + info.getSession().getRemoteAddress() + " :" + bytesToHexString(headData));
//                                }
//                            }
//                        }
//                        EventBus.getDefault().post(new MessageEvent(true));
//                    }
//                }
//                break;
                case CLOSE_DBM: {
                    for (StationInfo info : App.get().getMList()) {
                        if (info.getType() == 4) {
                            LETLog.d(TAG, "AppMlist..." + info.getSession() + " :" + info.isConfigDBM());
                            if (info.isCellConfig() && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && info.isConfigDBM()) {
                                info.setConfigDBM(false);
                                info.setOpen(false);
                                //，关闭射频
                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
                                seqNo++;
                                info.getSession().write(headData);

                            }
                        }
                    }

                }
                break;
                case CLOSE_DBM1: {
                    for (StationInfo info : App.get().getMList()) {
                        if (info.getType() == 4) {
                            LETLog.d(TAG, "AppMlist..." + info.getSession() + " :" + info.isConfigDBM());
                            if (info.isCellConfig() && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                                //，关闭射频
                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
                                seqNo++;
                                info.setConfigDBM(false);
                                info.getSession().write(headData);

                            }
                        }
                    }

                }
                break;
                case OPEN_DBM1: {
//                    for (final StationInfo info : App.get().getmList()) {
                    final StationInfo info = (StationInfo) msg.obj;
                    if (info.getType() == 4) {
                        LETLog.d(TAG, "AppMlist..." + info.getSession() + " :" + info.isConfigDBM());
                        if (info.isCellConfig() && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                            int tac = info.getCellConfig().getAddTac();
                            byte[] plmn = info.getCellConfig().getPlmnCmd();
                            byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            byte[] sendData3 = new byte[plmn.length + data2.length];
                            System.arraycopy(data2, 0, sendData3, 0, data2.length);
                            System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                            info.getSession().write(sendData3);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //，开启射频
                                    byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, info.getDBM()};
                                    seqNo++;
                                    info.setConfigDBM(true);
                                    info.getSession().write(headData);
                                    App.get().openPower(info);
                                }
                            }, 1000L);

                        }
                    }
//                    }

                }
                break;
                case QUERY_SYSYTEM: {
                    StationInfo stationInfo = (StationInfo) msg.obj;
                    if (stationInfo.getSystem() != null && stationInfo.getSystem().size() != 0) {
                        ArrayList<Integer> ms = stationInfo.getSystem();
                        int length = ms.get(20);
                        if (ms.size() > length + 20 + 16) {
                            LETLog.d("ms :" + "--" + ms.get(length + 20 + 4) + "--" + ms.get(length + 20 + 8)
                                    + "--" + ms.get(length + 20 + 12)
                                    + "--" + ms.get(length + 20 + 16));
                        }
                        byte[] soft_state = new byte[length];
                        for (int i = 0; i < length; i++) {
                            soft_state[i] = ms.get(21 + i).byteValue();
                        }
                        switch (ms.get(17)) {
                            case 0:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbuinit));
                                break;
                            case 1:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbuscan));
                                break;
                            case 2:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu2));
                                break;
                            case 3:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu3));
                                break;
                            case 4:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu4));
                                break;
                            case 5:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu5));
                                break;
                            case 6:
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu6));
                                break;
                        }
                        if (ms.size() > length + 20 + 4) {
                            stationInfo.setCpu_tem(ms.get(length + 20 + 4));
                        }
                        if (ms.size() > length + 20 + 8) {
                            stationInfo.setCpu_use(ms.get(length + 20 + 8));
                        }
                        if (ms.size() > length + 20 + 12) {
                            stationInfo.setRom_use(ms.get(length + 20 + 12));
                        }
                        if (ms.size() > length + 20 + 16) {
                            stationInfo.setTem(ms.get(length + 20 + 16));
                        }
                        EventBus.getDefault().post(new MessageEvent(true));
                        if (stationInfo.isCellConfig()) {
                            stationInfo.setQueryTime(System.currentTimeMillis());
                            Message message = Message.obtain();
                            message.what = UPGRADE_STATE;
                            message.obj = stationInfo;
                            this.sendMessageDelayed(message, 20 * 1000l);
                        }

                    }
                }
                break;
                case UPGRADE_STATE: {
                    StationInfo stationInfo = (StationInfo) msg.obj;
                    if (System.currentTimeMillis() - stationInfo.getQueryTime() >= 10 * 1000L) {
                        IoSession session = stationInfo.getSession();
                        byte[] headData = new byte[]{0x02, 0x0a, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                        seqNo++;
                        session.write(headData);
                    }
                    break;
                }
                case SHOUDONG: {
                    StationInfo stationInfo = (StationInfo) msg.obj;
                    stationInfo.setSoft_state(mContext.get().getString(R.string.bbu_scan));
                    byte[] data = stationInfo.getScanSet().getCmd();
                    byte[] headData = new byte[]{0x01, 0x05, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                    byte[] sendData = new byte[data.length + headData.length];
                    System.arraycopy(headData, 0, sendData, 0, headData.length);
                    System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
                    LETLog.d(TAG, "DTATA :" + bytesToHexString(sendData));
                    seqNo++;
                    stationInfo.setSoft_state(mContext.get().getString(R.string.bbu_scan));
                    EventBus.getDefault().post(new MessageEvent(true));
                    stationInfo.getSession().write(sendData);
                    break;
                }
                case SESSION_CLOSED:
                    IoSession session = (IoSession) msg.obj;
                    LETLog.d("sessionClosed :" + String.valueOf(session.getRemoteAddress()));
                    List<StationInfo> mList = App.get().getMList();
                    for (StationInfo stationInfo : mList) {
                        LETLog.d("sessionClosed : mList" + stationInfo.getSession());
                        if (stationInfo.getType() == 4) {
                            if (stationInfo.getSession() != null) {
                                if (stationInfo.getSession().isClosing() || stationInfo.getSession().getRemoteAddress() == null) {
                                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                    stationInfo.setConfigState(StationInfo.ConfigState.UN_CONFIG);
                                    stationInfo.setConfig(false);
                                    stationInfo.setOpen(false);
                                    stationInfo.setSoft_state("");
                                    stationInfo.setCpu_tem(0);
                                    stationInfo.setCpu_use(0);
                                    stationInfo.setRom_use(0);
                                    stationInfo.setIsCellConfig(false);
                                    stationInfo.setFreq(0);
                                    stationInfo.setConfigDBM(false);
                                    EventBus.getDefault().post(new MessageEvent(true));
                                }
                            }
                            if (stationInfo.getSession() == session) {
                                stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                stationInfo.setConfigState(StationInfo.ConfigState.UN_CONFIG);
                                stationInfo.setConfig(false);
                                stationInfo.setOpen(false);
                                stationInfo.setSoft_state("");
                                stationInfo.setCpu_tem(0);
                                stationInfo.setCpu_use(0);
                                stationInfo.setRom_use(0);
                                stationInfo.setIsCellConfig(false);
                                stationInfo.setFreq(0);
                                stationInfo.setConfigDBM(false);
                                EventBus.getDefault().post(new MessageEvent(true));
                            }
                            if (stationInfo.getSession() == null) {
                                stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                stationInfo.setConfigState(StationInfo.ConfigState.UN_CONFIG);
                                stationInfo.setConfig(false);
                                stationInfo.setOpen(false);
                                stationInfo.setSoft_state("");
                                stationInfo.setCpu_tem(0);
                                stationInfo.setCpu_use(0);
                                stationInfo.setRom_use(0);
                                stationInfo.setIsCellConfig(false);
                                stationInfo.setFreq(0);
                                stationInfo.setConfigDBM(false);
                                EventBus.getDefault().post(new MessageEvent(true));
                            }
                            if (session != null) {
                                if (TextUtils.equals(String.valueOf(session.getRemoteAddress()), stationInfo.getIp())) {
                                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                    stationInfo.setConfigState(StationInfo.ConfigState.UN_CONFIG);
                                    stationInfo.setConfig(false);
                                    stationInfo.setOpen(false);
                                    stationInfo.setSoft_state("");
                                    stationInfo.setCpu_tem(0);
                                    stationInfo.setCpu_use(0);
                                    stationInfo.setRom_use(0);
                                    stationInfo.setIsCellConfig(false);
                                    stationInfo.setFreq(0);
                                    stationInfo.setConfigDBM(false);
                                    EventBus.getDefault().post(new MessageEvent(true));
                                }
                            }
                        }
                    }
                    break;
                case CELL_UPGRADE1: {
                    seqNo++;
                    StationInfo stationInfo = (StationInfo) msg.obj;
                    byte[] sendData = stationInfo.getCellConfig().getCellUpGradeCmd(seqNo);
                    stationInfo.getSession().write(sendData);
                }
                    break;
                case RXLEVMIN_UPGRADE:{
                    seqNo++;
                    StationInfo stationInfo = (StationInfo) msg.obj;
                    short rxLevMin=(short)stationInfo.getRxlevmin();
                    byte[] bytes = {29,0x00,0x02,(byte) ((rxLevMin>>8)&0xFF),(byte) ((rxLevMin)&0xFF),30,0x00,0x01,0x01};
                    byte[] headData = new byte[]{0x02, 30, 0x00, (byte) (0x07 + bytes.length), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                    byte[] data = new byte[bytes.length + headData.length];
                    System.arraycopy(headData, 0, data, 0, headData.length);
                    System.arraycopy(bytes, 0, data, 0 + headData.length, bytes.length);
                    String datastr="";
                    for(int i=0;i<data.length;i++)
                    {
                        datastr+=Integer.toHexString(0xFF&data[i]);
                        datastr+=" ";
                    }
                    LETLog.d("RxLevMin configure: stationInfo.getSession().write"+datastr);
                    stationInfo.getSession().write(data);
                }
                break;
                case RXLEVMIN_UPGRADE_RESPONSE:{
                    Integer result=(Integer)msg.obj;
                    //显示最小接收电平配置结果
                    rxLevMinConfigComplete.rxLevMinConfigCallback(result);
                }
                break;
                case RESTART: {
                    for (StationInfo info : App.get().getMList()) {
                        if (info.getType() == 4) {
                            LETLog.d(TAG, "AppMlist..." + info.getSession() + " :" + info.isConfigDBM());
                            if (info.isCellConfig() && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                                //，复位
                                String restart = mContext.get().getString(R.string.restart);
                                byte[] bytes = restart.getBytes();
                                byte[] headData = new byte[]{0x02, 14, 0x00, (byte) (0x0a + bytes.length), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 5, 0x00, (byte) bytes.length};
                                byte[] data = new byte[bytes.length + headData.length];
                                System.arraycopy(headData, 0, data, 0, headData.length);
                                System.arraycopy(bytes, 0, data, 0 + headData.length, bytes.length);
                                seqNo++;
                                info.getSession().write(data);

                            }
                        }
                    }
                }
                break;
                case STOP_SCAN:
                    seqNo++;
                    StationInfo stationInfo1 = (StationInfo) msg.obj;
                    byte[] sendData1 = new byte[]{0x01, 0x07, 0x00, 7, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                    stationInfo1.getSession().write(sendData1);
                    break;
                case RESTART_SCAN:
                    seqNo++;
                    StationInfo stationInfo2 = (StationInfo) msg.obj;
                    byte[] sendData2 = new byte[]{0x01, 0x09, 0x00, 7, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                    stationInfo2.getSession().write(sendData2);
                    break;
                case OPEN_POSITION:
                    App.get().isClose = false;//打开定位，关闭其他基带板标记置为false；
//                    mSendHandler.removeMessages(POINT);
//                    mSendHandler.removeMessages(CHECK_POSITION);
                    for (final StationInfo info : App.get().getMList()) {
                        if (info.getType() == 4) {
                            if (info.isCellConfig() && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                                //，定位
                                StringBuilder stringBuilder = new StringBuilder();
                                boolean isContainYiDong = false;
                                boolean isContainLianTong = false;
                                boolean isContainDianXin = false;
                                for (String imsi : App.get().selectImsi) {
                                    stringBuilder.append(imsi);
                                    if (getOpera1(imsi) == 1) {
                                        isContainYiDong = true;
                                    } else if (getOpera1(imsi) == 2) {
                                        isContainLianTong = true;
                                    } else if (getOpera1(imsi) == 3) {
                                        isContainDianXin = true;
                                    }
                                }
                                byte[] bytes = AppUtils.hexStr2Bytes(stringBuilder.toString());
                                seqNo++;
                                byte[] headData = new byte[]{0x02, 32, 0x00, (byte) (14 + bytes.length), 36, 0x00, 0x01, 0x01, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 37, (byte) ((bytes.length >> 8) & 0xFF), (byte) (bytes.length & 0xFF)};
                                final byte[] data = new byte[bytes.length + headData.length];
                                System.arraycopy(headData, 0, data, 0, headData.length);
                                System.arraycopy(bytes, 0, data, 0 + headData.length, bytes.length);
                                if (isContainYiDong) {
                                    if (info.getIp() != null && info.getIp().length() > 15) {
                                        if (TextUtils.equals(info.getIp().substring(15, 16), "1")
                                                || TextUtils.equals(info.getIp().substring(15, 16), "2")
                                                || TextUtils.equals(info.getIp().substring(15, 16), "3")
                                                ) {
                                            postDelayed(new Runnable() {
                                                int isUpgrade = 1;

                                                @Override
                                                public void run() {
                                                    if (isUpgrade == 1) {
                                                        int tac = info.getCellConfig().getAddTac();
                                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
                                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                                        byte[] sendData3 = new byte[plmn.length + data2.length];
                                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                                        LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                                        info.getSession().write(sendData3);
                                                        isUpgrade = 2;
                                                        postDelayed(this, 1000L);
                                                    } else if (isUpgrade == 2) {
                                                        info.getSession().write(data);
                                                        info.setPositionOFF(false);
//                                                        isUpgrade = 3;
//                                                        postDelayed(this, 1000L);
                                                    }
                                                    else {
                                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 5};
                                                        seqNo++;
                                                        info.setDbm((byte) 5);
                                                        info.setOpen(true);
                                                        info.setConfigDBM(true);
                                                        info.getSession().write(headData);
                                                        App.get().openPower(info);
//                                                        mSendHandler.removeMessages(CHECK_POSITION);
                                                    }
                                                }
                                            }, 1000L);
                                        } else {
//                                            if (!isContainLianTong && !isContainDianXin) {
//                                                info.setConfigDBM(false);
//                                                info.setOpen(false);
//                                                //，关闭射频
//                                                byte[] headData1 = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
//                                                seqNo++;
//                                                info.getSession().write(headData1);
//                                                App.get().closePower(info);
//                                            }
                                            if (isContainDianXin) {
//                                                if (TextUtils.equals(info.getIp().substring(15, 16), "7")) {
//                                                    postDelayed(new Runnable() {
//                                                        int isUpgrade = 1;
//
//                                                        @Override
//                                                        public void run() {
//                                                            if (isUpgrade == 1) {
//                                                                //更新小区频点100。修改完延时2俩秒打开定位
//                                                                info.getCellConfig().setDownlink_frequency_point(100);
//                                                                info.getCellConfig().setUplink_frequency_point(18100);
//                                                                ArrayList<Integer> list = new ArrayList<>();
//                                                                try {
//                                                                    list.add(Integer.parseInt("46011f", 16));
//                                                                } catch (Exception e) {
//
//                                                                }
//                                                                info.getCellConfig().setPlmn(list);
//                                                                int tac = info.getCellConfig().getAddTac();
//                                                                byte[] plmn = info.getCellConfig().getPlmnCmd();
//                                                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
//                                                                byte[] sendData3 = new byte[plmn.length + data2.length];
//                                                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
//                                                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
//                                                                LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
//                                                                info.getSession().write(sendData3);
//                                                                isUpgrade = 2;
//                                                                postDelayed(this, 1000l);
//                                                            } else if (isUpgrade == 2) {
//                                                                info.getSession().write(data);
//                                                                isUpgrade = 3;
//                                                                info.setPositionOFF(false);
//                                                                postDelayed(this, 1000l);
//                                                            } else {
//                                                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 30};
//                                                                seqNo++;
//                                                                info.setDbm((byte) 30);
//                                                                info.setConfigDBM(true);
//                                                                info.setOpen(true);
//                                                                info.getSession().write(headData);
//                                                                App.get().openPower(info);
//                                                                mSendHandler.removeMessages(CHECK_POSITION);
//                                                            }
//                                                        }
//                                                    }, 1000L);
//                                                }
                                                if (TextUtils.equals(info.getIp().substring(15, 16), "6")) {
                                                    postDelayed(new Runnable() {
                                                        int isUpgrade = 1;

                                                        @Override
                                                        public void run() {
                                                            if (isUpgrade == 1) {
                                                                int tac = info.getCellConfig().getAddTac();
                                                                byte[] plmn = info.getCellConfig().getPlmnCmd();
                                                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                                                byte[] sendData3 = new byte[plmn.length + data2.length];
                                                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                                                LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                                                info.getSession().write(sendData3);
                                                                isUpgrade = 2;
                                                                postDelayed(this, 1000L);
                                                            } else if (isUpgrade == 2) {
                                                                info.getSession().write(data);
//                                                                isUpgrade = 3;
                                                                info.setPositionOFF(false);
//                                                                postDelayed(this, 1000L);
                                                            } else {
                                                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 5};
                                                                seqNo++;
                                                                info.setDbm((byte) 5);
                                                                info.setConfigDBM(true);
                                                                info.setOpen(true);
                                                                info.getSession().write(headData);
                                                                App.get().openPower(info);
//                                                                mSendHandler.removeMessages(CHECK_POSITION);
                                                            }
                                                        }
                                                    }, 1000L);
                                                }
                                            }
                                            if (isContainLianTong) {
//                                                if (TextUtils.equals(info.getIp().substring(15, 16), "6")) {
//                                                    postDelayed(new Runnable() {
//                                                        int isUpgrade = 1;
//
//                                                        @Override
//                                                        public void run() {
//                                                            if (isUpgrade == 1) {
//                                                                //更新小区频点500。修改完延时2俩秒打开定位
//                                                                info.getCellConfig().setDownlink_frequency_point(500);
//                                                                info.getCellConfig().setUplink_frequency_point(18500);
//                                                                ArrayList<Integer> list = new ArrayList<>();
//                                                                try {
//                                                                    list.add(Integer.parseInt("46001f", 16));
//                                                                } catch (Exception e) {
//
//                                                                }
//                                                                info.getCellConfig().setPlmn(list);
//                                                                int tac = info.getCellConfig().getAddTac();
//                                                                byte[] plmn = info.getCellConfig().getPlmnCmd();
//                                                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
//                                                                byte[] sendData3 = new byte[plmn.length + data2.length];
//                                                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
//                                                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
//                                                                LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
//                                                                info.getSession().write(sendData3);
//                                                                isUpgrade = 2;
//                                                                postDelayed(this, 1000L);
//                                                            } else if (isUpgrade == 2) {
//                                                                info.getSession().write(data);
//                                                                info.setPositionOFF(false);
//                                                                isUpgrade = 3;
//                                                                postDelayed(this, 1000L);
//                                                            } else {
//                                                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 30};
//                                                                seqNo++;
//                                                                info.setDbm((byte) 30);
//                                                                info.setConfigDBM(true);
//                                                                info.setOpen(true);
//                                                                info.getSession().write(headData);
//                                                                App.get().openPower(info);
//                                                                mSendHandler.removeMessages(CHECK_POSITION);
//                                                            }
//                                                        }
//                                                    }, 1000L);
//                                                }
                                                if (TextUtils.equals(info.getIp().substring(15, 16), "7")) {
                                                    postDelayed(new Runnable() {
                                                        int isUpgrade = 1;

                                                        @Override
                                                        public void run() {
                                                            if (isUpgrade == 1) {
//                                                                info.getCellConfig().setDownlink_frequency_point(1650);
//                                                                info.getCellConfig().setUplink_frequency_point(19650);
                                                                int tac = info.getCellConfig().getAddTac();
                                                                byte[] plmn = info.getCellConfig().getPlmnCmd();
                                                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                                                byte[] sendData3 = new byte[plmn.length + data2.length];
                                                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                                                LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                                                info.getSession().write(sendData3);
                                                                isUpgrade = 2;
                                                                postDelayed(this, 1000L);
                                                            } else if (isUpgrade == 2) {
                                                                info.getSession().write(data);
                                                                info.setPositionOFF(false);
//                                                                isUpgrade = 3;
//                                                                postDelayed(this, 1000L);
                                                            } else {
                                                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 5};
                                                                seqNo++;
                                                                info.setDbm((byte) 5);
                                                                info.setConfigDBM(true);
                                                                info.setOpen(true);
                                                                info.getSession().write(headData);
                                                                App.get().openPower(info);
//                                                                mSendHandler.removeMessages(CHECK_POSITION);
                                                            }
                                                        }
                                                    }, 1000L);
                                                }
                                            }
                                        }
                                    }
                                } else {
//                                    if (TextUtils.equals(info.getIp().substring(15, 16), "1")
//                                            || TextUtils.equals(info.getIp().substring(15, 16), "2")
//                                            || TextUtils.equals(info.getIp().substring(15, 16), "3")
//                                            ) {
//                                        info.setConfigDBM(false);
//                                        info.setOpen(false);
//                                        //，关闭射频
//                                        byte[] headData1 = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
//                                        seqNo++;
//                                        info.getSession().write(headData1);
//                                        App.get().closePower(info);
//                                    }
                                    if (isContainDianXin) {
                                        if (TextUtils.equals(info.getIp().substring(15, 16), "6")) {
                                            postDelayed(new Runnable() {
                                                int isUpgrade = 1;

                                                @Override
                                                public void run() {
                                                    if (isUpgrade == 1) {
                                                        //更新小区频点100。修改完延时2俩秒打开定位
//                                                        info.getCellConfig().setDownlink_frequency_point(100);
//                                                        info.getCellConfig().setUplink_frequency_point(18100);
                                                        int tac = info.getCellConfig().getAddTac();
                                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
                                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                                        byte[] sendData3 = new byte[plmn.length + data2.length];
                                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                                        LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                                        info.getSession().write(sendData3);
                                                        isUpgrade = 2;
                                                        postDelayed(this, 1000L);
                                                    } else if (isUpgrade == 2) {
                                                        info.getSession().write(data);
                                                        info.setPositionOFF(false);
//                                                        isUpgrade = 3;
//                                                        postDelayed(this, 1000L);
                                                    } else {
                                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 5};
                                                        seqNo++;
                                                        info.setDbm((byte) 5);
                                                        info.setConfigDBM(true);
                                                        info.setOpen(true);
                                                        info.getSession().write(headData);
                                                        App.get().openPower(info);
//                                                        mSendHandler.removeMessages(CHECK_POSITION);
                                                    }

                                                }
                                            }, 1000L);
                                        }
//                                        if (TextUtils.equals(info.getIp().substring(15, 16), "7")) {
//                                            postDelayed(new Runnable() {
//                                                int isUpgrade = 1;
//
//                                                @Override
//                                                public void run() {
//                                                    if (isUpgrade == 1) {
//                                                        info.getCellConfig().setDownlink_frequency_point(1825);
//                                                        info.getCellConfig().setUplink_frequency_point(19825);
//                                                        ArrayList<Integer> list = new ArrayList<>();
//                                                        try {
//                                                            list.add(Integer.parseInt("46011f", 16));
//                                                        } catch (Exception e) {
//
//                                                        }
//                                                        info.getCellConfig().setPlmn(list);
//                                                        int tac = info.getCellConfig().getAddTac();
//                                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
//                                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
//                                                        byte[] sendData3 = new byte[plmn.length + data2.length];
//                                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
//                                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
//                                                        LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
//                                                        info.getSession().write(sendData3);
//                                                        isUpgrade = 2;
//                                                        postDelayed(this, 1000L);
//                                                    } else if (isUpgrade == 2) {
//                                                        info.getSession().write(data);
//                                                        info.setPositionOFF(false);
//                                                        isUpgrade = 3;
//                                                        postDelayed(this, 1000L);
//                                                    } else {
//                                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 30};
//                                                        seqNo++;
//                                                        info.setDbm((byte) 30);
//                                                        info.setConfigDBM(true);
//                                                        info.setOpen(true);
//                                                        info.getSession().write(headData);
//                                                        App.get().openPower(info);
//                                                        mSendHandler.removeMessages(CHECK_POSITION);
//                                                    }
//
//                                                }
//                                            }, 1000L);
//                                        }
                                    }
                                    if (isContainLianTong) {
//                                        if (TextUtils.equals(info.getIp().substring(15, 16), "6")) {
//                                            postDelayed(new Runnable() {
//                                                int isUpgrade = 1;
//
//                                                @Override
//                                                public void run() {
//                                                    if (isUpgrade == 1) {
//                                                        //更新小区频点500。修改完延时2俩秒打开定位
//                                                        info.getCellConfig().setDownlink_frequency_point(500);
//                                                        info.getCellConfig().setUplink_frequency_point(18500);
//                                                        ArrayList<Integer> list = new ArrayList<>();
//                                                        try {
//                                                            list.add(Integer.parseInt("46001f", 16));
//                                                        } catch (Exception e) {
//
//                                                        }
//                                                        info.getCellConfig().setPlmn(list);
//                                                        int tac = info.getCellConfig().getAddTac();
//                                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
//                                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
//                                                        byte[] sendData3 = new byte[plmn.length + data2.length];
//                                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
//                                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
//                                                        LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
//                                                        info.getSession().write(sendData3);
//                                                        isUpgrade = 2;
//                                                        postDelayed(this, 1000L);
//                                                    } else if (isUpgrade == 2) {
//                                                        info.getSession().write(data);
//                                                        info.setPositionOFF(false);
//                                                        isUpgrade = 3;
//                                                        postDelayed(this, 1000L);
//                                                    } else {
//                                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 30};
//                                                        seqNo++;
//                                                        info.setDbm((byte) 30);
//                                                        info.setConfigDBM(true);
//                                                        info.setOpen(true);
//                                                        info.getSession().write(headData);
//                                                        App.get().openPower(info);
//                                                        mSendHandler.removeMessages(CHECK_POSITION);
//                                                    }
//                                                }
//                                            }, 1000L);
//                                        }
                                        if (TextUtils.equals(info.getIp().substring(15, 16), "7")) {
                                            postDelayed(new Runnable() {
                                                int isUpgrade = 1;

                                                @Override
                                                public void run() {
                                                    if (isUpgrade == 1) {
                                                        //更新小区频点500。修改完延时2俩秒打开定位
//                                                        info.getCellConfig().setDownlink_frequency_point(1650);
//                                                        info.getCellConfig().setUplink_frequency_point(19650);
                                                        int tac = info.getCellConfig().getAddTac();
                                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
                                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                                        byte[] sendData3 = new byte[plmn.length + data2.length];
                                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                                        LETLog.d("messageSent :" + "打开定位更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                                        info.getSession().write(sendData3);
                                                        isUpgrade = 2;
                                                        postDelayed(this, 1000L);
                                                    } else if (isUpgrade == 2) {
                                                        info.getSession().write(data);
                                                        info.setPositionOFF(false);
//                                                        isUpgrade = 3;
//                                                        postDelayed(this, 1000L);
                                                    }
//                                                    else {
//                                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 5};
//                                                        seqNo++;
//                                                        info.setDbm((byte) 5);
//                                                        info.setConfigDBM(true);
//                                                        info.setOpen(true);
//                                                        info.getSession().write(headData);
//                                                        App.get().openPower(info);
////                                                        mSendHandler.removeMessages(CHECK_POSITION);
//                                                    }

                                                }
                                            }, 1000L);
                                        }
                                    }
                                }

                            }

                        }
                    }
                    break;
                case CLOSE_POSITION:
                    for (final StationInfo info : App.get().getMList()) {
                        if (info.getType() == 4) {
                            if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                                boolean isContainYiDong = false;
                                boolean isContainLianTong = false;
                                boolean isContainDianXin = false;
                                for (String imsi : App.get().selectCloseImsi) {
                                    if (getOpera1(imsi) == 1) {
                                        isContainYiDong = true;
                                    } else if (getOpera1(imsi) == 2) {
                                        isContainLianTong = true;
                                    } else if (getOpera1(imsi) == 3) {
                                        isContainDianXin = true;
                                    }
                                }

                                //，关闭定位 延时两秒关闭射频
                                final byte[] headData = new byte[]{0x02, 32, 0x00, 11, 36, 0x00, 0x01, 0x00, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};

                                if (isContainYiDong) {
                                    if (info.getIp() != null && info.getIp().length() > 15) {
                                        if (TextUtils.equals(info.getIp().substring(15, 16), "1")
                                                || TextUtils.equals(info.getIp().substring(15, 16), "2")
                                                || TextUtils.equals(info.getIp().substring(15, 16), "3")
                                        ) {
                                            postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    seqNo++;
                                                    info.setPositionOFF(true);
                                                    info.getSession().write(headData);
                                                    info.initExceptDbm(info.getIp(), true);//回复默认配置
                                                    LETLog.d("send close position to "+info.toString());

                                                }
                                            }, 1000L);
                                        } else {
                                            if (TextUtils.equals(info.getIp().substring(15, 16), "6")) {
                                                postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        seqNo++;
                                                        info.setPositionOFF(true);
                                                        info.getSession().write(headData);
                                                        info.initExceptDbm(info.getIp(), true);//回复默认配置
                                                        LETLog.d("send close position to "+info.toString());
                                                    }
                                                }, 1000L);
                                            }
                                            if (isContainLianTong) {
                                                if (TextUtils.equals(info.getIp().substring(15, 16), "7")) {
                                                    postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            seqNo++;
                                                            info.setPositionOFF(true);
                                                            info.getSession().write(headData);
                                                            info.initExceptDbm(info.getIp(), true);//回复默认配置
                                                            LETLog.d("send close position to "+info.toString());
                                                        }
                                                    }, 1000L);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (isContainDianXin) {
                                        if (TextUtils.equals(info.getIp().substring(15, 16), "6")) {
                                            postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    seqNo++;
                                                    info.setPositionOFF(true);
                                                    info.getSession().write(headData);
                                                    info.initExceptDbm(info.getIp(), true);//回复默认配置
                                                    LETLog.d("send close position to "+info.toString());
                                                }
                                            }, 1000L);
                                        }
                                    }
                                    if (isContainLianTong) {
                                        if (TextUtils.equals(info.getIp().substring(15, 16), "7")) {
                                            postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    seqNo++;
                                                    info.setPositionOFF(true);
                                                    info.getSession().write(headData);
                                                    info.initExceptDbm(info.getIp(), true);//回复默认配置
                                                    LETLog.d("send close position to "+info.toString());
                                                }
                                            }, 1000L);
                                        }
                                    }
                                }

//                                postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        //，关闭射频
//                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
//                                        seqNo++;
//                                        info.getSession().write(headData);
//                                        info.setConfigDBM(false);
//                                        removeMessages(CHECK_POSITION);
//                                        App.get().closePower(info);
//                                    }
//                                }, 2000L);
                            }
                        }
                    }
                    break;
                case OPEN_DBM2: {
                    ThreadPoolManager.getInstance().removeAll();
                    for (StationInfo info : App.get().getMList()) {
                        if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED
                                && info.isCellConfig() && info.isConfigDBM()) {
                            if (msg.arg1 > 4 && msg.arg1 < 31) {
                                //小区配置成功，开启射频
                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, (byte) msg.arg1};
                                seqNo++;
                                info.setDbm((byte) msg.arg1);
                                info.setConfigDBM(true);
                                info.setOpen(true);
                                info.getSession().write(headData);
                                EventBus.getDefault().post(new MessageEvent(true));
                                App.get().openPower(info);


                                //lph 20190510 增加 解决关闭帧码开关后仍然执行小区更新问题
                                info.setUpdateCellOpen(true);
//                                ThreadPoolManager.getInstance().removeAll();
//                                ThreadPoolManager.getInstance().execute(new FutureTask<Object>(new CellUpDateRunable(info.getSession(), info), null), (info.getCellConfig().getTac_cycle() * 1000L));//延时执行


                            } else {
                                //，关闭射频
                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
                                seqNo++;
                                info.setDbm((byte) msg.arg1);
                                info.getSession().write(headData);
                                info.setConfigDBM(false);
                                EventBus.getDefault().post(new MessageEvent(true));
                                App.get().closePower(info);

                                //lph 20190510 增加 解决关闭帧码开关后仍然执行小区更新问题
                                info.setUpdateCellOpen(false);
                            }
                            //lph 20190510 注释掉 解决关闭帧码开关后仍然执行小区更新问题
//                          info.setUpdateCellOpen(true);
                            ThreadPoolManager.getInstance().removeAll();
                            ThreadPoolManager.getInstance().execute(new FutureTask<Object>(new CellUpDateRunable(info.getSession(), info), null), (info.getCellConfig().getTac_cycle() * 1000L));//延时执行
                        }
                    }
                    break;
                }
                case ClOSE_DBM3: {
                    StationInfo stationInfo3 = (StationInfo) msg.obj;
                    //，关闭射频
                    byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
                    seqNo++;
                    if (stationInfo3.getSession() != null) {
                        stationInfo3.getSession().write(headData);
                        stationInfo3.setConfigDBM(false);
                        EventBus.getDefault().post(new MessageEvent(true));
//                        App.get().closePower(stationInfo3);
                    }
                    break;
                }
                case POINT: {
                    StationInfo stationInfo3 = (StationInfo) msg.obj;
                    if (stationInfo3 != null) {
                        if (isUpGrade) {
                            CellConfig cellConfig = stationInfo3.getCellConfig();
                            int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
                            if (37750 <= downlink_frequency_point && downlink_frequency_point <= 38249||downlink_frequency_point==40936) {
                                BandTable band = DataManager.getInstance().findBandByName("band38/41");
                                if (band != null) {
                                    RealmList<RealmInteger> point = band.getPoint();
                                    int number = point.get((stationInfo3.getPoint() % point.size())).getNumber();
                                    cellConfig.setDownlink_frequency_point(number);
                                    cellConfig.setUplink_frequency_point(number);
                                }
                                int tac = stationInfo3.getCellConfig().getTac();
                                byte[] plmn = stationInfo3.getCellConfig().getPlmnCmd();
                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                byte[] sendData3 = new byte[plmn.length + data2.length];
                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                LETLog.d("messageSent :" + "循环更新小区 ：1 --" + stationInfo3.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                stationInfo3.getSession().write(sendData3);
                                int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                Message message = Message.obtain();
                                message.what = POINT;
                                message.obj = stationInfo3;
                                sendMessageDelayed(message, intConfig * 1000L);
                            } else if (38250 <= downlink_frequency_point && downlink_frequency_point <= 38649) {
                                BandTable band = DataManager.getInstance().findBandByName("band39");
                                if (band != null) {
                                    RealmList<RealmInteger> point = band.getPoint();
                                    int number = point.get((stationInfo3.getPoint() % point.size())).getNumber();
                                    cellConfig.setDownlink_frequency_point(number);
//                                    if (info.getTDDtype() == 1) {
//                                        cellConfig.setUplink_frequency_point(number + 18000);
//                                    } else {
                                    cellConfig.setUplink_frequency_point(number);
//                                    }
                                }
                                int tac = stationInfo3.getCellConfig().getTac();
                                byte[] plmn = stationInfo3.getCellConfig().getPlmnCmd();
                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                byte[] sendData3 = new byte[plmn.length + data2.length];
                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                LETLog.d("messageSent :" + "循环更新小区：2 --" + stationInfo3.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                stationInfo3.getSession().write(sendData3);
                                int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                Message message = Message.obtain();
                                message.what = POINT;
                                message.obj = stationInfo3;
                                sendMessageDelayed(message, intConfig * 1000L);
                            } else if (38650 <= downlink_frequency_point && downlink_frequency_point <= 39649) {
                                BandTable band = DataManager.getInstance().findBandByName("band40");
                                if (band != null) {
                                    RealmList<RealmInteger> point = band.getPoint();
                                    int number = point.get((stationInfo3.getPoint() % point.size())).getNumber();
                                    cellConfig.setDownlink_frequency_point(number);
//                                    if (info.getTDDtype() == 1) {
//                                        cellConfig.setUplink_frequency_point(number + 18000);
//                                    } else {
                                    cellConfig.setUplink_frequency_point(number);
//                                    }
                                }
                                int tac = stationInfo3.getCellConfig().getTac();
                                byte[] plmn = stationInfo3.getCellConfig().getPlmnCmd();
                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                byte[] sendData3 = new byte[plmn.length + data2.length];
                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                LETLog.d("messageSent :" + "循环更新小区：3 --" + stationInfo3.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                stationInfo3.getSession().write(sendData3);
                                int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                Message message = Message.obtain();
                                message.what = POINT;
                                message.obj = stationInfo3;
                                sendMessageDelayed(message, intConfig * 1000L);
                            }
                        }
                    } else {
                        if (isUpGrade) {
                            for (StationInfo info : App.get().getMList()) {
                                if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED
                                        && info.isCellConfig() && info.isConfigDBM()) {
                                    CellConfig cellConfig = info.getCellConfig();
                                    int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
                                    if (37750 <= downlink_frequency_point && downlink_frequency_point <= 38249||downlink_frequency_point==40936) {
                                        BandTable band = DataManager.getInstance().findBandByName("band38/41");
                                        if (band != null) {
                                            RealmList<RealmInteger> point = band.getPoint();
                                            int number = point.get((info.getPoint() % point.size())).getNumber();
                                            cellConfig.setDownlink_frequency_point(number);
                                            cellConfig.setUplink_frequency_point(number);
//                                    }
                                        }
                                        int tac = info.getCellConfig().getTac();
                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                        byte[] sendData3 = new byte[plmn.length + data2.length];
                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                        LETLog.d("messageSent :" + "循环更新小区 ：1 --" + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                        info.getSession().write(sendData3);
                                        int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                        Message message = Message.obtain();
                                        message.what = POINT;
                                        message.obj = info;
                                        sendMessageDelayed(message, intConfig * 1000L);
                                    } else if (38250 <= downlink_frequency_point && downlink_frequency_point <= 38649) {
                                        BandTable band = DataManager.getInstance().findBandByName("band39");
                                        if (band != null) {
                                            RealmList<RealmInteger> point = band.getPoint();
                                            int number = point.get((info.getPoint() % point.size())).getNumber();
                                            cellConfig.setDownlink_frequency_point(number);
//                                    if (info.getTDDtype() == 1) {
//                                        cellConfig.setUplink_frequency_point(number + 18000);
//                                    } else {
                                            cellConfig.setUplink_frequency_point(number);
//                                    }
                                        }
                                        int tac = info.getCellConfig().getTac();
                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                        byte[] sendData3 = new byte[plmn.length + data2.length];
                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                        LETLog.d("messageSent :" + "循环更新小区：2 --" + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                        info.getSession().write(sendData3);
                                        int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                        Message message = Message.obtain();
                                        message.what = POINT;
                                        message.obj = info;
                                        sendMessageDelayed(message, intConfig * 1000L);
                                    } else if (38650 <= downlink_frequency_point && downlink_frequency_point <= 39649) {
                                        BandTable band = DataManager.getInstance().findBandByName("band40");
                                        if (band != null) {
                                            RealmList<RealmInteger> point = band.getPoint();
                                            int number = point.get((info.getPoint() % point.size())).getNumber();
                                            cellConfig.setDownlink_frequency_point(number);
//                                    if (info.getTDDtype() == 1) {
//                                        cellConfig.setUplink_frequency_point(number + 18000);
//                                    } else {
                                            cellConfig.setUplink_frequency_point(number);
//                                    }
                                        }
                                        int tac = info.getCellConfig().getTac();
                                        byte[] plmn = info.getCellConfig().getPlmnCmd();
                                        byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                        byte[] sendData3 = new byte[plmn.length + data2.length];
                                        System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                        System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                        LETLog.d("messageSent :" + "循环更新小区：3 --" + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                        info.getSession().write(sendData3);
                                        int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                        Message message = Message.obtain();
                                        message.what = POINT;
                                        message.obj = info;
                                        sendMessageDelayed(message, intConfig * 1000L);
                                    }
                                }
                            }

                        }
                    }

                    break;
                }
                case POINT1: {
                    if (isUpGrade1) {
                        for (StationInfo info : App.get().getMList()) {
                            if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED
                                    && info.isCellConfig() && info.isConfigDBM()) {
                                CellConfig cellConfig = info.getCellConfig();
                                int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
                                if (300 <= downlink_frequency_point && downlink_frequency_point <= 599) {
                                    BandTable band = DataManager.getInstance().findBandByName("联通band1");
                                    if (isData) {
                                        BandTable band2 = DataManager.getInstance().findBandByName("联通band3");
                                        if (band2 != null) {
                                            RealmList<RealmInteger> point1 = band2.getPoint();
                                            if (band != null) {
                                                RealmList<RealmInteger> point = band.getPoint();
                                                int i = info.getPoint() % (point.size() + point1.size());
                                                int number = 0;
                                                if (i < point.size()) {
                                                    number = point.get(i).getNumber();
                                                } else {
                                                    number = point1.get(i - point.size()).getNumber();
                                                }
                                                cellConfig.setDownlink_frequency_point(number);
//                                            if (info.getTDDtype() == 1) {
                                                cellConfig.setUplink_frequency_point(number + 18000);
//                                            } else {
//                                                cellConfig.setUplink_frequency_point(number);
//                                            }
                                            }
                                        }
                                    } else if (band != null) {
                                        RealmList<RealmInteger> point = band.getPoint();
                                        int number = point.get((info.getPoint() % point.size())).getNumber();
                                        cellConfig.setDownlink_frequency_point(number);
//                                    if (info.getTDDtype() == 1) {
                                        cellConfig.setUplink_frequency_point(number + 18000);
//                                    } else {
//                                        cellConfig.setUplink_frequency_point(number);
//                                    }
                                    }
                                    int tac = info.getCellConfig().getTac();
                                    byte[] plmn = info.getCellConfig().getPlmnCmd();
                                    byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                    byte[] sendData3 = new byte[plmn.length + data2.length];
                                    System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                    System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                    LETLog.d("messageSent :" + "循环更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                    info.getSession().write(sendData3);
                                    int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                    sendEmptyMessageDelayed(POINT1, intConfig * 1000L);
                                } else if (1200 <= downlink_frequency_point && downlink_frequency_point <= 1699) {
                                    if (isData) {
                                        BandTable band = DataManager.getInstance().findBandByName("联通band1");
                                        BandTable band2 = DataManager.getInstance().findBandByName("联通band3");
                                        if (band2 != null) {
                                            RealmList<RealmInteger> point1 = band2.getPoint();
                                            if (band != null) {
                                                RealmList<RealmInteger> point = band.getPoint();
                                                int i = info.getPoint() % (point.size() + point1.size());
                                                int number = 0;
                                                if (i < point.size()) {
                                                    number = point.get(i).getNumber();
                                                } else {
                                                    number = point1.get(i - point.size()).getNumber();
                                                }
                                                cellConfig.setDownlink_frequency_point(number);
//                                            if (info.getTDDtype() == 1) {
                                                cellConfig.setUplink_frequency_point(number + 18000);
//                                            } else {
//                                                cellConfig.setUplink_frequency_point(number);
//                                            }
                                            }
                                        }
                                    } else {
                                        BandTable band = DataManager.getInstance().findBandByName("联通band3");
                                        if (band != null) {
                                            RealmList<RealmInteger> point = band.getPoint();
                                            int number = point.get((info.getPoint() % point.size())).getNumber();
                                            cellConfig.setDownlink_frequency_point(number);
//                                        if (info.getTDDtype() == 1) {
                                            cellConfig.setUplink_frequency_point(number + 18000);
//                                        } else {
//                                            cellConfig.setUplink_frequency_point(number);
//                                        }
                                        }
                                    }
                                    int tac = info.getCellConfig().getTac();
                                    byte[] plmn = info.getCellConfig().getPlmnCmd();
                                    byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                    byte[] sendData3 = new byte[plmn.length + data2.length];
                                    System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                    System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                    LETLog.d("messageSent :" + "循环更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                    info.getSession().write(sendData3);
                                    int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                    sendEmptyMessageDelayed(POINT1, intConfig * 1000L);
                                }

                            }
                        }

                    }
                    break;
                }
                case CHECK_ONLINE:
                    EventBus.getDefault().post(new MessageEvent(true));
                    break;
                case CHECK_POSITION: {
                    StationInfo stationInfo3 = (StationInfo) msg.obj;
                    if (!stationInfo3.isPositionOFF()) {
                        if (stationInfo3 != null) {
                            int tac = stationInfo3.getCellConfig().getAddTac();
                            byte[] plmn = stationInfo3.getCellConfig().getPlmnCmd();
                            byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            byte[] sendData3 = new byte[plmn.length + data2.length];
                            System.arraycopy(data2, 0, sendData3, 0, data2.length);
                            System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                            stationInfo3.getSession().write(sendData3);
                        }
                        for (final StationInfo info : App.get().getMList()) {
                            if (info.getType() == 4 && info.isPositionOn()) {
                                info.setPositionOn(false);
                                int tac = info.getCellConfig().getAddTac();
                                byte[] plmn = info.getCellConfig().getPlmnCmd();
                                byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                byte[] sendData3 = new byte[plmn.length + data2.length];
                                System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                info.getSession().write(sendData3);
                                App.get().isClose = false;
                                postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //，开启射频
                                        byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 30};
                                        seqNo++;
                                        info.setDbm((byte) 30);
                                        info.setConfigDBM(true);
                                        info.setOpen(true);
                                        info.getSession().write(headData);
//                                        App.get().openPower(info);
                                    }
                                }, 2000L);
                            }
                        }

                    }

                    break;
                }
                case POINT2: {
                    if (isUpGrade2) {
                        for (StationInfo info : App.get().getMList()) {
                            if (info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED
                                    && info.isCellConfig() && info.isConfigDBM()) {
                                CellConfig cellConfig = info.getCellConfig();
                                int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
                                if (0 <= downlink_frequency_point && downlink_frequency_point <= 299) {
                                    BandTable band = DataManager.getInstance().findBandByName("电信band1");
                                    if (isData) {
                                        BandTable band2 = DataManager.getInstance().findBandByName("电信band3");
                                        if (band2 != null) {
                                            RealmList<RealmInteger> point1 = band2.getPoint();
                                            if (band != null) {
                                                RealmList<RealmInteger> point = band.getPoint();
                                                int i = info.getPoint() % (point.size() + point1.size());
                                                int number = 0;
                                                if (i < point.size()) {
                                                    number = point.get(i).getNumber();
                                                } else {
                                                    number = point1.get(i - point.size()).getNumber();
                                                }
                                                cellConfig.setDownlink_frequency_point(number);
//                                            if (info.getTDDtype() == 1) {
                                                cellConfig.setUplink_frequency_point(number + 18000);
//                                            } else {
//                                                cellConfig.setUplink_frequency_point(number);
//                                            }
                                            }
                                        }
                                    } else if (band != null) {
                                        RealmList<RealmInteger> point = band.getPoint();
                                        int number = point.get((info.getPoint() % point.size())).getNumber();
                                        cellConfig.setDownlink_frequency_point(number);
//                                    if (info.getTDDtype() == 1) {
                                        cellConfig.setUplink_frequency_point(number + 18000);
//                                    } else {
//                                        cellConfig.setUplink_frequency_point(number);
//                                    }
                                    }
                                    int tac = info.getCellConfig().getTac();
                                    byte[] plmn = info.getCellConfig().getPlmnCmd();
                                    byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                    byte[] sendData3 = new byte[plmn.length + data2.length];
                                    System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                    System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                    LETLog.d("messageSent :" + "循环更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                    info.getSession().write(sendData3);
                                    int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                    sendEmptyMessageDelayed(POINT2, intConfig * 1000L);
                                } else if (1700 <= downlink_frequency_point && downlink_frequency_point <= 1949) {
                                    if (isData) {
                                        BandTable band = DataManager.getInstance().findBandByName("电信band1");
                                        BandTable band2 = DataManager.getInstance().findBandByName("电信band3");
                                        if (band2 != null) {
                                            RealmList<RealmInteger> point1 = band2.getPoint();
                                            if (band != null) {
                                                RealmList<RealmInteger> point = band.getPoint();
                                                int i = info.getPoint() % (point.size() + point1.size());
                                                int number = 0;
                                                if (i < point.size()) {
                                                    number = point.get(i).getNumber();
                                                } else {
                                                    number = point1.get(i - point.size()).getNumber();
                                                }
                                                cellConfig.setDownlink_frequency_point(number);
//                                            if (info.getTDDtype() == 1) {
                                                cellConfig.setUplink_frequency_point(number + 18000);
//                                            } else {
//                                                cellConfig.setUplink_frequency_point(number);
//                                            }
                                            }
                                        }
                                    } else {
                                        BandTable band = DataManager.getInstance().findBandByName("电信band3");
                                        if (band != null) {
                                            RealmList<RealmInteger> point = band.getPoint();
                                            int number = point.get((info.getPoint() % point.size())).getNumber();
                                            cellConfig.setDownlink_frequency_point(number);
//                                        if (info.getTDDtype() == 1) {
                                            cellConfig.setUplink_frequency_point(number + 18000);
//                                        } else {
//                                            cellConfig.setUplink_frequency_point(number);
//                                        }
                                        }

                                    }
                                    int tac = info.getCellConfig().getTac();
                                    byte[] plmn = info.getCellConfig().getPlmnCmd();
                                    byte[] data2 = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                    byte[] sendData3 = new byte[plmn.length + data2.length];
                                    System.arraycopy(data2, 0, sendData3, 0, data2.length);
                                    System.arraycopy(plmn, 0, sendData3, 0 + data2.length, plmn.length);
                                    LETLog.d("messageSent :" + "循环更新小区： " + info.getIp() + ":   " + bytesToHexString((byte[]) sendData3));
                                    info.getSession().write(sendData3);
                                    int intConfig = SharedPreferencesUtil.getIntConfig(App.get(), "sniffer", "time", 15);
                                    sendEmptyMessageDelayed(POINT2, intConfig * 1000L);
                                }
                            }
                        }
                    }
                    break;
                }
                case REDIRECT: {
                    seqNo++;
                    for (StationInfo info : App.get().getMList()) {
                        if (info.getType() == 4 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && info.getRedirectCmd() != null) {
                            byte[] data = info.getRedirectCmd();
                            byte[] headData = new byte[]{0x01, 27, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            byte[] sendData3 = new byte[data.length + headData.length];
                            System.arraycopy(headData, 0, sendData3, 0, headData.length);
                            System.arraycopy(data, 0, sendData3, 0 + headData.length, data.length);
                            info.getSession().write(sendData3);
                        }
                    }
                    break;
                }
//                case 25: {
//                    byte[] bytes1 = new byte[]{2, 40, 0, 40, 53, 0, 15, 52, 54, 48, 48, 55, 48, 54, 50, 49, 54, 48, 55, 55, 49, 50, 54, 0, 2, 0, 15, 55, 0, 4, 0, 0, 0, 97, 70, 0, 2, 0, 0, 71, 0, 2, -108, -50};
//                    ArrayList<Integer> list = new ArrayList<>();
//                    for (byte aByte : bytes1) {
//                        list.add((int) aByte);
//                    }
//                    StationInfo stationInfo4 = new StationInfo();
//                    stationInfo4.setIp("/192.168.178.211:31790", true);
//
//                    analysisPositionData(stationInfo4, list);
//                    byte[] bytes2 = new byte[]{2, 40, 0, 40, 53, 0, 15, 52, 54, 48, 48, 57, 52, 56, 55, 53, 56, 48, 54, 57, 53, 52, 54, 0, 2, 0, 24, 55, 0, 4, 0, 0, 0, -96, 70, 0, 2, 0, 0, 71, 0, 2, 76, -66};
//                    ArrayList<Integer> list1 = new ArrayList<>();
//                    for (byte aByte : bytes2) {
//                        list1.add((int) aByte);
//                    }
//
//                    analysisPositionData(stationInfo4, list1);
//                    byte[] bytes3 = new byte[]{2, 40, 0, 40, 53, 0, 15, 52, 54, 48, 48, 48, 48, 57, 55, 49, 52, 57, 53, 56, 53, 54, 54, 0, 2, 0, 15, 55, 0, 4, 0, 0, 0, 81, 70, 0, 2, 0, 0, 71, 0, 2, -108, 8};
//                    ArrayList<Integer> list2 = new ArrayList<>();
//                    for (byte aByte : bytes3) {
//                        list2.add((int) aByte);
//                    }
//
//                    analysisPositionData(stationInfo4, list2);
//                    sendEmptyMessageDelayed(25,500);
//                    break;
//                }
            }

        }

        public String hexStr2Str(String hexStr) {
            String str = "0123456789ABCDEF";
            char[] hexs = hexStr.toCharArray();
            byte[] bytes = new byte[hexStr.length() / 2];
            int n;

            for (int i = 0; i < bytes.length; i++) {
                n = str.indexOf(hexs[2 * i]) * 16;
                n += str.indexOf(hexs[2 * i + 1]);
                bytes[i] = (byte) (n & 0xff);
            }
            return new String(bytes);
        }
    };
    int i = 0;

    //调试代码，模拟数据
    public StationInfo testadd(String ip) {
        StationInfo stationInfo = new StationInfo();
        stationInfo.setSession(null);
        stationInfo.setId(App.get().getStationId());
        stationInfo.setName("基站" + (App.get().getMList().size() + 1));
        stationInfo.setIp(ip, true);
        stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CRATE);
        stationInfo.setType(22);
        App.get().getMList().add(stationInfo);
        App.get().insert(stationInfo);
        EventBus.getDefault().post(new MessageEvent(true));
//        StationInfo stationInfo1 = new StationInfo();
//        stationInfo1.setSession(null);
//        stationInfo1.setId(Long.valueOf((App.get().getmList().size() + 2)));
//        stationInfo1.setName("基站" + (App.get().getmList().size() + 1));
//        stationInfo1.setIp(ip, true);
//        stationInfo1.setConnectionStatus(StationInfo.ConnectionStatus.CRATE);
//        stationInfo1.setType(21);
//        App.get().getmList().add(stationInfo1);
//        App.get().insert(stationInfo1);
//        EventBus.getDefault().post(new MessageEvent(true));
//        StationInfo stationInfo2 = new StationInfo();
//        stationInfo2.setSession(null);
//        stationInfo2.setId(Long.valueOf((App.get().getmList().size() + 2)));
//        stationInfo2.setName("基站" + (App.get().getmList().size() + 1));
//        stationInfo2.setIp(ip, true);
//        stationInfo2.setConnectionStatus(StationInfo.ConnectionStatus.CRATE);
//        stationInfo2.setType(22);
//        App.get().getmList().add(stationInfo2);
//        App.get().insert(stationInfo2);
//        EventBus.getDefault().post(new MessageEvent(true));
//        StationInfo stationInfo3 = new StationInfo();
//        stationInfo3.setSession(null);
//        stationInfo3.setId(Long.valueOf((App.get().getmList().size() + 2)));
//        stationInfo3.setName("基站" + (App.get().getmList().size() + 1));
//        stationInfo3.setIp(ip, true);
//        stationInfo3.setConnectionStatus(StationInfo.ConnectionStatus.CRATE);
//        stationInfo3.setType(23);
//        App.get().getmList().add(stationInfo3);
//        App.get().insert(stationInfo3);
//        EventBus.getDefault().post(new MessageEvent(true));
        return stationInfo;
    }

    public void testAdd4g() {
        byte[] bytes1 = new byte[]{1, 19, 0, -122, 1, 0, 4, 0, 0, 0, -1, 27, 0, 1, 4, 28, 0, 120, 52, 54, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 54, 56, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 52, 54, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 54, 56, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 52, 54, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 54, 56, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 52, 54, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 54, 56, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ArrayList<Integer> list = new ArrayList<>();
        for (byte aByte : bytes1) {
            list.add((int) aByte);
        }

        inisterData(list, new StationInfo());
    }

    public void testAdd2g() {
        byte[] bytes1 = new byte[]{0x42, 0x00, 0x00, 0x00, 0x17, 0x18, 0x01, 0x00, 0x07, 0x00, 0x02, 0x02, 0x00, 0x00, 0x00, 0x17, 0x11, 0x02, 0x34, 0x36, 0x30, 0x30, 0x31, 0x32, 0x39, 0x34, 0x34, 0x32, 0x33, 0x35, 0x35, 0x36, 0x39,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x17, 0x12, 0x02, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x46, 0x30, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x15, 0x02, (byte) 0xf9, (byte) 0xff};
        tcpSevice.test(bytes1);
    }

    public void sendGsmUdpMsg(byte[] data) {
        if (tcpSevice != null) {
            tcpSevice.sendGsmUdpMsg(data);
        }
    }

    public void addQueryMsg() {
        if (tcpSevice != null) {
            tcpSevice.addQueryGsmMsg();
        }
    }

    public void openGsm(boolean on) {
        tcpSevice.openGsm(on);
    }

    public void openCdma(boolean on) {
        tcpSevice.openCdma(on);
    }

    public void sendCmdaUdpMsg(byte[] cmd1) {
        tcpSevice.sendCmdaUdpMsg(cmd1);
    }

    public void addCmdaQueryMsg() {
        if (tcpSevice != null) {
            tcpSevice.addCmdaQueryMsg();
        }
    }

    public void upDateGsmConfig(GsmConfig gsmConfig) {
        tcpSevice.upGradeGsmConfig(gsmConfig);
    }

    public void upDateCdmaConfig(CdmaConfig cmdaConfig) {
        tcpSevice.upGradeCmdaConfig(cmdaConfig);
    }

    public void setOpenDbm(int progress) {

        Message message = Message.obtain();
        message.what = OPEN_DBM2;
        message.arg1 = progress;
        mSendHandler.sendMessage(message);
    }

    public void setCloseDbm(StationInfo stationInfo) {
        Message message = Message.obtain();
        message.what = ClOSE_DBM3;
        message.obj = stationInfo;
        mSendHandler.sendMessage(message);
    }

    public void setPositionOn() {
        mSendHandler.sendEmptyMessageDelayed(OPEN_POSITION, 200L);
    }

    public void setPositionOFF() {
        mSendHandler.sendEmptyMessageDelayed(CLOSE_POSITION, 200L);
    }

    public void setCellUpGrade(StationInfo stationInfo) {
        Message message1 = Message.obtain();
        message1.what = CELL_UPGRADE1;
        message1.obj = stationInfo;
        mSendHandler.sendMessageDelayed(message1, 2000L);
    }

    private CellConfigFragment.rxLevMinConfigCompleteInterface rxLevMinConfigComplete;
    public void setRxLevMinUpGrade(StationInfo stationInfo,CellConfigFragment.rxLevMinConfigCompleteInterface myInterface)
    {
        rxLevMinConfigComplete=myInterface;
        Message message1=Message.obtain();
        message1.what=RXLEVMIN_UPGRADE;
        message1.obj=stationInfo;
        mSendHandler.sendMessageDelayed(message1,1000L);
    }

    public void setCloseDbm() {
        mSendHandler.sendEmptyMessageDelayed(CLOSE_DBM1, 2000L);
    }

    public void setOpenDam(StationInfo stationInfo) {
        Message message1 = Message.obtain();
        message1.what = OPEN_DBM1;
        message1.obj = stationInfo;
        mSendHandler.sendMessageDelayed(message1, 200L);
    }

    public void setRestart() {
        mSendHandler.sendEmptyMessageDelayed(RESTART, 2000L);
    }

    public void setGsmSetting() {
        tcpSevice.setGsmSetting();
    }

    public void setGsmClose() {
        tcpSevice.setGsmClose();
    }

    public void testpo() {
        mSendHandler.sendEmptyMessage(25);
    }

    public void setUpDatePoint(boolean on, boolean isData) {
        if (on) {
            this.isData = isData;
            isUpGrade = true;
            App.get().isClose = false;
            mSendHandler.removeMessages(POINT);
            mSendHandler.sendEmptyMessageDelayed(POINT, 10L);
        } else {
            this.isData = isData;
            isUpGrade = false;
            mSendHandler.removeMessages(POINT);
        }
    }

    public void setUpDatePoint1(boolean on, boolean isData) {
        if (on) {
            this.isData = isData;
            isUpGrade1 = true;
            mSendHandler.removeMessages(POINT1);
            mSendHandler.sendEmptyMessageDelayed(POINT1, 10L);
        } else {
            this.isData = isData;
            isUpGrade1 = false;
            mSendHandler.removeMessages(POINT1);
        }
    }

    public void setUpDatePoint2(boolean on, boolean isData) {
        if (on) {
            this.isData = isData;
            isUpGrade2 = true;
            mSendHandler.removeMessages(POINT2);
            mSendHandler.sendEmptyMessageDelayed(POINT2, 10L);
        } else {
            this.isData = isData;
            isUpGrade2 = false;
            mSendHandler.removeMessages(POINT2);
        }
    }

    public void setStartRedirect() {
        mSendHandler.sendEmptyMessageDelayed(REDIRECT, 10L);
    }

    private class TcpHandler implements TcpListener {

        private final WeakReference<TcpManager> mParent;

        TcpHandler(TcpManager machine) {
            this.mParent = new WeakReference<TcpManager>(machine);
        }

        @Override
        public void messageReceived(final IoSession session, Object message) {
            LETLog.d("messageReceived :" + session.getRemoteAddress() + "： " + message.toString());
            ArrayList<Integer> msg = (ArrayList<Integer>) message;
            //设备启动信息
            for (StationInfo stationInfo : App.get().getMList()) {
                if (TextUtils.equals(String.valueOf(session.getRemoteAddress()), stationInfo.getIp())) {
                    stationInfo.setCurrentTime(System.currentTimeMillis());
                    mSendHandler.removeMessages(CHECK_ONLINE);
                    mSendHandler.sendEmptyMessageDelayed(CHECK_ONLINE, 30 * 1000l);
                    if (msg.size() >= 11) {
                        if (msg.get(0) == 0x01 && msg.get(1) == 0x01) {
                            mSendHandler.sendEmptyMessage(CLOSE_DBM);
                            stationInfo.setIsCellConfig(false);
                            byte[] data = new byte[]{0x01, 0x02, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            session.write(data);
                            stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                            stationInfo.setSession(session);
                            EventBus.getDefault().post(new MessageEvent(true));
                            if (stationInfo.getCellConfig() == null || stationInfo.getScanSet() == null || stationInfo.getInitConfig() == null) {
                                return;
                            }
                            Log.d(TAG, "发送消息..." + session.getRemoteAddress() + " :" + bytesToHexString(data));
                            if (stationInfo.getInitConfig() != null) {
                                byte[] headData = getInitConfigCmd(msg, stationInfo);
                                session.write(headData);
                                stationInfo.setConfig(true);
                                stationInfo.setConfigState(StationInfo.ConfigState.INIT_CONFIG_ING);
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu_init));
                                EventBus.getDefault().post(new MessageEvent(true));
                                Log.d(TAG, "发送消息..." + session.getRemoteAddress() + " :" + bytesToHexString(headData));
                            }
                            analysis(msg, stationInfo);
                        } else if (msg.get(0) == 0x01 && msg.get(1) == 0x03) {
                            byte[] headData = new byte[]{0x01, 0x04, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            session.write(headData);
                        } else if (msg.get(0) == 1 && msg.get(1) == 8) {
                            Message message1 = Message.obtain();
                            message1.what = RESTART_SCAN;
                            message1.obj = stationInfo;
                            mSendHandler.sendMessageDelayed(message1, 200L);
                            stationInfo.setClose(true);
                            mSendHandler.sendEmptyMessageDelayed(CLOSE_DBM, 200L);
                        } else if (msg.get(0) == 1 && msg.get(1) == 10) {
                            stationInfo.setClose(true);
                            mSendHandler.sendEmptyMessageDelayed(CLOSE_DBM, 200L);
                        } else if (msg.get(0) == 2 && msg.get(1) == 29) {
                            if (msg.get(4) == 2 && msg.get(7) == 0) {
                                stationInfo.setConfig(true);
                                long times = System.currentTimeMillis() / 1000L;
//                                初始配置完成，设置系统时间
                                byte[] headData = new byte[]{0x02, 0x01, 0x00, 0x12, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF),
                                        0x03, 0x00, 0x08, (byte) ((times >> 56) & 0xFF), (byte) ((times >> 48) & 0xFF), (byte) ((times >> 40) & 0xFF), (byte) ((times >> 32) & 0xFF), (byte) ((times >> 24) & 0xFF), (byte) ((times >> 16) & 0xFF), (byte) ((times >> 8) & 0xFF), (byte) (times & 0xFF)};
                                seqNo++;
                                stationInfo.setConfigState(StationInfo.ConfigState.SET_SYSTEM_TIME);
                                EventBus.getDefault().post(new MessageEvent(true));
                                session.write(headData);
                            } else if (msg.get(4) == 1) {
                                if (msg.get(11) == 2 && msg.get(14) == 0) {
                                    stationInfo.setConfig(true);
                                    long times = System.currentTimeMillis();
                                    //初始配置完成，设置系统时间
                                    byte[] headData = new byte[]{0x02, 0x01, 0x00, 0x12, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF),
                                            0x03, 0x00, 0x08, (byte) ((times >> 56) & 0xFF), (byte) ((times >> 48) & 0xFF), (byte) ((times >> 40) & 0xFF), (byte) ((times >> 32) & 0xFF), (byte) ((times >> 24) & 0xFF), (byte) ((times >> 16) & 0xFF), (byte) ((times >> 8) & 0xFF), (byte) (times & 0xFF)};
                                    seqNo++;
                                    stationInfo.setConfigState(StationInfo.ConfigState.SET_SYSTEM_TIME);
                                    EventBus.getDefault().post(new MessageEvent(true));
                                    session.write(headData);
                                }
                            }

                        } else if (msg.get(0) == 2 && msg.get(1) == 2) {
                            if (msg.get(4) == 2 && msg.get(7) == 0) {
                                //时间设置响应,关闭DBM
                                byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
                                seqNo++;
                                session.write(headData);
                                stationInfo.setConfigDBM(false);
                                stationInfo.setClose(true);
                                stationInfo.setConfigState(StationInfo.ConfigState.CLOSE_DBM);
                                EventBus.getDefault().post(new MessageEvent(true));
                            } else if (msg.get(4) == 1) {
                                if (msg.get(11) == 2 && msg.get(14) == 0) {
                                    //时间设置响应,关闭DBM
                                    byte[] headData = new byte[]{0x02, 0x2f, 0x00, 0x0b, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF), 0x3a, 0x00, 0x01, 0x7f};
                                    seqNo++;
                                    session.write(headData);
                                    stationInfo.setClose(true);
                                    stationInfo.setConfigDBM(false);
                                    stationInfo.setConfigState(StationInfo.ConfigState.CLOSE_DBM);
                                    EventBus.getDefault().post(new MessageEvent(true));
                                }
                            }
                        } else if (msg.get(0) == 2 && msg.get(1) == 48) {
                            if (stationInfo.isCloseDbm()) {
                                stationInfo.setCloseDbm(false);

                                EventBus.getDefault().post(new MessageEvent(true));
                            }
//                            if (stationInfo.isOpenDbm()) {
//                                mSendHandler.postDelayed(new CellUpDateRunable1(session, stationInfo), 10L);
//                                stationInfo.setOpenDbm(false);
//                                stationInfo.setConfigState(StationInfo.ConfigState.OPEN_DBM_SUCCESS);
//                                EventBus.getDefault().post(new MessageEvent(true));
//                            }
                            if (stationInfo.isOpen()) {
                                if (msg.get(4) == 2 && msg.get(7) == 0) {
                                    //开启DBM响应
                                    stationInfo.setConfigState(StationInfo.ConfigState.OPEN_DBM_SUCCESS);
                                    EventBus.getDefault().post(new MessageEvent(true));
                                    byte[] headData = new byte[]{0x02, 0x05, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                    seqNo++;
                                    session.write(headData);
                                } else if (msg.get(4) == 1) {
                                    if (msg.get(11) == 2 && msg.get(14) == 0) {
                                        stationInfo.setConfigState(StationInfo.ConfigState.OPEN_DBM_SUCCESS);
                                        EventBus.getDefault().post(new MessageEvent(true));
                                        byte[] headData = new byte[]{0x02, 0x05, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                        seqNo++;
                                        session.write(headData);
                                    }
                                }
                                stationInfo.setOpen(false);
                            }
                            if (stationInfo.isClose()) {
                                if (msg.get(4) == 2 && msg.get(7) == 0) {
                                    //关闭DBM响应,系统状态查询
                                    byte[] headData = new byte[]{0x02, 0x0a, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                    seqNo++;
                                    session.write(headData);
                                    stationInfo.setIsCellConfig(false);
                                    stationInfo.setConfigDBM(false);
                                    stationInfo.setConfigState(StationInfo.ConfigState.QUERY_SYSTEM_STATUS);
                                    EventBus.getDefault().post(new MessageEvent(true));
                                } else if (msg.get(4) == 1) {
                                    if (msg.get(11) == 2 && msg.get(14) == 0) {
                                        //关闭DBM响应,系统状态查询
                                        byte[] headData = new byte[]{0x02, 0x0a, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                        seqNo++;
                                        stationInfo.setIsCellConfig(false);
                                        stationInfo.setConfigState(StationInfo.ConfigState.QUERY_SYSTEM_STATUS);
                                        stationInfo.setConfigDBM(false);
                                        EventBus.getDefault().post(new MessageEvent(true));
                                        session.write(headData);
                                    }
                                }
                                stationInfo.setClose(false);
                            }
                        } else if (msg.get(0) == 2 && msg.get(1) == 0x0b) {
                            //系统状态上报，扫频
                            Message message1 = Message.obtain();
                            message1.what = QUERY_SYSYTEM;
                            stationInfo.setSystem(msg);
                            message1.obj = stationInfo;
                            mSendHandler.sendMessage(message1);
                            LETLog.d(session.getRemoteAddress() + "： " +stationInfo.isCellConfig());
                            //启动扫频
                            if (!stationInfo.isCellConfig()) {
                                byte[] data = stationInfo.getScanSet().getCmd();
                                byte[] headData = new byte[]{0x01, 0x05, 0x00, (byte) (data.length + 7), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                byte[] sendData = new byte[data.length + headData.length];
                                System.arraycopy(headData, 0, sendData, 0, headData.length);
                                System.arraycopy(data, 0, sendData, 0 + headData.length, data.length);
                                seqNo++;
                                stationInfo.setSoft_state(mContext.get().getString(R.string.bbu_scan));
                                stationInfo.setConfigState(StationInfo.ConfigState.START_SCAN);
                                EventBus.getDefault().post(new MessageEvent(true));
                                LETLog.d(session.getRemoteAddress() + "： " + bytesToHexString( sendData));
                                session.write(sendData);
                                App.get().openScan();


                            }

                        }else if (msg.get(0) == 2 && msg.get(1) == 31) {
                            //最小接收电平配置相应
                            Message message1 = Message.obtain();
                            message1.what = RXLEVMIN_UPGRADE_RESPONSE;
                            message1.obj = msg.get(7);
                            mSendHandler.sendMessage(message1);
                            LETLog.d("RXLEVMIN_UPGRADE_RESPONSE result="+msg.get(7));
                        } else if (msg.get(0) == 1 && msg.get(1) == 0x0b) {
                            if (((msg.get(2) & 0xFF) * 256) + (msg.get(3) & 0xFF) == msg.size() - 4) {
                                scanResult(msg, stationInfo);
                                byte[] data = new byte[]{0x01, 0x0c, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                                seqNo++;
                                session.write(data);
                            } else {
                                stationInfo.setScanResultMsg(msg);
                            }

                        } else if (msg.get(0) == 1 && msg.get(1) == 0x0d) {
                            byte[] data = new byte[]{0x01, 0x0e, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            stationInfo.setConfigState(StationInfo.ConfigState.CELL_CONFIG_ING);
                            stationInfo.setSoft_state(mContext.get().getString(R.string.bbu_cell_config));
                            EventBus.getDefault().post(new MessageEvent(true));
                            session.write(data);
                            Log.d(TAG, "发送消息..." + session.getRemoteAddress() + " :" + bytesToHexString(data));
                            Message message1 = Message.obtain();
                            message1.obj = session;
                            message1.what = SEND_CELL_MSG;
                            mSendHandler.sendMessageDelayed(message1, 2000L);
                        } else if (msg.get(0) == 1 && msg.get(1) == 16) {
                            LETLog.d(TAG, "小区配置响应 ：");
                            byte[] headData = new byte[]{0x02, 0x05, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            session.write(headData);
                            if (SharedPreferencesUtil.getBooleanConfig(App.get(), "sniffer", "data", false)) {
                                setOpenDbm(5);
                            } else if (SharedPreferencesUtil.getBooleanConfig(App.get(), "sniffer", "position", false)) {
                                setPositionOn();
                            }
                            mSendHandler.sendEmptyMessageDelayed(REDIRECT, 20 * 1000L);
                            if (msg.get(4) == 6 && msg.get(7) == 0) {
                                LETLog.d(TAG, "小区配置响应 ：配置成功"+stationInfo.toString());
                                //小区配置成功
                                stationInfo.setIsCellConfig(true);
                                stationInfo.setConfigState(StationInfo.ConfigState.CELL_CONFIG_ED);
                                EventBus.getDefault().post(new MessageEvent(true));

                                Message message1 = Message.obtain();
                                message1.obj = stationInfo;
                                message1.what = CELL_CONFIG_SUCESS;
                                mSendHandler.sendMessageDelayed(message1, 100L);


//                               mSendHandler.sendEmptyMessageDelayed(OPEN_DBM, 10l);

                            } else if (msg.get(4) == 1) {
                                if (msg.get(11) == 6 && msg.get(14) == 0) {
                                    stationInfo.setIsCellConfig(true);
                                    stationInfo.setConfigState(StationInfo.ConfigState.CELL_CONFIG_ED);
                                    EventBus.getDefault().post(new MessageEvent(true));
//                                        mSendHandler.sendEmptyMessageDelayed(OPEN_DBM, 10l);

                                    Message message1 = Message.obtain();
                                    message1.obj = stationInfo;
                                    message1.what = CELL_CONFIG_SUCESS;
                                    mSendHandler.sendMessageDelayed(message1, 100L);

                                } else if (msg.get(11) == 6 && msg.get(14) != 0) {
                                    Message message1 = Message.obtain();
                                    message1.obj = session;
                                    message1.what = SEND_CELL_MSG;
                                    mSendHandler.sendMessageDelayed(message1, 200L);
                                }
                            } else if (msg.get(4) == 6 && msg.get(7) != 0) {
                                Message message1 = Message.obtain();
                                message1.obj = session;
                                message1.what = SEND_CELL_MSG;
                                mSendHandler.sendMessageDelayed(message1, 200L);
                            }
                        } else if (msg.get(0) == 1 && msg.get(1) == 19) {
                            if ((msg.get(2) & 0xFF) * 256 + (msg.get(3) & 0xFF) == msg.size() - 4) {
                                inisterData(msg, stationInfo);
                            } else {
                                stationInfo.setMsg(msg);
                            }
                            byte[] headData = new byte[]{0x01, 20, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            session.write(headData);
                        } else if (msg.get(0) == 2 && msg.get(1) == 6) {
                            stationInfo.setQueryTime(System.currentTimeMillis());
                            Message message1 = Message.obtain();
                            message1.what = UPGRADE_STATE;
                            message1.obj = stationInfo;
                            mSendHandler.sendMessageDelayed(message1, 10 * 1000l);
                            if (msg.get(3) == msg.size() - 4) {
                                initVer(msg, stationInfo);
                            } else {
                                stationInfo.setVerMsg(msg);
                            }
                        } else if (msg.get(0) == 1 && msg.get(1) == 18) {
                            //小区更新相应
                            LETLog.d(TAG, "接收到小区更新响应");
                            EventBus.getDefault().post(new CellUpgradeEvent());

                        } else if (msg.get(0) == 2 && msg.get(1) == 15) {
                            stationInfo.setRestart(false);
//                            App.get().upDateList();
                            mSendHandler.removeCallbacksAndMessages(null);
                            EventBus.getDefault().post(new RestartEvent());
                            EventBus.getDefault().post(new MessageEvent(true));
                        } else if (msg.get(0) == 2 && msg.get(1) == 40) {
                            LETLog.d(TAG,"接收到小区定位上报");
                            analysisPositionData(stationInfo, msg);
                        } else if (msg.get(0) == 1 && msg.get(1) == 28) {
                            redirectData(stationInfo, msg);
                        }
//                        }
                    }
                    if (stationInfo.getMsg() != null && stationInfo.getMsg().size() > 4) {
                        if (((stationInfo.getMsg().get(2) & 0xFF) * 256) + (stationInfo.getMsg().get(3) & 0xFF) == (msg.size() + stationInfo.getMsg().size() - 4)) {
                            stationInfo.getMsg().addAll(msg);
                            inisterData(stationInfo.getMsg(), stationInfo);
                            stationInfo.setMsg(null);
                        }
                    }
                    if (stationInfo.getVerMsg() != null && stationInfo.getVerMsg().size() > 4) {
                        if (((stationInfo.getVerMsg().get(2) & 0xFF) * 256) + (stationInfo.getVerMsg().get(3) & 0xFF) == (msg.size() + stationInfo.getVerMsg().size() - 4)) {
                            stationInfo.getVerMsg().addAll(msg);
                            initVer(stationInfo.getVerMsg(), stationInfo);
                            stationInfo.setVerMsg(null);
                            byte[] headData = new byte[]{0x02, 0x0a, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            session.write(headData);
                        }
                    }
                    if (stationInfo.getScanResultMsg() != null && stationInfo.getScanResultMsg().size() > 4) {
                        if (((stationInfo.getScanResultMsg().get(2) & 0xFF) * 256) + (stationInfo.getScanResultMsg().get(3) & 0xFF) == (msg.size() + stationInfo.getScanResultMsg().size() - 4)) {
                            stationInfo.getScanResultMsg().addAll(msg);
                            scanResult(stationInfo.getScanResultMsg(), stationInfo);
                            byte[] data = new byte[]{0x01, 0x0c, 0x00, 0x07, 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                            seqNo++;
                            session.write(data);
                        }
                    }
                    break;
                }
            }
        }


        @Override
        public void messageSent(IoSession session, Object message) {
            LETLog.d("messageSent :" + session.getRemoteAddress() + "： " + bytesToHexString((byte[]) message));
        }

        @Override
        public void sessionClosed(IoSession session) {
            Message message = Message.obtain();
            message.obj = session;
            message.what = SESSION_CLOSED;
            mSendHandler.sendMessage(message);
        }

        @Override
        public void sessionCreated(IoSession session) {
            LETLog.d("messageCreated :" + session.getRemoteAddress());
            boolean isContain = false;
            for (StationInfo stationInfo : App.get().getMList()) {
                if (TextUtils.equals(String.valueOf(session.getRemoteAddress()), stationInfo.getIp())) {
                    int i = stationInfo.getCreate();
                    stationInfo.setCreate(++i);
                    isContain = true;
                    stationInfo = App.get().initInfo(stationInfo.getId());
                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                    stationInfo.setSession(session);
                    EventBus.getDefault().post(new MessageEvent(true));
                }
            }
            if (!isContain) {
                StationInfo stationInfo = new StationInfo();
                stationInfo.setSession(session);
                stationInfo.setId(App.get().getStationId());
                stationInfo.setName("基站" + (App.get().getMList().size() + 1));
                stationInfo.setIp(String.valueOf(session.getRemoteAddress()), true);
                stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CRATE);
                stationInfo.setType(4);
                int i = stationInfo.getCreate();
                stationInfo.setCreate(++i);
                App.get().getMList().add(stationInfo);
                App.get().insert(stationInfo);
                EventBus.getDefault().post(new MessageEvent(true));
            }
        }

        @Override
        public void sessionOpened(IoSession session) {
            LETLog.d(TAG, "sessionOpened " + session.getRemoteAddress());
            for (StationInfo stationInfo : App.get().getMList()) {
                if (TextUtils.equals(String.valueOf(session.getRemoteAddress()), stationInfo.getIp())) {
                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                    stationInfo.setSession(session);
                    EventBus.getDefault().post(new MessageEvent(true));
                }
            }
        }
    }

    private void analysis(final ArrayList<Integer> msg, final StationInfo stationInfo) {
        mSendHandler.post(new Runnable() {
            @Override
            public void run() {
                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
                while (msg.size() > 4) {
                    int tag = msg.remove(4);
                    int tagLength = 0;
                    if (msg.size() > 6) {
                        tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                    }
                    ArrayList<Integer> list = new ArrayList<>();
                    while (tagLength > 0) {
                        tagLength--;
                        if (msg.size() > 4) {
                            list.add(msg.remove(4));
                        }
                    }
                    map.put(tag, list);
                }
                for (int i : map.keySet()) {
                    switch (i) {
                        case 21: {
                            if (map.get(i).size() > 1) {
                                stationInfo.setSupportBand(map.get(i).get(0));
                            }
                            break;
                        }
                        case 22: {
                            if (map.get(i).size() > 1) {
                                stationInfo.setTDDtype(map.get(i).get(0));
                            }
                            break;
                        }
                    }
                }

                App.get().insert(stationInfo);
            }
        });
    }

//    private void analysisPositionData(final StationInfo stationInfo, final ArrayList<Integer> msg) {
//        ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
//            @Override
//            public void run() {
//                mSendHandler.removeMessages(CHECK_POSITION);
//                Message message = Message.obtain();
//                message.obj = stationInfo;
//                message.what = CHECK_POSITION;
//                mSendHandler.sendMessageDelayed(message, 3 * 1000l);
//                stationInfo.setUpdateCellOpen(false);
//                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
//                while (msg.size() > 4) {
//                    int tag = msg.remove(4);
//                    int tagLength = 0;
//                    if (msg.size() > 6) {
//                        tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
//                    }
//                    ArrayList<Integer> list = new ArrayList<>();
//                    while (tagLength > 0) {
//                        tagLength--;
//                        if (msg.size() > 4) {
//                            list.add(msg.remove(4));
//                        }
//                    }
//                    map.put(tag, list);
//                }
//                TargetBean targetBean = new TargetBean();
//                isUpGrade = false;
//                if (stationInfo.getIp().length() > 16) {
//                    targetBean.setBbu("BBU" + stationInfo.getIp().substring(15, 16));
//                    if (TextUtils.equals(stationInfo.getIp().substring(15, 16), "1")) {
//                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
//                        for (StationInfo info : App.get().getMList()) {
//                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "2") || TextUtils.equals(info.getIp().substring(15, 16), "3"))) {
////                                setCloseDbm(info);
////                                info.setPositionOn(true);
//                                mSendHandler.removeMessages(POINT);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent("false",null,null));
//                            }
//                        }
//                    } else if (TextUtils.equals(stationInfo.getIp().substring(15, 16), "2")) {
//                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
//                        for (StationInfo info : App.get().getMList()) {
//                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "1") || TextUtils.equals(info.getIp().substring(15, 16), "3"))) {
////                                setCloseDbm(info);
////                                info.setPositionOn(true);
//                                mSendHandler.removeMessages(POINT);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph 20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent("false",null,null));
//                            }
//                        }
//                    } else if ( TextUtils.equals(stationInfo.getIp().substring(15, 16), "3")) {
//                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
//                        for (StationInfo info : App.get().getMList()) {
//                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "1") || TextUtils.equals(info.getIp().substring(15, 16), "2"))) {
////                                setCloseDbm(info);
////                                info.setPositionOn(true);
//                                mSendHandler.removeMessages(POINT);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent("false",null,null));
//                            }
//                        }
//                    } else if ( TextUtils.equals(stationInfo.getIp().substring(15, 16), "6")) {
//                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
//                        for (StationInfo info : App.get().getMList()) {
//                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "7"))) {
////                                setCloseDbm(info);
////                                info.setPositionOn(true);
//                                mSendHandler.removeMessages(POINT2);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent(null,null,"false"));
//                            }
//                        }
//                    } else if (TextUtils.equals(stationInfo.getIp().substring(15, 16), "7")) {
//                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
//                        for (StationInfo info : App.get().getMList()) {
//                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "6"))) {
////                                setCloseDbm(info);
////                                info.setPositionOn(true);
//                                mSendHandler.removeMessages(POINT1);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent(null,"false",null));
//                            }
//                        }
//                    }
//                }
//                targetBean.setPci(stationInfo.getCellConfig().getCell_pci() + "");
//                targetBean.setTime(formatTime(System.currentTimeMillis()));
//                for (int i : map.keySet()) {
//                    switch (i) {
//                        case 53: {
//                            byte[] bytes = new byte[map.get(i).size()];
//                            int j = 0;
//                            for (Integer integer : map.get(i)) {
//                                bytes[j] = integer.byteValue();
//                                j++;
//                            }
//                            targetBean.setImsi(hexStr2Str(bytesToHexString1(bytes)));
//                            break;
//                        }
//                        case 54: {
//                            byte[] bytes = new byte[map.get(i).size()];
//                            int j = 0;
//                            for (Integer integer : map.get(i)) {
//                                bytes[j] = integer.byteValue();
//                                j++;
//                            }
//                            targetBean.setDelay((bytesToInt4(bytes, 0)) + "");
//                            break;
//                        }
//                        case 55: {
//                            byte[] bytes = new byte[map.get(i).size()];
//                            int j = 0;
//                            for (Integer integer : map.get(i)) {
//                                bytes[j] = integer.byteValue();
//                                j++;
//                            }
//                            targetBean.setSinr((bytesToInt2(bytes, 0)));
//                            break;
//                        }
//                        case 70: {
//                            byte[] bytes = new byte[map.get(i).size()];
//                            int j = 0;
//                            for (Integer integer : map.get(i)) {
//                                bytes[j] = integer.byteValue();
//                                j++;
//                            }
//                            targetBean.setRsrp((bytesToInt4(bytes, 0)));
//                            break;
//                        }
//                        case 71: {
//                            byte[] bytes = new byte[map.get(i).size()];
//                            int j = 0;
//                            for (Integer integer : map.get(i)) {
//                                bytes[j] = integer.byteValue();
//                                j++;
//                            }
//                            targetBean.setFreq((bytesToInt4(bytes, 0)) + "");
//                            break;
//                        }
//                    }
//                }
//                EventBus.getDefault().post(new TargetListMessage(targetBean, false));
//            }
//        });
//    }



    private void analysisPositionData(final StationInfo stationInfo, final ArrayList<Integer> msg) {
        ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                mSendHandler.removeMessages(CHECK_POSITION);
                Message message = Message.obtain();
                message.obj = stationInfo;
                message.what = CHECK_POSITION;
                mSendHandler.sendMessageDelayed(message, 3 * 1000l);
                stationInfo.setUpdateCellOpen(false);
                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
                while (msg.size() > 4) {
                    int tag = msg.remove(4);
                    int tagLength = 0;
                    if (msg.size() > 6) {
                        tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                    }
                    ArrayList<Integer> list = new ArrayList<>();
                    while (tagLength > 0) {
                        tagLength--;
                        if (msg.size() > 4) {
                            list.add(msg.remove(4));
                        }
                    }
                    map.put(tag, list);
                }
                TargetBean targetBean = new TargetBean();
                //isUpGrade = false;
                if (stationInfo.getIp().length() > 16) {
                    targetBean.setBbu("BBU" + stationInfo.getIp().substring(15, 16));
                    if (TextUtils.equals(stationInfo.getIp().substring(15, 16), "1")) {
                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
                        mSendHandler.removeMessages(POINT);
                        //定位目标上来后，循环下配开关显示 关闭状态lph20190519
                        LETLog.d("移动循环关闭,TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
                        EventBus.getDefault().post(new MessageEvent("false",null,null));
                        isUpGrade = false;

                        for (StationInfo info : App.get().getMList()) {
                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "2") || TextUtils.equals(info.getIp().substring(15, 16), "3"))) {
//                                setCloseDbm(info);
//                                info.setPositionOn(true);

//                                mSendHandler.removeMessages(POINT);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent("false",null,null));
                            }
                        }



                    } else if (TextUtils.equals(stationInfo.getIp().substring(15, 16), "2")) {
                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
                        mSendHandler.removeMessages(POINT);
                        //定位目标上来后，循环下配开关显示 关闭状态lph20190519
                        LETLog.d("移动循环关闭,TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
                        EventBus.getDefault().post(new MessageEvent("false",null,null));
                        isUpGrade = false;

                        for (StationInfo info : App.get().getMList()) {
                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "1") || TextUtils.equals(info.getIp().substring(15, 16), "3"))) {
//                                setCloseDbm(info);
//                                info.setPositionOn(true);

//                                mSendHandler.removeMessages(POINT);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph 20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent("false",null,null));
                            }
                        }
                    } else if ( TextUtils.equals(stationInfo.getIp().substring(15, 16), "3")) {
                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
                        mSendHandler.removeMessages(POINT);
                        //定位目标上来后，循环下配开关显示 关闭状态lph20190519
                        LETLog.d("移动循环关闭,TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
                        EventBus.getDefault().post(new MessageEvent("false",null,null));
                        isUpGrade = false;

                        for (StationInfo info : App.get().getMList()) {
                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "1") || TextUtils.equals(info.getIp().substring(15, 16), "2"))) {
//                                setCloseDbm(info);
//                                info.setPositionOn(true);

//                                mSendHandler.removeMessages(POINT);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent("false",null,null));
                            }
                        }
                    } else if ( TextUtils.equals(stationInfo.getIp().substring(15, 16), "6")) {
                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
                        mSendHandler.removeMessages(POINT2);
                        //定位目标上来后，循环下配开关显示 关闭状态lph20190519
                        LETLog.d("电信循环关闭,TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
                        EventBus.getDefault().post(new MessageEvent(null,null,"false"));
                        isUpGrade2 = false;

                        for (StationInfo info : App.get().getMList()) {
                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "7"))) {
//                                setCloseDbm(info);
//                                info.setPositionOn(true);

//                                mSendHandler.removeMessages(POINT2);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent(null,null,"false"));
                            }
                        }
                    } else if (TextUtils.equals(stationInfo.getIp().substring(15, 16), "7")) {
                        App.get().isClose = true;//执行完关闭其他基带板后置为true；
                        mSendHandler.removeMessages(POINT1);
                        //定位目标上来后，循环下配开关显示 关闭状态lph20190519
                        LETLog.d("联通循环关闭,TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
                        EventBus.getDefault().post(new MessageEvent(null,"false",null));
                        isUpGrade1 = false;

                        for (StationInfo info : App.get().getMList()) {
                            if (info.getIp().length() > 16 && info.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED && (TextUtils.equals(info.getIp().substring(15, 16), "6"))) {
//                                setCloseDbm(info);
//                                info.setPositionOn(true);

//                                mSendHandler.removeMessages(POINT1);
//                                //定位目标上来后，循环下配开关显示 关闭状态lph20190519
//                                LETLog.d("TcpManager remove "+"BBU" + stationInfo.getIp().substring(15, 16));
//                                EventBus.getDefault().post(new MessageEvent(null,"false",null));
                            }
                        }
                    }
                }
                targetBean.setPci(stationInfo.getCellConfig().getCell_pci() + "");
                targetBean.setTime(formatTime(System.currentTimeMillis()));
                for (int i : map.keySet()) {
                    switch (i) {
                        case 53: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setImsi(hexStr2Str(bytesToHexString1(bytes)));
                            break;
                        }
                        case 54: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setDelay((bytesToInt4(bytes, 0)) + "");
                            break;
                        }
                        case 55: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setSinr((bytesToInt2(bytes, 0)));
                            break;
                        }
                        case 70: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setRsrp((bytesToInt4(bytes, 0)));
                            break;
                        }
                        case 71: {
                            byte[] bytes = new byte[map.get(i).size()];
                            int j = 0;
                            for (Integer integer : map.get(i)) {
                                bytes[j] = integer.byteValue();
                                j++;
                            }
                            targetBean.setFreq((bytesToInt4(bytes, 0)) + "");
                            break;
                        }
                    }
                }
                EventBus.getDefault().post(new TargetListMessage(targetBean, false));
            }
        });
    }







    private void redirectData(final StationInfo stationInfo, final ArrayList<Integer> msg) {
        ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                LETLog.d("redirectData:" + msg);
                Map<Integer, ArrayList<Integer>> map = new HashMap<>();
                while (msg.size() > 4) {
                    int tag = msg.remove(4);
                    int tagLength = 0;
                    if (msg.size() > 6) {
                        tagLength = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                    }
                    ArrayList<Integer> list = new ArrayList<>();
                    while (tagLength > 0) {
                        tagLength--;
                        if (msg.size() > 4) {
                            list.add(msg.remove(4));
                        }
                    }
                    map.put(tag, list);
                }
                for (int i : map.keySet()) {
                    if (i == 6) {
                        LETLog.d("redirectData:" + map.get(i).get(0));
                        EventBus.getDefault().post(new RedireectEvent(stationInfo, map.get(i).get(0)));
                    }
                }

            }
        });
    }

    public class CellUpDateRunable implements Runnable {
        private StationInfo stationInfo;

        private IoSession ioSession;

        public CellUpDateRunable(IoSession ioSession, StationInfo stationInfo) {
            this.ioSession = ioSession;
            this.stationInfo = stationInfo;
        }

        @Override
        public void run() {
            if (stationInfo.isUpdateCellOpen()) {
                int tac = stationInfo.getAddTAC();
                byte[] plmn = stationInfo.getCellConfig().getPlmnCmd();
                byte[] data = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                byte[] sendData = new byte[plmn.length + data.length];
                System.arraycopy(data, 0, sendData, 0, data.length);
                System.arraycopy(plmn, 0, sendData, 0 + data.length, plmn.length);
                LETLog.d("messageSent :" + "CellUpDateRunable： " + stationInfo.getIp() + ":   " + bytesToHexString((byte[]) sendData));
                ioSession.write(sendData);
                ThreadPoolManager.getInstance().execute(new FutureTask<Object>(this, null), (stationInfo.getCellConfig().getTac_cycle() * 1000L));//延时执行
            }
        }
    }

    private class CellUpDateRunable1 implements Runnable {
        private StationInfo stationInfo;

        private IoSession ioSession;

        public CellUpDateRunable1(IoSession ioSession, StationInfo stationInfo) {
            this.ioSession = ioSession;
            this.stationInfo = stationInfo;
        }

        @Override
        public void run() {
            int tac = stationInfo.getAddTAC();
            byte[] plmn = stationInfo.getCellConfig().getPlmnCmd();
            byte[] data = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
            byte[] sendData = new byte[plmn.length + data.length];
            System.arraycopy(data, 0, sendData, 0, data.length);
            System.arraycopy(plmn, 0, sendData, 0 + data.length, plmn.length);
            LETLog.d("messageSent :" + "CellUpDateRunable： " + stationInfo.getIp() + ":   " + bytesToHexString((byte[]) sendData));
            ioSession.write(sendData);
        }
    }

    private void upGradeCell(final StationInfo stationInfo) {
//        LETLog.d("messageSent :" + "自动配置： " + stationInfo.getmList().size());
        mSendHandler.post(new Runnable() {
            @Override
            public void run() {
                CellConfig cellConfig = stationInfo.getCellConfig();
                int rssi = 0;
//                LETLog.d("messageSent :" + "自动配置： " + stationInfo.getmList().size() + ":   "
//                        + rssi);
                for (ScanResult scanResult : stationInfo.getScanResults()) {
                    if (stationInfo.isShoudongSend()) {
//                        LETLog.d("messageSent :" + "自动配置： " + scanResult.getRSSI() + ":   "
//                                + rssi);
                        if (scanResult.getRSSI() != 65535) {
                            if (scanResult.getRSSI() > rssi) {
                                int a = scanResult.getPci() + 1;
                                cellConfig.setCell_pci(a);
//                                LETLog.d("messageSent :" + "自动配置： " + stationInfo.getIp() + ":   "
//                                        + stationInfo.getIp().substring(13, 16));
                                if (stationInfo.getIp() != null) {
                                    if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "211") ||
                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "212") ||
                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "213")) {
                                        cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                        cellConfig.setUplink_frequency_point(scanResult.getFrequency());
                                    } else if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "216") ||
                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "217")) {
                                        cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                        cellConfig.setUplink_frequency_point((scanResult.getFrequency() + 18000));

                                    }
                                } else {
                                    cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                    cellConfig.setUplink_frequency_point(scanResult.getFrequency());
                                }
                                rssi = scanResult.getRSSI();
                            }

                        }
                    }
                }
                if (stationInfo.isShoudongSend()) {
//                    LETLog.d("messageSent :" + "自动配置： " + cellConfig.getDownlink_frequency_point() + ":   "
//                            + cellConfig.getUplink_frequency_point());
                    int tac = stationInfo.getAddTAC();
                    byte[] plmn = cellConfig.getPlmnCmd();
                    byte[] data = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
                    byte[] sendData = new byte[plmn.length + data.length];
                    System.arraycopy(data, 0, sendData, 0, data.length);
                    System.arraycopy(plmn, 0, sendData, 0 + data.length, plmn.length);
//                    LETLog.d("messageSent :" + "自动配置： " + stationInfo.getIp() + ":   " + bytesToHexString((byte[]) sendData));
                    App.get().insert(stationInfo);
                    stationInfo.getSession().write(sendData);
                    stationInfo.setShoudongSend(false);
                }
            }
        });
    }

    private void initVer(final ArrayList<Integer> msg, final StationInfo stationInfo) {
        mSendHandler.post(new Runnable() {
            @Override
            public void run() {
                int softVerLength = msg.get(13);
                byte[] softVer = new byte[softVerLength];
                for (int i = 0; i < softVerLength; i++) {
                    softVer[i] = msg.get(i + 14).byteValue();
                }
                int Length1 = msg.get(14 + softVerLength + 2);
                byte[] softVer1 = new byte[Length1];
                for (int i = 0; i < Length1; i++) {
                    softVer1[i] = msg.get(i + 14 + softVerLength + 3).byteValue();
                }
                int Length2 = msg.get(14 + softVerLength + 3 + Length1 + 2);
                byte[] softVer2 = new byte[Length2];
                for (int i = 0; i < Length2; i++) {
                    softVer2[i] = msg.get(i + 14 + softVerLength + 3 + Length1 + 3).byteValue();
                }
                int Length3 = msg.get(14 + softVerLength + 3 + Length1 + 3 + Length2 + 2);
                byte[] softVer3 = new byte[Length3];
                for (int i = 0; i < Length3; i++) {
                    softVer3[i] = msg.get(i + 14 + softVerLength + 3 + Length1 + 3 + Length2 + 3).byteValue();
                }
                stationInfo.setSoftVer1(hexStr2Str(bytesToHexString1(softVer)));
                stationInfo.setSoftVer2(hexStr2Str(bytesToHexString1(softVer1)));
                stationInfo.setSoftVer3(hexStr2Str(bytesToHexString1(softVer2)));
                stationInfo.setSoftVer4(hexStr2Str(bytesToHexString1(softVer3)));
            }
        });
    }

    private void inisterData(final ArrayList<Integer> msg, final StationInfo stationInfo) {
        mSendHandler.post(new Runnable() {
            @Override
            public void run() {
                if (msg.get(15) == 28) {
                    for (int i = 18; i < (msg.size() - 29); i += 30) {
                        byte[] imsi = new byte[15];
                        for (int j = 0; j < 15; j++) {
                            imsi[j] = msg.get(i + j).byteValue();
                        }
                        MacData macData = new MacData();
                        ImsiData imsiData = new ImsiData();
                        imsiData.setStationName(stationInfo.getIp());
                        imsiData.setDeviceId(App.get().deviceId);
                        imsiData.setImsi(hexStr2Str(bytesToHexString1(imsi)));
                        imsiData.setId(++App.get().imsiId);
                        imsiData.setTime(System.currentTimeMillis());
                        macData.setImsi(hexStr2Str(bytesToHexString1(imsi)));
                        byte[] imei = new byte[15];
                        boolean isHasImei = false;
                        for (int j = 0; j < 15; j++) {
                            if (msg.get(15 + i + j) != 0) {
                                imei[j] = msg.get(15 + i + j).byteValue();
                                isHasImei = true;
                            } else {
                                imei[j] = 0;
                            }
                        }
                        if (isHasImei) {
                            macData.setMac(hexStr2Str(bytesToHexString1(imei)));
                            macData.setId(++App.get().imeiId);
                            macData.setTime(System.currentTimeMillis());
                            macData.setStationName(stationInfo.getIp());
                            EventBus.getDefault().post(new MessageEvent(macData));
                            imsiData.setImei(hexStr2Str(bytesToHexString1(imei)));
                        }
                        EventBus.getDefault().post(new MessageEvent(imsiData));
                    }
                }
            }
        });
    }

    private void scanResult(ArrayList<Integer> msg, StationInfo stationInfo) {
        mSendHandler.postDelayed(new RunAddScanResult(msg, stationInfo), 10);
    }

    private class RunAddScanResult implements Runnable {
        final ArrayList<Integer> msg;
        final StationInfo stationInfo;
        private int rssi = 0;

        RunAddScanResult(ArrayList<Integer> msg, StationInfo stationInfo) {
            this.msg = msg;
            this.stationInfo = stationInfo;
        }

        @Override
        public void run() {
            ArrayList<ArrayList<Integer>> mList = new ArrayList<>();
            while (msg.size() > 6) {
                int tag = msg.remove(4);
                int tagLeng = 0;
                if (msg.size() > 6) {
                    tagLeng = (msg.remove(4) & 0xFF) * 256 + msg.remove(4) & 0xFF;
                }
                if (tag == 11) {
                    ArrayList<Integer> list = new ArrayList<>();
                    while (tagLeng > 0) {
                        tagLeng--;
                        if (msg.size() > 4) {
                            list.add(msg.remove(4));
                        }
                    }
                    mList.add(list);
                } else {
                    while (tagLeng > 0) {
                        tagLeng--;
                        if (msg.size() > 4) {
                            msg.remove(4);
                        }
                    }
                }

            }
            CellConfig cellConfig = stationInfo.getCellConfig();
            int priority = -1;
            for (ArrayList<Integer> list : mList) {
                LETLog.d("scanResult :" + list.toString());
                if (list.size() > 7) {
                    ScanResult scanResult = new ScanResult();
                    scanResult.setTime(System.currentTimeMillis());
                    scanResult.setFrequency(((list.get(0) & 0xFF) * 256) + (list.get(1) & 0xFF));
                    scanResult.setPci(((list.get(2) & 0xFF) * 256) + (list.get(3) & 0xFF));
                    scanResult.setTAC(((list.get(4) & 0xFF) * 256) + (list.get(5) & 0xFF));
                    scanResult.setRSSI(((list.get(6) & 0xFF) * 256) + (list.get(7) & 0xFF));
                    if (list.size() > 9) {
                        scanResult.setPriority(((list.get(8) & 0xFF) * 256) + (list.get(9) & 0xFF));
                    }
                    if(scanResult.getTAC() != 65535 && scanResult.getFrequency() != 65535
                            && scanResult.getPci() != 65535 && scanResult.getRSSI() != 65535){
                        scanResult.setId(++App.get().scanResultId);
                        EventBus.getDefault().post(new MessageEvent(true));
                        App.get().updateScanResult(scanResult, stationInfo);
                        stationInfo.getmList().add(scanResult);
                        stationInfo.getScanResults().add(scanResult);
                    }
                    if (cellConfig != null && cellConfig.getConfigmode() == 0) {
//                        if (scanResult.getRSSI() != 65535) {
                        if (scanResult.getTAC() != 65535 && scanResult.getFrequency() != 65535
                                && scanResult.getPci() != 65535 && scanResult.getRSSI() != 65535) {
                            if (scanResult.getPriority() > priority) {
                                if (scanResult.getPci() != 65535) {
                                    if (scanResult.getPci() % 3 == 0) {
                                        cellConfig.setCell_pci(391);
                                    } else if (scanResult.getPci() % 3 == 1) {
                                        cellConfig.setCell_pci(392);
                                    } else if (scanResult.getPci() % 3 == 2) {
                                        cellConfig.setCell_pci(393);
                                    }

                                }
//                                if(scanResult.getTAC() != 65535){
//                                    cellConfig.setTac(scanResult.getTAC()+1);
//                                }
                                if (stationInfo.getIp() != null) {
                                    if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "211") ||
                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "212") ||
                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "213")) {
                                        if (scanResult.getFrequency() != 65535) {
                                            cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                            cellConfig.setUplink_frequency_point(scanResult.getFrequency());
                                        }
                                    } else if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "216") ||
                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "217")) {
                                        if (scanResult.getFrequency() != 65535) {
                                            cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                            cellConfig.setUplink_frequency_point((scanResult.getFrequency() + 18000));
                                        }
                                    }
                                } else {
                                    if (scanResult.getFrequency() != 65535) {
                                        cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                        cellConfig.setUplink_frequency_point(scanResult.getFrequency());
                                    }
                                }
                                priority = scanResult.getPriority();
                            } else if (scanResult.getPriority() == priority) {
                                if (scanResult.getRSSI() != 65535 && scanResult.getRSSI() > rssi) {
                                    if (scanResult.getPci() != 65535) {
                                        if (scanResult.getPci() % 3 == 0) {
                                            cellConfig.setCell_pci(391);
                                        } else if (scanResult.getPci() % 3 == 1) {
                                            cellConfig.setCell_pci(392);
                                        } else if (scanResult.getPci() % 3 == 2) {
                                            cellConfig.setCell_pci(393);
                                        }

                                    }
//                                    if(scanResult.getTAC() != 65535){
//                                        cellConfig.setTac((scanResult.getTAC()+1));
//                                    }
                                    if (stationInfo.getIp() != null) {
                                        if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "211") ||
                                                TextUtils.equals(stationInfo.getIp().substring(13, 16), "212") ||
                                                TextUtils.equals(stationInfo.getIp().substring(13, 16), "213")) {
                                            if (scanResult.getFrequency() != 65535) {
                                                cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                                cellConfig.setUplink_frequency_point(scanResult.getFrequency());
                                            }
                                        } else if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "216") ||
                                                TextUtils.equals(stationInfo.getIp().substring(13, 16), "217")) {
                                            if (scanResult.getFrequency() != 65535) {
                                                cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                                cellConfig.setUplink_frequency_point((scanResult.getFrequency() + 18000));
                                            }
                                        }
                                    } else {
                                        if (scanResult.getFrequency() != 65535) {
                                            cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
                                            cellConfig.setUplink_frequency_point(scanResult.getFrequency());
                                        }
                                    }
                                    rssi = scanResult.getRSSI();
                                }
                            }

                        }
//                        }
                        LETLog.d("messageSent", "---" + stationInfo.getmList().size());
//                    }
//                    if (stationInfo.isShoudongSend()) {
//                        stationInfo.getmList().add(scanResult);
//                        if (scanResult.getRSSI() != 65535) {
//                            if (scanResult.getRSSI() > rssi) {
//                                int a = scanResult.getPci() + 1;
//                                cellConfig.setCell_pci(a);
//                                if (stationInfo.getIp() != null) {
//                                    if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "211") ||
//                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "212") ||
//                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "213")) {
//                                        cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
//                                        cellConfig.setUplink_frequency_point(scanResult.getFrequency());
//                                    } else if (TextUtils.equals(stationInfo.getIp().substring(13, 16), "216") ||
//                                            TextUtils.equals(stationInfo.getIp().substring(13, 16), "217")) {
//                                        cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
//                                        cellConfig.setUplink_frequency_point((scanResult.getFrequency() + 18000));
//
//                                    }
//                                } else {
//                                    cellConfig.setDownlink_frequency_point(scanResult.getFrequency());
//                                    cellConfig.setUplink_frequency_point(scanResult.getFrequency());
//                                }
//                                rssi = scanResult.getRSSI();
//                            }
//
//                        }
//                    }
                    }
                }
            }
            if (cellConfig != null && cellConfig.getConfigmode() == 0) {
                App.get().insert(stationInfo);
            }
//            if(stationInfo.isShoudongSend()){
//                LETLog.d("messageSent :" + "自动配置： " + cellConfig.getDownlink_frequency_point() + ":   "
//                        + cellConfig.getUplink_frequency_point());
//                int tac = stationInfo.getAddTAC();
//                byte[] plmn = cellConfig.getPlmnCmd();
//                byte[] data = new byte[]{0x01, 17, 0x00, (byte) (12 + plmn.length), 14, 0x00, 0x02, (byte) ((tac >> 8) & 0xFF), (byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
//                byte[] sendData = new byte[plmn.length + data.length];
//                System.arraycopy(data, 0, sendData, 0, data.length);
//                System.arraycopy(plmn, 0, sendData, 0 + data.length, plmn.length);
//                LETLog.d("messageSent :" + "自动配置： " + stationInfo.getIp() + ":   " + bytesToHexString((byte[]) sendData));
//                App.get().insert(stationInfo);
//                stationInfo.getSession().write(sendData);
//                stationInfo.setShoudongSend(false);
//            }


//            int tag =0;
//            int tagLength = 0;
//            boolean upgradeTag = true;
//            for (int i = 4; i < msg.size(); i) {
//                if(upgradeTag){
//                    tag = msg.get(i);
//                    upgradeTag = false;
//                    tagLength = 0;
//                }else {
//                   tagLength += (msg.get(i) & 0xFF) * 256;
//                }
//                if(msg.size() > (19 + i * 11)){
//                    ScanResult scanResult = new ScanResult();
//                    scanResult.setId(++App.get().scanResultId);
//                    scanResult.setTime(System.currentTimeMillis());
//                    scanResult.setFrequency(((msg.get(12 + i * 11) & 0xFF) * 256) + (msg.get(13 + i * 11) & 0xFF));
//                    scanResult.setPci(((msg.get(14 + i * 11) & 0xFF) * 256) + (msg.get(15 + i * 11) & 0xFF));
//                    scanResult.setTAC(((msg.get(16 + i * 11) & 0xFF) * 256) + (msg.get(17 + i * 11) & 0xFF));
//                    scanResult.setRSSI(((msg.get(18 + i * 11) & 0xFF) * 256) + (msg.get(19 + i * 11) & 0xFF));
//                    EventBus.getDefault().post(new MessageEvent(true));
//                    App.get().updateScanResult(scanResult, stationInfo);
//                }
//            }

//            int num = msg.get(8);
//            CellConfig cellConfig = stationInfo.getCellConfig();
//            int rssi = 0;
//            for (int i = 0; i < num; i++) {
//                if(msg.size() > (19 + i * 11)){
//                    ScanResul msg.get(18 + i * 11) & 0xFF) * 256) + (msg.get(19 + i * 11) & 0xFF));
//                   if(scanResult.getRSSI() != 65535){
//                        if(scanResult.getRSSI() >rssi){
//                            int a = scanResult.getPci() +1;
//                            cellConfig.setCell_pci(a);
//                      }
//                        rssi = scanResult.getRSSI();
//                    }
//                    EventBus.getDefault().post(new MessageEvent(true));
//                    App.get().updateScanResult(scanResult, stationInfo);
//                }
//            }

        }

    }

    private byte[] getInitConfigCmd(ArrayList<Integer> msg, StationInfo stationInfo) {
        byte[] bindwith = new byte[]{27, 0x00, 0x01, (byte) stationInfo.getInitConfig().getBandwidth()};
        byte[] timeDelayField = new byte[]{46, 0x00, 0x04, (byte) ((stationInfo.getInitConfig().getTimeDelayField() >> 24) & 0xFF), (byte) ((stationInfo.getInitConfig().getTimeDelayField() >> 16) & 0xFF), (byte) ((stationInfo.getInitConfig().getTimeDelayField() >> 8) & 0xFF), (byte) (stationInfo.getInitConfig().getTimeDelayField() & 0xFF)};
        byte[] synchronousMode = new byte[]{14, 0x00, 0x01, (byte) stationInfo.getInitConfig().getSynchronousMode()};
        byte[] frequencyOffset = new byte[]{47, 0x00, 0x01, (byte) stationInfo.getInitConfig().getFrequencyOffset()};
        byte[] operatingBand = new byte[]{50, 0x00, 0x01, (byte) stationInfo.getInitConfig().getOperatingBand()};
        return new byte[]{0x02, 28, 0x00, 30, bindwith[0], bindwith[1], bindwith[2], bindwith[3],
                timeDelayField[0], timeDelayField[1], timeDelayField[2], timeDelayField[3], timeDelayField[4], timeDelayField[5], timeDelayField[6],
                synchronousMode[0], synchronousMode[1], synchronousMode[2], synchronousMode[3],
                frequencyOffset[0], frequencyOffset[1], frequencyOffset[2], frequencyOffset[3],
                operatingBand[0], operatingBand[1], operatingBand[2], operatingBand[3],
                msg.get(msg.size() - 7).byteValue(), msg.get(msg.size() - 7 + 1).byteValue(), msg.get(msg.size() - 7 + 2).byteValue(), msg.get(msg.size() - 7 + 3).byteValue(),
                msg.get(msg.size() - 7 + 4).byteValue(), msg.get(msg.size() + 5 - 7).byteValue(), 1};
    }

}
