package com.lte.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;


public class EventHelper {
    private static Map<String, Long> pluginLastLaunchTime = new HashMap<String, Long>();


    public static boolean isRubbish(Context context, String mask) {
        boolean result = false;

        try {
            long lastLaunchTime = pluginLastLaunchTime.containsKey(mask) ? pluginLastLaunchTime.get(mask) : 0;
            if (System.currentTimeMillis() - lastLaunchTime < 1500) {
                result = true;
//                Log.e("EventHelper", "the event "+mask+ " is rubbish,so will be abandoned");
            }

            pluginLastLaunchTime.put(mask, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    

    public static boolean isRubbish(Context context, String mask,long ignoretime) {
    	boolean result = false;

        try {
            long lastLaunchTime = pluginLastLaunchTime.containsKey(mask) ? pluginLastLaunchTime.get(mask) : 0;
            if (System.currentTimeMillis() - lastLaunchTime < ignoretime) {
                result = true;
//    		Log.e("EventHelper", "the event "+mask+ " is rubbish,so will be abandoned");
            }

            pluginLastLaunchTime.put(mask, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
