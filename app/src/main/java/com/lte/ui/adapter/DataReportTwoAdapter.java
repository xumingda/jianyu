package com.lte.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.table.MacDataTable;
import com.lte.utils.Constants;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

import static com.lte.utils.DateUtils.getNowDate;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class DataReportTwoAdapter extends RealmRecyclerViewAdapter<MacDataTable, DataReportTwoAdapter.MViewHolder> {
    private final static String TAG = "FunctionAdapter";
    private LayoutInflater mInflater;
    private OrderedRealmCollection<MacDataTable> mList;
    private Context mContext;
    private CheckListener onCheckListener;

    public DataReportTwoAdapter(Context context, OrderedRealmCollection<MacDataTable> list, CheckListener onCheckListener, RecyclerView recyclerView) {
        super(list, true, recyclerView);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(RealmResults<MacDataTable> mList) {
        this.mList = mList;
        super.updateData(mList);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.data_report_two_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final MacDataTable imsiData = mList.get(position);

        holder.serial_number.setText((mList.size() - position) + "");

        if (imsiData.getMac() != null) {
            holder.mac.setText(imsiData.getMac());
        }
        if (imsiData.getTime() != 0) {
            holder.timestamp.setText(getNowDate(imsiData.getTime()));
        }
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position, List<Object> payloads) {
        Log.d("Adapter ", "key :" + (payloads == null || payloads.isEmpty()));
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                Log.d("Adapter ", "key :" + key);
                switch (key) {
                    case Constants.MAC:
                        holder.mac.setText(bundle.getString(key));
                        break;
                    case Constants.TIME:
                        holder.timestamp.setText(getNowDate(bundle.getLong(key)));
                        break;

                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView serial_number;
        final TextView mac;
        final TextView timestamp;


        MViewHolder(View itemView) {
            super(itemView);
            serial_number = (TextView) itemView.findViewById(R.id.serial_number);
            mac = (TextView) itemView.findViewById(R.id.mac);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
                onCheckListener.onClick(v, mList.get(getAdapterPosition()));
            }
        }
    }

    public interface CheckListener {
        void onClick(View view, MacDataTable imsiData);
    }
}
