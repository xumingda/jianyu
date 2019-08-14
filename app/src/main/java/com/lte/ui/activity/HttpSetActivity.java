package com.lte.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.communication.utils.LETLog;
import com.communication.volley.Request;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.https.MobileQuery;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenxiaojun on 2017/12/5.
 */

public class HttpSetActivity extends BaseActivity implements View.OnClickListener {

    private TitleBar titleBar;

    private EditText et_noman_up_address;

    private EditText et_mobile_usename;

    private EditText et_mobile_usepassword;

    private EditText et_mobile_number;

    private EditText query_mobile_address;

    private EditText et_noman_up_port;

    private EditText query_mobile_port;

    private Button chang_use;

    private Button chang_pswd;

    private Button btn_save;
    private SweetAlertDialog mDialog;
    private boolean ischange;

    private Long time;

    private AtomicReference<String> localProgressFlag;
    private String deviceNum;

    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            Log.d("http","callBack"+jsonObject.toString());
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            if(jsonObject.toString().equals("-1")){
                showDialog2();
            }else {
                App.get().DevNumber = deviceNum;
            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private SweetAlertDialog mDialog2;

    private void showDialog2() {
        mDialog2 = new SweetAlertDialog.Builder(this)
                .setMessage(getString(R.string.devnumber_not_register))
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel)
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(HttpSetActivity.this, DeviceRegisterActivity.class);
                        startActivity(intent);
                        mDialog.dismiss();
                    }
                }).create();
        mDialog2.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.httpset_activity);
        init();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent) {
        if (outEvent.isOut()) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {

        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(getString(R.string.http_set));

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedSupport();
            }
        });

//        titleBar.addAction(new TitleBar.TextAction("手机号翻译") {
//            @Override
//            public void performAction(View view) {
//                Intent intent = new Intent(HttpSetActivity.this, QueryMobileActivity.class);
//                startActivity(intent);
//            }
//        });

        et_noman_up_address = (EditText) findViewById(R.id.et_noman_up_address);

        et_mobile_number = (EditText) findViewById(R.id.et_mobile_number);

        et_mobile_usename = (EditText) findViewById(R.id.et_mobile_usename);

        et_mobile_usepassword = (EditText) findViewById(R.id.et_mobile_usepassword);

        query_mobile_address = (EditText) findViewById(R.id.query_mobile_address);

        et_noman_up_port = (EditText) findViewById(R.id.et_noman_up_port);

        query_mobile_port = (EditText) findViewById(R.id.query_mobile_port);

        chang_use = (Button) findViewById(R.id.chang_use);

        chang_pswd = (Button) findViewById(R.id.chang_pswd);

        btn_save = (Button) findViewById(R.id.btn_save);

        et_noman_up_address.setText(App.get().userInfo.getUrl());

        et_mobile_usename.setText(App.get().userInfo.getMobileUserName());

        et_mobile_usepassword.setText(App.get().userInfo.getMobilePassword());

        et_mobile_number.setText(App.get().apiNumber + "");

        query_mobile_address.setText(App.get().userInfo.getQueryUrl());

        et_noman_up_port.setText(App.get().userInfo.getImsiPort());

        query_mobile_port.setText(App.get().userInfo.getMobilePort());

        chang_use.setOnClickListener(this);

        chang_pswd.setOnClickListener(this);

        if(App.get().apikeyCanUse){
            getNumber();
        }else {
            if(App.get().userInfo.getMobileUserName() != null && App.get().userInfo.getMobilePassword() != null){
//                getApiKey();
                time = System.currentTimeMillis();
                localProgressFlag = new AtomicReference<>();
                localProgressFlag.set(DialogManager.showProgressDialog(HttpSetActivity.this, "令牌获取中，请稍候..",true));
                mHandler.sendEmptyMessageDelayed(5,3*1000L);
            }
        }
        btn_save.setOnClickListener(this);
    }

    private void getNumber() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", App.get().apikey);
        HttpRequestHelper.putHttpRequest(Request.Method.GET, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "remain"), headers, callBack);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_save) {
            boolean isChange = false;
            boolean isSave = false;
            if (!TextUtils.equals(et_noman_up_address.getText().toString(), App.get().userInfo.getUrl())) {
                if(App.get().userInfo.getUrl() == null){
                    isSave = true;
                }
                App.get().userInfo.setUrl(et_noman_up_address.getText().toString());
                DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                isChange = true;
            }
            if (!TextUtils.equals(query_mobile_address.getText().toString(), App.get().userInfo.getQueryUrl())) {
                App.get().userInfo.setQueryUrl(query_mobile_address.getText().toString());
                DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                isChange = true;
            }
            if (!TextUtils.equals(et_noman_up_port.getText().toString(), App.get().userInfo.getImsiPort())) {
                if(App.get().userInfo.getImsiPort() == null){
                    isSave = true;
                }
                App.get().userInfo.setImsiPort(et_noman_up_port.getText().toString());
                DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                isChange = true;
            }
            if (!TextUtils.equals(query_mobile_port.getText().toString(), App.get().userInfo.getMobilePort())) {
                App.get().userInfo.setMobilePort(query_mobile_port.getText().toString());
                DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                isChange = true;
            }
            if (isChange) {
                CommonToast.show(App.get(), getString(R.string.save_success));
            }
            if(isSave){
                sendRegisterState();
            }
        } else if (v == chang_use) {
            if (!TextUtils.equals(query_mobile_port.getText().toString(), App.get().userInfo.getMobilePort())) {
                App.get().userInfo.setMobilePort(query_mobile_port.getText().toString());
                DataManager.getInstance().crateOrUpdate(App.get().userInfo);
            }
            showDialog(et_mobile_usename.getText().toString());
        } else if (v == chang_pswd) {
            showDialog1(et_mobile_usename.getText().toString());
        }
    }
    private void sendRegisterState() {
        App.get().deviceId = AppUtils.getConnectWifiSsid();
        LETLog.d("查询是否注册 ：" + App.get().deviceId);
        if( App.get().deviceId != null && !TextUtils.equals( App.get().deviceId, "<unknown ssid>")
                && App.get().userInfo.getUrl() != null && App.get().userInfo.getImsiPort() != null){
            HttpRequestHelper.sendHttpRequest(Constants.getRegister( App.get().deviceId) ,callback);
            deviceNum =  App.get().deviceId;
        }
    }
    private void showDialog1(final String use) {
        mDialog = new SweetAlertDialog.Builder(this)

                .setMessage(getString(R.string.change_password_tip))
                .setHasTwoBtn(true)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                        et_mobile_usepassword.setText(App.get().userInfo.getMobilePassword());
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if (!compileExChar(et_psw.getText().toString())
                                && checkInput(et_psw.getText().toString())) {
                            changePassWord(et_psw.getText().toString());
                        }
                    }
                })
                .create();
        mDialog.addContentView(R.layout.dialog);
        et_use = (EditText) mDialog.findView(R.id.et_user_name);
        et_psw = (EditText) mDialog.findView(R.id.et_password);
        et_use.setText(use);
        et_use.setFocusable(false);
        et_use.setFocusableInTouchMode(false);
        et_psw.setHint(getString(R.string.psw_tips));
        mDialog.show();
    }

    private boolean compileExChar(String str) {
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            Toast.makeText(HttpSetActivity.this, "不允许输入特殊符号！", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public boolean checkInput(String str) {
        int num = 0;
        num = Pattern.compile("\\d").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[a-z]").matcher(str).find() ? num + 1 : num;
        num = Pattern.compile("[A-Z]").matcher(str).find() ? num + 1 : num;
        if (num >= 3) {
            return true;
        }
        Toast.makeText(HttpSetActivity.this, "必须且只能包含大小写和数字！", Toast.LENGTH_LONG).show();
        return false;
    }

    boolean isChangPsw = false;

    private void changePassWord(String passWord) {
        this.passWord = passWord;
        isChangPsw = true;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", App.get().apikey);
        headers.put("oldone", App.get().userInfo.getMobilePassword());
        headers.put("newone", passWord);
        HttpRequestHelper.putHttpRequest(Request.Method.PUT, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "passwd"), headers, callBack);

    }

    String useName;
    String passWord;
    EditText et_use;
    EditText et_psw;

    private void showDialog(final String use) {

        mDialog = new SweetAlertDialog.Builder(this)
                .setMessage(getString(R.string.change_use_tip) + ":" + use)
                .setHasTwoBtn(true)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        ischange = true;
                        App.get().apikey = null;
                        App.get().apikeyCanUse = false;
                        useName = et_use.getText().toString();
                        passWord = et_psw.getText().toString();
                        getApiKey();
                        mHandler.sendEmptyMessageDelayed(3,60*1000l);
                        localProgressFlag = new AtomicReference<>();
                        localProgressFlag.set(DialogManager.showProgressDialog(HttpSetActivity.this, "切换中，请稍候..",true));
                    }
                })
                .create();
        mDialog.addContentView(R.layout.dialog);
        et_use = (EditText) mDialog.findView(R.id.et_user_name);
        et_psw = (EditText) mDialog.findView(R.id.et_password);
        et_use.setHint(getString(R.string.use_tips));
        et_psw.setHint(getString(R.string.psw_tips));
        mDialog.show();
    }

    private void getApiKey() {
        MobileQuery.getInstance().getApiKey(callBack,useName,passWord);
    }

    private HttpCallback callBack = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            try {
                JsonObject asJsonObject = jsonObject.getAsJsonObject();
                if (asJsonObject.get("Authorization") != null) {
                    App.get().apikey = asJsonObject.get("Authorization").getAsString();
                    mHandler.sendEmptyMessageDelayed(1, 20 * 1000);
                } else if (asJsonObject.get("remaintimes") != null) {
                    App.get().apiNumber = asJsonObject.get("remaintimes").getAsInt();
                    et_mobile_number.setText(App.get().apiNumber + "");
                    mHandler.sendEmptyMessage(2);
                } else if (asJsonObject.get("responseCode") != null) {
                    if (asJsonObject.get("responseCode").getAsInt() == 200) {
                        if (isChangPsw) {
                            mHandler.sendEmptyMessage(4);
                        }
                    } else if (asJsonObject.get("responseCode").getAsInt() == 401) {
                        if (isChangPsw || ischange) {
                            mHandler.sendEmptyMessage(3);
                        }else {
                            getApiKey();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                App.get().apikeyCanUse = true;
                getNumber();
            } else if (msg.what == 2) {
                if (ischange) {
                    CommonToast.show(App.get(), getString(R.string.change_use_succes));
                    ischange = false;
                    App.get().apikeyCanUse = true;
                    App.get().userInfo.setMobileUserName(useName);
                    App.get().userInfo.setMobilePassword(passWord);
                    DataManager.getInstance().crateOrUpdate(App.get().userInfo);
                    et_mobile_usename.setText(useName);
                    et_mobile_usepassword.setText(passWord);
                    try {
                        DialogManager.closeDialog(localProgressFlag.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (App.get().apiNumber < 100 && App.get().apiNumber != 0) {
                    CommonToast.show(App.get(), getString(R.string.tips));
                }
                App.get().getApiKeyDay();
                removeCallbacksAndMessages(null);
            } else if (msg.what == 3) {
                isChangPsw = false;
                ischange = false;
                CommonToast.show(App.get(), getString(R.string.change_fail_tips));
                try {
                    DialogManager.closeDialog(localProgressFlag.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getApiKey();
            } else if (msg.what == 4) {
                isChangPsw = false;
                CommonToast.show(App.get(), getString(R.string.change_psw_tips));
                getApiKey();
            }else if(msg.what == 5){
                if(App.get().apikeyCanUse){
                    try {
                        DialogManager.closeDialog(localProgressFlag.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(App.get().apiNumber == 100){
                        getNumber();
                    }else {
                        et_mobile_number.setText(App.get().apiNumber + "");
                    }
                }else {
                    if(System.currentTimeMillis() - time >25*1000L){
                        try {
                            DialogManager.closeDialog(localProgressFlag.get());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CommonToast.show(App.get(), getString(R.string.network_error));
                    }else {
                        mHandler.sendEmptyMessageDelayed(5, 3 * 1000L);
                    }
                }
            }
        }
    };
}
