package com.lte.ui.fragment.second;

import android.Manifest;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.App;
import com.cleveroad.adaptivetablelayout.OnItemLongClickListener;
import com.communication.utils.LETLog;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.ImsiDataTable;
import com.lte.permissons.RequestCode;
import com.lte.permissons.XPermissionUtils;
import com.lte.ui.adapter.datasource.AnalyDataSource;
import com.lte.ui.adapter.datasource.IdNameEntity;
import com.lte.ui.adapter.datasource.TableDataSource;
import com.lte.ui.widget.DialogManager;
import com.lte.utils.AppUtils;
import com.communication.utils.DateUtil;
import com.lte.utils.ExcelUtils;
import com.lte.utils.FileUtils;
import com.lte.utils.ToastUtils;


import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.lte.utils.AppUtils.copy;
import static com.lte.utils.AppUtils.dip2px;
import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * 历史数据
 */
public class AnalyHistoryFragment extends AnalyBaseFragment {

    private static final String TAG = AnalyHistoryFragment.class.getSimpleName();
    private MaterialSpinner mLocationSpinner;
    private MaterialSpinner mOperatorSpinner;
    private Button export_btn;
    private EditText mImsiET, mImeiET;
    private List<IdNameEntity> mLocationList = new ArrayList<>();
    private List<IdNameEntity> mOperatorList = new ArrayList<>();
    private ArrayList<ArrayList<String>> recordList;
    private static String[] title = {"设备id", "时间", "imsi", "imei", "手机号码", "归属地", "运营商",   "序号"};
    private EditText mMobileET;
    private PopupWindow popupWindow;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mDeviceSpinner.setVisibility(View.GONE);
        mLocationSpinner = (MaterialSpinner) rootView.findViewById(R.id.location_spinner);
        mOperatorSpinner = (MaterialSpinner) rootView.findViewById(R.id.operator_spinner);
        mImsiET = (EditText) rootView.findViewById(R.id.imsi_input_value);
        mImeiET = (EditText) rootView.findViewById(R.id.imei_input_value);
        mMobileET = (EditText) rootView.findViewById(R.id.mobile_input_value);
        export_btn = (Button) rootView.findViewById(R.id.export_btn);
        export_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportExcel();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_history_data_list;
    }

    AnalyDataSource dataSource = new AnalyDataSource();

    @Override
    protected TableDataSource getTableDataSource() {
        return dataSource;
    }

    private int page;

    @Override
    protected void pageLoad(int page) {
        Log.d(TAG, "tempList pageLoad:" + page);
        this.page = page;
        if (tempList != null) {
            int startNum = (page - 1) * pageSize;
            Log.d(TAG, "tempList startNum:" + startNum + " " + tempList.size());
            int i = 0;
            if (startNum < tempList.size()) {
                List<ImsiDataTable> result = tempList.subList(startNum, Math.min(startNum + pageSize, tempList.size()));
                Log.d(TAG, "tempList startNum result:" + result.size());
                dataSource.refreshDataSource(result, tempList.size(), this.page);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void initData() {
        initLocationData();
        initOperatorData();
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int row, int column) {
                if(column >0 && column <4){
                    ImsiDataTable imsiDataTable = null;
                    int index = row;
                    if (index < 0) {
                        index = 0;
                    }
                    if (page != 0) {
                        if (tempList.size() > (index + ((page - 1) * pageSize))) {
                            imsiDataTable = tempList.get(index + ((page - 1) * pageSize));
                        }
                    } else {
                        if (tempList.size() > index) {
                            imsiDataTable = tempList.get(index);
                        }
                    }
                    String text = "";
                    switch (column){
                        case 1:
                            text = imsiDataTable.getImsi();
                            break;
                        case 2:
                            text = imsiDataTable.getImei();
                            break;
                        case 3:
                            text = imsiDataTable.getMobile();
                            break;
                    }
                    Log.d("onLongClick","imsiDataTable :"+column +"-----" + imsiDataTable.toString());
                    getCopy(text);
                }
            }

            @Override
            public void onLeftTopHeaderLongClick() {

            }
        });
    }
    /**
     * popuwindow 复制功能框
     */
    private void getCopy( final String text) {
        View popupWindowView = LayoutInflater
                .from(_mActivity.getApplicationContext()).inflate(
                        R.layout.popupwindow_copy, null);
        popupWindowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                copy(text, getApplicationContext());
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                ToastUtils.showToast(_mActivity,getString(R.string.copy_success),1);
            }
        });
        // 播放动画有一个前提 就是窗体必须有背景
        popupWindow = new PopupWindow(popupWindowView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // popuWindow.setFocusable(true);//PopuWindow获得焦点，点击外面消失才会消失
        popupWindow.setOutsideTouchable(true);// 不能在没有焦点的时候使用

        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // 获取控件Editext在手机中的位置，并设置popupwindow位置
        // 数组长度必须为2
        int[] locations = new int[2];

//        v.getLocationInWindow(locations);
        // v.getLocationOnScreen(locations);
        int x = locations[0];// 获取组件当前位置的横坐标
        int y = locations[1];// 获取组件当前位置的纵坐标

        // 根据手机手机的分辨率 把200dip 转化成 不同的值 px

        int px = dip2px(getApplicationContext(), x / 2);

//        popupWindow.showAtLocation(v, Gravity.TOP + Gravity.LEFT, px, y + 100);
        popupWindow.showAtLocation(mTableLaout,Gravity.CENTER,0,0);

        // 设置动画效果
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(200);
        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(200);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(sa);
        set.addAnimation(aa);
        popupWindowView.startAnimation(set);

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "isVisibleToUser " + isVisibleToUser);
        if (isVisibleToUser) {
            //TODO now it's visible to user

        } else {
            //TODO now it's invisible to user
        }
    }

    private void initHistoryData() {

//        DataManager.getInstance().findImsiByImeiAndImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
//            @Override
//            public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
        tempList = App.get().imsiDataList;
        Log.d(TAG, "initHistoryData" + "---" + tempList.size());
//        Log.d(TAG, "imsiDataTables :" + App.get().imsiDataList.size());
//            }
        upDate();
//        });

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        Log.d(TAG, "onSupportVisible ");
        initHistoryData();
    }

    private void initLocationData() {
        IdNameEntity entity = new IdNameEntity();
        entity.setId("-1");
        entity.setName("请选择地区");
        mLocationList.add(entity);
        for (int i = 0; i < 10; i++) {
            entity = new IdNameEntity();
            entity.setId(String.valueOf(i));
            entity.setName("广州天河体育中心地区" + i);
            mLocationList.add(entity);
        }
        mLocationSpinner.setItems(mLocationList);
        mLocationSpinner.setVisibility(View.GONE);
    }

    private void initOperatorData() {
        IdNameEntity entity = new IdNameEntity();
        entity.setId("-1");
        entity.setName("请选择运营商");
        mOperatorList.add(entity);

        entity = new IdNameEntity();
        entity.setId(String.valueOf(0));
        entity.setName("移动");
        mOperatorList.add(entity);

        entity = new IdNameEntity();
        entity.setId(String.valueOf(1));
        entity.setName("电信");
        mOperatorList.add(entity);

        entity = new IdNameEntity();
        entity.setId(String.valueOf(2));
        entity.setName("联通");
        mOperatorList.add(entity);

        mOperatorSpinner.setItems(mOperatorList);
    }

    List<ImsiDataTable> tempList = null;

    @Override
    protected void search() {
        String imei = null;
        String imsi = null;
        String mobile = null;
        if (mImeiET.getText() != null) {
            imei = mImeiET.getText().toString();
        }
        if (mImsiET.getText() != null) {
            imsi = mImsiET.getText().toString();
        }
        if (mMobileET.getText() != null) {
            mobile = mMobileET.getText().toString();
        }
        LETLog.d("IMEI " + imei +"----imsi :" +imsi + "mobile "+ mobile +
        "---" + mOperatorSpinner.getSelectedIndex() +"--mEndMillseconds-"+mEndMillseconds+
        "mBeginMillseconds --" + mBeginMillseconds);
        tempList = new ArrayList<>();
        final String finalImsi = imsi;
        final String finalImei = imei;
        final String finalMobile = mobile;
        if (mOperatorSpinner.getSelectedIndex() == 0) {
            if (mEndMillseconds > 0) {//只选结束时间或者两个都选
                if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        ){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            tempList = imsiDataTables.sort("time", Sort.DESCENDING);
                            upDate();
                        }
                    });
                }
            } else if (mBeginMillseconds > 0) {//只选开始时间，结束时间默认当前时间
                mEndMillseconds = System.currentTimeMillis();
                if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if( imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) ){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else {
                    DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            tempList = imsiDataTables.sort("time", Sort.DESCENDING);
                            upDate();
                        }
                    });
                }
            } else {//不选时间
                if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData( new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData( new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) &&imsiDataTable.getImei() != null && imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei)) {
                    DataManager.getInstance().findImsiData( new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if( imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi)) {
                    Log.d(TAG, "tempList size:" + "%" + imsi + "%");
                    DataManager.getInstance().findImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) ){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else {
                    Log.d(TAG, "Thread :" + Thread.currentThread().toString());
                    DataManager.getInstance().findImsiData(new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            Log.d(TAG, "imsiDataTables :" + imsiDataTables.size());
                            tempList = imsiDataTables.sort("time", Sort.DESCENDING);
                            upDate();
                        }
                    });
                }
            }
        } else {
            String operator = mOperatorList.get(mOperatorSpinner.getSelectedIndex()).getName();
            if (mEndMillseconds > 0) {//只选结束时间或者两个都选
                if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi)&&imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                       ){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            tempList = imsiDataTables;
                            upDate();
                        }
                    });
                }

            } else if (mBeginMillseconds > 0) {//只选开始时间，结束时间默认当前时间
                mEndMillseconds = System.currentTimeMillis();
                if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile()!= null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)&&
                                        imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) &&
                                        imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) ){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else {
                    DataManager.getInstance().findImsiByOperatorData(mBeginMillseconds, mEndMillseconds, operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            tempList = imsiDataTables.sort("time", Sort.DESCENDING);
                            upDate();
                        }
                    });
                }
            } else {//没选时间
                if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(operator,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if( imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)
                                        &&imsiDataTable.getMobile() != null &&imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(mobile)) {
                    DataManager.getInstance().findImsiByOperatorData(operator,  new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) &&
                                        imsiDataTable.getMobile() != null && imsiDataTable.getMobile().contains(finalMobile)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei) && !TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiByOperatorData(operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) && imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imei)) {
                    DataManager.getInstance().findImsiByOperatorData(operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if( imsiDataTable.getImei() != null &&imsiDataTable.getImei().contains(finalImei)){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else if (!TextUtils.isEmpty(imsi)) {
                    DataManager.getInstance().findImsiByOperatorData(operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            RealmResults<ImsiDataTable> time = imsiDataTables.sort("time", Sort.DESCENDING);
                            for (ImsiDataTable imsiDataTable : time) {
                                if(imsiDataTable.getImsi().contains(finalImsi) ){
                                    tempList.add(imsiDataTable);
                                }
                            }
                            upDate();
                        }
                    });
                } else {
                    DataManager.getInstance().findImsiByOperatorData(operator, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                        @Override
                        public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                            tempList = imsiDataTables.sort("time", Sort.DESCENDING);
                            upDate();
                        }
                    });
                }
            }
            if (tempList != null) {
                Log.d(TAG, "operator:" + operator + " tempList size:" + tempList.size());
            }
        }

    }

    private void upDate() {

        if (tempList != null && tempList.size() > 0) {
            mNavPageView.initData(tempList.size(), pageSize);
            int startNum = 0;
            Log.d(TAG, "tempList startNum:" + startNum);
            int i = 0;
            if (startNum < tempList.size()) {
                List<ImsiDataTable> result = tempList.subList(startNum, Math.min(startNum + pageSize, tempList.size()));
                dataSource.refreshDataSource(result, tempList.size(), ++i);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Log.d(TAG, "tempList ==null");
            dataSource.refreshDataSource(null, 0, 0);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 导出excel
     */
    public void exportExcel() {
        if (tempList == null || tempList.size() == 0) {
            AppUtils.showToast(mContext, "请先查询数据");
            return;
        }
        XPermissionUtils.requestPermissions(mContext, RequestCode.EXTERNAL, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new XPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        XPermissionUtils.releaseOnPermissionListener();
                        FileUtils.createDir(FileUtils.getDataDownloadPath(mContext));
                        String filePath = FileUtils.getDataDownloadPath(mContext) + "/数据" + System.currentTimeMillis() + ".xls";
                        ExcelUtils.initExcel(filePath, title);
                        ExcelUtils.writeObjListToExcel(getRecordData(), filePath, mContext);
                    }

                    @Override
                    public void onPermissionDenied(final String[] deniedPermissions, boolean alwaysDenied) {
                        if (alwaysDenied) {
                            XPermissionUtils.releaseOnPermissionListener();
                            DialogManager.showGoToSysSettingDialog(mContext, "权限申请", mContext.getResources().getString(R.string.app_name) + "需要读取文件");
                        } else {
                            DialogManager.showAlertDialog(mContext, "权限申请", mContext.getResources().getString(R.string.app_name) + "需要读取文件", false, "确定", new DialogManager.IClickListener() {
                                @Override
                                public boolean click(Dialog dlg, View view) {
                                    XPermissionUtils.requestPermissionsAgain(mContext, deniedPermissions, RequestCode.EXTERNAL);
                                    return true;
                                }
                            }, "取消", new DialogManager.IClickListener() {
                                @Override
                                public boolean click(Dialog dlg, View view) {
                                    XPermissionUtils.releaseOnPermissionListener();
                                    return true;
                                }
                            });
                        }
                    }
                });

    }

    /**
     * 将数据集合 转化成ArrayList<ArrayList<String>>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> getRecordData() {
        if (tempList == null || tempList.size() == 0) {
            return null;
        }
        recordList = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            ImsiDataTable analyEntity = tempList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(App.get().DevNumber);
            beanList.add(DateUtil.formatTime(analyEntity.getTime()));
            beanList.add(analyEntity.getImsi());
            beanList.add(analyEntity.getImei() == null ? "" : analyEntity.getImei());
            beanList.add(analyEntity.getMobile());
            beanList.add(analyEntity.getSource());
            beanList.add(analyEntity.getOperator());
            beanList.add((tempList.size() - i) + "");
//            beanList.add(analyEntity.lat);
//            beanList.add(analyEntity.lng);
            recordList.add(beanList);
        }
        return recordList;
    }

    public static AnalyHistoryFragment newInstance() {
        return new AnalyHistoryFragment();
    }
}
