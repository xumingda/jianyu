package com.lte.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.lte.R;
import com.lte.data.CdmaConfig;
import com.lte.data.DataManager;
import com.lte.data.GsmConfig;
import com.lte.tcpserver.TcpManager;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.utils.Constants;
import com.lte.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/12/21.
 */

public class CdmaFragment extends SupportFragment implements View.OnClickListener {


    private Long id;

    private EditText et_cdma_reDetectMinuts, et_cdma_sid, et_cdma_nid, et_cdma_pn, et_cdma_bsid, et_cdma_regnum, et_cdma_captime, et_cdma_lowait, et_cdma_upatt, et_cdma_scantime, et_cdma_scanperiod,
            et_cdma_freq1, et_cdma_freq2, et_cdma_freq3, et_cdma_freq4, et_cdma_scantime1, et_cdma_scantime2, et_cdma_scantime3, et_cdma_scantime4,
            et_cdma_scancaptime1, et_cdma_scancaptime2, et_cdma_scancaptime3, et_cdma_scancaptime4,
            et_cdma_NEIBOR1FREQ1, et_cdma_NEIBOR2FREQ1, et_cdma_NEIBOR3FREQ1, et_cdma_NEIBOR4FREQ1,
            et_cdma_NEIBOR1FREQ2, et_cdma_NEIBOR2FREQ2, et_cdma_NEIBOR3FREQ2, et_cdma_NEIBOR4FREQ2,
            et_cdma_NEIBOR1FREQ3, et_cdma_NEIBOR2FREQ3, et_cdma_NEIBOR3FREQ3, et_cdma_NEIBOR4FREQ3,
            et_cdma_NEIBOR1FREQ4, et_cdma_NEIBOR2FREQ4, et_cdma_NEIBOR3FREQ4, et_cdma_NEIBOR4FREQ4;
    private MaterialSpinner spinner_cdma_mnc, spinner_cdma_workmodel, spinner_cdma_resetmodel, spinner_cdma_workmodel1, spinner_cdma_workmodel2, spinner_cdma_workmodel3, spinner_cdma_workmodel4;

    private Button btn_new_account;

    protected List<String> mCdmaMncList = new ArrayList<>();

    protected List<String> mCdmaWorkModelList = new ArrayList<>();
    protected List<String> mCdmaResetModelList = new ArrayList<>();
    protected List<String> mCdmaWorkModel1List = new ArrayList<>();
//    private RadioGroup raGroupCDMA;

    private TextView tv_cdma_mcc;
    private CdmaConfig cmdaConfig;

    public static CdmaFragment newInstance(Long ip) {
        CdmaFragment fragment = new CdmaFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.ID, ip);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cdma_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        Bundle bundle = getArguments();
        id = bundle.getLong(Constants.ID);

        cmdaConfig = DataManager.getInstance().findCdmaConfigFrist();
        btn_new_account = (Button) view.findViewById(R.id.btn_new_account);
        btn_new_account.setOnClickListener(this);
//        raGroupCDMA = (RadioGroup) view.findViewById(R.id.rg_bts_enable_cdma);
        tv_cdma_mcc = (TextView) view.findViewById(R.id.tv_cdma_mcc);
        spinner_cdma_mnc = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_mnc);
        mCdmaMncList.add("移动");
        mCdmaMncList.add("联通");
        mCdmaMncList.add("电信");
        spinner_cdma_mnc.setItems(mCdmaMncList);
        et_cdma_reDetectMinuts = (EditText) view.findViewById(R.id.et_cdma_reDetectMinuts);
        et_cdma_sid = (EditText) view.findViewById(R.id.et_cdma_sid);
        et_cdma_nid = (EditText) view.findViewById(R.id.et_cdma_nid);
        et_cdma_pn = (EditText) view.findViewById(R.id.et_cdma_pn);
        et_cdma_bsid = (EditText) view.findViewById(R.id.et_cdma_bsid);
        et_cdma_regnum = (EditText) view.findViewById(R.id.et_cdma_regnum);
        et_cdma_captime = (EditText) view.findViewById(R.id.et_cdma_captime);
        et_cdma_lowait = (EditText) view.findViewById(R.id.et_cdma_lowait);
        et_cdma_upatt = (EditText) view.findViewById(R.id.et_cdma_upatt);
        spinner_cdma_workmodel = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_workmodel);
        mCdmaWorkModelList.add("驻留模式");
        mCdmaWorkModelList.add("非驻留模式");
        spinner_cdma_workmodel.setItems(mCdmaWorkModelList);

        spinner_cdma_resetmodel = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_resetmodel);
        mCdmaResetModelList.add("复位指定载波的协议栈");
        mCdmaResetModelList.add("复位协议栈和DSP PHY");
        mCdmaResetModelList.add("复位协议栈、DSP PHY和射频");
        spinner_cdma_resetmodel.setItems(mCdmaResetModelList);
        et_cdma_scantime = (EditText) view.findViewById(R.id.et_cdma_scantime);
        et_cdma_scanperiod = (EditText) view.findViewById(R.id.et_cdma_scanperiod);

        et_cdma_freq1 = (EditText) view.findViewById(R.id.et_cdma_freq1);
        et_cdma_freq2 = (EditText) view.findViewById(R.id.et_cdma_freq2);
        et_cdma_freq3 = (EditText) view.findViewById(R.id.et_cdma_freq3);
        et_cdma_freq4 = (EditText) view.findViewById(R.id.et_cdma_freq4);
        spinner_cdma_workmodel1 = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_workmodel1);
        spinner_cdma_workmodel2 = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_workmodel2);
        spinner_cdma_workmodel3 = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_workmodel3);
        spinner_cdma_workmodel4 = (MaterialSpinner) view.findViewById(R.id.spinner_cdma_workmodel4);
        mCdmaWorkModel1List.add("扫描");
        mCdmaWorkModel1List.add("常开");
        mCdmaWorkModel1List.add("关闭");
        spinner_cdma_workmodel1.setItems(mCdmaWorkModel1List);
        spinner_cdma_workmodel2.setItems(mCdmaWorkModel1List);
        spinner_cdma_workmodel3.setItems(mCdmaWorkModel1List);
        spinner_cdma_workmodel4.setItems(mCdmaWorkModel1List);

        et_cdma_scantime1 = (EditText) view.findViewById(R.id.et_cdma_scantime1);
        et_cdma_scantime2 = (EditText) view.findViewById(R.id.et_cdma_scantime2);
        et_cdma_scantime3 = (EditText) view.findViewById(R.id.et_cdma_scantime3);
        et_cdma_scantime4 = (EditText) view.findViewById(R.id.et_cdma_scantime4);

        et_cdma_scancaptime1 = (EditText) view.findViewById(R.id.et_cdma_scancaptime1);
        et_cdma_scancaptime2 = (EditText) view.findViewById(R.id.et_cdma_scancaptime2);
        et_cdma_scancaptime3 = (EditText) view.findViewById(R.id.et_cdma_scancaptime3);
        et_cdma_scancaptime4 = (EditText) view.findViewById(R.id.et_cdma_scancaptime4);

        et_cdma_NEIBOR1FREQ1 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR1FREQ1);
        et_cdma_NEIBOR2FREQ1 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR2FREQ1);
        et_cdma_NEIBOR3FREQ1 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR3FREQ1);
        et_cdma_NEIBOR4FREQ1 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR4FREQ1);

        et_cdma_NEIBOR1FREQ2 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR1FREQ2);
        et_cdma_NEIBOR2FREQ2 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR2FREQ2);
        et_cdma_NEIBOR3FREQ2 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR3FREQ2);
        et_cdma_NEIBOR4FREQ2 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR4FREQ2);

        et_cdma_NEIBOR1FREQ3 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR1FREQ3);
        et_cdma_NEIBOR2FREQ3 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR2FREQ3);
        et_cdma_NEIBOR3FREQ3 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR3FREQ3);
        et_cdma_NEIBOR4FREQ3 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR4FREQ3);

        et_cdma_NEIBOR1FREQ4 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR1FREQ4);
        et_cdma_NEIBOR2FREQ4 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR2FREQ4);
        et_cdma_NEIBOR3FREQ4 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR3FREQ4);
        et_cdma_NEIBOR4FREQ4 = (EditText) view.findViewById(R.id.et_cdma_NEIBOR4FREQ4);

//        raGroupCDMA.check(TextUtils.equals(cmdaConfig.Enable, "1") ? R.id.rb_bts_enable_enable_cdma : R.id.rb_bts_enable_not_enable_cdma);
//        raGroupCDMA.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.rb_bts_enable_enable_cdma) {
//                    try {
//                        cmdaConfig.Enable = "1";
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (checkedId == R.id.rb_bts_enable_not_enable_cdma) {
//                    try {
//
//                        cmdaConfig.Enable = "0";
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        tv_cdma_mcc.setText("MCC:  " + cmdaConfig.MCC);
        et_cdma_reDetectMinuts.setText(cmdaConfig.reDetectMinuts);
        et_cdma_sid.setText(cmdaConfig.SID);
        et_cdma_nid.setText(cmdaConfig.NID);
        et_cdma_pn.setText(cmdaConfig.PN);
        et_cdma_bsid.setText(cmdaConfig.BSID);
        et_cdma_regnum.setText(cmdaConfig.REGNUM);
        et_cdma_captime.setText(cmdaConfig.CAPTIME);
        et_cdma_lowait.setText(cmdaConfig.LOWATT);
        et_cdma_upatt.setText(cmdaConfig.UPATT);
        et_cdma_scantime.setText(cmdaConfig.SCANTIME);
        et_cdma_scanperiod.setText(cmdaConfig.SCANPERIOD);
        et_cdma_freq1.setText(cmdaConfig.FREQ1);
        et_cdma_freq2.setText(cmdaConfig.FREQ2);
        et_cdma_freq3.setText(cmdaConfig.FREQ3);
        et_cdma_freq4.setText(cmdaConfig.FREQ4);
        et_cdma_scantime1.setText(cmdaConfig.SCANTIME1);
        et_cdma_scantime2.setText(cmdaConfig.SCANTIME2);
        et_cdma_scantime3.setText(cmdaConfig.SCANTIME3);
        et_cdma_scantime4.setText(cmdaConfig.SCANTIME4);
        et_cdma_scancaptime1.setText(cmdaConfig.SCANCAPTIME1);
        et_cdma_scancaptime2.setText(cmdaConfig.SCANCAPTIME2);
        et_cdma_scancaptime3.setText(cmdaConfig.SCANCAPTIME3);
        et_cdma_scancaptime4.setText(cmdaConfig.SCANCAPTIME4);
        et_cdma_NEIBOR1FREQ1.setText(cmdaConfig.NEIBOR1FREQ1);
        et_cdma_NEIBOR2FREQ1.setText(cmdaConfig.NEIBOR2FREQ1);
        et_cdma_NEIBOR3FREQ1.setText(cmdaConfig.NEIBOR3FREQ1);
        et_cdma_NEIBOR4FREQ1.setText(cmdaConfig.NEIBOR4FREQ1);
        et_cdma_NEIBOR1FREQ2.setText(cmdaConfig.NEIBOR1FREQ2);
        et_cdma_NEIBOR2FREQ2.setText(cmdaConfig.NEIBOR2FREQ2);
        et_cdma_NEIBOR3FREQ2.setText(cmdaConfig.NEIBOR3FREQ2);
        et_cdma_NEIBOR4FREQ2.setText(cmdaConfig.NEIBOR4FREQ2);
        et_cdma_NEIBOR1FREQ3.setText(cmdaConfig.NEIBOR1FREQ3);
        et_cdma_NEIBOR2FREQ3.setText(cmdaConfig.NEIBOR2FREQ3);
        et_cdma_NEIBOR3FREQ3.setText(cmdaConfig.NEIBOR3FREQ3);
        et_cdma_NEIBOR4FREQ3.setText(cmdaConfig.NEIBOR4FREQ3);
        et_cdma_NEIBOR1FREQ4.setText(cmdaConfig.NEIBOR1FREQ4);
        et_cdma_NEIBOR2FREQ4.setText(cmdaConfig.NEIBOR2FREQ4);
        et_cdma_NEIBOR3FREQ4.setText(cmdaConfig.NEIBOR3FREQ4);
        et_cdma_NEIBOR4FREQ4.setText(cmdaConfig.NEIBOR4FREQ4);

        if (TextUtils.equals(cmdaConfig.MNC, "00")) {
            spinner_cdma_mnc.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.MNC, "01")) {
            spinner_cdma_mnc.setSelectedIndex(1);
        } else if (TextUtils.equals(cmdaConfig.MNC, "02")) {
            spinner_cdma_mnc.setSelectedIndex(2);
        }
        if (TextUtils.equals(cmdaConfig.WORKMODEL, "0")) {
            spinner_cdma_workmodel.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.WORKMODEL, "1")) {
            spinner_cdma_workmodel.setSelectedIndex(1);
        }
        if (TextUtils.equals(cmdaConfig.RESETMODEL, "0")) {
            spinner_cdma_resetmodel.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.RESETMODEL, "1")) {
            spinner_cdma_resetmodel.setSelectedIndex(1);
        } else if (TextUtils.equals(cmdaConfig.RESETMODEL, "2")) {
            spinner_cdma_resetmodel.setSelectedIndex(2);
        }
        if (TextUtils.equals(cmdaConfig.WORKMODE1, "0")) {
            spinner_cdma_workmodel1.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE1, "1")) {
            spinner_cdma_workmodel1.setSelectedIndex(1);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE1, "2")) {
            spinner_cdma_workmodel1.setSelectedIndex(2);
        }
        if (TextUtils.equals(cmdaConfig.WORKMODE2, "0")) {
            spinner_cdma_workmodel2.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE2, "1")) {
            spinner_cdma_workmodel2.setSelectedIndex(1);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE2, "2")) {
            spinner_cdma_workmodel2.setSelectedIndex(2);
        }
        if (TextUtils.equals(cmdaConfig.WORKMODE3, "0")) {
            spinner_cdma_workmodel3.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE3, "1")) {
            spinner_cdma_workmodel3.setSelectedIndex(1);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE3, "2")) {
            spinner_cdma_workmodel3.setSelectedIndex(2);
        }
        if (TextUtils.equals(cmdaConfig.WORKMODE4, "0")) {
            spinner_cdma_workmodel4.setSelectedIndex(0);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE4, "1")) {
            spinner_cdma_workmodel4.setSelectedIndex(1);
        } else if (TextUtils.equals(cmdaConfig.WORKMODE4, "2")) {
            spinner_cdma_workmodel4.setSelectedIndex(2);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_new_account) {
            cmdaConfig.reDetectMinuts = et_cdma_reDetectMinuts.getText().toString();

//            cmdaConfig.Enable = (raGroupCDMA.getCheckedRadioButtonId() == R.id.rb_bts_enable_enable_cdma) ? "1" : "0";
            cmdaConfig.SID = et_cdma_sid.getText().toString();
            cmdaConfig.NID = et_cdma_nid.getText().toString();
            cmdaConfig.PN = et_cdma_pn.getText().toString();
            cmdaConfig.BSID = et_cdma_bsid.getText().toString();
            cmdaConfig.REGNUM = et_cdma_regnum.getText().toString();
            cmdaConfig.CAPTIME = et_cdma_captime.getText().toString();
            cmdaConfig.LOWATT = et_cdma_lowait.getText().toString();
            cmdaConfig.UPATT = et_cdma_upatt.getText().toString();
            cmdaConfig.SCANTIME = et_cdma_scantime.getText().toString();
            cmdaConfig.SCANPERIOD = et_cdma_scanperiod.getText().toString();
            cmdaConfig.FREQ1 = et_cdma_freq1.getText().toString();
            cmdaConfig.FREQ2 = et_cdma_freq2.getText().toString();
            cmdaConfig.FREQ3 = et_cdma_freq3.getText().toString();
            cmdaConfig.FREQ4 = et_cdma_freq4.getText().toString();
            cmdaConfig.SCANTIME1 = et_cdma_scantime1.getText().toString();
            cmdaConfig.SCANTIME2 = et_cdma_scantime2.getText().toString();
            cmdaConfig.SCANTIME3 = et_cdma_scantime3.getText().toString();
            cmdaConfig.SCANTIME4 = et_cdma_scantime4.getText().toString();
            cmdaConfig.SCANCAPTIME1 = et_cdma_scancaptime1.getText().toString();
            cmdaConfig.SCANCAPTIME2 = et_cdma_scancaptime2.getText().toString();
            cmdaConfig.SCANCAPTIME3 = et_cdma_scancaptime3.getText().toString();
            cmdaConfig.SCANCAPTIME4 = et_cdma_scancaptime4.getText().toString();
            cmdaConfig.NEIBOR1FREQ1 = et_cdma_NEIBOR1FREQ1.getText().toString();
            cmdaConfig.NEIBOR2FREQ1 = et_cdma_NEIBOR2FREQ1.getText().toString();
            cmdaConfig.NEIBOR3FREQ1 = et_cdma_NEIBOR3FREQ1.getText().toString();
            cmdaConfig.NEIBOR4FREQ1 = et_cdma_NEIBOR4FREQ1.getText().toString();
            cmdaConfig.NEIBOR1FREQ2 = et_cdma_NEIBOR1FREQ2.getText().toString();
            cmdaConfig.NEIBOR2FREQ2 = et_cdma_NEIBOR2FREQ2.getText().toString();
            cmdaConfig.NEIBOR3FREQ2 = et_cdma_NEIBOR3FREQ2.getText().toString();
            cmdaConfig.NEIBOR4FREQ2 = et_cdma_NEIBOR4FREQ2.getText().toString();
            cmdaConfig.NEIBOR1FREQ3 = et_cdma_NEIBOR1FREQ3.getText().toString();
            cmdaConfig.NEIBOR2FREQ3 = et_cdma_NEIBOR2FREQ3.getText().toString();
            cmdaConfig.NEIBOR3FREQ3 = et_cdma_NEIBOR3FREQ3.getText().toString();
            cmdaConfig.NEIBOR4FREQ3 = et_cdma_NEIBOR4FREQ3.getText().toString();
            cmdaConfig.NEIBOR1FREQ4 = et_cdma_NEIBOR1FREQ4.getText().toString();
            cmdaConfig.NEIBOR2FREQ4 = et_cdma_NEIBOR2FREQ4.getText().toString();
            cmdaConfig.NEIBOR3FREQ4 = et_cdma_NEIBOR3FREQ4.getText().toString();
            cmdaConfig.NEIBOR4FREQ4 = et_cdma_NEIBOR4FREQ4.getText().toString();

            if (spinner_cdma_mnc.getSelectedIndex() == 0) {
                cmdaConfig.MNC = "00";
            } else if (spinner_cdma_mnc.getSelectedIndex() == 1) {
                cmdaConfig.MNC = "01";
            } else if (spinner_cdma_mnc.getSelectedIndex() == 2) {
                cmdaConfig.MNC = "02";
            }

            if (spinner_cdma_workmodel.getSelectedIndex() == 0) {
                cmdaConfig.WORKMODEL = "0";
            } else if (spinner_cdma_workmodel.getSelectedIndex() == 1) {
                cmdaConfig.WORKMODEL = "1";
            }
            if (spinner_cdma_resetmodel.getSelectedIndex() == 0) {
                cmdaConfig.RESETMODEL = "0";
            } else if (spinner_cdma_resetmodel.getSelectedIndex() == 1) {
                cmdaConfig.RESETMODEL = "1";
            } else if (spinner_cdma_resetmodel.getSelectedIndex() == 2) {
                cmdaConfig.RESETMODEL = "2";
            }
            if (spinner_cdma_workmodel1.getSelectedIndex() == 0) {
                cmdaConfig.WORKMODE1 = "0";
            } else if (spinner_cdma_workmodel1.getSelectedIndex() == 1) {
                cmdaConfig.WORKMODE1 = "1";
            } else if (spinner_cdma_workmodel1.getSelectedIndex() == 2) {
                cmdaConfig.WORKMODE1 = "2";
            }
            if (spinner_cdma_workmodel2.getSelectedIndex() == 0) {
                cmdaConfig.WORKMODE2 = "0";
            } else if (spinner_cdma_workmodel2.getSelectedIndex() == 1) {
                cmdaConfig.WORKMODE2 = "1";
            } else if (spinner_cdma_workmodel2.getSelectedIndex() == 2) {
                cmdaConfig.WORKMODE2 = "2";
            }
            if (spinner_cdma_workmodel3.getSelectedIndex() == 0) {
                cmdaConfig.WORKMODE3 = "0";
            } else if (spinner_cdma_workmodel3.getSelectedIndex() == 1) {
                cmdaConfig.WORKMODE3 = "1";
            } else if (spinner_cdma_workmodel3.getSelectedIndex() == 2) {
                cmdaConfig.WORKMODE3 = "2";
            }

            if (spinner_cdma_workmodel4.getSelectedIndex() == 0) {
                cmdaConfig.WORKMODE4 = "0";
            } else if (spinner_cdma_workmodel4.getSelectedIndex() == 1) {
                cmdaConfig.WORKMODE4 = "1";
            } else if (spinner_cdma_workmodel4.getSelectedIndex() == 2) {
                cmdaConfig.WORKMODE4 = "2";
            }
            DataManager.getInstance().crateOrUpdate(cmdaConfig);
            cmdaConfig.setCMD();
            handler.sendEmptyMessageDelayed(1, 2000L);
        }
    }

    private OnBackPressedListener mActivityListener;

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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
//                if(TextUtils.equals(cmdaConfig.Enable, "1")){
                TcpManager.getInstance().sendCmdaUdpMsg(cmdaConfig.cmd1);
//                }
                TcpManager.getInstance().addCmdaQueryMsg();
                TcpManager.getInstance().upDateCdmaConfig(cmdaConfig);
                ToastUtils.showToast(_mActivity, "保存成功，正在下发配置，请稍后", Toast.LENGTH_SHORT);
                if (mActivityListener != null) {
                    mActivityListener.onBack();
                }

            }
        }
    };
}

