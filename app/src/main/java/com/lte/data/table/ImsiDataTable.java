package com.lte.data.table;

import android.text.TextUtils;

import com.lte.data.ImsiData;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class ImsiDataTable extends RealmObject {

    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private String stationName;

    private String operator;//运营商

    private String imsi;

    private String imei;

    private String mobile;//手机号

    private String source;//归属地

    private String bbu;

    private long time;

    private int times;

    private int isBlackAndWhite;

    private String deviceId;
    @Ignore
    public int cts;

    private int number;

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

    public ImsiData createImsiData() {
        return new ImsiData.ImsiDataBuilder().
                id(this.id)
                .stationName(this.stationName)
                .operator(this.operator)
                .imsi(this.imsi)
                .bbu(this.bbu)
                .times(this.times)
                .time(this.time)
                .build();
    }


    public String getBbu() {
        return bbu;
    }

    public void setBbu(String bbu) {
        this.bbu = bbu;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getIsBlackAndWhite() {
        return isBlackAndWhite;
    }

    public void setIsBlackAndWhite(int isBlackAndWhite) {
        this.isBlackAndWhite = isBlackAndWhite;
    }



    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImsiDataTable that = (ImsiDataTable) o;
        return Objects.equals(imsi, that.imsi) ;
    }

    @Override
    public int hashCode() {

        return Objects.hash(imsi);
    }
}
