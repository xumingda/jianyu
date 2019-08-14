package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lte.R;
import com.lte.ui.fragment.second.CQYFragment;
import com.lte.ui.listener.OnFragmentSelectListener;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleCheckView;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/20.
 */

public class DataReportFragment extends BaseMainFragment implements View.OnClickListener {

    private static final int START_SEND = 1;
    private Button left, right,last;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THREEE = 2;
    private TextView more;

//    private TextView time;

    private TabLayout.Tab tab1;

    private TabLayout.Tab tab2;

    private TabLayout.Tab tab3;

    private TabLayout.Tab tab4;

    private SupportFragment[] mFragments = new SupportFragment[4];
    private OnFragmentSelectListener mActivityListener;
    private Long i = 18L;
    //    private TabLayout mTab;
    private SweetAlertDialog mDialog;
    private boolean isGet;
    private Button start;
    private boolean isStart;
    private Button bt_save;
    private Button bt_clear;
    private Button bt_more;
    private TimePickerDialog mDialogAll;

    protected long mBeginMillseconds = 0L;
    protected long mEndMillseconds = 0L;

    protected static final String BEGIN_TIME_TAG = "begin_time_tag";
    protected static final String END_TIME_TAG = "end_time_tag";

    protected TitleCheckView mBeginTimeTCV;
    protected TitleCheckView mEndTimeTCV;


    public static DataReportFragment newInstance() {
        DataReportFragment fragment = new DataReportFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_report_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        left = (Button) view.findViewById(R.id.left);
        right = (Button) view.findViewById(R.id.right);
        last = (Button) view.findViewById(R.id.last);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        last.setOnClickListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(FirstFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = FirstFragment.newInstance();
            mFragments[SECOND] = TargetHomeFragment.newInstance();
            mFragments[THREEE] = CQYFragment.newInstance();
//            mFragments[FOUR] = SystemFragment.newInstance();
            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THREEE]);
            left.setEnabled(false);
            right.setEnabled(true);
            last.setEnabled(true);
        } else {
            // 这里我们需要拿到mFragments的引用,也可以通过getChildFragmentManager.findFragmentByTag自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(TargetHomeFragment.class);
            mFragments[THREEE] = findChildFragment(CQYFragment.class);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivityListener = (OnFragmentSelectListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IConnectionFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                if (left.isEnabled()) {
                    left.setEnabled(false);
                    right.setEnabled(true);
                    last.setEnabled(true);
                    showHideFragment(mFragments[0]);
                }
                break;
            case R.id.right:
                if (right.isEnabled()) {
                    right.setEnabled(false);
                    left.setEnabled(true);
                    last .setEnabled(true);
                    showHideFragment(mFragments[1]);
                }
                break;
            case R.id.last:{
                if (last.isEnabled()) {
                    last.setEnabled(false);
                    left.setEnabled(true);
                    right .setEnabled(true);
                    showHideFragment(mFragments[2]);
                }
                break;
            }

        }
    }
}
