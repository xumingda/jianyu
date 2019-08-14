package com.lte.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.table.DeviceTypeTable;

import io.realm.OrderedRealmCollection;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class DeviceTypeAdapter extends RealmRecyclerViewAdapter<DeviceTypeTable,DeviceTypeAdapter.MViewHolder> {
    private LayoutInflater mInflater;
    private OrderedRealmCollection<DeviceTypeTable> mList;
    private Context mContext;
    private CheckListener onCheckListener;
    private int selectedPosition= 0;

    public DeviceTypeAdapter(Context context, OrderedRealmCollection<DeviceTypeTable> list, CheckListener onCheckListener, RecyclerView recyclerView) {
        super(list,true,recyclerView);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(OrderedRealmCollection<DeviceTypeTable> mList) {
        this.mList = mList;
        super.updateData(mList);
    }
    public void upDataSelectedPosition() {
        selectedPosition = mList.size()-1;
        notifyDataSetChanged();
    }
    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.item_string, parent, false));
    }
    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final DeviceTypeTable deviceTypeTable = mList.get(position);
        holder.name.setText(deviceTypeTable.getName());
        if (selectedPosition == position) {
            if (onCheckListener != null) {
                onCheckListener.onClick(holder.name, deviceTypeTable);
            }
            holder.name.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        final TextView name;
        MViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_type_text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        @Override
        public void onClick(View v) {
            selectedPosition = getAdapterPosition(); //选择的position赋值给参数，
            notifyDataSetChanged();//刷新当前点击item
        }

        @Override
        public boolean onLongClick(View v) {
            if (onCheckListener != null && mList.size()>getAdapterPosition()) {
                onCheckListener.onLongClick(v, mList.get(getAdapterPosition()));
                Log.d("Apppp","onLongChilk");
                return true;
            }
            return false;
        }
    }
    public interface CheckListener{
        void onClick(View view, DeviceTypeTable deviceTypeTable);
        void onLongClick(View view, DeviceTypeTable deviceTypeTable);
    }
}
