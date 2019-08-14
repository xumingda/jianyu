package com.lte.data.table;

import com.lte.data.GsmConfig;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/12/21.
 */

public class GsmConfigTable extends RealmObject {

    @PrimaryKey
    public Long id = null;//@Id必须为Long

    public String Enable1;
    public String BAND1;
    public String BCC1;
    public String MCC1;
    public String MNC1;
    public String LAC1;
    public String CRO1;
    public String CAPTIME1;
    public String LOWATT1;
    public String UPATT1;
    public String CONFIGMODE1;
    public String WORKMODE1;
    public String Enable2;
    public String BAND2;
    public String BCC2;
    public String MCC2;
    public String MNC2;
    public String LAC2;
    public String CRO2;
    public String CAPTIME2;
    public String LOWATT2;
    public String UPATT2;
    public String CONFIGMODE2;
    public String WORKMODE2;

    public GsmConfig builder() {
        GsmConfig gsmConfig = new GsmConfig();
        gsmConfig.id = id;
        gsmConfig.Enable1 = Enable1;
        gsmConfig.BAND1 = BAND1;
        gsmConfig.BCC1 = BCC1;
        gsmConfig.MCC1 = MCC1;
        gsmConfig.MNC1 = MNC1;
        gsmConfig.LAC1 = LAC1;
        gsmConfig.CRO1 = CRO1;
        gsmConfig.CAPTIME1 = CAPTIME1;
        gsmConfig.LOWATT1 = LOWATT1;
        gsmConfig.UPATT1 = UPATT1;
        gsmConfig.CONFIGMODE1 = CONFIGMODE1;
        gsmConfig.WORKMODE1 = WORKMODE1;
        gsmConfig.Enable2 = Enable2;
        gsmConfig.BAND2 = BAND2;
        gsmConfig.BCC2 = BCC2;
        gsmConfig.MCC2 = MCC2;
        gsmConfig.MNC2 = MNC2;
        gsmConfig.LAC2 = LAC2;
        gsmConfig.CRO2 = CRO2;
        gsmConfig.CAPTIME2 = CAPTIME2;
        gsmConfig.LOWATT2 = LOWATT2;
        gsmConfig.UPATT2 = UPATT2;
        gsmConfig.CONFIGMODE2 = CONFIGMODE2;
        gsmConfig.WORKMODE2 = WORKMODE2;
        gsmConfig.setCMD();
        return gsmConfig;
    }
}
