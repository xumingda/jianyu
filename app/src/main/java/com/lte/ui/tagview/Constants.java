package com.lte.ui.tagview;

import android.graphics.Color;

/**
 * Created by 06peng on 2017/10/15.
 */

public class Constants {

    public static final float DEFAULT_LINE_MARGIN = 5.0F;
    public static final float DEFAULT_TAG_MARGIN = 5.0F;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8.0F;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5.0F;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8.0F;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5.0F;
    public static final float LAYOUT_WIDTH_OFFSET = 2.0F;
    public static final float DEFAULT_TAG_TEXT_SIZE = 14.0F;
    public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14.0F;
    public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0.0F;
    public static final float DEFAULT_TAG_RADIUS = 100.0F;
    public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#AED374");
    public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#88363636");
    public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
    public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
    public static final String DEFAULT_TAG_DELETE_ICON = "×";
    public static final boolean DEFAULT_TAG_IS_DELETABLE = false;

    private Constants() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }
}
