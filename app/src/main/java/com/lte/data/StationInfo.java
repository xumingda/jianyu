package com.lte.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.library.entity.MultiItemEntity;
import com.lte.data.table.RealmInteger;

import org.apache.mina.core.session.IoSession;


import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.RealmList;


/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class StationInfo implements Parcelable, Cloneable,MultiItemEntity {


    private Long id  = null;

    private boolean isConfig;
    private byte dbm;

    private ScanSet scanSet;

    private CellConfig cellConfig;
    private boolean isCellConfig;

    private String bbu;
    private boolean close;

    private String state;

    private boolean isOpen;
    private boolean isConfigDBM;

    private ConfigState configState = ConfigState.UN_CONFIG;

    private String soft_state;

    private int cpu_tem;

    private int cpu_use;

    private int rom_use;
    private ArrayList<Integer> system;
    private int tem;

    private ArrayList<ScanResult> mList = new ArrayList<>();
    private ArrayList<ScanResult> scanResults = new ArrayList<>();
    private Long scanResultId;
    private boolean isshoudong;
    private boolean shoudongSend;
    private int freq;
    private int pci;
    private int TAC;
    private int RSSI;
    private long receivedTime;
    private ArrayList<Integer> msg;
    private ArrayList<Integer> verMsg;
    private String softVer1;
    private String softVer2;
    private String softVer3;
    private String softVer4;
    private int type;
    private String MCC;//国家码
    private String MNC;//网络码
    private String LAC;//位置区号
    private String CI;//小区号
    private String CRO;//小区重选偏置
    private byte workModel;//工作模式
    private byte configModel;//配置模式
    private int recaptureTime;//重新捕获间隔时间
    private String carrierFrequencyPoint;//载波频点
    private String downlinkAttenuation;//下行衰减
    private String uplinkAttenuation;//上行衰减
    private int startingFrequencyPoint1;//起始频点1
    private int endFrequencyPoint1;//结束频点1
    private int startingFrequencyPoint2;//起始频点2
    private int endFrequencyPoint2;//结束频点2
    private int frequencyOffset;//频率偏移
    private boolean closeDbm;
    private String MCC1;//国家码
    private String MNC1;//网络码
    private String LAC1;//位置区号
    private String CI1;//小区号
    private String CRO1;//小区重选偏置
    private byte workModel1;//工作模式
    private byte configModel1;//配置模式
    private int recaptureTime1;//重新捕获间隔时间
    private String carrierFrequencyPoint1;//载波频点
    private String downlinkAttenuation1;//下行衰减
    private String uplinkAttenuation1;//上行衰减
    private int startingFrequencyPoint11;//起始频点1
    private int endFrequencyPoint11;//结束频点1
    private int startingFrequencyPoint21;//起始频点2
    private int endFrequencyPoint21;//结束频点2
    private int frequencyOffset1;//频率偏移


    private int create;//连接次数


    private int rxlevmin;//最小接收电平



    public static final Creator<StationInfo> CREATOR = new Creator<StationInfo>() {
        @Override
        public StationInfo createFromParcel(Parcel in) {
            return new StationInfo(in);
        }

        @Override
        public StationInfo[] newArray(int size) {
            return new StationInfo[size];
        }
    };
    private String pn;
    private String sid;
    private String nid;
    private String bsid;
    private String cmda;
    private int workModel2;
    private boolean openDbm;
    private boolean restart;
    private ArrayList<Integer> scanResult;
    private boolean positionOn;//目标上号后关闭输出标记

    private ArrayList<Integer> scanResultMsg;

    private int supportBand;
    private int TDDtype;//0：TDD 1：FDD
    private long queryTime;
    private int point;
    private long currentTime;
    private boolean startPosition;//定位开始上号标记
    private long positionTime;//最后接到定位数据时间
    private byte[] redirectCmd;
    private boolean positionOFF;
    private boolean updateCellOpen;

    public ConfigState getConfigState() {
        return configState;
    }

    public void setConfigState(ConfigState configState) {
        this.configState = configState;
    }

    public String getSoft_state() {
        return soft_state;
    }

    public void setSoft_state(String soft_state) {
        this.soft_state = soft_state;
    }

    public int getCpu_tem() {
        return cpu_tem;
    }

    public void setCpu_tem(int cpu_tem) {
        this.cpu_tem = cpu_tem;
    }

    public int getCpu_use() {
        return cpu_use;
    }

    public void setCpu_use(int cpu_use) {
        this.cpu_use = cpu_use;
    }

    public int getRom_use() {
        return rom_use;
    }

    public void setRom_use(int rom_use) {
        this.rom_use = rom_use;
    }

    public void setSystem(ArrayList<Integer> system) {
        this.system = system;
    }

    public ArrayList<Integer> getSystem() {
        return system;
    }

    public void setTem(int tem) {
        this.tem = tem;
    }

    public int getTem() {
        return tem;
    }



    public int getRxlevmin(){return rxlevmin;}
    public void setRxlevmin(int value){this.rxlevmin=value;};

    @Override
    public String toString() {
        return "StationInfo{" +
                "id=" + id +
                ", isConfig=" + isConfig +
                ", dbm=" + dbm +
                ", type=" + type +
                ", scanSet=" + scanSet +
                ", cellConfig=" + cellConfig +
                ", isCellConfig=" + isCellConfig +
                ", bbu='" + bbu + '\'' +
                ", close=" + close +
                ", state='" + state + '\'' +
                ", isOpen=" + isOpen +
                ", isConfigDBM=" + isConfigDBM +
                ", configState=" + configState +
                ", soft_state='" + soft_state + '\'' +
                ", cpu_tem=" + cpu_tem +
                ", cpu_use=" + cpu_use +
                ", rom_use=" + rom_use +
                ", system=" + system +
                ", tem=" + tem +
                ", name='" + name + '\'' +
                ", Ip='" + Ip + '\'' +
                ", session=" + session +
                ", isConnected=" + isConnected +
                ", mDataList=" + mDataList +
                ", initConfig=" + initConfig +
                '}';
    }

    public ArrayList<ScanResult> getmList() {
//        if(mList == null){
//            return new ArrayList<>();
//        }
        return mList;
    }

    public void setmList(ArrayList<ScanResult> mList) {
        this.mList = mList;
    }

    public void setScanResultId(Long id) {
        this.scanResultId = id;
    }

    public Long getScanResultId() {
        if(scanResultId == null){
            scanResultId = 1l;
        }
        return ++scanResultId;
    }

    public void setisshoudong(boolean isshoudong) {
        this.isshoudong = isshoudong;
    }

    public boolean isIsshoudong() {
        return isshoudong;
    }

    public void setShoudongSend(boolean shoudongSend) {
        this.shoudongSend = shoudongSend;
    }

    public boolean isShoudongSend() {
        return shoudongSend;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void setPci(int pci) {
        this.pci = pci;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int getFreq() {
        return freq;
    }

    public int getPci() {
        return pci;
    }

    public int getTAC() {
        return TAC;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public void setMsg(ArrayList<Integer> msg) {
        this.msg = msg;
    }

    public ArrayList<Integer> getMsg() {
        return msg;
    }

    public void setVerMsg(ArrayList<Integer> verMsg) {
        this.verMsg = verMsg;
    }

    public ArrayList<Integer> getVerMsg() {
        return verMsg;
    }

    public void setSoftVer1(String softVer1) {
        this.softVer1 = softVer1;
    }

    public void setSoftVer2(String softVer2) {
        this.softVer2 = softVer2;
    }

    public void setSoftVer3(String softVer3) {
        this.softVer3 = softVer3;
    }

    public void setSoftVer4(String softVer4) {
        this.softVer4 = softVer4;
    }

    public String getSoftVer1() {
        return softVer1;
    }

    public String getSoftVer2() {
        return softVer2;
    }

    public String getSoftVer3() {
        return softVer3;
    }

    public String getSoftVer4() {
        return softVer4;
    }

    public int getAddTAC() {
        return cellConfig.getAddTac();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemType() {
        return type;
    }

    public void setMCC(String MCC) {
        this.MCC = MCC;
    }

    public String getMCC() {
        return MCC;
    }

    public void setMNC(String MNC) {
        this.MNC = MNC;
    }

    public void setLAC(String LAC) {
        this.LAC = LAC;
    }

    public void setCI(String CI) {
        this.CI = CI;
    }

    public void setCRO(String CRO) {
        this.CRO = CRO;
    }

    public void setWorkModel(byte workModel) {
        this.workModel = workModel;
    }

    public void setconfigModel(byte configModel) {
        this.configModel = configModel;
    }

    public void setRecaptureTime(int recaptureTime) {
        this.recaptureTime = recaptureTime;
    }

    public void setCarrierFrequencyPoint(String carrierFrequencyPoint) {
        this.carrierFrequencyPoint = carrierFrequencyPoint;
    }

    public void setDownlinkAttenuation(String downlinkAttenuation) {
        this.downlinkAttenuation = downlinkAttenuation;
    }

    public void setUplinkAttenuation(String uplinkAttenuation) {
        this.uplinkAttenuation = uplinkAttenuation;
    }

    public void setStartingFrequencyPoint1(int startingFrequencyPoint1) {
        this.startingFrequencyPoint1 = startingFrequencyPoint1;
    }

    public void setEndFrequencyPoint1(int endFrequencyPoint1) {
        this.endFrequencyPoint1 = endFrequencyPoint1;
    }

    public void setStartingFrequencyPoint2(int startingFrequencyPoint2) {
        this.startingFrequencyPoint2 = startingFrequencyPoint2;
    }

    public void setEndFrequencyPoint2(int endFrequencyPoint2) {
        this.endFrequencyPoint2 = endFrequencyPoint2;
    }

    public void setFrequencyOffset(int frequencyOffset) {
        this.frequencyOffset = frequencyOffset;
    }

    public String getMNC() {
        return MNC;
    }

    public String getLAC() {
        return LAC;
    }

    public String getCI() {
        return CI;
    }

    public String getCRO() {
        return CRO;
    }

    public byte getWorkModel() {
        return workModel;
    }

    public byte getConfigModel() {
        return configModel;
    }

    public void setConfigModel(byte configModel) {
        this.configModel = configModel;
    }

    public int getRecaptureTime() {
        return recaptureTime;
    }

    public String getCarrierFrequencyPoint() {
        return carrierFrequencyPoint;
    }

    public String getDownlinkAttenuation() {
        return downlinkAttenuation;
    }

    public String getUplinkAttenuation() {
        return uplinkAttenuation;
    }

    public int getStartingFrequencyPoint1() {
        return startingFrequencyPoint1;
    }

    public int getEndFrequencyPoint1() {
        return endFrequencyPoint1;
    }

    public int getStartingFrequencyPoint2() {
        return startingFrequencyPoint2;
    }

    public int getEndFrequencyPoint2() {
        return endFrequencyPoint2;
    }

    public int getFrequencyOffset() {
        return frequencyOffset;
    }

    public boolean isCloseDbm() {
        return closeDbm;
    }

    public void setCloseDbm(boolean closeDbm) {
        this.closeDbm = closeDbm;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeByte((byte) (isConfig ? 1 : 0));
        dest.writeByte(dbm);
        dest.writeParcelable(scanSet, flags);
        dest.writeParcelable(cellConfig, flags);
        dest.writeString(name);
        dest.writeString(Ip);
        dest.writeParcelable(initConfig, flags);
        dest.writeInt(type);
    }

    public String getMCC1() {
        return MCC1;
    }

    public void setMCC1(String MCC1) {
        this.MCC1 = MCC1;
    }

    public String getMNC1() {
        return MNC1;
    }

    public void setMNC1(String MNC1) {
        this.MNC1 = MNC1;
    }

    public String getLAC1() {
        return LAC1;
    }

    public void setLAC1(String LAC1) {
        this.LAC1 = LAC1;
    }

    public String getCI1() {
        return CI1;
    }

    public void setCI1(String CI1) {
        this.CI1 = CI1;
    }

    public String getCRO1() {
        return CRO1;
    }

    public void setCRO1(String CRO1) {
        this.CRO1 = CRO1;
    }

    public byte getWorkModel1() {
        return workModel1;
    }

    public void setWorkModel1(byte workModel1) {
        this.workModel1 = workModel1;
    }

    public byte getConfigModel1() {
        return configModel1;
    }

    public void setConfigModel1(byte configModel1) {
        this.configModel1 = configModel1;
    }

    public int getRecaptureTime1() {
        return recaptureTime1;
    }

    public void setRecaptureTime1(int recaptureTime1) {
        this.recaptureTime1 = recaptureTime1;
    }

    public String getCarrierFrequencyPoint1() {
        return carrierFrequencyPoint1;
    }

    public void setCarrierFrequencyPoint1(String carrierFrequencyPoint1) {
        this.carrierFrequencyPoint1 = carrierFrequencyPoint1;
    }

    public String getDownlinkAttenuation1() {
        return downlinkAttenuation1;
    }

    public void setDownlinkAttenuation1(String downlinkAttenuation1) {
        this.downlinkAttenuation1 = downlinkAttenuation1;
    }

    public String getUplinkAttenuation1() {
        return uplinkAttenuation1;
    }

    public void setUplinkAttenuation1(String uplinkAttenuation1) {
        this.uplinkAttenuation1 = uplinkAttenuation1;
    }

    public int getStartingFrequencyPoint11() {
        return startingFrequencyPoint11;
    }

    public void setStartingFrequencyPoint11(int startingFrequencyPoint11) {
        this.startingFrequencyPoint11 = startingFrequencyPoint11;
    }

    public int getEndFrequencyPoint11() {
        return endFrequencyPoint11;
    }

    public void setEndFrequencyPoint11(int endFrequencyPoint11) {
        this.endFrequencyPoint11 = endFrequencyPoint11;
    }

    public int getStartingFrequencyPoint21() {
        return startingFrequencyPoint21;
    }

    public void setStartingFrequencyPoint21(int startingFrequencyPoint21) {
        this.startingFrequencyPoint21 = startingFrequencyPoint21;
    }

    public int getEndFrequencyPoint21() {
        return endFrequencyPoint21;
    }

    public void setEndFrequencyPoint21(int endFrequencyPoint21) {
        this.endFrequencyPoint21 = endFrequencyPoint21;
    }

    public int getFrequencyOffset1() {
        return frequencyOffset1;
    }

    public void setFrequencyOffset1(int frequencyOffset1) {
        this.frequencyOffset1 = frequencyOffset1;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPn() {
        return pn;
    }

    public String getSid() {
        return sid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public void setBsid(String bsid) {
        this.bsid = bsid;
    }

    public void setCmda(String cmda) {
        this.cmda = cmda;
    }

    public void setWorkModel2(int workModel2) {
        this.workModel2 = workModel2;
    }

    public String getNid() {
        return nid;
    }

    public String getBsid() {
        return bsid;
    }

    public String getCmda() {
        return cmda;
    }

    public int getWorkModel2() {
        return workModel2;
    }

    public boolean isOpenDbm() {
        return openDbm;
    }

    public void setOpenDbm(boolean openDbm) {
        this.openDbm = openDbm;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public boolean isRestart() {
        return restart;
    }

    public void setScanResult(ArrayList<Integer> scanResult) {
        this.scanResult = scanResult;
    }

    public ArrayList<Integer> getScanResult() {
        return scanResult;
    }

    public ArrayList<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setPositionOn(boolean positionOn) {
        this.positionOn = positionOn;
    }

    public boolean isPositionOn() {
        return positionOn;
    }


    public void setScanResultMsg(ArrayList<Integer> scanResultMsg) {
        this.scanResultMsg = scanResultMsg;
    }

    public ArrayList<Integer> getScanResultMsg() {
        return scanResultMsg;
    }



    public void setSupportBand(int supportBand) {
        this.supportBand = supportBand;
    }

    public int getSupportBand() {
        return supportBand;
    }

    public void setTDDtype(int TDDtype) {
        this.TDDtype = TDDtype;
    }

    public int getTDDtype() {
        return TDDtype;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public int getPoint() {
        return ++point;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationInfo that = (StationInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public void setStartPosition(boolean startPosition) {
        this.startPosition = startPosition;
    }

    public boolean isStartPosition() {
        return startPosition;
    }

    public void setPositionTime(long positionTime) {
        this.positionTime = positionTime;
    }

    public void setRedirectCmd(byte[] redirectCmd) {
        this.redirectCmd = redirectCmd;
    }

    public byte[] getRedirectCmd() {
        return redirectCmd;
    }

    public int getCreate() {
        return create;
    }

    public void setCreate(int create) {
        this.create = create;
    }

    public void setPositionOFF(boolean positionOFF) {
        this.positionOFF = positionOFF;
    }

    public boolean isPositionOFF() {
        return positionOFF;
    }

    public boolean isUpdateCellOpen() {
        return updateCellOpen;
    }

    public void setUpdateCellOpen(boolean updateCellOpen) {
        this.updateCellOpen = updateCellOpen;
    }

    public enum ConfigState{
        UN_CONFIG(0), INIT_CONFIG_ING(1), INIT_CONFIG_ED(2),
        SET_SYSTEM_TIME(3),CLOSE_DBM(4),QUERY_SYSTEM_STATUS(5),
        START_SCAN(6),SCAN_ED(7),CELL_CONFIG_ING(8),CELL_CONFIG_ED(9),OPEN_DBM(10),
        OPEN_DBM_SUCCESS(11);
        private final int value;

        ConfigState(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public StationInfo(){

    }



    public StationInfo(InitConfig initConfig) {
        this.initConfig = initConfig;
    }


    protected StationInfo(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        isConfig = in.readByte() != 0;
        dbm = in.readByte();
        scanSet = in.readParcelable(ScanSet.class.getClassLoader());
        cellConfig = in.readParcelable(CellConfig.class.getClassLoader());
        name = in.readString();
        Ip = in.readString();
        initConfig = in.readParcelable(InitConfig.class.getClassLoader());
        type = in.readInt();
    }



    public synchronized CopyOnWriteArrayList<String> getmDataList() {
        if(mDataList == null){
            mDataList = new CopyOnWriteArrayList<>();
        }
        return mDataList;
    }

    public void setmDataList(CopyOnWriteArrayList<String> mDataList) {
        this.mDataList = mDataList;
    }

    public Long getId() {
        return id;
    }


    public InitConfig getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(InitConfig initConfig) {
        this.initConfig = initConfig;
    }

    public boolean isConfig() {
        return isConfig;
    }

    public void setConfig(boolean config) {
        isConfig = config;
    }

    public byte getDBM() {
        return dbm;
    }

    public void setDbm(byte dbm) {
        this.dbm = dbm;
    }

    public ScanSet getScanSet() {
        return scanSet;
    }

    public void setScanSet(ScanSet scanSet) {
        this.scanSet = scanSet;
    }

    public CellConfig getCellConfig() {
        return cellConfig;
    }

    public void setCellConfig(CellConfig cellConfig) {
        this.cellConfig = cellConfig;
    }



    public void setIsCellConfig(boolean isCellConfig) {
        this.isCellConfig = isCellConfig;
    }

    public boolean isCellConfig(){
        return isCellConfig;
    }

    public String getBbu() {
        return bbu;
    }

    public void setBbu(String bbu) {
        this.bbu = bbu;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public boolean isClose() {
        return close;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isConfigDBM() {
        return isConfigDBM;
    }

    public void setConfigDBM(boolean configDBM) {
        isConfigDBM = configDBM;
    }


    public enum ConnectionStatus {
        CRATE(0), CONNECTED(1), DISCONNECTED(2), SCAN(3);

        private final int value;

        ConnectionStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }


    private String name;


    private String Ip;



    private IoSession session;


    private ConnectionStatus isConnected = ConnectionStatus.DISCONNECTED;

    private CopyOnWriteArrayList<String> mDataList;

    private InitConfig initConfig;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip,boolean isAdd) {
        Ip = ip;
        if(isAdd){
            if(ip.length() >16){
                String substring = ip.substring(13, 16);
                switch (substring){
                    case "211": {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(38);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        dbm = 5;
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(37900);
                        arrayList.add(37900);
                        arrayList.add(38098);
                        arrayList.add(38098);
                        scanSet.setEarfchList(arrayList);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(37900);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46000f",16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(37900);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                    }
                        break;
                    case "212": {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(39);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        dbm = 30;
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(38400);
                        arrayList.add(38400);
                        arrayList.add(38544);
                        arrayList.add(38544);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(38400);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46000f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(38400);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    case "213": {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(40);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        dbm = 30;
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(38950);
                        arrayList.add(38950);
                        arrayList.add(39148);
                        arrayList.add(39148);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(38950);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46000f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(38950);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    case "216":{
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(3);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        dbm = 30;
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(1825);
                        arrayList.add(1825);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(1825);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46011f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(19825);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    case "217":{
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(3);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        dbm = 30;
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(1650);
                        arrayList.add(1650);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(1650);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46001f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(19650);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    default: {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(3);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        dbm = 30;
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(1650);
                        arrayList.add(1650);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(1650);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46001f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(19650);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }

                }
            }else{
                initConfig = new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setTimeDelayField(0);
                initConfig.setSynchronousMode(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(3);
                initConfig.setId(id);
                initConfig.setIp(ip);
                dbm = 30;
                scanSet = new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(1650);
                arrayList.add(1650);
                scanSet.setEarfchList(arrayList);
                scanSet.setId(id);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(1650);
                cellConfig.setCell_pci(393);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46001f", 16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(600);
                cellConfig.setTac(9880);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(19650);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                cellConfig.setId(id);
            }
        }
    }

    public void initExceptDbm(String ip,boolean isAdd) {
        Ip = ip;
        if(isAdd){
            if(ip.length() >16){
                String substring = ip.substring(13, 16);
                switch (substring){
                    case "211": {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(38);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(37900);
                        arrayList.add(37900);
                        arrayList.add(38098);
                        arrayList.add(38098);
                        scanSet.setEarfchList(arrayList);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(37900);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46000f",16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(37900);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                    }
                    break;
                    case "212": {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(39);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(38400);
                        arrayList.add(38400);
                        arrayList.add(38544);
                        arrayList.add(38544);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(38400);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46000f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(38400);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    case "213": {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(40);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(38950);
                        arrayList.add(38950);
                        arrayList.add(39148);
                        arrayList.add(39148);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(38950);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46000f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(38950);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    case "216":{
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(3);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(1825);
                        arrayList.add(1825);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(1825);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46011f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(19825);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    case "217":{
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(3);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(1650);
                        arrayList.add(1650);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(1650);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46001f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(19650);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }
                    default: {
                        initConfig = new InitConfig();
                        initConfig.setBandwidth(2);
                        initConfig.setTimeDelayField(0);
                        initConfig.setSynchronousMode(2);
                        initConfig.setFrequencyOffset(0);
                        initConfig.setOperatingBand(3);
                        initConfig.setId(id);
                        initConfig.setIp(ip);
                        scanSet = new ScanSet();
                        scanSet.setRssi((byte) 60);
                        scanSet.setScan_result((byte) 0);
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        arrayList.add(1650);
                        arrayList.add(1650);
                        scanSet.setEarfchList(arrayList);
                        scanSet.setId(id);
                        ArrayList<Integer> arrayList1 = new ArrayList<>();
                        arrayList1.add(0);
                        scanSet.setPciList(arrayList1);
                        cellConfig = new CellConfig();
                        cellConfig.setDownlink_frequency_point(1650);
                        cellConfig.setCell_pci(393);
                        ArrayList<Integer> arrayList2 = new ArrayList<>();
                        arrayList2.add(Integer.parseInt("46001f", 16));
                        cellConfig.setPlmn(arrayList2);
                        cellConfig.setTac_cycle(20);
                        cellConfig.setTac(9880);
                        cellConfig.setPciList(arrayList1);
                        cellConfig.setPilot_frequency_list(arrayList1);
                        cellConfig.setUplink_frequency_point(19650);
                        cellConfig.setTransmitted_power(30);
                        cellConfig.setMeasure(0);
                        cellConfig.setId(id);
                        break;
                    }

                }
            }else{
                initConfig = new InitConfig();
                initConfig.setBandwidth(2);
                initConfig.setTimeDelayField(0);
                initConfig.setSynchronousMode(2);
                initConfig.setFrequencyOffset(0);
                initConfig.setOperatingBand(3);
                initConfig.setId(id);
                initConfig.setIp(ip);
                scanSet = new ScanSet();
                scanSet.setRssi((byte) 60);
                scanSet.setScan_result((byte) 0);
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(1650);
                arrayList.add(1650);
                scanSet.setEarfchList(arrayList);
                scanSet.setId(id);
                ArrayList<Integer> arrayList1 = new ArrayList<>();
                arrayList1.add(0);
                scanSet.setPciList(arrayList1);
                cellConfig = new CellConfig();
                cellConfig.setDownlink_frequency_point(1650);
                cellConfig.setCell_pci(393);
                ArrayList<Integer> arrayList2 = new ArrayList<>();
                arrayList2.add(Integer.parseInt("46001f", 16));
                cellConfig.setPlmn(arrayList2);
                cellConfig.setTac_cycle(600);
                cellConfig.setTac(9880);
                cellConfig.setPciList(arrayList1);
                cellConfig.setPilot_frequency_list(arrayList1);
                cellConfig.setUplink_frequency_point(19650);
                cellConfig.setTransmitted_power(30);
                cellConfig.setMeasure(0);
                cellConfig.setId(id);
            }
        }
    }
    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }


    public ConnectionStatus getConnectionStatus() {
        return isConnected;
    }

    public void setConnectionStatus(ConnectionStatus connected) {
        isConnected = connected;
    }


    @Override
    public StationInfo clone() throws CloneNotSupportedException {
        StationInfo bean = null;
        try {
            bean = (StationInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bean;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
