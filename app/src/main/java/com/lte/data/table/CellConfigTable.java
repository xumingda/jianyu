package com.lte.data.table;

import com.lte.data.CellConfig;
import com.lte.data.ScanSet;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/7.
 */

public class CellConfigTable extends RealmObject {
    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private int downlink_frequency_point;//下行频点

    private int cell_pci;//小区PCI

    private RealmList<RealmInteger> plmn;//plmn列表

    private int tac;//tac

    private RealmList<RealmInteger> cell_pciList;//pci 列表

    private int tac_cycle = -1;//tac更新周期

    private RealmList<RealmInteger> pilot_frequency_list;//异频频点列表

    private int uplink_frequency_point = -1;//上行频点

    private int transmitted_power = -1;//发射功率

    private int measure = -1;//是否启用测量
    private int configmode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public RealmList<RealmInteger> getPlmn() {
        synchronized (this) {
            if (plmn == null) {
                plmn = new RealmList<>();
            }
        }
        return plmn;
    }

    public void setPlmn(RealmList<RealmInteger> plmn) {
        this.plmn = plmn;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public RealmList<RealmInteger> getPciList() {
        synchronized (this) {
            if (cell_pciList == null) {
                cell_pciList = new RealmList<>();
            }
        }
        return cell_pciList;
    }

    public void setPciList(RealmList<RealmInteger> pciList) {
        this.cell_pciList = pciList;
    }

    public int getTac_cycle() {
        return tac_cycle;
    }

    public void setTac_cycle(int tac_cycle) {
        this.tac_cycle = tac_cycle;
    }

    public RealmList<RealmInteger> getPilot_frequency_list() {
        synchronized (this) {
            if (pilot_frequency_list == null) {
                pilot_frequency_list = new RealmList<>();
            }
        }
        return pilot_frequency_list;
    }

    public void setPilot_frequency_list(RealmList<RealmInteger> pilot_frequency_list) {
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

    public CellConfig createCellConfig() {
        CellConfig cellConfig = new CellConfig();
        cellConfig.setId(this.id);
        cellConfig.setDownlink_frequency_point(downlink_frequency_point);
        cellConfig.setCell_pci(cell_pci);
        cellConfig.setMeasure(measure);
        cellConfig.setTac(tac);
        cellConfig.setTac_cycle(tac_cycle);
        cellConfig.setTransmitted_power(transmitted_power);
        cellConfig.setUplink_frequency_point(uplink_frequency_point);
        ArrayList<Integer> mPlmn = new ArrayList<>();
        ArrayList<Integer> mPciList = new ArrayList<>();
        ArrayList<Integer> mPilot_frequency_list = new ArrayList<>();
        if (cell_pciList != null && cell_pciList.size() != 0) {
            for (RealmInteger realmInteger : cell_pciList) {
                mPciList.add(realmInteger.getNumber());
            }
        }
        if (plmn != null && plmn.size() != 0) {
            for (RealmInteger realmInteger : plmn) {
                mPlmn.add(realmInteger.getNumber());
            }
        }
        if (pilot_frequency_list != null && pilot_frequency_list.size() != 0) {
            for (RealmInteger realmInteger : pilot_frequency_list) {
                mPilot_frequency_list.add(realmInteger.getNumber());
            }
        }
        cellConfig.setPciList(mPciList);
        cellConfig.setPlmn(mPlmn);
        cellConfig.setPilot_frequency_list(mPilot_frequency_list);
        cellConfig.setConfigmode(configmode);
        cellConfig.setCmd();
        return cellConfig;
    }

    @Override
    public String toString() {
        return "CellConfigTable{" +
                "id=" + id +
                ", downlink_frequency_point=" + downlink_frequency_point +
                ", cell_pci=" + cell_pci +
                ", plmn=" + plmn +
                ", tac=" + tac +
                ", cell_pciList=" + cell_pciList +
                ", tac_cycle=" + tac_cycle +
                ", pilot_frequency_list=" + pilot_frequency_list +
                ", uplink_frequency_point=" + uplink_frequency_point +
                ", transmitted_power=" + transmitted_power +
                ", measure=" + measure +
                '}';
    }

    public void setConfigmode(int configmode) {
        this.configmode = configmode;
    }

    public int getConfigmode() {
        return configmode;
    }
}
