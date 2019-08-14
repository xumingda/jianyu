package com.lte.ui.fragment.second;

import android.Manifest;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.App;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.SceneTable;
import com.lte.permissons.RequestCode;
import com.lte.permissons.XPermissionUtils;
import com.lte.ui.adapter.datasource.AnalyDataSource;
import com.lte.ui.adapter.datasource.TableDataSource;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.TitleCheckView;
import com.lte.utils.AppUtils;
import com.communication.utils.DateUtil;
import com.lte.utils.ExcelUtils;
import com.lte.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * 统计分析
 */
public class AnalyStatisticFragment extends AnalyBaseFragment implements View.OnClickListener{
    private static final String TAG = AnalyStatisticFragment.class.getSimpleName();
    protected TitleCheckView mBeginDateTCV;
    protected TitleCheckView mEndDateTCV;
    protected TimePickerDialog mDialogDate,mDialogHour;
    protected static final String BEGIN_DATE_TAG = "begin_date_tag";
    protected static final String END_DATE_TAG = "end_date_tag";
    protected static final String BEGIN_HOUR_TAG = "begin_hour_tag";
    protected static final String END_HOUR_TAG = "end_hour_tag";
    private long mBeginDateMillseconds,mEndDateMillseconds,mBeginHourMillseconds,mEndHourMillseconds;

    private static String[] title = {"设备ID", "时间", "imsi",  "imei","手机号码", "归属地", "运营商","序号"};
    private Button export_btn;
    private TextView scene;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        mBeginDateTCV = (TitleCheckView) rootView.findViewById(R.id.begin_date_input_value);
        mBeginDateTCV.setCheckText("开始日期");
        mBeginDateTCV.setOnClickListener(this);
        mEndDateTCV = (TitleCheckView) rootView.findViewById(R.id.end_date_input_value);
        mEndDateTCV.setCheckText("结束日期");
        mEndDateTCV.setOnClickListener(this);

        export_btn = (Button) rootView.findViewById(R.id.export_btn);
        export_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportExcel();
            }
        });
        addScenButton = (Button) rootView.findViewById(R.id.add_button1);
        addScenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sceneDialog.show();
            }
        });

        scene = (TextView)rootView.findViewById(R.id.scene);
        initDateDialog();
        initMuiteDateDialog();
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_chart_statistic;
    }

    AnalyDataSource dataSource = new AnalyDataSource();
    @Override
    protected TableDataSource getTableDataSource() {
        return dataSource;
    }
    private int page;
    @Override
    protected void pageLoad(int page) {
        Log.d("","tempList pageLoad:"+page);
        this.page = page;
        if(resultList!=null){
            int startNum = (page - 1) * pageSize;
            Log.d("","tempList startNum:"+startNum);
            int i = 0;
            if (startNum < resultList.size()){
                List<ImsiDataTable> result = resultList.subList(startNum, Math.min(startNum + pageSize, resultList.size()));
                Log.d("","tempList startNum result:"+result.size());
                dataSource.refreshDataSource(result,resultList.size(),this.page);
                if(mAdapter!=null){
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    protected void search() {
        if(resultList!=null){
            resultList.clear();
        }
        betweenDays(mBeginDateMillseconds,mEndDateMillseconds);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin_time_input_value:
                mDialogHour.show(getFragmentManager(), BEGIN_HOUR_TAG);
                return;
            case R.id.end_time_input_value:
                mDialogHour.show(getFragmentManager(), END_HOUR_TAG);
                return;
            case R.id.begin_date_input_value:
                mDialogDate.show(getFragmentManager(), BEGIN_DATE_TAG);
                return;
            case R.id.end_date_input_value:
                mDialogDate.show(getFragmentManager(), END_DATE_TAG);
                return;
        }
        super.onClick(v);
    }

    protected void initDateDialog() {
        long fiveYears = 5L * 365 * 1000 * 60 * 60 * 24L;
        mDialogDate = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis() - fiveYears)
                .setMaxMillseconds(System.currentTimeMillis() + fiveYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorAccent))
                .setWheelItemTextSize(14)
                .build();
    }

    protected void initMuiteDateDialog() {
        long fiveYears = 5L * 365 * 1000 * 60 * 60 * 24L;
        mDialogHour = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis() - fiveYears)
                .setMaxMillseconds(System.currentTimeMillis() + fiveYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.colorPrimary))
                .setType(Type.HOURS_MINS)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.colorPrimary))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.colorAccent))
                .setWheelItemTextSize(14)
                .build();
    }


    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String tag = timePickerView.getTag();
        if (BEGIN_DATE_TAG.equals(tag)) {
            mBeginDateMillseconds = millseconds;
            mBeginDateTCV.setCheckText(getCustonFormatTime(millseconds,"yyyy-MM-dd"));
        } else if (END_DATE_TAG.equals(tag)) {
            mEndDateMillseconds = millseconds;
            mEndDateTCV.setCheckText(getCustonFormatTime(millseconds,"yyyy-MM-dd"));
        } else if(BEGIN_HOUR_TAG.equals(tag)){
            Log.d(TAG,"millseconds :" + millseconds);
            mBeginHourMillseconds = millseconds;
            mBeginTimeTCV.setCheckText(getCustonFormatTime(millseconds,"HH:mm:ss"));
        } else if(END_HOUR_TAG.equals(tag)){
            Log.d(TAG,"millseconds :" + millseconds);
            mEndHourMillseconds = millseconds;
            mEndTimeTCV.setCheckText(getCustonFormatTime(millseconds,"HH:mm:ss"));
        }
    }


    List<ImsiDataTable> resultList = new ArrayList<ImsiDataTable>();
    private static final long ONE_DAY_MS=24*60*60*1000;
    /**
     * 计算两个日期之间的日期
     * @param startTime
     * @param endTime
     */
    private void betweenDays(long startTime,long endTime){
        if(startTime == 0){
            AppUtils.showToast(getActivity(),"请选择开始日期");
            return;
        }
        if(endTime == 0){
            AppUtils.showToast(getActivity(),"请选择结束日期");
            return;
        }
        if(startTime>endTime){
            AppUtils.showToast(getActivity(),"开始日期不能大于结束日期");
            return;
        }
        if(mBeginHourMillseconds == 0){
            AppUtils.showToast(getActivity(),"请选择开始时间段");
            return;
        }
        if(mEndHourMillseconds == 0){
            AppUtils.showToast(getActivity(),"请选择结束时间段");
            return;
        }
        Date date_start=new Date(startTime);
        Date date_end=new Date(endTime);
        //计算日期从开始时间于结束时间的0时计算
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(date_start);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(date_end);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

//        Calendar startCalendar = Calendar.getInstance();

        Date dateHour1 = new Date(mBeginHourMillseconds);
        Calendar calendarHour1 = Calendar.getInstance();
        calendarHour1.setTime(dateHour1);
        int hour1 = calendarHour1.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendarHour1.get(Calendar.MINUTE);

        fromCalendar.set(Calendar.HOUR_OF_DAY, hour1);
        fromCalendar.set(Calendar.MINUTE, minute1);
//
//        Calendar finalCalendar = Calendar.getInstance();
//
        Date dateHour2 = new Date(mEndHourMillseconds);
        Calendar calendarHour2 = Calendar.getInstance();
        calendarHour2.setTime(dateHour2);
        int hour2 = calendarHour2.get(Calendar.HOUR_OF_DAY);
        int minute2 = calendarHour2.get(Calendar.MINUTE);

        toCalendar.set(Calendar.HOUR_OF_DAY, hour2);
        toCalendar.set(Calendar.MINUTE, minute2);
//        Calendar endCalendar = Calendar.getInstance();

//
//        int s = (int) ((toCalendar.getTimeInMillis() - fromCalendar.getTimeInMillis())/ (ONE_DAY_MS));
//        if(s>0){
//            for(int i = 0;i<=s;i++){
//                finalCalendar.clear();
//                startCalendar.clear();
//                endCalendar.clear();
//                long todayDate = fromCalendar.getTimeInMillis() + i * ONE_DAY_MS;
//                finalCalendar.setTime(new Date(todayDate));
//                int year = finalCalendar.get(Calendar.YEAR);
//                int month = finalCalendar.get(Calendar.MONTH);
//                int day = finalCalendar.get(Calendar.DAY_OF_MONTH);
//                startCalendar.set(Calendar.YEAR,year);
//                startCalendar.set(Calendar.MONTH,month);
//                startCalendar.set(Calendar.DAY_OF_MONTH, day);
//                startCalendar.set(Calendar.HOUR_OF_DAY, hour1);
//                startCalendar.set(Calendar.MINUTE, minute1);
//                long mBeginMillSeconds = startCalendar.getTimeInMillis();
//
//                endCalendar.set(Calendar.YEAR,year);
//                endCalendar.set(Calendar.MONTH, month);
//                endCalendar.set(Calendar.DAY_OF_MONTH,day);
//                endCalendar.set(Calendar.HOUR_OF_DAY, hour2);
//                endCalendar.set(Calendar.MINUTE, minute2);
//                long mEndMillSeconds = endCalendar.getTimeInMillis();
                DataManager.getInstance().findImsiData(fromCalendar.getTimeInMillis(),toCalendar.getTimeInMillis(),new RealmChangeListener<RealmResults<ImsiDataTable>>() {
                    @Override
                    public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                        Log.d(TAG,"imsiDataTables :" + imsiDataTables.size());
                        final List<ImsiDataTable> tempList = imsiDataTables.sort("time", Sort.DESCENDING);
//                        if(tempList!=null && tempList.size()>0){
                            resultList.addAll(tempList);
                            update();
//                        }
                    }
                });
////                List<AnalyEntity> tempList = new DefaultDAO(getActivity()).queryToModel(AnalyEntity.class, true, "time>? and time<? ",new String[]{String.valueOf(mBeginMillSeconds/1000),String.valueOf(mEndMillSeconds/1000)}, "",null);
////                if(tempList!=null && tempList.size()>0){
////                    resultList.addAll(tempList);
////                }
//                Log.i("打印日期","mBegin: "+getCustonFormatTime(mBeginMillSeconds,"yyyy-MM-dd HH:mm:ss") +" End:"+getCustonFormatTime(mEndMillSeconds,"yyyy-MM-dd HH:mm:ss") );
//            }
//        } else {//此时在同一天之内
//            Log.i("打印日期",getCustonFormatTime(startTime,"yyyy-MM-dd"));
//
//            int year = fromCalendar.get(Calendar.YEAR);
//            int month = fromCalendar.get(Calendar.MONTH);
//            int day = fromCalendar.get(Calendar.DAY_OF_MONTH);
//            startCalendar.set(Calendar.YEAR,year);
//            startCalendar.set(Calendar.MONTH,month);
//            startCalendar.set(Calendar.DAY_OF_MONTH, day);
//            startCalendar.set(Calendar.HOUR_OF_DAY, hour1);
//            startCalendar.set(Calendar.MINUTE, minute1);
//            long mBeginMillSeconds = startCalendar.getTimeInMillis();
//
//            endCalendar.set(Calendar.YEAR,year);
//            endCalendar.set(Calendar.MONTH, month);
//            endCalendar.set(Calendar.DAY_OF_MONTH,day);
//            endCalendar.set(Calendar.HOUR_OF_DAY, hour2);
//            endCalendar.set(Calendar.MINUTE, minute2);
//            long mEndMillSeconds = endCalendar.getTimeInMillis();
//
//            DataManager.getInstance().findImsiByImeiAndImsiData(mBeginMillSeconds,mEndMillSeconds,new RealmChangeListener<RealmResults<ImsiDataTable>>() {
//                @Override
//                public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
//                    final List<ImsiDataTable> tempList = imsiDataTables;
//                    if(tempList!=null && tempList.size()>0){
//                        resultList.addAll(tempList);
//                        update();
//                    }
//                }
//            });
////            List<AnalyEntity> tempList = new DefaultDAO(getActivity()).queryToModel(AnalyEntity.class, true, "time>? and time<? ",new String[]{String.valueOf(mBeginMillSeconds/1000),String.valueOf(mEndMillSeconds/1000)}, "",null);
////            if(tempList!=null && tempList.size()>0){
////                resultList.addAll(tempList);
////            }
//        }
    }
    private void update(){
        if(resultList!=null && resultList.size()>0){
            mNavPageView.initData(resultList.size(),pageSize);
            int startNum = 0;
            Log.d("","tempList startNum:"+startNum +" resultList size:"+resultList.size());
            int i = 0;
            if (startNum < resultList.size()) {
                List<ImsiDataTable> result = resultList.subList(startNum, Math.min(startNum + pageSize, resultList.size()));
                dataSource.refreshDataSource(result,resultList.size(),this.page);
                if(mAdapter!=null){
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Log.d("","resultList ==null");
            dataSource.refreshDataSource(null,0,0);
            if(mAdapter!=null){
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * 格式化传入的时间
     *
     * @param time      需要格式化的时间
     * @param formatStr 格式化的格式
     * @return
     */
    public static String getCustonFormatTime(long time, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date d1 = new Date(time);
        return format.format(d1);
    }

    /**
     * 导出excel
     */
    public void exportExcel() {
        if (resultList == null || resultList.size() == 0) {
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
        if (resultList == null || resultList.size() == 0) {
            return null;
        }
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            ImsiDataTable analyEntity = resultList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(App.get().DevNumber);
            beanList.add(DateUtil.formatTime(analyEntity.getTime()));
            beanList.add(analyEntity.getImsi());
            beanList.add(analyEntity.getImei() == null ? "" : analyEntity.getImei());
            beanList.add(analyEntity.getMobile());
            beanList.add(analyEntity.getSource());
            beanList.add(analyEntity.getOperator());
            beanList.add((resultList.size()-i)+"");
//
//            beanList.add(analyEntity.getBbu());
//            beanList.add(analyEntity.lat);
//            beanList.add(analyEntity.lng);
            recordList.add(beanList);
        }
        return recordList;
    }

    public static AnalyStatisticFragment newInstance() {
        return new AnalyStatisticFragment();
    }
    @Override
    public void onClick(View view, SceneTable imsiData) {
        super.onClick(view, imsiData);
        sceneDialog.dismiss();
        if(resultList != null){
            resultList.clear();
        }
        scene.setVisibility(View.VISIBLE);
        scene.setText(imsiData.getName());
        DataManager.getInstance().findImsiData(imsiData.getmBeginMillseconds(),imsiData.getmEndMillseconds(),new RealmChangeListener<RealmResults<ImsiDataTable>>() {
            @Override
            public void onChange(RealmResults<ImsiDataTable> imsiDataTables) {
                Log.d(TAG,"imsiDataTables :" + imsiDataTables.size());
                final List<ImsiDataTable> tempList = imsiDataTables.sort("time", Sort.DESCENDING);
                resultList.addAll(tempList);
                update();
            }
        });
    }
}
