package com.lte.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.App;
import com.communication.request.HttpCallback;
import com.communication.utils.LETLog;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lte.R;
import com.lte.data.DataManager;
import com.lte.data.table.MobileResultTable;
import com.lte.https.MobileQuery;
import com.lte.ui.adapter.StringAdapter;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.tagview.Tag;
import com.lte.ui.tagview.TagView;
import com.lte.ui.widget.CommonToast;
import com.lte.ui.widget.DialogManager;
import com.lte.ui.widget.TitleBar;
import com.lte.utils.DateUtils;
import com.lte.utils.ThreadUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.yokeyword.fragmentation.SupportActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by chenxiaojun on 2017/12/18.
 */

public class QueryMobileActivity extends BaseActivity implements View.OnClickListener {

    private static final int TIME_OUT = 4;
    TextInputEditText imsi_et;

    Button addButton;

    Button queryButton;

    SwipeMenuRecyclerView tag_imsi;

    SwipeMenuRecyclerView tag_result;

    private List<String> result = new ArrayList<>();

    private List<String> imsi = new ArrayList<>();

    boolean isWrite;

    private AtomicReference<String> localProgressFlag;

    private String writeImsi;
    private HttpCallback callBack = new HttpCallback() {
        @Override
        public void onSuccess(final JsonElement jsonObject) {
            if (isWrite) {
                ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
                    @Override
                    public void run() {
                        LETLog.d(DateUtils.getNowDates(System.currentTimeMillis()) + jsonObject.toString());
                        try {
                            JsonObject asJsonObject = jsonObject.getAsJsonObject();
                            if (asJsonObject.get("responseCode") != null) {
                                if (asJsonObject.get("responseCode").getAsInt() == 200) {
                                    mHandler.sendEmptyMessage(1);
                                } else {
                                    CommonToast.show(QueryMobileActivity.this, "提交失败，令牌可能过期，正在请求令牌，请稍后20秒再试");
                                    MobileQuery.getInstance().getApiKey(null);
                                }
                            } else {

                            }
                        } catch (Exception e) {

                        }
                    }
                });
                isWrite = false;
            }
        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };
    private TitleBar titleBar;

    private int writeNum;
    private StringAdapter mImsiAdapter;
    private LinearLayoutManager mImsiLayoutManager;
    private SwipeMenuItemClickListener menuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            final int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            if(imsi.size() >adapterPosition){
                imsi.remove(adapterPosition);
                mImsiAdapter.setNewData(imsi);
            }
        }
    };
    private SwipeItemClickListener onItemClickListener = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {

        }
    };
    private SwipeMenuItemClickListener menuItemClickListener1 = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            final int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            final  String text = result.get(adapterPosition);
            result.remove(text);
            resultAdapter.setNewData(result);
            DataManager.getInstance().deleteMobileResult(text);
        }
    };
    private SwipeItemClickListener onItemClickListener1 = new SwipeItemClickListener() {
        @Override
        public void onItemClick(View itemView, int position) {

        }
    };
    private LinearLayoutManager mResultLayoutManager;
    private StringAdapter resultAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobile_activity);
        init();
        EventBus.getDefault().register(this);
    }

    private void init() {

        titleBar = (TitleBar) findViewById(R.id.titlebar);

        titleBar.setTitle(getString(R.string.mobile_zidingyi));

        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedSupport();

            }
        });
        imsi_et = (TextInputEditText) findViewById(R.id.imsi_et);

        addButton = (Button) findViewById(R.id.add_button);

        queryButton = (Button) findViewById(R.id.query_button);

        tag_imsi = (SwipeMenuRecyclerView) findViewById(R.id.tag_imsi);

        tag_result = (SwipeMenuRecyclerView) findViewById(R.id.tag_result);

        addButton.setOnClickListener(this);

        queryButton.setOnClickListener(this);

        tag_imsi.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        tag_imsi.setSwipeMenuItemClickListener(menuItemClickListener);
        tag_imsi.setSwipeItemClickListener(onItemClickListener);

        mImsiLayoutManager = new LinearLayoutManager(QueryMobileActivity.this);
        tag_imsi.setLayoutManager(mImsiLayoutManager);
        mImsiAdapter = new StringAdapter(R.layout.item_string,imsi);
        tag_imsi.setAdapter(mImsiAdapter);

        tag_result.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        tag_result.setSwipeMenuItemClickListener(menuItemClickListener1);
        tag_result.setSwipeItemClickListener(onItemClickListener1);

        mResultLayoutManager = new LinearLayoutManager(QueryMobileActivity.this);
        tag_result.setLayoutManager(mResultLayoutManager);
        resultAdapter = new StringAdapter(R.layout.item_string,result);
        tag_result.setAdapter(resultAdapter);

        DataManager.getInstance().findMobileResult(new RealmChangeListener<RealmResults<MobileResultTable>>() {
            @Override
            public void onChange(RealmResults<MobileResultTable> mobileResultTables) {
                Log.d("DataManager", "findMobileResult :" + mobileResultTables.size());
                for (MobileResultTable mobileResultTable : mobileResultTables) {
                    result.add(mobileResultTable.getMobile());
                }
                resultAdapter.setNewData(result);
            }
        });
    }
    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int with = getResources().getDimensionPixelSize(R.dimen.dp48);
            int height = getResources().getDimensionPixelSize(R.dimen.dp48);
            SwipeMenuItem deleteItem = new SwipeMenuItem(QueryMobileActivity.this)
                    .setBackgroundColorResource(R.color.colorAccent)
//                    .setImage(R.mipmap.ic_action_delete) // 图标。
                    .setText("删除") // 文字。
                    .setTextColor(Color.WHITE) // 文字颜色。
                    .setTextSize(16) // 文字大小。
                    .setWidth(with)
                    .setHeight(MATCH_PARENT);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
        }
    };
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                if (TextUtils.isEmpty(imsi_et.getText().toString())) {
                    CommonToast.show(QueryMobileActivity.this, "请输入imsi或手机号");
                    return;
                }
                if (!App.get().apikeyCanUse) {
                    CommonToast.show(QueryMobileActivity.this, "令牌未准备好，请稍后20秒再尝试");
                    return;
                }

                if (writeNum >= 5) {
                    CommonToast.show(QueryMobileActivity.this, "最多只能添加5个数据");
                    return;
                }
                isWrite = true;
                writeImsi = imsi_et.getText().toString();
                if (writeImsi.startsWith("1")) {
                    MobileQuery.getInstance().writeData("00086" + writeImsi, callBack);
                } else {
                    MobileQuery.getInstance().writeData("101" + writeImsi, callBack);
                }
                localProgressFlag = new AtomicReference<>();
                localProgressFlag.set(DialogManager.showProgressDialog(QueryMobileActivity.this, "正在提交，请稍候..",true));
                mHandler.sendEmptyMessageDelayed(TIME_OUT, 30 * 1000l);
                break;
            case R.id.query_button:
                localProgressFlag = new AtomicReference<>();
                localProgressFlag.set(DialogManager.showProgressDialog(QueryMobileActivity.this, "查询中，请稍候..",true));
                MobileQuery.getInstance().readData(callBack1);
                mHandler.sendEmptyMessageDelayed(TIME_OUT, 30 * 1000l);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String condition;
                if (writeImsi.startsWith("1")) {
                    condition = (getString(R.string.query_mobile_mobile) + writeImsi);
                } else {
                    condition = (getString(R.string.query_mobiel_imsi) + writeImsi);
                }
                imsi.add(condition);
                mImsiAdapter.setNewData(imsi);
                writeNum++;
                DialogManager.closeDialog(localProgressFlag.get());
                CommonToast.show(QueryMobileActivity.this, "提交成功");
            } else if (msg.what == 2) {
                String resultString = (String) msg.obj;
                if(!result.contains(resultString)){
                    result.add(resultString);
                    resultAdapter.setNewData(result);
                }
                removeMessages(TIME_OUT);
            } else if (msg.what == 3) {
                MobileQuery.getInstance().readData(callBack1);
            } else if (msg.what == 4) {
                removeMessages(3);
                try {
                    DialogManager.closeDialog(localProgressFlag.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CommonToast.show(QueryMobileActivity.this, "获取数据失败");
            }

        }
    };
    private HttpCallback callBack1 = new HttpCallback() {
        @Override
        public void onSuccess(final JsonElement jsonObject) {
            ThreadUtils.getThreadPoolProxy().execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("http", "jsonObject :" + jsonObject);
                    try {
                        JsonArray asJsonArray = jsonObject.getAsJsonArray();
                        for (JsonElement jsonElement : asJsonArray) {
                            JsonObject asJsonObject = jsonElement.getAsJsonObject();
                            if (asJsonObject.get("rcontent") != null) {
                                final String rcontent = new String(Base64.decode(asJsonObject.get("rcontent").toString(), Base64.DEFAULT));
                                Log.d("http", "rcontent :" + rcontent);
                                ThreadUtils.getThreadPoolProxy().execute(new ParseRunnable(rcontent));
                            }
                            writeNum = 0;
                            try {
                                DialogManager.closeDialog(localProgressFlag.get());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    } catch (Exception ignored) {

                    }
                    try {
                        JsonObject asJsonObject = jsonObject.getAsJsonObject();
                        if (asJsonObject.get("responseCode") != null) {
                            if (asJsonObject.get("responseCode").getAsInt() == 404) {
                                //查询库内没有结果，重新查询。
                                mHandler.sendEmptyMessageDelayed(3, 3000L);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });

        }

        @Override
        public void onFailed(Exception errorMsg) {

        }
    };

    private class ParseRunnable implements Runnable {
        final String rcontent;

        public ParseRunnable(String rcontent) {
            this.rcontent = rcontent;
        }

        @Override
        public void run() {
            for (String tag : imsi) {
                Log.d("http", "tag :" + tag);
                if (tag.replace(getString(R.string.query_mobile_mobile),"").startsWith("1")) {
                    String trim = rcontent.trim();
                    String mobile = trim.substring(5, 16);
                    String imsi = trim.substring(18);
                    Log.d("http", "imsi :" + imsi + "---" + "mobile ：" + mobile);
                    if (TextUtils.equals(tag.replace(getString(R.string.query_mobile_mobile),""), mobile)) {
                        String condition = (imsi + " " + mobile);
                        DataManager.getInstance().crateOrUpdateMobileResult(condition);
                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = condition;
                        mHandler.sendMessage(message);
                    }
                } else {
                    String trim = rcontent.trim();
                    String imsi = trim.substring(3, 18);
                    String mobile = trim.substring(22);
                    Log.d("http1", "imsi :" + imsi + "---" + "mobile ：" + mobile + " --");
                    if (TextUtils.equals(tag.replace(getString(R.string.query_mobiel_imsi),""), imsi)) {
                        String condition = (imsi + " " + mobile);
                        DataManager.getInstance().crateOrUpdateMobileResult(condition);
                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = condition;
                        mHandler.sendMessage(message);
                    }
                }
            }
        }
    }
}
