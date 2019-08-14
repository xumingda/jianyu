package com.lte.utils;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenxiaojun on 2017/12/9.
 */

public class ThreadPoolManager {
    private static  ThreadPoolManager instance=new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return instance;
    }

    private ThreadPoolExecutor threadPoolExecutor;
    private LinkedBlockingQueue<Future<?>> service=new LinkedBlockingQueue<>();

    private ThreadPoolManager(){
        threadPoolExecutor=new ThreadPoolExecutor(4,10,10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4),handler);
        threadPoolExecutor.execute(runnable);
    }

    public void removeAll(){
        if(service != null){
            service.clear();
            threadPoolExecutor.shutdownNow();
            threadPoolExecutor = null;
            threadPoolExecutor=new ThreadPoolExecutor(4,10,10, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(4),handler);
            threadPoolExecutor.execute(runnable);
        }
    }
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {

            while (true) {

                FutureTask futureTask = null;

                try {
                    Log.e("myThreadPook","service size "+service.size());
                    futureTask = (FutureTask) service.take();
                    Log.e("myThreadPook","池  "+threadPoolExecutor.getPoolSize());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (futureTask != null) {
                    threadPoolExecutor.execute(futureTask);
                }
            }
        }
    };

    public <T> void  execute(final FutureTask<T> futureTask, Object delayed){


        if(futureTask!=null){
            try {

                if(delayed!=null){
                    Timer timer=new Timer();
                    timer.schedule(new TimerTask(){
                        public void run(){
                            try {
                                service.put(futureTask);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }},(long)delayed);
                }else {
                    service.put(futureTask);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    private RejectedExecutionHandler handler=new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            try {
                service.put(new FutureTask<Object>(runnable,null));//
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

}


