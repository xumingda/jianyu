package com.lte.data.table;

import com.lte.data.CdmaConfig;
import com.lte.data.GsmConfig;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/12/26.
 */

public class CdmaConfigTable extends RealmObject {

    @PrimaryKey
    public Long id = null;//@Id必须为Long

    public String Enable;
    public String MCC;
    public String reDetectMinuts;
    public String SID;
    public String NID;
    public String PN;
    public String BSID;
    public String REGNUM;
    public String CAPTIME;
    public String LOWATT;
    public String UPATT;
    public String SCANTIME;
    public String SCANPERIOD;
    public String FREQ1;
    public String FREQ2;
    public String FREQ3;
    public String FREQ4;
    public String SCANTIME1;
    public String SCANTIME2;
    public String SCANTIME3;
    public String SCANTIME4;
    public String SCANCAPTIME1;
    public String SCANCAPTIME2;
    public String SCANCAPTIME3;
    public String SCANCAPTIME4;
    public String NEIBOR1FREQ1;
    public String NEIBOR2FREQ1;
    public String NEIBOR3FREQ1;
    public String NEIBOR4FREQ1;
    public String NEIBOR1FREQ2;
    public String NEIBOR2FREQ2;
    public String NEIBOR3FREQ2;
    public String NEIBOR4FREQ2;
    public String NEIBOR1FREQ3;
    public String NEIBOR2FREQ3;
    public String NEIBOR3FREQ3;
    public String NEIBOR4FREQ3;
    public String NEIBOR1FREQ4;
    public String NEIBOR2FREQ4;
    public String NEIBOR3FREQ4;
    public String NEIBOR4FREQ4;
    public String MNC;
    public String WORKMODEL;
    public String RESETMODEL;
    public String WORKMODE1;
    public String WORKMODE2;
    public String WORKMODE3;
    public String WORKMODE4;

    public CdmaConfig builder() {
        CdmaConfig cdmaConfig = new CdmaConfig();
        cdmaConfig.id = id;

        cdmaConfig.Enable = Enable;
        cdmaConfig.MCC = MCC;
        cdmaConfig.reDetectMinuts = reDetectMinuts;
        cdmaConfig.SID = SID;
        cdmaConfig.NID = NID;
        cdmaConfig.PN = PN;
        cdmaConfig.BSID = BSID;
        cdmaConfig.REGNUM = REGNUM;
        cdmaConfig.CAPTIME = CAPTIME;
        cdmaConfig.LOWATT = LOWATT;
        cdmaConfig.UPATT = UPATT;
        cdmaConfig.SCANTIME = SCANTIME;
        cdmaConfig.SCANPERIOD = SCANPERIOD;
        cdmaConfig.FREQ1 = FREQ1;
        cdmaConfig.FREQ2 = FREQ2;
        cdmaConfig.FREQ3 = FREQ3;
        cdmaConfig.FREQ4 = FREQ4;
        cdmaConfig.SCANTIME1 = SCANTIME1;
        cdmaConfig.SCANTIME2 = SCANTIME2;
        cdmaConfig.SCANTIME3 = SCANTIME3;
        cdmaConfig.SCANTIME4 = SCANTIME4;
        cdmaConfig.SCANCAPTIME1 = NEIBOR1FREQ1;
        cdmaConfig.SCANCAPTIME2 = NEIBOR1FREQ2;
        cdmaConfig.SCANCAPTIME3 = NEIBOR1FREQ3;
        cdmaConfig.SCANCAPTIME4 = NEIBOR1FREQ4;
        cdmaConfig.NEIBOR1FREQ1 = NEIBOR1FREQ1;
        cdmaConfig.NEIBOR2FREQ1 = NEIBOR2FREQ1;
        cdmaConfig.NEIBOR3FREQ1 = NEIBOR3FREQ1;
        cdmaConfig.NEIBOR4FREQ1 = NEIBOR4FREQ1;
        cdmaConfig.NEIBOR1FREQ2 = NEIBOR1FREQ2;
        cdmaConfig.NEIBOR2FREQ2 = NEIBOR2FREQ2;
        cdmaConfig.NEIBOR3FREQ2 = NEIBOR3FREQ2;
        cdmaConfig.NEIBOR4FREQ2 = NEIBOR4FREQ2;
        cdmaConfig.NEIBOR1FREQ3 = NEIBOR1FREQ3;
        cdmaConfig.NEIBOR2FREQ3 = NEIBOR2FREQ3;
        cdmaConfig.NEIBOR3FREQ3 = NEIBOR3FREQ3;
        cdmaConfig.NEIBOR4FREQ3 = NEIBOR4FREQ3;
        cdmaConfig.NEIBOR1FREQ4 = NEIBOR1FREQ4;
        cdmaConfig.NEIBOR2FREQ4 = NEIBOR2FREQ4;
        cdmaConfig.NEIBOR3FREQ4 = NEIBOR3FREQ4;
        cdmaConfig.NEIBOR4FREQ4 = NEIBOR4FREQ4;
        cdmaConfig.MNC = MNC;
        cdmaConfig.WORKMODEL = WORKMODEL;
        cdmaConfig.RESETMODEL = RESETMODEL;
        cdmaConfig.WORKMODE1 = WORKMODE1;
        cdmaConfig.WORKMODE2 = WORKMODE2;
        cdmaConfig.WORKMODE3 = WORKMODE3;
        cdmaConfig.WORKMODE4 = WORKMODE4;
        cdmaConfig.setCMD();
        return cdmaConfig;
    }
}
