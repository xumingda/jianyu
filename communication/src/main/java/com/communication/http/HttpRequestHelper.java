package com.communication.http;



import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.communication.BaseApplication;
import com.communication.request.HttpCallback;
import com.communication.volley.AuthFailureError;
import com.communication.volley.Request;
import com.communication.volley.Response;
import com.communication.volley.VolleyError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by dh on 2015/7/3.
 */
public class HttpRequestHelper {
	private static final String TAG="Http";
	/**
	 * 
	 * @param url
	 * @param params
	 * @param tag
	 * @param callback
	 */
    public static void sendHttpRequest(String url, JsonElement params, String tag, final HttpCallback callback){
        if(TextUtils.isEmpty(url)){
            try {
                throw new Exception("Url is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if(callback == null){
//            try {
//                throw new Exception("HttpCallback is null");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    	Log.i(TAG, "params="+params);
        BaseApplication.getInstance().addToRequestQueue(new JsonPostRequest(url, params, new Response.Listener<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                if(callback!=null){
                    callback.onSuccess(response);
                    if(response!=null){
                    	Log.i(TAG, "result="+response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(callback != null){
                    callback.onFailed(error);
                }
            }
        }),tag);
    }
    /**
     *
     * @param url
     * @param callback
     */
    public static void sendHttpRequest(String url, final HttpCallback callback){
        if(TextUtils.isEmpty(url)){
            try {
                throw new Exception("Url is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        BaseApplication.getInstance().addToRequestQueue(new JsonPostRequest(url,  new Response.Listener<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                if(callback!=null){
                    callback.onSuccess(response);
                    if(response!=null){
                        Log.i(TAG, "result="+response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(callback != null){
                    callback.onFailed(error);
                }
            }
        }));
    }
    /**
     *
     * @param url
     * @param callback
     */
    public static void sendHttpRequest(int method,String url, final HttpCallback callback){
        if(TextUtils.isEmpty(url)){
            try {
                throw new Exception("Url is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BaseApplication.getInstance().addToRequestQueue(new JsonPostRequest(method,url,  new Response.Listener<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                if(callback!=null){
                    callback.onSuccess(response);
                    if(response!=null){
                        Log.i(TAG, "result="+response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(callback != null){
                    callback.onFailed(error);
                }
            }
        }));
    }
    /**
     *
     * @param url
     * @param params
     * @param tag
     * @param callback
     */
    public static void sendHttpRequest(String url, String params, String tag, final HttpCallback callback){
        if(TextUtils.isEmpty(url)){
            try {
                throw new Exception("Url is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if(callback == null){
//            try {
//                throw new Exception("HttpCallback is null");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        Log.i(TAG, "params="+params);
//        BaseApplication.getInstance().addToRequestQueue(new JsonPostRequest(url, params, new Response.Listener<JsonObject>() {
//            @Override
//            public void onResponse(JsonObject response) {
//                if(callback!=null){
//                    callback.onSuccess(response);
//                    if(response!=null){
//                        Log.i(TAG, "result="+response);
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if(callback != null){
//                    callback.onFailed(error);
//                }
//            }
//        }),tag);
    }
    /**
     * 
     * @param url
     * @param params
     * @param callback
     */
    public static void sendHttpRequest(String url,JsonElement params,final HttpCallback callback){
        sendHttpRequest(url,params,"",callback);
    }
    /**
     *
     * @param url
     * @param headers
     * @param callback
     */
    public static void putHttpRequest(int method,String url, Map<String, String> headers,final HttpCallback callback){
        putHttpRequest(method,url,headers,"",callback);
    }
    public static void putHttpRequest(int method,String url, Map<String, String> headers, String tag, final HttpCallback callback){
        if(TextUtils.isEmpty(url)){
            try {
                throw new Exception("Url is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        if(callback == null){
//            try {
//                throw new Exception("HttpCallback is null");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        JsonPostRequest jsonPostRequest = new JsonPostRequest(method, url, new Response.Listener<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                if (callback != null) {
                    callback.onSuccess(response);
                    if (response != null) {
                        Log.i(TAG, "result=" + response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onFailed(error);
                }
            }
        });
        try {
            Map<String, String> headers1 = jsonPostRequest.getHeaders();
            headers1.clear();
            headers1.putAll(headers);
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        BaseApplication.getInstance().addToRequestQueue(jsonPostRequest,tag);
    }
    /**
     *
     * @param url
     * @param params
     * @param callback
     */
    public static void sendHttpRequest(String url,String params,final HttpCallback callback){
        sendHttpRequest(url,params,"",callback);
    }
    /**
     * 
     * @param tag
     */
    public static void cancelHttpRequest(String tag){
        BaseApplication.getInstance().cancelPendingRequests(tag);
    }


}
