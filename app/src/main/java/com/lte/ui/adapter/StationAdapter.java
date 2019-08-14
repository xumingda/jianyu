package com.lte.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.StationInfo;
import com.lte.utils.Constants;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.MViewHolder> {
    private final static String TAG = "FunctionAdapter";
    private LayoutInflater mInflater;
    private List<String> mList;
    private Context mContext;
    private CheckListener onCheckListener;

    public StationAdapter(Context context, List<String> list, CheckListener onCheckListener) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(List<String> mList) {
        this.mList = mList;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.station_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final String text = mList.get(position);
        holder.textView1.setText(text);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView textView1;

        MViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.config:
                    if (onCheckListener != null) {
                        onCheckListener.onClick(v, getAdapterPosition());
                    }
//                    break;
//                case R.id.cill_config:
//                    if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
//                        onCheckListener.onClick(v, mList.get(getAdapterPosition()));
//                    }
//                    break;
//            }
        }
    }
    public interface CheckListener{
        void onClick(View view, int position);
//        void onItemChick(View view,StationInfoTable stationInfo);
    }
}
