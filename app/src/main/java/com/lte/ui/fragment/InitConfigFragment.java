package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.InitConfig;
import com.lte.data.StationInfo;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.utils.Constants;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/11.
 */

public class InitConfigFragment extends SupportFragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private RadioGroup bandwidth, synchronous_mode, frequency_offset;

    private EditText time_delay, operating_band;

    private TextView upgrade, cancel;

    private InitConfig initConfig;

    private StationInfo stationInfo;
    private OnBackPressedListener mActivityListener;

    public static InitConfigFragment newInstance(InitConfig initConfig, StationInfo stationInfo) {
        InitConfigFragment fragment = new InitConfigFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.INIT_CONFIG, initConfig);
        bundle.putParcelable(Constants.STATION,stationInfo);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivityListener = (OnBackPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IConnectionFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityListener = null;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.init_config_dialog, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        Bundle bundle = getArguments();
        stationInfo = bundle.getParcelable(Constants.STATION);
        bandwidth = (RadioGroup) view.findViewById(R.id.bandwidth);
        synchronous_mode = (RadioGroup) view.findViewById(R.id.synchronous_mode);
        frequency_offset = (RadioGroup) view.findViewById(R.id.frequency_offset);
        time_delay = (EditText) view.findViewById(R.id.time_delay);
        operating_band = (EditText) view.findViewById(R.id.operating_band);
        upgrade = (TextView) view.findViewById(R.id.upgrade);
        cancel = (TextView) view.findViewById(R.id.cancel);
        if(stationInfo == null){
            for (StationInfo info : App.get().getMList()) {
                if(TextUtils.equals(info.getIp(),App.get().Ip)){
                    stationInfo = info;
                }
            }
        }else {
            for (StationInfo info : App.get().getMList()) {
                if(TextUtils.equals(info.getIp(),stationInfo.getIp())){
                    stationInfo = info;
                }
            }
        }
        initConfig = stationInfo.getInitConfig();
        if(initConfig != null){
            switch (initConfig.getBandwidth()){
                case 2:
                    bandwidth.check(R.id.m5);
                    break;
                case 0:
                    bandwidth.check(R.id.m10);
                    break;
                case 1:
                    bandwidth.check(R.id.m20);
                    break;
            }
            time_delay.setText(initConfig.getTimeDelayField()+"");
            switch (initConfig.getSynchronousMode()){
                case 0:
                    synchronous_mode.check(R.id.cnm);
                    break;
                case 1:
                    synchronous_mode.check(R.id.gps);
                    break;
                case 2:
                    synchronous_mode.check(R.id.mix);
                    break;
                case 3:
                    synchronous_mode.check(R.id.nmm);
                    break;
            }
            switch (initConfig.getFrequencyOffset()){
                case 0:
                    frequency_offset.check(R.id.yes);
                    break;
                case 1:
                    frequency_offset.check(R.id.no);
            }
            operating_band.setText(initConfig.getOperatingBand()+"");
        }else {
            initConfig = new InitConfig();
            initConfig.setId(stationInfo.getId());
            initConfig.setIp(stationInfo.getIp());
        }
        bandwidth.setOnCheckedChangeListener(this);
        synchronous_mode.setOnCheckedChangeListener(this);
        frequency_offset.setOnCheckedChangeListener(this);
        upgrade.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.bandwidth:
                switch (checkedId){
                    case R.id.m5:
                        initConfig.setBandwidth(2);
                        break;
                    case R.id.m10:
                        initConfig.setBandwidth(0);
                        break;
                    case R.id.m20:
                        initConfig.setBandwidth(1);
                        break;
                }
            case R.id.synchronous_mode:
                switch (checkedId){
                    case R.id.cnm:
                        initConfig.setSynchronousMode(0);
                        break;
                    case R.id.gps:
                        initConfig.setSynchronousMode(1);
                        break;
                    case R.id.mix:
                        initConfig.setSynchronousMode(2);
                        break;
                    case R.id.nmm:
                        initConfig.setSynchronousMode(3);
                        break;
                }
            case R.id.frequency_offset:
                switch (checkedId){
                    case R.id.yes:
                        initConfig.setFrequencyOffset(0);
                        break;
                    case R.id.no:
                        initConfig.setFrequencyOffset(1);
                        break;
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upgrade:
                if(time_delay.getText() != null && !TextUtils.equals(time_delay.getText().toString(),"")){
                    try {
                        initConfig.setTimeDelayField(Integer.parseInt(time_delay.getText().toString()));
                    }catch (Exception e){
                        Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                    }
                }
                if(operating_band.getText() != null && !TextUtils.equals(operating_band.getText().toString(),"")){
                    try {
                        initConfig.setOperatingBand(Integer.parseInt(operating_band.getText().toString()));
                    }catch (Exception e){
                        Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                    }
                }

                initConfig.setId(stationInfo.getId());
                Log.d("Station", "Station :" + stationInfo.getId() );
                stationInfo.setInitConfig(initConfig);
                for (StationInfo info : App.get().getMList()) {
                    if(TextUtils.equals(info.getIp(),stationInfo.getIp())){
                        info.setInitConfig(initConfig);
                    }
                }
                DataManager.getInstance().createOrUpdateStation(stationInfo);
                mActivityListener.onBack();
                break;
            case R.id.cancel:
                mActivityListener.onBack();
                break;
        }
    }

    public static InitConfigFragment newInstance() {
        return new InitConfigFragment();
    }
}
