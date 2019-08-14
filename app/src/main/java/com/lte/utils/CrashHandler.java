package com.lte.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler mInstance;
    private static final String TAG = "CRASHHANDLER";
    private Context mContext;
    private Thread.UncaughtExceptionHandler mLocalExceptionHandler;
    private boolean mInitialized = false;
    final int REQUEST_WRITE=1;//申请权限的请求码

    private CrashHandler() {

    }


    public static CrashHandler getInstance() {
        synchronized (CrashHandler.class) {
            if (mInstance == null) {
                mInstance = new CrashHandler();
            }
        }

        return mInstance;
    }


    public void initialize(Context context) {
        if (mInitialized) {
            return;
        }
        mContext = context;

        mLocalExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(this);

        mInitialized = true;

    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // TODO Auto-generated method stub

        handleException(throwable);

        //如果用户没有处理则让系统默认的异常处理器来处理
        if (mLocalExceptionHandler != null) {
            mLocalExceptionHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }


    public boolean handleException(Throwable throwable) {

        if (throwable == null) {
            return false;
        }

        //保存日志文件
        saveCrashInfo2File(throwable);
        return true;
    }



    private String saveCrashInfo2File(Throwable throwable) {

        StringBuffer sb = new StringBuffer();
        sb.append("Error information:\n");

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSSS", Locale.getDefault());
            String time = formatter.format(new Date(System.currentTimeMillis()));
            String fileName = "crash_" + time + ".log";

            Log.i(TAG, fileName);

            File file = new File("/storage/sdcard0/crash");
            if (!file.exists()) {
                file.mkdir();
            }

            Log.i(TAG, "file: " + file.toString());

            //判断是否6.0以上的手机   不是就不用
            if(Build.VERSION.SDK_INT>=23){
                //判断是否有这个权限
                if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    //2、申请权限: 参数二：权限的数组；参数三：请求码
                    Log.i(TAG, "---申请权限--没有权限-- ");

//					ActivityCompat.requestPermissions(mContext,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE);
                }else {

                    Log.i(TAG, "---writeToSdCard:1 ");
                    writeToSdCard(sb.toString());
                }
            } else{
                Log.i(TAG, "---Api<23-- ");
                writeToSdCard(sb.toString());
            }

//            FileOutputStream fos = new FileOutputStream(new File(file, fileName));
//            fos.write(sb.toString().getBytes());
//            fos.close();

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    //写数据
    public void writeToSdCard(String log){
        //1、判断sd卡是否可用
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //sd卡可用
            //2、获取sd卡路径
            DateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault());
            String time = formatter.format(new Date(System.currentTimeMillis()));
            String fileName = "Crash_" + time;
            File sdFile=Environment.getExternalStorageDirectory();
            File path=new File(sdFile,fileName+".txt");//sd卡下面的a.txt文件  参数 前面 是目录 后面是文件
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(path);
                fileOutputStream.write(log.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
