package com.lte.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.BaseQuickAdapter;
import com.github.library.BaseViewHolder;
import com.lte.R;
import com.lte.data.table.DeviceTypeTable;

import io.realm.OrderedRealmCollection;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class BbuTypeAdapter extends RecyclerView.Adapter<BbuTypeAdapter.MViewHolder> {
    private LayoutInflater mInflater;
    private byte[] mList;
    private CheckListener onCheckListener;

    public BbuTypeAdapter(Context context, byte[] list, CheckListener onCheckListener) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void setNewDate(byte[] mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.item_string, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final byte byte1 = mList[position];
        holder.name.setText("BBU" +byte1);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0:mList.length;
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView name;


        MViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_type_text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
                    if (onCheckListener != null && mList.length > getAdapterPosition() && getAdapterPosition() >= 0) {
                        onCheckListener.onClick(v, mList[getAdapterPosition()]);
                    }
        }

        @Override
        public boolean onLongClick(View v) {
            if(onCheckListener != null && mList.length > getAdapterPosition()){
                onCheckListener.onLongClick(v, mList[getAdapterPosition()],mList);
                return true;
            }
            return false;
        }
    }
    public interface CheckListener{
        void onClick(View view, byte data);
        void onLongClick(View view, byte data,byte[] bytes);
    }
}
