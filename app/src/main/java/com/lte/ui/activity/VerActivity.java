package com.lte.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.App;
import com.lte.R;
import com.lte.ui.adapter.VerAdapter;
import com.lte.ui.event.SystemOutEvent;
import com.lte.ui.widget.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.yokeyword.fragmentation.SupportActivity;

import static com.lte.utils.DateUtils.getVersionName;

/**
 * Created by chenxiaojun on 2017/11/6.
 */

public class VerActivity extends BaseActivity {
    private TitleBar titleBar;
    private RecyclerView recyclerView;
    private VerAdapter mAdapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_activity);
        titleBar = (TitleBar) findViewById(R.id.titlebar);
        titleBar.setTitle(R.string.verSoft);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerActivity.this.finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.ver_list);

//        textView = (TextView) findViewById(R.id.text);
//
//        String temp = getResources().getString(R.string.version);
//        String tabCurrentUser = String.format(temp, getVersionName(this));
//        textView.setText(tabCurrentUser);

        mAdapter = new VerAdapter(this, App.get().getOnLineList());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(mAdapter);
        EventBus.getDefault().register(this);
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
}
