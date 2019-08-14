package com.lte.utils;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by Yang on 2017/7/6.
 */

public class BluetoothUtils {
    private static BluetoothAdapter mBT = null;
    public BluetoothUtils(){
        synchronized (BluetoothUtils.class) {
            if (mBT == null) {
                mBT = BluetoothAdapter.getDefaultAdapter();
            }
        }
    }

    // 开启蓝牙
    public void BluetoothOpen() {
        if (!mBT.isEnabled()) {
            mBT.enable();
        }
    }

    // 关闭蓝牙
    public void BluetoothClose() {
        if (mBT.isEnabled()) {
            mBT.disable();
        }
    }

    public static BluetoothAdapter getmBT(){
        if (mBT == null) {
            mBT = BluetoothAdapter.getDefaultAdapter();
            return mBT;
        }else{
            return mBT;
        }
    }

    public boolean isBluetoothOpen() {
        return mBT != null && mBT.isEnabled();
    }
}
