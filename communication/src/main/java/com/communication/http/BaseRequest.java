package com.communication.http;


import android.util.Base64;

import com.communication.utils.Constant;
import com.communication.volley.AuthFailureError;
import com.communication.volley.Request;
import com.communication.volley.Response;



import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenxiaojun on 2017/8/15.
 */

public abstract class BaseRequest<T> extends Request<T> {
    HashMap<String, String> headers = new HashMap<String, String>();
    public BaseRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        headers.put("Charset", Constant.HTTP_ENCODE);
        headers.put("Content-Type", Constant.HTTP_CONTENTTYPE);
        headers.put("User-Agent", Constant.HTTP_USERAGENT);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
}
