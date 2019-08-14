package com.lte.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.BlackListTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.WhiteListTable;
import com.lte.utils.ViewUtil;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

import static com.communication.utils.DateUtil.formatTime;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class DataReportOneAdapter extends RealmRecyclerViewAdapter<ImsiDataTable, DataReportOneAdapter.MViewHolder> {
    private final static String TAG = "FunctionAdapter";
    private LayoutInflater mInflater;
    private OrderedRealmCollection<ImsiDataTable> mList;
    private Context mContext;
    private CheckListener onCheckListener;
    private final RecyclerView recyclerView;

    private int number;

    public DataReportOneAdapter(Context context, OrderedRealmCollection<ImsiDataTable> list, CheckListener onCheckListener, RecyclerView recyclerView) {
        super(list, true, recyclerView);
        this.recyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(OrderedRealmCollection<ImsiDataTable> mList) {
        this.mList = mList;
        super.updateData(mList);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.data_report_one_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final ImsiDataTable imsiData = mList.get(position);
        Log.d("ImsiData", imsiData.toString());
        if (imsiData.getIsBlackAndWhite() == 1) {
//            if(imsiData.getMobile() == null){
            holder.operator.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.imsi1.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.xuhao.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.timestamp.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.bbu.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.mobile.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.imei1.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.attribution.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.number.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.phone_username.setTextColor(ViewUtil.getColor(mContext, R.color.black));
            holder.position.setTextColor(ViewUtil.getColor(mContext, R.color.black));

            RealmResults<BlackListTable> blackListTables= DataManager.getInstance().findBlackData();
            if(blackListTables.where().equalTo("imsi",imsiData.getImsi()).findAll().size()>0)
            {
                //String str=blackListTables.where().equalTo("imsi",imsiData.getImsi()).findFirst().getPhoneUsername();
                BlackListTable bt=blackListTables.where().equalTo("imsi",imsiData.getImsi()).findFirst();
                holder.phone_username.setText(bt.getPhoneUsername());
                holder.position.setText(bt.getPosition());
            }


//                holder.number.setTextColor(ViewUtil.getColor(mContext,R.color.red));
        } else if(imsiData.getIsBlackAndWhite() == 2){
            holder.operator.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.imsi1.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.xuhao.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.timestamp.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.bbu.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.mobile.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.imei1.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.attribution.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.number.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.phone_username.setTextColor(ViewUtil.getColor(mContext, R.color.green));
            holder.position.setTextColor(ViewUtil.getColor(mContext, R.color.green));

            RealmResults<WhiteListTable> whiteListTables= DataManager.getInstance().findWhiteData();
            if(whiteListTables.where().equalTo("imsi",imsiData.getImsi()).findAll().size()>0)
            {
                WhiteListTable bt=whiteListTables.where().equalTo("imsi",imsiData.getImsi()).findFirst();
                holder.phone_username.setText(bt.getPhoneUsername());
                holder.position.setText(bt.getPosition());
                //holder.phone_username.setText(whiteListTables.where().equalTo("imsi",imsiData.getImsi()).findFirst().getPhoneUsername());
            }

        }else {
//            if (imsiData.getMobile() == null) {
                holder.operator.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.imsi1.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.xuhao.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.timestamp.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.bbu.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.mobile.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.imei1.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.attribution.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.number.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.phone_username.setTextColor(ViewUtil.getColor(mContext, R.color.red));
                holder.phone_username.setText("");
                holder.position.setText("");
//            } else {
//                holder.operator.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.imsi1.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.xuhao.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.timestamp.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.bbu.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.mobile.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.imei1.setTextColor(ViewUtil.getColor(mContext, R.color.black));
//                holder.attribution.setTextColor(ViewUtil.getColor(mContext, R.color.black));
////                holder.number.setTextColor(ViewUtil.getColor(mContext,R.color.black));
//            }
        }
        holder.number.setText(imsiData.getNumber()+"");
        if (imsiData.getOperator() != null) {
            holder.operator.setText(imsiData.getOperator());
        }
        if (imsiData.getImsi() != null) {
            if (imsiData.getImsi().startsWith("46000") || imsiData.getImsi().startsWith("46002")
                    || imsiData.getImsi().startsWith("46004") || imsiData.getImsi().startsWith("46007")) {
                holder.operator.setText(mContext.getString(R.string.yidong));
            } else if (imsiData.getImsi().startsWith("46001") || imsiData.getImsi().startsWith("46006")
                    || imsiData.getImsi().startsWith("46009")) {
                holder.operator.setText(mContext.getString(R.string.liantong));
            } else if (imsiData.getImsi().startsWith("46003") || imsiData.getImsi().startsWith("46005")
                    || imsiData.getImsi().startsWith("46011")) {
                holder.operator.setText(mContext.getString(R.string.dianxin));
            }
            holder.imsi1.setText(imsiData.getImsi());
        } else {
            holder.imsi1.setText("");
        }
        if (imsiData.getImei() != null) {
            holder.imei1.setText(imsiData.getImei());
            holder.imei1.setVisibility(View.VISIBLE);
        } else {
            holder.imei1.setText("");
            holder.imei1.setVisibility(View.GONE);
        }
        if (imsiData.getBbu() != null) {
            holder.bbu.setText(imsiData.getBbu());
        } else {
            if (imsiData.getStationName() != null) {
                if (imsiData.getStationName().length() > 16) {
                    holder.bbu.setText(imsiData.getStationName().substring(15, 16));

                }
            } else {
                holder.bbu.setText("");
            }
        }
        holder.xuhao.setText(String.valueOf(getItemCount() - position) + "");
        if (imsiData.getTime() != 0) {
            holder.timestamp.setText(formatTime(imsiData.getTime()));
        }
        if (imsiData.getMobile() != null) {
            holder.mobile.setText(imsiData.getMobile());
//            holder.attribution.setText();
        } else {
            holder.mobile.setText("");
        }
//        holder.number.setText(imsiData.getNumber() +"");
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView operator;
        final TextView imsi1;
        final TextView xuhao;
        final TextView timestamp;
        final TextView bbu;
        final TextView mobile;
        final TextView imei1;
        final TextView attribution;
        final TextView number;
        final TextView phone_username;
        final TextView position;


        MViewHolder(View itemView) {
            super(itemView);
            operator = (TextView) itemView.findViewById(R.id.number);
            imsi1 = (TextView) itemView.findViewById(R.id.imsi1);
            xuhao = (TextView) itemView.findViewById(R.id.xuhao);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            bbu = (TextView) itemView.findViewById(R.id.bbu);
            mobile = (TextView) itemView.findViewById(R.id.mobile);
            imei1 = (TextView) itemView.findViewById(R.id.imei1);
            attribution = (TextView) itemView.findViewById(R.id.attribution);
            number = (TextView) itemView.findViewById(R.id.number1);
            phone_username=(TextView) itemView.findViewById(R.id.phoneUsername);
            position=(TextView) itemView.findViewById(R.id.position);

            itemView.setOnClickListener(this);
            imsi1.setOnClickListener(this);
            imei1.setOnClickListener(this);
            mobile.setOnClickListener(this);
            imsi1.setOnLongClickListener(this);
            imei1.setOnLongClickListener(this);
            mobile.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
//                        if(v == imsi1 || v== imei1 || v == mobile){
                onCheckListener.onClick(itemView, mList.get(getAdapterPosition()));
//                        }else {
//                            onCheckListener.onClick(v, mList.get(getAdapterPosition()));
//                        }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
                if (v == imsi1) {
                    if (TextUtils.isEmpty(mList.get(getAdapterPosition()).getImsi())) {
                        return true;
                    }
                }
                if (v == imei1) {
                    if (TextUtils.isEmpty(mList.get(getAdapterPosition()).getImei())) {
                        return true;
                    }
                }
                if (v == mobile) {
                    if (TextUtils.isEmpty(mList.get(getAdapterPosition()).getMobile())) {
                        return true;
                    }
                }
                onCheckListener.onLongClick(v, mList.get(getAdapterPosition()));
            }
            return true;
        }
    }

    public interface CheckListener {
        void onClick(View view, ImsiDataTable imsiData);

        void onLongClick(View view, ImsiDataTable imsiData);
    }
}
