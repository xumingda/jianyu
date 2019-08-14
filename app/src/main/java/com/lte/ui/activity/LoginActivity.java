package com.lte.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.communication.utils.LETLog;
import com.communication.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.HttpResult;
import com.lte.data.UserInfo;
import com.lte.tcpserver.TcpServer;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.CommBottomDialog;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.Constants;
import com.lte.utils.CrcUtil;
import com.lte.utils.DateUtils;
import com.lte.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.lte.utils.AppUtils.getNewMac;
import static com.lte.utils.DateUtils.getTime;


/**
 * Created by chenxiaojun on 2017/8/14.
 */

public class LoginActivity extends BaseActivity {

    private static final int GET_JURISDICTION = 2;
    private static final int NO_JURISDICTION1 = 4;
    private TitleBar titleBar;

    private TextView lang;

    private EditText et_Account;

    private EditText et_Password;

    private Button login_bt;

    private List<UserInfo> userInfos;

    private MUiHandler mUiHandler;

    private static final int REGISTER_TIME_OUT = 1;

    private static final int NO_JURISDICTION = 3;

    private static class MUiHandler extends Handler {



        private WeakReference<LoginActivity> reference;


        MUiHandler(LoginActivity loginActivity) {
            reference = new WeakReference<>(loginActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_TIME_OUT:
                    try {
                        DialogManager.closeDialog(reference.get().localProgressFlag.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CommonToast.show(reference.get(), R.string.getJurisdictionFail);
                    reference.get().finish();
                    break;
                case GET_JURISDICTION:
                    reference.get().getJurisdiction("深圳市");
                    break;
                case NO_JURISDICTION:
                    reference.get().setOnDismissListener();
                    reference.get().showCommErrorDialog(R.string.no_Jurisdiction_error);
                    break;
                case NO_JURISDICTION1:
                    reference.get().setOnDismissListener();
                    reference.get().showCommErrorDialog(R.string.no_Jurisdiction_error1);
                    break;
                case 5:
                    HttpResult httpResult = (HttpResult) msg.obj;
                    Long times = getTime(httpResult.getTime());
                    reference.get().setOnDismissListener1();
                    reference.get().showCommRightDialog(String.format(reference.get().getString(R.string.current_remaining_use_time), httpResult.getAuthTime() - ((System.currentTimeMillis() - times) / 1000 / 60 / 60)));
                    App.get().setDelay(httpResult.getAuthTime() * 60 * 60 * 1000L - ((System.currentTimeMillis() - times)));
                    break;
            }
        }
    }

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
            detectIfCanLogin();
        }
    };
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (v == et_Account && et_Account.getHint() != null) {
                    String hint = et_Account.getHint().toString();
                    et_Account.setTag(hint);//保存预设字
                    et_Account.setHint(null);
                } else if (v == et_Password && et_Password.getHint() != null) {
                    String hint = et_Password.getHint().toString();
                    et_Password.setTag(hint);//保存预设字
                    et_Password.setHint(null);
                }
            } else {
                if (v == et_Account) {
                    if (et_Account.getText() == null || TextUtils.isEmpty(et_Account.getText().toString())) {
                        et_Account.setHint(et_Account.getTag().toString());
                    }
                } else if (v == et_Password) {
                    if (et_Password.getText() == null || TextUtils.isEmpty(et_Password.getText().toString())) {
                        et_Password.setHint(et_Password.getTag().toString());
                    }
                }
            }
        }
    };
    private String ip = "192.168.178.217";
    private int port = Constants.TCP_PORT;

    private String ip1 = "192.168.55.114";
    private int port1 = Constants.TCP_PORT;
    private TcpServer tcpSevice;
    private HttpCallback callback = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            if(jsonObject.toString().contains("responseCode")){
                return;
            }
            mUiHandler.removeCallbacksAndMessages(null);
            Gson gson = new Gson();
            HttpResult httpResult = gson.fromJson(jsonObject, new TypeToken<HttpResult>() {
            }.getType());
            try {
                DialogManager.closeDialog(localProgressFlag.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            LETLog.d(httpResult.toString());
            if (httpResult.getResult() != 0) {
                if (httpResult.getResult() == 1) {
                    Long times = getTime(httpResult.getTime());
                    if (System.currentTimeMillis() - times > (httpResult.getAuthTime() * 60 * 60 * 1000L) || System.currentTimeMillis() - times < 0) {
                        mUiHandler.sendEmptyMessageDelayed(NO_JURISDICTION1,10l);
                    } else {
                        Message message = Message.obtain();
                        message.obj = httpResult;
                        message.what = 5;
                        mUiHandler.sendMessageDelayed(message,10);
                    }
                } else {
                    LETLog.d("-----"+httpResult.toString());
                    mUiHandler.sendEmptyMessageDelayed(NO_JURISDICTION,10l);
                }
            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };

    private void showCommRightDialog(String resId) {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setSuccess(resId).show();
    }

    private AtomicReference<String> localProgressFlag;

    private void setOnDismissListener() {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    LoginActivity.this.finish();
                }
            });
    }

    private void setOnDismissListener1() {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
    }

    protected void showCommErrorDialog(@StringRes int resId) {
        if (mCommBottomDialog != null)
            mCommBottomDialog.setError(resId).show();

    }

    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                aMapLocation.getAccuracy();//获取精度信息
                LETLog.d("AMapLocationClient:" + aMapLocation.getCity());
                if (TextUtils.isEmpty(aMapLocation.getCity())) {
                    getJurisdiction(AppUtils.str2HexStr("深圳市"));
                } else {
                    getJurisdiction(AppUtils.str2HexStr(aMapLocation.getCity()));
                }
                mUiHandler.removeCallbacksAndMessages(null);
            }
        }
    };
    private CommBottomDialog mCommBottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        init();
        initAccount();
        EventBus.getDefault().register(this);
        initLocation();
    }

    private void getJurisdiction(String location) {
//        HttpRequestHelper.sendHttpRequest(Request.Method.POST,Constants.getJurisdiction( getNewMac(),str2HexStr(location)) ,callback);
//  xmd原来的，现在写死      HttpRequestHelper.sendHttpRequest(Request.Method.POST, Constants.getJurisdiction(getNewMac(), location), callback);
        if(!getNewMac().equalsIgnoreCase("EC:56:23:53:62:C3")&&!getNewMac().equalsIgnoreCase("30:74:96:4c:5c:68")){
            HttpRequestHelper.sendHttpRequest(Request.Method.POST, Constants.getJurisdiction(getNewMac(), location), callback);
        }else{
            try {
                DialogManager.closeDialog(localProgressFlag.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent) {
        if (outEvent.isOut()) {
            this.finish();
        }
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

        //xmd暂时去掉
        localProgressFlag = new AtomicReference<>();
        localProgressFlag.set(DialogManager.showProgressDialog(this, "申请权限中，请稍候..",false));

        mUiHandler = new MUiHandler(this);

        mUiHandler.sendEmptyMessageDelayed(GET_JURISDICTION, 5 * 1000L);

        mUiHandler.sendEmptyMessageDelayed(REGISTER_TIME_OUT, 20 * 1000L);

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


    private void initView() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);

        et_Account = (EditText) findViewById(R.id.et_user_name);

        et_Password = (EditText) findViewById(R.id.et_password);

        login_bt = (Button) findViewById(R.id.login_bt);

    }

    private void init() {
        titleBar.setTitle(R.string.account_login);
        login_bt.setOnClickListener(loginListener);
        et_Account.setOnFocusChangeListener(focusListener);
        et_Password.setOnFocusChangeListener(focusListener);

        mCommBottomDialog = new CommBottomDialog.Builder(this).create();

    }

    private void initAccount() {
        userInfos = DataManager.getInstance().findUser();
        Log.d("LOGIN", "userInfos :" + userInfos.size());
        if (userInfos == null || userInfos.size() == 0) {
            DataManager.getInstance().crateOrUpdate(Constants.DEFAULT_SUPER_ACCOUNT);
            userInfos.add(Constants.DEFAULT_SUPER_ACCOUNT);
        }
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected boolean validateUserName(String userName) {

        return !TextUtils.isEmpty(userName);
    }

    protected boolean validatePassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    private void detectIfCanLogin() {

        final String username = et_Account.getText().toString();
        final String password = et_Password.getText().toString();

        if (!validateUserName(username)) {
            CommonToast.show(this, R.string.account_username_empty);
        } else if (!validatePassword(password)) {
            CommonToast.show(this, R.string.account_password_at_least);
        } else {
            attemptLogin(username, password);
        }
    }

    private void attemptLogin(String username, String password) {
        boolean isSuccess = false;
        for (UserInfo userInfo : userInfos) {
            if (TextUtils.equals(username, userInfo.getUserName()) && TextUtils.equals(password, userInfo.getPassword())) {
                isSuccess = true;
                break;
            }
        }
        if (isSuccess) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            Constants.CURRENT_ACCOUNT = username;
            startActivity(intent);
        } else {
            CommonToast.show(this, R.string.login_error);
        }
    }
}
