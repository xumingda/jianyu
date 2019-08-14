package com.lte.ui.fragment;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.communication.utils.LETLog;
import com.communication.volley.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.ImsiData;
import com.lte.data.table.BlackListTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.ui.activity.WhiteListActivity;
import com.lte.ui.adapter.DataReportOneAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.widget.menu.MenuAttribute;
import com.lte.ui.widget.menu.MenuItemView;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.DateUtils;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ThreadUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class DataReportOneFragment extends SupportFragment implements DataReportOneAdapter.CheckListener {


    private static final int UPGRADE = 1;
    private static final int START_SEND = 1;
    private static final int GET_MOBILE = 2;
    private static final int READ_DATA = 3;
    private static final int PLAY_SOUND = 4;
    private RecyclerView imsi_list;
    private ArrayList<ImsiData> mList = new ArrayList<>();
    private ArrayList<ImsiData> mWriteList = new ArrayList<>();
    private ArrayList<ImsiData> mSendList = new ArrayList<>();
    private ArrayList<ImsiData> mWriteListRemove = new ArrayList<>();
    private DataReportOneAdapter mAdapter;

    private Vibrator vibrator;

    private int loadId;

    private HttpCallback callBack = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + "imsi ：上传 " + jsonObject.toString());
            if (jsonObject.toString().equals("0")) {
                synchronized (mList) {
                    if (mList.size() > 0) {
                        mList.removeAll(mSendList);
                        mSendList.clear();
                    }
                }

            }
        }
        @Override
        public void onFailed(Exception errorMsg) {

        }
    };

    public static DataReportOneFragment newInstance() {
        DataReportOneFragment fragment = new DataReportOneFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment :", "onDetach");
    }

    private long writeTime = 0L;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SEND:
                    synchronized (mList) {
                        if (mSendList.size() > 0) {
                            HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber), dataParams(mSendList), callBack);
                            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + "imsi ：上传 url" + Constants.getDataUrl(App.get().DevNumber) + "dataParams :" +
                                    dataParams(mSendList));
                        } else {
                            if (mList.size() > 0) {
                                for (ImsiData imsiData : mList) {
//                                    if (!TextUtils.isEmpty(imsiData.getMobile())) {//已解析手机号完成的数据提交服务器后台
                                        imsiData.setReport(true);
                                        mSendList.add(imsiData);
//                                    }
                                }
                                if (mSendList.size() != 0) {
                                    if(App.get().DevNumber != null){
                                        HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber), dataParams(mSendList), callBack);
                                        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + "imsi ：上传 url" + Constants.getDataUrl(App.get().DevNumber) + "dataParams :" +
                                                dataParams(mSendList));
                                    }
                                }
                            }
                        }
                        sendEmptyMessageDelayed(START_SEND, 3 * 1000L);
                        break;
                    }
                case GET_MOBILE:
//                    getWriteNumber();
                    if (writeNumber >= 5) {
                        handler.sendEmptyMessageDelayed(READ_DATA, 2000);//当前待查询次数大于5，查询结果
                    } else {
                        if (System.currentTimeMillis() - writeTime > 20 * 1000L && writeTime != 0L) {
                            if (writeNumber > 0) {
                                writeTime = System.currentTimeMillis();
                                handler.sendEmptyMessageDelayed(READ_DATA, 200);//当前距离最后一次写数据大于20秒，查询结果
                            }else {
                                if (mWriteList.size() > 0) {
                                    if (!TextUtils.isEmpty(App.get().apikey) && App.get().apikeyCanUse) {
                                        //查询手机号数据集合有数据，即开始提交数据查询，没两秒检查一次
                                        ImsiData imsiData = mWriteList.get(0);
                                        imsiData.isWrite = true;
                                        writeData("101" + imsiData.getImsi());
                                        writeTime = System.currentTimeMillis();//记录最后一次写数据的时间
                                    }
                                }
                            }
                        } else {
                            if (mWriteList.size() > 0) {
                                if (!TextUtils.isEmpty(App.get().apikey) && App.get().apikeyCanUse) {
                                    //查询手机号数据集合有数据，即开始提交数据查询，每三秒秒检查一次
                                    ImsiData imsiData = mWriteList.get(0);
                                    imsiData.isWrite = true;
                                    writeData("101" + imsiData.getImsi());
                                    writeTime = System.currentTimeMillis();//记录最后一次写数据的时间
                                }
                            }
                            Log.d("http", "GET_MOBILE :" + mWriteList.size());
                        }
                    }
                    sendEmptyMessageDelayed(GET_MOBILE, 3 * 1000L);
                    break;
                case READ_DATA:
                    readData();
                    break;
                case PLAY_SOUND:
                    boolean isRingOn = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "ringOn", true);
                    if(isRingOn && soundPool!=null){
                        soundPool.play(loadId, 1, 1, 1, 0, 1f);//参数：1、M取值 2、当前音量 3、最大音量 4、优先级 5、重播次数 6、播放速度
                    }
                    boolean isVibrate = SharedPreferencesUtil.getBooleanConfig(getActivity(), "sniffer", "isVibrate", true);
                    if(vibrator!=null && isVibrate){
                        vibrator.vibrate(1000);//振动1秒
                    }
                    break;
            }

        }
    };
    //读取数据解析完成的手机号
    private void readData() {
        if (!TextUtils.isEmpty(App.get().apikey) && App.get().apikeyCanUse) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", App.get().apikey);
            HttpRequestHelper.putHttpRequest(Request.Method.DELETE, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "output"), headers, callBack2);

        }
    }
    //提交数据库解析
    private void writeData(String dtag) {
        LETLog.d("write data :" + dtag);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", App.get().apikey);
        headers.put("dtag", Base64.encodeToString(dtag.getBytes(), Base64.DEFAULT));
        HttpRequestHelper.putHttpRequest(Request.Method.POST, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "input"), headers, callBack1);
    }

    private int writeNumber;

    private HttpCallback callBack1 = new HttpCallback() {
        @Override
        public void onSuccess(final JsonElement jsonObject) {
            ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
                @Override
                public void run() {
                    LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
                    try {
                        JsonObject asJsonObject = jsonObject.getAsJsonObject();
                        if (asJsonObject.get("responseCode") != null) {
                            if (asJsonObject.get("responseCode").getAsInt() == 200) {
                                    if (mWriteList.size() > 0) {
                                        if (mWriteList.get(0).isWrite) {
                                            //提交成功后，清除已提交数据
                                            mWriteListRemove.add(mWriteList.remove(0));
                                            ++writeNumber;
                                        }

                                    }
//                                handler.removeMessages(READ_DATA);//提交成功后，延时20秒查询结果
//                                handler.sendEmptyMessageDelayed(READ_DATA, 20 * 1000);//提交成功后，延时20秒查询结果
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private HttpCallback callBack2 = new HttpCallback() {
        @Override
        public void onSuccess(final JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JsonArray asJsonArray = jsonObject.getAsJsonArray();
                        for (JsonElement jsonElement : asJsonArray) {
                            JsonObject asJsonObject = jsonElement.getAsJsonObject();
                            if (asJsonObject.get("rcontent") != null) {
                                String rcontent = new String(Base64.decode(asJsonObject.get("rcontent").getAsString(), Base64.DEFAULT));
                                String trim = rcontent.trim();
                                String imsi = trim.substring(3, 18);
                                String mobile = trim.substring(22);
                                for (ImsiData imsiData : mList) {
                                    if (TextUtils.equals(imsi, imsiData.getImsi())) {
                                        imsiData.setMobile(mobile);
                                        App.get().insert(imsiData);
                                        Log.d("http", "callBack" + mWriteList.contains(imsiData));
                                        if (mWriteList.contains(imsiData)) {
                                            mWriteList.remove(imsiData);//成功获取手机号码，移除集合
                                        }
                                        synchronized (mWriteListRemove){
                                            if (mWriteListRemove.contains(imsiData)) {
                                                mWriteListRemove.remove(imsiData);//成功获取手机号码，移除集合
                                            }
                                        }
                                    }
                                }
                                LETLog.d("read data :" + trim.substring(3, 18));
                                Log.d("http", "callBack" + rcontent + "----" + trim.substring(3, 18)
                                        + trim.substring(22));
                            }

                        }
                        synchronized (mWriteListRemove) {
                            if (mWriteListRemove.size() > 0) {
                                mWriteList.addAll(0,mWriteListRemove);//已经提交查询过的集合，如果还有没有成功获取号码的，添加到查询集合继续循环查询，同时清空已提交集合
                                mWriteListRemove.clear();
                            }
                        }
                        writeNumber = writeNumber - asJsonArray.size();
                        handler.sendEmptyMessage(START_SEND);//开始上报数据
//                        handler.removeMessages(READ_DATA);//清除读取数据队列。查询手机号服务器又能重新提交数据，提交成功20秒后读取数据，如此循环
                    } catch (Exception ignored) {

                    }
                    try {
                        JsonObject asJsonObject = jsonObject.getAsJsonObject();
                        LETLog.d(" jsonObject : + " + (asJsonObject.get("responseCode") != null));
                        if (asJsonObject.get("responseCode") != null) {
                            if (asJsonObject.get("responseCode").getAsInt() == 404) {
                                //查询库内没有结果，重新提交号码。
                                writeTime = System.currentTimeMillis();
                                writeNumber = 0;
                                synchronized (mWriteListRemove) {
                                    if (mWriteListRemove.size() > 0) {
                                        mWriteList.addAll(0,mWriteListRemove);//已经提交查询过的集合，如果还有没有成功获取号码的，添加到查询集合继续循环查询，同时清空已提交集合
                                        mWriteListRemove.clear();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_report_one_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        EventBus.getDefault().register(this);
        imsi_list = (RecyclerView) view.findViewById(R.id.imsi_list);

        if (App.get().userInfo.getImsiType() == 2) {
            mAdapter = new DataReportOneAdapter(_mActivity, DataManager.getInstance().findImsiData(App.get().userInfo.getImsipreClearTime()), this, imsi_list);
        } else {
            mAdapter = new DataReportOneAdapter(_mActivity, DataManager.getInstance().findImsiData(App.get().userInfo.getImsiStartTime(), App.get().userInfo.getImsiendTime()), this, imsi_list);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);

        imsi_list.setLayoutManager(linearLayoutManager);

        imsi_list.setAdapter(mAdapter);

        loadId = soundPool.load(_mActivity, R.raw.beep, 1);
        // 震动效果的系统服务
        vibrator = (Vibrator)_mActivity.getSystemService(Activity.VIBRATOR_SERVICE);
        if(App.get().DevNumber != null){
            handler.sendEmptyMessage(START_SEND);
        }
    }

    private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
    public void upgrade(int type) {
        if (mAdapter != null) {
            if (type == 2) {
                mAdapter.upDataList(DataManager.getInstance().findImsiData(App.get().userInfo.getImsipreClearTime()));
            } else {
                mAdapter.upDataList(DataManager.getInstance().findImsiData(App.get().userInfo.getImsiStartTime(), App.get().userInfo.getImsiendTime()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * start other BrotherFragment
     */
    @Subscribe
    public void onUpdate(MessageEvent event) {
        if (event != null && event.imsiData != null) {
            for (BlackListTable blackListTable : App.get().blackListTables) {
                if (TextUtils.equals(blackListTable.getImsi(),event.imsiData.getImsi())){
                    event.imsiData.setIsblackList(1);
                    DataManager.getInstance().createOrUpdateImsi(event.imsiData);
                    handler.sendEmptyMessage(PLAY_SOUND);
                }
            }
            if(App.get().DevNumber != null){
                mList.add(event.imsiData);//存储所有上报的数据，相当于一个队列，上传服务器后台后，清除上传完成的数据
            }
//            mWriteList.add(event.imsiData);//存储所有查询手机号码的数据，查询数据提交成功后，清除数据，得到查询结果对也清除一样imsi的数据。
        }
    }

    public static JsonElement dataParams(ArrayList<ImsiData> mList) {
        JsonArray cmdListArray = new JsonArray();
        for (ImsiData imsiData : mList) {
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("cellNumber", "");
            jsonObject1.addProperty("imei", imsiData.getImei());
            jsonObject1.addProperty("imsi", imsiData.getImsi());
            jsonObject1.addProperty("tmsi", "");
            jsonObject1.addProperty("uptime", (imsiData.getTime() / 1000L));
            cmdListArray.add(jsonObject1);
        }
        return cmdListArray;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view, final ImsiDataTable imsiData) {
        if(imsiData.getIsBlackAndWhite() != 1){
            MenuItemView menu = new MenuItemView(_mActivity) {
                @Override
                protected MenuAttribute initAttribute() {
                    MenuAttribute attribute = new MenuAttribute();
                    attribute.imsiDataTable = imsiData;
                    attribute.type = 1;
                    return attribute;
                }
            };
            menu.showMenu(false, null);
        }else {
            MenuItemView menu = new MenuItemView(_mActivity) {
                @Override
                protected MenuAttribute initAttribute() {
                    MenuAttribute attribute = new MenuAttribute();
                    attribute.imsiDataTable = imsiData;
                    attribute.type = 4;
                    return attribute;
                }
            };
            menu.showMenu(false, null);
        }
    }

    @Override
    public void onLongClick(View view, ImsiDataTable imsiData) {

    }
}
