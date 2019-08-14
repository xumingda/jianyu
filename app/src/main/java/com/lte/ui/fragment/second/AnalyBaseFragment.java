package com.lte.ui.fragment.second;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.SceneTable;
import com.lte.ui.adapter.AnalyTableAdapter;
import com.lte.ui.adapter.SceneDialogAdapter;
import com.lte.ui.adapter.datasource.IdNameEntity;
import com.lte.ui.adapter.datasource.TableDataSource;
import com.lte.ui.widget.NavigationPageView;
import com.lte.ui.widget.TitleCheckView;
import com.lte.utils.AppUtils;
import com.communication.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.RealmResults;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * 统计分析fragment基类
 */
public abstract class AnalyBaseFragment extends SupportFragment implements View.OnClickListener, OnDateSetListener, SceneDialogAdapter.CheckListener, AnalyTableAdapter.CheckListener {

    protected Context mContext;
    protected int pageSize = 20;
    protected AdaptiveTableLayout mTableLaout;
    protected AnalyTableAdapter mAdapter;
    protected TableDataSource mDataSource;
    protected long mBeginMillseconds = 0L;
    protected long mEndMillseconds = 0L;

    protected Button addScenButton;

    protected MaterialSpinner mDeviceSpinner;
    protected TitleCheckView mBeginTimeTCV;
    protected TitleCheckView mEndTimeTCV;

    protected TimePickerDialog mDialogAll;

    protected ExpandableLinearLayout mExpandableLayout;
    protected RelativeLayout mExpandButton;
    protected View mRotateView;
    protected NavigationPageView mNavPageView;

    protected List<IdNameEntity> mDeviceInfoList = new ArrayList<>();
    private SparseBooleanArray expandState = new SparseBooleanArray();

    protected static final String BEGIN_TIME_TAG = "begin_time_tag";
    protected static final String END_TIME_TAG = "end_time_tag";
    protected AlertDialog sceneDialog;
    protected RecyclerView scene_List;
    protected RealmResults<SceneTable> mSceneList;
    protected SceneDialogAdapter sceneAdapter;

    public AtomicReference<String> localProgressFlag;


    protected abstract int getLayoutResourceId();

    protected abstract TableDataSource getTableDataSource();

    protected abstract void pageLoad(int page);

    protected TimesAdapter timeAdapter;


    protected AlertDialog timesDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResourceId(), container, false);
        initView(rootView);
        initTimeDialog();
        initSceneDialog();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();

    }
    private void initSceneDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_scene_list, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.UIdialog_trans);
        btn_select_cancel = (Button) view.findViewById(R.id.btn_select_cancel);
        btn_select_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sceneDialog.dismiss();
            }
        });
        builder.setView(view);
        sceneDialog = builder.create();
        Window window = sceneDialog.getWindow();
        if (window != null)
            window.setGravity(Gravity.BOTTOM);
        scene_List = (RecyclerView) view.findViewById(R.id.scene_list);
        mSceneList = DataManager.getInstance().findSceneList();
        Log.d("DataManager", "mSceneList :" + mSceneList.size());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mActivity);
        scene_List.setLayoutManager(linearLayoutManager);
        sceneAdapter = new SceneDialogAdapter(_mActivity, mSceneList, this, scene_List);
        scene_List.setAdapter(sceneAdapter);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataSource = getTableDataSource();
        initAdapter();
        initDeviceInfo();
        initData();
    }
    TextView textView;
    Button btn_select_cancel;
    protected void initAdapter() {
        mAdapter = new AnalyTableAdapter(mContext, mDataSource,this);
        mTableLaout.setAdapter(mAdapter);
        View view2 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_scene_type_list, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity(), R.style.UIdialog_trans);
        textView = (TextView) view2.findViewById(R.id.clear_cache_title);
        btn_select_cancel = (Button) view2.findViewById(R.id.btn_select_cancel);
        btn_select_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timesDialog.dismiss();
            }
        });
        builder2.setView(view2);
        timesDialog = builder2.create();
        Window window = timesDialog.getWindow();
        if (window != null)
            window.setGravity(Gravity.BOTTOM);
        lv_deviceTypeList = (ListView) view2.findViewById(R.id.lv_device_type);

        times = new ArrayList<>();
        timeAdapter = new TimesAdapter(times);
        lv_deviceTypeList.setAdapter(timeAdapter);
    }
    public void setTitile(String titile){
        if(textView != null){
            textView.setText(titile);
        }
    }
    protected void initDeviceInfo() {
        IdNameEntity entity = new IdNameEntity();
        entity.setId("-1");
        entity.setName("请选择设备");
        mDeviceInfoList.add(entity);
        for (int i = 0; i < 10; i++) {
            entity = new IdNameEntity();
            entity.setId(String.valueOf(i));
            entity.setName("广州天河体育中心地区" + i);
            mDeviceInfoList.add(entity);
        }
        mDeviceSpinner.setItems(mDeviceInfoList);
    }

    protected void initData() {

    }

    private ListView lv_deviceTypeList;
    private ArrayList<String> times;

    protected void upBasedate(ArrayList<String> list){
        timeAdapter.updateData(list);
    }

    @Override
    public void onClick(View view, SceneTable imsiData) {

    }

    @Override
    public void onLongClick(View view, String text) {
        Log.d("onLongClick","view " +text);
    }

    private class TimesAdapter extends BaseAdapter {

        private ArrayList<String> data;

        public TimesAdapter(ArrayList<String> data) {
            this.data = data;
        }

        public void updateData(ArrayList<String> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_type_list, null);
                viewHolder.textView = (TextView) view.findViewById(R.id.tv_type_text);
                viewHolder.top = (View) view.findViewById(R.id.devider_top);
                viewHolder.bottom = (View) view.findViewById(R.id.devider_bottom);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.textView.setText(data.get(i));
            if (i == data.size() - 1) {
                viewHolder.bottom.setVisibility(View.VISIBLE);
            } else {
                viewHolder.bottom.setVisibility(View.GONE);
            }

            return view;
        }

        class ViewHolder {
            TextView textView;
            View top;
            View bottom;
        }
    }
    protected void initView(View rootView) {
        mTableLaout = (AdaptiveTableLayout) rootView.findViewById(R.id.tableLayout);
        mDeviceSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner);
        mDeviceSpinner.setVisibility(View.GONE);
        mBeginTimeTCV = (TitleCheckView) rootView.findViewById(R.id.begin_time_input_value);
        mBeginTimeTCV.setCheckText("开始时间");
        mBeginTimeTCV.setOnClickListener(this);
        mEndTimeTCV = (TitleCheckView) rootView.findViewById(R.id.end_time_input_value);
        mEndTimeTCV.setCheckText("结束时间");
        mEndTimeTCV.setOnClickListener(this);
        Button mSearchBtn = (Button) rootView.findViewById(R.id.search_btn);
        mSearchBtn.setOnClickListener(this);
        mExpandableLayout = (ExpandableLinearLayout) rootView.findViewById(R.id.expandableLayout);
        mRotateView = rootView.findViewById(R.id.expand_rotate);
        mExpandButton = (RelativeLayout) rootView.findViewById(R.id.expand_button);
        mExpandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(mRotateView, 0f, 180f).start();
                expandLayoutOpenListener();
                expandState.put(1, true);
            }

            @Override
            public void onPreClose() {
                expandLayoutCloseListener();
                createRotateAnimator(mRotateView, 180f, 0f).start();
                expandState.put(1, false);
            }
        });
        mRotateView.setRotation(expandState.get(1) ? 180f : 0f);
        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mExpandableLayout.toggle();
            }
        });

        mNavPageView = (NavigationPageView) rootView.findViewById(R.id.navpage_layout);
        mNavPageView.setOnPageButtonClickLister(new NavigationPageView.OnPageButtonClickLister() {
            @Override
            public void pageClick(int page) {
                pageLoad(page);
            }
        });
    }

    protected void initTimeDialog() {
        long fiveYears = 5L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis() - fiveYears)
                .setMaxMillseconds(System.currentTimeMillis() + fiveYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorAccent))
                .setWheelItemTextSize(14)
                .build();

    }

    protected void expandLayoutOpenListener() {

    }

    protected void expandLayoutCloseListener() {

    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String tag = timePickerView.getTag();
        if (BEGIN_TIME_TAG.equals(tag)) {
            if (millseconds >= mEndMillseconds && mEndMillseconds != 0) {
                AppUtils.showToast(getActivity(), "开始时间必须小于结束时间");
                return;

            }
            mBeginMillseconds = millseconds;
            mBeginTimeTCV.setCheckText(DateUtil.formatTime(millseconds));
        } else if (END_TIME_TAG.equals(tag)) {
            Log.d("AnalyFollowFragment","onDateSet :" + millseconds + mBeginMillseconds);
            if (millseconds <= mBeginMillseconds) {
                AppUtils.showToast(getActivity(), "结束时间必须大于开始时间");
                return;

            }
            mEndMillseconds = millseconds;
            mEndTimeTCV.setCheckText(DateUtil.formatTime(millseconds));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin_time_input_value:
                mDialogAll.show(getFragmentManager(), BEGIN_TIME_TAG);
                break;
            case R.id.end_time_input_value:
                mDialogAll.show(getFragmentManager(), END_TIME_TAG);
                break;
            case R.id.search_btn:
                search();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected abstract void search();
}
