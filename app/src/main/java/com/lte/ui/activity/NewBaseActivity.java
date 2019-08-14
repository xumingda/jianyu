package com.lte.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.lte.R;
import com.lte.ui.event.SystemOutEvent;
import com.lte.utils.Sysinfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by chenxiaojun on 2018/2/9.
 */

public class NewBaseActivity extends SupportActivity {
    private static final String TAG = "NewBaseActivity";
    private PowerManager.WakeLock mWakeLock;
    private PowerManager pManager;

    protected static final byte IO_MODE_CHAR = 0;
    protected static final byte IO_MODE_HEX = 1;
    protected static final byte REQUEST_DISCOVERY = 1;
    protected static final byte REQUEST_ENABLE = 2;
    protected static final byte REQUEST_KEYBOARD = 3;
    private static boolean mbConectOk = false;
    private static BluetoothSocket mbsSocket = null;
    public static InputStream misIn = null;
    private static OutputStream mosOut = null;
    protected byte mInputMode;
    protected byte mOutputMode;
    public static String msBluetoothMAC;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);
        msBluetoothMAC = null;
        mInputMode = 0;
        mOutputMode = 0;
    }
    @Override
    protected void onResume() {
        super.onResume();
        pManager = ((PowerManager) getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, TAG);
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(null != mWakeLock){
            mWakeLock.release();
        }
    }
    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent){
        if(outEvent.isOut()){
            this.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected final void InitLoad()
    {
        mInputMode = (byte)getIntData("InputMode");
        mOutputMode = (byte)getIntData("OutputMode");
        msBluetoothMAC = getStrData("BluetoothMAC");
    }

    protected int ReceiveData(byte bytebuf[])
    {
        if (!mbConectOk)
            return -2;
        try
        {
            return misIn.read(bytebuf);
        }
        catch (IOException ioexception)
        {
            terminateConnect();
            return -3;
        }
    }

    protected int SendData(byte bytebuf[])
    {
        int bytelength;
        if (mbConectOk)
        {
            try
            {
                Log.i(TAG, "-----SendData-----"+bytebuf);
                mosOut.write(bytebuf);
                bytelength = bytebuf.length;
            }
            catch (IOException ioexception)
            {
                terminateConnect();
                bytelength = -3;
            }
        }
        else
            bytelength = -2;
        return bytelength;
    }

    protected boolean createBluetoothConnect()
    {
        BluetoothDevice bluetoothdevice;
        if (mbConectOk)
        {
            try {
                misIn.close();
                mosOut.close();
                mbsSocket.close();
            } catch (IOException ioexception1) {
                misIn = null;
                mosOut = null;
                mbsSocket = null;
                mbConectOk = false;
            }
        }
        try {
            bluetoothdevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msBluetoothMAC);
            mbsSocket = bluetoothdevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            mbsSocket.connect();
            saveData("BluetoothMAC", msBluetoothMAC);
            mosOut = mbsSocket.getOutputStream();
            misIn = mbsSocket.getInputStream();
            mbConectOk = true;
            return true;
        } catch (IOException ioexception) {
            msBluetoothMAC = null;
            saveData("BluetoothMAC", ((String) (null)));
            mbConectOk = false;
            return false;
        }
//		}
    }

    protected int getIntData(String s)
    {
        return getSharedPreferences((new Sysinfo(this)).getPackageName(), 0).getInt(s, 0);
    }

    protected String getStrData(String s)
    {
        return getSharedPreferences((new Sysinfo(this)).getPackageName(), 0).getString(s, null);
    }

    protected boolean isConnect()
    {
        return mbConectOk;
    }

    protected void openButetooth()
    {
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
        Toast.makeText(this, getString(R.string.msg_actDiscovery_Bluetooth_Open_Fail), Toast.LENGTH_SHORT).show();
        startActivityForResult(intent, 2);
    }

    protected String readBluetoothMAC()
    {
        return getSharedPreferences((new Sysinfo(this)).getPackageName(), 0).getString("BluetoothMAC", null);
    }

    protected void saveBluetoothMAC(String s)
    {
        android.content.SharedPreferences.Editor editor = getSharedPreferences((new Sysinfo(this)).getPackageName(), 0).edit();
        editor.putString("BluetoothMAC", s);
        editor.commit();
    }

    protected void saveData(String s, int i)
    {
        android.content.SharedPreferences.Editor editor = getSharedPreferences((new Sysinfo(this)).getPackageName(), 0).edit();
        editor.putInt(s, i);
        editor.commit();
    }

    protected void saveData(String s, String s1)
    {
        android.content.SharedPreferences.Editor editor = getSharedPreferences((new Sysinfo(this)).getPackageName(), 0).edit();
        editor.putString(s, s1);
        editor.commit();
    }



    protected void terminateConnect()
    {
        Log.i(TAG, "-----terminateConnect-----");
        if (mbConectOk)
        {
            try
            {
                mbConectOk = false;
                mbsSocket.close();
                misIn.close();
                mosOut.close();
            }
            catch (IOException localIOException)
            {
                misIn = null;
                mosOut = null;
                mbsSocket = null;
            }
        }
    }
}
