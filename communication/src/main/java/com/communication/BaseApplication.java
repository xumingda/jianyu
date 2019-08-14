package com.communication;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.communication.http.BaseRequest;
import com.communication.utils.LETLog;
import com.communication.volley.AuthFailureError;
import com.communication.volley.DefaultRetryPolicy;
import com.communication.volley.RequestQueue;
import com.communication.volley.VolleyLog;
import com.communication.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

/**
 * Created by chenxiaojun on 2017/8/15.
 */

public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();
    private static BaseApplication mInstance;
    private RequestQueue mRequestQueue;
    private Network httpURLConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    @TargetApi(21)
    public void forceSendRequestByMobileData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NET_CAPABILITY_INTERNET);
        //强制使用蜂窝数据网络-移动数据
        builder.addTransportType(TRANSPORT_CELLULAR);
        NetworkRequest build = builder.build();
        connectivityManager.requestNetwork(build, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                mRequestQueue = null;
                httpURLConnection = network;
                LETLog.d("network :" + network );

            }
        });
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            if (httpURLConnection != null) {
                try {
                    mRequestQueue = Volley.newRequestQueue(this, httpURLConnection,createIgnoreVerifySSL().getSocketFactory());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    mRequestQueue = Volley.newRequestQueue(this,createIgnoreVerifySSL().getSocketFactory());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        }
        return mRequestQueue;
    }
    private static SSLContext createIgnoreVerifySSL()
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
        return sc;
    }

    public <T> void addToRequestQueue(BaseRequest<T> req, String tag) {
        req.setRetryPolicy(new DefaultRetryPolicy(6 * 1000, 0, 1.0f));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
        try {
            LETLog.d("URL :" + req.getUrl() +req.getHeaders().toString() );
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    public <T> void addToRequestQueue(BaseRequest<T> req) {
        addToRequestQueue(req, "");
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
