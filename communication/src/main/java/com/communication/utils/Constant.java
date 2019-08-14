package com.communication.utils;

public class Constant {
    /**
     * 日志开关
     */
    public static final boolean IS_DEBUG = true;
    public static final int OFFLINE_COUNT = 3;
    public static final String DEFAULT_HEART_MSG_BODY = "KeepConnect";
    public static final int SEDN_PORT = 5558;
    public static final int RECEIVE_PORT = 5557;
    public static final int CMDA_SEDN_PORT = 6668;
    public static final int CMDA_RECEIVE_PORT = 6667;

    /**
     * 本地通讯端口
     */
    public static int LOCAL_COMMUNICATION_PORT = 6001;
    /**
     * 远程通讯端口
     */
    public static int REMOTE_COMMUNICATION_PORT = 6001;

    public static final String HTTP_ENCODE = "UTF_8";

    public static final String HTTP_CONTENTTYPE = "application/json; charset=utf-8";

    public static final String HTTP_USERAGENT = "WIFI-JX";

    /**
     * 服务器地址
     */
    public static String SERVER_URL = "http://query.jingxuncloud.com:6001";

    public static int SCREEN_WIDTH;

    public static int SCREEN_HEIGHT;

    public static int POWTAG;

    public static final String SharePerference="DisPlayTag";

    public static String standard;

    public static String synchro="";

    public static String channel;

    public static String pci;

    public static String display;

    public static boolean isSpeak;

    public static String mcu_version;

    public static String fpga_version;

    public static int curr_value = 0;

    public static int curr_battery = 0;

}
