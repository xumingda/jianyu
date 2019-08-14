package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lte.R;
import com.lte.data.StationInfo;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.utils.Constants;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/25.
 */

public class DeviceRegisterFragment extends SupportFragment{
    public static DeviceRegisterFragment newInstance() {
        return new DeviceRegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.device_register, container, false);
        init(view);
        return view;
    }

    private void init(View view) {

    }
}
