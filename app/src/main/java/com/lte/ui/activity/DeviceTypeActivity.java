package com.lte.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.DeviceTypeTable;
import com.lte.ui.adapter.BbuTypeAdapter;
import com.lte.ui.adapter.DeviceTypeAdapter;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.OrderedRealmCollection;

/**
 * Created by chenxiaojun on 2018/3/8.
 */

public class DeviceTypeActivity extends BaseActivity implements DeviceTypeAdapter.CheckListener, BbuTypeAdapter.CheckListener, View.OnClickListener {

    private RecyclerView deviceTypeList;
    private RecyclerView bbu;
    private DeviceTypeAdapter mDeviceAdapter;
    private byte[] data = new byte[0];
    private BbuTypeAdapter mBbuAdapter;

    private Button add_deviceType,add_bbu;
    private SweetAlertDialog mDialog;
    private TitleBar titleBar;
    private DeviceTypeTable selectDeviceType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.device_type_activity);
        init();
    }

    private void init() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(R.string.deviceType_manager);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deviceTypeList = (RecyclerView) findViewById(R.id.device_type);
        bbu = (RecyclerView) findViewById(R.id.bbu);
        add_deviceType = (Button) findViewById(R.id.add_deviceType);
        add_bbu = (Button) findViewById(R.id.add_bbu);
        add_deviceType.setOnClickListener(this);
        add_bbu.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        OrderedRealmCollection<DeviceTypeTable> deviceType = DataManager.getInstance().findDeviceType();
        mDeviceAdapter = new DeviceTypeAdapter(this, deviceType,this,deviceTypeList);
        deviceTypeList.setLayoutManager(linearLayoutManager);
        deviceTypeList.setAdapter(mDeviceAdapter);
        if(deviceType.size() >0){
            data = deviceType.get(0).getBbuList();
        }
        mBbuAdapter = new BbuTypeAdapter(this, data,this);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        bbu.setLayoutManager(linearLayoutManager1);
        bbu.setAdapter(mBbuAdapter);
    }

    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent){
        if(outEvent.isOut()){
            this.finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view, DeviceTypeTable deviceTypeTable) {
        mBbuAdapter.setNewDate(deviceTypeTable.getBbuList());
        selectDeviceType = deviceTypeTable;

    }

    @Override
    public void onLongClick(View view, DeviceTypeTable deviceTypeTable) {
        showDeleteDialog(deviceTypeTable);
    }

    private void showDeleteDialog(final DeviceTypeTable deviceTypeTable) {
        mDialog = new SweetAlertDialog.Builder(DeviceTypeActivity.this)
                .setTitle(R.string.delete_deviceType)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        DataManager.getInstance().deleteDeviceType(deviceTypeTable);
                        mDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.show();
    }

    @Override
    public void onClick(View view, byte data) {

    }

    @Override
    public void onLongClick(View view, byte data,byte[] bytes) {
        showDeleteDialog(data,bytes);
    }

    private void showDeleteDialog(final byte data, final byte[] bytes) {
        mDialog = new SweetAlertDialog.Builder(DeviceTypeActivity.this)
                .setTitle(R.string.delete_deviceBBU)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        int i= 0;
                        byte[] bytes1 = new byte[bytes.length -1];
                        for (byte aByte : bytes) {
                            if(data != aByte){
                                bytes1[i] = aByte;
                                i++;
                            }
                        }
                        DataManager.getInstance().upGradeDeviceType(selectDeviceType,bytes1);
                        mBbuAdapter.setNewDate(bytes1);
                        mDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_deviceType:
                showDialog("添加设备型号",1);
                break;
            case R.id.add_bbu:
                showDialog("添加BBU",2);
                break;
        }
    }
    private void showDialog(String str, final int type) {
        LinearLayout container = new LinearLayout(DeviceTypeActivity.this);
        container.setOrientation(LinearLayout.VERTICAL);

        final EditText txtInput = new EditText(DeviceTypeActivity.this);
        container.addView(txtInput);
        ((LinearLayout.LayoutParams)txtInput.getLayoutParams()).topMargin = ViewUtil.dip2px(DeviceTypeActivity.this,10);
        ((LinearLayout.LayoutParams)txtInput.getLayoutParams()).bottomMargin = ViewUtil.dip2px(DeviceTypeActivity.this,10);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).weight= ViewGroup.LayoutParams.MATCH_PARENT;
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).height=ViewUtil.dip2px(DeviceTypeActivity.this,55);
        txtInput.setHint(type == 1? "请输入设备型号":"");
        if(type == 2){
            txtInput.setText("BBU");
            txtInput.setSelection(3);
        }
        txtInput.setSingleLine();
        txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        DialogManager.showDialog(DeviceTypeActivity.this, str, container, "确定", new DialogManager.IClickListener() {
            public boolean click(Dialog dlg, View view) {
                String newTitle = txtInput.getText().toString();
                if (newTitle.trim().length() == 0){
                    AppUtils.showToast(DeviceTypeActivity.this,type == 1? "请输入设备型号":"请输入BBU");
                    return false;
                }
                AppUtils.hideInputKeyboard(DeviceTypeActivity.this, txtInput);

                saveDeviceTypeOrBBU(newTitle,type);

                return true;
            }
        }, "取消", new DialogManager.IClickListener() {
            public boolean click(Dialog dlg, View view) {
                AppUtils.hideInputKeyboard(DeviceTypeActivity.this, txtInput);
                return true;
            }
        }, null);
    }

    private void saveDeviceTypeOrBBU(String newTitle,int type) {
        switch (type){
            case 1:
                DeviceTypeTable deviceTypeTable = new DeviceTypeTable();
                deviceTypeTable.setId(++App.get().deviceTypeId);
                deviceTypeTable.setName(newTitle);
                DataManager.getInstance().addDeviceType(deviceTypeTable);
                mDeviceAdapter.upDataSelectedPosition();
                break;
            case 2:
                Log.d("BBU","BBU "+newTitle);
                if(newTitle.toUpperCase().contains("BBU")){
                    String bbu = newTitle.toUpperCase().replace("BBU","");
                    Log.d("BBU","BBU "+bbu);
                    try {
                        byte b = Byte.parseByte(bbu);
                        byte[] bbuList = selectDeviceType.getBbuList();
                        if(bbuList == null){
                            bbuList = new byte[]{b};
                            DataManager.getInstance().upGradeDeviceType(selectDeviceType,bbuList);
                            mBbuAdapter.setNewDate(bbuList);
                        }else {
                            byte[] bytes = new byte[bbuList.length+1];
                            System.arraycopy(bbuList, 0, bytes, 0, bbuList.length);
                            bytes[bytes.length-1] = b;
                            DataManager.getInstance().upGradeDeviceType(selectDeviceType,bytes);
                            mBbuAdapter.setNewDate(bytes);
                        }
                    }catch (Exception e){
                        Log.d("BBU","e "+e);
                        AppUtils.showToast(DeviceTypeActivity.this,"BBU命名不合法");
                    }
                }else {
                    AppUtils.showToast(DeviceTypeActivity.this,"BBU命名不合法");
                }
                break;
        }
    }
}
