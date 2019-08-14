package com.lte.utils;

import com.App;
import com.communication.BaseApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理工具类
 * @author zfb
 *
 */
public class ThreadUtils {
	//线程池执行者的代理对象
	private static ThreadPoolProxy threadPoolProxy;
	//根据可用cpu数量决定线程数
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
	private static final int KEEP_ALIVE = 1;
	//取得代理对象
	public static ThreadPoolProxy getThreadPoolProxy(){
		if(threadPoolProxy==null){
			threadPoolProxy=new ThreadPoolProxy(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEP_ALIVE);
		}
		return threadPoolProxy;
		
	}
	//线程池执行者的代理类
	public static class ThreadPoolProxy{
		private int corePoolSize;
		private int maximumPoolSize;
		private long keepAliveTime;
		public ThreadPoolProxy(int corePoolSize,int maximumPoolSize,long keepAliveTime){
			this.corePoolSize = corePoolSize;
			this.maximumPoolSize = maximumPoolSize;
			this.keepAliveTime = keepAliveTime;
		}
		//java 的线程池的执行者，管理者
		private ThreadPoolExecutor poolExecutor;
		public void execute(Runnable r){
			if(poolExecutor==null||poolExecutor.isShutdown()){
				poolExecutor=new ThreadPoolExecutor(
						//核心线程数量
						corePoolSize,
						//最大线程数量
						maximumPoolSize,
						// 在keepAliveTime时间内，没有执行任务，也是活跃的，超过该时间，则处于停止状态
						keepAliveTime,
						//时间单元，毫秒
						TimeUnit.MILLISECONDS,
						//任务队列
						new LinkedBlockingDeque<Runnable>(), 
						//线程工厂  ,缺省线程工厂
						Executors.defaultThreadFactory());
			}
			poolExecutor.execute(r);
		}
		public void removeTask(Runnable task){
			if(poolExecutor!=null){
				poolExecutor.remove(task);
			}
		}
		public void purge(){
			if(poolExecutor!=null){
				poolExecutor.purge();
			}
		}
	}

	//取得主线程的 id
	public static int getMainThreadId(){
		return App.getMainThreadId();
	}

	//判断当前线程是否为主线程
	public static boolean isMainThread(){
		return getMainThreadId()==android.os.Process.myTid();
	}
	//切换至主线程运行
	public static void runOnMainThread(Runnable r){
		if(isMainThread()){
			//直接执行

			r.run();
		}else{
			App.getHandler().post(r);
		}

	}

}
