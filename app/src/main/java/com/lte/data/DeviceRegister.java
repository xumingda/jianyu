package com.lte.data;

import io.realm.annotations.PrimaryKey;

/**
 * Created by chenxiaojun on 2017/9/25.
 */

public class DeviceRegister {
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

    public DeviceRegister(){}

    public DeviceRegister(DeviceRegisterBuilder deviceRegisterBuilder) {
        super();
        this.id = deviceRegisterBuilder.id;
        this.devNumber = deviceRegisterBuilder.devNumber;
        this.devName = deviceRegisterBuilder.devName;
        this.typeModel = deviceRegisterBuilder.typeModel;
        this.devType = deviceRegisterBuilder.devType;
        this.devConformation = deviceRegisterBuilder.devConformation;
        this.phoneNumber = deviceRegisterBuilder.phoneNumber;
        this.height = deviceRegisterBuilder.height;
        this.longitude = deviceRegisterBuilder.longitude;
        this.latitude = deviceRegisterBuilder.latitude;
        this.mac = deviceRegisterBuilder.mac;
        this.devAddress = deviceRegisterBuilder.devAddress;
        this.timestamp = deviceRegisterBuilder.timestamp;
    }

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

    public static class DeviceRegisterBuilder {
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

        public DeviceRegisterBuilder id(Long id){
            this.id = id;
            return this;
        }
        public DeviceRegisterBuilder devNumber(String devNumber){
            this.devNumber = devNumber;
            return this;
        }
        public DeviceRegisterBuilder devName(String devName){
            this.devName = devName;
            return this;
        }
        public DeviceRegisterBuilder typeModel(String typeModel){
            this.typeModel = typeModel;
            return this;
        }
        public DeviceRegisterBuilder devType(int devType){
            this.devType = devType;
            return this;
        }
        public DeviceRegisterBuilder devConformation(int devConformation){
            this.devConformation = devConformation;
            return this;
        }
        public DeviceRegisterBuilder phoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }
        public DeviceRegisterBuilder height(float height){
            this.height = height;
            return this;
        }
        public DeviceRegisterBuilder longitude(float longitude){
            this.longitude = longitude;
            return this;
        }
        public DeviceRegisterBuilder latitude(float latitude){
            this.devType = devType;
            return this;
        }
        public DeviceRegisterBuilder mac(String mac){
            this.mac = mac;
            return this;
        }
        public DeviceRegisterBuilder devAddress(String devAddress){
            this.devAddress = devAddress;
            return this;
        }
        public DeviceRegisterBuilder timestamp(long timestamp){
            this.timestamp = timestamp;
            return this;
        }
        public DeviceRegister build() {
            return new DeviceRegister(this);
        }
    }
}
