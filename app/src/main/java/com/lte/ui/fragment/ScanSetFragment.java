package com.lte.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.ScanSet;
import com.lte.data.StationInfo;
import com.lte.ui.listener.OnBackPressedListener;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.utils.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by chenxiaojun on 2017/9/14.
 */

public class ScanSetFragment extends SupportFragment implements View.OnClickListener{
    private StationInfo stationInfo;
    private Button save;
    private Button cancel;
    private OnBackPressedListener mActivityListener;
    private EditText pci_list;
    private EditText earfcn_range_list;
    private EditText rssi_et;
    private EditText sacn_reult;

    private ScanSet scanSet;
    private SweetAlertDialog mAddDialog;
    private EditText mEt;
    private ArrayList<Integer> pciList;
    private ArrayList<Integer> earfchList;

    public static ScanSetFragment newInstance(StationInfo stationInfo,ArrayList<Integer> pciList,ArrayList<Integer> earfchList) {
        ScanSetFragment fragment = new ScanSetFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(Constants.PCI,pciList);
        bundle.putIntegerArrayList(Constants.EARFCH,earfchList);
        bundle.putParcelable(Constants.STATION,stationInfo);
        fragment.setArguments(bundle);
        return fragment;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scan_set, container, false);
        init(view);
        return view;
    }
    private void init(View view) {
        Bundle bundle = getArguments();
        stationInfo = bundle.getParcelable(Constants.STATION);
        pciList = bundle.getIntegerArrayList(Constants.PCI);
        earfchList = bundle.getIntegerArrayList(Constants.EARFCH);
        pci_list = (EditText) view.findViewById(R.id.pci_list);
        earfcn_range_list = (EditText) view.findViewById(R.id.earfch__list);
        rssi_et = (EditText) view.findViewById(R.id.rssi_et);
        sacn_reult = (EditText) view.findViewById(R.id.scan_result);
        save = (Button)view.findViewById(R.id.save);
        cancel = (Button)view.findViewById(R.id.cancel);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        if(stationInfo == null){
            for (StationInfo info : App.get().getMList()) {
                if(TextUtils.equals(info.getIp(),App.get().Ip)){
                    stationInfo = info;
                }
            }
        }else {
            for (StationInfo info : App.get().getMList()) {
                if(TextUtils.equals(info.getIp(),stationInfo.getIp())){
                    stationInfo = info;
                }
            }
        }
        if(stationInfo != null){
            scanSet = stationInfo.getScanSet();
            if(scanSet != null){
                Log.d("scanSet","scanSet  :" + scanSet.toString());
                rssi_et.setText(scanSet.getRssi()+"");
                sacn_reult.setText(scanSet.getScan_result()+"");
                for (Integer integer : scanSet.getEarfchList()) {
                    earfcn_range_list.append(integer+",");
                }
                for (Integer integer : scanSet.getPciList()) {
                    pci_list.append(integer+",");
                }
            }else {
                scanSet = new ScanSet();
                scanSet.setId(stationInfo.getId());
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save:
                if(!TextUtils.isEmpty(pci_list.getText().toString())){
                    if(pci_list.getText().toString().length()!= 0  ){
                        ArrayList<Integer> list = new ArrayList<>();
                        if(pci_list.getText().toString().contains(",")){
                            String[] strings = pci_list.getText().toString().split(",");
                            for (String string : strings) {
                                if(string.length()>0){
                                    try {
                                        list.add(Integer.valueOf(string));
                                    }catch (Exception e){
                                        Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }else {
                            try {
                                list.add(Integer.valueOf(pci_list.getText().toString()));
                            }catch (Exception e){
                                Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                            }
                        }
                        scanSet.setPciList(list);
                    }
                }
                if(!TextUtils.isEmpty(earfcn_range_list.getText().toString())){
                    if(earfcn_range_list.getText().toString().length()!= 0  ){
                        ArrayList<Integer> list = new ArrayList<>();
                        if(earfcn_range_list.getText().toString().contains(",")){
                            String[] strings = earfcn_range_list.getText().toString().split(",");
                            for (String string : strings) {
                                if(string.length()>0){
                                    try {
                                        list.add(Integer.valueOf(string));
                                    }catch (Exception e){
                                        Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }else {
                            try {
                                list.add(Integer.valueOf(earfcn_range_list.getText().toString()));
                            }catch (Exception e){
                                Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                            }
                        }
                        scanSet.setEarfchList(list);
                    }
                }
                if(!TextUtils.isEmpty(sacn_reult.getText().toString())){
                    try {
                        scanSet.setScan_result(Byte.parseByte(sacn_reult.getText().toString()));
                    }catch (Exception e){
                        Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                    }
                }
                if(!TextUtils.isEmpty(rssi_et.getText().toString())){
                    try {
                        scanSet.setRssi(Byte.parseByte(rssi_et.getText().toString()));
                    }catch (Exception e){
                        Toast.makeText(getActivity(),getString(R.string.add_number),Toast.LENGTH_LONG).show();
                    }
                }
                stationInfo.setScanSet(scanSet);
                for (StationInfo info : App.get().getMList()) {
                    if(TextUtils.equals(info.getIp(),stationInfo.getIp())){
                        info.setScanSet(scanSet);
                    }
                }
                DataManager.getInstance().createOrUpdateStation(stationInfo);
                mActivityListener.onBack();
                break;
            case R.id.cancel:
                mActivityListener.onBack();
                break;
//            case R.id.pci_add:
//                showModifyNameDialog(getString(R.string.add_pci_title),true);
//                break;
//            case R.id.earfch_range_add:
//                showModifyNameDialog(getString(R.string.add_pci_title),false);
//                break;
        }
    }

    public static ScanSetFragment newInstance() {
        return new ScanSetFragment();
    }
//    private void showModifyNameDialog(String title, final boolean isAddPci) {
//
//        mAddDialog = new SweetAlertDialog.Builder(getActivity())
//                .setMessage(title)
//                .setHasTwoBtn(true)
//                .setNegativeButton(R.string.cancel)
//                .setPositiveButton(R.string.add, new SweetAlertDialog.OnDialogClickListener() {
//                    @Override
//                    public void onClick(Dialog dialog, int which) {
//
//                        String temp = mEt.getText().toString().trim();
//                        checkModifiedNameAndSave(isAddPci,temp);
//                    }
//                }).create();
//
//        mAddDialog.addContentView(R.layout.input_pci);
//        mEt = (EditText) mAddDialog.findView(R.id.et_pci);
//        mAddDialog.show();
//
//    }


//    private boolean checkModifiedNameAndSave(boolean isAddPci,String tempNumber) {
//        if (TextUtils.isEmpty(tempNumber)) {
//            Toast.makeText(getActivity(), getString(R.string.not_be_null), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(isAddPci){
//            scanSet.getPciList().add(Integer.valueOf(tempNumber));
//            pci_list.append(tempNumber);
//
//        }else {
//            scanSet.getEarfchList().add(Integer.valueOf(tempNumber));
//            earfcn_range_list.append(tempNumber);
//        }
//        return true;
//    }
}
