package com.lte.utils;


public class SendCommendHelper {

    public static String getPower(){
        String cmd = ":"+"1101"+"0C01"+"02"+"0C03"+"01";
        return cmd;
    }

    public static String getStateTag(){
        String cmd = ":"+"1101"+"0C03"+"01";
        return cmd;
    }

    public static String getElectricity(){
        String cmd = ":"+"1101"+"0C01"+"02"+"0C02"+"01";
        return cmd;
    }

    public static String setTDD(){
        String cmd = ":"+"1103"+"0B01"+"00";
        return cmd;
    }

    public static String setFDD(){
        String cmd = ":"+"1103"+"0B01"+"01";
        return cmd;
    }

    public static String setSyn(){
        String cmd = ":"+"1103"+"0B02";
        return cmd;
    }

    public static String setSyn40(){
        String cmd = ":"+"1103"+"0B02"+"00";
        return cmd;
    }

    public static String setSyn39(){
        String cmd = ":"+"1103"+"0B02"+"01";
        return cmd;
    }

    public static String setSyn38(){
        String cmd = ":"+"1103"+"0B02"+"02";
        return cmd;
    }

    public static String setSyn41(){
        String cmd = ":"+"1103"+"0B02"+"03";
        return cmd;
    }

    public static String setSyn_b1_lian(){
        String cmd = ":"+"1103"+"0B02"+"04";
        return cmd;
    }

    public static String setSyn_b1_dian(){
        String cmd = ":"+"1103"+"0B02"+"05";
        return cmd;
    }

    public static String setSyn_b3_lian(){
        String cmd = ":"+"1103"+"0B02"+"06";
        return cmd;
    }

    public static String setSyn_b3_dian(){
        String cmd = ":"+"1103"+"0B02"+"07";
        return cmd;
    }

    public static String setSyn_b5(){
        String cmd = ":"+"1103"+"0B02"+"08";
        return cmd;
    }

    public static String setSyn_b8(){
        String cmd = ":"+"1103"+"0B02"+"09";
        return cmd;
    }

    public static String setChannel(){
        String cmd = ":"+"1103"+"0B03";
        return cmd;
    }

    public static String setPCI(){
        String cmd = ":"+"1103"+"0B04";
        return cmd;
    }

    public static String setDisplay1(){
        String cmd = ":"+"1103"+"0B05"+"00";
        return cmd;
    }

    public static String setDisplay2(){
        String cmd = ":"+"1103"+"0B05"+"01";
        return cmd;
    }

    public static String setDisplay3(){
        String cmd = ":"+"1103"+"0B05"+"02";
        return cmd;
    }

    public static String setDisplay4(){
        String cmd = ":"+"1103"+"0B05"+"03";
        return cmd;
    }

    public static String getMcuVer(){
        String cmd = ":"+"1101"+"0A01"+"02"+"0A02"+"02";
        return cmd;
    }

    public static String getFpgaVer(){
        String cmd = ":"+"1101"+"0A02"+"02";
        return cmd;
    }


}
