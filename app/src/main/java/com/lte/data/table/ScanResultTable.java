package com.lte.data.table;

import android.text.TextUtils;

import com.lte.data.ScanResult;
import com.lte.data.UserInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by chenxiaojun on 2017/8/14.
 */

public class ScanResultTable extends RealmObject {
    @PrimaryKey
    private Long id;//@Id必须为Long

    private int frequency;//频点

    private int pci;//PCI

    private int TAC;//TAC

    private int RSSI;//RSSI
    private long time;

    private int priority;//优先级

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getPci() {
        return pci;
    }

    public void setPci(int pci) {
        this.pci = pci;
    }

    public int getTAC() {
        return TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ScanResult build() {
        ScanResult scanResult = new ScanResult();
        scanResult.setId(this.id);
        scanResult.setFrequency(this.frequency);
        scanResult.setPci(this.pci);
        scanResult.setTAC(this.TAC);
        scanResult.setRSSI(this.RSSI);
        scanResult.setTime(this.time);
        return scanResult;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
