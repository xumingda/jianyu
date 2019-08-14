package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lte.R;
import com.lte.data.StationInfo;
import com.lte.ui.adapter.StationAdapter;
import com.lte.ui.listener.OnItemClickListener;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportFragment;

import static com.lte.utils.Constants.TYPE;

/**
 * Created by chenxiaojun on 2017/9/11.
 */

public class StationFragment extends SupportFragment implements StationAdapter.CheckListener {

    private RecyclerView recyclerView;
    private List<String> mList;
    private StationAdapter mAdapter;
    private OnItemClickListener mActivityListener;

    private int type;

    public static StationFragment newInstance(int type) {
        StationFragment stationFragment = new StationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE,type);
        stationFragment.setArguments(bundle);
        return stationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.station_fragment, container, false);
        init(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivityListener = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IConnectionFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityListener = null;
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.config_list);

        mList = new ArrayList<>();
        type = getArguments().getInt(TYPE);
        Log.d("Station" ,"type :  "+ type);
        if(type == 4){
//            mList.add(getString(R.string.config));

//            mList.add(getString(R.string.dbm_set));

            mList.add(getString(R.string.scan_set));

            mList.add(getString(R.string.cill_config));

            mList.add(getString(R.string.scan_result1));
            //xmd增加一项
            mList.add(getString(R.string.sib5_scan_result1));
        }else if(type == 21 || type == 22 ){
            mList.add(getString(R.string.gsm));

        }else if(type == 23){
            mList.add(getString(R.string.wcda));
        }

        mAdapter = new StationAdapter(getActivity(), mList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view, int position) {
        if(mActivityListener != null){
            if(type == 4){
                mActivityListener.onClick(view,position);
            }else if(type == 21 ){
                mActivityListener.onClick(view,position+5);
            }else if(type == 22){
                mActivityListener.onClick(view,position+6);
            }else if(type == 23){
                mActivityListener.onClick(view,position+7);
            }
        }
    }
}
