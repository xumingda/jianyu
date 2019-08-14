package com.lte.ui.activity;

/*
 *  Date:2018/5/25.
 *  Time:下午6:52.
 *  author:chenxiaojun
 */

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.communication.utils.LETLog;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lte.R;
import com.lte.data.StationInfo;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.adapter.StringAdapter;
import com.lte.ui.event.MessageEvent;
import com.lte.ui.event.RedireectEvent;
import com.lte.ui.listener.OnOperItemClickL;
import com.lte.ui.widget.ActionSheetDialog;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RedirectActivity extends BaseActivity implements View.OnClickListener {

    private MaterialSpinner lte_type;

    private AppCompatEditText band;

    private AppCompatEditText point_list;

    private AppCompatEditText et_imsi;

    private TextView add;

    private RecyclerView imsi_list;

    private Button open;

    private Button close;

    private TextView add_bbu;

    private TextView text_bbu;
    private List<String> mList;

    protected List<String> mLteTypelList = new ArrayList<>();
    private StringAdapter mAdapter;
    private TitleBar titleBar;
    private ActionSheetDialog dialog;

    private List<String> bbulist = new ArrayList<String>();

    private List<String> selectBbulist = new ArrayList<String>();
    private AtomicReference<String> localProgressFlag;

    private MUiHandler mUiHandler;

    private static class MUiHandler extends Handler {
        private static final String TAG = "UpdateStateHandler";

        private WeakReference<RedirectActivity> reference;


        MUiHandler(RedirectActivity redirectActivity) {
            reference = new WeakReference<>(redirectActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            StationInfo stationInfo = (StationInfo) msg.obj;
            if(msg.what == 1 ){
                if(msg.arg1 == 0){
                    ToastUtils.showToast(reference.get(),"BBU"+(stationInfo.getIp().substring(15, 16))+"重定向成功",Toast.LENGTH_LONG);
                }else if(msg.arg1 == 1){
                    ToastUtils.showToast(reference.get(),"BBU"+(stationInfo.getIp().substring(15, 16))+"消息解析失败",Toast.LENGTH_LONG);
                }else if(msg.arg1 == 2){
                    ToastUtils.showToast(reference.get(),"BBU"+(stationInfo.getIp().substring(15, 16))+"语法检查不通过",Toast.LENGTH_LONG);
                }else if(msg.arg1 == 3){
                    ToastUtils.showToast(reference.get(),"BBU"+(stationInfo.getIp().substring(15, 16))+"软件错误",Toast.LENGTH_LONG);
                }else if(msg.arg1 == 4){
                    ToastUtils.showToast(reference.get(),"BBU"+(stationInfo.getIp().substring(15, 16))+"重定向4G时，BBU未配置工作频点",Toast.LENGTH_LONG);
                }else if(msg.arg1 == 5){
                    ToastUtils.showToast(reference.get(),"BBU"+(stationInfo.getIp().substring(15, 16))+"重定向4G时，频点与工作频带重复",Toast.LENGTH_LONG);
                }
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redirect);
        init();
    }

    private void init() {

        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(R.string.redirect);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lte_type = (MaterialSpinner) findViewById(R.id.lte_type);

        band = (AppCompatEditText) findViewById(R.id.band);

        point_list = (AppCompatEditText) findViewById(R.id.point_list);

        et_imsi = (AppCompatEditText) findViewById(R.id.et_imsi);

        add = (TextView) findViewById(R.id.add);

        imsi_list = (RecyclerView) findViewById(R.id.imsi_list);

        open = (Button) findViewById(R.id.open);

        close = (Button) findViewById(R.id.close);

        add_bbu = (TextView) findViewById(R.id.add_bbu);

        text_bbu = (TextView) findViewById(R.id.text_bbu);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mList = new ArrayList<>();

        mAdapter = new StringAdapter(R.layout.item_string1, mList);
        imsi_list.setLayoutManager(linearLayoutManager);
        imsi_list.setAdapter(mAdapter);

        open.setOnClickListener(this);

        close.setOnClickListener(this);

        add.setOnClickListener(this);

        add_bbu.setOnClickListener(this);

        mLteTypelList.add("LTE");
        mLteTypelList.add("GERNA-GSM");
        mLteTypelList.add("UTRA-FDD");
        mLteTypelList.add("UTRA-TDD");
        mLteTypelList.add("CDMA2000-HRPD");
        mLteTypelList.add("CDMA2000-1XRTT");

        lte_type.setItems(mLteTypelList);

        EventBus.getDefault().register(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                localProgressFlag = new AtomicReference<>();
                localProgressFlag.set(DialogManager.showProgressDialog(this, "正在打开重定向，请稍候..",true));
                setOpenRedirect(true);
                break;
            case R.id.close:
                localProgressFlag = new AtomicReference<>();
                localProgressFlag.set(DialogManager.showProgressDialog(this, "正在关闭重定向，请稍候..",true));
                setOpenRedirect(false);
                break;
            case R.id.add:
                if (TextUtils.isEmpty(et_imsi.getText())) {
                    return;
                }
                mAdapter.addData(et_imsi.getText().toString());
                break;
            case R.id.add_bbu:
                ShowDialog();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onSuccess(RedireectEvent event) {
        DialogManager.closeDialog(localProgressFlag.get());
        if(event != null){
            StationInfo stationInfo = event.getStationInfo();
            if( stationInfo != null && stationInfo.getIp() != null && stationInfo.getIp().length() > 15){
                LETLog.d("redirectData:"+event.getStationInfo());
                Message message = Message.obtain();
                message.arg1 = event.getCode();
                message.obj = stationInfo;
                mUiHandler.sendMessage(message);

            }
        }
    }

    private void setOpenRedirect(boolean isOpen) {
        if(TextUtils.isEmpty(text_bbu.getText().toString())){
            ToastUtils.showToast(this,getString(R.string.add_bbu_tip),Toast.LENGTH_LONG);
            return;
        }
        if(isOpen){
            byte[] openTag = new byte[]{29, 0, 1, 1};
            byte[] typeTag = new byte[]{25, 0, 1, (byte) lte_type.getSelectedIndex()};

            int band1 = 0;
            if (!TextUtils.isEmpty(band.getText())) {
                try {
                    band1 = Integer.parseInt(band.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.add_number), Toast.LENGTH_LONG).show();
                }
            }
            byte[] bandTag = new byte[]{21, 0, 1, (byte) band1};
            ArrayList<Integer> list = new ArrayList<>();
            if (!TextUtils.isEmpty(point_list.getText())) {
                Log.d("TAG",point_list.getText().toString() +" --" +point_list.getText().toString().contains(","));
                if (point_list.getText().toString().contains(",")) {
                    String[] strings = point_list.getText().toString().split(",");
                    for (String string : strings) {
                        Log.d("TAG",string );
                        if (string.length() > 0) {
                            try {
                                list.add(Integer.parseInt(string));
                            } catch (Exception e) {
                                Toast.makeText(this, getString(R.string.add_number), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else {
                    try {
                        list.add(Integer.parseInt(point_list.getText().toString()));
                    } catch (Exception e) {
                        Toast.makeText(this, getString(R.string.add_number), Toast.LENGTH_LONG).show();
                    }
                }
            }
            byte[] pilot_numberTag = new byte[]{0x07, 0x00, 0x01, (byte) list.size()};

            byte[] pilot_listTag = new byte[0];
            if (list.size() != 0) {
                pilot_listTag = new byte[list.size() * 2 + 3];
                pilot_listTag[0] = 24;
                pilot_listTag[1] = 0x00;
                pilot_listTag[2] = (byte) (list.size() * 2);
                for (int i = 0; i < list.size(); i++) {
                    pilot_listTag[2 * i + 3] = (byte) ((list.get(i) >> 8) & 0xFF);
                    pilot_listTag[2 * i + 4] = (byte) ((list.get(i)) & 0xFF);
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String imsi : mAdapter.getData()) {
                stringBuilder.append(imsi);
            }
            byte[] bytes = AppUtils.hexStr2Bytes(stringBuilder.toString());
            byte[] bytes1 = new byte[]{39, (byte) ((bytes.length >> 8) & 0xFF), (byte) (bytes.length & 0xFF)};
            byte[] imsiTag = new byte[bytes.length + bytes1.length];
            System.arraycopy(bytes1, 0, imsiTag, 0, bytes1.length);
            System.arraycopy(bytes, 0, imsiTag, bytes1.length, bytes.length);
            byte[] data = new byte[openTag.length + typeTag.length + bandTag.length + pilot_numberTag.length +
                    pilot_listTag.length + imsiTag.length];
            System.arraycopy(openTag, 0, data, 0, openTag.length);
            System.arraycopy(typeTag, 0, data, 0 + openTag.length, typeTag.length);
            System.arraycopy(bandTag, 0, data, 0 + openTag.length + typeTag.length, bandTag.length);
            System.arraycopy(pilot_numberTag, 0, data, 0 + openTag.length + typeTag.length + bandTag.length, pilot_numberTag.length);
            System.arraycopy(pilot_listTag, 0, data, 0 + openTag.length + typeTag.length + bandTag.length + pilot_numberTag.length, pilot_listTag.length);
            System.arraycopy(imsiTag, 0, data, 0 + openTag.length + typeTag.length + bandTag.length + pilot_numberTag.length + pilot_listTag.length, imsiTag.length);
            for (StationInfo stationInfo : App.get().getOnLineList()) {
                if (stationInfo.getType() == 4) {
                    stationInfo.setRedirectCmd(null);
                }
            }
            for (String s : selectBbulist) {
                for (StationInfo stationInfo : App.get().getOnLineList()) {
                    if (stationInfo.getType() == 4) {
                        if (stationInfo.getIp() != null && stationInfo.getIp().length() > 15) {
                            if(TextUtils.equals(s,stationInfo.getIp().substring(15, 16))){
                                stationInfo.setRedirectCmd(data);
                            }
                        }
                    }
                }
            }
        }else {
            byte[] openTag = new byte[]{29, 0, 1, 0};
            StringBuilder stringBuilder = new StringBuilder();
            for (String imsi : mAdapter.getData()) {
                stringBuilder.append(imsi);
            }
            byte[] bytes = AppUtils.hexStr2Bytes(stringBuilder.toString());
            byte[] bytes1 = new byte[]{39, (byte) ((bytes.length >> 8) & 0xFF), (byte) (bytes.length & 0xFF)};
            byte[] imsiTag = new byte[bytes.length + bytes1.length];
            System.arraycopy(bytes1, 0, imsiTag, 0, bytes1.length);
            System.arraycopy(bytes, 0, imsiTag, bytes1.length, bytes.length);
            byte[] data = new byte[openTag.length  + imsiTag.length];
            System.arraycopy(openTag, 0, data, 0, openTag.length);
            System.arraycopy(imsiTag, 0, data, 0 + openTag.length, imsiTag.length);
            for (StationInfo stationInfo : App.get().getOnLineList()) {
                if (stationInfo.getType() == 4) {
                    stationInfo.setRedirectCmd(data);
                }
            }
        }

        TcpManager.getInstance().setStartRedirect();
    }
    List<String> list = new ArrayList<>();
    private void ShowDialog() {
        list.clear();
        for (StationInfo stationInfo : App.get().getOnLineList()) {
            if (stationInfo.getType() == 4) {
                if (stationInfo.getIp() != null && stationInfo.getIp().length() > 15) {
                    list.add("BBU" + stationInfo.getIp().substring(15, 16));
                    bbulist.add(stationInfo.getIp().substring(15, 16));
                }
            }
        }
        String[] contents = new String[list.size()];
        for(int i = 0;i<list.size();i++){
            contents[i] = list.get(i);
        }
        dialog = new ActionSheetDialog(this, //
                contents, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialog.isTitleShow(false)
                    .lvBgColor(getColor(R.color.white))
                    .itemTextColor(getColor(R.color.black))
                    .layoutAnimation(null);
        } else {
            dialog.isTitleShow(false)
                    .lvBgColor(getResources().getColor(R.color.white))
                    .itemTextColor(getResources().getColor(R.color.black))
                    .layoutAnimation(null);
        }
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                StringBuilder stringBuilder = new StringBuilder();
                if (!TextUtils.isEmpty(text_bbu.getText())) {
                    stringBuilder.append(text_bbu.getText());
                }
                if (!TextUtils.isEmpty(stringBuilder.toString())&&stringBuilder.toString().contains(list.get(position))) {
                    ToastUtils.showToast(RedirectActivity.this, getString(R.string.bbu_is_add), Toast.LENGTH_LONG);
                    return;
                }
                stringBuilder.append((String) list.get(position));
                text_bbu.setText(" "+stringBuilder.toString());
                selectBbulist.add(bbulist.get(position));
            }
        });
        dialog.show();
    }
}
