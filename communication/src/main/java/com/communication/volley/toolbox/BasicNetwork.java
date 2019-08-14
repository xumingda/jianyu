/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.os.SystemClock;
import android.util.Log;

import com.communication.volley.AuthFailureError;
import com.communication.volley.Cache.Entry;
import com.communication.volley.Network;
import com.communication.volley.NetworkError;
import com.communication.volley.NetworkResponse;
import com.communication.volley.NoConnectionError;
import com.communication.volley.Request;
import com.communication.volley.RetryPolicy;
import com.communication.volley.ServerError;
import com.communication.volley.TimeoutError;
import com.communication.volley.VolleyError;
import com.communication.volley.VolleyLog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//import org.apache.http.conn.ConnectTimeoutException;

/**
 * A network performing Volley requests over an {@link HttpStack}.
 */
public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;

    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

    private static int DEFAULT_POOL_SIZE = 4096;

    protected final HttpStack mHttpStack;

    protected final ByteArrayPool mPool;

    /**
     * @param httpStack HTTP stack to be used
     */
    public BasicNetwork(HttpStack httpStack) {
        // If BaseApplication pool isn't passed in, then build BaseApplication small default pool that will give us BaseApplication lot of
        // benefit and not use too much memory.
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    /**
     * @param httpStack HTTP stack to be used
     * @param pool BaseApplication buffer pool that improves GC performance in copy operations
     */
    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
        mHttpStack = httpStack;
        mPool = pool;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
//            HttpResponse httpResponse = null;
        	HttpURLConnection httpResponse = null;
            byte[] responseContents = null;
            Map<String, String> responseHeaders = Collections.emptyMap();
            try {
                // Gather headers.
                Map<String, String> headers = new HashMap<String, String>();
                addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = mHttpStack.performRequest(request, headers);
                int statusCode =httpResponse.getResponseCode();
                responseHeaders = convertHeaders(httpResponse.getHeaderFields());
                Log.w("click", "----------0---------->"+statusCode);
                // Handle cache validation.
                if (statusCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                	Log.w("click", "----------1---------->"+statusCode);
                    Entry entry = request.getCacheEntry();
                    if (entry == null) {
                        return new NetworkResponse(HttpURLConnection.HTTP_NOT_MODIFIED, null,
                                responseHeaders, true,
                                SystemClock.elapsedRealtime() - requestStart);
                        
                    }

                    // A HTTP 304 response does not have all header fields. We
                    // have to use the header fields from the cache entry plus
                    // the new ones from the response.
                    // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
                    entry.responseHeaders.putAll(responseHeaders);
                    return new NetworkResponse(HttpURLConnection.HTTP_NOT_MODIFIED, entry.data,
                            entry.responseHeaders, true,
                            SystemClock.elapsedRealtime() - requestStart);
                }
                
                // Handle moved resources
                if (statusCode == HttpURLConnection.HTTP_MOVED_PERM|| statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                	String newUrl = responseHeaders.get("Location");
                	request.setRedirectUrl(newUrl);
                }

                // Some responses such as 204s do not have content.  We must check.
                if (httpResponse != null) {
                  responseContents = entityToBytes(httpResponse);
                } 
//                else {
//                  // Add 0 byte response as BaseApplication way of honestly representing BaseApplication
//                  // no-content request.
//                  responseContents = new byte[0];
//                }

                // if the request is slow, log it.
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                logSlowRequests(requestLifetime, request, responseContents, statusCode);

                if (statusCode < 200 || statusCode > 299) {
                	Log.w("click", "----------2---------->"+statusCode);
                    throw new IOException();
                }
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false,
                        SystemClock.elapsedRealtime() - requestStart);
            } catch (SocketTimeoutException e) {
            	Log.w("click", "----------3---------->");
                attemptRetryOnException("socket", request, new TimeoutError(e.getLocalizedMessage()));
            } 
//            catch (ConnectTimeoutException e) {
//                attemptRetryOnException("connection", request, new TimeoutError());
//            } 
            catch (MalformedURLException e) {
            	Log.w("click", "----------4---------->");
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
            	Log.w("click", "----------5---------->");
                int statusCode = 0;
                NetworkResponse networkResponse = null;
                if (httpResponse != null) {
                    try {
						statusCode = httpResponse.getResponseCode();
					} catch (IOException e1) {
						Log.w("click", "----------6---------->");
						throw new NoConnectionError(e1);
						
					}
                } else {
                	Log.w("click", "----------7---------->"+statusCode);
                	Log.w("click", "----------7---------->"+e.getLocalizedMessage());
                    throw new NoConnectionError(e);
                }
                String exceptionMessage=null;
                if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || 
                		statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                	exceptionMessage="Request at "+request.getOriginUrl()+" has been redirected to "+request.getUrl();
                	VolleyLog.e("Request at %s has been redirected to %s", request.getOriginUrl(), request.getUrl());
                } else {
                	exceptionMessage="Unexpected response code "+statusCode+" for "+request.getUrl();
                	VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
                }
                if (responseContents != null) {
                	Log.w("click", "----------8---------->");
                	networkResponse = new NetworkResponse(statusCode, responseContents,
                			responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                    if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED ||
                            statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    	
                    	Log.w("click", "----------9---------->");
                        attemptRetryOnException("auth",
                                request, new AuthFailureError(networkResponse));
                    } else if (statusCode == HttpURLConnection.HTTP_MOVED_PERM || 
                    			statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    	Log.w("click", "----------10---------->");
                        attemptRetryOnException("redirect",
                                request, new AuthFailureError(networkResponse));
                    } else {
                    	Log.w("click", "----------11---------->");
                        // TODO: Only throw ServerError for 5xx status codes.
                        throw new ServerError(networkResponse);
                    }
                } else {
                	Log.w("click", "----------12---------->");
                    throw new NetworkError(exceptionMessage);
                    
                }
            }
        }
    }

    /**
     * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
     */
    private void logSlowRequests(long requestLifetime, Request<?> request,
            byte[] responseContents, int statusCode) {
        if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " +
                    "[rc=%d], [retryCount=%s]", request, requestLifetime,
                    responseContents != null ? responseContents.length : "null",
                    statusCode, request.getRetryPolicy().getCurrentRetryCount());
        }
    }

    /**
     * Attempts to prepare the request for BaseApplication retry. If there are no more attempts remaining in the
     * request's retry policy, BaseApplication timeout exception is thrown.
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request,
            VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(exception);
        } catch (VolleyError e) {
            request.addMarker(
                    String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    private void addCacheHeaders(Map<String, String> headers, Entry entry) {
        // If there's no cache entry, we're done.
        if (entry == null) {
            return;
        }

        if (entry.etag != null) {
            headers.put("If-None-Match", entry.etag);
        }

        if (entry.lastModified > 0) {
            Date refTime = new Date(entry.lastModified);
            headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
        }
    }

    protected void logError(String what, String url, long start) {
        long now = SystemClock.elapsedRealtime();
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, (now - start), url);
    }

    /** Reads the contents of HttpEntity into BaseApplication byte[]. */
    private byte[] entityToBytes(HttpURLConnection entity) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes =
                new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = entity.getInputStream();
            
            if (in == null) {
                throw new ServerError();
            }
            buffer = mPool.getBuf(1024);
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                entity.disconnect();
            } catch (Exception e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
            mPool.returnBuf(buffer);
            bytes.close();
        }
    }

    /**
     * Converts Headers[] to Map<String, String>.
     */
    protected static Map<String, String> convertHeaders(Map<String,List<String>> headers) {
        Map<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	    for (Map.Entry<String, List<String>> header : headers.entrySet()) {
	      if (header.getKey() != null) {
	          result.put(header.getKey(), header.getValue().get(0));
	      }
	    }
        return result;
    }
}
