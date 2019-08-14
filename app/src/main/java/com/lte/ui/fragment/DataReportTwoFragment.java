package com.lte.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.communication.utils.LETLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.MacData;
import com.lte.data.table.MacDataTable;
import com.lte.ui.adapter.DataReportTwoAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.utils.Constants;
import com.lte.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class DataReportTwoFragment extends SupportFragment implements DataReportTwoAdapter.CheckListener {


    private static final int UPGRADE = 1;
    private static final int START_SEND = 1;
    private RecyclerView mac_list;
    private ArrayList<MacData> mList = new ArrayList<>();
    private DataReportTwoAdapter mAdapter;
//    private HttpCallback callBack = new HttpCallback() {
//        @Override
//        public void onSuccess(JsonElement jsonObject) {
//            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis())+"imei ：上传 " + jsonObject.toString());
//            if(jsonObject.toString().equals("0")){
//                synchronized (mList) {
//                    if (mList.size() > 0) {
//                        if (mList.get(0).isReport()) {
//                            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis())+"imei ：remove " + mList.get(0));
//                            mList.remove(0);
//                        }
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onFailed(Exception errorMsg) {
//
//        }
//    };

    public static DataReportTwoFragment newInstance() {
        DataReportTwoFragment fragment = new DataReportTwoFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case START_SEND:
//                    synchronized (mList){
//                        if(mList.size() > 0){
//                            final MacData macData = mList.get(0);
//                            macData.setReport(true);
//                            HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber),dataParams(0,macData.getMac(),null,null
//                                    ,(macData.getTime())/1000),callBack);
//                            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis())+"imei ：上传 url"+Constants.getDataUrl(App.get().DevNumber) +"dataParams :"+
//                                    dataParams(0,macData.getMac(),null,null
//                                            ,macData.getTime()/1000));
//                        }
//                    }
//                    sendEmptyMessageDelayed(START_SEND,2000l);
//                    break;
//            }
//        }
//    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_report_two_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        EventBus.getDefault().register(this);
        mac_list = (RecyclerView) view.findViewById(R.id.mac_list);

        if(App.get().userInfo.getMacType() == 2) {
            mAdapter = new DataReportTwoAdapter(_mActivity,DataManager.getInstance().findNowImeiiData(App.get().userInfo.getMacpreClearTime()), this, mac_list);
        }else {
            mAdapter = new DataReportTwoAdapter(_mActivity,DataManager.getInstance().findNowImeiData(App.get().userInfo.getMacStartTime(),App.get().userInfo.getMacendTime()), this, mac_list);
        }

        mac_list.setLayoutManager(new LinearLayoutManager(_mActivity));

        mac_list.setAdapter(mAdapter);

//        handler.sendEmptyMessage(START_SEND);
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }
    /**
     * start other BrotherFragment
     */
    @Subscribe
    public void onUpdate(MessageEvent event){
//        handler.sendEmptyMessage(UPGRADE);
//        LETLog.d("imei ：上传 DevNumber == null"+(App.get().DevNumber != null));
//        if(event != null && event.macData != null && App.get().DevNumber != null){
//            mList.add(event.macData);
//            HttpRequestHelper.sendHttpRequest(Constants.getDataUrl(App.get().DevNumber),dataParams(0,event.macData.getMac(),null,null
//                    ,(event.imsiData.getTime())/1000),callBack);
//            LETLog.d("imei ：上传 url"+Constants.getDataUrl(App.get().DevNumber) +"dataParams :"+
//                    dataParams(0,event.macData.getMac(),null,null
//                            ,event.imsiData.getTime()));
//        }
    }
    public static JsonElement dataParams(int cellNumber, String imei, String imsi, String tmsi, long time) {
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
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment :","onDetach");
    }
    @Override
    public void onClick(View view, MacDataTable imsiData) {

    }

    public void upgrade(int type) {
        if(mAdapter != null){
            if(type == 2){
                mAdapter.upDataList(DataManager.getInstance().findNowImeiiData(App.get().userInfo.getMacpreClearTime()));
            }else {
                mAdapter.upDataList(DataManager.getInstance().findNowImeiData(App.get().userInfo.getMacStartTime(),App.get().userInfo.getMacendTime()));
            }
        }
    }
}
