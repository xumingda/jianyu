package com.lte.ui.activity;

/*
 *  Date:2018/4/27.
 *  Time:上午10:52.
 *  author:chenxiaojun
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.BandTable;
import com.lte.data.table.RealmInteger;
import com.lte.ui.adapter.BandAdapter;
import com.lte.ui.adapter.PointAdapter;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.AppUtils;
import com.lte.utils.SharedPreferencesUtil;
import com.lte.utils.ToastUtils;
import com.lte.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.OrderedRealmCollection;
import io.realm.RealmList;

public class PointSetActivity extends BaseActivity implements BandAdapter.CheckListener, PointAdapter.CheckListener, View.OnClickListener {

    private RecyclerView bandList;
    private RecyclerView pointList;
    private BandAdapter mAdapter;
    private RealmList<RealmInteger> data = new RealmList<>();
    private PointAdapter mPointAdapter;

    private Button add_point;
    private SweetAlertDialog mDialog;
    private TitleBar titleBar;
    private BandTable selectBandTable;

    private EditText time_et;

    private Button time_bt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.point_manager_activity);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(R.string.point_manger);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bandList = (RecyclerView) findViewById(R.id.band_list);
        pointList = (RecyclerView) findViewById(R.id.point_list);
        add_point = (Button) findViewById(R.id.add_point);
        time_et = (EditText) findViewById(R.id.time_et);
        time_bt = (Button) findViewById(R.id.time_bt);
        time_bt.setOnClickListener(this);
        add_point.setOnClickListener(this);
        int intConfig = SharedPreferencesUtil.getIntConfig(this, "sniffer", "time", 15);
        time_et.setText(intConfig+"");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        OrderedRealmCollection<BandTable> bandTables = DataManager.getInstance().findBandTable();
        mAdapter = new BandAdapter(this, bandTables, this, bandList);
        bandList.setLayoutManager(linearLayoutManager);
        bandList.setAdapter(mAdapter);
        if (bandTables.size() > 0) {
            data = bandTables.get(0).getPoint();
        }
        mPointAdapter = new PointAdapter(this, data, this);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        pointList.setLayoutManager(linearLayoutManager1);
        pointList.setAdapter(mPointAdapter);
    }

    @Subscribe
    public void onSystemOut(SystemOutEvent outEvent) {
        if (outEvent.isOut()) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view, BandTable bandTable) {
        mPointAdapter.setNewDate(bandTable.getPoint());
        selectBandTable = bandTable;
    }

    @Override
    public void onLongClick(View view, BandTable bandTable) {
        showDeleteDialog(bandTable);
    }

    private void showDeleteDialog(final BandTable bandTable) {
        mDialog = new SweetAlertDialog.Builder(PointSetActivity.this)
                .setTitle(R.string.delete_deviceType)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        DataManager.getInstance().deleteBand(bandTable);
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
    public void onClick(View view, RealmInteger data) {

    }

    @Override
    public void onLongClick(View view, RealmInteger data, RealmList<RealmInteger> bytes) {
        showDeleteDialog(data, bytes);
    }

    private void showDeleteDialog(final RealmInteger data, final RealmList<RealmInteger> bytes) {
        mDialog = new SweetAlertDialog.Builder(PointSetActivity.this)
                .setTitle(R.string.delete_point)
                .setHasTwoBtn(true)
                .setPositiveButton("确定", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        DataManager.getInstance().deletePoint(selectBandTable, data);
                        mPointAdapter.setNewDate(selectBandTable.getPoint());
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
        switch (v.getId()) {
            case R.id.add_point:
                showDialog("添加Point", 2);
                break;
            case R.id.time_bt:
                if(!TextUtils.isEmpty(time_et.getText().toString())){
                    int i = Integer.parseInt(time_et.getText().toString());
                    SharedPreferencesUtil.setConfig(this, "sniffer", "time", i);
                    ToastUtils.showToast(this,"保存成功", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private void showDialog(String str, final int type) {
        LinearLayout container = new LinearLayout(PointSetActivity.this);
        container.setOrientation(LinearLayout.VERTICAL);

        final EditText txtInput = new EditText(PointSetActivity.this);
        container.addView(txtInput);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).topMargin = ViewUtil.dip2px(PointSetActivity.this, 10);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).bottomMargin = ViewUtil.dip2px(PointSetActivity.this, 10);
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).weight = ViewGroup.LayoutParams.MATCH_PARENT;
        ((LinearLayout.LayoutParams) txtInput.getLayoutParams()).height = ViewUtil.dip2px(PointSetActivity.this, 55);
        txtInput.setHint(type == 1 ? "请输入Band" : "请输入Point");
        txtInput.setSingleLine();
        txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        DialogManager.showDialog(PointSetActivity.this, str, container, "确定", new DialogManager.IClickListener() {
            public boolean click(Dialog dlg, View view) {
                String newTitle = txtInput.getText().toString();
                if (newTitle.trim().length() == 0) {
                    AppUtils.showToast(PointSetActivity.this, type == 1 ? "请输入Band" : "请输入Point");
                    return false;
                }
                AppUtils.hideInputKeyboard(PointSetActivity.this, txtInput);

                saveBnadOrPoint(newTitle, type);

                return true;
            }
        }, "取消", new DialogManager.IClickListener() {
            public boolean click(Dialog dlg, View view) {
                AppUtils.hideInputKeyboard(PointSetActivity.this, txtInput);
                return true;
            }
        }, null);
    }

    private void saveBnadOrPoint(String newTitle, int type) {
        switch (type) {
            case 1:
                BandTable bandTable = new BandTable();
                bandTable.setId(++App.get().bandId);
                bandTable.setName(newTitle);
                DataManager.getInstance().addBand(bandTable);
                mAdapter.upDataSelectedPosition();
                break;
            case 2:
                try {
                    int b = Integer.parseInt(newTitle);
                        DataManager.getInstance().upGradeBand(selectBandTable, b);
                        mPointAdapter.setNewDate(selectBandTable.getPoint());

                } catch (Exception e) {
                    AppUtils.showToast(PointSetActivity.this, "Point命名不合法");
                }

                break;
        }
    }
}
