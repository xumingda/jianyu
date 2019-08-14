package com.lte.ui.fragment.second;

import android.Manifest;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.App;
import com.cleveroad.adaptivetablelayout.OnItemClickListener;
import com.communication.utils.LETLog;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.SceneTable;
import com.lte.permissons.RequestCode;
import com.lte.permissons.XPermissionUtils;
import com.lte.ui.adapter.datasource.AnalyDataSource;
import com.lte.ui.adapter.datasource.TableDataSource;
import com.lte.ui.tagview.Tag;
import com.lte.ui.tagview.TagView;
import com.lte.ui.widget.DialogManager;
import com.lte.utils.AppUtils;
import com.communication.utils.DateUtil;
import com.lte.utils.ExcelUtils;
import com.lte.utils.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * 伴随分析
 */
public class AnalyFollowFragment extends AnalyBaseFragment {


    private static final String TAG = AnalyFollowFragment.class.getSimpleName();
    private EditText mFollowTimeET;
    private EditText mImsiET;
    private Button mAddButton;
    private TagView mTagGroupView;
    private static String[] title = {"出现次数",  "imsi",  "imei","手机号码", "归属地", "运营商","序号"};
    private Button export_btn;

    private int page =1;
    private MaterialSpinner collison_type;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_analyfollow;
    }

    AnalyDataSource dataSource = new AnalyDataSource();

    @Override
    protected TableDataSource getTableDataSource() {
        dataSource.type = 1;
        dataSource.mColumnHeaderData = new String[]{"设备ID",  "imsi",
                "手机号码", "归属地", "运营商","序号"};
        return dataSource;
    }

    @Override
    protected void pageLoad(int page) {
        Log.d(TAG, "tempList pageLoad:" + page);
        this.page = page;
        if (finalList != null) {
            int startNum = (page - 1) * pageSize;
            Log.d(TAG, "tempList startNum:" + startNum);
            if (startNum < finalList.size()) {
                List<ImsiDataTable> result = finalList.subList(startNum, Math.min(startNum + pageSize, finalList.size()));
                Log.d(TAG, "tempList startNum result:" + result.size());
                dataSource.refreshDataSource(result,finalList.size(),this.page);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mFollowTimeET = (EditText) rootView.findViewById(R.id.time_input_value);
        collison_type = (MaterialSpinner) rootView.findViewById(R.id.collison_type);
        List<String> items = new ArrayList<>();
        items.add("imsi");
        items.add("imei");
        collison_type.setItems(items);
        collison_type.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                switch (position){
                    case 0:
                        dataSource.type = 1;
                        dataSource.mColumnHeaderData = new String[]{"设备ID",  "imsi",
                                "手机号码", "归属地", "运营商","序号"};
                        mAdapter.upDateDataSource(dataSource);
                        break;
                    case 1:
                        dataSource.type = 2;
                        dataSource.mColumnHeaderData = new String[]{"设备ID",  "imei",
                                "手机号码", "归属地", "运营商","序号"};
                        mAdapter.upDateDataSource(dataSource);
                        break;
                }
            }
        });
        mImsiET = (EditText) rootView.findViewById(R.id.imsi_input_value);
        mAddButton = (Button) rootView.findViewById(R.id.add_button);
        mTagGroupView = (TagView) rootView.findViewById(R.id.tag_group);
        addScenButton = (Button) rootView.findViewById(R.id.add_button1);
        addScenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sceneDialog.show();
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSearchCondition();
            }
        });
        mTagGroupView.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                view.remove(position);
            }
        });
        export_btn = (Button) rootView.findViewById(R.id.export_btn);
        export_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportExcel();
            }
        });
    }

    //添加搜索条件
    private void addSearchCondition() {
        if (mBeginMillseconds == 0 || mEndMillseconds == 0) {
            AppUtils.showToast(getActivity(), "请添加搜索条件");
            return;
        }
        if (mTagGroupView.getTags().size() != 0) {
            for (Tag tag : mTagGroupView.getTags()) {
                if ((mBeginMillseconds <= tag.getBeginTime() && mEndMillseconds > tag.getBeginTime())
                        || (mBeginMillseconds >= tag.getBeginTime() && mBeginMillseconds < tag.getEndTime())) {
                    AppUtils.showToast(getActivity(), "时间与已添加时间重叠，请重新添加");
                    return;
                }
            }
        }
        String condition = ("时间：");
        condition += DateUtil.formatTime(mBeginMillseconds);
        condition += "至";
        condition += DateUtil.formatTime(mEndMillseconds);
        Tag tag = new Tag(condition);
        tag.setBeginTime(mBeginMillseconds);
        tag.setEndTime(mEndMillseconds);
        tag.radius = 10f;
        tag.isDeletable = true;
        tag.value = mBeginMillseconds + ";" + mEndMillseconds;
        mTagGroupView.addTag(tag);
    }

    @Override
    protected void initData() {
        super.initData();
        setTitile(getString(R.string.follow_tme));
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int row, int column) {
                Log.d(TAG,"row :" +row);
                ImsiDataTable imsiDataTable = null;
                int index = row-1;
                if(index <0){
                   index = 0 ;
                }
                if (page != 0) {
                    if(finalList.size()>(index + ((page - 1) * pageSize))){
                        imsiDataTable = finalList.get(index + ((page - 1) * pageSize));
                    }
                } else {
                    if(finalList.size() > index){
                        imsiDataTable = finalList.get(index);
                    }
                }
                if(imsiDataTable != null){
//                    ArrayList<String> list = new ArrayList<>();
//                    for (ImsiDataTable dataTable : mResult) {
//                        if (TextUtils.equals(dataTable.getImsi(), imsiDataTable.getImsi())) {
//                            list.add(DateUtil.formatTime(dataTable.getTime()));
//                        }
//                    }
                    if(resultTime.get(imsiDataTable.getImsi()) != null){
                        upBasedate(resultTime.get(imsiDataTable.getImsi()));
                    }
                    timesDialog.show();
                }

            }

            @Override
            public void onRowHeaderClick(int row) {
                Log.d(TAG, "onItemClick :" + row);
                ImsiDataTable imsiDataTable = null;
                int index = row-1;
                if(index <0){
                    index = 0 ;
                }
                if (page != 0) {
                    if(finalList.size()>(index + ((page - 1) * pageSize))){
                        imsiDataTable = finalList.get(index + ((page - 1) * pageSize));
                    }
                } else {
                    if(finalList.size() > index){
                        imsiDataTable = finalList.get(index);
                    }
                }
                if(imsiDataTable != null){
//                    ArrayList<String> list = new ArrayList<>();
//                    for (ImsiDataTable dataTable : mResult) {
//                        if (TextUtils.equals(dataTable.getImsi(), imsiDataTable.getImsi())) {
//                            list.add(DateUtil.formatTime(dataTable.getTime()));
//                        }
//                    }
//                    upBasedate(list);
                    if(resultTime.get(imsiDataTable.getImsi()) != null){
                        upBasedate(resultTime.get(imsiDataTable.getImsi()));
                    }
                    timesDialog.show();
                }
            }

            @Override
            public void onColumnHeaderClick(int column) {

            }

            @Override
            public void onLeftTopHeaderClick() {

            }
        });
    }

    List<ImsiDataTable> finalList = new ArrayList<ImsiDataTable>();
    int num = 0;

    @Override
    protected void search() {
        final List<Tag> tags = mTagGroupView.getTags();
        if (tags.isEmpty()) {
            AppUtils.showToast(getActivity(), "请添加查询时间！");
            return;
        }
        if (TextUtils.isEmpty(mImsiET.getText().toString())) {
            AppUtils.showToast(getActivity(), "请输入imsi！");
            return;
        }
        if (TextUtils.isEmpty(mFollowTimeET.getText().toString())) {
            AppUtils.showToast(getActivity(), "请添加伴随时长！");
            return;
        }
        finalList.clear();
        num = 0;
        final long halfFollowTime = Long.valueOf(mFollowTimeET.getText().toString())  == 0 ? 1000L : (Long.valueOf(mFollowTimeET.getText().toString())) * 1000L;
        final List<ImsiDataTable> list = new ArrayList<ImsiDataTable>();
        if(collison_type.getSelectedIndex() == 0){
            for (Tag tag : tags) {
                Log.d(TAG, "------查询条件：" + tag.value);
                String[] time = tag.value.split(";");
                Long mBeginMillseconds = Long.valueOf(time[0]);
                Long mEndMillseconds = Long.valueOf(time[1]);
                Log.d(TAG, "mBeginMillseconds:" + mBeginMillseconds + " mEndMillseconds:" + mEndMillseconds + " imsi:" + String.valueOf(mImsiET.getText().toString()));
//            List<AnalyEntity> tempList = new DefaultDAO(getActivity()).queryToModel(AnalyEntity.class, true, "time>? and time<? and imsi=?", new String[]{mBeginMillseconds,mEndMillseconds,String.valueOf(mImsiET.getText().toString())}, "",null);
                DataManager.getInstance().findImsiByImsiData(mBeginMillseconds, mEndMillseconds, String.valueOf(mImsiET.getText().toString()), new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                    @Override
                    public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                        final List<ImsiDataTable> tempList = imsiDataTables;
                        LETLog.d("tempList :"+ tempList.size()  +"----"+System.currentTimeMillis());
                        if (tempList.size() > 0) {
                            list.addAll(tempList);
                            ++num;
                            if (num == tags.size() && list.size() > 1) {
                                upDate(list, halfFollowTime);
                            }
                        }
                    }
                });

            }
        }else {
            for (Tag tag : tags) {
                Log.d(TAG, "------查询条件：" + tag.value);
                String[] time = tag.value.split(";");
                Long mBeginMillseconds = Long.valueOf(time[0]);
                Long mEndMillseconds = Long.valueOf(time[1]);
                Log.d(TAG, "mBeginMillseconds:" + mBeginMillseconds + " mEndMillseconds:" + mEndMillseconds + " imsi:" + String.valueOf(mImsiET.getText().toString()));
//            List<AnalyEntity> tempList = new DefaultDAO(getActivity()).queryToModel(AnalyEntity.class, true, "time>? and time<? and imsi=?", new String[]{mBeginMillseconds,mEndMillseconds,String.valueOf(mImsiET.getText().toString())}, "",null);
                DataManager.getInstance().findImsiByImeiData(mBeginMillseconds, mEndMillseconds, String.valueOf(mImsiET.getText().toString()), new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                    @Override
                    public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                        final List<ImsiDataTable> tempList = imsiDataTables;
                        if (tempList != null && tempList.size() > 0) {
                            list.addAll(tempList);
                            ++num;
                            if (num == tags.size() && list.size() > 1) {
                                upImeiDate(list, halfFollowTime);
                            }
                        }
                    }
                });

            }
        }

    }

    private void upImeiDate(final List<ImsiDataTable> list, long halfFollowTime) {
        num = 0;
        mResult.clear();
        if (list != null && list.size() > 0) {
            Log.d(TAG, " imsi List：" + list.size());
            for (ImsiDataTable analyEntity : list) {
                Log.d(TAG,"analyEntity :" + analyEntity.getTime());
                DataManager.getInstance().findImsiData(analyEntity.getTime() - halfFollowTime, analyEntity.getTime() + halfFollowTime, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                    @Override
                    public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                        final HashSet<ImsiDataTable> result = new HashSet<ImsiDataTable>();
                        for (ImsiDataTable imsiDataTable : imsiDataTables) {
                            boolean isContains = false;
                            for (ImsiDataTable dataTable : result) {
                                if(TextUtils.equals(imsiDataTable.getImei(),dataTable.getImei())){
                                    isContains = true;
                                }
                            }
                            if(!isContains){
                                result.add(imsiDataTable);
                            }
                        }
                        mResult.addAll(result);
                        ++num;
                        Log.d(TAG, " tempList：" + imsiDataTables.size());
                        if (num == list.size()) {
                            final HashSet<ImsiDataTable> imsiDataTables1 = new HashSet<>();
                            for(int i = 0;i<mResult.size();i++){
                                for (int j = i;j<mResult.size();j++){
                                    if(TextUtils.equals(mResult.get(i).getImei(),mResult.get(j).getImei())
                                            && mResult.get(i).getTime() == mResult.get(j).getTime()){
                                        imsiDataTables1.add(mResult.get(i));
                                    }
                                }
                            }
                            if(imsiDataTables1.size()>0){
                                mResult.removeAll(imsiDataTables1);
                                for (ImsiDataTable imsiDataTable : imsiDataTables1) {
                                    boolean isContains = false;
                                    for (ImsiDataTable dataTable : mResult) {
                                        if (TextUtils.equals(imsiDataTable.getImei(), dataTable.getImei())
                                                && imsiDataTable.getTime() == dataTable.getTime()) {
                                            isContains = true;
                                        }
                                    }
                                    if (!isContains) {
                                        mResult.add(imsiDataTable);
                                    }
                                }
                            }
                            upImeidate(mResult);
                        }
                    }
                });
            }
        }
    }
    final List<ImsiDataTable> mResult = new ArrayList<>();
    private void upImeidate(List<ImsiDataTable> result) {
        Map<Long, AtomicInteger> resultMap = new HashMap<Long, AtomicInteger>(10000);
        try {
            for (ImsiDataTable imsi : result) {
                AtomicInteger imsiCount = resultMap.get(Long.valueOf(imsi.getImei()));
                if (null == imsiCount) {
                    resultMap.put(Long.valueOf(imsi.getImei()), new AtomicInteger(1));
                } else {
                    imsiCount.incrementAndGet();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<Map.Entry<Long, AtomicInteger>> it = resultMap.entrySet().iterator();
        int imsiCount = 0;
        while (it.hasNext()) {
            Map.Entry<Long, AtomicInteger> entry = it.next();
            imsiCount = entry.getValue().get();
            if (imsiCount >= 2) {
                ImsiDataTable report = getImei(entry.getKey().toString());
                if (report != null && !TextUtils.equals(report.getImei(), String.valueOf(mImsiET.getText().toString()))) {
                    report.cts = (imsiCount);
                    finalList.add(report);
                }
            }
        }
        Collections.sort(finalList, new Comparator<ImsiDataTable>() {
            @Override
            public int compare(ImsiDataTable o1, ImsiDataTable o2) {
                return o2.cts - o1.cts;
            }
        });
        if (finalList != null) {
            int startNum = 0;
            mNavPageView.initData(finalList.size(), pageSize);
            int i = 0;
            if (startNum < finalList.size()) {
                List<ImsiDataTable> result1 = finalList.subList(startNum, Math.min(startNum + pageSize, finalList.size()));
                Log.d(TAG, "tempList startNum result:" + result.size());
                dataSource.refreshDataSource(result1,finalList.size(),++i);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            dataSource.refreshDataSource(null,0,0);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private ImsiDataTable getImei(String s) {
        for (ImsiDataTable imsiDataTable : mResult) {
            if (TextUtils.equals(imsiDataTable.getImei(), s)) {
                return imsiDataTable;
            }
        }
        return null;
    }


    private void upDate(final List<ImsiDataTable> list, Long halfFollowTime) {
        num = 0;
        mResult.clear();
        if (list != null && list.size() > 0) {
            Log.d(TAG, " imsi List：" + list.size());
            for (ImsiDataTable analyEntity : list) {
                DataManager.getInstance().findImsiData(analyEntity.getTime() - halfFollowTime, analyEntity.getTime() + halfFollowTime, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                    @Override
                    public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                        final HashSet<ImsiDataTable> result = new HashSet<ImsiDataTable>();
                        for (ImsiDataTable imsiDataTable : imsiDataTables) {
                            if (!result.contains(imsiDataTable)) {
                                result.add(imsiDataTable);
                            }
                        }
                        mResult.addAll(result);
                        ++num;
                        if (num == list.size()) {
                            update(mResult);
                        }
                    }
                });
            }
        }
    }
    private Map<String, ArrayList<String>> resultTime = new HashMap<>();
    private Map<String, Long> resultTime1 = new HashMap<>();
    private void update(List<ImsiDataTable> result) {
        resultTime.clear();
        resultTime1.clear();
        LETLog.d("result "+result.size());
        Map<Long, AtomicInteger> resultMap = new HashMap<Long, AtomicInteger>(10000);
        Map<Long, AtomicInteger> resultMap1 = new HashMap<Long, AtomicInteger>(10000);
        try {
            for (ImsiDataTable imsi : result) {
                AtomicInteger imsiCount = resultMap.get(Long.valueOf(imsi.getImsi()));
                if (null == imsiCount) {
                    resultMap.put(Long.valueOf(imsi.getImsi()), new AtomicInteger(1));
                    resultTime1.put(imsi.getImsi(),imsi.getTime());
                    resultMap1.put(Long.valueOf(imsi.getImsi())+imsi.getTime(),new AtomicInteger(1));
                } else {
                    if(resultMap1.get(Long.valueOf(imsi.getImsi())+imsi.getTime()) == null){
                        LETLog.d(""+(Long.valueOf(imsi.getImsi())+imsi.getTime())
                        +"---"+imsi.getImsi() +"---"+imsi.getTime());
                        if(resultTime.get(imsi.getImsi()) == null){
                            ArrayList<String> longs = new ArrayList<>();
                            longs.add(DateUtil.formatTime(resultTime1.get(imsi.getImsi())));
                            longs.add(DateUtil.formatTime(imsi.getTime()));
                            resultTime.put(imsi.getImsi(),longs);
                        }else {
                            resultTime.get(imsi.getImsi()).add(DateUtil.formatTime(imsi.getTime()));
                        }
                        imsiCount.incrementAndGet();
                        resultMap1.put(Long.valueOf(imsi.getImsi())+imsi.getTime(),new AtomicInteger(1));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterator<Map.Entry<Long, AtomicInteger>> it = resultMap.entrySet().iterator();
        int imsiCount = 0;
        int i = 0;
        while (it.hasNext()) {
            Map.Entry<Long, AtomicInteger> entry = it.next();
            imsiCount = entry.getValue().get();
            LETLog.d( " imsiCount：" + imsiCount +" ---" + (++i));
            if (imsiCount >= 2) {
                ImsiDataTable report = getImsi(entry.getKey().toString());
                if (report != null && !TextUtils.equals(report.getImsi(), String.valueOf(mImsiET.getText().toString()))) {
                    report.cts = (imsiCount);
                    finalList.add(report);
                }
            }
        }
        Collections.sort(finalList, new Comparator<ImsiDataTable>() {
            @Override
            public int compare(ImsiDataTable o1, ImsiDataTable o2) {
                return o2.cts - o1.cts;
            }
        });
        if (finalList != null) {
            int startNum = 0;
            mNavPageView.initData(finalList.size(), pageSize);
            int a = 0;
            if (startNum < finalList.size()) {
                List<ImsiDataTable> result1 = finalList.subList(startNum, Math.min(startNum + pageSize, finalList.size()));
                Log.d(TAG, "tempList startNum result:" + result.size());
                dataSource.refreshDataSource(result1,finalList.size(),++a);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            dataSource.refreshDataSource(null,0,0);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    private ImsiDataTable getImsi(String s) {
        for (ImsiDataTable imsiDataTable : mResult) {
            if (TextUtils.equals(imsiDataTable.getImsi(), s)) {
                return imsiDataTable;
            }
        }
        return null;
    }

    /**
     * 导出excel
     */
    public void exportExcel() {
        if (finalList == null || finalList.size() == 0) {
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
        if (finalList == null || finalList.size() == 0) {
            return null;
        }
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < finalList.size(); i++) {
            ImsiDataTable analyEntity = finalList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
//            beanList.add(App.get().DevNumber);
            beanList.add(analyEntity.cts+"");
            beanList.add(analyEntity.getImsi());
            beanList.add(analyEntity.getImei());
            beanList.add(analyEntity.getMobile());
            beanList.add(analyEntity.getSource());
            beanList.add(analyEntity.getOperator());
            beanList.add((finalList.size()-i)+"");
//            beanList.add(analyEntity.getImei() == null ? "" : analyEntity.getImei());
//            beanList.add(analyEntity.getBbu());
//            beanList.add(analyEntity.lat);
//            beanList.add(analyEntity.lng);
            recordList.add(beanList);
        }
        return recordList;
    }

    public static AnalyFollowFragment newInstance() {
        return new AnalyFollowFragment();
    }

    @Override
    public void onClick(View view, SceneTable imsiData) {
        super.onClick(view, imsiData);
        sceneDialog.dismiss();
        if (mTagGroupView.getTags().size() != 0) {
            for (Tag tag : mTagGroupView.getTags()) {
                Log.d(TAG, "tag :" + tag.getBeginTime());
                if ((imsiData.getmBeginMillseconds() <= tag.getBeginTime() && imsiData.getmEndMillseconds() > tag.getBeginTime())
                        || (imsiData.getmBeginMillseconds() >= tag.getBeginTime() && imsiData.getmBeginMillseconds() < tag.getEndTime())) {
                    AppUtils.showToast(getActivity(), "场景与已添加时间重叠，请重新添加");
                    return;
                }
            }
        }
        String condition = (imsiData.getName()+"：");
        condition += DateUtil.formatTime(imsiData.getmBeginMillseconds());
        condition += "至";
        condition += DateUtil.formatTime(imsiData.getmEndMillseconds());
        Tag tag = new Tag(condition);
        tag.setBeginTime(imsiData.getmBeginMillseconds());
        tag.setEndTime(imsiData.getmEndMillseconds());
        tag.radius = 10f;
        tag.isDeletable = true;
//        tag.value = device.getId() + ";" + mBeginMillseconds + ";" + mEndMillseconds;
        tag.value = imsiData.getmBeginMillseconds() + ";" + imsiData.getmEndMillseconds();
        mTagGroupView.addTag(tag);
    }
}
