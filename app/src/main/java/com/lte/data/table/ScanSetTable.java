package com.lte.data.table;

import com.lte.data.ScanSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class ScanSetTable extends RealmObject {

    private Long id;//@Id必须为Long
    private RealmList<RealmInteger> pciList = new RealmList<>();
    private RealmList<RealmInteger> earfchList = new RealmList<>();
    private byte rssi;
    private byte scan_result = -1;

    public ScanSetTable() {
    }

    public RealmList<RealmInteger> getPciList() {
        return pciList;
    }

    public void setPciList(RealmList<RealmInteger> pciList) {
        this.pciList = pciList;
    }

    public RealmList<RealmInteger> getEarfchList() {
        return earfchList;
    }

    public void setEarfchList(RealmList<RealmInteger> earfchList) {
        this.earfchList = earfchList;
    }

    public byte getRssi() {
        return rssi;
    }

    public void setRssi(byte rssi) {
        this.rssi = rssi;
    }

    public byte getScan_result() {
        return scan_result;
    }

    public void setScan_result(byte scan_result) {
        this.scan_result = scan_result;
    }

    public ScanSet createScanSet() {
        ScanSet scanSet = new ScanSet();
        scanSet.setRssi(rssi);
        scanSet.setScan_result(scan_result);
        ArrayList<Integer> pci_list = new ArrayList<>();
        ArrayList<Integer> earfch_list = new ArrayList<>();
        for (RealmInteger realmInteger : pciList) {
            pci_list.add(realmInteger.getNumber());
        }
        for (RealmInteger realmInteger : earfchList) {
            earfch_list.add(realmInteger.getNumber());
        }
        scanSet.setPciList(pci_list);
        scanSet.setEarfchList(earfch_list);
        scanSet.setCmd();
        return scanSet;
    }

    @Override
    public String toString() {
        return "ScanSetTable{" +
                "id=" + id +
                ", pciList=" + pciList +
                ", earfchList=" + earfchList +
                ", rssi=" + rssi +
                ", scan_result=" + scan_result +
                '}';
    }
}
