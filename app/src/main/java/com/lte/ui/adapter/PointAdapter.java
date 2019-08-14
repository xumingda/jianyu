package com.lte.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.table.RealmInteger;

import io.realm.RealmList;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.MViewHolder> {
    private LayoutInflater mInflater;
    private RealmList<RealmInteger> mList;
    private CheckListener onCheckListener;

    public PointAdapter(Context context, RealmList<RealmInteger> list, CheckListener onCheckListener) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void setNewDate(RealmList<RealmInteger> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.item_string, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final RealmInteger byte1 = mList.get(position);
        holder.name.setText("" +byte1.getNumber());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0:mList.size();
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
                    if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
                        onCheckListener.onClick(v, mList.get(getAdapterPosition()));
                    }
        }

        @Override
        public boolean onLongClick(View v) {
            if(onCheckListener != null && mList.size() > getAdapterPosition()){
                onCheckListener.onLongClick(v, mList.get(getAdapterPosition()),mList);
                return true;
            }
            return false;
        }
    }
    public interface CheckListener{
        void onClick(View view, RealmInteger data);
        void onLongClick(View view, RealmInteger data, RealmList<RealmInteger> bytes);
    }
}
