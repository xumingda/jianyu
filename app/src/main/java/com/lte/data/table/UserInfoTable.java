package com.lte.data.table;

import android.text.TextUtils;

import com.lte.data.UserInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/8/14.
 */

public class UserInfoTable extends RealmObject {
    @PrimaryKey
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

    private String sceneName;

    private Long imsipreClearTime;

    private Long macpreClearTime;

    private String url;
    private String queryUrl;

    private String imsiPort;

    private String mobilePort;

    private String deviceType;

    public UserInfoTable(Long id, String userName, String password, int type) {
        this.id = id;
        this.mobileUserName = userName;
        this.mobilePassword = password;
    }
    public UserInfoTable(){}
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
        if (obj instanceof UserInfoTable) {
            UserInfoTable userInfo = (UserInfoTable) obj;

            return TextUtils.equals(userInfo.getMobileUserName(),this.mobileUserName);
        } else {
            return false;
        }
    }
    public UserInfo createUserInfo(){
        return new UserInfo(id, userName,password,mobileUserName, mobilePassword,imsiStartTime,imsiendTime,initTime,ImsiType,
                macStartTime,macendTime,macType,imsipreClearTime,macpreClearTime,url,queryUrl,imsiPort,mobilePort,sceneName,deviceType);
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


    public void setMacpreClearTime(Long macpreClearTime) {
        this.macpreClearTime = macpreClearTime;
    }

    public void setImsipreClearTime(Long imsipreClearTime) {
        this.imsipreClearTime = imsipreClearTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public String getImsiPort() {
        return imsiPort;
    }

    public void setImsiPort(String imsiPort) {
        this.imsiPort = imsiPort;
    }

    public String getMobilePort() {
        return mobilePort;
    }

    public void setMobilePort(String mobilePort) {
        this.mobilePort = mobilePort;
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
