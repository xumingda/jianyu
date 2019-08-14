package com.lte.ui.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.App;
import com.communication.BaseApplication;
import com.communication.utils.DateUtil;
import com.communication.utils.LETLog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.lte.R;
import com.lte.data.TargetBean;
import com.lte.ui.event.TargetListMessage;
import com.lte.ui.fragment.second.CQYFragment;
import com.lte.utils.CHexConver;
import com.lte.utils.Constants;
import com.lte.utils.SendCommendHelper;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ThreadUtils;
import com.lte.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * A simple {@link Fragment} subclass.
 */
public class TargetHomeFragment extends BaseMainFragment {


    BarChart mBarChat;
    ListView mlvTarget;
    long time = System.currentTimeMillis();
    public static ArrayList<TargetBean> mDatas;
    static int selectedPosition = 0;
    TargetHomeAdapter mAdapter;
    //当前选中的imsi
    String IMSI;
    //柱状图的数据集合
    List<BarEntry> value1 = new ArrayList<>();
    List<BarEntry> value2 = new ArrayList<>();

    BarDataSet set1;
    BarDataSet set2;

    Boolean selected = false;

    private XAxis xAxis; //X坐标轴
    private YAxis yAxis1; //Y坐标轴
    private YAxis yAxis2; //Y坐标轴
    BarData data;

    Float rsrp;
    Float delay;

    public static SimpleDateFormat format;

    int xLabel = 1;

    boolean itemChanged = false;
    //音频池
    SoundPool mPool;
    //缓存声音文件id
    Map<Float, Integer> map;
    int poolID;

    public static boolean voiceOn;
    boolean voiceComplete = false;

    private TextView clear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        View view = inflater.inflate(R.layout.fragment_target_home, container, false);
        initView(view);
        mDatas = new ArrayList<>();
        mAdapter = new TargetHomeAdapter();
        mlvTarget.setAdapter(mAdapter);

        return view;
    }

    private void initView(View view) {

        mBarChat = (BarChart) view.findViewById(R.id.barchat);
        mlvTarget = (ListView) view.findViewById(R.id.lv_target);

        clear = (TextView) view.findViewById(R.id.clear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        clear();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        //加载音频文件到缓存中
        ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

                map = new HashMap<>();
                Field[] fields = R.raw.class.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    try {
                        int id = fields[i].getInt(R.raw.class);
                        String name = getResources().getResourceName(id);
                        name = name.substring(name.length() - 3);
                        Float num = Float.parseFloat(name);
                        poolID = mPool.load(BaseApplication.getInstance(), id, 1);
                        map.put(num, poolID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                mPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        //                        mPool.play(v01,1,1,0,0,1);
                        voiceOn = SharedPreferencesUtil.getBooleanConfig(_mActivity, "sniffer", Constants.VOICEON, true);
                        voiceComplete = true;

                    }
                });
            }
        });
        /**
         * 以下为柱状图初始化设置
         */

        Description description = new Description();
        description.setText("");//设置描述为空
        xAxis = mBarChat.getXAxis();
        yAxis1 = mBarChat.getAxisLeft();//场强的Y轴参数
        yAxis2 = mBarChat.getAxisRight();//时延的Y轴参数

        xAxis.setAxisMinimum(-0.5f);

        yAxis1.setAxisMinimum(0);
        yAxis1.setAxisMaximum(220);
        yAxis2.setAxisMinimum(0);
        yAxis2.setAxisMaximum(220);
        xAxis.setEnabled(false);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴在下边

        xAxis.setDrawGridLines(false);//不要竖线网格

        mBarChat.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴
        mBarChat.setDescription(description);
        mBarChat.setNoDataText("等待数据中");
        mBarChat.setNoDataTextColor(Color.RED);
        mBarChat.setNoDataTextTypeface(DEFAULT_BOLD);
        mBarChat.setDrawGridBackground(true);
        mBarChat.setDrawBorders(true);
        mBarChat.setTouchEnabled(false);


        //notifydatachannged,invalidate必须和此段代码在一起
        if (mBarChat.getData() != null && mBarChat.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChat.getData().getDataSetByIndex(0);
            set1.setValues(value1);
            set2 = (BarDataSet) mBarChat.getData().getDataSetByIndex(1);
            set2.setValues(value2);
            xAxis.setAxisMinimum(-0.5f);
            List<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            data = new BarData(dataSets);
            data.setBarWidth(0.1F);
            data.groupBars(0.3f, 0.25f, 0.05f);

            mBarChat.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(value1, "场强");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.BLUE);
            set1.setValueTextSize(6f);

            set2 = new BarDataSet(value2, "时延");
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            set2.setValueTextSize(6f);


            List<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            data = new BarData(dataSets);
            data.setBarWidth(0.1F);
            data.groupBars(0.3f, 0.25f, 0.05f);
            xAxis.setAxisMinimum(-0.5f);
            mBarChat.setData(data);
            mBarChat.notifyDataSetChanged();
        }
        /**
         * 以上为柱状图初始化设置
         */
        //xmd取消item点击
//        mlvTarget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selected = true;
//                if (selectedPosition == position) {
//                    itemChanged = false;
//                } else {
//                    itemChanged = true;
//                }
//                if (itemChanged) {
//                    mBarChat.setData(null);
//                    selectedPosition = position;
//                    IMSI = mDatas.get(selectedPosition).getImsi();
//
//                    mBarChat.clear();
//                    itemChanged = false;
//                    data.groupBars(0.3f, 0.25f, 0.05f);
//                }
//            }
//        });


    }

    public synchronized void updateData(Float f1, Float f2) {


        BarEntry barEntry1 = new BarEntry(xLabel, f1);
        BarEntry barEntry2 = new BarEntry(xLabel, f2);
        xLabel++;

        if (mBarChat.getData() != null && mBarChat.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChat.getData().getDataSetByIndex(0);
            set1.setBarBorderWidth(0.1f);
            set1.setValueTextSize(6f);
            set2 = (BarDataSet) mBarChat.getData().getDataSetByIndex(1);
            set2.setBarBorderWidth(0.1f);
            set2.setValueTextSize(6f);
            if (set1.getEntryCount() > 8) {
                set1.removeEntry(0);
                set2.removeEntry(0);
            }
            set1.addEntry(barEntry1);
            set2.addEntry(barEntry2);

            List<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            data = new BarData(dataSets);
            data.groupBars(0.3f, 0.25f, 0.05f);
            mBarChat.setData(data);
            set2.setVisible(true);
            mBarChat.setVisibleXRangeMaximum(20);
            mBarChat.setVisibleXRangeMinimum(20);
            xAxis.setAxisMinimum(-0.5f);
            mBarChat.getData().notifyDataChanged();

        } else {
            set1.clear();
            set2.clear();
            value1.clear();
            value2.clear();
            data.clearValues();
            set1 = new BarDataSet(value1, "场强");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.BLUE);
            set1.setBarBorderWidth(0.1f);
            set1.setValueTextSize(6f);
            set1.addEntry(barEntry1);

            set2 = new BarDataSet(value2, "时延");
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setColor(Color.RED);
            set2.setBarBorderWidth(0.1f);
            set2.setValueTextSize(6f);
            set2.addEntry(barEntry2);

            List<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            data = new BarData(dataSets);
            data.groupBars(0.3f, 0.25f, 0.05f);
            mBarChat.setData(data);
            mBarChat.setVisibleXRangeMaximum(20);

            xAxis.setAxisMinimum(-0.5f);
        }


        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mBarChat.notifyDataSetChanged();
                mBarChat.invalidate();
            }
        });
    }
    private void clear(){
        Description description = new Description();
        description.setText("");//设置描述为空
        xAxis = mBarChat.getXAxis();
        yAxis1 = mBarChat.getAxisLeft();//场强的Y轴参数
        yAxis2 = mBarChat.getAxisRight();//时延的Y轴参数

        xAxis.setAxisMinimum(-0.5f);

        yAxis1.setAxisMinimum(0);
        yAxis1.setAxisMaximum(220);
        yAxis2.setAxisMinimum(0);
        yAxis2.setAxisMaximum(220);
        xAxis.setEnabled(false);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴在下边

        xAxis.setDrawGridLines(false);//不要竖线网格

        mBarChat.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴
        mBarChat.setDescription(description);
        mBarChat.setNoDataText("等待数据中");
        mBarChat.setNoDataTextColor(Color.RED);
        mBarChat.setNoDataTextTypeface(DEFAULT_BOLD);
        mBarChat.setDrawGridBackground(true);
        mBarChat.setDrawBorders(true);
        mBarChat.setTouchEnabled(false);

        set1.clear();
        set2.clear();
        value1.clear();
        value2.clear();
        data.clearValues();
        set1 = new BarDataSet(value1, "场强");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.BLUE);
        set1.setValueTextSize(6f);

        set2 = new BarDataSet(value2, "时延");
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setValueTextSize(6f);


        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        data = new BarData(dataSets);
        data.setBarWidth(0.1F);
        data.groupBars(0.3f, 0.25f, 0.05f);
        xAxis.setAxisMinimum(-0.5f);
        mBarChat.setData(data);
        IMSI = "";
        mBarChat.clearAllViewportJobs();
        mBarChat.notifyDataSetChanged();
        mBarChat.invalidate();
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mDatas.clear();
                mAdapter.setDatas(mDatas);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
    @Subscribe
    public void onTargetListMessage(final TargetListMessage event) {

        if ((event == null) || event.targetBean == null) {
            return;
        }
        if (event.clear) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LETLog.d("clear");
                    Description description = new Description();
                    description.setText("");//设置描述为空
                    xAxis = mBarChat.getXAxis();
                    yAxis1 = mBarChat.getAxisLeft();//场强的Y轴参数
                    yAxis2 = mBarChat.getAxisRight();//时延的Y轴参数

                    xAxis.setAxisMinimum(-0.5f);

                    yAxis1.setAxisMinimum(0);
                    yAxis1.setAxisMaximum(220);
                    yAxis2.setAxisMinimum(0);
                    yAxis2.setAxisMaximum(220);
                    xAxis.setEnabled(false);

                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//X轴在下边

                    xAxis.setDrawGridLines(false);//不要竖线网格

                    mBarChat.getAxisRight().setEnabled(false); // 隐藏右边 的坐标轴
                    mBarChat.setDescription(description);
                    mBarChat.setNoDataText("等待数据中");
                    mBarChat.setNoDataTextColor(Color.RED);
                    mBarChat.setNoDataTextTypeface(DEFAULT_BOLD);
                    mBarChat.setDrawGridBackground(true);
                    mBarChat.setDrawBorders(true);
                    mBarChat.setTouchEnabled(false);

                    set1.clear();
                    set2.clear();
                    value1.clear();
                    value2.clear();
                    data.clearValues();
                    set1 = new BarDataSet(value1, "场强");
                    set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                    set1.setColor(Color.BLUE);
                    set1.setValueTextSize(6f);

                    set2 = new BarDataSet(value2, "时延");
                    set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
                    set2.setColor(Color.RED);
                    set2.setValueTextSize(6f);


                    List<IBarDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set1);
                    dataSets.add(set2);

                    data = new BarData(dataSets);
                    data.setBarWidth(0.1F);
                    data.groupBars(0.3f, 0.25f, 0.05f);
                    xAxis.setAxisMinimum(-0.5f);
                    mBarChat.setData(data);
                    IMSI = "";
                    mBarChat.clearAllViewportJobs();
                    mBarChat.notifyDataSetChanged();
                    mBarChat.invalidate();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mDatas.clear();
                            mAdapter.setDatas(mDatas);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }, 5000L);
        } else {
            ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
                boolean tag = false;

                @Override
                public void run() {

                    //更新列表
                    TargetBean bean = event.targetBean;
                    LETLog.d(" TargetBean :" + bean);
//                Log.d("TargetBean "," TargetBean :" + bean);
                    for (int j = 0; j < mDatas.size(); j++) {
                        tag = false;
                        if (mDatas.get(j) != null && mDatas.get(j).getImsi().equals(bean.getImsi())) {
                            bean.setCount(j);
                            mDatas.set(j, bean);
                            tag = true;
                            break;
                        }
                    }
                    if (tag == false) {
                        bean.setCount(mDatas.size());
//                    synchronized (mDatas) {
                        mDatas.add(bean);
                        tag = true;
                        IMSI = mDatas.get(0).getImsi();
//                    }

                    }
                    if (bean.getImsi().equals(IMSI)) {
                        rsrp = bean.getSinr();
                        delay = Float.parseFloat(bean.getDelay());
                        updateData(rsrp, delay);
                    }
                    bean.setTime(format.format(new Date()));
                    if (voiceOn && voiceComplete && bean.getImsi().equals(IMSI)) {
                        float j = bean.getSinr();
                        while (j > 0) {
                            if (map.get(j) != null) {
                                mPool.play(map.get(j), 1, 1, 0, 0, 1);
                                break;
                            }
                            //向下取值一直到有对应的声音文件
                            j--;
                        }
                    }
                    if (mDatas.size() == 1) {
                        IMSI = mDatas.get(0).getImsi();
                    }
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setDatas(mDatas);
                            mAdapter.notifyDataSetChanged();

                        }
                    });
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mPool!=null){
            mPool.release();
            EventBus.getDefault().unregister(this);
        }
    }

    public static TargetHomeFragment newInstance() {
        return new TargetHomeFragment();
    }

    public class TargetHomeAdapter extends BaseAdapter {

        ArrayList<TargetBean> datas = new ArrayList<>();

        public void setDatas(ArrayList<TargetBean> mDatas) {
            datas = mDatas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final TargetHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(BaseApplication.getInstance());
                convertView = inflater.inflate(R.layout.item_lv_target, parent, false);
                holder = new TargetHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (TargetHolder) convertView.getTag();
            }

            holder.mImsi.setText(datas.get(position).getImsi());
            holder.mRsrp.setText(String.valueOf(datas.get(position).getSinr()));
            holder.mFreq.setText(datas.get(position).getFreq());
            holder.mDelay.setText(String.valueOf(datas.get(position).getDelay()));
            holder.mBbu.setText(datas.get(position).getBbu());
            holder.mpci.setText(datas.get(position).getPci());
            holder.mTime.setText(datas.get(position).getTime());
            holder.Operator.setText(DateUtil.getOpera(datas.get(position).getImsi()));
            holder.btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    selected = true;
//                    //xmd增加选中效果
//                    if (selectedPosition == position) {
//                        itemChanged = false;
//                    } else {
//                        itemChanged = true;
//                    }
//                    if (itemChanged) {
                        mBarChat.setData(null);
                        IMSI = mDatas.get(position).getImsi();
                        holder.ll_bg.setBackgroundColor(UIUtils.getColor(R.color.blue));
                        mBarChat.clear();
                        itemChanged = false;
                        data.groupBars(0.3f, 0.25f, 0.05f);
//                    }


                    //1是新，0是老的
                    if(App.type==1) {
                        if (Integer.parseInt(datas.get(position).getFreq()) + 4 > 28359) {
                            App.pattern = "23DC04";
                        } else {
                            App.pattern = "23DC05";
                        }

                        App.channel = CHexConver.toHex(Integer.parseInt(datas.get(position).getFreq()) + 4);

                        App.pci = CHexConver.toHex(Integer.parseInt(datas.get(position).getPci()));
                    }else{

                        if (Integer.parseInt(datas.get(position).getFreq()) + 4 > 28359) {
                            //tdd
                            App.pattern = SendCommendHelper.setTDD();

                        } else {
                            //fdd
                            App.pattern = SendCommendHelper.setFDD();

                        }

                        //老蓝牙才有同步频点
                        App.channel = SendCommendHelper.setChannel()+ CHexConver.toHex(Integer.parseInt(datas.get(position).getFreq()) + 4 );
                        App.pci = SendCommendHelper.setPCI()+ CHexConver.toHex(Integer.parseInt(datas.get(position).getPci()));
                    }
                    //xmd调用重新设置蓝牙参数
                    CQYFragment.item=0;
                }
            });
            return convertView;
        }

        public class TargetHolder {
            public LinearLayout ll_bg;
            public final Button btn_send;
            public final TextView mImsi;

            public final TextView mTime;

            public final TextView mRsrp;

            public final TextView mFreq;

            public final TextView mDelay;

            public final TextView mBbu;

            public final TextView mpci;

            public final TextView Operator;


            @SuppressLint("CutPasteId")
            public TargetHolder(View itemView) {
                ll_bg = (LinearLayout) itemView.findViewById(R.id.ll_bg);
                mImsi = (TextView) itemView.findViewById(R.id.imsi);
                mTime = (TextView) itemView.findViewById(R.id.time);
                mRsrp = (TextView) itemView.findViewById(R.id.rsrp);
                mFreq = (TextView) itemView.findViewById(R.id.freq);
                mDelay = (TextView) itemView.findViewById(R.id.delay);
                mBbu = (TextView) itemView.findViewById(R.id.bbu);
                mpci = (TextView) itemView.findViewById(R.id.pci);
                Operator = (TextView) itemView.findViewById(R.id.Operator);
                btn_send = (Button) itemView.findViewById(R.id.btn_send);
            }
        }
    }

}
