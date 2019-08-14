/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.communication.volley.toolbox;

import android.content.Context;
import android.util.Log;

import com.communication.volley.Network;
import com.communication.volley.RequestQueue;

import java.io.File;

import javax.net.ssl.SSLSocketFactory;

public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates BaseApplication default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set BaseApplication maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes,android.net.Network network1,SSLSocketFactory sslSocketFactory) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

//        String userAgent = "volley/0";
//        try {
//            String packageName = context.getPackageName();
//            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
//            userAgent = packageName + "/" + info.versionCode;
//        } catch (NameNotFoundException e) {
//        }
        
        /**------------20151130--------移除httpclient---*/
        
        if (stack == null) {
//            if (Build.VERSION.SDK_INT >= 9) {
            if(network1 != null){
                stack = new HurlStack(network1,sslSocketFactory);
            }else {
                stack = new HurlStack(sslSocketFactory);
            }

//            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
//            }
        }

        Network network = new BasicNetwork(stack);
        
        RequestQueue queue;
        if (maxDiskCacheBytes <= -1)
        {
        	// No maximum size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        }
        else
        {
        	// Disk cache size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network);
        }

        queue.start();

        return queue;
    }
    
    /**
     * Creates BaseApplication default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set BaseApplication maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, int maxDiskCacheBytes) {
        return newRequestQueue(context, null, maxDiskCacheBytes,null,null);
    }
    
    /**
     * Creates BaseApplication default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, android.net.Network network,SSLSocketFactory sslSocketFactory)
    {
    	return newRequestQueue(context, stack, -1,network,sslSocketFactory);
    }
    
    /**
     * Creates BaseApplication default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
//    public static RequestQueue newRequestQueue(Context context) {
//        return newRequestQueue(context, null,null,null);
//    }
//    public static RequestQueue newRequestQueue(Context context, android.net.Network network) {
//        return newRequestQueue(context,null, network,null);
//    }
    public static RequestQueue newRequestQueue(Context context, SSLSocketFactory sslSocketFactory) {
        Log.d("https","newRequestQueue :"+sslSocketFactory);
        return newRequestQueue(context, null,null,sslSocketFactory);
    }
    public static RequestQueue newRequestQueue(Context context, android.net.Network network,SSLSocketFactory sslSocketFactory) {
        Log.d("https","newRequestQueue :"+sslSocketFactory);
        return newRequestQueue(context,null, network,sslSocketFactory);
    }
}

