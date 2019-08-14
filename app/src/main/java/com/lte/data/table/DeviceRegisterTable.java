package com.lte.data.table;

import com.lte.data.DeviceRegister;
import com.lte.data.ImsiData;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/25.
 */

public class DeviceRegisterTable extends RealmObject {
    @PrimaryKey
    private Long id = null;//@Id必须为Long

    private String devNumber;

    private String devName;

    private String typeModel;

    private int devType;

    private int devConformation;

    private String phoneNumber;

    private float height;

    private float longitude;

    private float latitude;

    private String mac;

    private String devAddress;

    private long timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevNumber() {
        return devNumber;
    }

    public void setDevNumber(String devNumber) {
        this.devNumber = devNumber;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getTypeModel() {
        return typeModel;
    }

    public void setTypeModel(String typeModel) {
        this.typeModel = typeModel;
    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public int getDevConformation() {
        return devConformation;
    }

    public void setDevConformation(int devConformation) {
        this.devConformation = devConformation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDevAddress() {
        return devAddress;
    }

    public void setDevAddress(String devAddress) {
        this.devAddress = devAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public DeviceRegister createDeviceRegister() {
        return new DeviceRegister.DeviceRegisterBuilder().
                id(this.id)
                .devNumber(this.devNumber)
                .devName(this.devName)
                .typeModel(this.typeModel)
                .devType(this.devType)
                .devConformation(this.devConformation)
                .phoneNumber(this.phoneNumber)
                .height(this.height)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .mac(this.mac)
                .devAddress(this.devAddress)
                .timestamp(timestamp)
                .build();
    }
}
