package com.lte.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.App;
import com.communication.BaseApplication;

public class SharedPreferencesUtil {

	public static void setConfig(Context context, String configName, String configKey, Object configValue) {
		setConfig(context, configName, configKey, configValue,null);
    }
    public static void setConfig(Context context, String configName, String configKey, Object configValue, OnSharedataCommitListener commit) {
        new Thread(new SetConfigRunnable(context, configName, configKey, configValue,commit)).start();
    }

    private static class SetConfigRunnable implements Runnable {

        private Context context;
        private String configName;
        private String configKey;
        private Object configValue;
        private OnSharedataCommitListener commit;
        
        public SetConfigRunnable(Context context, String configName, String configKey, Object configValue, OnSharedataCommitListener commit) {
            this.configKey = configKey;
            this.configName = configName;
            this.configValue = configValue;
            this.context = context;
            this.commit=commit;
        }

        @Override
        public void run() {
            if (context == null)
                context = App.getInstance();
            if (TextUtils.isEmpty(configName) || TextUtils.isEmpty(configKey)) {
                return;
            }

            SharedPreferences.Editor sharedata = context.getSharedPreferences(configName, 0).edit();
            if (configValue instanceof Integer) {
                sharedata.putInt(configKey, (Integer) configValue);
            } else if (configValue instanceof String) {
                sharedata.putString(configKey, (String) configValue);
            } else if (configValue instanceof Boolean) {
                sharedata.putBoolean(configKey, (Boolean) configValue);
            } else if (configValue instanceof Float) {
                sharedata.putFloat(configKey, (Float) configValue);
            } else if (configValue instanceof Long) {
                sharedata.putLong(configKey, (Long) configValue);
            }

            try {
                sharedata.commit();
                if(this.commit!=null){
                	this.commit.onSharedataCommit(configKey,configValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void removeConfig(Context context, String cfgName, String cfgKey) {
    	if (context == null){
    		context = BaseApplication.getInstance().getApplicationContext();
    	}
    	try {
    		SharedPreferences shareData = context.getSharedPreferences(cfgName, 0);
    		shareData.edit().remove(cfgKey);
    		shareData.edit().commit();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    public static int getIntConfig(Context context, String cfgName, String cfgKey, int defVal) {
        if (context == null)
            context = BaseApplication.getInstance();
        SharedPreferences shareData = context.getSharedPreferences(cfgName, 0);
        return shareData.getInt(cfgKey, defVal);
    }


    public static boolean getBooleanConfig(Context context, String cfgName, String cfgKey, boolean defVal) {
        if (context == null)
            context = BaseApplication.getInstance();
        SharedPreferences shareData = context.getSharedPreferences(cfgName, 0);
        return shareData.getBoolean(cfgKey, defVal);
    }


    public static long getLongConfig(Context context, String cfgName, String cfgKey, Long defVal) {
        if (context == null)
            context = BaseApplication.getInstance();
        SharedPreferences shareData = context.getSharedPreferences(cfgName, 0);
        return shareData.getLong(cfgKey, defVal);
    }


    public static String getStringConfig(Context context, String cfgName, String cfgKey, String defVal) {
        if (context == null)
            context = BaseApplication.getInstance();
        SharedPreferences shareData = context.getSharedPreferences(cfgName, 0);
        return shareData.getString(cfgKey, defVal);
    }
    
    public interface OnSharedataCommitListener{
    	/***
    	 * 设置保存成功后触发
    	 * @param configKey    设置键
    	 * @param configValue  设置键值
    	 */
    	void onSharedataCommit(String configKey, Object configValue);
    }

}
