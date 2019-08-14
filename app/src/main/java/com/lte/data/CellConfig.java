package com.lte.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.lte.data.table.CellConfigTable;
import com.lte.data.table.RealmInteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chenxiaojun on 2017/9/7.
 */

public class CellConfig implements Parcelable{

    private Long ID;

    private int downlink_frequency_point;//下行频点

    private int cell_pci;//小区PCI

    private ArrayList<Integer> plmn;//plmn列表

    private int tac;//tac

    private ArrayList<Integer> pciList;//pci 列表

    private int tac_cycle = -1;//tac更新周期

    private ArrayList<Integer> pilot_frequency_list;//异频频点列表

    private int uplink_frequency_point = -1;//上行频点

    private int transmitted_power = -1;//发射功率

    private int measure =-1;//是否启用测量

    private byte[] cmd;
    private boolean cellUpReady;
    private byte[] cmd1;
    private int configmode;

    public void setId(Long id){
        this.ID =id;
    }
    public Long getId(){
        return ID;
    }

    public CellConfig(){}
    protected CellConfig(Parcel in) {
        if (in.readByte() == 0) {
            ID = null;
        } else {
            ID = in.readLong();
        }
        downlink_frequency_point = in.readInt();
        cell_pci = in.readInt();
        tac = in.readInt();
        tac_cycle = in.readInt();
        uplink_frequency_point = in.readInt();
        transmitted_power = in.readInt();
        measure = in.readInt();
    }

    public static final Creator<CellConfig> CREATOR = new Creator<CellConfig>() {
        @Override
        public CellConfig createFromParcel(Parcel in) {
            return new CellConfig(in);
        }

        @Override
        public CellConfig[] newArray(int size) {
            return new CellConfig[size];
        }
    };

    public int getDownlink_frequency_point() {
        return downlink_frequency_point;
    }

    public void setDownlink_frequency_point(int downlink_frequency_point) {
        this.downlink_frequency_point = downlink_frequency_point;
    }

    public int getCell_pci() {
        return cell_pci;
    }

    public void setCell_pci(int cell_pci) {
        this.cell_pci = cell_pci;
    }

    public ArrayList<Integer> getPlmn() {
        synchronized (this){
            if(plmn == null){
                plmn = new ArrayList<>();
            }
        }
        return plmn;
    }

    public void setPlmn(ArrayList<Integer> plmn) {
        this.plmn = plmn;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public ArrayList<Integer> getPciList() {
        synchronized (this){
            if(pciList == null){
                pciList = new ArrayList<>();
            }
        }
        return pciList;
    }

    public void setPciList(ArrayList<Integer> pciList) {
        this.pciList = pciList;
    }

    public int getTac_cycle() {
        return tac_cycle;
    }

    public void setTac_cycle(int tac_cycle) {
        this.tac_cycle = tac_cycle;
    }

    public ArrayList<Integer> getPilot_frequency_list() {
        synchronized (this){
            if(pilot_frequency_list == null){
                pilot_frequency_list = new ArrayList<>();
            }
        }
        return pilot_frequency_list;
    }

    public void setPilot_frequency_list(ArrayList<Integer> pilot_frequency_list) {
        this.pilot_frequency_list = pilot_frequency_list;
    }

    public int getUplink_frequency_point() {
        return uplink_frequency_point;
    }

    public void setUplink_frequency_point(int uplink_frequency_point) {
        this.uplink_frequency_point = uplink_frequency_point;
    }

    public int getTransmitted_power() {
        return transmitted_power;
    }

    public void setTransmitted_power(int transmitted_power) {
        this.transmitted_power = transmitted_power;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public CellConfigTable createCellConfigTable() {
        CellConfigTable cellConfigTable = new CellConfigTable();
        cellConfigTable.setDownlink_frequency_point(this.downlink_frequency_point);
        cellConfigTable.setCell_pci(this.cell_pci);
        cellConfigTable.setId(this.ID);
        cellConfigTable.setTac(tac);
        cellConfigTable.setTac_cycle(tac_cycle);
        cellConfigTable.setMeasure(measure);
        cellConfigTable.setUplink_frequency_point(uplink_frequency_point);
        cellConfigTable.setTransmitted_power(transmitted_power);
        if(this.pciList != null){
            for (Integer integer : pciList) {
                RealmInteger realmInteger = new RealmInteger();
                realmInteger.setNumber(integer);
                cellConfigTable.getPciList().add(realmInteger);
            }
        }
        if(this.plmn != null){
            for (Integer integer : plmn) {
                RealmInteger realmInteger = new RealmInteger();
                realmInteger.setNumber(integer);
                cellConfigTable.getPlmn().add(realmInteger);
            }
        }
        if(this.pilot_frequency_list != null){
            for (Integer integer : pilot_frequency_list) {
                RealmInteger realmInteger = new RealmInteger();
                realmInteger.setNumber(integer);
                cellConfigTable.getPilot_frequency_list().add(realmInteger);
            }
        }
        cellConfigTable.setConfigmode(configmode);
        setCmd();
        return cellConfigTable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (ID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(ID);
        }
        dest.writeInt(downlink_frequency_point);
        dest.writeInt(cell_pci);
        dest.writeInt(tac);
        dest.writeInt(tac_cycle);
        dest.writeInt(uplink_frequency_point);
        dest.writeInt(transmitted_power);
        dest.writeInt(measure);
    }

    @Override
    public String toString() {
        return "CellConfig{" +
                "ID=" + ID +
                ", downlink_frequency_point=" + downlink_frequency_point +
                ", cell_pci=" + cell_pci +
                ", plmn=" + plmn +
                ", tac=" + tac +
                ", pciList=" + pciList +
                ", tac_cycle=" + tac_cycle +
                ", pilot_frequency_list=" + pilot_frequency_list +
                ", uplink_frequency_point=" + uplink_frequency_point +
                ", transmitted_power=" + transmitted_power +
                ", measure=" + measure +
                ",cmd =" + Arrays.toString(cmd)+
                '}';
    }

    public byte[] getCmd() {
        return cmd;
    }

    public void setCmd(byte[] cmd) {
        this.cmd = cmd;
    }
    public void setCmd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    pciList = getPciList();
                    plmn = getPlmn();
                    pilot_frequency_list = getPilot_frequency_list();
                    byte[] downLink_frequency_pointTag = new byte[]{0x08,0x00,0x02, (byte) ((downlink_frequency_point >> 8) & 0xFF),(byte) (downlink_frequency_point & 0xFF)};
                    byte[] cellPciTag = new byte[]{0x09,0x00,0x02, (byte) ((cell_pci >> 8) & 0xFF),(byte) (cell_pci & 0xFF)};
                    byte[] tacTag = new byte[]{14,0x00,0x02,(byte) ((tac >> 8) & 0xFF),(byte) (tac & 0xFF)};
                    byte[] pLmnNumberTag = new byte[]{34,0x00,0x04,(byte) ((plmn.size() >> 24) & 0xFF),(byte) (plmn.size()>>16 & 0xFF),(byte) ((plmn.size() >> 8) & 0xFF),(byte) (plmn.size() & 0xFF)};
                    byte[] pLmnListTag = new byte[0];
                    if(plmn != null && plmn.size() != 0){
                        pLmnListTag = new byte[7*plmn.size()];
                        for(int i = 0; i< plmn.size();i++){
                            pLmnListTag[i*7] = 23;
                            pLmnListTag[i*7+1] = 0x00;
                            pLmnListTag[i*7+2] = 0x04;
                            pLmnListTag[i*7+3] = (byte) ((plmn.get(i) >> 24) & 0xFF);
                            pLmnListTag[i*7+4] = (byte) ((plmn.get(i) >> 16) & 0xFF);
                            pLmnListTag[i*7+5] = (byte) ((plmn.get(i) >> 8) & 0xFF);
                            pLmnListTag[i*7+6] = (byte) ((plmn.get(i) >> 0) & 0xFF);
                        }
                    }
                    byte[] pciNumberTag = new byte[0];
                    byte[] pciListTag = new byte[0];
                    if(pciList == null){
                        pciNumberTag = new byte[]{2,0x00,0x01, 0};
                    }else {
                        pciNumberTag = new byte[]{2,0x00,0x01, (byte) pciList.size()};
                        if(pciList.size() != 0){
                            pciListTag = new byte[pciList.size()*2+3];
                            pciListTag[0] = 0x03;
                            pciListTag[1] = 0x00;
                            pciListTag[2] = (byte) (pciList.size()*2);
                            for(int i = 0;i<pciList.size();i++){
                                pciListTag[2 * i + 3] = (byte) ((pciList.get(i) >> 8) & 0xFF);
                                pciListTag[2 * i + 4] = (byte) ((pciList.get(i)) & 0xFF);
                            }
                        }
                    }



                    byte[] pilot_frequency_numberTag = new byte[]{0x07,0x00,0x01, (byte) pilot_frequency_list.size()};
                    byte[] pilot_frequency_listTag = new byte[0];
                    if(pilot_frequency_list.size() != 0){
                        pilot_frequency_listTag = new byte[pilot_frequency_list.size()*2+3];
                        pilot_frequency_listTag[0] = 24;
                        pilot_frequency_listTag[1] = 0x00;
                        pilot_frequency_listTag[2] = (byte) (pilot_frequency_list.size()*2);
                        for(int i = 0;i<pilot_frequency_list.size();i++){
                            pilot_frequency_listTag[2 * i + 3] = (byte) ((pilot_frequency_list.get(i) >> 8) & 0xFF);
                            pilot_frequency_listTag[2 * i + 4] = (byte) ((pilot_frequency_list.get(i)) & 0xFF);
                        }
                    }
                    byte[] uplink_frequency_pointTag = new byte[0];
                    if(uplink_frequency_point != -1){
                        uplink_frequency_pointTag = new byte[]{31,0x00,0x02, (byte) ((uplink_frequency_point >> 8) & 0xFF),(byte) (uplink_frequency_point & 0xFF)};
                    }
                    byte[] measureTag = new byte[0];
                    if(measure != -1){
                        measureTag = new byte[]{33,0x00,0x01, (byte) measure };
                    }
                    byte[] transmitted_powerTag = new byte[0];
                    if(transmitted_power != -1){
                        transmitted_powerTag = new byte[]{32,0x00,0x02, (byte) (( transmitted_power>> 8) & 0xFF),(byte) (transmitted_power & 0xFF)};
                    }
                    byte[] tac_cycleTag = new byte[0];
                    if(tac_cycle != -1){
                        tac_cycleTag = new byte[]{35,0x00,0x04,(byte) ((tac_cycle >> 24) & 0xFF),(byte) (tac_cycle>>16 & 0xFF),(byte) ((tac_cycle >> 8) & 0xFF),(byte) (tac_cycle & 0xFF)};
                    }

                    int length = 0;
                    length += downLink_frequency_pointTag.length;
                    length += cellPciTag.length;
                    length += tacTag.length;
                    length += pLmnNumberTag.length;
                    length += pLmnListTag.length;
                    length += pciNumberTag.length;
                    length += pciListTag.length;
                    length += pilot_frequency_numberTag.length;
                    length += pilot_frequency_listTag.length;
                    length += uplink_frequency_pointTag.length;
                    length += measureTag.length;
                    length += transmitted_powerTag.length;
                    length += tac_cycleTag.length;
                    cmd = new byte[length];
                    System.arraycopy(downLink_frequency_pointTag, 0, cmd, 0, downLink_frequency_pointTag.length);
                    System.arraycopy(cellPciTag, 0, cmd, downLink_frequency_pointTag.length, cellPciTag.length);
                    System.arraycopy(tacTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length, tacTag.length);
                    System.arraycopy(pLmnNumberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length, pLmnNumberTag.length);
                    System.arraycopy(pLmnListTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length, pLmnListTag.length);
                    System.arraycopy(pciNumberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length, pciNumberTag.length);
                    System.arraycopy(pciListTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length, pciListTag.length);
                    System.arraycopy(pilot_frequency_numberTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length + pciListTag.length, pilot_frequency_numberTag.length);
                    System.arraycopy(pilot_frequency_listTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length, pilot_frequency_listTag.length);
                    System.arraycopy(uplink_frequency_pointTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length, uplink_frequency_pointTag.length);
                    System.arraycopy(measureTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length, measureTag.length);
                    System.arraycopy(transmitted_powerTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length+measureTag.length, transmitted_powerTag.length);
                    System.arraycopy(tac_cycleTag, 0, cmd, downLink_frequency_pointTag.length + cellPciTag.length + tacTag.length + pLmnNumberTag.length + pLmnListTag.length +
                            pciNumberTag.length + pciListTag.length+pilot_frequency_numberTag.length+pilot_frequency_listTag.length+uplink_frequency_pointTag.length+measureTag.length, tac_cycleTag.length);
                    setCmd(cmd);
                }
            }
        }).start();
    }
    public byte[] getPlmnCmd(){
        byte[] pLmnNumberTag = new byte[]{34,0x00,0x04,(byte) ((plmn.size() >> 24) & 0xFF),(byte) (plmn.size()>>16 & 0xFF),(byte) ((plmn.size() >> 8) & 0xFF),(byte) (plmn.size() & 0xFF)};
        byte[] pLmnListTag = new byte[0];
        if(plmn != null && plmn.size() != 0){
            pLmnListTag = new byte[7*plmn.size()];
            for(int i = 0; i< plmn.size();i++){
                pLmnListTag[i*7] = 23;
                pLmnListTag[i*7+1] = 0x00;
                pLmnListTag[i*7+2] = 0x04;
                pLmnListTag[i*7+3] = (byte) ((plmn.get(i) >> 24) & 0xFF);
                pLmnListTag[i*7+4] = (byte) ((plmn.get(i) >> 16) & 0xFF);
                pLmnListTag[i*7+5] = (byte) ((plmn.get(i) >> 8) & 0xFF);
                pLmnListTag[i*7+6] = (byte) ((plmn.get(i) >> 0) & 0xFF);
            }
        }
        byte[] measureTag = new byte[0];
        if(measure != -1){
            measureTag = new byte[]{33,0x00,0x01, (byte) measure };
        }
        byte[] downLink_frequency_pointTag = new byte[]{0x08,0x00,0x02, (byte) ((downlink_frequency_point >> 8) & 0xFF),(byte) (downlink_frequency_point & 0xFF)};
        byte[] uplink_frequency_pointTag = new byte[0];
        if(uplink_frequency_point != -1){
            uplink_frequency_pointTag = new byte[]{31,0x00,0x02, (byte) ((uplink_frequency_point >> 8) & 0xFF),(byte) (uplink_frequency_point & 0xFF)};
        }
        byte[] transmitted_powerTag = new byte[0];
        if(transmitted_power != -1){
            transmitted_powerTag = new byte[]{32,0x00,0x02, (byte) (( transmitted_power>> 8) & 0xFF),(byte) (transmitted_power & 0xFF)};
        }
        int length = 0;
        length += pLmnNumberTag.length;
        length += pLmnListTag.length;
        length += measureTag.length;
        length += downLink_frequency_pointTag.length;
        length += transmitted_powerTag.length;
        length += uplink_frequency_pointTag.length;
        cmd1 = new byte[length];
        System.arraycopy(pLmnNumberTag, 0, cmd1, 0, pLmnNumberTag.length);
        System.arraycopy(pLmnListTag, 0, cmd1, pLmnNumberTag.length, pLmnListTag.length);
        System.arraycopy(measureTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length, measureTag.length);
        System.arraycopy(downLink_frequency_pointTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length+measureTag.length, downLink_frequency_pointTag.length);
        System.arraycopy(transmitted_powerTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length+measureTag.length+downLink_frequency_pointTag.length, transmitted_powerTag.length);
        System.arraycopy(uplink_frequency_pointTag, 0, cmd1, pLmnNumberTag.length+pLmnListTag.length+measureTag.length+downLink_frequency_pointTag.length+transmitted_powerTag.length, uplink_frequency_pointTag.length);
        return cmd1;
    }
    public void setCellUpReady(boolean cellUpReady){
        this.cellUpReady = cellUpReady;
    }
    public byte[] getCellUpGradeCmd(int seqNo){
        byte[] plmn = getPlmnCmd();
        byte[] data = new byte[]{0x01, 17, 0x00, (byte) (12+plmn.length),14,0x00,0x02,(byte) ((tac >> 8) & 0xFF),(byte) (tac & 0xFF), 0x01, 0x00, 0x04, (byte) ((seqNo >> 24) & 0xFF), (byte) ((seqNo >> 16) & 0xFF), (byte) ((seqNo >> 8) & 0xFF), (byte) (seqNo & 0xFF)};
        byte[] sendData = new byte[plmn.length +data.length];
        System.arraycopy(data, 0, sendData, 0, data.length);
        System.arraycopy(plmn, 0, sendData, 0 + data.length, plmn.length);
        return sendData;
    }
    public int getAddTac() {
        return ++tac;
    }

    public boolean isCellUpReady() {
        return cellUpReady;
    }

    public int getConfigmode() {
        return configmode;
    }

    public void setConfigmode(int configmode) {
        this.configmode = configmode;
    }
}
