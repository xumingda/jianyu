package com.lte.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.lte.R;
import com.lte.data.table.RealmInteger;
import com.lte.data.table.ScanSetTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class ScanSet implements Parcelable {

    private Long id;//@Id必须为Long
    private ArrayList<Integer> pciList;
    private ArrayList<Integer> earfchList;
    private byte rssi;
    private byte scan_result = -1;

    private byte[] cmd;


    public ScanSet() {
    }
    public void setId(Long id){
        this.id =id;
    }
    public Long getId(){
        return id;
    }

    protected ScanSet(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        rssi = in.readByte();
        scan_result = in.readByte();
        cmd = in.createByteArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeByte(rssi);
        dest.writeByte(scan_result);
        dest.writeByteArray(cmd);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScanSet> CREATOR = new Creator<ScanSet>() {
        @Override
        public ScanSet createFromParcel(Parcel in) {
            return new ScanSet(in);
        }

        @Override
        public ScanSet[] newArray(int size) {
            return new ScanSet[size];
        }
    };

    public ArrayList<Integer> getPciList() {
        synchronized (this) {
            if (pciList == null) {
                pciList = new ArrayList<>();
            }
        }
        return pciList;
    }

    public void setPciList(ArrayList<Integer> pciList) {
        this.pciList = pciList;
    }

    public ArrayList<Integer> getEarfchList() {
        synchronized (this) {
            if (earfchList == null) {
                earfchList = new ArrayList<>();
            }
        }
        return earfchList;
    }

    public void setEarfchList(ArrayList<Integer> earfchList) {
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

    public ScanSetTable createScanSetTable() {
        ScanSetTable scanSetTable = new ScanSetTable();
        scanSetTable.setRssi(rssi);
        scanSetTable.setScan_result(scan_result);
        if (pciList != null && pciList.size() != 0) {
            for (Integer integer : pciList) {
                RealmInteger realmInteger = new RealmInteger();
                realmInteger.setNumber(integer);
                scanSetTable.getPciList().add(realmInteger);
            }
        }
        if (earfchList != null && earfchList.size() != 0) {
            for (Integer integer : earfchList) {
                RealmInteger realmInteger = new RealmInteger();
                realmInteger.setNumber(integer);
                scanSetTable.getEarfchList().add(realmInteger);
            }
        }
        setCmd();
        return scanSetTable;
    }

    public void setCmd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    pciList = getPciList();
                    earfchList = getEarfchList();
                    byte[] pciNumber = new byte[]{2, 0x00, 0x01, (byte) pciList.size()};
                    byte[] pciList1 = new byte[0];;
                    if (pciList.size() != 0) {
                        pciList1 = new byte[pciList.size() * 2 + 3];
                        pciList1[0] = 3;
                        pciList1[1] = 0;
                        pciList1[2] = (byte) (pciList.size() * 2);
                        for (int i = 0; i < pciList.size(); i++) {
                            pciList1[2 * i + 3] = (byte) ((pciList.get(i) >> 8) & 0xFF);
                            pciList1[2 * i + 4] = (byte) ((pciList.get(i)) & 0xFF);
                        }
                    }
                    byte[] earfchNumber = new byte[]{4, 0x00, 0x01, (byte) (earfchList.size()/2)};
                    byte[] earfchList1 = new byte[0];
                    ;
                    if (earfchList.size() != 0) {
                        earfchList1 = new byte[earfchList.size() * 2 + 3];
                        earfchList1[0] = 5;
                        earfchList1[1] = 0;
                        earfchList1[2] = (byte) (earfchList.size() * 2);
                        for (int i = 0; i < earfchList.size(); i++) {
                            earfchList1[2 * i + 3] = (byte) ((earfchList.get(i) >> 8) & 0xFF);
                            earfchList1[2 * i + 4] = (byte) ((earfchList.get(i)) & 0xFF);
                        }
                    }
                    byte[] rssi = new byte[]{13, 0x00, 0x02, (byte) ((getRssi() >> 8) & 0xFF), (byte) ((getRssi()) & 0xFF)};
                    byte[] scan_result = new byte[0];
                    if (getScan_result() != -1) {
                        scan_result = new byte[]{26, 0x00, 0x01, getScan_result()};
                    }
                    int length = 0;
                    length += pciList1.length;
                    length += earfchList1.length;
                    length += scan_result.length;
                    length += pciNumber.length;
                    length += earfchNumber.length;
                    length += rssi.length;
                    cmd = new byte[length];
                    System.arraycopy(pciNumber, 0, cmd, 0, pciNumber.length);
                    System.arraycopy(pciList1, 0, cmd, 0 + pciNumber.length, pciList1.length);
                    System.arraycopy(earfchNumber, 0, cmd, 0 + pciNumber.length + pciList1.length, earfchNumber.length);
                    System.arraycopy(earfchList1, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length, earfchList1.length);
                    System.arraycopy(rssi, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length + earfchList1.length, rssi.length);
                    System.arraycopy(scan_result, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length + earfchList1.length + rssi.length, scan_result.length);
                    setCmd(cmd);
                }
            }
        }).start();
    }

    public byte[] getCmd() {
        if (cmd == null) {
            byte[] pciNumber = new byte[]{2, 0x00, 0x01, (byte) pciList.size()};
            byte[] pciList1 = new byte[0];
            ;
            if (pciList.size() != 0) {
                pciList1 = new byte[pciList.size() * 2 + 3];
                pciList1[0] = 3;
                pciList1[1] = 0;
                pciList1[2] = (byte) (pciList.size() * 2);
                for (int i = 0; i < pciList.size(); i++) {
                    pciList1[2 * i + 3] = (byte) ((pciList.get(i) >> 8) & 0xFF);
                    pciList1[2 * i + 4] = (byte) ((pciList.get(i)) & 0xFF);
                }
            }
            byte[] earfchNumber = new byte[]{4, 0x00, 0x01, (byte) earfchList.size()};
            byte[] earfchList1 = new byte[0];
            ;
            if (earfchList.size() != 0) {
                earfchList1 = new byte[earfchList.size() * 2 + 3];
                earfchList1[0] = 5;
                earfchList1[1] = 0;
                earfchList1[2] = (byte) (earfchList.size() * 2);
                for (int i = 0; i < earfchList.size(); i++) {
                    earfchList1[2 * i + 3] = (byte) ((earfchList.get(i) >> 8) & 0xFF);
                    earfchList1[2 * i + 4] = (byte) ((earfchList.get(i)) & 0xFF);
                }
            }
            byte[] rssi = new byte[]{13, 0x00, 0x02, (byte) ((getRssi() >> 8) & 0xFF), (byte) ((getRssi()) & 0xFF)};
            byte[] scan_result = new byte[0];
            if (getScan_result() != -1) {
                scan_result = new byte[]{26, 0x00, 0x01, getScan_result()};
            }
            int length = 0;
            length += pciList1.length;
            length += earfchList1.length;
            length += scan_result.length;
            length += pciNumber.length;
            length += earfchNumber.length;
            length += rssi.length;
            cmd = new byte[length];
            System.arraycopy(pciNumber, 0, cmd, 0, pciNumber.length);
            System.arraycopy(pciList, 0, cmd, 0 + pciNumber.length, pciList1.length);
            System.arraycopy(earfchNumber, 0, cmd, 0 + pciNumber.length + pciList1.length, earfchNumber.length);
            System.arraycopy(earfchList, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length, earfchList1.length);
            System.arraycopy(rssi, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length + earfchList1.length, rssi.length);
            System.arraycopy(scan_result, 0, cmd, 0 + pciNumber.length + pciList1.length + earfchNumber.length + earfchList1.length + rssi.length, scan_result.length);
        }
        return cmd;
    }

    public void setCmd(byte[] cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "ScanSet{" +
                "id=" + id +
                ", pciList=" + pciList +
                ", earfchList=" + earfchList +
                ", rssi=" + rssi +
                ", scan_result=" + scan_result +
                ", cmd=" + Arrays.toString(cmd) +
                '}';
    }
}
