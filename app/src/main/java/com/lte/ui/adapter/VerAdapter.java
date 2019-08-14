package com.lte.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.StationInfo;
import com.lte.ui.activity.VerActivity;
import com.lte.utils.Constants;

import java.util.List;

/**
 * Created by chenxiaojun on 2017/11/6.
 */

public class VerAdapter extends RecyclerView.Adapter<VerAdapter.MViewHolder>{
    private LayoutInflater mInflater;
    private List<StationInfo> mList;
    private Context mContext;

    public VerAdapter(Context context, List<StationInfo> list) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
    }

    public void upDataList(List<StationInfo> mList) {
        this.mList = mList;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.ver_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VerAdapter.MViewHolder holder, int position) {
        final StationInfo stationInfo = mList.get(position);
//        holder.ip.setText(stationInfo.getName());
        holder.ip.setText(stationInfo.getIp());
        holder.softver1.setText(String.format(mContext.getString(R.string.softver1),stationInfo.getSoftVer1()));
        holder.softver2.setText(String.format(mContext.getString(R.string.softver2),stationInfo.getSoftVer2()));
        holder.softver3.setText(String.format(mContext.getString(R.string.softver3),stationInfo.getSoftVer3()));
        holder.softver4.setText(String.format(mContext.getString(R.string.softver4),stationInfo.getSoftVer4()));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class MViewHolder extends RecyclerView.ViewHolder{
        final TextView ip;
        final TextView softver1;
        final TextView softver2;
        final TextView softver3;
        final TextView softver4;



        MViewHolder(View itemView) {
            super(itemView);
            ip = (TextView) itemView.findViewById(R.id.ip);
            softver1 = (TextView) itemView.findViewById(R.id.softVer1);
            softver2 = (TextView) itemView.findViewById(R.id.softVer2);
            softver3 = (TextView) itemView.findViewById(R.id.softVer3);
            softver4 = (TextView) itemView.findViewById(R.id.softVer4);
        }
    }
}
