package com.lte.ui.event;

/**
 * Created by chenxiaojun on 2017/10/31.
 */

public class SystemOutEvent {
    private boolean out;

    public SystemOutEvent(boolean out) {
        this.out = out;
    }

    public boolean isOut() {
        return out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }
}
