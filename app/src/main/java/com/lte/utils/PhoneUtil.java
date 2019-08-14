package com.lte.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;

/**
 * 手机相关工具类，用户获取手机相关信息
 */
public final class PhoneUtil {
    private static PhoneUtil phoneUtil;
    private Context mContext;
    private TelephonyManager telephonyManager;
    
    //内部发布测试标识
    public boolean isInsideTest(){
    	return false;
    }

    /**
     * 获取 PhoneUtil 类的实例
     *
     * @param context 上下文
     * @return 唯一的 PhoneUtil 实例
     */
    public synchronized static PhoneUtil getInstance(Context context) {
        if (phoneUtil == null || phoneUtil.mContext == null) {
            phoneUtil = new PhoneUtil(context);
        }
        return phoneUtil;
    }

    private PhoneUtil(Context context) {
        try {
            mContext = context.getApplicationContext();
            telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 将 dp 单位的值换算成 px 单位
     *
     * @param dp dp 单位的数值
     * @return 转换后的以 px 为单位的数值
     */
    public int dp2px(int dp) {
        float scale = 1.0f;
        try{
            scale = mContext.getResources().getDisplayMetrics().density;
        }
        catch(Exception exp){
            exp.printStackTrace();
        }
        return (int) (dp * scale + 0.5);
    }

    /**
     * 判断是否有SD卡
     *
     * @return
     */
    public static boolean isHaveSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 获取手机的电子串号
     *
     * @return 手机电子串号
     */
    public String getEsn() {
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取客户端系统版本
     *
     * @return 客户端系统版本
     */
    public String getClientOSVersion() {
        return "android " + Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public String getModel() {
        String model = Build.MODEL;
        if ("sdk".equals(model)) {
            model = "XT800";
        }
        return model;
    }

    /**
     * 获取 IMEI 号<br/>
     * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称<br/>
     * IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的<br/>
     * 其组成为：<br/>
     * 1. 前6位数(TAC)是”型号核准号码”，一般代表机型<br/>
     * 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地<br/>
     * 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号<br/>
     * 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用<br/>
     *
     * @return IMEI 号
     */
    public String getIMEI() {
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取手机的 IMSI 号<br/>
     * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)<br/>
     * IMSI共有15位，其结构如下：<br/>
     * MCC+MNC+MIN<br/>
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;<br/>
     * MNC:Mobile NetworkCode，移动网络码，共2位<br/>
     * 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03<br/>
     * 合起来就是（也是Android手机中APN配置文件中的代码）：<br/>
     * 中国移动：46000 46002<br/>
     * 中国联通：46001<br/>
     * 中国电信：46003<br/>
     * 举例，一个典型的IMSI号码为460030912121001<br/>
     *
     * @return IMSI 号
     */
    public String getIMSI() {
    	String imsi=null;
//        int state = telephonyManager.getSimState();
//        if(state == TelephonyManager.SIM_STATE_ABSENT || state == TelephonyManager.SIM_STATE_UNKNOWN){
//            return null;
//        }
		try {
			imsi = telephonyManager.getSubscriberId();
			Log.d("PhoneUtil","getIMSI() imsi=" + imsi);
		} catch (Exception e) {
			e.printStackTrace();
		}

        if (imsi == null || imsi.length() == 0){
            imsi = "00000000000000";
        }
        return imsi;
    }

    /**
     * 获取手机 SDK 版本
     *
     * @return 手机 SDK 版本
     */
    public int getPhoneSDK() {
    	if (Build.VERSION.SDK == null) {
    		return Build.VERSION.SDK_INT;
    	}
    	try {
    		return Integer.parseInt(Build.VERSION.SDK);
    	} catch (NumberFormatException e) {
    		e.printStackTrace();
    	}
        return Build.VERSION.SDK_INT;
    }
    
    public int getTargetSdkVersion(){
    	int version = 0;
	    PackageManager pm = mContext.getPackageManager();
	    try {
	        ApplicationInfo applicationInfo = pm.getApplicationInfo(mContext.getPackageName(), 0);
	        if (applicationInfo != null) {
	          version = applicationInfo.targetSdkVersion;
	        } 
	    } catch (Exception e){
	    	e.printStackTrace();
	    }
	    return version;
    }

    /**
     * 获取手机内置 CONFIG_UA
     *
     * @return CONFIG_UA
     */
    public String getPhoneUA() {
        return Build.MODEL;
    }

    /**
     * 获取手机屏幕信息
     *
     * @return 手机屏幕信息，如尺寸等
     */
    public DisplayMetrics getScreenInfo() {
        DisplayMetrics dm = new DisplayMetrics();
        try {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dm;
    }

    /**
     * 获取手机屏幕显示区域大小，包括标题栏，不包括状态栏
     *
     * @param activity Activity 对象
     * @return 手机屏幕显示区域大小
     */
    public Rect getPhoneDisplaySize(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect;
    }

    /**
     * 获取手机外部存储器的总空间大小<br/>
     * 单位：byte
     *
     * @return 外部存储器不存在，则返回 -1
     */
    public long getTotalExternalMemorySize() {
        if (!isHaveSDCard()) {
            return -1;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long blockCount = stat.getBlockCount();
        return blockSize * blockCount;
    }

    /**
     * 获取手机内存的总空间大小<br/>
     * 单位：byte
     *
     * @return 手机内存总大小
     */
    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
    
}
