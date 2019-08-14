package com.lte.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.lte.R;
import com.lte.ui.activity.BlackListActivity;
import com.lte.ui.activity.BlackResultActivity;
import com.lte.ui.activity.DataProcessActivity;
import com.lte.ui.activity.QueryMobileActivity;
import com.lte.ui.activity.WhiteListActivity;
import com.lte.ui.widget.SwitchButton;
import com.lte.utils.SharedPreferencesUtil;

import static com.lte.utils.Constants.TYPE;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class DataProcessFragment extends BaseMainFragment implements View.OnClickListener {


    public static DataProcessFragment newInstance() {
        DataProcessFragment fragment = new DataProcessFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_process_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        RelativeLayout relativeLayout2 = (RelativeLayout) view.findViewById(R.id.layout_two);
        relativeLayout2.setOnClickListener(this);
        RelativeLayout relativeLayout1 = (RelativeLayout) view.findViewById(R.id.layout_one);
        relativeLayout1.setOnClickListener(this);
        RelativeLayout relativeLayout3 = (RelativeLayout) view.findViewById(R.id.layout_three);
        relativeLayout3.setOnClickListener(this);
        RelativeLayout relativeLayout4 = (RelativeLayout) view.findViewById(R.id.layout_four);
        relativeLayout4.setOnClickListener(this);
        RelativeLayout relativeLayout5 = (RelativeLayout) view.findViewById(R.id.layout_five);
        relativeLayout5.setOnClickListener(this);
        RelativeLayout relativeLayout6 = (RelativeLayout) view.findViewById(R.id.layout_sixe);
        relativeLayout6.setOnClickListener(this);
        RelativeLayout relativeLayout7 = (RelativeLayout) view.findViewById(R.id.layout_seven);
        relativeLayout7.setOnClickListener(this);
        RelativeLayout relativeLayout8 = (RelativeLayout) view.findViewById(R.id.layout_eight);
        relativeLayout8.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_one: {
                Intent intent = new Intent(_mActivity, DataProcessActivity.class);
                intent.putExtra(TYPE,1);
                startActivity(intent);
                break;
            }
            case R.id.layout_two: {
                Intent intent = new Intent(_mActivity, DataProcessActivity.class);
                intent.putExtra(TYPE,2);
                startActivity(intent);
                break;
            }
            case R.id.layout_three: {
                Intent intent = new Intent(_mActivity, DataProcessActivity.class);
                intent.putExtra(TYPE,3);
                startActivity(intent);
                break;
            }
            case R.id.layout_four: {
                Intent intent = new Intent(_mActivity, DataProcessActivity.class);
                intent.putExtra(TYPE,4);
                startActivity(intent);
                break;
            }
            case R.id.layout_five:{
                Intent intent = new Intent(_mActivity, BlackListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_sixe:{
                Intent intent = new Intent(_mActivity, WhiteListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_seven:{
                Intent intent = new Intent(_mActivity, BlackResultActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.layout_eight:{
                Intent intent = new Intent(_mActivity, QueryMobileActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
