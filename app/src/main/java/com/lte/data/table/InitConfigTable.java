package com.lte.data.table;

import android.os.Parcel;
import android.os.Parcelable;


import com.lte.data.InitConfig;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by chenxiaojun on 2017/9/6.
 */
public class InitConfigTable extends RealmObject {

    @PrimaryKey
    private Long id;//@Id必须为Long

    @Required
    private String ip;

    private int bandwidth;//带宽

    private int timeDelayField;//时延域

    private int synchronousMode;//同步模式

    private int frequencyOffset;//是否保存频偏

    private int operatingBand;//工作频带


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


    public InitConfig createConfig() {
        InitConfig initConfig = new InitConfig();
        initConfig.setId(id);
        initConfig.setIp(ip);
        initConfig.setBandwidth(bandwidth);
        initConfig.setSynchronousMode(synchronousMode);
        initConfig.setFrequencyOffset(frequencyOffset);
        initConfig.setOperatingBand(operatingBand);
        initConfig.setTimeDelayField(timeDelayField);
        return initConfig;
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
}
