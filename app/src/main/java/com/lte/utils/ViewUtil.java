package com.lte.utils;

import android.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

public class ViewUtil {

    public static int dip2px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dip * scale + 0.5f);
    }
    
    public static int dip2px(Context context, float dip) {
    	float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (dip * scale + 0.5f);
    }
    

    public static int px2dip(Context context, float pxValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (pxValue / scale + 0.5f);
    }


    public static void setMargin(View view, int left, int top, int right, int bottom) {
        if (view == null) {
            return;
        }
        MarginLayoutParams marginLayoutParams = null;
        if (view.getLayoutParams() != null && view.getLayoutParams() instanceof MarginLayoutParams) {
            marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.setMargins(left, top, right, bottom);
        }
    }


    public static ColorStateList createColorStateList(int normal, int pressed,int selected, int focused, int unable) {
        int[] colors = new int[]{pressed,selected, focused, normal, focused, unable, normal};
        int[][] states = new int[7][];
        states[0] = new int[]{R.attr.state_pressed, R.attr.state_enabled};
        states[1] = new int[]{R.attr.state_selected, R.attr.state_enabled};
        states[2] = new int[]{R.attr.state_enabled, R.attr.state_focused};
        states[3] = new int[]{R.attr.state_enabled};
        states[4] = new int[]{R.attr.state_focused};
        states[5] = new int[]{R.attr.state_window_focused};
        states[6] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }


    public static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused,
                                                int idUnable) {
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);

        return newSelector(normal,pressed,focused,unable);
    }


    public static StateListDrawable newSelector(Drawable normal,Drawable pressed,Drawable focused,Drawable unable){
        StateListDrawable bg = new StateListDrawable();

        bg.addState(new int[] { R.attr.state_pressed, R.attr.state_enabled }, pressed);

        bg.addState(new int[] { R.attr.state_enabled, R.attr.state_focused }, focused);

        bg.addState(new int[] { R.attr.state_enabled }, normal);

        bg.addState(new int[] { R.attr.state_focused }, focused);

        bg.addState(new int[] { R.attr.state_window_focused }, unable);

        bg.addState(new int[] {}, normal);
        return bg;
    }
    
    
    /**
     * 获取文本行的高度
     *
     * @param paint 画笔
     * @return 高度
     */
    public static float getStringHeight(Paint paint) {
        float height = 0F;
        if (paint != null) {
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            height = fontMetrics.bottom - fontMetrics.top;
        }
        return height;
    }
    
    /**
     * 获取字符串中每一个字符在屏幕上显示的宽度
     *
     * @param paint 画笔
     * @param str   目标字符串
     * @return 每一个字符的宽度数组，如 str 为空，则返回 null
     */
    public static float[] getCharacterWidth(Paint paint, String str) {
        float[] widths = null;
        if (!TextUtils.isEmpty(str) && paint != null) {
            widths = new float[str.length()];
            paint.getTextWidths(str, widths);
        }
        return widths;
    }
    
    /**
     * 获取字符串在屏幕上的显示宽度
     *
     * @param paint 画笔
     * @param str   目标字符串
     * @return 字符串的显示宽度
     */
    public static float getStringWidth(Paint paint, String str) {
        float width = 0F;
        float[] widths = getCharacterWidth(paint, str);
        if (widths != null) {
            for (float f : widths) {
                width += f;
            }
        }
        return width;
    }
    public static int getColor(Context mContext,int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.getColor(resId);
        }else {
            return mContext.getResources().getColor(resId);
        }
    }
}
