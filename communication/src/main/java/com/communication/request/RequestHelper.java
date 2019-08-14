package com.communication.request;

import android.text.TextUtils;


import com.communication.http.HttpRequestHelper;
import com.communication.tcp.Client;
import com.communication.tcp.TcpService;
import com.communication.tcp.base.IMessage;
import com.communication.utils.Constant;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * 
* @ClassName: RequestHelper 
* @Description:(请求辅助类) 
* @author dh
* @date 2015-12-9 下午5:44:23 
*
 */
public class RequestHelper {
	protected static final String TAG = "RequestHelper";
	private volatile static RequestHelper mRequestHelper;
	private LinkedList<String> deviceIds;
	private String path;
	private boolean isSuccessed = true;
	private int localFailTime;
	private int unitFailTime;
	/**
	 * 
	* @Title: isLocalConnected 
	* @Description: TODO(本地是否连接) 
	* @return    
	* @return boolean true|false 连接|未连接
	* @throws
	 */
	public boolean isLocalConnected(String ip){
		return Client.getInstance().isLocal(ip);
	}
	
	/**
	 * 
	* @Title: createSocket 
	* @Description: TODO(创建Socket) 
	* @param tcpService    IP地址
	* @return void 
	* @throws
	 */
	public void createSocket(TcpService tcpService){
		if(TextUtils.isEmpty(tcpService.getIp())){
			return;
		}
		if(isLocalConnected(tcpService.getIp())){
			return;
		}
		try {
			Client.getInstance().addTcpService(tcpService);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	* @Title: releaseSocket 
	* @Description: TODO(释放socket连接)     
	* @return void
	* @throws
	 */
	public void releaseSocket(TcpService tcpService){
		Client.getInstance().releaseConnected(tcpService.getIp());
	}
	
//	/**
//	 *
//	* @Title: restartSocket
//	* @Description: TODO(重连上一个ip的socket)
//	* @return void
//	* @throws
//	 */
//	public void restartSocket(String ip){
//		if(isLocalConnected(ip)){
//			return;
//		}
//		mSocketHelper.connect(Session.getInstance().getHost(ip), Constant.LOCAL_COMMUNICATION_PORT);
//	}
	/**
	 *
	* @Title: registerListener
	* @Description: TODO(注册本地接受信息接口)
	* @param iLocalMessage
	* @return void
	* @throws
	 */
//	public void registerListener(ILocalMessage iLocalMessage){
//		mSocketHelper.registerListener(iLocalMessage);
//	}
//	/**
//	 *
//	* @Title: unRegisterListener
//	* @Description: TODO(反注册本地接受信息接口)
//	* @return void
//	* @throws
//	 */
//	public void unRegisterListener(){
//		mSocketHelper.unregisterListener();
//	}
	/**
	 *
	* @Title: requestRemoteData
	* @Description: TODO(远程服务器请求)
	* @param url  服务器地址
	* @param params 请求参数
	* @param responseCallBack  请求接口回调
	* @return void
	* @throws
	 */
	public void requestHttpData(String url,final JsonObject params,final HttpCallback responseCallBack){
		HttpRequestHelper.sendHttpRequest(url, params, responseCallBack);
	}
	/**
//	 *
//	* @Title: requestLocalData
//	* @Description: TODO(本地请求)
//	* @param ip  本地IP
//	* @param params 请求参数
// 	* @param responseCallBack  请求回调接口
//	* @return void
//	* @throws
//	 */
//	public void requestSocketData(final String ip, final String params, final SocketCallback responseCallBack){
//		if(!isLocalConnected(ip)){
//			if(ip !=null){
//				createSocket(ip);
//			}
//		}
//		new Thread(){
//			public void run() {
//					mSocketHelper.sendMsg(params, new IMessage() {
//						@Override
//						public void receiveMsg(int uid, String data) {
//							if(responseCallBack!=null){
//								responseCallBack.onSuccess(data);
//							}
//						}
//
//						@Override
//						public void onFailed(Exception e) {
//							if(responseCallBack!=null){
//								responseCallBack.onFailed(e);
//							}
//						}
//					});
//			}
//		}.start();
//	}

	
	private RequestHelper(){}
	public static RequestHelper getInstance(){
		if(mRequestHelper==null){
			synchronized (RequestHelper.class) {
				if(mRequestHelper==null){
					mRequestHelper=new RequestHelper();
				}
			}
		}
		return mRequestHelper;
	}
	private String getHttpUrl(String ip){
		return "http://"+ip+":"+Constant.REMOTE_COMMUNICATION_PORT;
	}
}
