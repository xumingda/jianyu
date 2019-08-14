package com.lte.data.table;

import com.lte.data.MacData;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class MacDataTable extends RealmObject {
    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private String stationName;

    private String SerialNumber;//序号

    private String Mac;

    private String imsi;

    private long time;

    private int times;

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

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }
    public MacData createMacData() {
        return new MacData.MacDataBuilder()
                .id(this.id)
                .stationName(this.stationName)
                .SerialNumber(this.SerialNumber)
                .mac(this.Mac)
                .times(this.times)
                .time(this.time)
                .build();
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}
