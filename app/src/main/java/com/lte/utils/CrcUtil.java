package com.lte.utils;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by chenxiaojun on 2018/4/2.
 */

public class CrcUtil {
    private static int[] CRC16Table = {
/* CRC16 余式表 */
            0, 4129, 8258, 12387, 16516, 20645, 24774, 28903,
            33032, 37161, 41290, 45419, 49548, 53677, 57806, 61935,
            4657, 528, 12915, 8786, 21173, 17044, 29431, 25302,
            37689, 33560, 45947, 41818, 54205, 50076, 62463, 58334,
            9314, 13379, 1056, 5121, 25830, 29895, 17572, 21637,
            42346, 46411, 34088, 38153, 58862, 62927, 50604, 54669,
            13907, 9842, 5649, 1584, 30423, 26358, 22165, 18100,
            46939, 42874, 38681, 34616, 63455, 59390, 55197, 51132,
            18628, 22757, 26758, 30887, 2112, 6241, 10242, 14371,
            51660, 55789, 59790, 63919, 35144, 39273, 43274, 47403,
            23285, 19156, 31415, 27286, 6769, 2640, 14899, 10770,
            56317, 52188, 64447, 60318, 39801, 35672, 47931, 43802,
            27814, 31879, 19684, 23749, 11298, 15363, 3168, 7233,
            60846, 64911, 52716, 56781, 44330, 48395, 36200, 40265,
            32407, 28342, 24277, 20212, 15891, 11826, 7761, 3696,
            65439, 61374, 57309, 53244, 48923, 44858, 40793, 36728,
            37256, 33193, 45514, 41451, 53516, 49453, 61774, 57711,
            4224, 161, 12482, 8419, 20484, 16421, 28742, 24679,
            33721, 37784, 41979, 46042, 49981, 54044, 58239, 62302,
            689, 4752, 8947, 13010, 16949, 21012, 25207, 29270,
            46570, 42443, 38312, 34185, 62830, 58703, 54572, 50445,
            13538, 9411, 5280, 1153, 29798, 25671, 21540, 17413,
            42971, 47098, 34713, 38840, 59231, 63358, 50973, 55100,
            9939, 14066, 1681, 5808, 26199, 30326, 17941, 22068,
            55628, 51565, 63758, 59695, 39368, 35305, 47498, 43435,
            22596, 18533, 30726, 26663, 6336, 2273, 14466, 10403,
            52093, 56156, 60223, 64286, 35833, 39896, 43963, 48026,
            19061, 23124, 27191, 31254, 2801, 6864, 10931, 14994,
            64814, 60687, 56684, 52557, 48554, 44427, 40424, 36297,
            31782, 27655, 23652, 19525, 15522, 11395, 7392, 3265,
            61215, 65342, 53085, 57212, 44955, 49082, 36825, 40952,
            28183, 32310, 20053, 24180, 11923, 16050, 3793, 7920
    };
    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     **/
    public static byte[] HexString2Bytes(String src) {
        int length = src.length() / 2;
        byte[] ret = new byte[length];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < length; i++) {
            if (tmp.length > i) {
                ret[i] = tmp[i];

            } else {
                ret[i] = (byte) 0;
            }
        }
        return ret;
    }
    /**
     * 把16进制字符串转换成字节数组
     * @param hexString
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
    /*
    * bytes字符串转换为Byte值
     *
             * @param String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;

            ret[i] = Byte.parseByte( src.substring(i * 2, m) + src.substring(m, n),16);
            Log.d("Welcome","ret[i] :" +ret[i]);

        }
        return ret;
    }
    /**
     * 字符串转化成为16进制字符串
     *
     * @param s
     * @return
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        Log.d("Welcome","str :" +str);
        return str;
    }

    public static String GetCRC(String source) {
        byte[] bytes = hexStr2Bytes(strTo16(source));//转换16进制字符再转bytes
//        int crc = 0;
//        int l = bytes.length;
//        for (int i = 0; i<l;i++){
//            int by = (crc >> 8)&0xff;
//            crc = (crc & 0xffff) <<8;
//            crc = (crc^ CRC16Table[(bytes[i] ^by)& 0xff]) & 0xffff ;
//        }
//        int crc = 0;
//        int l = bytes.length;
//        for (int i = 0; i < l; i++) {
//            int by = (crc >> 8) & 0xff;
//            crc = (crc & 0xffff) << 8;
//            crc = (crc ^ CRC16Table[(bytes[i] ^ by) & 0xff]) & 0xffff;
//        }
////        return crc;
//        Log.d("Welcome","crc :" +crc);
//        return Integer.toHexString(crc);
        int checkSum = 0;
        for (int i = 0 ; i< bytes.length; i++) {
            int by = (checkSum >> 8) & 0xff;
            checkSum = (checkSum & 0xffff) << 8;
            checkSum = (checkSum ^ CRC16Table[(bytes[i] ^ by) & 0xff]) & 0xffff;

        }
        String height = Integer.toHexString(checkSum % 256);
        String low = Integer.toHexString(checkSum/256) ;
        String h = "";
        String l = "";
        Log.d("CRC","H" +height + "--"+low);
        if(height.length() == 1){
            h = "0"+height;
        }else {
            h = height;
        }
        if(low.length() == 1){
            l = "0"+low;
        }else {
            l = low;
        }
        return h+l;
    }

    //byte[]转换为16进制字符串

    public static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder("");

        if ((src == null) || (src.length <= 0)) {

            return null;

        }

        for (int i = 0; i < src.length; ++i) {

            int v = src[i] & 0xFF;

            String hv = Integer.toHexString(v);

            if (hv.length() < 2) {

                stringBuilder.append(0);

            }

            stringBuilder.append(hv);

        }

        return stringBuilder.toString();

    }

}
