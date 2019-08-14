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

public class GsmConfig {
    public Long id = null;//@Id必须为Long
    public String Enable1 = "1";
    public String BAND1 = "900";
    public String BCC1 ="35";
    public String MCC1 = "460";
    public String MNC1 = "0";
    public String LAC1 ="1255";
    public String CRO1 ="60";
    public String CAPTIME1 ="600";
    public String LOWATT1 ="22";
    public String UPATT1 ="55";
    public String CONFIGMODE1 = "0";
    public String WORKMODE1;
    public String Enable2 = "1";
    public String BAND2 ="900";
    public String BCC2 ="112";
    public String MCC2 ="460";
    public String MNC2 = "1";
    public String LAC2 ="1600";
    public String CRO2 = "60";
    public String CAPTIME2 ="600";
    public String LOWATT2 ="22";
    public String UPATT2 ="55";
    public String CONFIGMODE2 = "0";
    public String WORKMODE2;

    public byte[] cmd1;

    public byte[] cmd2;

    public byte[] cmd3;

    public byte[] cmd4;

    public GsmConfig(){

    }
    public void setCMD(){
        ThreadUtils.getThreadPoolProxy().execute(runnable);
        ThreadUtils.getThreadPoolProxy().execute(runnable1);
    }
    public byte[] getCmd1(){
        return cmd1;
    }
    public byte[] getCmd2(){
        return cmd2;
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            byte[] mcc1 = new byte[0];
            if(MCC1 != null){
                mcc1 = new byte[0x0b];
                mcc1[0] = 0x0b;
                mcc1[1] = 0x01;
                mcc1[2] = 0x01;
                byte[] bytes = HexString2Bytes(MCC1, 8);
                System.arraycopy(bytes, 0, mcc1, 3, bytes.length);;
            }
            byte[] mnc1 = new byte[0];
            if(MNC1 != null){
                mnc1 = new byte[0x0b];
                mnc1[0] = 0x0b;
                mnc1[1] = 0x02;
                mnc1[2] = 0x01;
                byte[] bytes = HexString2Bytes(MNC1, 8);
                System.arraycopy(bytes, 0, mnc1, 3, bytes.length);;
            }
            byte[] bcc1 = new byte[0];
            if(BCC1 != null){
                bcc1 = new byte[0x0b];
                bcc1[0] = 0x0b;
                bcc1[1] = 0x50;
                bcc1[2] = 0x01;
                byte[] bytes = HexString2Bytes(BCC1, 8);
                System.arraycopy(bytes, 0, bcc1, 3, bytes.length);;
            }
            byte[] lac1 = new byte[0];
            if(LAC1 != null){
                lac1 = new byte[0x0b];
                lac1[0] = 0x0b;
                lac1[1] = 0x03;
                lac1[2] = 0x01;
                byte[] bytes = HexString2Bytes(LAC1, 8);
                System.arraycopy(bytes, 0, lac1, 3, bytes.length);;
            }
            byte[] cro1 = new byte[0];
            if(CRO1 != null){
                cro1 = new byte[0x0b];
                cro1[0] = 0x0b;
                cro1[1] = 0x06;
                cro1[2] = 0x01;
                byte[] bytes = HexString2Bytes(CRO1, 8);
                System.arraycopy(bytes, 0, cro1, 3, bytes.length);;
            }
            byte[] cap1 = new byte[0];
            if(CAPTIME1 != null){
                cap1 = new byte[7];
                cap1[0] = 0x07;
                cap1[1] = 0x0c;
                cap1[2] = 0x01;
                byte[] bytes = null;
                try {
                    bytes = little_intToByte(Integer.parseInt(CAPTIME1),4);
                }catch (Exception ignored){

                }
                if(bytes != null){
                    System.arraycopy(bytes, 0, cap1, 3, bytes.length);;
                }
            }
            byte[] low1 = new byte[0];
            if(LOWATT1 != null){
                low1 = new byte[0x0b];
                low1[0] = 0x0b;
                low1[1] = 0x51;
                low1[2] = 0x01;
                byte[] bytes = HexString2Bytes(LOWATT1, 8);
                System.arraycopy(bytes, 0, low1, 3, bytes.length);;
            }
            byte[] up1 = new byte[0];
            if(UPATT1 != null){
                up1 = new byte[0x0b];
                up1[0] = 0x0b;
                up1[1] = 0x52;
                up1[2] = 0x01;
                byte[] bytes = HexString2Bytes(UPATT1, 8);
                System.arraycopy(bytes, 0, up1, 3, bytes.length);;
            }
            byte[] config1 = new byte[0];
            if(CONFIGMODE1 != null){
                config1 = new byte[0x04];
                config1[0] = 0x04;
                config1[1] = 0x0a;
                config1[2] = 0x01;
                try {
                    config1[3] = Byte.parseByte(CAPTIME1);
                }catch (Exception ignored){

                }
            }
            byte[] work1 = new byte[0];
            if(WORKMODE1 != null){
                work1 = new byte[0x04];
                work1[0] = 0x04;
                work1[1] = 0x0b;
                work1[2] = 0x01;
                try {
                    work1[3] = Byte.parseByte(WORKMODE1);
                }catch (Exception ignored){

                }
            }
            int length = 8+ mcc1.length + mnc1.length+bcc1.length+lac1.length+cro1.length
                    +cap1.length+low1.length+up1.length +config1.length+work1.length;
            cmd1 = new byte[length];
            byte[] dataHead = new byte[8];
            byte[] dataLength = little_intToByte(length,4);
            System.arraycopy(dataLength,0,dataHead,0,dataLength.length);
            dataHead[4] = (byte) ++App.get().udpNo;
            dataHead[5] = 0x02;
            dataHead[6] = 0;
            dataHead[7] = 0;
            System.arraycopy(dataHead, 0, cmd1, 0, dataHead.length);
            System.arraycopy(mcc1, 0, cmd1, 8, mcc1.length);
            System.arraycopy(mnc1, 0, cmd1, 8+mcc1.length, mnc1.length);
            System.arraycopy(bcc1, 0, cmd1, 8+mcc1.length+mnc1.length, bcc1.length);
            System.arraycopy(lac1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length, lac1.length);
            System.arraycopy(cro1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length+lac1.length, cro1.length);
            System.arraycopy(cap1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length+lac1.length+cro1.length, cap1.length);
            System.arraycopy(low1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length+lac1.length+cro1.length+cap1.length, low1.length);
            System.arraycopy(up1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length+lac1.length+cro1.length+cap1.length+low1.length, up1.length);
            System.arraycopy(config1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length+lac1.length+cro1.length+cap1.length+low1.length+up1.length, config1.length);
            System.arraycopy(work1, 0, cmd1, 8+mcc1.length+mnc1.length+bcc1.length+lac1.length+cro1.length+cap1.length+low1.length+up1.length+config1.length, work1.length);

            byte[] mcc2 = new byte[0];
            if(MCC2 != null){
                mcc2 = new byte[0x0b];
                mcc2[0] = 0x0b;
                mcc2[1] = 0x01;
                mcc2[2] = 0x01;
                byte[] bytes = HexString2Bytes(MCC2, 8);
                System.arraycopy(bytes, 0, mcc2, 3, bytes.length);;
            }
            byte[] mnc2 = new byte[0];
            if(MNC2 != null){
                mnc2 = new byte[0x0b];
                mnc2[0] = 0x0b;
                mnc2[1] = 0x02;
                mnc2[2] = 0x01;
                byte[] bytes = HexString2Bytes(MNC2, 8);
                System.arraycopy(bytes, 0, mnc2, 3, bytes.length);;
            }
            byte[] bcc2 = new byte[0];
            if(BCC2 != null){
                bcc2 = new byte[0x0b];
                bcc2[0] = 0x0b;
                bcc2[1] = 0x50;
                bcc2[2] = 0x01;
                byte[] bytes = HexString2Bytes(BCC2, 8);
                System.arraycopy(bytes, 0, bcc2, 3, bytes.length);;
            }
            byte[] lac2 = new byte[0];
            if(LAC2 != null){
                lac2 = new byte[0x0b];
                lac2[0] = 0x0b;
                lac2[1] = 0x03;
                lac2[2] = 0x01;
                byte[] bytes = HexString2Bytes(LAC2, 8);
                System.arraycopy(bytes, 0, lac2, 3, bytes.length);;
            }
            byte[] cro2 = new byte[0];
            if(CRO2 != null){
                cro2 = new byte[0x0b];
                cro2[0] = 0x0b;
                cro2[1] = 0x06;
                cro2[2] = 0x01;
                byte[] bytes = HexString2Bytes(CRO2, 8);
                System.arraycopy(bytes, 0, cro2, 3, bytes.length);;
            }
            byte[] cap2 = new byte[0];
            if(CAPTIME2 != null){
                cap2 = new byte[7];
                cap2[0] = 0x07;
                cap2[1] = 0x0c;
                cap2[2] = 0x01;
                byte[] bytes = null;
                try {
                    bytes = little_intToByte(Integer.parseInt(CAPTIME2),4);
                }catch (Exception ignored){

                }
                if(bytes != null){
                    System.arraycopy(bytes, 0, cap2, 3, bytes.length);;
                }
            }
            byte[] low2 = new byte[0];
            if(LOWATT2 != null){
                low2 = new byte[0x0b];
                low2[0] = 0x0b;
                low2[1] = 0x51;
                low2[2] = 0x01;
                byte[] bytes = HexString2Bytes(LOWATT2, 8);
                System.arraycopy(bytes, 0, low2, 3, bytes.length);;
            }
            byte[] up2 = new byte[0];
            if(UPATT2 != null){
                up2 = new byte[0x0b];
                up2[0] = 0x0b;
                up2[1] = 0x52;
                up2[2] = 0x01;
                byte[] bytes = HexString2Bytes(UPATT2, 8);
                System.arraycopy(bytes, 0, up2, 3, bytes.length);;
            }
            byte[] config2 = new byte[0];
            if(CONFIGMODE2 != null){
                config2 = new byte[0x04];
                config2[0] = 0x04;
                config2[1] = 0x0a;
                config2[2] = 0x01;
                try {
                    config2[3] = Byte.parseByte(CONFIGMODE2);
                }catch (Exception ignored){

                }
            }
            byte[] work2 = new byte[0];
            if(WORKMODE2 != null){
                work2 = new byte[0x04];
                work2[0] = 0x04;
                work2[1] = 0x0b;
                work2[2] = 0x01;
                try {
                    work2[3] = Byte.parseByte(WORKMODE2);
                }catch (Exception ignored){

                }
            }
            int length2 = 8+ mcc2.length + mnc2.length+bcc2.length+lac2.length+cro2.length
                    +cap2.length+low2.length+up2.length +config2.length+work2.length;
            cmd2 = new byte[length2];
            byte[] dataHead2 = new byte[8];
            byte[] dataLength2 = little_intToByte(length2,4);
            System.arraycopy(dataLength2,0,dataHead2,0,dataLength2.length);
            dataHead2[4] = (byte) ++App.get().udpNo;
            dataHead2[5] = 0x02;
            dataHead2[6] = 1;
            dataHead2[7] = 0;
            System.arraycopy(dataHead2, 0, cmd2, 0, dataHead2.length);
            System.arraycopy(mcc2, 0, cmd2, 8, mcc2.length);
            System.arraycopy(mnc2, 0, cmd2, 8+mcc2.length, mnc2.length);
            System.arraycopy(bcc2, 0, cmd2, 8+mcc2.length+mnc2.length, bcc2.length);
            System.arraycopy(lac2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length, lac2.length);
            System.arraycopy(cro2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length+lac2.length, cro2.length);
            System.arraycopy(cap2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length+lac2.length+cro2.length, cap2.length);
            System.arraycopy(low2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length+lac2.length+cro2.length+cap2.length, low2.length);
            System.arraycopy(up2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length+lac2.length+cro2.length+cap2.length+low2.length, up2.length);
            System.arraycopy(config2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length+lac2.length+cro2.length+cap2.length+low2.length+up2.length, config2.length);
            System.arraycopy(work2, 0, cmd2, 8+mcc2.length+mnc2.length+bcc2.length+lac2.length+cro2.length+cap2.length+low2.length+up2.length+config2.length, work2.length);
        }
    };
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            byte[] mcc1 = new byte[0];
            if(MCC1 != null){
                mcc1 = new byte[0x0b];
                mcc1[0] = 0x0b;
                mcc1[1] = 0x01;
                mcc1[2] = 0x01;
                byte[] bytes = HexString2Bytes(MCC1, 8);
                System.arraycopy(bytes, 0, mcc1, 3, bytes.length);;
            }
            byte[] mnc1 = new byte[0];
            if(MNC1 != null){
                mnc1 = new byte[0x0b];
                mnc1[0] = 0x0b;
                mnc1[1] = 0x02;
                mnc1[2] = 0x01;
                byte[] bytes = HexString2Bytes(MNC1, 8);
                System.arraycopy(bytes, 0, mnc1, 3, bytes.length);;
            }
//            byte[] bcc1 = new byte[0];
//            if(BCC1 != null){
//                bcc1 = new byte[0x0b];
//                bcc1[0] = 0x0b;
//                bcc1[1] = 0x50;
//                bcc1[2] = 0x01;
//                byte[] bytes = HexString2Bytes(BCC1, 8);
//                System.arraycopy(bytes, 0, bcc1, 3, bytes.length);;
//            }
            byte[] lac1 = new byte[0];
            if(LAC1 != null){
                lac1 = new byte[0x0b];
                lac1[0] = 0x0b;
                lac1[1] = 0x03;
                lac1[2] = 0x01;
                byte[] bytes = HexString2Bytes(LAC1, 8);
                System.arraycopy(bytes, 0, lac1, 3, bytes.length);;
            }
            byte[] cro1 = new byte[0];
            if(CRO1 != null){
                cro1 = new byte[0x0b];
                cro1[0] = 0x0b;
                cro1[1] = 0x06;
                cro1[2] = 0x01;
                byte[] bytes = HexString2Bytes(CRO1, 8);
                System.arraycopy(bytes, 0, cro1, 3, bytes.length);;
            }
            byte[] cap1 = new byte[0];
            if(CAPTIME1 != null){
                cap1 = new byte[7];
                cap1[0] = 0x07;
                cap1[1] = 0x0c;
                cap1[2] = 0x01;
                byte[] bytes = null;
                try {
                    bytes = little_intToByte(Integer.parseInt(CAPTIME1),4);
                }catch (Exception ignored){

                }
                if(bytes != null){
                    System.arraycopy(bytes, 0, cap1, 3, bytes.length);;
                }
            }
            byte[] low1 = new byte[0];
            if(LOWATT1 != null){
                low1 = new byte[0x0b];
                low1[0] = 0x0b;
                low1[1] = 0x51;
                low1[2] = 0x01;
                byte[] bytes = HexString2Bytes(LOWATT1, 8);
                System.arraycopy(bytes, 0, low1, 3, bytes.length);;
            }
            byte[] up1 = new byte[0];
            if(UPATT1 != null){
                up1 = new byte[0x0b];
                up1[0] = 0x0b;
                up1[1] = 0x52;
                up1[2] = 0x01;
                byte[] bytes = HexString2Bytes(UPATT1, 8);
                System.arraycopy(bytes, 0, up1, 3, bytes.length);;
            }
            byte[] config1 = new byte[0];
            if(CONFIGMODE1 != null){
                config1 = new byte[0x04];
                config1[0] = 0x04;
                config1[1] = 0x0a;
                config1[2] = 0x01;
                try {
                    config1[3] = Byte.parseByte(CONFIGMODE1);
                }catch (Exception ignored){

                }
            }
            byte[] work1 = new byte[0];
            if(WORKMODE1 != null){
                work1 = new byte[0x04];
                work1[0] = 0x04;
                work1[1] = 0x0b;
                work1[2] = 0x01;
                try {
                    work1[3] = Byte.parseByte(WORKMODE1);
                }catch (Exception ignored){

                }
            }
            int length = 8+ mcc1.length + mnc1.length+lac1.length+cro1.length
                    +cap1.length+low1.length+up1.length +config1.length+work1.length;
            cmd3 = new byte[length];
            byte[] dataHead = new byte[8];
            byte[] dataLength = little_intToByte(length,4);
            System.arraycopy(dataLength,0,dataHead,0,dataLength.length);
            dataHead[4] = (byte) ++App.get().udpNo;
            dataHead[5] = 0x02;
            dataHead[6] = 0;
            dataHead[7] = 0;
            System.arraycopy(dataHead, 0, cmd3, 0, dataHead.length);
            System.arraycopy(mcc1, 0, cmd3, 8, mcc1.length);
            System.arraycopy(mnc1, 0, cmd3, 8+mcc1.length, mnc1.length);
//            System.arraycopy(bcc1, 0, cmd1, 8+mcc1.length+mnc1.length, bcc1.length);
            System.arraycopy(lac1, 0, cmd3, 8+mcc1.length+mnc1.length, lac1.length);
            System.arraycopy(cro1, 0, cmd3, 8+mcc1.length+mnc1.length+lac1.length, cro1.length);
            System.arraycopy(cap1, 0, cmd3, 8+mcc1.length+mnc1.length+lac1.length+cro1.length, cap1.length);
            System.arraycopy(low1, 0, cmd3, 8+mcc1.length+mnc1.length+lac1.length+cro1.length+cap1.length, low1.length);
            System.arraycopy(up1, 0, cmd3, 8+mcc1.length+mnc1.length+lac1.length+cro1.length+cap1.length+low1.length, up1.length);
            System.arraycopy(config1, 0, cmd3, 8+mcc1.length+mnc1.length+lac1.length+cro1.length+cap1.length+low1.length+up1.length, config1.length);
            System.arraycopy(work1, 0, cmd3, 8+mcc1.length+mnc1.length+lac1.length+cro1.length+cap1.length+low1.length+up1.length+config1.length, work1.length);

            byte[] mcc2 = new byte[0];
            if(MCC2 != null){
                mcc2 = new byte[0x0b];
                mcc2[0] = 0x0b;
                mcc2[1] = 0x01;
                mcc2[2] = 0x01;
                byte[] bytes = HexString2Bytes(MCC2, 8);
                System.arraycopy(bytes, 0, mcc2, 3, bytes.length);;
            }
            byte[] mnc2 = new byte[0];
            if(MNC2 != null){
                mnc2 = new byte[0x0b];
                mnc2[0] = 0x0b;
                mnc2[1] = 0x02;
                mnc2[2] = 0x01;
                byte[] bytes = HexString2Bytes(MNC2, 8);
                System.arraycopy(bytes, 0, mnc2, 3, bytes.length);;
            }
//            byte[] bcc2 = new byte[0];
//            if(BCC2 != null){
//                bcc2 = new byte[0x0b];
//                bcc2[0] = 0x0b;
//                bcc2[1] = 0x50;
//                bcc2[2] = 0x01;
//                byte[] bytes = HexString2Bytes(BCC2, 8);
//                System.arraycopy(bytes, 0, bcc2, 3, bytes.length);;
//            }
            byte[] lac2 = new byte[0];
            if(LAC2 != null){
                lac2 = new byte[0x0b];
                lac2[0] = 0x0b;
                lac2[1] = 0x03;
                lac2[2] = 0x01;
                byte[] bytes = HexString2Bytes(LAC2, 8);
                System.arraycopy(bytes, 0, lac2, 3, bytes.length);;
            }
            byte[] cro2 = new byte[0];
            if(CRO2 != null){
                cro2 = new byte[0x0b];
                cro2[0] = 0x0b;
                cro2[1] = 0x06;
                cro2[2] = 0x01;
                byte[] bytes = HexString2Bytes(CRO2, 8);
                System.arraycopy(bytes, 0, cro2, 3, bytes.length);;
            }
            byte[] cap2 = new byte[0];
            if(CAPTIME2 != null){
                cap2 = new byte[7];
                cap2[0] = 0x07;
                cap2[1] = 0x0c;
                cap2[2] = 0x01;
                byte[] bytes = null;
                try {
                    bytes = little_intToByte(Integer.parseInt(CAPTIME2),4);
                }catch (Exception ignored){

                }
                if(bytes != null){
                    System.arraycopy(bytes, 0, cap2, 3, bytes.length);;
                }
            }
            byte[] low2 = new byte[0];
            if(LOWATT2 != null){
                low2 = new byte[0x0b];
                low2[0] = 0x0b;
                low2[1] = 0x51;
                low2[2] = 0x01;
                byte[] bytes = HexString2Bytes(LOWATT2, 8);
                System.arraycopy(bytes, 0, low2, 3, bytes.length);;
            }
            byte[] up2 = new byte[0];
            if(UPATT2 != null){
                up2 = new byte[0x0b];
                up2[0] = 0x0b;
                up2[1] = 0x52;
                up2[2] = 0x01;
                byte[] bytes = HexString2Bytes(UPATT2, 8);
                System.arraycopy(bytes, 0, up2, 3, bytes.length);;
            }
            byte[] config2 = new byte[0];
            if(CONFIGMODE2 != null){
                config2 = new byte[0x04];
                config2[0] = 0x04;
                config2[1] = 0x0a;
                config2[2] = 0x01;
                try {
                    config2[3] = Byte.parseByte(CONFIGMODE2);
                }catch (Exception ignored){

                }
            }
            byte[] work2 = new byte[0];
            if(WORKMODE2 != null){
                work2 = new byte[0x04];
                work2[0] = 0x04;
                work2[1] = 0x0b;
                work2[2] = 0x01;
                try {
                    work2[3] = Byte.parseByte(WORKMODE2);
                }catch (Exception ignored){

                }
            }
            int length2 = 8+ mcc2.length + mnc2.length+lac2.length+cro2.length
                    +cap2.length+low2.length+up2.length +config2.length+work2.length;
            cmd4 = new byte[length2];
            byte[] dataHead2 = new byte[8];
            byte[] dataLength2 = little_intToByte(length2,4);
            System.arraycopy(dataLength2,0,dataHead2,0,dataLength2.length);
            dataHead2[4] = (byte) ++App.get().udpNo;
            dataHead2[5] = 0x02;
            dataHead2[6] = 1;
            dataHead2[7] = 0;
            System.arraycopy(dataHead2, 0, cmd4, 0, dataHead2.length);
            System.arraycopy(mcc2, 0, cmd4, 8, mcc2.length);
            System.arraycopy(mnc2, 0, cmd4, 8+mcc2.length, mnc2.length);
//            System.arraycopy(bcc2, 0, cmd2, 8+mcc2.length+mnc2.length, bcc2.length);
            System.arraycopy(lac2, 0, cmd4, 8+mcc2.length+mnc2.length, lac2.length);
            System.arraycopy(cro2, 0, cmd4, 8+mcc2.length+mnc2.length+lac2.length, cro2.length);
            System.arraycopy(cap2, 0, cmd4, 8+mcc2.length+mnc2.length+lac2.length+cro2.length, cap2.length);
            System.arraycopy(low2, 0, cmd4, 8+mcc2.length+mnc2.length+lac2.length+cro2.length+cap2.length, low2.length);
            System.arraycopy(up2, 0, cmd4, 8+mcc2.length+mnc2.length+lac2.length+cro2.length+cap2.length+low2.length, up2.length);
            System.arraycopy(config2, 0, cmd4, 8+mcc2.length+mnc2.length+lac2.length+cro2.length+cap2.length+low2.length+up2.length, config2.length);
            System.arraycopy(work2, 0, cmd4, 8+mcc2.length+mnc2.length+lac2.length+cro2.length+cap2.length+low2.length+up2.length+config2.length, work2.length);
        }
    };

    @Override
    public String toString() {
        return "GsmConfig{" +
                "id=" + id +
                ", Enable1='" + Enable1 + '\'' +
                ", BAND1='" + BAND1 + '\'' +
                ", BCC1='" + BCC1 + '\'' +
                ", MCC1='" + MCC1 + '\'' +
                ", MNC1='" + MNC1 + '\'' +
                ", LAC1='" + LAC1 + '\'' +
                ", CRO1='" + CRO1 + '\'' +
                ", CAPTIME1='" + CAPTIME1 + '\'' +
                ", LOWATT1='" + LOWATT1 + '\'' +
                ", UPATT1='" + UPATT1 + '\'' +
                ", CONFIGMODE1='" + CONFIGMODE1 + '\'' +
                ", WORKMODE1='" + WORKMODE1 + '\'' +
                ", Enable2='" + Enable2 + '\'' +
                ", BAND2='" + BAND2 + '\'' +
                ", BCC2='" + BCC2 + '\'' +
                ", MCC2='" + MCC2 + '\'' +
                ", MNC2='" + MNC2 + '\'' +
                ", LAC2='" + LAC2 + '\'' +
                ", CRO2='" + CRO2 + '\'' +
                ", CAPTIME2='" + CAPTIME2 + '\'' +
                ", LOWATT2='" + LOWATT2 + '\'' +
                ", UPATT2='" + UPATT2 + '\'' +
                ", CONFIGMODE2='" + CONFIGMODE2 + '\'' +
                ", WORKMODE2='" + WORKMODE2 + '\'' +
                ", cmd1=" + Arrays.toString(cmd1) +
                ", cmd2=" + Arrays.toString(cmd2) +
                ", cmd3=" + Arrays.toString(cmd3) +
                ", cmd4=" + Arrays.toString(cmd4) +
                ", runnable=" + runnable +
                ", runnable1=" + runnable1 +
                '}';
    }
}
