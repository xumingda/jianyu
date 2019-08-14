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
import android.widget.ScrollView;
import android.widget.TextView;

import com.communication.utils.LETLog;
import com.lte.R;
import com.lte.data.CellConfig;
import com.lte.data.StationInfo;
import com.lte.utils.Constants;

import java.security.Key;
import java.util.List;

import static com.lte.data.StationInfo.ConfigState.UN_CONFIG;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.MViewHolder> {
    private final static String TAG = "ConfigAdapter";
    private LayoutInflater mInflater;
    private List<StationInfo> mList;
    private Context mContext;
    private CheckListener onCheckListener;

    public ConfigAdapter(Context context, List<StationInfo> list, CheckListener onCheckListener) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(List<StationInfo> mList) {
        this.mList = mList;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.station_item1, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final StationInfo stationInfo = mList.get(position);
//        holder.ip.setText(stationInfo.getName());
        if(stationInfo.getType() == 4){
            if(stationInfo.getIp() != null && stationInfo.getIp().length()>15){
                holder.ip.setText("BBU"+stationInfo.getIp().substring(15,16));
            }else {
                holder.ip.setText("BBU");
            }
        }else if(stationInfo.getType() == 21){
            holder.ip.setText("BBU8");
        }else if(stationInfo.getType() == 22){
            holder.ip.setText("BBU9");
        }else if(stationInfo.getType() == 23){
            holder.ip.setText("BBU10");
        }
        switch (stationInfo.getConnectionStatus()) {
            case CRATE:
                holder.link.setText(mContext.getString(R.string.crate_connect));
                break;
            case CONNECTED:
                holder.link.setText(mContext.getString(R.string.connect));
                break;
            case DISCONNECTED:
                holder.link.setText(mContext.getString(R.string.disConnected));
                holder.state.setText("");
//                holder.freq.setText("");
//                holder.pci.setText( "");
//                holder.tac.setText( "");
//                holder.rssi.setText("");
                holder.power.setText("");
                holder.soft_state.setText("");
                holder.cpu_tem.setText("");
                holder.cpu_use.setText("");
                holder.rom_use.setText("");
                holder.tem.setText("");
                break;
        }
        switch (stationInfo.getConfigState()) {
            case UN_CONFIG:
                holder.state.setText(mContext.getString(R.string.unConfig));
                break;
            case INIT_CONFIG_ING:
                holder.state.setText(mContext.getString(R.string.configing));
                break;
            case INIT_CONFIG_ED:
                break;
            case SET_SYSTEM_TIME:
                holder.state.setText(mContext.getString(R.string.set_time));
                break;
            case CLOSE_DBM:
                holder.state.setText(mContext.getString(R.string.close_dbm));
                break;
            case QUERY_SYSTEM_STATUS:
                holder.state.setText(mContext.getString(R.string.query_status));
                break;
            case START_SCAN:
                holder.state.setText(mContext.getString(R.string.start_scan));
                break;
            case SCAN_ED:
                holder.state.setText(mContext.getString(R.string.scaned));
                break;
            case CELL_CONFIG_ING:
                holder.state.setText(mContext.getString(R.string.cell_config));
                break;
            case CELL_CONFIG_ED:
                holder.state.setText(mContext.getString(R.string.cell_configed));
                break;
            case OPEN_DBM:
                holder.state.setText(mContext.getString(R.string.open_dbm));
                break;
            case OPEN_DBM_SUCCESS:
                holder.state.setText(mContext.getString(R.string.configed));
                break;
        }
//        holder.freq.setText(stationInfo.getFreq() + "");
//        holder.pci.setText(stationInfo.getPci() + "");
//        holder.tac.setText(stationInfo.getTAC() + "");
//        holder.rssi.setText(stationInfo.getRSSI() + "");
        holder.power.setText(stationInfo.isConfigDBM() ? stationInfo.getDBM() + "dbm" : mContext.getString(R.string.close));
        holder.soft_state.setText(stationInfo.getSoft_state());
        holder.cpu_tem.setText(String.format(mContext.getString(R.string.cpu_tem), stationInfo.getCpu_tem()));
        holder.cpu_use.setText(String.format(mContext.getString(R.string.cpu_use), stationInfo.getCpu_use()) + "%");
        holder.rom_use.setText(String.format(mContext.getString(R.string.rom_use), stationInfo.getRom_use()) + "%");
        holder.tem.setText(String.format(mContext.getString(R.string.tem), stationInfo.getTem()));
        LETLog.d("ConfigAdapter cpu_tem ：" +stationInfo.getCpu_tem() + "tem ：" +stationInfo.getTem());
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position, List<Object> payloads) {
        Log.d("Adapter ", "key :" + (payloads == null || payloads.isEmpty()));
        final StationInfo stationInfo = mList.get(position);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            for (String key : bundle.keySet()) {
                Log.d("Adapter ", "key :" + key);
                switch (key) {
                    case Constants.NAME:
                        holder.ip.setText(bundle.getString(key));
                        break;
                    case Constants.STATE:
                        switch (bundle.getInt(key)) {
                            case 0:
                                holder.link.setText(mContext.getString(R.string.crate_connect));
                                break;
                            case 1:
                                holder.link.setText(mContext.getString(R.string.connect));
                                break;
                            case 2:
                                holder.link.setText(mContext.getString(R.string.disConnected));
                                holder.state.setText("");
//                                holder.freq.setText("");
//                                holder.pci.setText( "");
//                                holder.tac.setText( "");
//                                holder.rssi.setText("");
                                holder.power.setText("");
                                holder.soft_state.setText("");
                                holder.cpu_tem.setText("");
                                holder.cpu_use.setText("");
                                holder.rom_use.setText("");
                                holder.tem.setText("");
                                break;
                        }
                        break;
                    case Constants.DATA:
                        holder.power.setText(bundle.getBoolean(key) ? stationInfo.getDBM() + "dbm" : mContext.getString(R.string.close));
                        break;
                    case Constants.CONFIG_STATE:
                        switch (bundle.getInt(key)) {
                            case 0:
                                holder.state.setText(mContext.getString(R.string.unConfig));
                                break;
                            case 1:
                                holder.state.setText(mContext.getString(R.string.configing));
                                break;
                            case 2:
                                break;
                            case 3:
                                holder.state.setText(mContext.getString(R.string.set_time));
                                break;
                            case 4:
                                holder.state.setText(mContext.getString(R.string.close_dbm));
                                break;
                            case 5:
                                holder.state.setText(mContext.getString(R.string.query_status));
                                break;
                            case 6:
                                holder.state.setText(mContext.getString(R.string.start_scan));
                                break;
                            case 7:
                                holder.state.setText(mContext.getString(R.string.scaned));
                                break;
                            case 8:
                                holder.state.setText(mContext.getString(R.string.cell_config));
                                break;
                            case 9:
                                holder.state.setText(mContext.getString(R.string.cell_configed));
                                break;
                            case 10:
                                holder.state.setText(mContext.getString(R.string.open_dbm));
                                break;
                            case 11:
                                holder.state.setText(mContext.getString(R.string.configed));
                                holder.soft_state.setText(mContext.getString(R.string.bbu5));
                                break;
                        }
                        break;
                    case Constants.SOFT_STATE:
                        holder.soft_state.setText(bundle.getString(key));
                        if(TextUtils.equals(bundle.getString(key),mContext.getString(R.string.bbu5))){
                            holder.state.setText(mContext.getString(R.string.configed));
                        }
                        break;
                    case Constants.CPU_TEM:
                        holder.cpu_tem.setText(String.format(mContext.getString(R.string.cpu_tem), bundle.getInt(key)));
                        break;
                    case Constants.CPU_USE:
                        holder.cpu_use.setText(String.format(mContext.getString(R.string.cpu_use), bundle.getInt(key)) + "%");
                        break;
                    case Constants.ROM_UES:
                        holder.rom_use.setText(String.format(mContext.getString(R.string.rom_use), bundle.getInt(key)) + "%");
                        break;
                    case Constants.TEM:
                        holder.tem.setText(String.format(mContext.getString(R.string.tem), bundle.getInt(key)));
                        break;
//                    case Constants.FREQ:
//                        holder.freq.setText(bundle.getInt(key)+"");
//                        break;
//                    case Constants.PCI:
//                        holder.pci.setText(bundle.getInt(key)+"");
//                        break;
//                    case Constants.TAC:
//                        holder.tac.setText(bundle.getInt(key)+"");
//                        break;
//                    case Constants.RSSI:
//                        holder.rssi.setText(bundle.getInt(key)+"");
//                        break;
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView ip;
        final TextView link;
        final TextView state;
//        final TextView rssi;
//        final TextView freq;
//        final TextView tac;
//        final TextView pci;
        final TextView power;
        final TextView soft_state;
        final TextView cpu_tem;
        final TextView cpu_use;
        final TextView rom_use;
        final Button open_dbm;
        final TextView tem;


        MViewHolder(View itemView) {
            super(itemView);
            ip = (TextView) itemView.findViewById(R.id.ip);
            link = (TextView) itemView.findViewById(R.id.link);
            state = (TextView) itemView.findViewById(R.id.state);
//            freq = (TextView) itemView.findViewById(R.id.freq);
//            rssi = (TextView) itemView.findViewById(R.id.rssi);
//            tac = (TextView) itemView.findViewById(R.id.tac);
//            pci = (TextView) itemView.findViewById(R.id.pci);
            power = (TextView) itemView.findViewById(R.id.power);
            soft_state = (TextView) itemView.findViewById(R.id.soft_state);
            soft_state.setVisibility(View.INVISIBLE);//隐藏状态显示20190509 lph
            cpu_tem = (TextView) itemView.findViewById(R.id.cpu_tem);
            cpu_use = (TextView) itemView.findViewById(R.id.cpu_use);
            rom_use = (TextView) itemView.findViewById(R.id.rom_use);
            open_dbm = (Button) itemView.findViewById(R.id.open_dbm);
            tem = (TextView) itemView.findViewById(R.id.tem);
            itemView.setOnClickListener(this);
            open_dbm.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            switch (v.getId()){
//            if(v.getId() == R.id.open_dbm){
//                if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
//                    onCheckListener.onClick(v, mList.get(getAdapterPosition()));
//                }
//            }else {
            if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
                onCheckListener.onClick(v, mList.get(getAdapterPosition()));
            }
//            }

//                case R.id.cill_config:
//                    if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
//                        onCheckListener.onClick(v, mList.get(getAdapterPosition()));
//                    }
//                    break;
//            }
        }
    }

    public interface CheckListener {
        void onClick(View view, StationInfo stationInfo);
//        void onItemChick(View view,StationInfoTable stationInfo);
    }
}
