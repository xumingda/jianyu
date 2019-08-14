package com.lte.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.DeviceRegister;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.CommBottomDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by chenxiaojun on 2017/9/25.
 */

public class DeviceRegisterActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private List<DeviceRegister> mList;
    private DeviceRegister deviceRegister;

    private EditText devNumber;

    private EditText devName;

    private EditText typeModel;

    private EditText devType;

    private EditText devConformation;

    private EditText phoneNumber;

    private EditText height;

    private EditText longitude;

    private EditText latitude;

    private EditText mac;

    private EditText devAddress;

    private EditText timestamp;

    private String ssid;

    private AlertDialog alertDialog;

    private TextView upgrade, cancel;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            aMapLocation.getErrorCode();
            if (aMapLocation != null) {
                Log.d("aMapLocation",aMapLocation.getErrorCode() +"");
                aMapLocation.getAccuracy();//获取精度信息
                devAddress.setText(aMapLocation.getCity());  //地址
                latitude.setText(aMapLocation.getLatitude()+"");//获取纬度
                longitude.setText(aMapLocation.getLongitude()+"");//获取经度
                height.setText(aMapLocation.getAltitude()+"");//海拔高度信息
            }
        }
    };

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            Log.d("jsonObject",jsonObject.toString());
            if(jsonObject.toString().equals("0")){
//                Toast.makeText(DeviceRegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                App.get().DevNumber = deviceRegister.getDevNumber();
                DataManager.getInstance().createOrUpdateDeviceRegister(deviceRegister);
                if(isOnresume){
                    showCommSuccessDialog(R.string.register_success);
                }
            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private CommBottomDialog mCommBottomDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_register);
        initDialog();
        init();
        iview();
        initLocation();
    }

    private void initLocation() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }
    private boolean isOnresume;
    @Override
    protected void onResume() {
        super.onResume();
        isOnresume = true;
    }

    private void init() {
        EventBus.getDefault().register(this);
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        devNumber = (EditText) findViewById(R.id.DevNumber);
        devName = (EditText) findViewById(R.id.DevName);
        typeModel = (EditText) findViewById(R.id.typeModel);
        devType = (EditText) findViewById(R.id.devType);
        devConformation = (EditText) findViewById(R.id.devConformation);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        height = (EditText) findViewById(R.id.height);
        longitude = (EditText) findViewById(R.id.longitude);
        latitude = (EditText) findViewById(R.id.latitude);
        mac = (EditText) findViewById(R.id.mac);
        devAddress = (EditText) findViewById(R.id.devAddress);
        timestamp = (EditText) findViewById(R.id.timestamp);

        upgrade = (TextView) findViewById(R.id.upgrade);

        cancel = (TextView) findViewById(R.id.cancel);

        upgrade.setOnClickListener(this);

        cancel.setOnClickListener(this);

        titleBar.setTitle(R.string.register);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mList = DataManager.getInstance().finalDeviceRegister();


        if (mList.size() > 0) {
            deviceRegister = mList.get(0);
        }
        timestamp.setText((System.currentTimeMillis()/1000)+"");
        mac.setText(getNewMac());
        if (deviceRegister != null) {
            devNumber.setText(deviceRegister.getDevNumber());
            devName.setText(deviceRegister.getDevName());
            typeModel.setText(deviceRegister.getTypeModel());
            devConformation.setText(deviceRegister.getDevConformation() + "");
            phoneNumber.setText(deviceRegister.getPhoneNumber());
            height.setText(deviceRegister.getHeight() + "");
            longitude.setText(deviceRegister.getLongitude() + "");
            latitude.setText(deviceRegister.getLatitude() + "");
            devAddress.setText(deviceRegister.getDevAddress());
            devType.setText(deviceRegister.getDevType() + "");
        }else {
            devNumber.setText((System.currentTimeMillis()/1000)+"");
        }
        ssid = getConnectWifiSsid();
        if(ssid != null){
            alertDialog.show();
        }
    }
    private void initDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("当前连接wifi为："+getConnectWifiSsid() +",请确认当前wifi是否为设备热点wifi？（注意：确认为热点wifi，此wifi的SSID将作为设备标识，注册到服务器）");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                devNumber.setText(ssid);
                App.get().ssid = ssid;
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }
    private void iview() {
        mCommBottomDialog = new CommBottomDialog.Builder(this).create();
    }

    protected void showCommSuccessDialog(String msg) {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setSuccess(msg).show();

    }

    protected void showCommSuccessDialog(@StringRes int resId) {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setSuccess(resId).show();

    }

    protected void showCommErrorDialog(String msg) {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setError(msg).show();
    }
    protected void showCommErrorDialog(@StringRes int resId) {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setError(resId).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
        isOnresume = false;
    }

    protected void dismissDialog() {
        if (mCommBottomDialog != null)
            mCommBottomDialog.dismiss();
    }
    /**
     * 通过网络接口取
     * @return
     */
    private static String getNewMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    private String getConnectWifiSsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID().replace("\"", "");
    }
    public static JsonElement dataParams(int cellNumber,String imei,String imsi,String tmsi,long time) {
        JsonArray cmdListArray = new JsonArray();
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("cellNumber",cellNumber);
        jsonObject1.addProperty("imei",imei);
        jsonObject1.addProperty("imsi",imsi);
        jsonObject1.addProperty("tmsi",tmsi);
        jsonObject1.addProperty("uptime",time);
        cmdListArray.add(jsonObject1);
        return cmdListArray;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upgrade:
                boolean isUpgrade = false;
                String devNum = null;
                if(devNumber.getText()!= null && devNumber.getText().toString().length()>0){
                    devNum = devNumber.getText().toString();
                }
                String devNam = null;
                if(devName.getText()!= null && devName.getText().toString().length()>0){
                    devNam = devName.getText().toString();
                }
                String typeMod = null;
                if(typeModel.getText()!= null && typeModel.getText().toString().length()>0){
                    typeMod = typeModel.getText().toString();
                }
                int devtyp = 0;
                if(devType.getText()!= null && devType.getText().toString().length()>0){
                    devtyp = Integer.parseInt(devType.getText().toString());
                }
                int devCon = 0;
                if(devConformation.getText()!= null && devConformation.getText().toString().length()>0){
                    devCon = Integer.parseInt(devConformation.getText().toString());
                }
                String phone = null;
                if(phoneNumber.getText()!= null && phoneNumber.getText().toString().length()>0){
                    phone = phoneNumber.getText().toString();
                }
                float heigh = 0;
                if(height.getText()!= null && height.getText().toString().length()>0){
                    heigh = Float.parseFloat(height.getText().toString());
                }
                float longit = 0;
                if(longitude.getText()!= null && longitude.getText().toString().length()>0){
                    longit = Float.parseFloat(longitude.getText().toString());
                }
                float lat = 0;
                if(latitude.getText()!= null  && latitude.getText().toString().length()>0){
                    lat = Float.parseFloat(latitude.getText().toString());
                }
                String ma = null;
                if(mac.getText()!= null && mac.getText().toString().length()>0){
                    ma = mac.getText().toString();
                }
                String devA = null;
                if(devAddress.getText()!= null && devAddress.getText().toString().length()>0){
                    devA = devAddress.getText().toString();
                }
                long time = 0;
                if(timestamp.getText()!= null && timestamp.getText().toString().length()>0){
                    time = Long.parseLong(timestamp.getText().toString());
                }
                if(deviceRegister == null){
                    isUpgrade = true;
                    deviceRegister = new DeviceRegister();
                    deviceRegister.setId(1L);
                    deviceRegister.setDevNumber(devNum);
                    deviceRegister.setDevName(devNam);
                    deviceRegister.setDevAddress(devA);
                    deviceRegister.setMac(ma);
                    deviceRegister.setPhoneNumber(phone);
                    deviceRegister.setTimestamp(time);
                    deviceRegister.setTypeModel(typeMod);
                    deviceRegister.setDevType(devtyp);
                    deviceRegister.setDevConformation(devCon);
                    deviceRegister.setHeight(heigh);
                    deviceRegister.setLongitude(longit);
                    deviceRegister.setLatitude(lat);
                }else {
                    if(!TextUtils.equals(devNum,deviceRegister.getDevNumber())){
                        deviceRegister.setDevNumber(devNum);
                        isUpgrade = true;
                    }
                    if(!TextUtils.equals(devNam,deviceRegister.getDevName())){
                        deviceRegister.setDevName(devNam);
                        isUpgrade = true;
                    }
                    if(!TextUtils.equals(devA,deviceRegister.getDevAddress())){
                        deviceRegister.setDevAddress(devA);
                        isUpgrade = true;
                    }
//                    if(!TextUtils.equals(ma,deviceRegister.getMac())){
//                        deviceRegister.setMac(ma);
//                        isUpgrade = true;
//                    }
                    if(!TextUtils.equals(phone,deviceRegister.getPhoneNumber())){
                        deviceRegister.setPhoneNumber(phone);
                        isUpgrade = true;
                    }
//                    if(time != deviceRegister.getTimestamp()){
//                        deviceRegister.setTimestamp(time);
//                        isUpgrade = true;
//                    }
                    if(!TextUtils.equals(typeMod,deviceRegister.getTypeModel())){
                        deviceRegister.setTypeModel(typeMod);
                        isUpgrade = true;
                    }
                    if(devtyp != deviceRegister.getDevType()){
                        deviceRegister.setDevType(devtyp);
                        isUpgrade = true;
                    }
                    if(devCon != deviceRegister.getDevConformation()){
                        deviceRegister.setDevConformation(devCon);
                        isUpgrade = true;
                    }
                    if(heigh != deviceRegister.getHeight()){
                        deviceRegister.setHeight(heigh);
                        isUpgrade = true;
                    }
                    if(longit != deviceRegister.getLongitude()){
                        deviceRegister.setLongitude(longit);
                        isUpgrade = true;
                    }
                    if(lat != deviceRegister.getLatitude()){
                        deviceRegister.setLatitude(lat);
                        isUpgrade = true;
                    }
                }
//                if(isUpgrade){

                    JsonObject jsonObject = getParams(devNum,devNam,typeMod,devtyp,devCon,phone,heigh,longit,lat,ma,devA,time);
                    HttpRequestHelper.sendHttpRequest(Constants.httpDeviceIp,jsonObject,callback);
//                }
//                else {
//                    HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber),dataParams(0,null,"4600016836204262",null
//                            ,(System.currentTimeMillis()/1000)),callback);
//                }

                break;
            case R.id.cancel:
                this.finish();
                break;
        }
    }
    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent){
        if(outEvent.isOut()){
            this.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static JsonObject getParams(String devNumber, String devName, String typeModel, int devType
    , int devConformation, String phoneNumber, float height, float longitude, float latitude, String mac, String devAddress, long timestamp) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("devNumber", devNumber);
        jsonObject.addProperty("devName", devName);
        jsonObject.addProperty("typeModel", typeModel);
        jsonObject.addProperty("devType", devType);
        jsonObject.addProperty("devConformation", devConformation);
        jsonObject.addProperty("phoneNumber", phoneNumber);
        JsonObject jsonObject1 = new JsonObject() ;
        jsonObject1.addProperty("height",height);
        jsonObject1.addProperty("longitude",longitude);
        jsonObject1.addProperty("latitude",latitude);
        jsonObject.add("devPos",jsonObject1);
        jsonObject.addProperty("mac",mac);
        jsonObject.addProperty("devAddress",devAddress);
        jsonObject.addProperty("timestamp",timestamp);
        return jsonObject;
    }
}
