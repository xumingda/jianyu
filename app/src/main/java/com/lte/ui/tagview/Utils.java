package com.lte.ui.tagview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by 06peng on 2017/10/15.
 */

public class Utils {

    private Utils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(1, dipValue, metrics);
    }

    public static int bytes2int(byte[] l) {
        if(l.length == 4){
            return (l[2]& 0xFF)*256+(l[3]& 0xFF);
        }
        return 0;
    }
}
