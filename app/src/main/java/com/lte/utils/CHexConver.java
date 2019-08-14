package com.lte.utils;

/**
 * Created by Yang on 2017/7/5.
 */

/**
 * 16进制值与String/Byte之间的转换
 * */
public class CHexConver
{
    private static final char mChars[] = "0123456789ABCDEF".toCharArray();
    private static final String mHexStr = "0123456789ABCDEF";

    public static String byte2HexStr(byte abyte0[], int i)
    {
        StringBuilder stringbuilder = new StringBuilder("");
        int j = 0;
        do
        {
            if (j >= i)
                return stringbuilder.toString().toUpperCase().trim();
            String s = Integer.toHexString(0xff & abyte0[j]);
            String s1;
            if (s.length() == 1)
                s1 = (new StringBuilder("0")).append(s).toString();
            else
                s1 = s;
            stringbuilder.append(s1);
//            stringbuilder.append(" ");
            j++;
        } while (true);
    }

    /**
     * 检查16进制字符串是否有效
     * @param sHex String 16进制字符串
     * @return boolean
     */
    public static boolean checkHexStr(String sHex){
        String sTmp = sHex.toString().trim().replace(" ", "").toUpperCase();
        int iLen = sTmp.length();

        if (iLen > 1 && iLen%2 == 0){
            for(int i=0; i<iLen; i++)
                if (!mHexStr.contains(sTmp.substring(i, i+1)))
                    return false;
            return true;
        }
        else
            return false;
    }

    public static byte[] hexStr2Bytes(String s)
    {
        int i = s.length() / 2;
        System.out.println(i);
        byte abyte0[] = new byte[i];
        int j = 0;
        do
        {
            if (j >= i)
                return abyte0;
            int k = 1 + j * 2;
            int l = k + 1;
            abyte0[j] = (byte)(0xff & Integer.decode((new StringBuilder("0x")).append(s.substring(j * 2, k)).append(s.substring(k, l)).toString()).intValue());
            j++;
        } while (true);
    }

    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String hexStr2Str(String s)
    {
        char ac[] = s.toCharArray();
        byte abyte0[] = new byte[s.length() / 2];
        int i = 0;
        do
        {
            if (i >= abyte0.length)
                return new String(abyte0);
            abyte0[i] = (byte)(0xff & 16 * "0123456789ABCDEF".indexOf(ac[i * 2]) + "0123456789ABCDEF".indexOf(ac[1 + i * 2]));
            i++;
        } while (true);
    }

    public static String  str2HexStr(String s)
    {
        StringBuilder stringbuilder = new StringBuilder("");
        byte abyte0[] = s.getBytes();
        int i = 0;
        do
        {
            if (i >= abyte0.length)
                return stringbuilder.toString().trim();
            int j = (0xf0 & abyte0[i]) >> 4;
            stringbuilder.append(mChars[j]);
            int k = 0xf & abyte0[i];
            stringbuilder.append(mChars[k]);
//            stringbuilder.append(' ');
            i++;
        } while (true);
    }

    public static String strToUnicode(String s)
            throws Exception
    {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        do
        {
            if (i >= s.length())
                return stringbuilder.toString();
            char c = s.charAt(i);
            String s1 = Integer.toHexString(c);
            if (c > '\200')
                stringbuilder.append((new StringBuilder("\\u")).append(s1).toString());
            else
                stringbuilder.append((new StringBuilder("\\u00")).append(s1).toString());
            i++;
        } while (true);
    }

    public static String unicodeToString(String s)
    {
        int i = s.length() / 6;
        StringBuilder stringbuilder = new StringBuilder();
        int j = 0;
        do
        {
            if (j >= i)
                return stringbuilder.toString();
            String s1 = s.substring(j * 6, 6 * (j + 1));
            String s2 = (new StringBuilder(String.valueOf(s1.substring(2, 4)))).append("00").toString();
            String s3 = s1.substring(4);
            stringbuilder.append(new String(Character.toChars(Integer.valueOf(s2, 16).intValue() + Integer.valueOf(s3, 16).intValue())));
            j++;
        } while (true);
    }



    /**
     * 10进制转16进制
     * @param num
     * @return
     */
    public static String toHex(int num){
        char[] chs = new char[4];//定义容器，存储的是字符，长度为8.一个整数最多8个16进制数
        int index = chs.length-1;
        for(int i = 0;i<4;i++) {
            int temp = num & 15;

            if(temp > 9){
                chs[index] = ((char)(temp-10+'A'));
            }else {
                chs[index] = ((char)(temp+'0'));
            }

            index--;
            num = num >>> 4;
        }
        return toString(chs);
    }


    /**
     * 将数组转为字符串
     * @param arr
     * @return
     */
    public static String toString(char[] arr){
        String temp = "";
        for(int i = 0;i<arr.length;i++){
            temp = temp + arr[i];
        }
        return temp;
    }

}
