package com.lte.utils;


import com.App;
import com.lte.data.UserInfo;

/**
 * Created by chenxiaojun on 2017/8/14.
 */

public class Constants {

    public final static String SUPER_ACCOUNT_NUM = "admin";

    public final static String SUPER_ACCOUNT_KEY = "123456";

    public final static UserInfo DEFAULT_SUPER_ACCOUNT = new UserInfo(1l,SUPER_ACCOUNT_NUM,SUPER_ACCOUNT_KEY);

    public static final int TCP_PORT = 32790;
    public static final String NAME = "name";
    public static final String DATA = "data";
    public static final String STATE = "state";
    public static final String SEND_DATA = "send_data";
    public static final String RECEIVE_DATA = "receive_data";
    public static final int UPGRADE = 1;
    public static final String STATION = "station";
    public static final String BUNDLE = "bundle";
    public static final String INIT_CONFIG = "init_config";
    public static final String PCI = "pci";
    public static final String EARFCH = "earfch";
    public static final String PLMN = "plmn";
    public static final String PILOT = "pilot";
    public static final String SCANSET_PCI = "scanset_pci";
    public static final String OPERATOR = "operator";
    public static final String IMSI = "imsi";
    public static final String ATTRIBUATION = "attribuation";
    public static final String TIME = "time";
    public static final String BBU = "bbu";
    public static final String SERIALNUMBER = "serial_number";
    public static final String MAC = "mac";
    public static final String TYPE = "type";

    public static String VOICEON="voiceon";
    public static String TIMEFORMAT="timeformat";
    public static String SHOWDELAY="showdelay";

    public static String httpDeviceIp = AppUtils.getHttpUrl(App.get().userInfo.getUrl(),App.get().userInfo.getImsiPort(),"datas/device");

//    public static final String httpDeviceIp = "http://223.85.223.105:8090/datas/device";


    public static final String CONFIG_STATE = "config_state";
    public static final String CPU_TEM = "CPU_TEM";
    public static final String CPU_USE = "CPU_USE";
    public static final String ROM_UES = "ROM_USE";
    public static final String SOFT_STATE = "SOFT_STATE";
    public static final String TEM = "tem";
    public static final String ID = "ID";
    public static final String FREQ = "freq";
    public static final String TAC = "tac";
    public static final String RSSI = "rssi";


    public static String getDataUrl(String deviceNumber){
        return AppUtils.getHttpUrl(App.get().userInfo.getUrl(),App.get().userInfo.getImsiPort(),"datas/userreport?devNumber="+deviceNumber);

    }
    public static String getRegister(String deviceNumber){
        return AppUtils.getHttpUrl(App.get().userInfo.getUrl(),App.get().userInfo.getImsiPort(),"datas/status?devNumber="+deviceNumber);

    }
//    public static String getDataUrl(String deviceNumber){
//        return "http://223.85.223.105:8090/datas/userreport?devNumber="+deviceNumber;
//
//    }
//    public static String getRegister(String deviceNumber){
//        return "http://223.85.223.105:8090/datas/status?devNumber="+deviceNumber;
//
//    }

    public  static UserInfo SUPER_ACCOUNT = DEFAULT_SUPER_ACCOUNT;

    public static String CURRENT_ACCOUNT;

    public static String crcId = "0098";

    public static String getJurisdiction(String mac,String location) {
        return "http://117.50.55.253:8081/datas/auth?mac="+mac+"&"+"location="+location;
    }
}
