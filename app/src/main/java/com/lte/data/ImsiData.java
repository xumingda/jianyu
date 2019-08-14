package com.lte.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.lte.R;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class ImsiData implements Cloneable,Parcelable {

    private Long id = null;//@Id必须为Long

    private String stationName;

    private String operator;//运营商

    private String attribuation;

    private String imsi;

    private String imei;

    private int isblackList;//0都不是 1:黑名单 2：白名单

    private String mobile;//手机号

    private long time;

    private int times;

    private String deviceId;

    private String bbu;
    private boolean report;
    public boolean isWrite;
    private String esn;
    private String tmsi;


    public ImsiData(){}

    public ImsiData(ImsiDataBuilder imsiDataBuilder) {
        super();
        this.id = imsiDataBuilder.id;
        this.stationName = imsiDataBuilder.stationName;
        this.operator = imsiDataBuilder.operator;
        this.imsi = imsiDataBuilder.imsi;
        this.bbu = imsiDataBuilder.bbu;
        this.time = imsiDataBuilder.time;
        this.times = imsiDataBuilder.times;
    }

    protected ImsiData(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        stationName = in.readString();
        operator = in.readString();
        imsi = in.readString();
        times = in.readInt();
    }

    public static final Creator<ImsiData> CREATOR = new Creator<ImsiData>() {
        @Override
        public ImsiData createFromParcel(Parcel in) {
            return new ImsiData(in);
        }

        @Override
        public ImsiData[] newArray(int size) {
            return new ImsiData[size];
        }
    };

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
        if(imsi.startsWith("46001") || imsi.startsWith("46009")||
                imsi.startsWith("46006")){
            operator = "联通";
        } else if (imsi.startsWith("46011")||imsi.startsWith("46003")||
                imsi.startsWith("46005")
        ){
            operator = "电信";
        } else {
            operator = "移动";
        }
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
        dest.writeString(stationName);
        dest.writeString(operator);
        dest.writeString(imsi);
    }

    public String getAttribuation() {
        return attribuation;
    }

    public void setAttribuation(String attribuation) {
        this.attribuation = attribuation;
    }

    public String getBbu() {
        return bbu;
    }

    public void setBbu(String bbu) {
        this.bbu = bbu;
    }

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
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

    public int getIsblackList() {
        return isblackList;
    }

    public void setIsblackList(int isblackList) {
        this.isblackList = isblackList;
    }

    public void setEsn(String esn) {
        this.esn = esn;
    }

    public void setTmsi(String tmsi) {
        this.tmsi = tmsi;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "ImsiData{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", operator='" + operator + '\'' +
                ", attribuation='" + attribuation + '\'' +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", isblackList=" + isblackList +
                ", mobile='" + mobile + '\'' +
                ", time=" + time +
                ", times=" + times +
                ", deviceId='" + deviceId + '\'' +
                ", bbu='" + bbu + '\'' +
                ", report=" + report +
                ", isWrite=" + isWrite +
                ", esn='" + esn + '\'' +
                ", tmsi='" + tmsi + '\'' +
                '}';
    }


    public static class ImsiDataBuilder {
        private Long id = null;//@Id必须为Long

        private String stationName;

        private String operator;//运营商

        private String imsi;

        private int field_strength;

        private long time;

        private int times;

        private String attribuation;

        private String bbu;

        public ImsiDataBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ImsiDataBuilder stationName(String stationName) {
            this.stationName = stationName;
            return this;
        }
        public ImsiDataBuilder operator(String operator) {
            this.operator = operator;
            return this;
        }
        public ImsiDataBuilder imsi(String imsi) {
            this.imsi = imsi;
            return this;
        }
        public ImsiDataBuilder field_strength(int field_strength) {
            this.field_strength = field_strength;
            return this;
        }
        public ImsiDataBuilder time(long time) {
            this.time = time;
            return this;
        }

        public ImsiDataBuilder times(int times){
            this.times = times;
            return this;
        }

        public ImsiDataBuilder attribuation(String attribuation){
            this.attribuation = attribuation;
            return this;
        }

        public ImsiDataBuilder bbu(String bbu){
            this.bbu = bbu;
            return this;
        }
        public ImsiData build() {
            return new ImsiData(this);
        }
    }
    @Override
    public ImsiData clone() throws CloneNotSupportedException {
        ImsiData imsiData = null;
        try {
            imsiData = (ImsiData) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return imsiData;
    }
}
