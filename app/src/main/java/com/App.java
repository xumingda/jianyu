package com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.communication.BaseApplication;
import com.communication.request.HttpCallback;
import com.communication.request.RequestHelper;
import com.communication.tcp.TcpService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.R;
import com.lte.data.CellConfig;
import com.lte.data.DataManager;
import com.lte.data.DeviceRegister;
import com.lte.data.GsmConfig;
import com.lte.data.ImsiData;
import com.lte.data.MacData;
import com.lte.data.ScanResult;
import com.lte.data.StationInfo;
import com.lte.data.UserInfo;
import com.lte.data.table.BandTable;
import com.lte.data.table.BlackListTable;
import com.lte.data.table.DeviceTypeTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.MacDataTable;
import com.lte.data.table.RealmInteger;
import com.lte.data.table.WhiteListTable;
import com.lte.https.MobileQuery;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.event.SystemOutEvent;
import com.lte.utils.AppUtils;
import com.communication.utils.LETLog;
import com.lte.utils.CrashHandler;
import com.lte.utils.DateUtils;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ToastUtils;
import com.tencent.bugly.Bugly;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.lte.utils.AppUtils.get03Cmd;
import static com.lte.utils.Constants.UPGRADE;
import static com.lte.utils.DateUtils.getCurrentDayTime;

/**
 * Created by chenxiaojun on 2017/9/5.
 */

public class App extends BaseApplication {

    private static final String TAG = "App";
    private static final int INSERT = 4;
    private CopyOnWriteArrayList<StationInfo> mList = new CopyOnWriteArrayList<>();
    private RealmResults<MacDataTable> macDataList;
    public List<ImsiDataTable> imsiDataList = new ArrayList<>();

    public SharedPreferences mShared;
    public SharedPreferences.Editor mSharedEditor;

    public String DevNumber;

    public String deviceId;

    public long before;

    public long after;

    private static App mInstance;
    public String Ip;

    public String ssid;

    public Long id;

    public Long imsiId = 0l;

    public Long imeiId = 0L;

    public Long blackId = 0L;

    public Long whiteId = 0L;

    public Long scanResultId = 0l;

    public Long sceneID = 0L;

    public Long deviceTypeId = 0L;

    public Long bandId = 0L;

    public Long mobileResultId;

    public UserInfo userInfo;
    private WifiReciver wifiReciver;
    public String apikey;

    public boolean apikeyCanUse;

    public TcpService tcpService;

    public ArrayList<String> selectImsi = new ArrayList<>();
    public ArrayList<String> selectCloseImsi = new ArrayList<>();

    public List<BlackListTable> blackListTables = new ArrayList<>();

    public List<WhiteListTable> whiteListTables = new ArrayList<>();

    public int udpNo;

    private static List<ImsiData> imsiDatas = new ArrayList<>();

    private static Handler handler;//线程通信h工具
    public OrderedRealmCollection<BandTable> bandTables;
    private String closePowerMsg1 = "";


    public static Handler getHandler() {
        return handler;
    }

    private static int mainThreadId;//主线程id

    public boolean isBlcakOn;

    public boolean isWhitOn;

    private Long stationId = 0L;

    public boolean isClose;//打开定位，关闭其他，防止同时收到两个BBU定位消息，导致所有BBU被关闭，加上一个标记。只执行一次目标上号后关闭其他BBU定位命令

    public static int type=0;
    //模式
    public static String pattern;
    //频点
    public static String channel;
    //模式
    public static String pci;
    //获得主线程id
    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static App get() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mShared=getSharedPreferences("share", Context.MODE_PRIVATE);
        mSharedEditor=mShared.edit();

        mainThreadId = android.os.Process.myTid();//取得当前的线程的id
        handler = new Handler();
        mInstance = this;
        initData();
        EventBus.getDefault().register(this);
        IntentFilter myIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        myIntentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        myIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        myIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiReciver = new WifiReciver();
        registerReceiver(wifiReciver, myIntentFilter);
//        Utils.init(this);

    }

    private void initDeviceTypeData() {
        DeviceTypeTable deviceTypeTable = new DeviceTypeTable();
        deviceTypeTable.setId(1L);
        deviceTypeTable.setName("L034");
        deviceTypeTable.setBbuList(new byte[]{1, 3, 6, 7});
        DataManager.getInstance().addDeviceType(deviceTypeTable);
        DeviceTypeTable deviceTypeTable1 = new DeviceTypeTable();
        deviceTypeTable1.setId(2L);
        deviceTypeTable1.setName("L050");
        deviceTypeTable1.setBbuList(new byte[]{1, 2, 6, 7});
        DataManager.getInstance().addDeviceType(deviceTypeTable1);
        DeviceTypeTable deviceTypeTable2 = new DeviceTypeTable();
        deviceTypeTable2.setId(3L);
        deviceTypeTable2.setName("L058");
        deviceTypeTable2.setBbuList(new byte[]{8, 9, 10});
        DataManager.getInstance().addDeviceType(deviceTypeTable2);
        DeviceTypeTable deviceTypeTable3 = new DeviceTypeTable();
        deviceTypeTable3.setId(4L);
        deviceTypeTable3.setName("L063");
        deviceTypeTable3.setBbuList(new byte[]{1, 2, 6, 7, 8, 9});
        DataManager.getInstance().addDeviceType(deviceTypeTable3);
        DeviceTypeTable deviceTypeTable4 = new DeviceTypeTable();
        deviceTypeTable4.setId(5L);
        deviceTypeTable4.setName("L035");
        deviceTypeTable4.setBbuList(new byte[]{1, 3, 6, 7, 8, 9});
        DataManager.getInstance().addDeviceType(deviceTypeTable4);
    }

    private void initPointData() {
        BandTable bandTable = new BandTable();
        bandTable.setId(1L);
        bandTable.setName("电信band1");
        RealmList<RealmInteger> realmIntegers = new RealmList<>();
        RealmInteger realmInteger = new RealmInteger();
        realmInteger.setNumber(100);
        realmIntegers.add(realmInteger);
        bandTable.setPoint(realmIntegers);
        DataManager.getInstance().addBand(bandTable);
        BandTable bandTable1 = new BandTable();
        bandTable1.setId(2L);
        bandTable1.setName("电信band3");
        RealmList<RealmInteger> realmIntegers1 = new RealmList<>();
        RealmInteger realmInteger1 = new RealmInteger();
        realmInteger1.setNumber(1825);
        realmIntegers1.add(realmInteger1);
        bandTable1.setPoint(realmIntegers1);
        DataManager.getInstance().addBand(bandTable1);
        BandTable bandTable2 = new BandTable();
        bandTable2.setId(3L);
        bandTable2.setName("联通band1");
        RealmList<RealmInteger> realmIntegers2 = new RealmList<>();
        RealmInteger realmInteger2 = new RealmInteger();
        realmInteger2.setNumber(450);
        realmIntegers2.add(realmInteger2);
        RealmInteger realmInteger3 = new RealmInteger();
        realmInteger3.setNumber(500);
        realmIntegers2.add(realmInteger3);
        bandTable2.setPoint(realmIntegers2);
        DataManager.getInstance().addBand(bandTable2);
        BandTable bandTable3 = new BandTable();
        bandTable3.setId(4L);
        bandTable3.setName("联通band3");
        RealmList<RealmInteger> realmIntegers3 = new RealmList<>();
        RealmInteger realmInteger4 = new RealmInteger();
        realmInteger4.setNumber(1650);
        realmIntegers3.add(realmInteger4);
        RealmInteger realmInteger5 = new RealmInteger();
        realmInteger5.setNumber(1533);
        realmIntegers3.add(realmInteger5);
        RealmInteger realmInteger6 = new RealmInteger();
        realmInteger6.setNumber(1506);
        realmIntegers3.add(realmInteger6);
        bandTable3.setPoint(realmIntegers3);
        DataManager.getInstance().addBand(bandTable3);
        BandTable bandTable4 = new BandTable();
        bandTable4.setId(5L);
        bandTable4.setName("band38/41");
        RealmList<RealmInteger> realmIntegers4 = new RealmList<>();
        RealmInteger realmInteger7 = new RealmInteger();
        realmInteger7.setNumber(37900);
        realmIntegers4.add(realmInteger7);
        RealmInteger realmInteger8 = new RealmInteger();
        realmInteger8.setNumber(38098);
        realmIntegers4.add(realmInteger8);
        bandTable4.setPoint(realmIntegers4);
        DataManager.getInstance().addBand(bandTable4);
        BandTable bandTable5 = new BandTable();
        bandTable5.setId(6L);
        bandTable5.setName("band39");
        RealmList<RealmInteger> realmIntegers5 = new RealmList<>();
        RealmInteger realmInteger9 = new RealmInteger();
        realmInteger9.setNumber(38400);
        realmIntegers5.add(realmInteger9);
        RealmInteger realmInteger10 = new RealmInteger();
        realmInteger10.setNumber(38544);
        realmIntegers5.add(realmInteger10);
        bandTable5.setPoint(realmIntegers5);
        DataManager.getInstance().addBand(bandTable5);
        BandTable bandTable6 = new BandTable();
        bandTable6.setId(7L);
        bandTable6.setName("band40");
        RealmList<RealmInteger> realmIntegers6 = new RealmList<>();
        RealmInteger realmInteger11 = new RealmInteger();
        realmInteger11.setNumber(38950);
        realmIntegers6.add(realmInteger11);
        RealmInteger realmInteger12 = new RealmInteger();
        realmInteger12.setNumber(39148);
        realmIntegers6.add(realmInteger12);
        bandTable6.setPoint(realmIntegers6);
        DataManager.getInstance().addBand(bandTable6);
    }

    public void onInit() {
        String fileName = "lte-";
        fileName += System.currentTimeMillis();
        fileName += ".log";
        LETLog.LOG2FILE_ENABLE = true;
        LETLog.onCreate(fileName);
        CrashHandler.getInstance().initialize(mInstance);
        Bugly.init(getApplicationContext(), "ae8c91391d", false);
        BindService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            forceSendRequestByMobileData();
        }
        if (userInfo.getMobileUserName() != null && userInfo.getMobilePassword() != null) {
            mHandler.sendEmptyMessageDelayed(3, 5 * 1000l);
        }
    }

    private String openPowerMsg = "";

    private String closePowerMsg = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ToastUtils.showToast(getApplicationContext(), "获取令牌成功", Toast.LENGTH_SHORT);
                App.get().apikeyCanUse = true;
                removeMessages(3);
                MobileQuery.getInstance().getNumber(App.get().callBack1);
            } else if (msg.what == 2) {
                sendEmptyMessageDelayed(3, 58 * 60 * 1000L);
            } else if (msg.what == 3) {
                MobileQuery.getInstance().getApiKey(App.get().callBack1);
                mHandler.sendEmptyMessageDelayed(3, 25 * 1000L);
            } else if (msg.what == 4) {
                Toast.makeText(App.get(), R.string.tipsss, Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(new SystemOutEvent(true));
            }else if(msg.what == 5){
                tcpService.sendMsg(get03Cmd("11",openPowerMsg+openPowerMsg1), null);
                openPowerMsg = "";
                openPowerMsg1 = "";
                isContain38 = false;
                isContain39 = false;
                isContain40 = false;
            }else if(msg.what == 6){
                tcpService.sendMsg(get03Cmd("11",closePowerMsg+closePowerMsg1), null);
                closePowerMsg = "";
                closePowerMsg1 = "";
            }else if(msg.what == 8){
                tcpService.sendMsg(get03Cmd("11","0407010204080100"), null);
            }

        }
    };

    public void openGSM(){
        if(tcpService != null){
            tcpService.sendMsg(get03Cmd("11","04080101"), null);
        }
    }
    public int apiNumber = 0;
    private HttpCallback callBack1 = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            try {
                JsonObject asJsonObject = jsonObject.getAsJsonObject();
                if (asJsonObject.get("Authorization") != null) {
                    apikey = asJsonObject.get("Authorization").getAsString();
                    Log.d("http", "apikey :" + apikey);
                    mHandler.sendEmptyMessageDelayed(1, 20 * 1000);
                } else if (asJsonObject.get("remaintimes") != null) {
                    apiNumber = asJsonObject.get("remaintimes").getAsInt();
                    mHandler.sendEmptyMessage(2);
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    /**
     * start other BrotherFragment
     */
    @Subscribe
    public void onUpdate(MessageEvent event) {
        if (event.data != null) {
            Toast.makeText(this, event.data, Toast.LENGTH_LONG).show();
        }

        //lph20150523 解决断开连接重连后无法更新功放信息问题
//        if(tcpService!=null) {
//            if (tcpService.isOpen() == false) {
//                tcpService=null;
//            }
//        }
        if ( tcpService == null) {
            boolean isConnect = false;
            for (StationInfo stationInfo : mList) {
                if (stationInfo.getConnectionStatus() != StationInfo.ConnectionStatus.DISCONNECTED) {
                    isConnect = true;
                    break;
                }
            }
            LETLog.d("TcpService " + "isConnect :" + isConnect);
            synchronized (this){

                if(isConnect && tcpService == null){
                    String ip = AppUtils.intToIp(AppUtils.getWifiGateIP(this));
                    tcpService = new TcpService(ip, 5000);
                    LETLog.d("TcpService " + "ip :" + ip);
                    RequestHelper.getInstance().createSocket(tcpService);
                    mHandler.sendEmptyMessageDelayed(8,3000L);
                }
            }
        }
    }


    public void initData() {
        userInfo = DataManager.getInstance().findUser(1l);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setId(1l);
            userInfo.setUserName("admin");
            userInfo.setPassword("123456");
            userInfo.setInitTime(System.currentTimeMillis());
            userInfo.setImsiStartTime(System.currentTimeMillis());//默认开始时间
            userInfo.setImsipreClearTime(System.currentTimeMillis());//新安装APP默认清屏时间
            userInfo.setImsiType(2);
            userInfo.setMacStartTime(getCurrentDayTime());
            userInfo.setMacendTime(getCurrentDayTime() + 24 * 60 * 60 * 1000);
            userInfo.setMacType(0);
            userInfo.setMacpreClearTime(0l);
//            userInfo.setUrl("116.196.125.155");
//            userInfo.setQueryUrl("45.252.63.167");
            userInfo.setQueryUrl("118.244.206.98");
//            userInfo.setImsiPort("8081");
//            userInfo.setMobilePort("1443");
            DataManager.getInstance().crateOrUpdate(userInfo);
            initDeviceTypeData();
            initPointData();
            GsmConfig gsmConfig = new GsmConfig();
            gsmConfig.id = 1l;
            gsmConfig.setCMD();
            DataManager.getInstance().crateOrUpdate(gsmConfig);
        }
        deviceTypeId = DataManager.getInstance().findDeviceTypeId();
        bandId = DataManager.getInstance().findBandId();
        sceneID = DataManager.getInstance().findSceneId();
        mList = DataManager.getInstance().finalStation();
        imeiId = DataManager.getInstance().findImeiId();
        imsiId = DataManager.getInstance().findLastId();
        blackId = DataManager.getInstance().findBlackId();
        whiteId = DataManager.getInstance().findWhiteId();
        scanResultId = DataManager.getInstance().findLastResultId();
        mobileResultId = DataManager.getInstance().findMobileResultId();
        App.get().blackListTables = DataManager.getInstance().findBlackData();
        App.get().whiteListTables = DataManager.getInstance().findWhiteData();
        stationId = DataManager.getInstance().findStationId();
        isBlcakOn = SharedPreferencesUtil.getBooleanConfig(this, "sniffer", "blackOn", true);
        isWhitOn = SharedPreferencesUtil.getBooleanConfig(this, "sniffer", "whiteOn", false);
        DataManager.getInstance().findImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
            @Override
            public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                App.get().imsiDataList = imsiDataTables.sort("time", Sort.DESCENDING);
            }
        });
        List<DeviceRegister> deviceRegisters = DataManager.getInstance().finalDeviceRegister();
        if (deviceRegisters.size() > 0) {
            DevNumber = deviceRegisters.get(0).getDevNumber();
        }
    }

    public void BindService() {
        TcpManager.getInstance().initInstance(this);
    }

//    public List<StationInfo> getmList() {
//        return mList;
//    }

    public void upDateList() {
        mList = DataManager.getInstance().finalStation();
    }

    public void insert(StationInfo stationInfo) {
        LETLog.d("insert" + stationInfo.toString());
        DataManager.getInstance().createOrUpdateStation(stationInfo);
    }

    public RealmResults<MacDataTable> getMacDataList() {
        return macDataList;
    }

    public void insert(MacData macData) {
        DataManager.getInstance().createOrUpdateMac(macData);
    }

    public List<ImsiDataTable> getImsiDataList() {
        return imsiDataList;
    }

    public void insert(ImsiData imsiData) {
        LETLog.d("insert :" + imsiData.toString());
        DataManager.getInstance().createOrUpdateImsi(imsiData);
    }

    public void updateScanResult(ScanResult scanResult, StationInfo stationInfo) {
        DataManager.getInstance().createOrUpdateStation(stationInfo, scanResult);
    }

    public void getApiKeyDay() {
        mHandler.sendEmptyMessageDelayed(3, 58 * 60 * 1000L);
    }

    public StationInfo initInfo(Long id) {
        return DataManager.getInstance().findStatInfoById(id);
    }

    private boolean isFirst = true;
    CopyOnWriteArrayList<StationInfo> list = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<StationInfo> getOnLineList() {
        for (StationInfo stationInfo : mList) {
            if (stationInfo.getConnectionStatus() != StationInfo.ConnectionStatus.DISCONNECTED) {
                if (!list.contains(stationInfo)) {
                    list.add(stationInfo);
                }
            }
        }
        if (isFirst) {
            isFirst = false;
            for (StationInfo stationInfo : mList) {
                if (stationInfo.getConnectionStatus() != StationInfo.ConnectionStatus.DISCONNECTED) {
                    if (!list.contains(stationInfo)) {
                        list.add(stationInfo);
                    }
                } else {
                    if (deviceId == null) {
                        deviceId = AppUtils.getConnectWifiSsid();
                    }
                    if (deviceId != null) {
                        if (deviceId.length() > 3) {
                            DeviceTypeTable deviceTypeTable = DataManager.getInstance().findDeviceTypebyName(deviceId.substring(0, 4));
                            if (deviceTypeTable != null) {
                                byte[] bbuList = deviceTypeTable.getBbuList();
                                if (stationInfo.getType() == 4) {
                                    if (stationInfo.getIp() != null && stationInfo.getIp().length() > 15) {
                                        for (byte b : bbuList) {
                                            if (TextUtils.equals(stationInfo.getIp().substring(15, 16), b + "")) {
                                                if (!list.contains(stationInfo)) {
                                                    list.add(stationInfo);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                } else if (stationInfo.getType() == 21) {
                                    for (byte b : bbuList) {
                                        if (b == 8) {
                                            if (!list.contains(stationInfo)) {
                                                list.add(stationInfo);
                                            }
                                            break;
                                        }
                                    }
                                } else if (stationInfo.getType() == 22) {
                                    for (byte b : bbuList) {
                                        if (b == 9) {
                                            if (!list.contains(stationInfo)) {
                                                list.add(stationInfo);
                                            }
                                            break;
                                        }
                                    }
                                } else if (stationInfo.getType() == 23) {
                                    for (byte b : bbuList) {
                                        if (b == 10) {
                                            if (!list.contains(stationInfo)) {
                                                list.add(stationInfo);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }
        return list;
    }

    public void upDateBlackList() {
        App.get().blackListTables = DataManager.getInstance().findBlackData();
    }

    public void upDateWhiteList() {
        App.get().whiteListTables = DataManager.getInstance().findWhiteData();
    }

    public void findBand() {
        bandTables = DataManager.getInstance().findBandTable();
    }

    public synchronized long getStationId() {
        return ++stationId;
    }

    boolean isContain38;
    boolean isContain39;
    boolean isContain40;

    private String openPowerMsg1 = "";

    public void openPower(StationInfo info) {
        CellConfig cellConfig = info.getCellConfig();
        if(cellConfig != null && tcpService != null) {
            int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
            if (37750 <= downlink_frequency_point && downlink_frequency_point <= 38249||downlink_frequency_point==40936) {
                isContain38 = true;
                if(isContain39 && !isContain40){
                    openPowerMsg1 = "04070101FA300100";
                }else if(isContain39 &&isContain40){
                    openPowerMsg1 = "04070101FA300100";
                }else if(!isContain39 && isContain40){
                    openPowerMsg1 = "04070102FA300100";
                }else {
                    openPowerMsg1 = "04070104FA300100";
                }
            } else if (38250 <= downlink_frequency_point && downlink_frequency_point <= 38649) {
                isContain39 = true;
                if(isContain38 && !isContain40){
                    openPowerMsg1 = "04070101FA300100";
                }else if(isContain38 &&isContain40){
                    openPowerMsg1 = "04070101FA300100";
                }else if(!isContain38 && isContain40){
                    openPowerMsg1 = "04070102FA300100";
                }else {
                    openPowerMsg1 = "04070105FA300100";
                }
            } else if (38650 <= downlink_frequency_point && downlink_frequency_point <= 39649) {
                isContain40 = true;
                if(isContain38 && !isContain39){
                    openPowerMsg1 = "04070102FA300100";
                }else if(isContain38 &&isContain39){
                    openPowerMsg1 = "04070102FA300100";
                }else if(!isContain38 && isContain39){
                    openPowerMsg1 = "04070103FA300100";
                }else {
                    openPowerMsg1 = "04070106FA300100";
                }
            } else if (0 <= downlink_frequency_point && downlink_frequency_point <= 1949) {
                openPowerMsg = "0402010104030101FA300100";
            }
        }
        mHandler.removeMessages(5);
        mHandler.sendEmptyMessageDelayed(5,3000L);
    }

    public void closePower(StationInfo info) {
        CellConfig cellConfig = info.getCellConfig();
        if(cellConfig != null) {
            int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
            if (37750 <= downlink_frequency_point && downlink_frequency_point <= 39649||downlink_frequency_point==40936) {
                closePowerMsg = "04070100";
            } else if (0 <= downlink_frequency_point && downlink_frequency_point <= 29499) {
                closePowerMsg1 = "0402010004030100";
            }
        }
        mHandler.removeMessages(6);
        mHandler.sendEmptyMessageDelayed(6,1000L);
    }

    public void openScan() {
        if(tcpService != null){
            tcpService.sendMsg(get03Cmd("11","FA300101"), null);
        }
    }

    private class WifiReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    LETLog.d("APP WIFI DISCONNECT ACTION");
                    for (StationInfo stationInfo : mList) {
                        stationInfo.setConnectionStatus(StationInfo.ConnectionStatus.DISCONNECTED);
                        stationInfo.setConfigState(StationInfo.ConfigState.UN_CONFIG);
                        stationInfo.setConfig(false);
                        stationInfo.setOpen(false);
                        stationInfo.setSoft_state("");
                        stationInfo.setCpu_tem(0);
                        stationInfo.setCpu_use(0);
                        stationInfo.setRom_use(0);
                        if (stationInfo.getType() == 4) {
                            stationInfo.setIsCellConfig(false);
                        }
                        stationInfo.setFreq(0);
                        if (stationInfo.getType() == 21 || stationInfo.getType() == 22) {
                            TcpManager.getInstance().addQueryMsg();
                        } else if (stationInfo.getType() == 23) {
                            TcpManager.getInstance().addCmdaQueryMsg();
                        }

                        EventBus.getDefault().post(new MessageEvent(true));
                    }

                    //lph20190520 添加,功放重新连接功能
                    try {
                        if (tcpService != null||(!tcpService.isOpen())) {
                            if (tcpService.isOpen()) {
                                tcpService.close();
                            }
                            tcpService = null;
                        }
                    }
                    catch (Exception ex){
                        LETLog.d("tcpService error:"+ex.toString());
                    }
                }
                //lph20190520 添加,功放重新连接功能
                else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                    LETLog.d("APP WIFI SCONNECT ACTION");
                    EventBus.getDefault().post(new MessageEvent(true));
                }

            }
        }
    }

    public List<StationInfo> getMList() {
        return mList;
    }

    public void setDelay(long l) {
        mHandler.sendEmptyMessageDelayed(4, l);
    }
}
