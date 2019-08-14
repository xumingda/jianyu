package com.lte.ui.event;

/*
 *  Date:2018/5/29.
 *  Time:下午3:25.
 *  author:chenxiaojun
 */

import com.lte.data.StationInfo;

public class RedireectEvent {
    private int code;

    private StationInfo stationInfo;

    public RedireectEvent(StationInfo stationInfo,int code) {
        this.stationInfo = stationInfo;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public StationInfo getStationInfo() {
        return stationInfo;
    }

    public void setStationInfo(StationInfo stationInfo) {
        this.stationInfo = stationInfo;
    }
}
