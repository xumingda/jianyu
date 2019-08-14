package com.lte.data;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.App;
import com.communication.utils.LETLog;
import com.lte.R;
import com.lte.data.table.BandTable;
import com.lte.data.table.BlackListTable;
import com.lte.data.table.CdmaConfigTable;
import com.lte.data.table.DeviceRegisterTable;
import com.lte.data.table.DeviceTypeTable;
import com.lte.data.table.GsmConfigTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.MacDataTable;
import com.lte.data.table.MobileResultTable;
import com.lte.data.table.RealmInteger;
import com.lte.data.table.ScanResultTable;
import com.lte.data.table.SceneTable;
import com.lte.data.table.StationInfoTable;
import com.lte.data.table.UserInfoTable;
import com.lte.data.table.WhiteListTable;
import com.lte.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.lte.utils.FileUtils.getFileSizeString;

/**
 * Created by chenxiaojun on 2017/9/11.
 */

public class DataManager {

    private static final String TAG = "DataManager";
    private static final long SEND_MESH_CMD_PERIOD_MS = 240l;
    private static DataManager mInstance;

    private Realm mRealm;

    private Realm mHandRealm;

    private final HandlerThread mThread;


//    private static MyQueue<Object> mQueue;

    private static Handler handler;

    private final int maxWork = 10;

    private int work;

    public static DataManager getInstance() {
        if (mInstance == null) {
            synchronized (DataManager.class) {
                if (mInstance == null) {
                    mInstance = new DataManager();
                }
            }
        }
        return mInstance;
    }

    private DataManager() {
        Realm.init(App.get());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(7)
                .build();
        Realm.setDefaultConfiguration(config);
        mRealm = Realm.getDefaultInstance();
        this.mThread = new HandlerThread("Data Thread");
        this.mThread.start();
        handler = new Handler(mThread.getLooper());
    }

    public void onDestroy() {
//        mQueue.clear();
        mRealm.close();
        handler.removeCallbacksAndMessages(null);
        mThread.quit();
    }

    /******************************************增加******************************************/
    public void crateOrUpdate(final UserInfo userInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                mRealm = Realm.getDefaultInstance();
                if (mHandRealm == null) {
                    mHandRealm = Realm.getDefaultInstance();
                }
                try {
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final UserInfoTable user = new UserInfoTable();
                            user.setUserName(userInfo.getUserName());
                            user.setPassword(userInfo.getPassword());
                            user.setMobileUserName(userInfo.getMobileUserName());
                            user.setMobilePassword(userInfo.getMobilePassword());
                            user.setId(userInfo.getId());
                            user.setImsiStartTime(userInfo.getImsiStartTime());
                            user.setInitTime(userInfo.getInitTime());
                            user.setImsiendTime(userInfo.getImsiendTime());
                            user.setImsiType(userInfo.getImsiType());
                            user.setMacStartTime(userInfo.getMacStartTime());
                            user.setMacendTime(userInfo.getMacendTime());
                            user.setMacType(userInfo.getMacType());
                            user.setImsipreClearTime(userInfo.getImsipreClearTime());
                            user.setMacpreClearTime(userInfo.getMacpreClearTime());
                            user.setUrl(userInfo.getUrl());
                            user.setQueryUrl(userInfo.getQueryUrl());
                            user.setImsiPort(userInfo.getImsiPort());
                            user.setMobilePort(userInfo.getMobilePort());
                            user.setSceneName(userInfo.getSceneName());
                            user.setDeviceType(userInfo.getDeviceType());
                            realm.copyToRealmOrUpdate(user);
                        }
                    });
                } catch (Exception e) {

                }

            }
        });

    }

    public void createOrUpdateStation(final StationInfo stationInfo, final ScanResult scanResult) {
        Log.d(TAG, "ScanResult :" + scanResult.toString() + "---" + stationInfo.getId());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mHandRealm == null) {
                    mHandRealm = Realm.getDefaultInstance();
                }
                try {
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ScanResultTable scanResultTable = scanResult.bulider();
                            StationInfoTable station = realm.where(StationInfoTable.class)
                                    .equalTo("id", stationInfo.getId())
                                    .findFirst();
                            station.getResultTables().add(scanResultTable);
                            realm.copyToRealmOrUpdate(station);
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }

    private StationInfoTable findStation(Long id) {
        StationInfoTable id1 = mHandRealm.where(StationInfoTable.class)
                .equalTo("id", id)
                .findFirst();
        return id1;
    }

    public void createOrUpdateStation(final StationInfo stationInfo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mHandRealm == null) {
                    mHandRealm = Realm.getDefaultInstance();
                }
                try {
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                                                           @Override
                                                           public void execute(Realm realm) {
                                                               final StationInfoTable stationInfoTable = new StationInfoTable();
                                                               stationInfoTable.setId(stationInfo.getId());
                                                               stationInfoTable.setIp(stationInfo.getIp());
                                                               stationInfoTable.setDbm(stationInfo.getDBM());
                                                               stationInfoTable.setBbu(stationInfo.getBbu());
                                                               stationInfoTable.setRxlevmin(stationInfo.getRxlevmin());
                                                               if (stationInfo.getInitConfig() != null) {
                                                                   stationInfoTable.setInitConfigTable(stationInfo.getInitConfig().createInitConfigTable());

                                                               }
                                                               if (stationInfo.getmList().size() != 0) {
                                                                   for (ScanResult scanResult : stationInfo.getmList()) {
                                                                       stationInfoTable.getResultTables().add(scanResult.bulider());
                                                                   }
                                                               }
                                                               if (stationInfo.getScanSet() != null) {
                                                                   stationInfoTable.setScanSetTable(stationInfo.getScanSet().createScanSetTable());
                                                               }
                                                               if (stationInfo.getCellConfig() != null) {
                                                                   stationInfoTable.setCellConfigTable(stationInfo.getCellConfig().createCellConfigTable());
                                                               }
                                                               stationInfoTable.setName(stationInfo.getName());
                                                               stationInfoTable.setType(stationInfo.getType());
                                                               stationInfoTable.setTDDtype(stationInfo.getTDDtype());
                                                               realm.copyToRealmOrUpdate(stationInfoTable);
                                                           }
                                                       }
                    );
                } catch (Exception e) {

                }

            }
        });
    }

    public void createOrUpdateImsi(final ImsiData imsiData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          ImsiDataTable imsi = realm.where(ImsiDataTable.class).equalTo("imsi", imsiData.getImsi()).findFirst();
                                                          if (imsi != null) {
                                                              int number = imsi.getNumber()+1;
                                                              imsi.setNumber(number);
                                                              Log.d("NUMBER",number+"");
                                                              realm.copyToRealmOrUpdate(imsi);
                                                          }else {
                                                              final ImsiDataTable imsiDataTable = new ImsiDataTable();
                                                              imsiDataTable.setId(imsiData.getId());
                                                              imsiDataTable.setStationName(imsiData.getStationName());
                                                              imsiDataTable.setOperator(imsiData.getOperator());
                                                              imsiDataTable.setImsi(imsiData.getImsi());
                                                              imsiDataTable.setImei(imsiData.getImei());
                                                              imsiDataTable.setBbu(imsiData.getBbu());
                                                              imsiDataTable.setTime(imsiData.getTime());
                                                              imsiDataTable.setTimes(imsiData.getTimes());
                                                              imsiDataTable.setMobile(imsiData.getMobile());
                                                              imsiDataTable.setIsBlackAndWhite(imsiData.getIsblackList());
                                                              imsiDataTable.setDeviceId(imsiData.getDeviceId());
                                                              imsiDataTable.setNumber(1);
                                                              realm.copyToRealmOrUpdate(imsiDataTable);
                                                          }

                                                      }
                                                  }

                    );
//                    mRealm.close();
                } finally {
                }
            }
        });
    }

    public void createOrUpdateMac(final MacData macData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "createOrUpdateStation :" + macData.toString());
                try {
                    final MacDataTable macDataTable = new MacDataTable();
                    macDataTable.setId(macData.getId());
                    macDataTable.setStationName(macData.getStationName());
                    macDataTable.setSerialNumber(macData.getSerialNumber());
                    macDataTable.setMac(macData.getMac());
                    macDataTable.setImsi(macData.getImsi());
                    macDataTable.setTime(macData.getTime());
                    macDataTable.setTimes(macData.getTimes());
//                    mRealm = Realm.getDefaultInstance();
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          realm.copyToRealmOrUpdate(macDataTable);
                                                      }
                                                  }
                    );
//                    mRealm.close();
                } finally {
//                    mRealm.close();
                }
            }
        });
    }

    public void createOrUpdateDeviceRegister(final DeviceRegister deviceRegister) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "createOrUpdateStation :" + deviceRegister.toString());
                try {
                    final DeviceRegisterTable deviceRegisterTable = new DeviceRegisterTable();
                    deviceRegisterTable.setId(deviceRegister.getId());
                    deviceRegisterTable.setDevNumber(deviceRegister.getDevNumber());
                    deviceRegisterTable.setDevName(deviceRegister.getDevName());
                    deviceRegisterTable.setTypeModel(deviceRegister.getTypeModel());
                    deviceRegisterTable.setDevConformation(deviceRegister.getDevConformation());
                    deviceRegisterTable.setDevAddress(deviceRegister.getDevAddress());
                    deviceRegisterTable.setHeight(deviceRegister.getHeight());
                    deviceRegisterTable.setLatitude(deviceRegister.getLatitude());
                    deviceRegisterTable.setLongitude(deviceRegister.getLongitude());
                    deviceRegisterTable.setMac(deviceRegister.getMac());
                    deviceRegisterTable.setPhoneNumber(deviceRegister.getPhoneNumber());
                    deviceRegisterTable.setTimestamp(deviceRegister.getTimestamp());
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                                                           @Override
                                                           public void execute(Realm realm) {
                                                               realm.copyToRealmOrUpdate(deviceRegisterTable);
                                                           }
                                                       }, new Realm.Transaction.OnSuccess() {
                                                           @Override
                                                           public void onSuccess() {
                                                               //成功回调
                                                               Log.d(TAG, "onSuccess :");
//                                                           mRealm.close();
                                                           }
                                                       }, new Realm.Transaction.OnError() {
                                                           @Override
                                                           public void onError(Throwable error) {
                                                               Log.d(TAG, "error :" + error.getLocalizedMessage());
                                                               //失败回调
//                                                           mRealm.close();
                                                           }
                                                       }
                    );
                } finally {
                }
            }
        });
    }

    /******************************************删除******************************************/
    public void deleteUser(final UserInfo userInfo) {
        try {
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                               @Override
                                               public void execute(Realm realm) {
                                                   UserInfoTable user = realm.createObject(UserInfoTable.class);
                                                   user.setMobileUserName(userInfo.getMobileUserName());
                                                   user.setMobilePassword(userInfo.getMobilePassword());
                                                   user.setId(userInfo.getId());
                                               }
                                           }
            );
        }catch (Exception e){

        }

    }

    public void deleteStation(final StationInfo stationInfo) {
        try {
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                               @Override
                                               public void execute(Realm realm) {
                                                   StationInfoTable stationInfoTable = realm.createObject(StationInfoTable.class);
                                                   stationInfoTable.setId(stationInfo.getId());
                                                   stationInfoTable.setIp(stationInfo.getIp());
                                                   stationInfoTable.setInitConfigTable(stationInfo.getInitConfig().createInitConfigTable());
                                                   stationInfoTable.setName(stationInfo.getName());
                                               }
                                           }
            );
        }catch (Exception e){

        }

    }

    /******************************************查询******************************************/
    public List<UserInfo> findUser() {
        RealmResults<UserInfoTable> userList = mRealm.where(UserInfoTable.class)
                .findAllAsync();
        List<UserInfo> mList = new ArrayList<>();
        for (UserInfoTable userInfo : userList) {
            mList.add(userInfo.createUserInfo());
        }
        return mList;
    }

    public UserInfo findUser(Long id) {
        UserInfoTable userInfoTable = mRealm.where(UserInfoTable.class)
                .equalTo("id", id)
                .findFirst();
        return userInfoTable != null ? userInfoTable.createUserInfo() : null;
    }

    public CopyOnWriteArrayList<StationInfo> finalStation() {
        RealmResults<StationInfoTable> userList = mRealm.where(StationInfoTable.class)
                .findAll();
        CopyOnWriteArrayList<StationInfo> mList = new CopyOnWriteArrayList<>();
        for (StationInfoTable stationInfoTable : userList) {
            mList.add(stationInfoTable.createStationInfo());
        }
        return mList;
    }

    public RealmResults<ImsiDataTable> finalImsiData() {
        Log.d(TAG, "imsiDataTables :finalImsiData");
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .findAllAsync();
        userList.addChangeListener(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
            @Override
            public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                Log.d(TAG, "imsiDataTables :" + imsiDataTables.size());
                App.get().imsiId = Long.valueOf(imsiDataTables.size());
            }
        });
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<DeviceRegister> finalDeviceRegister() {
        RealmResults<DeviceRegisterTable> userList = mRealm.where(DeviceRegisterTable.class)
                .findAll();
        List<DeviceRegister> mList = new ArrayList<>();
        for (DeviceRegisterTable deviceRegisterTable : userList) {
            mList.add(deviceRegisterTable.createDeviceRegister());
        }
        return mList;
    }

    public RealmResults<MacDataTable> finalMacData() {
        RealmResults<MacDataTable> userList = mRealm.where(MacDataTable.class)
                .findAllAsync();
        userList.addChangeListener(new RealmChangeListener<RealmResults<MacDataTable>>() {
            @Override
            public void onChange(RealmResults<MacDataTable> macDataTables) {
                Log.d(TAG, "macDataTables :" + macDataTables.size());
                App.get().imeiId = Long.valueOf(macDataTables.size());
            }
        });
        return userList.sort("time", Sort.DESCENDING);
    }

    /******************************************修改******************************************/

    public void upDateStation(final StationInfo stationInfo) {
        try {
            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                               @Override
                                               public void execute(Realm realm) {
                                                   StationInfoTable stationInfoTable = new StationInfoTable();
                                                   stationInfoTable.setId(stationInfo.getId());
                                                   stationInfoTable.setIp(stationInfo.getIp());
                                                   stationInfoTable.setInitConfigTable(stationInfo.getInitConfig().createInitConfigTable());
                                                   stationInfoTable.setName(stationInfo.getName());
                                                   realm.copyToRealmOrUpdate(stationInfoTable);
                                               }
                                           }
            );
        }catch (Exception e){

        }

    }


    public OrderedRealmCollection<ScanResultTable> findScanResult(Long id) {
//        mRealm = Realm.getDefaultInstance();
        StationInfoTable id1 = mRealm.where(StationInfoTable.class)
                .equalTo("id", id)
                .findFirst();
        if (id1 != null) {
            return id1.getResultTables();
        }
        return new RealmList<>();
    }

    public OrderedRealmCollection<ImsiDataTable> findImsiData(Long time) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .greaterThan("time", time)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);
    }

    public OrderedRealmCollection<ImsiDataTable> findBlackImsiData(Long time) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .greaterThan("time", time)
                .equalTo("isBlackAndWhite", 1)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);
    }

    public RealmResults<MacDataTable> findNowImeiiData(long time) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<MacDataTable> userList = mRealm.where(MacDataTable.class)
                .greaterThan("time", time)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);
    }

    public RealmResults<ImsiDataTable> findImsiData(Long imsiStartTime, Long imsiendTime) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", imsiStartTime, imsiendTime)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);
    }

    public RealmResults<MacDataTable> findNowImeiData(Long macStartTime, Long macendTime) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<MacDataTable> userList = mRealm.where(MacDataTable.class)
                .between("time", macStartTime, macendTime)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);
    }

    public Long findImeiId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(MacDataTable.class).max("id") != null) {
            long id = mRealm.where(MacDataTable.class).max("id").longValue();
//            mRealm.close();
            return id;
        }
        return 0L;
    }

    public Long findLastId() {
        if (mRealm.where(ImsiDataTable.class).max("id") != null) {
            long id = mRealm.where(ImsiDataTable.class).max("id").longValue();
            return id;
        }
        return 0L;
    }

    public Long findLastResultId() {
        if (mRealm.where(ScanResultTable.class).max("id") != null) {
            long id = mRealm.where(ScanResultTable.class).max("id").longValue();
            return id;
        }
        return 0L;
    }

    public List<ImsiDataTable> findImsiData(Long mBeginMillseconds, Long mEndMillseconds, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        Log.d(TAG, "findImsiByImeiAndImsiData :" + mBeginMillseconds + " --- END :" + mEndMillseconds);
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findDaImsiData(Long mBeginMillseconds, Long mEndMillseconds, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByImsiData(Long mBeginMillseconds, Long mEndMillseconds, String s, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("imsi", s)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiByImeiAndImsiData(long mBeginMillseconds, long mEndMillseconds, String imsi, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiByImsiData(long mBeginMillseconds, long mEndMillseconds, String imsi, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiByImeiData(long mBeginMillseconds, long mEndMillseconds, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiByImeiAndImsiData(String imsi, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiByImeiData(String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .equalTo("imei", imei)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiData(String imsi, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .equalTo("imsi", imsi)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public void findImsiData(RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiData(long mBeginMillseconds, long mEndMillseconds, String operator, String imsi, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorAndImeiData(long mBeginMillseconds, long mEndMillseconds, String operator, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorAndImeiData(String operator, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorAndImsiData(long mBeginMillseconds, long mEndMillseconds, String operator, String imsi, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorAndImsiData(String operator, String imsi, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorData(long mBeginMillseconds, long mEndMillseconds, String operator, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorData(String operator, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public List<ImsiDataTable> findImsiByOperatorAndImeiAndImsiData(String operator, String imsi, String imei, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
        return userList.sort("time", Sort.DESCENDING);
    }

    public RealmResults<BlackListTable> findBlackData() {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<BlackListTable> userList = mRealm.where(BlackListTable.class)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);

    }

    public RealmResults<BlackListTable> queueBlackData(String Imsi,String UserName) {
//        mRealm = Realm.getDefaultInstance();

        RealmResults<BlackListTable> userList;
        if(Imsi.length()!=0&&UserName.length()!=0)
        {
            userList = mRealm.where(BlackListTable.class)
                    .contains("imsi",Imsi)
                    .or()
                    .contains("phoneUsername",UserName)
                    .findAllAsync();

        }
        else if(Imsi.length()==0&&UserName.length()!=0)
        {
            userList = mRealm.where(BlackListTable.class)
                    .contains("phoneUsername",UserName)
                    .findAllAsync();
        }
        else if(Imsi.length()!=0&&UserName.length()==0)
        {
            userList = mRealm.where(BlackListTable.class)
                    .contains("imsi",Imsi)
                    .findAllAsync();
        }
        else
        {
            userList = mRealm.where(BlackListTable.class)
                .findAllAsync();
        }
        return userList.sort("imsi", Sort.DESCENDING);
    }


    public RealmResults<WhiteListTable> findWhiteData() {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<WhiteListTable> userList = mRealm.where(WhiteListTable.class)
                .findAllAsync();
        return userList.sort("time", Sort.DESCENDING);
    }

    public RealmResults<WhiteListTable> queueWhiteData(String Imsi,String UserName) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<WhiteListTable> userList;
        if(Imsi.length()!=0&&UserName.length()!=0)
        {
            userList = mRealm.where(WhiteListTable.class)
                    .contains("imsi",Imsi)
                    .or()
                    .contains("phoneUsername",UserName)
                    .findAllAsync();

        }
        else if(Imsi.length()==0&&UserName.length()!=0)
        {
            userList = mRealm.where(WhiteListTable.class)
                    .contains("phoneUsername",UserName)
                    .findAllAsync();
        }
        else if(Imsi.length()!=0&&UserName.length()==0)
        {
            userList = mRealm.where(WhiteListTable.class)
                    .contains("imsi",Imsi)
                    .findAllAsync();
        }
        else
        {
            userList = mRealm.where(WhiteListTable.class)
                    .findAllAsync();
        }
        return userList.sort("imsi", Sort.DESCENDING);

    }

    public void crateOrBlackUpdate(final String imsi, final String imei, final String mobile,final String phoneUsername,final String position) {
        Log.d(TAG, "THREAD 1 :" + Thread.currentThread().getId());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "THREAD 2 :" + Thread.currentThread().getId());
//                    mRealm = Realm.getDefaultInstance();
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          Log.d(TAG, "THREAD 3 :" + Thread.currentThread().getId() + App.get().blackId);
                                                          BlackListTable blackListTable1 = realm.where(BlackListTable.class).equalTo("imsi", imsi).findFirst();
                                                          if (blackListTable1 == null) {
                                                              final BlackListTable blackListTable = new BlackListTable();
                                                              blackListTable.setId(++App.get().blackId);
                                                              blackListTable.setImei(imei);
                                                              blackListTable.setImsi(imsi);
                                                              blackListTable.setMobile(mobile);
                                                              blackListTable.setTime(System.currentTimeMillis());
                                                              blackListTable.setPhoneUsername(phoneUsername);
                                                              blackListTable.setPosition(position);
                                                              realm.copyToRealmOrUpdate(blackListTable);
                                                              if (imsi != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsi).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                                  }
                                                              } else if (imei != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imei", imei).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                                  }
                                                              } else if (mobile != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("mobile", mobile).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                                  }
                                                              }
                                                          } else {
                                                              ToastUtils.showToast(App.get().getApplicationContext(), "黑名单已存在", Toast.LENGTH_SHORT);
                                                          }
                                                      }
                                                  }
                    );
                } finally {
                }
            }
        });
    }


    public void BlackUpdate(final String preImsi,final String imsi, final String imei, final String mobile,final String phoneUsername,final String position) {
        Log.d(TAG, "THREAD 1 :" + Thread.currentThread().getId());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "THREAD 2 :" + Thread.currentThread().getId());
//                    mRealm = Realm.getDefaultInstance();
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          Log.d(TAG, "THREAD 3 :" + Thread.currentThread().getId() + App.get().blackId);
                                                          BlackListTable blackListTable1 = realm.where(BlackListTable.class).equalTo("imsi", preImsi).findFirst();

                                                              final BlackListTable blackListTable = new BlackListTable();
                                                              blackListTable.setId(blackListTable1.getId());
                                                              blackListTable.setImei(imei);
                                                              blackListTable.setImsi(imsi);
                                                              blackListTable.setMobile(mobile);
                                                              blackListTable.setTime(System.currentTimeMillis());
                                                              blackListTable.setPhoneUsername(phoneUsername);
                                                              blackListTable.setPosition(position);
                                                              realm.copyToRealmOrUpdate(blackListTable);
                                                              if (imsi != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsi).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                                  }
                                                              } else if (imei != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imei", imei).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                                  }
                                                              } else if (mobile != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("mobile", mobile).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                                  }
                                                              }

                                                      }
                                                  }
                    );
                } finally {
                }
            }
        });
    }






    public void crateOrUpdateWhite(final String imsi, final String imei, final String mobile,final String phoneUsernumber,final String position) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          WhiteListTable whiteListTable1 = null;
                                                          if (imsi != null) {
                                                              whiteListTable1 = realm.where(WhiteListTable.class).equalTo("imsi", imsi).findFirst();
                                                          } else if (imei != null) {
                                                              whiteListTable1 = realm.where(WhiteListTable.class).equalTo("imei", imei).findFirst();
                                                          } else if (mobile != null) {
                                                              whiteListTable1 = realm.where(WhiteListTable.class).equalTo("mobile", mobile).findFirst();
                                                          }
                                                          if (whiteListTable1 == null) {
                                                              final WhiteListTable whiteListTable = new WhiteListTable();
                                                              whiteListTable.setId(++App.get().whiteId);
                                                              whiteListTable.setImei(imei);
                                                              whiteListTable.setImsi(imsi);
                                                              whiteListTable.setMobile(mobile);
                                                              whiteListTable.setPhoneUsername(phoneUsernumber);
                                                              whiteListTable.setPosition(position);
                                                              whiteListTable.setTime(System.currentTimeMillis());
                                                              realm.copyToRealmOrUpdate(whiteListTable);
                                                              if (imsi != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsi).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(2);
                                                                  }
                                                              } else if (imei != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imei", imei).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(2);
                                                                  }
                                                              } else if (mobile != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("mobile", mobile).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(2);
                                                                  }
                                                              }
                                                          } else {
                                                              ToastUtils.showToast(App.get().getApplicationContext(), "白名单已存在", Toast.LENGTH_SHORT);
                                                          }

                                                      }
                                                  }
                    );

                } finally {
//                    mRealm.close();
                }
            }
        });
    }

    public void WhiteUpdate(final String preImsi,final String imsi, final String imei, final String mobile,final String phoneUsernumber,final String position) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          WhiteListTable whiteListTable1 = null;
                                                          if (preImsi != null) {
                                                              whiteListTable1 = realm.where(WhiteListTable.class).equalTo("imsi", preImsi).findFirst();
                                                          }
                                                              final WhiteListTable whiteListTable = new WhiteListTable();
                                                              whiteListTable.setId(whiteListTable1.getId());
                                                              whiteListTable.setImei(imei);
                                                              whiteListTable.setImsi(imsi);
                                                              whiteListTable.setMobile(mobile);
                                                              whiteListTable.setPhoneUsername(phoneUsernumber);
                                                              whiteListTable.setPosition(position);
                                                              whiteListTable.setTime(System.currentTimeMillis());
                                                              realm.copyToRealmOrUpdate(whiteListTable);
                                                              if (imsi != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsi).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(2);
                                                                  }
                                                              } else if (imei != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imei", imei).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(2);
                                                                  }
                                                              } else if (mobile != null) {
                                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("mobile", mobile).findAll();
                                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                                      imsiDataTable.setIsBlackAndWhite(2);
                                                                  }
                                                              }

                                                      }
                                                  }
                    );

                } finally {
//                    mRealm.close();
                }
            }
        });
    }






    public void addBlack(final ImsiDataTable imsiDataTable) {
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              BlackListTable blackListTable1 = realm.where(BlackListTable.class).equalTo("imsi", imsiDataTable.getImsi()).findFirst();
                                              if (blackListTable1 == null) {
                                                  final BlackListTable blackListTable = new BlackListTable();
                                                  blackListTable.setId(++App.get().blackId);
                                                  blackListTable.setMobile(imsiDataTable.getMobile());
                                                  blackListTable.setImei(imsiDataTable.getImei());
                                                  blackListTable.setImsi(imsiDataTable.getImsi());
                                                  blackListTable.setTime(System.currentTimeMillis());
                                                  realm.copyToRealmOrUpdate(blackListTable);
                                                  imsiDataTable.setIsBlackAndWhite(1);
                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsiDataTable.getImsi()).findAll();
                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                      imsiDataTable.setIsBlackAndWhite(1);
                                                  }
                                                  realm.copyToRealmOrUpdate(imsiDataTable);
                                              } else {
                                                  ToastUtils.showToast(App.get().getApplicationContext(), "黑名单已存在", Toast.LENGTH_SHORT);
                                              }
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }

    public void addWhite(final ImsiDataTable imsiDataTable) {
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              WhiteListTable whiteListTable1 = null;
                                              if (imsiDataTable.getImsi() != null) {
                                                  whiteListTable1 = realm.where(WhiteListTable.class).equalTo("imsi", imsiDataTable.getImsi()).findFirst();
                                              } else if (imsiDataTable.getImei() != null) {
                                                  whiteListTable1 = realm.where(WhiteListTable.class).equalTo("imei", imsiDataTable.getImei()).findFirst();
                                              } else if (imsiDataTable.getMobile() != null) {
                                                  whiteListTable1 = realm.where(WhiteListTable.class).equalTo("mobile", imsiDataTable.getMobile()).findFirst();
                                              }
                                              if (whiteListTable1 == null) {
                                                  final WhiteListTable whiteListTable = new WhiteListTable();
                                                  whiteListTable.setId(++App.get().whiteId);
                                                  whiteListTable.setMobile(imsiDataTable.getMobile());
                                                  whiteListTable.setImei(imsiDataTable.getImei());
                                                  whiteListTable.setImsi(imsiDataTable.getImsi());
                                                  whiteListTable.setTime(System.currentTimeMillis());
                                                  realm.copyToRealmOrUpdate(whiteListTable);
                                                  if (imsiDataTable.getImsi() != null) {
                                                      RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsiDataTable.getImsi()).findAll();
                                                      for (ImsiDataTable imsiDataTable : imsi1) {
                                                          imsiDataTable.setIsBlackAndWhite(2);
                                                      }
                                                  } else if (imsiDataTable.getImsi() != null) {
                                                      RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imei", imsiDataTable.getImei()).findAll();
                                                      for (ImsiDataTable imsiDataTable : imsi1) {
                                                          imsiDataTable.setIsBlackAndWhite(2);
                                                      }
                                                  } else if (imsiDataTable.getMobile() != null) {
                                                      RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("mobile", imsiDataTable.getMobile()).findAll();
                                                      for (ImsiDataTable imsiDataTable : imsi1) {
                                                          imsiDataTable.setIsBlackAndWhite(2);
                                                      }
                                                  }
                                                  imsiDataTable.setIsBlackAndWhite(2);
                                                  realm.copyToRealmOrUpdate(imsiDataTable);
                                              } else {
                                                  ToastUtils.showToast(App.get().getApplicationContext(), "白名单已存在", Toast.LENGTH_SHORT);
                                              }

                                          }
                                      }
            );
        } finally {
        }
    }

    public void deleteBlack(final BlackListTable blackListTable) {
        Log.d(TAG, "THREAD 4 :" + Thread.currentThread().getId());
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              Log.d(TAG, "THREAD 5 :" + Thread.currentThread().getId());
                                              RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", blackListTable.getImsi()).findAll();
                                              for (ImsiDataTable imsiDataTable : imsi1) {
                                                  imsiDataTable.setIsBlackAndWhite(0);
                                              }

                                              final RealmResults<BlackListTable> id = realm.where(BlackListTable.class).equalTo("id", blackListTable.getId()).findAll();
                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }


    public void clearBlack() {
        Log.d(TAG, "THREAD 4 :" + Thread.currentThread().getId());
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              Log.d(TAG, "THREAD 5 :" + Thread.currentThread().getId());

                                              final RealmResults<BlackListTable> id = realm.where(BlackListTable.class).findAll();
                                              for(BlackListTable bt:id) {
                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi",bt.getImsi()).findAll();
                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                      imsiDataTable.setIsBlackAndWhite(0);
                                                  }
                                              }
                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }




    public void deleteBlack(final ImsiDataTable imsiDataTable) {
        Log.d(TAG, "THREAD 4 :" + Thread.currentThread().getId());
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              Log.d(TAG, "THREAD 5 :" + Thread.currentThread().getId());
                                              RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsiDataTable.getImsi()).findAll();
                                              for (ImsiDataTable imsiDataTable : imsi1) {
                                                  imsiDataTable.setIsBlackAndWhite(0);
                                              }

                                              final RealmResults<BlackListTable> id = realm.where(BlackListTable.class).equalTo("imsi", imsiDataTable.getImsi()).findAll();
                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }

    public void deleteWhite(final ImsiDataTable imsiDataTable) {
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsiDataTable.getImsi()).findAll();
                                              for (ImsiDataTable imsiDataTable : imsi1) {
                                                  imsiDataTable.setIsBlackAndWhite(0);
                                              }

                                              final RealmResults<WhiteListTable> id = realm.where(WhiteListTable.class).equalTo("imsi", imsiDataTable.getImsi()).findAll();
                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }
    public void clearWhite() {
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {


                                              final RealmResults<WhiteListTable> id = realm.where(WhiteListTable.class).findAll();
                                              for(WhiteListTable wt:id) {
                                                  RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi",wt.getImsi()).findAll();
                                                  for (ImsiDataTable imsiDataTable : imsi1) {
                                                      imsiDataTable.setIsBlackAndWhite(0);
                                                  }
                                              }

                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }


    public void deleteWhite(final WhiteListTable whiteListTable) {
//        mRealm = Realm.getDefaultInstance();
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", whiteListTable.getImsi()).findAll();
                                              for (ImsiDataTable imsiDataTable : imsi1) {
                                                  imsiDataTable.setIsBlackAndWhite(0);
                                              }

                                              final RealmResults<WhiteListTable> id = mRealm.where(WhiteListTable.class).equalTo("id", whiteListTable.getId()).findAll();
                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }

    public Long findBlackId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(BlackListTable.class).max("id") != null) {
            return mRealm.where(BlackListTable.class).max("id").longValue();
        }
        return 0L;
    }

    public Long findWhiteId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(WhiteListTable.class).max("id") != null) {
            return mRealm.where(WhiteListTable.class).max("id").longValue();
        }
        return 0L;
    }

    public void findImsiDataByimsiAndimeiAndMobile(long mBeginMillseconds, long mEndMillseconds, String imsi, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimeiAndMobile(long mBeginMillseconds, long mEndMillseconds, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("mobile", mobile)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndMobile(long mBeginMillseconds, long mEndMillseconds, String imsi, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiByMobileData(long mBeginMillseconds, long mEndMillseconds, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("mobile", mobile)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndimeiAndMobile(String imsi, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {

//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimeiAndMobile(String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("mobile", mobile)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndMobile(String imsi, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiByMobileData(String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("mobile", mobile)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndimeiAndMobileAndOperator(long mBeginMillseconds, long mEndMillseconds, String operator, String imsi, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimeiAndMobileAndOperator(long mBeginMillseconds, long mEndMillseconds, String operator, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .equalTo("mobile", mobile)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndMobileAndOperator(long mBeginMillseconds, long mEndMillseconds, String operator, String imsi, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
        if (mEndMillseconds > System.currentTimeMillis()) {
            mEndMillseconds = System.currentTimeMillis();
        }
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .between("time", mBeginMillseconds, mEndMillseconds)
                .equalTo("operator", operator)
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndimeiAndMobileAndOperator(String operator, String imsi, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimeiAndMobileAndOperator(String operator, String imei, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .equalTo("mobile", mobile)
                .equalTo("imei", imei)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void findImsiDataByimsiAndMobileAndOperator(String operator, String imsi, String mobile, RealmChangeListener<RealmResults<ImsiDataTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                .lessThanOrEqualTo("time", System.currentTimeMillis())
                .equalTo("operator", operator)
                .equalTo("mobile", mobile)
                .equalTo("imsi", imsi)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void createOrUpdateImsi(final String imsi, final String mobile) {
        LETLog.d("createOrUpdateImsi :" + imsi + "---" + mobile);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
//                    mRealm = Realm.getDefaultInstance();
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransaction(new Realm.Transaction() {
                                                      @Override
                                                      public void execute(Realm realm) {
                                                          RealmResults<ImsiDataTable> imsi1 = realm.where(ImsiDataTable.class).equalTo("imsi", imsi).findAll();
                                                          LETLog.d("createOrUpdateImsi :" + imsi1.toString());
                                                          for (ImsiDataTable imsiDataTable : imsi1) {
                                                              imsiDataTable.setMobile(mobile);
                                                          }
                                                      }
                                                  }
                    );

                } finally {
//                    mRealm.close();
                }
            }
        });
    }

    public GsmConfig findGsmConfig(Long id) {
//        mRealm = Realm.getDefaultInstance();
        GsmConfigTable gsmConfigTable = mRealm.where(GsmConfigTable.class)
                .equalTo("id", id)
                .findFirst();
        if (gsmConfigTable != null) {
            return gsmConfigTable.builder();
        } else
            return null;
    }

    public void crateOrUpdate(final GsmConfig gsmConfig) {
        Log.d(TAG, "GsmConfig :" + gsmConfig);
        handler.post(new Runnable() {
            @Override
            public void run() {
//                mRealm = Realm.getDefaultInstance();
                if (mHandRealm == null) {
                    mHandRealm = Realm.getDefaultInstance();
                }
                try {
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            GsmConfigTable gsmConfigTable = new GsmConfigTable();
                            gsmConfigTable.id = gsmConfig.id;
                            gsmConfigTable.Enable1 = gsmConfig.Enable1;
                            gsmConfigTable.BAND1 = gsmConfig.BAND1;
                            gsmConfigTable.BCC1 = gsmConfig.BCC1;
                            gsmConfigTable.MCC1 = gsmConfig.MCC1;
                            gsmConfigTable.MNC1 = gsmConfig.MNC1;
                            gsmConfigTable.LAC1 = gsmConfig.LAC1;
                            gsmConfigTable.CRO1 = gsmConfig.CRO1;
                            gsmConfigTable.CAPTIME1 = gsmConfig.CAPTIME1;
                            gsmConfigTable.LOWATT1 = gsmConfig.LOWATT1;
                            gsmConfigTable.UPATT1 = gsmConfig.UPATT1;
                            gsmConfigTable.CONFIGMODE1 = gsmConfig.CONFIGMODE1;
                            gsmConfigTable.WORKMODE1 = gsmConfig.WORKMODE1;
                            gsmConfigTable.Enable2 = gsmConfig.Enable2;
                            gsmConfigTable.BAND2 = gsmConfig.BAND2;
                            gsmConfigTable.BCC2 = gsmConfig.BCC2;
                            gsmConfigTable.MCC2 = gsmConfig.MCC2;
                            gsmConfigTable.MNC2 = gsmConfig.MNC2;
                            gsmConfigTable.LAC2 = gsmConfig.LAC2;
                            gsmConfigTable.CRO2 = gsmConfig.CRO2;
                            gsmConfigTable.CAPTIME2 = gsmConfig.CAPTIME2;
                            gsmConfigTable.LOWATT2 = gsmConfig.LOWATT2;
                            gsmConfigTable.UPATT2 = gsmConfig.UPATT2;
                            gsmConfigTable.CONFIGMODE2 = gsmConfig.CONFIGMODE2;
                            gsmConfigTable.WORKMODE2 = gsmConfig.WORKMODE2;
                            realm.copyToRealmOrUpdate(gsmConfigTable);
                        }
                    });
                } catch (Exception ignored) {

                }
            }
        });
    }

    public CdmaConfig findCdmaConfig(Long id) {
//        mRealm = Realm.getDefaultInstance();
        CdmaConfigTable cdmaConfigTable = mRealm.where(CdmaConfigTable.class)
                .equalTo("id", id)
                .findFirst();
        if (cdmaConfigTable != null) {
            return cdmaConfigTable.builder();
        } else
            return null;
    }

    public GsmConfig findGsmConfigFrist() {
//        mRealm = Realm.getDefaultInstance();
        GsmConfigTable gsmConfigTable = mRealm.where(GsmConfigTable.class)
                .findFirst();
        if (gsmConfigTable != null) {
            return gsmConfigTable.builder();
        } else {
            GsmConfig gsmConfig = new GsmConfig();
            gsmConfig.id = 1l;
            gsmConfig.setCMD();
            return gsmConfig;
        }
    }

    public CdmaConfig findCdmaConfigFrist() {
//        mRealm = Realm.getDefaultInstance();
        CdmaConfigTable cdmaConfigTable = mRealm.where(CdmaConfigTable.class)
                .findFirst();
        if (cdmaConfigTable != null) {
            return cdmaConfigTable.builder();
        } else {
            CdmaConfig cdmaConfig = new CdmaConfig();
            cdmaConfig.id = 1L;
            cdmaConfig.setCMD();
            return cdmaConfig;
        }
    }

    public void crateOrUpdate(final CdmaConfig cmdaConfig) {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                mRealm = Realm.getDefaultInstance();
                try {
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            CdmaConfigTable cdmaConfigTable = new CdmaConfigTable();
                            cdmaConfigTable.id = cmdaConfig.id;
                            cdmaConfigTable.Enable = cmdaConfig.Enable;
                            cdmaConfigTable.MCC = cmdaConfig.MCC;
                            cdmaConfigTable.reDetectMinuts = cmdaConfig.reDetectMinuts;
                            cdmaConfigTable.SID = cmdaConfig.SID;
                            cdmaConfigTable.NID = cmdaConfig.NID;
                            cdmaConfigTable.PN = cmdaConfig.PN;
                            cdmaConfigTable.BSID = cmdaConfig.BSID;
                            cdmaConfigTable.REGNUM = cmdaConfig.REGNUM;
                            cdmaConfigTable.CAPTIME = cmdaConfig.CAPTIME;
                            cdmaConfigTable.LOWATT = cmdaConfig.LOWATT;
                            cdmaConfigTable.UPATT = cmdaConfig.UPATT;
                            cdmaConfigTable.SCANTIME = cmdaConfig.SCANTIME;
                            cdmaConfigTable.SCANPERIOD = cmdaConfig.SCANPERIOD;
                            cdmaConfigTable.FREQ1 = cmdaConfig.FREQ1;
                            cdmaConfigTable.FREQ2 = cmdaConfig.FREQ2;
                            cdmaConfigTable.FREQ3 = cmdaConfig.FREQ3;
                            cdmaConfigTable.FREQ4 = cmdaConfig.FREQ4;
                            cdmaConfigTable.SCANTIME1 = cmdaConfig.SCANTIME1;
                            cdmaConfigTable.SCANTIME2 = cmdaConfig.SCANTIME2;
                            cdmaConfigTable.SCANTIME3 = cmdaConfig.SCANTIME3;
                            cdmaConfigTable.SCANTIME4 = cmdaConfig.SCANTIME4;
                            cdmaConfigTable.SCANCAPTIME1 = cmdaConfig.SCANCAPTIME1;
                            cdmaConfigTable.SCANCAPTIME2 = cmdaConfig.SCANCAPTIME2;
                            cdmaConfigTable.SCANCAPTIME3 = cmdaConfig.SCANCAPTIME3;
                            cdmaConfigTable.SCANCAPTIME4 = cmdaConfig.SCANCAPTIME4;
                            cdmaConfigTable.NEIBOR1FREQ1 = cmdaConfig.NEIBOR1FREQ1;
                            cdmaConfigTable.NEIBOR2FREQ1 = cmdaConfig.NEIBOR2FREQ1;
                            cdmaConfigTable.NEIBOR3FREQ1 = cmdaConfig.NEIBOR3FREQ1;
                            cdmaConfigTable.NEIBOR4FREQ1 = cmdaConfig.NEIBOR4FREQ1;
                            cdmaConfigTable.NEIBOR1FREQ2 = cmdaConfig.NEIBOR1FREQ2;
                            cdmaConfigTable.NEIBOR2FREQ2 = cmdaConfig.NEIBOR2FREQ2;
                            cdmaConfigTable.NEIBOR3FREQ2 = cmdaConfig.NEIBOR3FREQ2;
                            cdmaConfigTable.NEIBOR4FREQ2 = cmdaConfig.NEIBOR4FREQ2;
                            cdmaConfigTable.NEIBOR1FREQ3 = cmdaConfig.NEIBOR1FREQ3;
                            cdmaConfigTable.NEIBOR2FREQ3 = cmdaConfig.NEIBOR2FREQ3;
                            cdmaConfigTable.NEIBOR3FREQ3 = cmdaConfig.NEIBOR3FREQ3;
                            cdmaConfigTable.NEIBOR4FREQ3 = cmdaConfig.NEIBOR4FREQ3;
                            cdmaConfigTable.NEIBOR1FREQ4 = cmdaConfig.NEIBOR1FREQ4;
                            cdmaConfigTable.NEIBOR2FREQ4 = cmdaConfig.NEIBOR2FREQ4;
                            cdmaConfigTable.NEIBOR3FREQ4 = cmdaConfig.NEIBOR3FREQ4;
                            cdmaConfigTable.NEIBOR4FREQ4 = cmdaConfig.NEIBOR4FREQ4;
                            cdmaConfigTable.MNC = cmdaConfig.MNC;
                            cdmaConfigTable.WORKMODEL = cmdaConfig.WORKMODEL;
                            cdmaConfigTable.RESETMODEL = cmdaConfig.RESETMODEL;
                            cdmaConfigTable.WORKMODE1 = cmdaConfig.WORKMODE1;
                            cdmaConfigTable.WORKMODE2 = cmdaConfig.WORKMODE2;
                            cdmaConfigTable.WORKMODE3 = cmdaConfig.WORKMODE3;
                            cdmaConfigTable.WORKMODE4 = cmdaConfig.WORKMODE4;
                            realm.copyToRealmOrUpdate(cdmaConfigTable);
                        }
                    });
                }catch (Exception E){

                }

            }
        });
    }

    public RealmResults<SceneTable> findSceneList() {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<SceneTable> userList = mRealm.where(SceneTable.class)
                .findAll();
        return userList.sort("addTime", Sort.DESCENDING);
    }

    public Long findSceneId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(SceneTable.class).max("id") != null) {
            return mRealm.where(SceneTable.class).max("id").longValue();
        }
        return 0L;
    }

    public void createOrUpdateScene(final String name, final long mBeginMillseconds, final long mEndMillseconds) {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                mRealm = Realm.getDefaultInstance();
                try {
                    if (mHandRealm == null) {
                        mHandRealm = Realm.getDefaultInstance();
                    }
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SceneTable sceneTable = new SceneTable();
                            sceneTable.setId(++App.get().sceneID);
                            sceneTable.setAddTime(System.currentTimeMillis());
                            sceneTable.setName(name);
                            sceneTable.setmBeginMillseconds(mBeginMillseconds);
                            sceneTable.setmEndMillseconds(mEndMillseconds);
                            realm.copyToRealmOrUpdate(sceneTable);
                        }
                    });
                }catch (Exception e){

                }

            }
        });
    }

    public void createOrUpdateScene(final long mBeginMillseconds, final long mEndMillseconds, final Long Id) {
//        mRealm = Realm.getDefaultInstance();
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              SceneTable sceneTable = mRealm.where(SceneTable.class).equalTo("id", Id).findFirst();
                                              sceneTable.setmBeginMillseconds(mBeginMillseconds);
                                              sceneTable.setmEndMillseconds(mEndMillseconds);
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }

    public void createOrUpdateScene(final String name, final Long Id) {
//        mRealm = Realm.getDefaultInstance();
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              SceneTable sceneTable = mRealm.where(SceneTable.class).equalTo("id", Id).findFirst();
                                              sceneTable.setName(name);
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }

    public void crateScene() {
        handler.post(new Runnable() {
            @Override
            public void run() {
//                mRealm = Realm.getDefaultInstance();
                if (mHandRealm == null) {
                    mHandRealm = Realm.getDefaultInstance();
                }
                try {
                    mHandRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SceneTable sceneTable = new SceneTable();
                            sceneTable.setId(1L);
                            sceneTable.setName(App.get().getApplicationContext().getString(R.string.newScene));
                            sceneTable.setAddTime(System.currentTimeMillis());
                            realm.copyToRealmOrUpdate(sceneTable);
                        }
                    });
                }catch (Exception e){

                }

            }
        });
    }

//    public RealmResults<SceneTable> findSceneNotContainFirstList() {
//        mRealm = Realm.getDefaultInstance();
//        RealmResults<SceneTable> userList = mRealm.where(SceneTable.class)
//                .findAll();
//        return userList.sort("addTime",Sort.DESCENDING);
//    }

    public StationInfo findStatInfoById(Long id) {
//        mRealm = Realm.getDefaultInstance();
        StationInfoTable id1 = mRealm.where(StationInfoTable.class)
                .equalTo("id", id)
                .findFirst();
        return id1 != null ? id1.createStationInfo() : null;
    }

    public String getCacheSize() {
//        mRealm = Realm.getDefaultInstance();
        return getFileSizeString(mRealm.getPath());
    }

    public void removeAll() {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }
    public void remove(final long time) {
//        mRealm = Realm.getDefaultInstance();
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                                                      .lessThan("time", time)
                                                      .findAll();
                                              if (userList != null) {
                                                  userList.deleteAllFromRealm();
                                              }
                                          }
                                      }
            );
        }catch (Exception e){

        }
    }
    public void deleteScene(final SceneTable sceneTable) {
//        mRealm = Realm.getDefaultInstance();
        try {
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              Log.d(TAG, "THREAD 5 :" + Thread.currentThread().getId());
                                              RealmResults<ImsiDataTable> userList = mRealm.where(ImsiDataTable.class)
                                                      .between("time", sceneTable.getmBeginMillseconds(), sceneTable.getmEndMillseconds())
                                                      .findAll();
                                              if (userList != null) {
                                                  userList.deleteAllFromRealm();
                                              }
                                              final RealmResults<SceneTable> id = realm.where(SceneTable.class).equalTo("id", sceneTable.getId()).findAll();
                                              id.deleteAllFromRealm();
                                          }
                                      }
            );
        }catch (Exception e){

        }

    }

    public void crateOrUpdateMobileResult(final String condition) {
        Log.d(TAG, " crateOrUpdateMobileResult :" + condition);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
//        mRealm = Realm.getDefaultInstance();
                    mRealm.executeTransaction(new Realm.Transaction() {
                                                  @Override
                                                  public void execute(@NonNull Realm realm) {
                                                      MobileResultTable mobileResultTable = new MobileResultTable();
                                                      mobileResultTable.setId(++App.get().mobileResultId);
                                                      mobileResultTable.setMobile(condition);
                                                      realm.copyToRealmOrUpdate(mobileResultTable);
                                                  }
                                              }
                    );
                } catch (Exception ignored) {

                }
            }
        });
    }

    public void findMobileResult(RealmChangeListener<RealmResults<MobileResultTable>> realmChangeListener) {
//        mRealm = Realm.getDefaultInstance();
        RealmResults<MobileResultTable> userList = mRealm.where(MobileResultTable.class)
                .findAllAsync();
        userList.addChangeListener(realmChangeListener);
    }

    public void deleteMobileResult(final String condition) {
        try {
//            mRealm = Realm.getDefaultInstance();
            mRealm.executeTransaction(new Realm.Transaction() {
                                          @Override
                                          public void execute(Realm realm) {
                                              MobileResultTable mobileResultTable = realm.where(MobileResultTable.class).equalTo("mobile", condition).findFirst();
                                              if (mobileResultTable != null) {
                                                  mobileResultTable.deleteFromRealm();
                                              }
                                          }
                                      }
            );
        } finally {
//            mRealm.close();
        }
    }

    public Long findMobileResultId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(WhiteListTable.class).max("id") != null) {
            return mRealm.where(WhiteListTable.class).max("id").longValue();
        }
        return 0L;
    }

    public OrderedRealmCollection<DeviceTypeTable> findDeviceType() {
//        mRealm = Realm.getDefaultInstance();
        return mRealm.where(DeviceTypeTable.class)
                .findAllAsync();
    }

    public DeviceTypeTable findDeviceTypebyName(String name) {
//        mRealm = Realm.getDefaultInstance();
        return mRealm.where(DeviceTypeTable.class)
                .contains("name", name)
                .findFirst();
    }

    public void addDeviceType(final DeviceTypeTable deviceTypeTable) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          realm.copyToRealmOrUpdate(deviceTypeTable);
                                      }
                                  }
        );
    }

    public Long findDeviceTypeId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(DeviceTypeTable.class).max("id") != null) {
            return mRealm.where(DeviceTypeTable.class).max("id").longValue();
        }
        return 0L;
    }

    public void deleteDeviceType(final DeviceTypeTable deviceTypeTable) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          DeviceTypeTable deviceTypeTable1 = realm.where(DeviceTypeTable.class).equalTo("id", deviceTypeTable.getId()).findFirst();
                                          deviceTypeTable1.deleteFromRealm();
                                      }
                                  }
        );
    }

    public void upGradeDeviceType(final DeviceTypeTable selectDeviceType, final byte[] bbuList) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          selectDeviceType.setBbuList(bbuList);
                                      }
                                  }
        );
    }

    public void clearScanResult(final StationInfo stationInfo) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                StationInfoTable id1 = mRealm.where(StationInfoTable.class)
                        .equalTo("id", stationInfo.getId())
                        .findFirst();
                if (id1 != null) {
                    id1.getResultTables().clear();
                }
            }
        });
    }

    public OrderedRealmCollection<BandTable> findBandTable() {
//        mRealm = Realm.getDefaultInstance();
        return mRealm.where(BandTable.class)
                .findAllAsync();
    }

    public void deleteBand(final BandTable bandTable) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          BandTable bandTable1 = realm.where(BandTable.class).equalTo("id", bandTable.getId()).findFirst();
                                          if (bandTable1 != null) {
                                              bandTable1.deleteFromRealm();
                                          }
                                      }
                                  }
        );
    }

    public void upGradeBand(final BandTable selectBandTable, final int bytes1) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          RealmInteger realmInteger = new RealmInteger();
                                          realmInteger.setNumber(bytes1);
                                          selectBandTable.getPoint().add(realmInteger);
                                      }
                                  }
        );
    }

    public Long findBandId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(BandTable.class).max("id") != null) {
            return mRealm.where(BandTable.class).max("id").longValue();
        }
        return 0L;
    }

    public void addBand(final BandTable bandTable) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          realm.copyToRealmOrUpdate(bandTable);
                                      }
                                  }
        );
    }

    public void deletePoint(final BandTable selectBandTable, final RealmInteger data) {
//        mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
                                      @Override
                                      public void execute(Realm realm) {
                                          selectBandTable.getPoint().remove(data);
                                      }
                                  }
        );
    }

    public BandTable findBandByName(String name) {
//        mRealm = Realm.getDefaultInstance();
        return mRealm.where(BandTable.class)
                .contains("name", name)
                .findFirst();
    }

    public Long findStationId() {
//        mRealm = Realm.getDefaultInstance();
        if (mRealm.where(StationInfoTable.class).max("id") != null) {
            return mRealm.where(StationInfoTable.class).max("id").longValue();
        }
        return 0L;
    }
}
