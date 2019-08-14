package com.lte.https;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import com.App;
import com.communication.http.HttpRequestHelper;
import com.communication.request.HttpCallback;
import com.communication.utils.LETLog;
import com.communication.volley.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.data.DataManager;
import com.lte.data.table.ImsiDataTable;
import com.lte.ui.listener.QueryListener;
import com.lte.utils.AppUtils;
import com.lte.utils.DateUtils;
import com.lte.utils.ThreadUtils;
import com.lte.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * Created by chenxiaojun on 2017/12/14.
 */
// 根据imsi查询手机号
public class MobileQuery {
    private static final int READ_DATA = 2;

    private MobileQuery() {
    }

    private static MobileQuery mInstance;

    public static MobileQuery getInstance() {
        if (mInstance == null) {
            synchronized (DataManager.class) {
                if (mInstance == null) {
                    mInstance = new MobileQuery();
                }
            }
        }
        return mInstance;
    }

    public  void getApiKey(HttpCallback callBack) {
        String UserPasswd = App.get().userInfo.getMobileUserName() + ":" + App.get().userInfo.getMobilePassword();
        String Authorencode = "Basic " +
                Base64.encodeToString(UserPasswd.getBytes(), Base64.DEFAULT);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Authorencode);
        HttpRequestHelper.putHttpRequest(Request.Method.PUT, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "getapitoken"), headers, callBack);
    }
    public void getApiKey(HttpCallback callBack,String useName,String psw) {
        String UserPasswd = useName + ":" + psw;
        String Authorencode = "Basic " +
                Base64.encodeToString(UserPasswd.getBytes(), Base64.DEFAULT);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", Authorencode);
        HttpRequestHelper.putHttpRequest(Request.Method.PUT, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "getapitoken"), headers, callBack);
    }

    public  void getNumber(HttpCallback callBack) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", App.get().apikey);
        HttpRequestHelper.putHttpRequest(Request.Method.GET, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "remain"), headers, callBack);
    }

    //读取数据解析完成的手机号
    public  void readData(HttpCallback callBack) {
        if (!TextUtils.isEmpty(App.get().apikey) && App.get().apikeyCanUse) {
            readFailNum = 0;
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", App.get().apikey);
            HttpRequestHelper.putHttpRequest(Request.Method.DELETE, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "output"), headers, callBack);

        }
    }

    //提交数据库解析
    public  void writeData(String dtag, HttpCallback callBack) {
        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ："+"write data :" + dtag);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", App.get().apikey);
        headers.put("dtag", Base64.encodeToString(dtag.getBytes(), Base64.DEFAULT));
        HttpRequestHelper.putHttpRequest(Request.Method.POST, AppUtils.getHttpsUrl(App.get().userInfo.getQueryUrl(), App.get().userInfo.getMobilePort(), "input"), headers, callBack);
        mHandler.sendEmptyMessageDelayed(6,5*1000L);
    }

    private ImsiDataTable imsiData;
    QueryListener listener;

    public void queryMobile(ImsiDataTable imsiDataTable, QueryListener listener) {
        this.imsiData = imsiDataTable;
        this.listener = listener;
        mHandler.sendEmptyMessageDelayed(5,60*1000L);
        LETLog.d("queryMobile :" + (App.get().apikey == null));
        if (App.get().apikey == null) {
            getApiKey(callBack);
        } else {
            if (App.get().apikeyCanUse) {
                writeData("101" + imsiData.getImsi(), callBack1);
            } else {
                mHandler.sendEmptyMessageDelayed(4, 20 * 1000L);
            }
        }
    }

    private Handler mHandler = new Handler(App.get().getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtils.showToast(getApplicationContext(),"获取令牌成功", Toast.LENGTH_SHORT);
                    App.get().apikeyCanUse = true;
                    writeData("101" + imsiData.getImsi(), callBack1);
                    break;
                case READ_DATA:
                    readData(callBack2);
                    break;
                case 3:
                    removeMessages(5);
                    String mobile = (String) msg.obj;
                    LETLog.d("解析结果：" + mobile + imsiData.toString());
                    DataManager.getInstance().createOrUpdateImsi(imsiData.getImsi(), mobile);
                    break;
                case 4:
                    writeData("101" + imsiData.getImsi(), callBack1);
                    break;
                case 5:
                    if (listener != null) {
                        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ：" +" fail13");
                        listener.onFail();
                    }
                    break;
                case 6:
                    if (listener != null) {
                        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ：" +" 提交失败");
                        listener.onFail();
                    }
                    break;
            }
        }
    };
    private HttpCallback callBack = new HttpCallback() {
        @Override
        public void onSuccess(JsonElement jsonObject) {
            try {
                JsonObject asJsonObject = jsonObject.getAsJsonObject();
                if (asJsonObject.get("Authorization") != null) {
                    App.get().apikey = asJsonObject.get("Authorization").getAsString();
                    mHandler.sendEmptyMessageDelayed(1, 20 * 1000);
                } else {
                    if (listener != null) {
                        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ：" +" fail14");
                        listener.onFail();
                    }
                    LETLog.d("query 5");
                }
            } catch (Exception e) {

            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
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
                                mHandler.removeMessages(READ_DATA);//提交成功后，延时20秒查询结果
                                mHandler.removeMessages(6);
                                mHandler.sendEmptyMessageDelayed(READ_DATA, 3 * 1000);//提交成功后，延时20秒查询结果
                            }else {
                                if(!App.get().apikeyCanUse){
                                    getApiKey(callBack);
                                }
                            }
                        } else {
                            if (listener != null) {
                                LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ：" +" fail1");
                                listener.onFail();
                            }
                        }
                    } catch (Exception e) {
                        if (listener != null) {
                            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ：" +" fail2");
                            listener.onFail();

                        }
                    }
                }
            });
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private static int readFailNum = 0;
    private HttpCallback callBack2 = new HttpCallback() {
        @Override
        public void onSuccess(final JsonElement jsonObject) {
            LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
            try {
                JsonArray asJsonArray = jsonObject.getAsJsonArray();
                for (JsonElement jsonElement : asJsonArray) {
                    JsonObject asJsonObject = jsonElement.getAsJsonObject();
                    if (asJsonObject.get("rcontent") != null) {
                        String rcontent = new String(Base64.decode(asJsonObject.get("rcontent").getAsString(), Base64.DEFAULT));
                        String trim = rcontent.trim();
                        String imsi = trim.substring(3, 18);
                        String mobile = trim.substring(22);
                        LETLog.d("解析结果：" + imsi + imsiData.toString());
                        if (TextUtils.equals(imsiData.getImsi(), imsi)) {
                            Message message = Message.obtain();
                            message.obj = mobile;
                            message.what = 3;
                            mHandler.sendMessage(message);
                            if (listener != null) {
                                listener.onSuccess();
                            }

                        }
                    }

                }
            } catch (Exception ignored) {

            }
            try {
                JsonObject asJsonObject = jsonObject.getAsJsonObject();
                LETLog.d(" jsonObject : + " + (asJsonObject.get("responseCode") != null));
                if (asJsonObject.get("responseCode") != null) {
                    if (asJsonObject.get("responseCode").getAsInt() == 404) {

                        if(readFailNum<7){
                            mHandler.sendEmptyMessageDelayed(READ_DATA,3*1000L);
                            readFailNum++;
                        }else {
                            //查询库内没有结果，重新提交号码。
                            if (listener != null) {
                                LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + " ：" +" fail15");
                                listener.onFail();
                            }
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
}
