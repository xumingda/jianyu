package com.lte.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Administrator on 2016/12/20 0020.
 */

public class ScreenUtil {

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Context context) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return (int) px;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * �õ��豸��Ļ�ĸ߶�
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
