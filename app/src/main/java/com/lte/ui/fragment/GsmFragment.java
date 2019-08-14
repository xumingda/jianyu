package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.GsmConfig;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.utils.Constants;
import com.lte.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/12/21.
 */

public class GsmFragment extends SupportFragment implements View.OnClickListener {


    private Long id;

    private MaterialSpinner spinner_gsm_bts1_band;
    //    private RadioGroup raGroupGSM;
//    private RadioGroup raGroup2GSM;
//    private MaterialSpinner spinner_gsm_bts2_band;
    private EditText et_gsm_bts1_bcc;
//    private EditText et_gsm_bts2_bcc;
    private EditText et_gsm_bts1_mcc;
//    private EditText et_gsm_bts2_mcc;
    private MaterialSpinner spinner_gsm_bts1_mnc;
//    private MaterialSpinner spinner_gsm_bts2_mnc;
    private EditText et_gsm_bts1_lac;
//    private EditText et_gsm_bts2_lac;
//    private EditText et_gsm_bts2_captime;
    private EditText et_gsm_bts1_captime;
    private EditText et_gsm_bts1_lowatt;
    private EditText et_gsm_bts1_upatt;
//    private EditText et_gsm_bts2_lowatt;
//    private EditText et_gsm_bts2_upatt;
    private EditText et_gsm_bts1_cro;
//    private EditText et_gsm_bts2_cro;
    private MaterialSpinner spinner_gsm_bts1_configmode;
//    private MaterialSpinner spinner_gsm_bts2_configmode;
    private TextView spinner_gsm_bts1_workmode;
//    private MaterialSpinner spinner_gsm_bts2_workmode;
    protected List<String> mCdmaWorkModelList = new ArrayList<>();
    protected List<String> mCdmaResetModelList = new ArrayList<>();
    protected List<String> mCdmaWorkModel1List = new ArrayList<>();
    protected List<String> mGSMBandList = new ArrayList<>();
    protected List<String> mGSMConfigModeList = new ArrayList<>();
    protected List<String> mGSMMncList1 = new ArrayList<>();
//    protected List<String> mGSMMncList2 = new ArrayList<>();
    protected List<String> mCROMList = new ArrayList<>();
    protected GsmConfig gsmConfig;

    private OnBackPressedListener mActivityListener;

    protected Button save;

    public static GsmFragment newInstance(Long ip) {
        GsmFragment fragment = new GsmFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, ip);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gsm_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        Bundle bundle = getArguments();
        id = bundle.getLong(Constants.ID);
        gsmConfig = DataManager.getInstance().findGsmConfigFrist();
        Log.d("Station :", " id :" + id + " gsmConfig :" + gsmConfig.id);
        save = (Button) view.findViewById(R.id.btn_new_account);
        save.setOnClickListener(this);
//        raGroupGSM = (RadioGroup) view.findViewById(R.id.rg_bts_enable_gsm);
//        raGroup2GSM = (RadioGroup) view.findViewById(R.id.rg_bts2_enable_gsm);
        spinner_gsm_bts1_band = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts1_band);
//        spinner_gsm_bts2_band = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts2_band);
        mGSMBandList.add("900");
        mGSMBandList.add("1800");
        spinner_gsm_bts1_band.setItems(mGSMBandList);
//        spinner_gsm_bts2_band.setItems(mGSMBandList);
        et_gsm_bts1_bcc = (EditText) view.findViewById(R.id.et_gsm_bts1_bcc);
//        et_gsm_bts2_bcc = (EditText) view.findViewById(R.id.et_gsm_bts2_bcc);
        et_gsm_bts1_mcc = (EditText) view.findViewById(R.id.et_gsm_bts1_mcc);
//        et_gsm_bts2_mcc = (EditText) view.findViewById(R.id.et_gsm_bts2_mcc);
        spinner_gsm_bts1_mnc = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts1_mnc);
//        spinner_gsm_bts2_mnc = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts2_mnc);
        spinner_gsm_bts1_mnc.setItems(mCdmaWorkModel1List);
//        spinner_gsm_bts2_mnc.setItems(mCdmaWorkModel1List);
        et_gsm_bts1_lac = (EditText) view.findViewById(R.id.et_gsm_bts1_lac);
//        et_gsm_bts2_lac = (EditText) view.findViewById(R.id.et_gsm_bts2_lac);
        et_gsm_bts1_captime = (EditText) view.findViewById(R.id.et_gsm_bts1_captime);
//        et_gsm_bts2_captime = (EditText) view.findViewById(R.id.et_gsm_bts2_captime);
        et_gsm_bts1_lowatt = (EditText) view.findViewById(R.id.et_gsm_bts1_lowatt);
//        et_gsm_bts2_lowatt = (EditText) view.findViewById(R.id.et_gsm_bts2_lowatt);
        et_gsm_bts1_upatt = (EditText) view.findViewById(R.id.et_gsm_bts1_upatt);
//        et_gsm_bts2_upatt = (EditText) view.findViewById(R.id.et_gsm_bts2_upatt);
        et_gsm_bts1_cro = (EditText) view.findViewById(R.id.et_gsm_bts1_cro);
//        et_gsm_bts2_cro = (EditText) view.findViewById(R.id.et_gsm_bts2_cro);
        spinner_gsm_bts1_configmode = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts1_configmode);
//        spinner_gsm_bts2_configmode = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts2_configmode);
        spinner_gsm_bts1_workmode = (TextView) view.findViewById(R.id.spinner_gsm_bts1_workmode);
//        spinner_gsm_bts2_workmode = (MaterialSpinner) view.findViewById(R.id.spinner_gsm_bts2_workmode);
        mGSMMncList1.add("移动");
        mGSMMncList1.add("联通");
        mGSMMncList1.add("电信");
        spinner_gsm_bts1_mnc.setItems(mGSMMncList1);
//        mGSMMncList2.add("移动");
//        mGSMMncList2.add("联通");
//        mGSMMncList2.add("电信");
//        spinner_gsm_bts2_mnc.setItems(mGSMMncList2);
        mGSMConfigModeList.add("自动配置");
        mGSMConfigModeList.add("手动配置");
        mCdmaWorkModelList.add("驻留模式");
        mCdmaWorkModelList.add("非驻留模式");
        spinner_gsm_bts1_configmode.setItems(mGSMConfigModeList);
//        spinner_gsm_bts2_configmode.setItems(mGSMConfigModeList);
//        spinner_gsm_bts1_workmode.setItems(mCdmaWorkModelList);
//        spinner_gsm_bts2_workmode.setItems(mCdmaWorkModelList);
//        raGroupGSM.check(TextUtils.equals(gsmConfig.Enable1, "1") ? R.id.rb_bts_enable_enable_gsm : R.id.rb_bts_enable_not_enable_gsm);
//        raGroupGSM.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rb_bts_enable_enable_gsm) {
//                    try {
//                        gsmConfig.Enable1 = "1";
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (checkedId == R.id.rb_bts_enable_not_enable_gsm) {
//                    try {
//                        gsmConfig.Enable1 = "0";
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        if (TextUtils.equals(gsmConfig.BAND1, "900")) {
            spinner_gsm_bts1_band.setSelectedIndex(0);
        } else if (TextUtils.equals(gsmConfig.BAND1, "1800")) {
            spinner_gsm_bts1_band.setSelectedIndex(1);
        }
        et_gsm_bts1_bcc.setText(gsmConfig.BCC1);
        et_gsm_bts1_mcc.setText(gsmConfig.MCC1);
        if (gsmConfig.MNC1 != null) {
            switch (gsmConfig.MNC1) {
                case "00":
                case "0":
                    spinner_gsm_bts1_mnc.setSelectedIndex(0);
                    break;
                case "01":
                case "1":
                    spinner_gsm_bts1_mnc.setSelectedIndex(1);
                    break;
                case "03":
                case "3":
                    spinner_gsm_bts1_mnc.setSelectedIndex(2);
                    break;
            }
        }

        et_gsm_bts1_lac.setText(gsmConfig.LAC1);
        et_gsm_bts1_cro.setText(gsmConfig.CRO1);
        et_gsm_bts1_captime.setText(gsmConfig.CAPTIME1);
        et_gsm_bts1_lowatt.setText(gsmConfig.LOWATT1);
        et_gsm_bts1_upatt.setText(gsmConfig.UPATT1);
        if (TextUtils.equals(gsmConfig.CONFIGMODE1, "0")) {
            spinner_gsm_bts1_configmode.setSelectedIndex(0);
        } else if (TextUtils.equals(gsmConfig.CONFIGMODE1, "1")) {
            spinner_gsm_bts1_configmode.setSelectedIndex(1);
        }
//        if (TextUtils.equals(gsmConfig.WORKMODE1, "0")) {
//            spinner_gsm_bts1_workmode.setSelectedIndex(0);
//        } else if (TextUtils.equals(gsmConfig.WORKMODE1, "1")) {
//            spinner_gsm_bts1_workmode.setSelectedIndex(1);
//        }
//            }
//            if( gsmWirelessInfoBean.BTS2!=null){
//        raGroup2GSM.check(TextUtils.equals(gsmConfig.Enable2, "1") ? R.id.rb_bts2_enable_enable_gsm : R.id.rb_bts2_enable_not_enable_gsm);
//        raGroup2GSM.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rb_bts2_enable_enable_gsm) {
//                    try {
//                        gsmConfig.Enable2 = "1";
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (checkedId == R.id.rb_bts2_enable_not_enable_gsm) {
//                    try {
//                        gsmConfig.Enable2 = "0";
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        if (TextUtils.equals(gsmConfig.BAND2, "900")) {
//            spinner_gsm_bts2_band.setSelectedIndex(0);
//        } else if (TextUtils.equals(gsmConfig.BAND2, "1800")) {
//            spinner_gsm_bts2_band.setSelectedIndex(1);
//        }
//        et_gsm_bts2_bcc.setText(gsmConfig.BCC2);
//        et_gsm_bts2_mcc.setText(gsmConfig.MCC2);
//        if (gsmConfig.MNC2 != null) {
//            switch (gsmConfig.MNC2) {
//                case "00":
//                case "0":
//                    spinner_gsm_bts2_mnc.setSelectedIndex(0);
//                    break;
//                case "01":
//                case "1":
//                    spinner_gsm_bts2_mnc.setSelectedIndex(1);
//                    break;
//                case "03":
//                case "3":
//                    spinner_gsm_bts2_mnc.setSelectedIndex(2);
//                    break;
//            }
//        }
//        et_gsm_bts2_lac.setText(gsmConfig.LAC2);
//        et_gsm_bts2_cro.setText(gsmConfig.CRO2);
//        et_gsm_bts2_captime.setText(gsmConfig.CAPTIME2);
//        et_gsm_bts2_lowatt.setText(gsmConfig.LOWATT2);
//        et_gsm_bts2_upatt.setText(gsmConfig.UPATT2);
//        if (TextUtils.equals(gsmConfig.CONFIGMODE2, "0")) {
//            spinner_gsm_bts2_configmode.setSelectedIndex(0);
//        } else if (TextUtils.equals(gsmConfig.CONFIGMODE2, "1")) {
//            spinner_gsm_bts2_configmode.setSelectedIndex(1);
//        }
//        if (TextUtils.equals(gsmConfig.WORKMODE2, "0")) {
//            spinner_gsm_bts2_workmode.setSelectedIndex(0);
//        } else if (TextUtils.equals(gsmConfig.WORKMODE2, "1")) {
//            spinner_gsm_bts2_workmode.setSelectedIndex(1);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mActivityListener = (OnBackPressedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement IConnectionFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityListener = null;
    }

    @Override
    public void onClick(View v) {
//        gsmConfig.Enable1 = (raGroupGSM.getCheckedRadioButtonId() == R.id.rb_bts_enable_enable_gsm) ? "1" : "0";

        if (spinner_gsm_bts1_band.getSelectedIndex() == 0) {
            gsmConfig.BAND1 = "900";
        } else if (spinner_gsm_bts1_band.getSelectedIndex() == 1) {
            gsmConfig.BAND1 = "1800";
        }
        gsmConfig.BCC1 = et_gsm_bts1_bcc.getText().toString();
        gsmConfig.MCC1 = et_gsm_bts1_mcc.getText().toString();
        gsmConfig.CRO1 = et_gsm_bts1_cro.getText().toString();
        gsmConfig.LAC1 = et_gsm_bts1_lac.getText().toString();
        gsmConfig.CAPTIME1 = et_gsm_bts1_captime.getText().toString();
        gsmConfig.LOWATT1 = et_gsm_bts1_lowatt.getText().toString();
        gsmConfig.UPATT1 = et_gsm_bts1_upatt.getText().toString();

        if (spinner_gsm_bts1_mnc.getSelectedIndex() == 0) {
            gsmConfig.MNC1 = "00";
        } else if (spinner_gsm_bts1_mnc.getSelectedIndex() == 1) {
            gsmConfig.MNC1 = "01";
        } else if (spinner_gsm_bts1_mnc.getSelectedIndex() == 2) {
            gsmConfig.MNC1 = "03";
        }

        if (spinner_gsm_bts1_configmode.getSelectedIndex() == 0) {
            gsmConfig.CONFIGMODE1 = "0";
        } else if (spinner_gsm_bts1_configmode.getSelectedIndex() == 1) {
            gsmConfig.CONFIGMODE1 = "1";
        }
//        if (spinner_gsm_bts1_workmode.getSelectedIndex() == 0) {
            gsmConfig.WORKMODE1 = "0";
//        } else if (spinner_gsm_bts1_workmode.getSelectedIndex() == 1) {
//            gsmConfig.WORKMODE1 = "1";
//        }
//        gsmConfig.Enable2 = (raGroup2GSM.getCheckedRadioButtonId() == R.id.rb_bts2_enable_enable_gsm) ? "1" : "0";

//        if (spinner_gsm_bts2_band.getSelectedIndex() == 0)
//
//        {
//            gsmConfig.BAND2 = "900";
//        } else if (spinner_gsm_bts2_band.getSelectedIndex() == 1)
//
//        {
//            gsmConfig.BAND2 = "1800";
//        }
//
//        gsmConfig.BCC2 = et_gsm_bts2_bcc.getText().toString();
//
//        gsmConfig.MCC2 = et_gsm_bts2_mcc.getText().toString();
//
//        gsmConfig.CRO2 = et_gsm_bts2_cro.getText().toString();
//
//        gsmConfig.LAC2 = et_gsm_bts2_lac.getText().toString();
//
//        gsmConfig.CAPTIME2 = et_gsm_bts2_captime.getText().toString();
//
//        gsmConfig.LOWATT2 = et_gsm_bts2_lowatt.getText().toString();
//
//        gsmConfig.UPATT2 = et_gsm_bts2_upatt.getText().toString();
//        if (spinner_gsm_bts2_mnc.getSelectedIndex() == 0) {
//            gsmConfig.MNC2 = "00";
//        } else if (spinner_gsm_bts2_mnc.getSelectedIndex() == 1) {
//            gsmConfig.MNC2 = "01";
//        } else if (spinner_gsm_bts2_mnc.getSelectedIndex() == 2) {
//            gsmConfig.MNC2 = "03";
//        }
//        if (spinner_gsm_bts2_configmode.getSelectedIndex() == 0) {
//            gsmConfig.CONFIGMODE2 = "0";
//        } else if (spinner_gsm_bts2_configmode.getSelectedIndex() == 1) {
//            gsmConfig.CONFIGMODE2 = "1";
//        }
//        if (spinner_gsm_bts2_workmode.getSelectedIndex() == 0) {
//            gsmConfig.WORKMODE2 = "0";
//        } else if (spinner_gsm_bts2_workmode.getSelectedIndex() == 1) {
//            gsmConfig.WORKMODE2 = "1";
//        }
        DataManager.getInstance().crateOrUpdate(gsmConfig);
        gsmConfig.setCMD();
        handler.sendEmptyMessageDelayed(1, 2000L);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
//                if(TextUtils.equals(gsmConfig.Enable1, "1")){
                TcpManager.getInstance().sendGsmUdpMsg(gsmConfig.cmd1);
//                }
//                if(TextUtils.equals(gsmConfig.Enable2, "1")){
//                TcpManager.getInstance().sendGsmUdpMsg(gsmConfig.cmd2);
//                }

                TcpManager.getInstance().addQueryMsg();
                TcpManager.getInstance().upDateGsmConfig(gsmConfig);
                ToastUtils.showToast(_mActivity, "保存成功，正在下发配置，请稍后", Toast.LENGTH_SHORT);
                if (mActivityListener != null) {
                    mActivityListener.onBack();
                }

            }
        }
    };
}

