package com.lte.data.table;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/12/6.
 */

public class WhiteListTable extends RealmObject {

    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private String stationName;

    private String operator;//运营商

    private String imsi;

    private String imei;

    private String mobile;//手机号

    private String source;//归属地

    private String phoneUsername; //姓名

    private String position; //位职别

    private String bbu;

    private long time;

    private int times;

    private int isBlackAndWhite;
    @Ignore
    private int cts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getPhoneUsername(){return phoneUsername;}

    public void setPhoneUsername(String phoneUsername){this.phoneUsername=phoneUsername;}

    public String getPosition(){return position;}
    public void setPosition(String  position){this.position=position;}

    public String getBbu() {
        return bbu;
    }

    public void setBbu(String bbu) {
        this.bbu = bbu;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getIsBlackAndWhite() {
        return isBlackAndWhite;
    }

    public void setIsBlackAndWhite(int isBlackAndWhite) {
        this.isBlackAndWhite = isBlackAndWhite;
    }

    public int getCts() {
        return cts;
    }

    public void setCts(int cts) {
        this.cts = cts;
    }


}
