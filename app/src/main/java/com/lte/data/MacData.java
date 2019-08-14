package com.lte.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class MacData implements Cloneable{
    private Long id = null;//@Id必须为Long

    private String stationName;

    private String SerialNumber;//序号

    private String Mac;

    private String imsi;

    private long time;

    private int times;
    private boolean report;

    public MacData(){}

    public MacData(MacDataBuilder macDataBuilder) {
        super();
        this.id = macDataBuilder.id;
        this.stationName = macDataBuilder.stationName;
        this.SerialNumber = macDataBuilder.SerialNumber;
        this.Mac = macDataBuilder.Mac;
        this.time = macDataBuilder.time;
        this.times = macDataBuilder.times;
    }

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

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public static class MacDataBuilder {
        private Long id = null;//@Id必须为Long

        private String stationName;

        private String SerialNumber;//序号

        private String Mac;

        private long time;

        private int times;
        public MacDataBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MacDataBuilder stationName(String stationName) {
            this.stationName = stationName;
            return this;
        }
        public MacDataBuilder SerialNumber(String SerialNumber) {
            this.SerialNumber = SerialNumber;
            return this;
        }
        public MacDataBuilder mac(String Mac) {
            this.Mac = Mac;
            return this;
        }
        public MacDataBuilder time(long time) {
            this.time = time;
            return this;
        }

        public MacDataBuilder times(int times){
            this.times = times;
            return this;
        }

        public MacData build() {
            return new MacData(this);
        }
    }
    @Override
    public MacData clone() throws CloneNotSupportedException {
        MacData macData = null;
        try {
            macData = (MacData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return macData;
    }
}
