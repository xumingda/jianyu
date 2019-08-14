package com.lte.ui.event;

import com.lte.data.ImsiData;
import com.lte.data.MacData;

import java.util.List;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/22.
 */

public class MessageEvent {
    private List<MacData> macDataList;
    private List<ImsiData> imsiDataList;
    public String data;

    public boolean isUpDate;

    public String point_vibrate;   //移动循环定位开关
    public String point_vibrate1;  //联通循环定位开关
    public String point_vibrate2;  //电信循环定位开关

    public MacData macData;

    public ImsiData imsiData;

    public MessageEvent(String b,String b1,String b2){
        point_vibrate=b;
        point_vibrate1=b1;
        point_vibrate2=b2;
    }

    public MessageEvent(MacData macData) {
        this.macData = macData;
    }

    public MessageEvent(ImsiData imsiData) {
        this.imsiData = imsiData;
    }

    public MessageEvent(String bing) {
        data = bing;
    }

    public MessageEvent(boolean isUpDate) {
        this.isUpDate = isUpDate;
    }
    public MessageEvent(List<ImsiData> imsiDataList) {
        this.imsiDataList = imsiDataList;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "macDataList=" + macDataList +
                ", imsiDataList=" + imsiDataList +
                ", data='" + data + '\'' +
                ", isUpDate=" + isUpDate +
                ", macData=" + macData +
                ", imsiData=" + imsiData +
                ", imsiData=" + point_vibrate +
                ", imsiData=" + point_vibrate1 +
                ", imsiData=" + point_vibrate2 +
                '}';
    }
}
