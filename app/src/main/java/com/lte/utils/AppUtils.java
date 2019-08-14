package com.lte.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.communication.BaseApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.data.ImsiData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

@SuppressLint("NewApi")
public final class AppUtils {
    private static final int NOTIFICATION_ID = 100003;
    public static Notification notification;
    public static final String FINISH_ALL_ACTIVITY_ACTION = "FINISH_ALL_ACTIVITY_ACTIONG";
    private static List<Activity> activityList = new ArrayList<Activity>();

    /**
     * 添加Activity到容器中 @param activity
     */
    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 从容器中删除Activity @param activity
     */
    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**
     * 遍历所有Activity并finish
     */
    public static void exitAllActivity() {
        if (activityList != null && activityList.size() > 0) {
            try {
                for (Activity activity : activityList) {
                    if (activity != null) {
                        activity.finish();
                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            sb.append("0x");
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toLowerCase());
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * 十六进制转换字符串
     *
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
//		String str = "0123456789ABCDEF";
//		char[] hexs = hexStr.toCharArray();
//		byte[] bytes = new byte[hexStr.length() / 2];
//		int n;
//
//		for (int i = 0; i < bytes.length; i++) {
//			n = str.indexOf(hexs[2 * i]) * 16;
//			n += str.indexOf(hexs[2 * i + 1]);
//			bytes[i] = (byte) (n & 0xff);
//		}
//		return new String(bytes);
        byte[] baKeyword = new byte[hexStr.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            hexStr = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return hexStr;
    }

    public static JsonElement dataParams(ArrayList<ImsiData> mList) {
        JsonArray cmdListArray = new JsonArray();
        for (ImsiData imsiData : mList) {
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("cellNumber", "");
            jsonObject1.addProperty("imei", imsiData.getImei());
            jsonObject1.addProperty("imsi", imsiData.getImsi());
            jsonObject1.addProperty("tmsi", "");
            jsonObject1.addProperty("uptime", (imsiData.getTime() / 1000L));
            cmdListArray.add(jsonObject1);
        }
        return cmdListArray;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     **/
    public static byte[] HexString2Bytes(String src, int length) {
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
     * bytes字符串转换为Byte值
     *
     * @param String src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < src.length(); i++) {
            c = src.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            str.append(strHex);
        }
        int m = 0, n = 0;
        int l = str.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Integer.decode("0x" + str.substring(i * 2, m) + str.substring(m, n)).byteValue();
        }
        return ret;
    }
    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }
    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt4(byte[] src, int offset) {
        if(src.length >= 2){
            int value;
            value = (int) (((src[offset] & 0xFF)<<8)
                    |(src[offset+1] & 0xFF));
            return value;
        }
        return 0;
    }
    /**
     * 将两个ASCII字符合成一个字节；
     * 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     **/
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static int little_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
        } else {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
            addr |= (((int) bytes[2] << 16) & 0xFF0000);
            addr |= (((int) bytes[3] << 24) & 0xFF000000);
        }
        return addr;
    }

    public static byte[] little_intToByte(int i, int len) {
        byte[] abyte = new byte[len];
        if (len == 1) {
            abyte[0] = (byte) (0xff & i);
        } else if (len == 2) {
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
        } else {
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            abyte[2] = (byte) ((0xff0000 & i) >> 16);
            abyte[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return abyte;
    }

    public static String bytesToHexString1(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & aBArray);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 通过网络接口取
     * @return
     */
    public static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String get03Cmd(String targetAddress, String address, int length) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(targetAddress);
        stringBuilder.append("23");
        stringBuilder.append(address);
        String l = Integer.toHexString(length);
        stringBuilder.append("0").append(l.length());
        if (l.length() < 2) {
            stringBuilder.append("0").append(l);
        } else {
            stringBuilder.append(l);
        }
        String crc = CrcUtil.GetCRC(stringBuilder.toString());

        return ":" +
                stringBuilder +
                Constants.crcId +
                crc +
                "\r\n";
    }
    public static String get03Cmd(String targetAddress, String address) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(targetAddress);
        stringBuilder.append("23");
        stringBuilder.append(address);
        String crc = CrcUtil.GetCRC(stringBuilder.toString());
        return ":" +
                stringBuilder +
                Constants.crcId +
                crc +
                "\r\n";
    }
    public static String replaceBeginAndEnd(String str) {
        if (str == null) {
            return str;
        }
        String start = str.substring(0,str.length()/2);
        String end = str.substring(str.length()/2);
        return end+start;
    }
    public static int parseHex4(String num) {
        if (num.length() != 4) {
            throw new NumberFormatException("Wrong length: " + num.length() + ", must be 4.");
        }
        int ret = Integer.parseInt(num, 16);
        ret = ((ret & 0x8000) > 0) ? (ret - 0x10000) : (ret);
        return ret;
    }
    /**
     * 以小端模式将byte[]转成int
     */
    public static int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8));
        return value;
    }
    /**
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len < 4 ) {
            String tmp = "0000" + byteStr;
            //取最后4位，将多补的0去掉
            byteStr += tmp.substring(tmp.length() - 4);
        }else if(len >4 && len < 8){
            String tmp = "0000" + byteStr;
            //取最后4位，将多补的0去掉
            byteStr += tmp.substring(tmp.length() - 8);
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }
    public static String hexString2binaryString(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        String binaryString = "";
        for (int i = 0; i < hexString.length(); i++) {
            //截取hexStr的一位
            String hex = hexString.substring(i, i + 1);
            //通过toBinaryString将十六进制转为二进制
            String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));
            //因为高位0会被舍弃，先补上4个0
            String tmp = "0000" + binary;
            //取最后4位，将多补的0去掉
            binaryString += tmp.substring(tmp.length() - 4);
        }
        return binaryString;
    }
    public static void exitApp(Context context) {
//		MobclickAgent.onKillProcess(context);
//        try {
//        	/**释放cpu  java.lang.Exception: WakeLock finalized while still held*/
//        	SettingManager.getInstance().releaseWakeLock();
//
//        	hideNotification(context);
//        	  QASUtil.onExit();
//        	  MusicPlayManager.getInstance(context).stopPlayMusic(true);
//              try {
//            	  //modified by djm 2015.4.14
//            	  long t_online_time=System.currentTimeMillis()-app_online_time;
//            	  SharedPreferences.Editor sharedata = context.getSharedPreferences("app_online_time_config", 0).edit();
//                  sharedata.putString("app_online_time_value",String.valueOf(t_online_time));
//                  sharedata.commit();
//                  Log.d("app_online_time","app_online_time exitApp :"+t_online_time);
//              } catch(Exception ex){
//            	  ex.printStackTrace();
//        	  }
//
//		new Thread(){
//			@Override
//			public void run() {
//				Log.d("*** DEBUG ***","start");
//				CMDExecute execute = new CMDExecute();
////				String command1="/system/bin/chmod 777 /data/bin/fep";
//				String command1="killall -9 bes";
//				String command2="killall -9 fep";
//				if(execute.rootCommand(command1)){
//					Log.d("*** DEBUG ***","exitApp success1111");
//				}
//				if(execute.rootCommand(command2)){
//					Log.d("*** DEBUG ***","exitApp success222");
//				}
//				super.run();
//			}
//		}.start();
//		if (SocketService.thread1 != null) {
//			try {
//				SocketService.thread1.closeSocket();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if (SocketService.thread2 != null) {
//			try {
//				SocketService.thread2.closeSocket();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		context.stopService(new Intent(context,SocketService.class));
//		SocketService.finishService();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        exitAllActivity();
//			  hasPopedZarketingCampaign = false;
//              if (SettingManager.getInstance().getSleepType() != SleepTimingSet.SLEEP_TIME_TYPE_CLOSE) {
//            	  SettingManager.getInstance().setSleepType(SleepTimingSet.SLEEP_TIME_TYPE_CLOSE);
//                  SettingManager.getInstance().setSleepTime(context, 0);
//              }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
        finishApp(context);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
    }

    /**
     * 关闭应用程序
     */
    public static void finishApp(final Context context) {
        try {
            Log.d("finishApp", "finishAppfinishApp");
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Integer.parseInt(Build.VERSION.SDK) < 8) {
                manager.restartPackage(context.getPackageName());
            } else {
                // 结束所有服务
                List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(100);
                for (ActivityManager.RunningServiceInfo runningService : runningServices) {
                    if (runningService.process.startsWith(context.getPackageName())) {
                        Intent intent = new Intent();
                        intent.setComponent(runningService.service);
                        context.stopService(intent);
                    }
                }
                context.sendBroadcast(new Intent(FINISH_ALL_ACTIVITY_ACTION));
                // 结束进程
                new Thread() {
                    @Override
                    public void run() {
                        while (getRunningActivityNumber(context) > 0) {
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前应用程序被打开的 Activity 界面数
     *
     * @param context 上下文
     * @return Activity 个数
     */
    public static int getRunningActivityNumber(Context context) {
        int num = 0;
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> runningTasks = manager.getRunningTasks(100);
            String packageName = context.getPackageName();
            for (RunningTaskInfo taskInfo : runningTasks) {
                if (packageName.equals(taskInfo.baseActivity.getPackageName())) {
                    num += taskInfo.numActivities;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * 清除指定通知
     *
     * @param notificationId 需要清除的通知id
     */
    public static void clearNotification(int notificationId) {
        NotificationManager manager = (NotificationManager) BaseApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    public static void showToast(Context context, int stringId) {
        try {
            if (context != null) {
                showToast(context, context.getString(stringId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示自定义样式的Toast消息(默认Duration Toast.LENGTH_SHORT)
     *
     * @param context  ctx
     * @param toastMsg 待显示的Toast消息内容
     */
    public static void showToast(Context context, String toastMsg) {
        showToast(context, toastMsg, Toast.LENGTH_SHORT);
    }

    @Deprecated
    public static void showToastOK(Context context, String toastMsg) {
        showToast(context, toastMsg, Toast.LENGTH_SHORT);
    }

    @Deprecated
    public static void showToastWarn(Context context, String toastMsg) {
        showToast(context, toastMsg, Toast.LENGTH_SHORT);
    }

    /**
     * 显示自定义样式的Toast消息
     *
     * @param duration 显示时长 （Toast.LENGTH_SHORT or Toast.LENGTH_LONG）
     * @param context  ctx
     * @param toastMsg 待显示的Toast消息内容
     */
    public static void showToast(Context context, String toastMsg, int duration) {
        ToastUtils.showToast(context, toastMsg, duration);
    }

    private AppUtils() {
    }


//    public static boolean isCalling(Context context) {
//      	if(null==context){
//    		     return false;
//     	 }
//    	boolean isCalling=false;
//    	TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//    	TelephonyManager telephonyManager2 ;
//
//    	List<String> SamsungGsmUAList=Arrays.asList(context.getResources().getStringArray(R.array.Samsung_GSM_ua));
//    	if(SamsungGsmUAList.contains(Build.MODEL)){
//    		try {
//				telephonyManager2 = (TelephonyManager) context.getSystemService("phone2");
//				if(telephonyManager!=null&&telephonyManager2!=null){
//					int callState=telephonyManager.getCallState();
//					int callState2=telephonyManager2.getCallState();
//					if( callState!=TelephonyManager.PHONE_TYPE_NONE||callState2!=0){
//						isCalling= true;
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//    	}else{
//    		if(telephonyManager!=null){
//    			int callState=telephonyManager.getCallState();
//    			if( callState!=TelephonyManager.PHONE_TYPE_NONE){
//    				isCalling= true;
//    			}
//    		}
//    	}
//		return isCalling;
//    }


    public static int getStatusBarHeight(Context context) {
        int barHeight = 0;


        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            barHeight = context.getResources().getDimensionPixelSize(x);
            return barHeight;
        } catch (Exception e1) {

            e1.printStackTrace();
            float fontScale = context.getResources().getDisplayMetrics().density;
            if (fontScale == 2.0f) {
                barHeight = 50;
            } else if (fontScale == 1.5f) {
                barHeight = 38;
            } else if (fontScale == 1.0f) {
                barHeight = 25;
            } else if (fontScale == 0.75) {
                barHeight = 19;
            }
        }
        return barHeight;
    }


    public static boolean isHaveSDCard() {
        boolean haveSD = false;
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            haveSD = true;
        } else {
            haveSD = false;
        }
        return haveSD;
    }


    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }


    public static File getAssetsApk(Context context, String apkname) {
        final String dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iting/";
        try {
            String apkLocalPath = dbPath + apkname;
            File apkfile = new File(apkLocalPath);
            if (createApkFile(apkfile)) {
                InputStream is = context.getResources().getAssets().open(apkname);
                FileOutputStream fos = new FileOutputStream(apkLocalPath);
                byte[] buffer = new byte[81920];
                int count = 0;

                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
                return apkfile;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("", "apk文件读取拷贝失败！");
        }
        return null;
    }


    public static boolean createApkFile(File filepath) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.i("", "~!!!!!!!!!!!!!!!Path: " + filepath.getPath());

            if (filepath.mkdirs()) {
                Log.i("", "~!!!!!!!!!!!!!!!:新建文件路径成功！");
            }
            if (filepath.exists()) {
                Log.i("", "~!!!!!!!!!!!!!!!:删除成功否？···" + filepath.delete());
            }
            try {
                boolean isNewOK = filepath.createNewFile();
                Log.i("", "~!!!!!!!!!!!!!!!:新建空文件成功？" + isNewOK);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("", "~!!!!!!!!!!!!!!!:新建空文件出错！");
            }
        } else {
            Log.i("", "~!!!!!!!!!!!!!!!:SDcard不存在！");
        }
        return false;
    }


    public static boolean checkInstalled(Context context, String packageName) {

        Log.i("~~:", "   checkInstalled..." + packageName);
        if (packageName == null) return false;
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo != null) {
            return true;
        }
        return false;
    }


    public static void openInputKeyboard(Context context, View editText) {
        try {
            if (context == null) {
                Log.e("AppUtils", "hideInputKeyboard context is NULL");
                return;
            }
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (editText != null) {
                inputMethodManager.showSoftInput(editText, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hideInputKeyboard(Activity activity) {
        try {
            if (activity == null) {
                return;
            }
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏键盘
     *
     * @param context
     */
    public static void hideInputKeyboard(Context context, View view) {
        if (context == null) {
            Log.e("AppUtils", "hideInputKeyboard context is NULL");
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出键盘
     *
     * @param context
     */
    public static void showInputKeyboard(Context context) {
        if (context == null) {
            Log.e("AppUtils", "showInputKeyboard context is NULL");
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化手机号码。 如：188****8888
     *
     * @param phoneStr 需要格式的手机号码
     * @return
     */
    public static String getFormatPhone(String phoneStr) {
        if (phoneStr != null && phoneStr.length() == 11) {
            return phoneStr.substring(0, 3) + "****" + phoneStr.substring(phoneStr.length() - 4, phoneStr.length());
        } else {
            return phoneStr;
        }
    }

    /**
     * 当前应用程序处于后台. true:后台 false:前台
     */
    public static boolean isAppAtBackground = false;

    /**
     * 判断当前应用程序处于后台
     *
     * @param context
     * @return true:后台 false:前台
     */
    public static boolean isApplicationAtBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否在主线程
     *
     * @return true:是
     */
    public static boolean isMainThread() {
        if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
            return true;
        }
        return false;
    }


    public static boolean isPackageExists(Context context, String targetPackage) {

        try {
            List<ApplicationInfo> packages;
            PackageManager pm;
            pm = context.getPackageManager();
            packages = pm.getInstalledApplications(0);

            for (ApplicationInfo packageInfo : packages) {
                if (packageInfo.packageName.equals(targetPackage)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isWifi(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public static boolean isWeakPassword(String password) {
        if (password == null || password.length() <= 1) { //字符串长度小于2就判定为弱密码
            return true;
        }

        // 第一步，判断是否是重复字母字符串,不为重复字母执行下一步判断，负责判定为弱密码
        for (int i = 0, length = password.length(); i < length - 1; i++) {
            char currentChar = password.charAt(i);
            char nextChar = password.charAt(i + 1);
            if (currentChar != nextChar) { // 字符出现不相同，退出循环
                break;
            } else if (i == length - 2) { // 遍历到最后，字符未出现不同,则字符串未同一字符构成
                return true;
            }
        }

        //第二步，判断是否是纯数字，是纯数字执行下一步判断，负责判定为非弱密码
        if (!TextUtils.isDigitsOnly(password)) {
            return false;
        }

        //第三步、判断纯数字是否按顺序排列
        if (password.charAt(0) == '0') { //仅1-9依次排列的数字为弱密码
            return false;
        }
        for (int i = 0, j = 0, length = password.length(); i < length - 1; i++) {
            char currentChar = password.charAt(i);
            char nextChar = password.charAt(i + 1);
            if (nextChar - currentChar == 1) { //递增，则j++
                j++;
            } else if (nextChar - currentChar == -1) { //递减，则j--
                j--;
            } else {
                return false; //不是按序排列数字
            }
            if (j != (i + 1) && j != 0 - (i + 1)) { //如果一直递增，则j == i + 1，如果一直递减则， j == -(i + 1)，其他情况代表出现了非递增、递减的情况
                return false;
            }
        }
        return true;
    }


    public static boolean isYunOS() {
        String version = null;
        String vmName = null;

        try {
            Method m = Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class});
            version = (String) m.invoke((Object) null, new Object[]{"ro.yunos.version"});
            vmName = (String) m.invoke((Object) null, new Object[]{"java.vm.name"});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (vmName != null && vmName.toLowerCase().contains("lemur")) || (version != null && version.trim().length() > 0);
    }

    /**
     * 判断当前手机是否有ROOT权限
     *
     * @return
     */
    public boolean isRoot() {
        boolean bool = false;
        try {
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
            Log.d("isRoot", "bool = " + bool);
        } catch (Exception e) {

        }
        return bool;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static String getHttpUrl(String ip, String port, String detile) {
        return "http://" + ip + ":" + port + "/" + detile;
    }

    public static String getHttpsUrl(String ip, String port, String detile) {
        return "https://" + ip + ":" + port + "/" + detile;
    }

    public static String getConnectWifiSsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID().replace("\"", "");
    }
    public static InetAddress getBroadcastAddress(Context context) throws IOException {
        WifiManager myWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
        if (myDhcpInfo == null) {
            return null;
        }
        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
                + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }
    public static int getWifiGateIP(Context context){
        if(context==null){
            return -1;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if(dhcpInfo==null){
            return  -1;
        }
        return dhcpInfo.gateway;
    }
    /**
     * 字符串转换成十六进制字符串
     * @param String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str)
    {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }
    public static String getHttpUrl(String ip){
        return "http://"+ip+":"+"5000";
    }
}
