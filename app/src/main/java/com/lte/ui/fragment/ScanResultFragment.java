package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.StationInfo;
import com.lte.ui.adapter.DataReportOneAdapter;
import com.lte.ui.adapter.ScanResultAdapter;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.utils.Constants;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class ScanResultFragment extends SupportFragment {

    private static final String TAG = "ScanResultFragment";
    private Long id;
    private RecyclerView scanResult;
    private ScanResultAdapter mAdapter;

    public static ScanResultFragment newInstance(Long ip) {
        ScanResultFragment fragment = new ScanResultFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID,ip);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scan_result_fragment, container, false);
        init(view);
        return view;
    }
    private void init(View view) {
        Bundle bundle = getArguments();
        id = bundle.getLong(Constants.ID);
        scanResult = (RecyclerView) view.findViewById(R.id.scan_result);

        Log.d(TAG,"ID :" +id);
        mAdapter = new ScanResultAdapter(_mActivity, DataManager.getInstance().findScanResult(id),scanResult);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);

        scanResult.setLayoutManager(linearLayoutManager);

        scanResult.setAdapter(mAdapter);
    }

    public static ScanResultFragment newInstance() {
        return new ScanResultFragment();
    }
}
