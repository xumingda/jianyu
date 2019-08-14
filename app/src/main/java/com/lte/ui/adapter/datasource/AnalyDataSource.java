package com.lte.ui.adapter.datasource;

import android.util.Log;

import com.lte.data.table.ImsiDataTable;
import com.communication.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 分析数据源
 * Created by 06peng on 2017/10/3.
 */

public class AnalyDataSource implements TableDataSource<String, String, String, String> {

    public String[] mColumnHeaderData = new String[] { "设备ID", "时间", "imsi", "imei",
            "手机号码", "归属地", "运营商","序号"};
    /**1:碰撞 2:其他*/
    public int type = 3;
    private List<ImsiDataTable> mRowData = new ArrayList<>();
    private int count;

    private int page;

    public AnalyDataSource() {
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

    public void refreshDataSource(List<ImsiDataTable> list,int count,int page) {
        this.count = count;
        this.page = page;
        if(mRowData!=null){
            mRowData.clear();
        }
        if(list!=null){
            mRowData.addAll(list);
        }
        Log.d("refreshDataSource" ,"COUNT "+count + "page :" + page);
    }

    @Override
    public int getRowsCount() {
        return 21;
    }

    @Override
    public int getColumnsCount() {
        if(type == 1 || type == 2){
            return 6;
        }
        return 8;
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
        Log.d("tempList","rowIndex " + rowIndex);
        if(type == 1){
            if(mRowData!=null && mRowData.size()>0 && rowIndex<=mRowData.size()){
                for (int i = 0; i <mRowData.size();i++) {
                    ImsiDataTable entity = mRowData.get(rowIndex-1);
                    switch (columnIndex) {
                        case 0:
                            return type==1?String.valueOf(entity.cts): entity.getDeviceId();
                        case 1:
                            return entity.getImsi();
                        case 2:
                            return entity.getMobile();
                        case 3:
                            return entity.getSource();
                        case 4:
                            return entity.getOperator();
                        case 5:
                            return (count-(rowIndex-1+((page-1) *20)))+"";
                    }
                }
            }
        }else if(type == 2){
            if (mRowData != null && mRowData.size() > 0 && rowIndex <= mRowData.size()) {
                for (int i = 0; i < mRowData.size(); i++) {
                    ImsiDataTable entity = mRowData.get(rowIndex - 1);
                    switch (columnIndex) {
                        case 0:
                            return type == 1 ? String.valueOf(entity.cts) : entity.getDeviceId();
                        case 1:
                            return entity.getImei();
                        case 2:
                            return entity.getMobile();
                        case 3:
                            return entity.getSource();
                        case 4:
                            return entity.getOperator();
                        case 5:
                            return (count - (rowIndex - 1 + ((page - 1) * 20))) + "";
                    }
                }
            }
        }else {
            if (mRowData != null && mRowData.size() > 0 && rowIndex <= mRowData.size()) {
                for (int i = 0; i < mRowData.size(); i++) {
                    ImsiDataTable entity = mRowData.get(rowIndex - 1);
                    switch (columnIndex) {
                        case 0:
                            return type == 1 ? String.valueOf(entity.cts) : entity.getDeviceId();
                        case 1:
                            return DateUtil.formatTime(entity.getTime());
                        case 2:
                            return entity.getImsi();
                        case 3:
                            return entity.getImei();
                        case 4:
                            return entity.getMobile();
                        case 5:
                            return entity.getSource();
                        case 6:
                            return entity.getOperator();
                        case 7:
                            return (count - (rowIndex - 1 + ((page - 1) * 20))) + "";
                    }
                }
            }
        }
        return null;
    }
}
