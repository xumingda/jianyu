package com.lte.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.lte.data.table.InitConfigTable;


/**
 * Created by chenxiaojun on 2017/9/6.
 */
public class InitConfig implements Parcelable{

    private Long id = null;//@Id必须为Long

    private String ip;

    private int bandwidth;//带宽

    private int timeDelayField;//时延域

    private int synchronousMode;//同步模式

    private int frequencyOffset;//是否保存频偏

    private int operatingBand;//工作频带



    public InitConfig(Long id, String ip, int bandwidth, int timeDelayField,
            int synchronousMode, int frequencyOffset, int operatingBand) {
        this.id = id;
        this.ip = ip;
        this.bandwidth = bandwidth;
        this.timeDelayField = timeDelayField;
        this.synchronousMode = synchronousMode;
        this.frequencyOffset = frequencyOffset;
        this.operatingBand = operatingBand;
    }


    public InitConfig() {
    }


    protected InitConfig(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        ip = in.readString();
        bandwidth = in.readInt();
        timeDelayField = in.readInt();
        synchronousMode = in.readInt();
        frequencyOffset = in.readInt();
        operatingBand = in.readInt();
    }

    public static final Creator<InitConfig> CREATOR = new Creator<InitConfig>() {
        @Override
        public InitConfig createFromParcel(Parcel in) {
            return new InitConfig(in);
        }

        @Override
        public InitConfig[] newArray(int size) {
            return new InitConfig[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getTimeDelayField() {
        return timeDelayField;
    }

    public void setTimeDelayField(int timeDelayField) {
        this.timeDelayField = timeDelayField;
    }

    public int getSynchronousMode() {
        return synchronousMode;
    }

    public void setSynchronousMode(int synchronousMode) {
        this.synchronousMode = synchronousMode;
    }

    public int getFrequencyOffset() {
        return frequencyOffset;
    }

    public void setFrequencyOffset(int frequencyOffset) {
        this.frequencyOffset = frequencyOffset;
    }

    public int getOperatingBand() {
        return operatingBand;
    }

    public void setOperatingBand(int operatingBand) {
        this.operatingBand = operatingBand;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
        dest.writeString(ip);
        dest.writeInt(bandwidth);
        dest.writeInt(timeDelayField);
        dest.writeInt(synchronousMode);
        dest.writeInt(frequencyOffset);
        dest.writeInt(operatingBand);
    }

    @Override
    public String toString() {
        return "InitConfigTable{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", bandwidth=" + bandwidth +
                ", timeDelayField=" + timeDelayField +
                ", synchronousMode=" + synchronousMode +
                ", frequencyOffset=" + frequencyOffset +
                ", operatingBand=" + operatingBand +
                '}';
    }
    public InitConfigTable createInitConfigTable() {
        InitConfigTable initConfigTable = new InitConfigTable();
        initConfigTable.setId(id);
        initConfigTable.setIp(ip);
        initConfigTable.setBandwidth(bandwidth);
        initConfigTable.setFrequencyOffset(frequencyOffset);
        initConfigTable.setSynchronousMode(synchronousMode);
        initConfigTable.setOperatingBand(operatingBand);
        initConfigTable.setTimeDelayField(timeDelayField);
        return initConfigTable;
    }
}
