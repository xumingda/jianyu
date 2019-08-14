package com.lte.ui.tagview;

import android.graphics.drawable.Drawable;

/**
 * Created by 06peng on 2017/10/15.
 */

public class Tag {

    public int id;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;
    public String value;
    private long beginTime;

    private long endTime;

    public Tag(String text) {
        this.init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, 14.0F, Constants.DEFAULT_TAG_LAYOUT_COLOR,
                Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR,
                14.0F, 100.0F, "×", 0.0F, Constants.DEFAULT_TAG_LAYOUT_BORDER_COLOR);
    }

    private void init(int id, String text, int tagTextColor, float tagTextSize, int layoutColor, int layoutColorPress,
                      boolean isDeletable, int deleteIndicatorColor, float deleteIndicatorSize, float radius, String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layoutColor;
        this.layoutColorPress = layoutColorPress;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
