package com.lte.data;

import android.text.TextUtils;


/**
 * Created by chenxiaojun on 2017/8/14.
 */
public class UserInfo {

    private Long id;//@Id必须为Long

    private String userName;
    private String password;

    private String mobileUserName;

    private String mobilePassword;

    private Long imsiStartTime;

    private Long imsiendTime;

    private Long initTime;

    private int ImsiType;

    private Long macStartTime;

    private Long macendTime;

    private int macType;

    private Long imsipreClearTime = 0l;

    private Long macpreClearTime = 0l;

    private String sceneName;

    private String url;
    private String queryUrl;

    private String imsiPort;

    private String mobilePort;

    private String deviceType;


    public UserInfo(Long id, String userName,String password,String mobileUserName, String mobilePassword, Long imsiStartTime, Long imsiendTime, Long initTime, int ImsiType,
                    Long macStartTime, Long macendTime,int macType,Long imsipreClearTime,Long macpreClearTime,String url,String queryUrl
    ,String imsiPort,String mobilePort,String sceneName,String deviceType) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.mobileUserName = mobileUserName;
        this.mobilePassword = mobilePassword;
        this.imsiStartTime = imsiStartTime;
        this.imsiendTime = imsiendTime;
        this.initTime = initTime;
        this.macStartTime = macStartTime;
        this.macendTime = macendTime;
        this.ImsiType = ImsiType;
        this.macType = macType;
        this.imsipreClearTime = imsipreClearTime;
        this.macpreClearTime = macpreClearTime;
        this.url = url;
        this.queryUrl = queryUrl;
        this.imsiPort = imsiPort;
        this.mobilePort = mobilePort;
        this.sceneName = sceneName;
        this.deviceType = deviceType;
    }
    public UserInfo(){}

    public UserInfo(long l, String superAccountNum, String superAccountKey) {
        this.id = l;
        this.mobileUserName = superAccountNum;
        this.mobilePassword = superAccountKey;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMobileUserName() {
        return this.mobileUserName;
    }
    public void setMobileUserName(String mobileUserName) {
        this.mobileUserName = mobileUserName;
    }
    public String getMobilePassword() {
        return this.mobilePassword;
    }
    public void setMobilePassword(String mobilePassword) {
        this.mobilePassword = mobilePassword;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) obj;

            return TextUtils.equals(userInfo.getMobileUserName(),this.mobileUserName);
        } else {
            return false;
        }
    }


    public Long getImsiStartTime() {
        return imsiStartTime;
    }

    public void setImsiStartTime(Long imsiStartTime) {
        this.imsiStartTime = imsiStartTime;
    }

    public Long getImsiendTime() {
        return imsiendTime;
    }

    public void setImsiendTime(Long imsiendTime) {
        this.imsiendTime = imsiendTime;
    }

    public Long getInitTime() {
        return initTime;
    }

    public void setInitTime(Long initTime) {
        this.initTime = initTime;
    }

    public int getImsiType() {
        return ImsiType;
    }

    public void setImsiType(int imsiType) {
        ImsiType = imsiType;
    }

    public Long getMacStartTime() {
        return macStartTime;
    }

    public void setMacStartTime(Long macStartTime) {
        this.macStartTime = macStartTime;
    }

    public Long getMacendTime() {
        return macendTime;
    }

    public void setMacendTime(Long macendTime) {
        this.macendTime = macendTime;
    }

    public int getMacType() {
        return macType;
    }

    public void setMacType(int macType) {
        this.macType = macType;
    }

    public Long getImsipreClearTime() {
        return imsipreClearTime;
    }

    public void setImsipreClearTime(Long imsipreClearTime) {
        this.imsipreClearTime = imsipreClearTime;
    }

    public Long getMacpreClearTime() {
        return macpreClearTime;
    }

    public void setMacpreClearTime(Long macpreClearTime) {
        this.macpreClearTime = macpreClearTime;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", mobileUserName='" + mobileUserName + '\'' +
                ", mobilePassword='" + mobilePassword + '\'' +
                ", imsiStartTime=" + imsiStartTime +
                ", imsiendTime=" + imsiendTime +
                ", initTime=" + initTime +
                ", ImsiType=" + ImsiType +
                ", macStartTime=" + macStartTime +
                ", macendTime=" + macendTime +
                ", macType=" + macType +
                ", imsipreClearTime=" + imsipreClearTime +
                ", macpreClearTime=" + macpreClearTime +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public String getMobilePort() {
        return mobilePort;
    }

    public void setMobilePort(String mobilePort) {
        this.mobilePort = mobilePort;
    }

    public String getImsiPort() {
        return imsiPort;
    }

    public void setImsiPort(String imsiPort) {
        this.imsiPort = imsiPort;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
