package com.lte.data;

import android.text.TextUtils;

import com.App;
import com.lte.utils.ThreadUtils;

import java.util.Arrays;

import static com.lte.utils.AppUtils.HexString2Bytes;
import static com.lte.utils.AppUtils.little_intToByte;

/**
 * Created by chenxiaojun on 2017/12/21.
 */

public class CdmaConfig {
    public Long id = null;//@Id必须为Long
    public String Enable = "1";
    public String MCC = "460";
    public String reDetectMinuts = "600";
    public String SID = "13824";
    public String NID =" 3";
    public String PN = "0";
    public String BSID = "2036";
    public String REGNUM = "1261" ;
    public String CAPTIME = "600";
    public String LOWATT = "30";
    public String UPATT = "55";
    public String SCANTIME = "5";
    public String SCANPERIOD = "25";
    public String FREQ1 = "160";
    public String FREQ2 = "201";
    public String FREQ3 = "242";
    public String FREQ4 = "283";
    public String SCANTIME1 = "5";
    public String SCANTIME2 = "5";
    public String SCANTIME3 = "5";
    public String SCANTIME4 = "5";
    public String SCANCAPTIME1 = "25" ;
    public String SCANCAPTIME2 = "25";
    public String SCANCAPTIME3 = "25";
    public String SCANCAPTIME4 = "25";
    public String NEIBOR1FREQ1 = "0";
    public String NEIBOR2FREQ1 = "0";
    public String NEIBOR3FREQ1 = "0";
    public String NEIBOR4FREQ1 = "0";
    public String NEIBOR1FREQ2 = "0";
    public String NEIBOR2FREQ2 = "0";
    public String NEIBOR3FREQ2 = "0";
    public String NEIBOR4FREQ2 = "0";
    public String NEIBOR1FREQ3 = "0";
    public String NEIBOR2FREQ3 = "0";
    public String NEIBOR3FREQ3 = "0";
    public String NEIBOR4FREQ3 = "0";
    public String NEIBOR1FREQ4 = "0";
    public String NEIBOR2FREQ4 = "0";
    public String NEIBOR3FREQ4 = "0";
    public String NEIBOR4FREQ4 = "0";
    public String MNC = "02";
    public String WORKMODEL = "0";
    public String RESETMODEL = "1";
    public String WORKMODE1 ="1";
    public String WORKMODE2 ="1";
    public String WORKMODE3 ="1";
    public String WORKMODE4 = "1";
    public byte[] cmd1;

    public void setCMD() {
        ThreadUtils.getThreadPoolProxy().execute(runnable);
    }

    public byte[] getCmd1() {
        return cmd1;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            byte[] mcc = new byte[0];
            if (MCC != null) {
                mcc = new byte[0x0b];
                mcc[0] = 0x0b;
                mcc[1] = 0x01;
                mcc[2] = 0x01;
                byte[] bytes = HexString2Bytes(MCC, 8);
                System.arraycopy(bytes, 0, mcc, 3, bytes.length);
            }
            byte[] pn = new byte[0];
            if (PN != null) {
                pn = new byte[0x0b];
                pn[0] = 0x0b;
                pn[1] = 0x10;
                pn[2] = 0x01;
                byte[] bytes = HexString2Bytes(PN, 8);
                System.arraycopy(bytes, 0, pn, 3, bytes.length);
            }
            byte[] sid = new byte[0];
            if (SID != null) {
                sid = new byte[0x0b];
                sid[0] = 0x0b;
                sid[1] = 0x11;
                sid[2] = 0x01;
                byte[] bytes = HexString2Bytes(SID, 8);
                System.arraycopy(bytes, 0, sid, 3, bytes.length);
            }
            byte[] nid = new byte[0];
            if (NID != null) {
                nid = new byte[0x0b];
                nid[0] = 0x0b;
                nid[1] = 0x12;
                nid[2] = 0x01;
                byte[] bytes = HexString2Bytes(NID, 8);
                System.arraycopy(bytes, 0, nid, 3, bytes.length);
            }
            byte[] bsid = new byte[0];
            if (BSID != null) {
                bsid = new byte[0x0b];
                bsid[0] = 0x0b;
                bsid[1] = 0x13;
                bsid[2] = 0x01;
                byte[] bytes = HexString2Bytes(BSID, 8);
                System.arraycopy(bytes, 0, bsid, 3, bytes.length);
            }
//            byte[] cdma = new byte[0];
//            if (!= null) {
//                pn = new byte[0x0b];
//                pn[0] = 0x0b;
//                pn[1] = 0x10;
//                pn[2] = 0x01;
//                byte[] bytes = HexString2Bytes(PN, 8);
//                System.arraycopy(bytes, 0, pn, 3, bytes.length);
//            }
            byte[] mnc = new byte[0];
            if (MNC != null) {
                mnc = new byte[0x0b];
                mnc[0] = 0x0b;
                mnc[1] = 0x02;
                mnc[2] = 0x01;
                byte[] bytes = HexString2Bytes(MNC, 8);
                System.arraycopy(bytes, 0, mnc, 3, bytes.length);
                ;
            }

            byte[] cap = new byte[0];
            if (CAPTIME != null) {
                cap = new byte[7];
                cap[0] = 0x07;
                cap[1] = 0x0c;
                cap[2] = 0x01;
                byte[] bytes = null;
                try {
                    bytes = little_intToByte(Integer.parseInt(CAPTIME), 4);
                } catch (Exception ignored) {

                }
                if (bytes != null) {
                    System.arraycopy(bytes, 0, cap, 3, bytes.length);
                    ;
                }
            }
            byte[] low = new byte[0];
            if (LOWATT != null) {
                low = new byte[0x0b];
                low[0] = 0x0b;
                low[1] = 0x51;
                low[2] = 0x01;
                byte[] bytes = HexString2Bytes(LOWATT, 8);
                System.arraycopy(bytes, 0, low, 3, bytes.length);
                ;
            }
            byte[] up = new byte[0];
            if (UPATT != null) {
                up = new byte[0x0b];
                up[0] = 0x0b;
                up[1] = 0x52;
                up[2] = 0x01;
                byte[] bytes = HexString2Bytes(UPATT, 8);
                System.arraycopy(bytes, 0, up, 3, bytes.length);
                ;
            }

            byte[] work = new byte[0];
            if (WORKMODE1 != null) {
                work = new byte[0x04];
                work[0] = 0x04;
                work[1] = 0x0b;
                work[2] = 0x01;
                try {
                    work[3] = Byte.parseByte(WORKMODE1);
                } catch (Exception ignored) {

                }
            }
            int length2 = 8+ mcc.length + pn.length+sid.length+nid.length+bsid.length
                    +mnc.length+cap.length+low.length +up.length+work.length;
            cmd1 = new byte[length2];
            byte[] dataHead2 = new byte[8];
            byte[] dataLength2 = little_intToByte(length2,4);
            System.arraycopy(dataLength2,0,dataHead2,0,dataLength2.length);
            dataHead2[4] = (byte) ++App.get().udpNo;
            dataHead2[5] = 0x02;
            dataHead2[6] = 0;
            dataHead2[7] = 0;
            System.arraycopy(dataHead2, 0, cmd1, 0, dataHead2.length);
            System.arraycopy(mcc, 0, cmd1, 8, mcc.length);
            System.arraycopy(pn, 0, cmd1, 8+mcc.length, pn.length);
            System.arraycopy(sid, 0, cmd1, 8+mcc.length+pn.length, sid.length);
            System.arraycopy(nid, 0, cmd1, 8+mcc.length+pn.length+sid.length, nid.length);
            System.arraycopy(bsid, 0, cmd1, 8+mcc.length+pn.length+sid.length+nid.length, bsid.length);
            System.arraycopy(mnc, 0, cmd1, 8+mcc.length+pn.length+sid.length+nid.length+bsid.length, mnc.length);
            System.arraycopy(cap, 0, cmd1, 8+mcc.length+pn.length+sid.length+nid.length+bsid.length+mnc.length, cap.length);
            System.arraycopy(low, 0, cmd1, 8+mcc.length+pn.length+sid.length+nid.length+bsid.length+mnc.length+cap.length, low.length);
            System.arraycopy(up, 0, cmd1, 8+mcc.length+pn.length+sid.length+nid.length+bsid.length+mnc.length+cap.length+low.length, up.length);
            System.arraycopy(work, 0, cmd1, 8+mcc.length+pn.length+sid.length+nid.length+bsid.length+mnc.length+cap.length+low.length+up.length, work.length);

        }
    };
}
