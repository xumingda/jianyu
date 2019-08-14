package com.communication.http;


import android.util.Log;

import com.communication.utils.LETLog;
import com.communication.volley.AuthFailureError;
import com.communication.volley.NetworkResponse;
import com.communication.volley.ParseError;
import com.communication.volley.Response;
import com.communication.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dh on 2015/7/3.
 */
public class JsonPostRequest extends BaseRequest<JsonElement> {
    private JsonElement mParams;
    private Response.Listener<JsonElement> mListener;

    public JsonPostRequest(int method, String url, JsonElement params, Response.Listener<JsonElement> listener, Response.ErrorListener ErrorListener) {
        super(method, url, ErrorListener);
        mListener = listener;
        mParams = params;
    }

    public JsonPostRequest(String url, JsonElement params, Response.Listener<JsonElement> listener, Response.ErrorListener ErrorListener) {
        super(Method.POST, url, ErrorListener);
        mListener = listener;
        mParams = params;
    }

    public JsonPostRequest(int method, String url, Response.Listener<JsonElement> listener, Response.ErrorListener ErrorListener) {
        super(method, url, ErrorListener);
        mListener = listener;
//        mParams=params;
    }

    public JsonPostRequest(String url, Response.Listener<JsonElement> listener, Response.ErrorListener ErrorListener) {
        super(Method.GET, url, ErrorListener);
        mListener = listener;
    }


    @Override
    protected JsonElement getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public Response<JsonElement> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JsonElement returnData = new JsonParser().parse(jsonString);
            if (jsonString.isEmpty()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("responseCode", response.statusCode);
                mListener.onResponse(jsonObject);
                return null;
            }else if(jsonString.contains("responseCode")){
                mListener.onResponse(returnData);
                return null;
            }
            return Response.success(returnData, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonIOException je) {
            return Response.error(new ParseError(je));
        }
    }
    public static String getNowDates(long time) {
        Date currentTime = new Date();
        currentTime.setTime(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
    @Override
    protected void deliverResponse(JsonElement response) {
        mListener.onResponse(response);
    }
}
