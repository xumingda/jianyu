package com.lte.ui.adapter.datasource;

import com.App;
import com.lte.data.table.ImsiDataTable;
import com.communication.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分析数据源
 * Created by 06peng on 2017/10/3.
 */

public class DataReportSource implements TableDataSource<String, String, String, String> {

    private String[] mColumnHeaderData = new String[] { "设备ID", "运营商", "imsi",
            "手机号码", "时间", "BBU","序号"};
    /**1:碰撞 2:其他*/
    public int type = 2;
    private List<ImsiDataTable> mRowData = new ArrayList<>();


    public DataReportSource() {
//        initDataSource();
    }

//    private void initDataSource() {
//        for (int i = 0;i <= 20;i++) {
//            AnalyEntity followEntity = new AnalyEntity();
//            followEntity.serialNumber = "2017100" + i;
//            followEntity.deviceId = "46000557425720" + i;
//            followEntity.imsi = "86238703041838" + i;
//            followEntity.mobile = "15930393983";
//            followEntity.source = "广州";
//            followEntity.operator = "移动";
//            followEntity.time = System.currentTimeMillis();
//            mRowData.add(followEntity);
//        }
//    }

    public void refreshDataSource(List<ImsiDataTable> list) {
        if(mRowData!=null){
            mRowData.clear();
        }
        if(list!=null){
            mRowData.addAll(list);
        }
    }

    @Override
    public int getRowsCount() {
        return 20;
    }

    @Override
    public int getColumnsCount() {
        return 7;
    }

    @Override
    public String getFirstHeaderData() {
        return type==1?"出现次数":"设备ID";
    }

    @Override
    public String getRowHeaderData(int index) {
        return null;
    }

    @Override
    public String getColumnHeaderData(int index) {
        return mColumnHeaderData[index];
    }

    @Override
    public String getItemData(int rowIndex, int columnIndex) {
        if(mRowData!=null && mRowData.size()>0 && rowIndex<mRowData.size()){
            for (int i = 0; i < rowIndex;i++) {
                ImsiDataTable entity = mRowData.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return type==1?String.valueOf(entity.cts): App.get().DevNumber;
                    case 1:
                        return entity.getOperator();
                    case 2:
                        return entity.getImsi();
                    case 3:
                        return entity.getMobile();
                    case 4:
                        return DateUtil.formatTime(entity.getTime());
                    case 5:
                        return entity.getBbu();
                    case 6:
                        return (mRowData.size() - rowIndex)+"";
                }
            }
        }
        return null;
    }
}
