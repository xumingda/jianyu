package com.lte.ui.fragment.second;

import android.Manifest;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import com.lte.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * 碰撞分析
 */
public class AnalyCollisionFragment extends AnalyBaseFragment {

    private static final String TAG = AnalyCollisionFragment.class.getSimpleName();
    private MaterialSpinner mNumberSpinner;  //碰撞次数
    private MaterialSpinner collison_type;
    private Button mAddButton;
    private TagView mTagGroupView;

    private int page = 1;

    private static String[] title = {"出现次数",  "imsi", "imei", "手机号码", "归属地", "运营商", "序号"};
    private Button export_btn;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_analycollision;
    }

    AnalyDataSource dataSource = new AnalyDataSource();

    @Override
    protected TableDataSource getTableDataSource() {
        dataSource.type = 1;
        dataSource.mColumnHeaderData = new String[]{"设备ID", "imsi",
                "手机号码", "归属地", "运营商", "序号"};
        return dataSource;
    }

    @Override
    protected void pageLoad(int page) {
        Log.d(TAG, "tempList pageLoad:" + page);
        this.page = page;
        if (userReportList != null) {
            int startNum = (page - 1) * pageSize;
            Log.d(TAG, "tempList startNum:" + startNum);
            int i = 0;
            if (startNum < userReportList.size()) {
                List<ImsiDataTable> result = userReportList.subList(startNum, Math.min(startNum + pageSize, userReportList.size()));
                Log.d(TAG, "tempList startNum result:" + result.size());
                dataSource.refreshDataSource(result, userReportList.size(), this.page);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mNumberSpinner = (MaterialSpinner) rootView.findViewById(R.id.number_spinner);
        collison_type = (MaterialSpinner) rootView.findViewById(R.id.collison_type);
        List<String> items = new ArrayList<>();
        items.add("imsi");
        items.add("imei");
        collison_type.setItems(items);
        collison_type.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                switch (position) {
                    case 0:
                        dataSource.type = 1;
                        dataSource.mColumnHeaderData = new String[]{"设备ID", "imsi",
                                "手机号码", "归属地", "运营商", "序号"};
                        mAdapter.upDateDataSource(dataSource);
                        break;
                    case 1:
                        dataSource.type = 2;
                        dataSource.mColumnHeaderData = new String[]{"设备ID", "imei",
                                "手机号码", "归属地", "运营商", "序号"};
                        mAdapter.upDateDataSource(dataSource);
                        break;
                }
            }
        });
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
                mTagGroupView.remove(position);
                setNumItems();
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
            ToastUtils.showToast(getActivity(), "请添加搜索条件", 3000);
            return;
        }
        if (mTagGroupView.getTags().size() != 0) {
            for (Tag tag : mTagGroupView.getTags()) {
                Log.d(TAG, "tag :" + tag.getBeginTime());
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
//        tag.value = device.getId() + ";" + mBeginMillseconds + ";" + mEndMillseconds;
        tag.value = mBeginMillseconds + ";" + mEndMillseconds;
        mTagGroupView.addTag(tag);
        setNumItems();
    }

    private void setNumItems() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < mTagGroupView.getTags().size(); i++) {
            items.add(i + 1);
        }
        if (items.size() > 0) {
            mNumberSpinner.setItems(items);
            mNumberSpinner.setSelectedIndex(0);
        }
    }

    List<ImsiDataTable> userReportList = new ArrayList<ImsiDataTable>();
    int num = 0;
    final List<ImsiDataTable> mResult = new ArrayList<>();
    final List<ImsiDataTable> mFinalResult = new ArrayList<>();

    @Override
    protected void search() {
        final List<Tag> tags = mTagGroupView.getTags();
        if (tags.isEmpty()) {
            ToastUtils.showToast(getActivity(), "请添加查询条件！", 3000);
            return;
        }
//        localProgressFlag = new AtomicReference<>();
//        localProgressFlag.set(DialogManager.showProgressDialog(_mActivity, "查询中，请稍候.."));
        userReportList.clear();
        mResult.clear();
        mFinalResult.clear();
        num = 0;
        final int count = mNumberSpinner.getSelectedIndex() + 1;
        long start = System.currentTimeMillis();
        for (Tag tag : tags) {
            Log.d(TAG, "------查询条件：" + tag.value);
            String[] time = tag.value.split(";");
            Long mBeginMillseconds = Long.valueOf(time[0]);
            Long mEndMillseconds = Long.valueOf(time[1]);
            // 不用distinct. 后面用hashset过滤
            DataManager.getInstance().findImsiData(mBeginMillseconds, mEndMillseconds, new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                @Override
                public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                    if (collison_type.getSelectedIndex() == 0) {
                        final HashSet<ImsiDataTable> result = new HashSet<ImsiDataTable>();
                        for (ImsiDataTable imsiDataTable : imsiDataTables) {
                            if (!result.contains(imsiDataTable)) {
                                result.add(imsiDataTable);
                            }
                        }
                        mResult.addAll(result);
                        mFinalResult.addAll(imsiDataTables);
                        ++num;
                        if (num == tags.size()) {
                            update(tags, count);
                        }
                    } else {
                        final HashSet<ImsiDataTable> result = new HashSet<ImsiDataTable>();
                        for (ImsiDataTable imsiDataTable : imsiDataTables) {
                            boolean isContains = false;
                            for (ImsiDataTable dataTable : result) {
                                if (TextUtils.equals(imsiDataTable.getImei(), dataTable.getImei())) {
                                    isContains = true;
                                }
                            }
                            if (!isContains) {
                                result.add(imsiDataTable);
                            }
                        }
                        mResult.addAll(result);
                        mFinalResult.addAll(imsiDataTables);
                        ++num;
                        if (num == tags.size()) {
                            update(tags, count);
                        }
                    }
                }
            });
        }
    }

    private void update(List<Tag> tags, int count) {
        userReportList.clear();
        Map<Long, AtomicInteger> resultMap = new HashMap<Long, AtomicInteger>(10000);
        Map<Long, AtomicInteger> finalResultMap = new HashMap<Long, AtomicInteger>(10000);
        if (collison_type.getSelectedIndex() == 0) {
            try {
                for (ImsiDataTable imsi : mResult) {
                    AtomicInteger imsiCount = resultMap.get(Long.valueOf(imsi.getImsi()));
                    if (null == imsiCount) {
                        resultMap.put(Long.valueOf(imsi.getImsi()), new AtomicInteger(1));
                    } else {
                        imsiCount.incrementAndGet();
                    }

                }
                for (ImsiDataTable imsi : mFinalResult) {
                    AtomicInteger imsiCount = finalResultMap.get(Long.valueOf(imsi.getImsi()));
                    if (null == imsiCount) {
                        finalResultMap.put(Long.valueOf(imsi.getImsi()), new AtomicInteger(1));
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
                if (imsiCount >= count) {
                    ImsiDataTable report = getImsi(entry.getKey().toString());
                    report.cts = finalResultMap.get(entry.getKey()).intValue();
                    userReportList.add(report);
                }
            }
        } else {
            try {
                for (ImsiDataTable imsi : mResult) {
                    AtomicInteger imsiCount = resultMap.get(Long.valueOf(imsi.getImei()));
                    if (null == imsiCount) {
                        resultMap.put(Long.valueOf(imsi.getImei()), new AtomicInteger(1));
                    } else {
                        imsiCount.incrementAndGet();
                    }

                }
                for (ImsiDataTable imsi : mFinalResult) {
                    AtomicInteger imsiCount = finalResultMap.get(Long.valueOf(imsi.getImei()));
                    if (null == imsiCount) {
                        finalResultMap.put(Long.valueOf(imsi.getImei()), new AtomicInteger(1));
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
                if (imsiCount >= count) {
                    ImsiDataTable report = getImei(entry.getKey().toString());
                    if (report != null) {
                        report.cts = finalResultMap.get(entry.getKey()).intValue();
                        userReportList.add(report);
                    }
                }
            }
        }
        Collections.sort(userReportList, new Comparator<ImsiDataTable>() {
            @Override
            public int compare(ImsiDataTable o1, ImsiDataTable o2) {
                return o2.cts - o1.cts;
            }
        });
        if (userReportList != null) {
            int startNum = 0;
            mNavPageView.initData(userReportList.size(), pageSize);
            List<ImsiDataTable> resultpage = userReportList.subList(startNum, Math.min(startNum + pageSize, userReportList.size()));
            dataSource.refreshDataSource(resultpage, userReportList.size(), this.page);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            dataSource.refreshDataSource(null, 0, 0);
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

    private ImsiDataTable getImsi(String s) {
        for (ImsiDataTable imsiDataTable : mResult) {
            if (TextUtils.equals(imsiDataTable.getImsi(), s)) {
                return imsiDataTable;
            }
        }
        return null;
    }


    private void crashAnalysis() {

    }

    @Override
    protected void initData() {
        super.initData();
        setTitile(getString(R.string.collision_tme));
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int row, int column) {
                Log.d(TAG, "onRowHeaderClick:" + (row + ((page - 1) * pageSize)));
                ImsiDataTable imsiDataTable = null;
                int index = row - 1;
                if (index < 0) {
                    index = 0;
                }
                if (page != 0) {
                    if (userReportList.size() > (index + ((page - 1) * pageSize))) {
                        imsiDataTable = userReportList.get(index + ((page - 1) * pageSize));
                    }
                } else {
                    if (userReportList.size() > index) {
                        imsiDataTable = userReportList.get(index);
                    }
                }
                if (imsiDataTable != null) {
                    ArrayList<String> list = new ArrayList<>();
                    for (ImsiDataTable dataTable : mFinalResult) {
                        if (TextUtils.equals(dataTable.getImsi(), imsiDataTable.getImsi())) {
                            list.add(DateUtil.formatTime(dataTable.getTime()));
                        }
                    }
                    upBasedate(list);
                    timesDialog.show();
                }
            }

            @Override
            public void onRowHeaderClick(int row) {
                Log.d(TAG, "onRowHeaderClick:" + (row + ((page - 1) * pageSize)));
                ImsiDataTable imsiDataTable = null;
                int index = row - 1;
                if (index < 0) {
                    index = 0;
                }
                if (page != 0) {
                    if (userReportList.size() > (index + ((page - 1) * pageSize))) {
                        imsiDataTable = userReportList.get(index + ((page - 1) * pageSize));
                    }
                } else {
                    if (userReportList.size() > index) {
                        imsiDataTable = userReportList.get(index);
                    }
                }
                if (imsiDataTable != null) {
                    ArrayList<String> list = new ArrayList<>();
                    for (ImsiDataTable dataTable : mFinalResult) {
                        if (TextUtils.equals(dataTable.getImsi(), imsiDataTable.getImsi())) {
                            list.add(DateUtil.formatTime(dataTable.getTime()));
                        }
                    }
                    upBasedate(list);
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

    /**
     * 导出excel
     */
    public void exportExcel() {
        if (userReportList == null || userReportList.size() == 0) {
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
        if (userReportList == null || userReportList.size() == 0) {
            return null;
        }
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < userReportList.size(); i++) {
            ImsiDataTable analyEntity = userReportList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(analyEntity.cts+"");
            beanList.add(analyEntity.getImsi());
            beanList.add(analyEntity.getImei());
            beanList.add(analyEntity.getMobile());
            beanList.add(analyEntity.getSource());
            beanList.add(analyEntity.getOperator());
            beanList.add((userReportList.size()-i)+"");

//            beanList.add(analyEntity.getImei() == null ? "" : analyEntity.getImei());
//            beanList.add(analyEntity.getBbu());
//            beanList.add(analyEntity.lat);
//            beanList.add(analyEntity.lng);
            recordList.add(beanList);
        }
        return recordList;
    }

    public static AnalyCollisionFragment newInstance() {
        return new AnalyCollisionFragment();
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
        String condition = (imsiData.getName() + "：");
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
        setNumItems();
    }
}
