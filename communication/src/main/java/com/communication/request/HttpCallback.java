package com.communication.request;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by dh on 2015/7/3.
 */
public interface HttpCallback {
    void onSuccess(JsonElement jsonObject);
    void onFailed(Exception errorMsg);
}
