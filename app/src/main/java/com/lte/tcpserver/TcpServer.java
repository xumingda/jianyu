package com.lte.tcpserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.App;
import com.communication.utils.Constant;
import com.communication.utils.LETLog;
import com.lte.data.CdmaConfig;
import com.lte.data.DataManager;
import com.lte.data.GsmConfig;
import com.lte.data.ImsiData;
import com.lte.data.MacData;
import com.lte.data.StationInfo;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.listener.TcpListener;
import com.lte.utils.Constants;
import com.lte.utils.ThreadPoolManager;
import com.lte.utils.ThreadUtils;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;


import static com.lte.utils.AppUtils.bytesToHexString;
import static com.lte.utils.AppUtils.bytesToHexString1;
import static com.lte.utils.AppUtils.hexStr2Str;
import static com.lte.utils.AppUtils.little_bytesToInt;

/**
 * Created by chenxiaojun on 2017/8/28.
 */

public class TcpServer extends Service {

    private final static String Tag = "TcpServer";

    private TcpListener listener;

    private final IBinder mBinder = new LocalBinder();

    private NioSocketAcceptor acceptor;
    private DatagramSocket mGsmSendSocket;
    private Runnable gsmRun;
    private boolean isStop;
    private DatagramSocket mGsmReceiveSocket;
    private Runnable gsmSendRunnable;
    static Handler mGsmHandler = new Handler();
    private Runnable cmdaRun;
    private DatagramPacket cmdaRecvPacket;
    private DatagramSocket mCmdaReceiveSocket;
    private int nowCmdaSendCmd;
    private final List<byte[]> mCmdaMsgList = new ArrayList<>();
    ;
    private Runnable cmdaSendRunnable;
    private DatagramSocket mCmdaSendSocket;
    static Handler mCmdaHandler = new Handler();
    private Long cmdaTime = 0L;

    private Long gsmTime = 0L;

    private byte[] cmdaQueryBytes = new byte[]{(byte) 153, 0, 0, 0, (byte) ++App.get().udpNo, 1, 0, 0, 0x0b, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 02,
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b,
            0x10, 1, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 0x11, 1, 0, 0, 0, 0, 0, 0, 0,
            0, 0x0b, 0x12, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0x0b, 0x13, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0x0b, 0x14, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0x0b, 0x19, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 4, 0x0b, 1, 0,
            4, 0x0a, 1, 0, 7, 0x0c, 1, 0, 0, 0,
            0, 0x0b, 0x51, 1, 0, 0, 0, 0, 0,
            0, 0, 0, 0x0b, 0x52, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 5, 0x56, 1, 0, 0, 5,
            0x57, 1, 0, 0, 5, 0x58, 1, 0, 0, 5,
            0x59, 1, 0, 0};

    private byte[] gsmBytes = new byte[]{(byte) 0x88, 00, 00, 00, (byte) ++App.get().udpNo, 01, 0, 0, 0x0b, 01, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 02, 01, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 03, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 04, 01, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 06, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            04, 0x0b, 01, 00, 04, 0x0a, 01, 00, 07, 0x0c, 01, 0, 0, 0, 0, 0x0b, 0x50, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 0x51, 01, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 0x52, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            05, 0x56, 01, 01, 00, 05, 0x57, 01, 0x5f, 00, 05, 0x58, 01, 00, 02, 05, 0x59, 01, 0x7c, 02, 05, 0x5f,
            01, 00, 00};
    private byte[] gsmBytes1 = new byte[]{(byte) 0x88, 00, 00, 00, (byte) ++App.get().udpNo, 01, 1, 0, 0x0b, 01, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 02, 01, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 03, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 04, 01, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 06, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            04, 0x0b, 01, 00, 04, 0x0a, 01, 00, 07, 0x0c, 01, 0, 0, 0, 0, 0x0b, 0x50, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            0x0b, 0x51, 01, 0, 0, 0, 0, 0, 0, 0, 0, 0x0b, 0x52, 01, 0, 0, 0, 0, 0, 0, 0, 0,
            05, 0x56, 01, 01, 00, 05, 0x57, 01, 0x5f, 00, 05, 0x58, 01, 00, 02, 05, 0x59, 01, 0x7c, 02, 05, 0x5f,
            01, 00, 00};
    private final byte[] dataClose0 = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
    private final byte[] dataClose1 = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 1, 0};
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private boolean isFirst1 = true;
    private final byte[] dataOpen0 = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 0, 0};
    private final byte[] dataOpen1 = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 5, 1, 0};
    private boolean isSetUp1;
    private boolean isSetUp2;
    private final byte[] dataScan0 = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 03, 0, 0};
    private final byte[] dataScan1 = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 03, 1, 0};

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void addQueryGsmMsg() {
        gsmConfig = DataManager.getInstance().findGsmConfigFrist();
        if (!mGsmMsgList.contains(gsmBytes)) {
            mGsmMsgList.add(gsmBytes);
        }
        if (!mGsmMsgList.contains(gsmBytes1)) {
            mGsmMsgList.add(gsmBytes1);
        }
    }

    public void openGsm(boolean on) {

    }

    public void openCdma(boolean on) {

    }

    public void sendCmdaUdpMsg(byte[] cmd1) {
        mCmdaMsgList.add(cmd1);
    }

    public void addCmdaQueryMsg() {
        mCmdaMsgList.add(cmdaQueryBytes);
    }

    public void test(byte[] data) {
        udpGsmAnalysis(data);
    }

    public void setGsmSetting() {
        if (!mGsmMsgList.contains(dataOpen0)) {
            mGsmMsgList.add(dataOpen0);
        }
        if (!mGsmMsgList.contains(dataOpen1)) {
            mGsmMsgList.add(dataOpen1);
        }
        if (!mCmdaMsgList.contains(dataOpen0)) {
            mCmdaMsgList.add(dataOpen0);
        }
    }

    public void setGsmClose() {
        mCmdaMsgList.add(new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0});
        mGsmMsgList.add(new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0});
        mGsmMsgList.add(new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 1, 0});
    }

    class LocalBinder extends Binder {
        TcpServer getService() {
            return TcpServer.this;
        }
    }

    public void setListener(final TcpListener listener) {
        this.listener = listener;
        Log.d(Tag, Tag + "setListener" + (listener == null));
        Log.d(Tag, Tag + "onCreate1" + (listener == null));
        acceptor = new NioSocketAcceptor();

        DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
        // 添加编码过滤器 处理乱码、编码问题
        filterChain.addLast("codec", new ProtocolCodecFilter(new CharsetCodeFactory()));

        acceptor.setReuseAddress(true);

        //为接收器设置管理服务（核心处理）
        acceptor.setHandler(new MyServerHandler(listener));

        try {
            acceptor.bind(new InetSocketAddress(Constants.TCP_PORT));
            LETLog.d("bind ：" + Constants.TCP_PORT + "成功");
            EventBus.getDefault().post(new MessageEvent("绑定 ：" + Constants.TCP_PORT + "成功"));

        } catch (IOException e) {
            LETLog.d("bind ：" + Constants.TCP_PORT + e.getLocalizedMessage());
            EventBus.getDefault().post(new MessageEvent("绑定 ：" + Constants.TCP_PORT + "失败，端口被占用"));
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        pm = (PowerManager) App.get().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myservice");
        wl.acquire();
        startGsmUdpService();
        startCdmaUdpService();
    }

    CdmaConfig cdmaConfig;

    public void upGradeGsmConfig(GsmConfig gsmConfig) {
        this.gsmConfig = gsmConfig;
    }

    public void upGradeCmdaConfig(CdmaConfig cdmaConfig) {
        this.cdmaConfig = cdmaConfig;
    }

    private void startCdmaUdpService() {
        LETLog.d("startCdmaUdpService ");
        try {
            mCmdaSendSocket = new DatagramSocket(Constant.CMDA_SEDN_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mCmdaMsgList.add(cmdaQueryBytes);
        cmdaTime = System.currentTimeMillis();
        cdmaConfig = DataManager.getInstance().findCdmaConfigFrist();
        if (cdmaConfig != null) {
            mCmdaMsgList.add(cdmaConfig.cmd1);
        }
        byte[] data = new byte[]{8, 00, 00, 00, (byte) ++App.get().udpNo, 6, 0, 0};
        mCmdaMsgList.add(data);
        cmdaSendRunnable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - cmdaTime > 210 * 1000L) {
                    for (StationInfo stationInfo : App.get().getMList()) {
                        if (stationInfo.getType() == 23) {
                            if (stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                                stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                EventBus.getDefault().post(new MessageEvent(true));
                                if (!mCmdaMsgList.contains(cmdaQueryBytes)) {
                                    mCmdaMsgList.add(cmdaQueryBytes);
                                }
                            }

                        }
                    }
                }
                DatagramPacket sendPacket = null;
                try {
                    synchronized (mCmdaMsgList) {
                        if (mCmdaMsgList.size() > 0) {
                            byte[] sendByte = mCmdaMsgList.get(0);
                            sendPacket = new DatagramPacket(sendByte, sendByte.length, InetAddress.getByName("192.168.178.203"), Constant.CMDA_SEDN_PORT);
                            if (null != mCmdaSendSocket && !mCmdaSendSocket.isClosed()) {
                                try {
                                    nowCmdaSendCmd = sendByte[5];
                                    mCmdaSendSocket.send(sendPacket);
//                                    LETLog.d("---udp sendData ：cmda :" + bytesToHexString(sendByte));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ThreadPoolManager.getInstance().execute(new FutureTask<Object>(this, null), 1000 * 10L);//延时执行
            }
        };
        ThreadPoolManager.getInstance().execute(new FutureTask<Object>(cmdaSendRunnable, null), 1000 * 5L);//延时执行
        cdmaInit();
    }

    byte[] data;

    private void cdmaInit() {
        ThreadUtils.getThreadPoolProxy().removeTask(cmdaRun);
        byte[] buff = new byte[1024];
        cmdaRecvPacket = new DatagramPacket(buff, buff.length);
        long receiveTime = SystemClock.elapsedRealtime();
        if (mCmdaReceiveSocket == null) {
            try {
                mCmdaReceiveSocket = new DatagramSocket(Constant.CMDA_RECEIVE_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        cmdaRun = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mCmdaReceiveSocket != null) {
                            synchronized (mCmdaReceiveSocket) {
                                if (mCmdaReceiveSocket != null && !mCmdaReceiveSocket.isClosed()) {
                                    mCmdaReceiveSocket.receive(cmdaRecvPacket);
                                    if (cmdaRecvPacket.getData().length > 0) {
                                        data = new byte[(cmdaRecvPacket.getData()[0] & 0xFF)];
                                        System.arraycopy(cmdaRecvPacket.getData(), 0, data, 0, (cmdaRecvPacket.getData()[0] & 0xFF));
                                    }
                                    LETLog.d("---udp recvice ：cmda :" + bytesToHexString(data));
                                    if (data.length > 6) {
                                        for (StationInfo stationInfo : App.get().getMList()) {
                                            if (stationInfo.getType() == 23) {
                                                stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                                                EventBus.getDefault().post(new MessageEvent(true));
                                            }
                                        }
                                        cmdaTime = System.currentTimeMillis();
                                        if (data[5] == 0x11) {
                                            if (nowCmdaSendCmd == 0x01) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.contains(cmdaQueryBytes)) {
                                                        mCmdaMsgList.remove(cmdaQueryBytes);
                                                    }
                                                }
                                            }
                                            udpCmdaAnalysis(data);
                                        } else if (data[5] == 0x12) {
                                            if (nowCmdaSendCmd == 0x02) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.size() > 0) {
                                                        mCmdaMsgList.remove(0);
                                                    }
                                                }
                                            }
                                        } else if (data[5] == 0x13) {
                                            if (nowCmdaSendCmd == 0x03) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.size() > 0) {
                                                        mCmdaMsgList.remove(0);
                                                    }
                                                }
                                            }
                                        } else if (data[5] == 0x15) {
                                            if (nowCmdaSendCmd == 0x05) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.size() > 0) {
                                                        mCmdaMsgList.remove(0);
                                                    }
                                                }
                                            }
                                        } else if (data[5] == 0x16) {
                                            if (nowCmdaSendCmd == 0x06) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.size() > 0) {
                                                        mCmdaMsgList.remove(0);
                                                    }
                                                }
                                            }
                                        } else if (data[5] == 0x17) {
                                            if (nowCmdaSendCmd == 0x07) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.size() > 0) {
                                                        mCmdaMsgList.remove(0);
                                                    }
                                                }
                                            }
                                            mCmdaMsgList.add(cmdaQueryBytes);
                                        } else if (data[5] == 0x19) {
                                            if (nowCmdaSendCmd == 0x09) {
                                                synchronized (mCmdaMsgList) {
                                                    if (mCmdaMsgList.size() > 0) {
                                                        mCmdaMsgList.remove(0);
                                                    }
                                                }
                                            }
                                        } else if (data[5] == 0x18) {
                                            udpCmdaAnalysis(data);
                                        }

                                    }
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ThreadUtils.getThreadPoolProxy().execute(cmdaRun);
    }

    private void udpCmdaAnalysis(final byte[] data) {
        mCmdaHandler.post(new Runnable() {
            @Override
            public void run() {
                Map<String, byte[]> map = new HashMap<>();
                byte[] data1 = new byte[(data[0] & 0xFF) - 8];
                System.arraycopy(data, 8, data1, 0, (data[0] & 0xFF) - 8);
                while (data1.length > 3) {
                    StringBuilder dataKey = new StringBuilder();
                    String sTemp = Integer.toHexString(0xFF & data1[2]);
                    if (sTemp.length() < 2)
                        dataKey.append(0);
                    dataKey.append(sTemp.toLowerCase());
                    String sTemp1 = Integer.toHexString(0xFF & data1[1]);
                    if (sTemp1.length() < 2)
                        dataKey.append(0);
                    dataKey.append(sTemp1.toLowerCase());
                    byte length = data1[0];
                    if (data1.length < length) {
                        break;
                    } else {
                        if (length > 3) {
                            byte[] dataValue = new byte[length - 3];
                            System.arraycopy(data1, 3, dataValue, 0, length - 3);
                            map.put(dataKey.toString(), dataValue);
                            int length1 = data1.length - length;
                            byte[] dataTemp = new byte[length1];
                            System.arraycopy(data1, length, dataTemp, 0, length1);
                            data1 = dataTemp;
                        }
                    }
                }
                if (data[5] == 0x11) {
                    StationInfo stationInfo = null;
                    for (StationInfo info : App.get().getMList()) {
                        if (info.getType() == 23) {
                            stationInfo = info;
                            break;
                        }
                    }
                    if (stationInfo == null) {
                        stationInfo = new StationInfo();
                        stationInfo.setId(App.get().getStationId());
                        stationInfo.setName("基站" + (App.get().getMList().size() + 1));
                        stationInfo.setIp("192.168.178.203", true);
                        stationInfo.setType(23);
                        App.get().getMList().add(stationInfo);
                        App.get().insert(stationInfo);
                    }
                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                    for (String s : map.keySet()) {
                        switch (s) {
                            case "0101":
                                stationInfo.setMCC(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                cdmaConfig.MCC = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                break;
                            case "0102":
                                stationInfo.setMNC(hexStr2Str(bytesToHexString1(map.get(s))).trim());
//                                cdmaConfig.MNC = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                break;
                            case "0104":
                                stationInfo.setCI(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "010b":
                                stationInfo.setWorkModel(map.get(s)[0]);
                                break;
                            case "010a":
                                stationInfo.setconfigModel(map.get(s)[0]);
                                break;
                            case "010c":
                                stationInfo.setRecaptureTime(little_bytesToInt(map.get(s)));
                                cdmaConfig.CAPTIME = little_bytesToInt(map.get(s)) + "";
                                break;
                            case "0150":
                                stationInfo.setCarrierFrequencyPoint(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0151":
                                stationInfo.setDownlinkAttenuation(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0152":
                                stationInfo.setUplinkAttenuation(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0156":
                                stationInfo.setStartingFrequencyPoint1(little_bytesToInt(map.get(s)));
                                break;
                            case "0157":
                                stationInfo.setEndFrequencyPoint1(little_bytesToInt(map.get(s)));
                                break;
                            case "0158":
                                stationInfo.setStartingFrequencyPoint2(little_bytesToInt(map.get(s)));
                                break;
                            case "0159":
                                stationInfo.setEndFrequencyPoint2(little_bytesToInt(map.get(s)));
                                break;
                            case "0110":
                                stationInfo.setPn(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0111":
                                stationInfo.setSid(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0112":
                                stationInfo.setNid(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0113":
                                stationInfo.setBsid(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0114":
                                stationInfo.setCmda(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0119":
                                stationInfo.setWorkModel2(little_bytesToInt(map.get(s)));

                                break;

                        }
                    }
                    EventBus.getDefault().post(new MessageEvent(true));
                } else if (data[5] == 0x18) {
                    ImsiData imsiData = new ImsiData();
                    imsiData.setId(++App.get().imsiId);
                    imsiData.setDeviceId(App.get().deviceId);
                    imsiData.setTime(System.currentTimeMillis());
                    imsiData.setBbu("10");
                    for (String s : map.keySet()) {
                        switch (s) {
                            case "0211":
                                imsiData.setImsi(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0213":
                                imsiData.setTmsi(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0214":
                                imsiData.setEsn(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                        }
                    }
                    App.get().insert(imsiData);
                    EventBus.getDefault().post(new MessageEvent(imsiData));
                }
            }
        });
    }

    public void sendMsg(String ip, byte[] data) {
        for (IoSession ioSession : acceptor.getManagedSessions().values()) {
            if (TextUtils.equals(ip, ioSession.getRemoteAddress().toString())) {
                ioSession.write(data);
            }
        }
    }

    public void sendMsg(byte[] data) {
        for (IoSession ioSession : acceptor.getManagedSessions().values()) {
            ioSession.write(data);
        }
    }

    boolean is = false;
    private List<byte[]> mGsmMsgList = new ArrayList<>();
    private int nowGsmSendCmd;
    GsmConfig gsmConfig;

    public void startGsmUdpService() {
        try {
            mGsmSendSocket = new DatagramSocket(Constant.SEDN_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (!mGsmMsgList.contains(gsmBytes)) {
            mGsmMsgList.add(gsmBytes);
        }
        if (!mGsmMsgList.contains(gsmBytes1)) {
            mGsmMsgList.add(gsmBytes1);
        }
        isSend1 = false;
        isSend2 = false;
        gsmTime = System.currentTimeMillis();
        gsmConfig = DataManager.getInstance().findGsmConfigFrist();
        gsmSendRunnable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - gsmTime > 210 * 1000L) {
                    for (StationInfo stationInfo : App.get().getMList()) {
                        if (stationInfo.getType() == 21 || stationInfo.getType() == 22) {
                            if (stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED) {
                                stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                                EventBus.getDefault().post(new MessageEvent(true));
                                if (!mGsmMsgList.contains(gsmBytes)) {
                                    mGsmMsgList.add(gsmBytes);
                                    isSend1 = false;
                                    isFirst = true;
                                }
                                if (!mGsmMsgList.contains(gsmBytes1)) {
                                    mGsmMsgList.add(gsmBytes1);
                                    isSend2 = false;
                                    isFirst1 = true;
                                }
                            }
                        }
                    }
                }
                DatagramPacket sendPacket = null;
                try {
                    synchronized (mGsmMsgList) {
                        if (mGsmMsgList.size() > 0) {
                            byte[] sendByte = mGsmMsgList.get(0);
                            sendPacket = new DatagramPacket(sendByte, sendByte.length, InetAddress.getByName("192.168.178.202"), Constant.SEDN_PORT);
                            if (null != mGsmSendSocket && !mGsmSendSocket.isClosed()) {
                                try {
                                    nowGsmSendCmd = sendByte[5];
                                    LETLog.d("---udp sendData ：gsm :" + bytesToHexString(sendByte));
                                    mGsmSendSocket.send(sendPacket);
                                    if (nowGsmSendCmd == 0x03) {
                                        if (mGsmMsgList.size() > 0) {
                                            mGsmMsgList.remove(0);
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ThreadPoolManager.getInstance().execute(new FutureTask<Object>(this, null), 1000 * 3L);//延时执行

            }
        };
        ThreadPoolManager.getInstance().execute(new FutureTask<Object>(gsmSendRunnable, null), 1000 * 5L);//延时执行
        gsmInit();
    }


    private byte[] gsmData;

    public void sendGsmUdpMsg(final byte[] data) {
        mGsmMsgList.add(data);
    }

    DatagramPacket gsmRecvPacket;

    boolean isScan;
    boolean isSend1;
    boolean isSend2;

    boolean isSetUpComplete1;
    boolean isSetUpComplete2;

    boolean isFirst = true;

    public void gsmInit() {
        ThreadUtils.getThreadPoolProxy().removeTask(gsmRun);
        byte[] buff = new byte[1024];
        gsmRecvPacket = new DatagramPacket(buff, buff.length);
        long receiveTime = SystemClock.elapsedRealtime();
        if (mGsmReceiveSocket == null) {
            try {
                mGsmReceiveSocket = new DatagramSocket(Constant.RECEIVE_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        isFirst = true;
        gsmRun = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mGsmReceiveSocket != null) {
                            synchronized (mGsmReceiveSocket) {
                                if (mGsmReceiveSocket != null && !mGsmReceiveSocket.isClosed()) {
                                    mGsmReceiveSocket.receive(gsmRecvPacket);
                                    if (gsmRecvPacket.getData().length > 0) {
                                        gsmData = new byte[(gsmRecvPacket.getData()[0] & 0xFF)];
                                        System.arraycopy(gsmRecvPacket.getData(), 0, gsmData, 0, (gsmRecvPacket.getData()[0] & 0xFF));
                                    }
                                    LETLog.d("---recvice sendData ：gsm :" + bytesToHexString(gsmData));
                                    if (gsmData.length > 6) {
                                        for (StationInfo stationInfo : App.get().getMList()) {
                                            if (stationInfo != null && (stationInfo.getType() == 21 || stationInfo.getType() == 22)) {
                                                if (stationInfo.getConnectionStatus() != StationInfo.ConnectionStatus.SCAN) {
                                                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                                                    EventBus.getDefault().post(new MessageEvent(true));
                                                    if(!isSetUp1 || !isSetUp2){
                                                        stationInfo.setIsCellConfig(true);
                                                    }
                                                }
                                            }
                                        }
                                        if (gsmData[5] == 0x11) {
                                            if (nowGsmSendCmd == 0x01) {
                                                synchronized (mGsmMsgList) {
                                                    if (mGsmMsgList.size() > 0) {
                                                        mGsmMsgList.remove(0);
                                                    }
                                                }
                                            }
                                            if (gsmData[6] == 0) {
                                                if (isSetUp1) {
                                                    isSetUp1 = false;
                                                    if (!mGsmMsgList.contains(dataClose0)) {
                                                        mGsmMsgList.add(dataClose0);
                                                        for (StationInfo stationInfo : App.get().getMList()) {
                                                            if (stationInfo != null && (stationInfo.getType() == 21 || stationInfo.getType() == 22)) {
                                                                stationInfo.setIsCellConfig(true);
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (gsmData[6] == 1) {
                                                if (isSetUp2) {
                                                    isSetUp2 = false;
                                                    if (!mGsmMsgList.contains(dataClose1)) {
                                                        mGsmMsgList.add(dataClose1);
                                                        for (StationInfo stationInfo : App.get().getMList()) {
                                                            if (stationInfo != null && (stationInfo.getType() == 21 || stationInfo.getType() == 22)) {
                                                                stationInfo.setIsCellConfig(true);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            udpGsmAnalysis(gsmData);
                                            gsmTime = System.currentTimeMillis();
                                        } else if (gsmData[5] == 0x12) {
                                            if (nowGsmSendCmd == 0x02) {
                                                synchronized (mGsmMsgList) {
                                                    if (mGsmMsgList.size() > 0) {
                                                        mGsmMsgList.remove(0);
                                                    }
                                                }
                                            }

                                            if (gsmData[6] == 0) {
//                                                if (isSetUp1) {
//                                                    isSetUp1 = false;
//                                                    if (!mGsmMsgList.contains(dataClose0)) {
//                                                        mGsmMsgList.add(dataClose0);
//                                                    }
//                                                } else {
                                                isScan = false;
                                                isSetUpComplete1 = true;
                                                if (!mGsmMsgList.contains(gsmBytes)) {
                                                    mGsmMsgList.add(gsmBytes);
                                                }
//                                                }
                                            } else if (gsmData[6] == 1) {
//                                                if (isSetUp2) {
//                                                    isSetUp2 = false;
//                                                    if (!mGsmMsgList.contains(dataClose1)) {
//                                                        mGsmMsgList.add(dataClose1);
//                                                    }
//                                                } else {
                                                isScan = false;
                                                isSetUpComplete2 = true;
                                                if (!mGsmMsgList.contains(gsmBytes1)) {
                                                    mGsmMsgList.add(gsmBytes1);
                                                }
//                                                }
                                            }
                                            gsmTime = System.currentTimeMillis();
                                        } else if (gsmData[5] == 0x13) {
                                            if (gsmData[6] == 0) {
                                                for (StationInfo stationInfo : App.get().getMList()) {
                                                    if (stationInfo.getType() == 21 || stationInfo.getType() == 22) {
                                                        stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.SCAN);
                                                        EventBus.getDefault().post(new MessageEvent(true));
                                                    }
                                                }
                                            } else if (gsmData[6] == 1) {
                                                for (StationInfo stationInfo : App.get().getMList()) {
                                                    if (stationInfo.getType() == 21 || stationInfo.getType() == 22) {
                                                        stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.SCAN);
                                                        EventBus.getDefault().post(new MessageEvent(true));
                                                    }
                                                }
                                            }
                                            gsmTime = System.currentTimeMillis() + 180 * 1000L;
                                        } else if (gsmData[5] == 0x15) {
                                            if (nowGsmSendCmd == 0x05) {
                                                synchronized (mGsmMsgList) {
                                                    if (mGsmMsgList.size() > 0) {
                                                        mGsmMsgList.remove(0);
                                                    }
                                                }
                                            }
                                            gsmTime = System.currentTimeMillis();
                                        } else if (gsmData[5] == 0x16) {
                                            if (nowGsmSendCmd == 0x06) {
                                                synchronized (mGsmMsgList) {
                                                    if (mGsmMsgList.size() > 0) {
                                                        mGsmMsgList.remove(0);
                                                    }
                                                }
                                            }
                                            if (gsmData[6] == 0) {
                                                if (mGsmMsgList.contains(dataClose0)) {
                                                    mGsmMsgList.remove(dataClose0);
                                                }
                                            } else if (gsmData[6] == 1) {
                                                if (mGsmMsgList.contains(dataClose1)) {
                                                    mGsmMsgList.remove(dataClose1);
                                                }
                                            }
                                            gsmTime = System.currentTimeMillis();
                                        } else if (gsmData[5] == 0x17) {
                                            if (nowGsmSendCmd == 0x07) {
                                                synchronized (mGsmMsgList) {
                                                    if (mGsmMsgList.size() > 0) {
                                                        mGsmMsgList.remove(0);
                                                    }
                                                }
                                            }
                                            isSend1 = false;
                                            isSend2 = false;
                                            if (!mGsmMsgList.contains(gsmBytes)) {
                                                mGsmMsgList.add(gsmBytes);
                                            }
                                            if (!mGsmMsgList.contains(gsmBytes1)) {
                                                mGsmMsgList.add(gsmBytes1);
                                            }
                                        } else if (gsmData[5] == 0x19) {
                                            if (nowGsmSendCmd == 0x09) {
                                                synchronized (mGsmMsgList) {
                                                    if (mGsmMsgList.size() > 0) {
                                                        mGsmMsgList.remove(0);
                                                    }
                                                }
                                            }
                                        } else if (gsmData[5] == 0x18) {
                                            udpGsmAnalysis(gsmData);
                                            gsmTime = System.currentTimeMillis();
                                        } else if (gsmData[5] == 0x1e) {
                                            synchronized (mGsmMsgList) {
                                                if (mGsmMsgList.size() > 0) {
                                                    mGsmMsgList.remove(0);
                                                }
                                            }
                                            if (gsmData.length == 8) {
                                                LETLog.d(" gsm  0x1e" + bytesToHexString(gsmData));
                                                for (StationInfo stationInfo : App.get().getMList()) {
                                                    if (stationInfo.getType() == 21) {
                                                        if (stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.SCAN) {
                                                            Message message = Message.obtain();
                                                            message.what = 1;
                                                            message.obj = stationInfo;
                                                            mHandler.sendMessageDelayed(message,30*1000L);
                                                            mGsmMsgList.add(gsmConfig.cmd3);
                                                            isScan = false;
                                                        }
                                                    } else if (stationInfo.getType() == 22) {
                                                        if (stationInfo.getConnectionStatus() == StationInfo.ConnectionStatus.SCAN) {
                                                            Message message = Message.obtain();
                                                            message.what = 2;
                                                            message.obj = stationInfo;
                                                            mHandler.sendMessageDelayed(message,30*1000L);
                                                            mGsmMsgList.add(gsmConfig.cmd4);
                                                            isScan = false;
                                                        }
                                                    }
                                                }
                                            }
                                            gsmTime = System.currentTimeMillis() + 180 * 1000L;
                                        } else if (gsmData[5] == 0x1f) {
                                            gsmTime = System.currentTimeMillis();
                                        }

                                    }
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ;
        ThreadUtils.getThreadPoolProxy().execute(gsmRun);

    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    StationInfo stationInfo = (StationInfo) msg.obj;
                    stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                    EventBus.getDefault().post(new MessageEvent(true));
                    break;
                case 2:
                    StationInfo stationInfo1 = (StationInfo) msg.obj;
                    stationInfo1.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                    EventBus.getDefault().post(new MessageEvent(true));
                    break;
            }
        }
    };
    private void udpGsmAnalysis(final byte[] data) {
        mGsmHandler.post(new Runnable() {

            @Override
            public void run() {
                Map<String, byte[]> map = new HashMap<>();
                byte[] data1 = new byte[(data[0] & 0xFF) - 8];
                System.arraycopy(data, 8, data1, 0, (data[0] & 0xFF) - 8);
                while (data1.length > 3) {
                    StringBuilder dataKey = new StringBuilder();
                    String sTemp = Integer.toHexString(0xFF & data1[2]);
                    if (sTemp.length() < 2)
                        dataKey.append(0);
                    dataKey.append(sTemp.toLowerCase());
                    String sTemp1 = Integer.toHexString(0xFF & data1[1]);
                    if (sTemp1.length() < 2)
                        dataKey.append(0);
                    dataKey.append(sTemp1.toLowerCase());
                    byte length = data1[0];
                    if (data1.length < length) {
                        break;
                    } else {
                        if (length > 3) {
                            byte[] dataValue = new byte[length - 3];
                            System.arraycopy(data1, 3, dataValue, 0, length - 3);
                            map.put(dataKey.toString(), dataValue);
                            int length1 = data1.length - length;
                            byte[] dataTemp = new byte[length1];
                            System.arraycopy(data1, length, dataTemp, 0, length1);
                            data1 = dataTemp;
                        }
                    }
                }
                if (data[5] == 0x11) {
                    StationInfo stationInfo = null;
                    if (data[6] == 0x00) {
                        for (StationInfo info : App.get().getMList()) {
                            if (info.getType() == 21) {
                                stationInfo = info;
                                break;
                            }
                        }
                        if (isFirst) {
                            isFirst = false;
                            isSetUp1 = true;
                            if (gsmConfig != null) {
                                if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                    if (!isSend1) {
                                        isSend1 = true;
                                        if (!mGsmMsgList.contains(dataScan0)) {
                                            mGsmMsgList.add(dataScan0);
                                        }
                                        isScan = true;
                                        gsmTime = System.currentTimeMillis() + 180 * 1000L;
                                    }
                                } else {
                                    if (!mGsmMsgList.contains(gsmConfig.cmd1)) {
                                        mGsmMsgList.add(gsmConfig.cmd1);
                                    }
                                }
                            }
                        }
                        if (stationInfo == null) {
                            stationInfo = new StationInfo();
                            stationInfo.setId(App.get().getStationId());
                            stationInfo.setName("基站" + (App.get().getMList().size() + 1));
                            stationInfo.setIp("", true);
                            stationInfo.setType(21);
                            App.get().getMList().add(stationInfo);
                            App.get().insert(stationInfo);
                        }
                        stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                        if (!isSetUp1) {
                            for (String s : map.keySet()) {
                                switch (s) {
                                    case "0101":
                                        stationInfo.setMCC(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.MCC1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0102":
                                        stationInfo.setMNC(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.MNC1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0103":
                                        stationInfo.setLAC(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.LAC1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0104":
                                        stationInfo.setCI(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        break;
                                    case "0106":
                                        stationInfo.setCRO(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.CRO1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "010b":
                                        stationInfo.setWorkModel(map.get(s)[0]);
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.WORKMODE1 = map.get(s)[0] + "";
                                        }
                                        break;
                                    case "010a":
                                        stationInfo.setconfigModel(map.get(s)[0]);
                                        break;
                                    case "010c":
                                        stationInfo.setRecaptureTime(little_bytesToInt(map.get(s)));
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.CAPTIME1 = little_bytesToInt(map.get(s)) + "";
                                        }
                                        break;
                                    case "0150":
                                        stationInfo.setCarrierFrequencyPoint(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.BCC1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0151":
                                        stationInfo.setDownlinkAttenuation(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.LOWATT1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0152":
                                        stationInfo.setUplinkAttenuation(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
                                            gsmConfig.UPATT1 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0156":
                                        stationInfo.setStartingFrequencyPoint1(little_bytesToInt(map.get(s)));
                                        break;
                                    case "0157":
                                        stationInfo.setEndFrequencyPoint1(little_bytesToInt(map.get(s)));
                                        break;
                                    case "0158":
                                        stationInfo.setStartingFrequencyPoint2(little_bytesToInt(map.get(s)));
                                        break;
                                    case "0159":
                                        stationInfo.setEndFrequencyPoint2(little_bytesToInt(map.get(s)));
                                        break;
                                    case "015f":
                                        stationInfo.setFrequencyOffset(little_bytesToInt(map.get(s)));
                                        break;

                                }
                            }

                        }
                    } else if (data[6] == 0x01) {
                        for (StationInfo info : App.get().getMList()) {
                            if (info.getType() == 22) {
                                stationInfo = info;
                                break;
                            }
                        }
                        if (isFirst1) {
                            isFirst1 = false;
                            isSetUp2 = true;
                            if (gsmConfig != null) {
                                if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0")) {
                                    if (!isSend1) {
                                        isSend1 = true;
                                        if (!mGsmMsgList.contains(dataScan1)) {
                                            mGsmMsgList.add(dataScan1);
                                        }
                                        isScan = true;
                                        gsmTime = System.currentTimeMillis() + 180 * 1000L;
                                    }
                                } else {
                                    if (!mGsmMsgList.contains(gsmConfig.cmd2)) {
                                        mGsmMsgList.add(gsmConfig.cmd2);
                                    }
                                }
                            }
                        }
                        if (stationInfo == null) {
                            stationInfo = new StationInfo();
                            stationInfo.setId(App.get().getStationId());
                            stationInfo.setName("基站" + (App.get().getMList().size() + 1));
                            stationInfo.setIp("", true);
                            stationInfo.setType(22);
                            App.get().getMList().add(stationInfo);
                            App.get().insert(stationInfo);
                        }
                        stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.CONNECTED);
                        if (!isSetUp2) {
                            for (String s : map.keySet()) {
                                switch (s) {
                                    case "0101":
                                        stationInfo.setMCC1(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.MCC2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0102":
                                        stationInfo.setMNC1(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.MNC2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0103":
                                        stationInfo.setLAC1(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.LAC2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0104":
                                        stationInfo.setCI1(hexStr2Str(bytesToHexString1(map.get(s))));
                                        break;
                                    case "0106":
                                        stationInfo.setCRO1(hexStr2Str(bytesToHexString1(map.get(s))));
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.CRO2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "010b":
                                        stationInfo.setWorkModel1(map.get(s)[0]);
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.WORKMODE2 = map.get(s)[0] + "";
                                        }
                                        break;
                                    case "010a":
                                        stationInfo.setConfigModel1(map.get(s)[0]);
                                        break;
                                    case "010c":
                                        stationInfo.setRecaptureTime1(little_bytesToInt(map.get(s)));
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.CAPTIME2 = little_bytesToInt(map.get(s)) + "";
                                        }
                                        break;
                                    case "0150":
                                        stationInfo.setCarrierFrequencyPoint1(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.BCC2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0151":
                                        stationInfo.setDownlinkAttenuation1(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.LOWATT2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0152":
                                        stationInfo.setUplinkAttenuation1(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0") && !isSend2) {
                                            gsmConfig.UPATT2 = hexStr2Str(bytesToHexString1(map.get(s))).trim();
                                        }
                                        break;
                                    case "0156":
                                        stationInfo.setStartingFrequencyPoint11(little_bytesToInt(map.get(s)));
                                        break;
                                    case "0157":
                                        stationInfo.setEndFrequencyPoint11(little_bytesToInt(map.get(s)));
                                        break;
                                    case "0158":
                                        stationInfo.setStartingFrequencyPoint21(little_bytesToInt(map.get(s)));
                                        break;
                                    case "0159":
                                        stationInfo.setEndFrequencyPoint21(little_bytesToInt(map.get(s)));
                                        break;
                                    case "015f":
                                        stationInfo.setFrequencyOffset1(little_bytesToInt(map.get(s)));
                                        break;

                                }

                                DataManager.getInstance().crateOrUpdate(gsmConfig);
                                gsmConfig.setCMD();
                                EventBus.getDefault().post(new MessageEvent(true));
                            }
                        }
                    }

                } else if (data[5] == 0x18) {
                    ImsiData imsiData = new ImsiData();
                    imsiData.setDeviceId(App.get().deviceId);
                    MacData macData = null;
                    imsiData.setId(++App.get().imsiId);
                    imsiData.setTime(System.currentTimeMillis());
                    if (data[6] == 0) {
                        imsiData.setBbu("8");
                    } else {
                        imsiData.setBbu("9");
                    }
                    for (String s : map.keySet()) {
                        switch (s) {
                            case "0211":
                                imsiData.setImsi(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0212":
                                imsiData.setImei(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0213":
                                imsiData.setTmsi(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                            case "0214":
                                imsiData.setEsn(hexStr2Str(bytesToHexString1(map.get(s))).trim());
                                break;
                        }
                    }
                    EventBus.getDefault().post(new MessageEvent(imsiData));
                }
            }
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * When the service is destroyed, make sure to close the Bluetooth connection.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Tag, Tag + "onCreate");
        wl.release();
    }
}
