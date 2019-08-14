package com.lte.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.App;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.SceneTable;
import com.lte.ui.adapter.SceneDialogAdapter;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.SweetAlertDialog;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.RealmResults;

import static com.lte.utils.Constants.TYPE;
import static com.lte.utils.DateUtils.getVersionName;

/**
 * Created by chenxiaojun on 2018/1/11.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener, SceneDialogAdapter.CheckListener {

    private TextView tv_appver;

//    private TextView cache;
    private TitleBar titleBar;
    private SweetAlertDialog mDialog;
    private RecyclerView scene_list;
    private RealmResults<SceneTable> mSceneList;
    private SceneDialogAdapter sceneAdapter;
    private TextView delete_all;
    private SweetAlertDialog mDialog1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_fragment);
        init();
    }

    private void init() {
        String temp = getResources().getString(R.string.copyright_ver);
        String ver=getVersionName(this);
        String time= DateUtils.milliToSimpleDateYear(System.currentTimeMillis());

        String tabCurrentUser = String.format(temp, ver.substring(10,ver.length())+time);

        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(R.string.setting);

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EventBus.getDefault().register(this);

        tv_appver = (TextView) findViewById(R.id.tv_appver);

//        cache = (TextView)findViewById(R.id.cache);

        tv_appver.setText(tabCurrentUser);


//        cache.setText(DataManager.getInstance().getCacheSize());

        RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.layout_two);
        relativeLayout2.setOnClickListener(this);
        RelativeLayout relativeLayout1 = (RelativeLayout) findViewById(R.id.layout_one);
        relativeLayout1.setOnClickListener(this);
        RelativeLayout relativeLayout3 = (RelativeLayout) findViewById(R.id.layout_three);
        relativeLayout3.setOnClickListener(this);
        RelativeLayout relativeLayout4 = (RelativeLayout) findViewById(R.id.layout_four);
        relativeLayout4.setOnClickListener(this);

        initClearDialog();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_one: {
                Intent intent = new Intent(SetActivity.this, HttpSetActivity.class);
                intent.putExtra(TYPE,1);
                startActivity(intent);
                break;
            }
            case R.id.layout_two: {
                Intent intent = new Intent(SetActivity.this, VerActivity.class);
                intent.putExtra(TYPE,2);
                startActivity(intent);
                break;
            }
            case R.id.layout_three:
                mDialog.show();
                break;
            case R.id.layout_four: {
                Intent intent = new Intent(SetActivity.this, DeviceRegisterActivity.class);
                intent.putExtra(TYPE,4);
                startActivity(intent);
                break;
            }
        }
    }
    private void initClearDialog() {
        mDialog = new SweetAlertDialog.Builder(SetActivity.this)
                .setTitle(R.string.delete_scene)
                .setHasTwoBtn(false)
                .setOneButton("取消", new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog.dismiss();
                    }
                })
                .create();
        mDialog.addContentView(R.layout.dialog_delete_scene_list);
        scene_list = (RecyclerView) mDialog.findView(R.id.scene_list);
        delete_all = (TextView) mDialog.findView(R.id.delete_all);
        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                showDeleteSureDialog(null,getString(R.string.delete_all),true);
            }
        });
        mSceneList = DataManager.getInstance().findSceneList();
        sceneAdapter = new SceneDialogAdapter(this, mSceneList, this, scene_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        scene_list.setLayoutManager(linearLayoutManager);
        scene_list.setAdapter(sceneAdapter);
    }
    private void showDeleteSureDialog(final SceneTable sceneTable, String str, final boolean isDeleteAll) {
        mDialog1 = new SweetAlertDialog.Builder(SetActivity.this)
                .setMessage(str)
                .setHasTwoBtn(true)
                .setNegativeButton(R.string.cancel, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        mDialog1.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new SweetAlertDialog.OnDialogClickListener() {
                    @Override
                    public void onClick(Dialog dialog, int which) {
                        if(isDeleteAll){
                            DataManager.getInstance().removeAll();
                        }else {
                            DataManager.getInstance().deleteScene(sceneTable);
                        }
                        App.get().initData();
                    }
                })
                .create();
        mDialog1.show();
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
    public void onClick(View view, SceneTable imsiData) {
        showDeleteSureDialog(imsiData,String.format(getString(R.string.delete_scene_sure),imsiData.getName()),false);
        mDialog.dismiss();
    }
}
