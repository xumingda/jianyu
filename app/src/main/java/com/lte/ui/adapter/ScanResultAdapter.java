package com.lte.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lte.R;
import com.lte.data.table.ScanResultTable;
import com.lte.ui.widget.BaseViewHolder;

import io.realm.OrderedRealmCollection;

import static com.communication.utils.DateUtil.formatTime;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class ScanResultAdapter extends RealmRecyclerViewAdapter<ScanResultTable,BaseViewHolder> {
    private final static String TAG = "ScanResultAdapter";
    private LayoutInflater mInflater;
    private OrderedRealmCollection<ScanResultTable> mList;
    private Context mContext;

    public ScanResultAdapter(Context context, OrderedRealmCollection<ScanResultTable> list,RecyclerView recyclerView) {
        super(list,true,recyclerView);
        Log.d(TAG,"mList ;" +list.size());
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
    }

    public void upDataList(OrderedRealmCollection<ScanResultTable> mList) {
        this.mList = mList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(mInflater.inflate(R.layout.scan_result_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ScanResultTable scanResultTable = mList.get(position);
        holder.setText(R.id.time,formatTime(scanResultTable.getTime()));
        holder.setText(R.id.freq,scanResultTable.getFrequency()+"");
        holder.setText(R.id.pci,scanResultTable.getPci()+"");
        holder.setText(R.id.tac,scanResultTable.getTAC()+"");
        holder.setText(R.id.rssi,scanResultTable.getRSSI()+"");
        holder.setText(R.id.priority,scanResultTable.getPriority()+"");
    }
}
