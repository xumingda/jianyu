package com.lte.data;

import com.lte.data.table.ScanResultTable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/8/14.
 */

public class ScanResult {

    private Long id;//@Id必须为Long

    private int frequency;//频点

    private int pci;//PCI

    private int TAC;//TAC

    private int RSSI;//RSSI

    private int priority;//优先级

    private long time;

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

    public ScanResultTable bulider() {
        ScanResultTable scanResultTable = new ScanResultTable();
        scanResultTable.setId(this.id);
        scanResultTable.setFrequency(this.frequency);
        scanResultTable.setPci(this.pci);
        scanResultTable.setTAC(this.TAC);
        scanResultTable.setRSSI(this.RSSI);
        scanResultTable.setTime(this.time);
        scanResultTable.setPriority(this.priority);
        return scanResultTable;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", pci=" + pci +
                ", TAC=" + TAC +
                ", RSSI=" + RSSI +
                ", priority=" + priority +
                ", time=" + time +
                '}';
    }
}
