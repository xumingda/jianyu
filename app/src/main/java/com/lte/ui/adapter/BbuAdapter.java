package com.lte.ui.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.communication.utils.LETLog;
import com.github.library.BaseMultiItemQuickAdapter;
import com.github.library.BaseViewHolder;
import com.lte.R;
import com.lte.data.CellConfig;
import com.lte.data.StationInfo;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.List;

import static com.communication.utils.LETLog.e;
import static com.communication.utils.LETLog.formatTime;

/**
 * Created by chenxiaojun on 2017/12/20.
 */

public class BbuAdapter extends BaseMultiItemQuickAdapter<StationInfo, BaseViewHolder> {


    private CheckListener onCheckListener;

    private List<StationInfo> mList;;
    private OnSeekBarChangeListener onSeekBarChangeListener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BbuAdapter(List<StationInfo> data,CheckListener onCheckListener) {
        super(data);
        addItemType(21, R.layout.station_21g_item);
        addItemType(22, R.layout.station_22g_item);
        addItemType(23, R.layout.station_2g_cmda_item);
        addItemType(4, R.layout.station_4g_item);
        mList =data;
        this.onCheckListener = onCheckListener;
    }
    @Override
    protected void convert(final BaseViewHolder helper, StationInfo item) {
        Log.d("convert","item :" + item );
        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                    onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                }
            }
        });
        switch (item.getItemType()) {
            case 21:
                helper.setText(R.id.name,"GSM移动");
                switch (item.getConnectionStatus()) {
                    case CRATE:
                        helper.setText(R.id.state,mContext.getString(R.string.crate_connect));
                        break;
                    case CONNECTED:
                        helper.setText(R.id.state,mContext.getString(R.string.connect));
                        break;
                    case DISCONNECTED:
                        helper.setText(R.id.state,mContext.getString(R.string.disConnected));
                        break;
                    case SCAN:
                        helper.setText(R.id.state,mContext.getString(R.string.scan3));
                        break;
                }
                if(item.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED){
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_green);
                }else {
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_red);
                }
//                helper.setText(R.id.mcc, String.format(mContext.getString(R.string.mcc), item.getMCC()));
//                helper.setText(R.id.mnc, String.format(mContext.getString(R.string.mnc), item.getMNC()));
//                helper.setText(R.id.lac, String.format(mContext.getString(R.string.lac), item.getLAC()));
//                helper.setText(R.id.ci, String.format(mContext.getString(R.string.ci), item.getCI()));
//                helper.setText(R.id.cro, String.format(mContext.getString(R.string.cro), item.getCRO()));
//                helper.setText(R.id.workModel, mContext.getString(item.getWorkModel() == 0 ? R.string.workModel0 : R.string.workModel1));
//                helper.setText(R.id.configModel, mContext.getString(item.getConfigModel() == 0 ? R.string.configModel0 : R.string.configModel1));
//                helper.setText(R.id.recaptureTime, String.format(mContext.getString(R.string.recaptureTime), item.getRecaptureTime()));
//                helper.setText(R.id.carrierFrequencyPoint, String.format(mContext.getString(R.string.carrierFrequencyPoint), item.getRecaptureTime()));
//                helper.setText(R.id.startingFrequencyPoint1, item.getStartingFrequencyPoint1() + "");
//                helper.setText(R.id.startingFrequencyPoint2, item.getStartingFrequencyPoint2() + "");
//                helper.setText(R.id.endFrequencyPoint1, item.getEndFrequencyPoint1() + "");
//                helper.setText(R.id.endFrequencyPoint2, item.getEndFrequencyPoint2() + "");
//                helper.setText(R.id.downlinkAttenuation, String.format(mContext.getString(R.string.downlinkAttenuation), item.getDownlinkAttenuation()));
//                helper.setText(R.id.uplinkAttenuation, String.format(mContext.getString(R.string.uplinkAttenuation), item.getUplinkAttenuation()));
//                helper.setText(R.id.frequencyOffset,String.format(mContext.getString(R.string.frequencyOffset), item.getFrequencyOffset()));
                helper.setOnClickListener(R.id.close_dbm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
//                helper.setOnClickListener(R.id.carrier_restart, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                helper.setOnClickListener(R.id.open_dbm, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                        onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                    }
                }
                });
                helper.setOnClickListener(R.id.scan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
//                helper.setOnClickListener(R.id.scan1, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                helper.setOnClickListener(R.id.system_restart, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
                break;
            case 22:
                helper.setText(R.id.name,"GSM联通");
                switch (item.getConnectionStatus()) {
                    case CRATE:
                        helper.setText(R.id.state,mContext.getString(R.string.crate_connect));
                        break;
                    case CONNECTED:
                        helper.setText(R.id.state,mContext.getString(R.string.connect));
                        break;
                    case DISCONNECTED:
                        helper.setText(R.id.state,mContext.getString(R.string.disConnected));
                        break;
                    case SCAN:
                        helper.setText(R.id.state,mContext.getString(R.string.scan3));
                        break;
                }
                if(item.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED){
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_green);
                }else {
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_red);
                }
//                helper.setText(R.id.mcc1,String.format(mContext.getString(R.string.mcc), item.getMCC1()));
//                helper.setText(R.id.mnc1,String.format(mContext.getString(R.string.mnc), item.getMNC1()));
//                helper.setText(R.id.lac1,String.format(mContext.getString(R.string.lac), item.getLAC1()));
//                helper.setText(R.id.ci1,String.format(mContext.getString(R.string.ci), item.getCI1()));
//                helper.setText(R.id.cro1,String.format(mContext.getString(R.string.cro), item.getCRO1()));
//                helper.setText(R.id.workModel1,mContext.getString(item.getWorkModel1() == 0? R.string.workModel0 :R.string.workModel1));
//                helper.setText(R.id.configModel1,mContext.getString(item.getConfigModel1() == 0? R.string.configModel0 :R.string.configModel1));
//                helper.setText(R.id.recaptureTime1,String.format(mContext.getString(R.string.recaptureTime), item.getRecaptureTime1()));
//                helper.setText(R.id.carrierFrequencyPoint1,String.format(mContext.getString(R.string.carrierFrequencyPoint), item.getRecaptureTime1()));
//                helper.setText(R.id.startingFrequencyPoint11,item.getStartingFrequencyPoint11()+"");
//                helper.setText(R.id.startingFrequencyPoint21,item.getStartingFrequencyPoint21()+"");
//                helper.setText(R.id.endFrequencyPoint11,item.getEndFrequencyPoint11()+"");
//                helper.setText(R.id.endFrequencyPoint21,item.getEndFrequencyPoint21()+"");
//                helper.setText(R.id.downlinkAttenuation1,String.format(mContext.getString(R.string.downlinkAttenuation), item.getDownlinkAttenuation1()));
//                helper.setText(R.id.uplinkAttenuation1,String.format(mContext.getString(R.string.uplinkAttenuation), item.getUplinkAttenuation1()));
//                helper.setText(R.id.frequencyOffset1,String.format(mContext.getString(R.string.frequencyOffset), item.getFrequencyOffset1()));
                helper.setOnClickListener(R.id.close_dbm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
                helper.setOnClickListener(R.id.open_dbm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
//                helper.setOnClickListener(R.id.carrier_restart, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                helper.setOnClickListener(R.id.scan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
//                helper.setOnClickListener(R.id.scan1, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                helper.setOnClickListener(R.id.system_restart, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
                break;
            case 23:
                helper.setText(R.id.name,"CMDA");
                switch (item.getConnectionStatus()) {
                    case CRATE:
                        helper.setText(R.id.state,mContext.getString(R.string.crate_connect));
                        break;
                    case CONNECTED:
                        helper.setText(R.id.state,mContext.getString(R.string.connect));
                        helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_green);
                        break;
                    case DISCONNECTED:
                        helper.setText(R.id.state,mContext.getString(R.string.disConnected));
                        helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_red);
                        break;
                }
                if(item.getConnectionStatus() == StationInfo.ConnectionStatus.CONNECTED){
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_green);
                }else {
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_red);
                }
//                helper.setText(R.id.mcc,String.format(mContext.getString(R.string.mcc), item.getMCC()));
//                helper.setText(R.id.mnc,String.format(mContext.getString(R.string.mnc), item.getMNC()));
//                helper.setText(R.id.pn,String.format(mContext.getString(R.string.pn), item.getPn()));
//                helper.setText(R.id.ci,String.format(mContext.getString(R.string.ci), item.getCI()));
//                helper.setText(R.id.sid,String.format(mContext.getString(R.string.sid), item.getSid()));
//                helper.setText(R.id.nid,String.format(mContext.getString(R.string.nid), item.getNid()));
//                helper.setText(R.id.bsid,String.format(mContext.getString(R.string.bsid), item.getBsid()));
//                helper.setText(R.id.cdma,String.format(mContext.getString(R.string.cmda), item.getCmda()));
//                helper.setText(R.id.workModel,mContext.getString(item.getWorkModel() == 0? R.string.workModel0 :R.string.workModel1));
//                helper.setText(R.id.workModel1,mContext.getString(item.getWorkModel2() == 0? R.string.workModel01 :R.string.workModel11));
//                helper.setText(R.id.configModel,mContext.getString(item.getConfigModel() == 0? R.string.configModel0 :R.string.configModel1));
//                helper.setText(R.id.recaptureTime,String.format(mContext.getString(R.string.recaptureTime), item.getRecaptureTime()));
//                helper.setText(R.id.carrierFrequencyPoint,String.format(mContext.getString(R.string.carrierFrequencyPoint), item.getRecaptureTime()));
//                helper.setText(R.id.startingFrequencyPoint1,item.getStartingFrequencyPoint1()+"");
//                helper.setText(R.id.startingFrequencyPoint2,item.getStartingFrequencyPoint2()+"");
//                helper.setText(R.id.endFrequencyPoint1,item.getEndFrequencyPoint1()+"");
//                helper.setText(R.id.endFrequencyPoint2,item.getEndFrequencyPoint2()+"");
//                helper.setText(R.id.downlinkAttenuation,String.format(mContext.getString(R.string.downlinkAttenuation), item.getDownlinkAttenuation()));
//                helper.setText(R.id.uplinkAttenuation,String.format(mContext.getString(R.string.uplinkAttenuation), item.getUplinkAttenuation()));
//                helper.setText(R.id.frequencyOffset,String.format(mContext.getString(R.string.frequencyOffset), item.getFrequencyOffset()));
                helper.setOnClickListener(R.id.close_dbm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
//                helper.setOnClickListener(R.id.carrier_restart, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                helper.setOnClickListener(R.id.open_dbm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
//                helper.setOnClickListener(R.id.scan, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
//                helper.setOnClickListener(R.id.scan1, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                helper.setOnClickListener(R.id.system_restart, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
                        }
                    }
                });
                break;
            case 4:
                CellConfig cellConfig = item.getCellConfig();
                String band = "";
                if(cellConfig != null){
                    int downlink_frequency_point = cellConfig.getDownlink_frequency_point();
                    if(37750<=downlink_frequency_point && downlink_frequency_point<=38249||downlink_frequency_point==40936){
                        band = "band38/41";
                    }else if(38250<=downlink_frequency_point && downlink_frequency_point<=38649){
                        band = "band39";
                    }else if(38650<=downlink_frequency_point && downlink_frequency_point<=39649){
                        band = "band40";
                    }else if(0<=downlink_frequency_point && downlink_frequency_point<=299){
                        band = "band1电信";
                    }else if(300<=downlink_frequency_point && downlink_frequency_point<=599){
                        band = "band1联通";
                    } else if(1200<=downlink_frequency_point && downlink_frequency_point<=1699){
                        band = "band3联通";
                    }else if(1700<=downlink_frequency_point && downlink_frequency_point<=1949){
                        band = "band3电信";
                    }
                    if (item.getIp() != null && item.getIp().length() > 15) {
                        helper.setText(R.id.name, band+"(" + item.getIp().substring(15, 16)+")");
                    } else {
                        helper.setText(R.id.name, band );
                    }
//                    if (item.getIp() != null && item.getIp().length() > 15) {
//                          helper.setText(R.id.name, band );
//                    }else {
//                        helper.setText(R.id.name, band );
//                    }
                }else {
                    if (item.getIp() != null && item.getIp().length() > 15) {
                        helper.setText(R.id.name, "BBU" + item.getIp().substring(15, 16));
                    } else {
                        helper.setText(R.id.name, "BBU");
                    }
                }
                if(item.isCellConfig()){
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_green);
                }else {
                    helper.setBackgroundRes(R.id.img_operator,R.mipmap.wifi_red);
                }
                if(System.currentTimeMillis() - item.getCurrentTime() < 20*1000L){
                    switch (item.getConnectionStatus()) {
                        case CRATE:
                            helper.setText(R.id.link,mContext.getString(R.string.crate_connect));
                            break;
                        case CONNECTED:
                            helper.setText(R.id.link,mContext.getString(R.string.connect));
                            break;
                        case DISCONNECTED:
                            helper.setText(R.id.link,mContext.getString(R.string.disConnected));
                            helper.setText(R.id.state,mContext.getString(R.string.unConfig));
                            helper.setText(R.id.power,mContext.getString(R.string.close));
//                            helper.setText(R.id.soft_state,"");
//                        helper.setText(R.id.cpu_tem,String.format(mContext.getString(R.string.cpu_tem), 0));
//                        helper.setText(R.id.cpu_use,String.format(mContext.getString(R.string.cpu_use),0) + "%");
//                        helper.setText(R.id.rom_use,String.format(mContext.getString(R.string.rom_use), 0) + "%");
                            helper.setText(R.id.tem,String.format(mContext.getString(R.string.tem), 0));
                            break;
                    }
                    switch (item.getConfigState()) {
                        case UN_CONFIG:
                            helper.setText(R.id.state,mContext.getString(R.string.unConfig));
                            break;
                        case INIT_CONFIG_ING:
                            helper.setText(R.id.state,mContext.getString(R.string.configing));
                            break;
                        case INIT_CONFIG_ED:
                        case SET_SYSTEM_TIME:
                            helper.setText(R.id.state,mContext.getString(R.string.set_time));
                            break;
                        case CLOSE_DBM:
                            helper.setText(R.id.state,mContext.getString(R.string.close_dbm));
                            break;
                        case QUERY_SYSTEM_STATUS:
                            helper.setText(R.id.state,mContext.getString(R.string.query_status));
                            break;
                        case START_SCAN:
                            helper.setText(R.id.state,mContext.getString(R.string.start_scan));
                            break;
                        case SCAN_ED:
                            helper.setText(R.id.state,mContext.getString(R.string.scaned));
                            break;
                        case CELL_CONFIG_ING:
                            helper.setText(R.id.state,mContext.getString(R.string.cell_config));
                            break;
                        case CELL_CONFIG_ED:
                            helper.setText(R.id.state,mContext.getString(R.string.configed));
                            break;
                        case OPEN_DBM:
                            helper.setText(R.id.state,mContext.getString(R.string.configed));
                            break;
                        case OPEN_DBM_SUCCESS:
                            helper.setText(R.id.state,mContext.getString(R.string.configed));
                            break;
                    }
                }else {
                    helper.setText(R.id.state,mContext.getString(R.string.unConfig));
                    helper.setText(R.id.link,mContext.getString(R.string.disConnected));
                    helper.setText(R.id.state,mContext.getString(R.string.unConfig));
                    helper.setText(R.id.power,mContext.getString(R.string.close));
//                    helper.setText(R.id.soft_state,"");
                    helper.setText(R.id.tem,String.format(mContext.getString(R.string.tem), 0));
                }

                helper.setText(R.id.power,item.isConfigDBM() ? item.getDBM() + "dbm" : mContext.getString(R.string.close));
//                helper.setText(R.id.soft_state,item.getSoft_state());
//                helper.setText(R.id.cpu_tem,String.format(mContext.getString(R.string.cpu_tem), item.getCpu_tem()));
//                helper.setText(R.id.cpu_use,String.format(mContext.getString(R.string.cpu_use), item.getCpu_use()) + "%");
//                helper.setText(R.id.rom_use,String.format(mContext.getString(R.string.rom_use), item.getRom_use()) + "%");
                helper.setText(R.id.tem,String.format(mContext.getString(R.string.tem), item.getTem()));
//                helper.setOnClickListener(R.id.open_dbm, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (onCheckListener != null && mList.size() > helper.getAdapterPosition() && helper.getAdapterPosition() >= 0) {
//                            onCheckListener.onClick(v, mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
//                IndicatorSeekBar seekBar = helper.getView(R.id.seekBar);
//                seekBar.setProgress(16);
//                seekBar.setOnSeekChangeListener(new IndicatorSeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
//                        if(onSeekBarChangeListener != null){
//                            onSeekBarChangeListener.onProgressChanged(seekBar,progress,fromUserTouch,mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//
//                    @Override
//                    public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String tickBelowText, boolean fromUserTouch) {
//
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
//                        if (onSeekBarChangeListener != null){
//                            onSeekBarChangeListener.onStartTrackingTouch(seekBar,mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//
//                    @Override
//                    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
//                        if (onSeekBarChangeListener != null){
//                            onSeekBarChangeListener.onStopTrackingTouch(seekBar,mList.get(helper.getAdapterPosition()));
//                        }
//                    }
//                });
                break;
        }
    }
    public void setSeekBarChangListener(OnSeekBarChangeListener onSeekBarChangeListener){
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }
    @Override
    public void setNewData(List<StationInfo> data) {
        super.setNewData(data);
        this.mList = data;
    }

    public interface CheckListener {
        void onClick(View view, StationInfo stationInfo);
    }
    public interface OnSeekBarChangeListener {

        public void onProgressChanged(IndicatorSeekBar seekBar, int progress, boolean fromUser,StationInfo stationInfo);


        public void onStartTrackingTouch(IndicatorSeekBar seekBar,StationInfo stationInfo);

        public void onStopTrackingTouch(IndicatorSeekBar seekBar,StationInfo stationInfo) ;
    }
}
