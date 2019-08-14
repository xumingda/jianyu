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
import com.lte.data.table.BlackListTable;
import com.lte.utils.Constants;

import java.util.List;

import io.realm.OrderedRealmCollection;

import static com.lte.utils.DateUtils.getNowDate;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class BlackAdapter extends RealmRecyclerViewAdapter<BlackListTable,BlackAdapter.MViewHolder> {
    private final static String TAG = "FunctionAdapter";
    private LayoutInflater mInflater;
    private OrderedRealmCollection<BlackListTable> mList;
    private Context mContext;
    private CheckListener onCheckListener;
    private final RecyclerView recyclerView;

    public BlackAdapter(Context context, OrderedRealmCollection<BlackListTable> list, CheckListener onCheckListener, RecyclerView recyclerView) {
        super(list,true,recyclerView);
        this.recyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(OrderedRealmCollection<BlackListTable> mList) {
        this.mList = mList;
        super.updateData(mList);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.black_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final BlackListTable imsiData = mList.get(position);
        Log.d("BlackAdapter",imsiData.toString());
        if(imsiData.getOperator() != null){
            holder.operator.setText(imsiData.getOperator());
        }
        if(imsiData.getImsi() != null){
            if(imsiData.getImsi().startsWith("46000")||imsiData.getImsi().startsWith("46002")
                    ||imsiData.getImsi().startsWith("46004")||imsiData.getImsi().startsWith("46007")){
                holder.operator.setText(mContext.getString(R.string.yidong));
            }else if(imsiData.getImsi().startsWith("46001")||imsiData.getImsi().startsWith("46006")
                    ||imsiData.getImsi().startsWith("46009")){
                holder.operator.setText(mContext.getString(R.string.liantong));
            }else if(imsiData.getImsi().startsWith("46003")||imsiData.getImsi().startsWith("46005")
                    ||imsiData.getImsi().startsWith("46011")){
                holder.operator.setText(mContext.getString(R.string.dianxin));
            }
            holder.imsi1.setText(imsiData.getImsi());
        }else {
            holder.imsi1.setText("");
        }
        holder.imei.setText(imsiData.getImei() == null ? "" : imsiData.getImei());

        holder.phoneUsername.setText(imsiData.getPhoneUsername()==null?"":imsiData.getPhoneUsername());
        holder.position.setText(imsiData.getPosition()==null?"":imsiData.getPosition());
//        if(imsiData.getStationName() != null){
//            if(imsiData.getStationName().length() >16){
//                holder.bbu.setText(imsiData.getStationName().substring(15,16));
//
//            }
//        }
//        if(imsiData.getAttribuation() != null){
            holder.attribuation.setText((mList.size() - position)+"");
//        }
//        if(imsiData.getTime() != 0){
//            holder.timestamp.setText(getNowDate(imsiData.getTime()));
//        }
        if(imsiData.getMobile() != null){
            holder.mobile.setText(imsiData.getMobile());
        }else {
            holder.mobile.setText("");
        }
    }

//    @Override
//    public int getItemCount() {
//        return mList != null ? mList.size() : 0;
//    }


//    public void upDateData(OrderedRealmCollection<ImsiDataTable>  realmResults) {
//        this.mList = realmResults;
//        super.clearData(realmResults);
//    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView operator;
        final TextView imsi1;
        final TextView imei;
        final TextView attribuation;
//        final TextView timestamp;
//        final TextView bbu;
        final TextView mobile;
        final TextView phoneUsername;
        final TextView position;


        MViewHolder(View itemView) {
            super(itemView);
            operator = (TextView) itemView.findViewById(R.id.number);
            imsi1 = (TextView) itemView.findViewById(R.id.imsi1);
            attribuation = (TextView) itemView.findViewById(R.id.xuhao);
//            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
//            bbu = (TextView)itemView.findViewById(R.id.bbu);
            mobile = (TextView) itemView.findViewById(R.id.mobile);
            imei = (TextView) itemView.findViewById(R.id.imei);
            phoneUsername=(TextView) itemView.findViewById(R.id.phoneUsername);
            position=(TextView)itemView.findViewById(R.id.position);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                    if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
                        onCheckListener.onClick(v, mList.get(getAdapterPosition()));
                    }
        }
    }
    public interface CheckListener{
        void onClick(View view, BlackListTable imsiData);
    }
}
