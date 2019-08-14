package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.lte.ui.listener.OnItemClickListener;
import com.lte.utils.Constants;

import org.w3c.dom.Text;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class DbmFragment extends SupportFragment implements View.OnClickListener {

    private StationInfo stationInfo;
    private EditText dbmEt;
    private Button save;
    private Button cancel;
    private OnBackPressedListener mActivityListener;

    public static DbmFragment newInstance(StationInfo stationInfo) {
        DbmFragment fragment = new DbmFragment();
        Bundle bundle = new Bundle();
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
        View view = inflater.inflate(R.layout.dbmfragment, container, false);
        init(view);
        return view;
    }
    private void init(View view) {
        Bundle bundle = getArguments();
        stationInfo = bundle.getParcelable(Constants.STATION);
        dbmEt = (EditText) view.findViewById(R.id.dbm);
        save = (Button)view.findViewById(R.id.save);
        cancel = (Button)view.findViewById(R.id.cancel);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
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
        if(stationInfo != null){
            dbmEt.setText(stationInfo.getDBM()+"");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                if(TextUtils.isEmpty(dbmEt.getText().toString())){
                    return;
                }
                try {
                    stationInfo.setDbm(Byte.parseByte(dbmEt.getText().toString()));
                }catch (Exception e){
                    Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                }
                for (StationInfo info : App.get().getMList()) {
                    if(TextUtils.equals(info.getIp(),stationInfo.getIp())){
                        info.setDbm(Byte.parseByte(dbmEt.getText().toString()));
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

    public static DbmFragment newInstance() {
        return new DbmFragment();
    }
}
