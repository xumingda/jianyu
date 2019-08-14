package com.lte.data.table;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.lte.data.CellConfig;
import com.lte.data.StationInfo;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class StationInfoTable extends RealmObject {

    @PrimaryKey
    private Long id;

    @Required
    private String name;

    @Required
    private String Ip;

    private byte dbm;

    private String bbu;

    private RealmList<ScanResultTable> resultTables;

    private ScanSetTable scanSetTable;

    private InitConfigTable initConfigTable;

    private CellConfigTable cellConfigTable;

    private int type;

    private int TDDtype;//0：TDD 1：FDD

    private int rxlevmin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public InitConfigTable getInitConfigTable() {
        return initConfigTable;
    }

    public void setInitConfigTable(InitConfigTable initConfigTable) {
        this.initConfigTable = initConfigTable;
    }

    public CellConfigTable getCellConfigTable() {
        return cellConfigTable;
    }

    public void setCellConfigTable(CellConfigTable cellConfigTable) {
        this.cellConfigTable = cellConfigTable;
    }


    public void setRxlevmin(int value){this.rxlevmin=value;}
    public int getRxlevmin(){return rxlevmin;}

    public StationInfo createStationInfo(){
        StationInfo stationInfo = new StationInfo();
        stationInfo.setId(id);
        stationInfo.setName(name);
        stationInfo.setIp(Ip,false);
        stationInfo.setDbm(dbm);
        stationInfo.setBbu(bbu);
        stationInfo.setScanResultId(Long.valueOf(this.resultTables.size()));
        stationInfo.setType(type);
        stationInfo.setTDDtype(TDDtype);

        stationInfo.setRxlevmin(rxlevmin);

        Log.d("stationInfo",this.toString());
        if(initConfigTable != null){
            stationInfo.setInitConfig(initConfigTable.createConfig());
        }
        if(scanSetTable != null){
            stationInfo.setScanSet(scanSetTable.createScanSet());
        }
        if(cellConfigTable != null){
            stationInfo.setCellConfig(cellConfigTable.createCellConfig());
        }
        if(getResultTables().size() != 0){
            for (ScanResultTable resultTable : resultTables) {
                stationInfo.getmList().add(resultTable.build());
            }
        }

        return stationInfo;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId(){
        return id;
    }

    public byte getDbm() {
        return dbm;
    }

    public void setDbm(byte dbm) {
        this.dbm = dbm;
    }

    public void setScanSetTable(ScanSetTable scanSetTable) {
        this.scanSetTable = scanSetTable;
    }

    @Override
    public String toString() {
        return "StationInfoTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", Ip='" + Ip + '\'' +
                ", dbm=" + dbm +
                ", scanSetTable=" + scanSetTable +
                ", initConfigTable=" + initConfigTable +
                ", cellConfigTable=" + cellConfigTable +
                '}';
    }

    public String getBbu() {
        return bbu;
    }

    public void setBbu(String bbu) {
        this.bbu = bbu;
    }

    public RealmList<ScanResultTable> getResultTables() {
        synchronized (this) {
            if (resultTables == null) {
                resultTables = new RealmList<>();
            }
        }
        return resultTables;
    }

    public void setResultTables(RealmList<ScanResultTable> resultTables) {
        this.resultTables = resultTables;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTDDtype() {
        return TDDtype;
    }

    public void setTDDtype(int TDDtype) {
        this.TDDtype = TDDtype;
    }
}
