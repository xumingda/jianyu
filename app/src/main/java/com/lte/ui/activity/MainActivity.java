package com.lte.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;


import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.communication.utils.LETLog;
import com.google.gson.JsonElement;
import com.lte.R;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.fragment.DataProcessFragment;
import com.lte.ui.fragment.DataReportFragment;
import com.lte.ui.fragment.DeviceRegisterFragment;
import com.lte.ui.fragment.SetFragment;
import com.lte.ui.fragment.DeviceFragment;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.ui.listener.OnFragmentSelectListener;
import com.lte.ui.widget.BottomBar;
import com.lte.ui.widget.BottomBarTab;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.InetAddress;

import me.yokeyword.fragmentation.SupportFragment;

public class MainActivity extends BaseActivity implements OnFragmentSelectListener, OnBackPressedListener {

    private static final int FOUR = 3;
    private RecyclerView recyclerView;
    private LayoutInflater mInflater;

    private TitleBar titleBar;

    private int id;
    private BottomBar mBottomBar;

    private SupportFragment[] mFragments = new SupportFragment[4];

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(msg.what == 1){
//                getReadData();
//            }else if(msg.what==2){
//                getWriteData();
//            }
//        }
//    };

//    private void getWriteData() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", App.get().apikey);
//        HttpRequestHelper.putHttpRequest(Request.Method.DELETE,"https://45.252.63.167:1443/output", headers, callBack3);
//    }
//
//    private void getReadData() {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", App.get().apikey);
//        String dtag = "101460010123456789";
//        headers.put("dtag", Base64.encodeToString(dtag.getBytes(),Base64.DEFAULT));
//        HttpRequestHelper.putHttpRequest(Request.Method.POST,"https://45.252.63.167:1443/input", headers, callBack2);
//    }

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            Log.d("http","callBack"+jsonObject.toString());
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            if(jsonObject.toString().equals("-1")){
                showDialog();
            }else {
                App.get().DevNumber = deviceNum;
            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };

//    private HttpCallback callBack2 = new HttpCallback() {
//        @Override
//        public void onSuccess(JsonElement jsonObject) {
//            Log.d("http","callBack2"+jsonObject.toString());
//            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
//            try {
//                JsonObject asJsonObject = jsonObject.getAsJsonObject();
//                if(asJsonObject.get("responseCode") != null){
//                    if(asJsonObject.get("responseCode").getAsInt() == 200){
//                        mHandler.sendEmptyMessageDelayed(2,20*1000);
//                    }
//                }
//            }catch (Exception e){
//
//            }
//        }
//
//        @Override
//        public void onFailed(Exception errorMsg) {
//
//        }
//    };
//    private HttpCallback callBack3 = new HttpCallback() {
//        @Override
//        public void onSuccess(JsonElement jsonObject) {
//            Log.d("http","callBack3"+jsonObject.toString() );
//            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
//            try {
//                JsonArray asJsonArray = jsonObject.getAsJsonArray();
//                for (JsonElement jsonElement : asJsonArray) {
//                    JsonObject asJsonObject = jsonElement.getAsJsonObject();
//                    Log.d("http","callBack3"+(asJsonObject.get("rcontent") != null) );
//                    if(asJsonObject.get("rcontent") != null){
//                        String rcontent = new String(Base64.decode(asJsonObject.get("rcontent").getAsString(), Base64.DEFAULT));
//                        Log.d("http","callBack3"+rcontent);
//                    }
//                }
//            }catch (Exception e){
//
//            }
//        }
//
//        @Override
//        public void onFailed(Exception errorMsg) {
//
//        }
//    };
    private SweetAlertDialog mDialog;

    private void showDialog(){
        mDialog = new SweetAlertDialog.Builder(this)
                .setMessage(getString(R.string.devnumber_not_register))
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel)
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, DeviceRegisterActivity.class);
                        startActivity(intent);
                        mDialog.dismiss();
                    }
                }).create();
        mDialog.show();
    }
    public static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
                + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
    }
    public static int getWifiGateIP(Context context){
        if(context==null){
            return -1;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if(dhcpInfo==null){
            return  -1;
        }
        return dhcpInfo.gateway;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        EventBus.getDefault().register(this);
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);

        Log.d("tetstqqq",intToIp(getWifiGateIP(this)));
//        titleBar.addAction(new TitleBar.ImageAction(R.drawable.personal_icon_settings) {
//            @Override
//            public void performAction(View view) {
//                Intent intent = new Intent(MainActivity.this,SetActivity.class);
//                startActivity(intent);
//            }
//        });
//        titleBar.setRightVisible(false);
        SupportFragment firstFragment = findFragment(DeviceFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = DeviceFragment.newInstance();
            mFragments[SECOND] = DataReportFragment.newInstance();
            mFragments[THIRD] = DataProcessFragment.newInstance();
            mFragments[FOUR] = SetFragment.newInstance();
            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOUR]);
            titleBar.setTitle(getString(R.string.set));
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.findFragmentByTag自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findFragment(DataReportFragment.class);
            mFragments[THIRD] = findFragment(DataProcessFragment.class);
            mFragments[FOUR] = findFragment(SetFragment.class);
        }

        mBottomBar
                .addItem(new BottomBarTab(this, R.drawable.select_icon_three, getString(R.string.set)))
                .addItem(new BottomBarTab(this, R.drawable.select_icon_one, getString(R.string.data_report)))
                .addItem(new BottomBarTab(this, R.drawable.select_icon_two, getString(R.string.data_processing)))
        .addItem(new BottomBarTab(this, R.drawable.select_icon_four, getString(R.string.setting)));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
                switch (position) {
                    case FIRST:
                        titleBar.setTitle(getString(R.string.set));

//                        titleBar.setRightVisible(false);
                        break;
                    case SECOND:
                        titleBar.setTitle(getString(R.string.data_report));

//                        titleBar.setRightVisible(false);
                        break;
                    case THIRD:
                        titleBar.setTitle(getString(R.string.data_processing));
//                        titleBar.setRightVisible(true);
                        break;
                    case FOUR:
                        titleBar.setTitle(getString(R.string.setting));
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
            }
        });


    }

    private static InetAddress getBroadcastAddress(Context context) throws IOException {
        WifiManager myWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
        if (myDhcpInfo == null) {
            return null;
        }
        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }
    @Override
    protected void onResume() {
        super.onResume();
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
    private String deviceNum;
    private void sendRegisterState() {
        App.get().deviceId = AppUtils.getConnectWifiSsid();
        LETLog.d("查询是否注册 ：" + App.get().deviceId);
        if( App.get().deviceId != null && !TextUtils.equals( App.get().deviceId, "<unknown ssid>")
                && App.get().userInfo.getUrl() != null && App.get().userInfo.getImsiPort() != null){
            HttpRequestHelper.sendHttpRequest(Constants.getRegister( App.get().deviceId) ,callback);
            deviceNum =  App.get().deviceId;
        }
    }


    @Override
    public void onBack() {
        onBackPressedSupport();
    }


    @Override
    public void onSelect(int position) {
        switch (position) {
            case 1:
                start(new DeviceRegisterFragment());
                break;
        }
    }
}
