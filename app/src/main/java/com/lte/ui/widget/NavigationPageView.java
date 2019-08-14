package com.lte.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lte.R;


/**
 * 自定义分页导航控件
 * Created by 06peng on 2017/10/16.
 */

public class NavigationPageView extends LinearLayout implements View.OnClickListener {

    private int mPageSize; //每页请求条数
    private int mPage = 1; //页数
    private int mTotalCount; //数据总条数
    private int mTotalPage; //总页数

    private Button mFirstBtn, mLastBtn, mPreviousBtn, mNextBtn;
    private TextView mTotalPageTV;

    private OnPageButtonClickLister mOnPageButtonClickLister;

    public NavigationPageView(Context context) {
        this(context, null);
    }

    public NavigationPageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.navigation_page_layout, this);
        mFirstBtn = (Button) findViewById(R.id.nav_first);
        mFirstBtn.setOnClickListener(this);
        mLastBtn = (Button) findViewById(R.id.nav_last);
        mLastBtn.setOnClickListener(this);
        mPreviousBtn = (Button) findViewById(R.id.nav_previous);
        mPreviousBtn.setOnClickListener(this);
        mNextBtn = (Button) findViewById(R.id.nav_next);
        mNextBtn.setOnClickListener(this);
        mTotalPageTV = (TextView) findViewById(R.id.nav_total);
    }

    public void initData(int count, int pageSize) {
        this.mTotalCount = count;
        this.mPageSize = pageSize;
        mPage =1;
        mTotalPage = mTotalCount / mPageSize + (mTotalCount % mPageSize > 0 ? 1 : 0);
        refreshButtonState();
    }

    public void setOnPageButtonClickLister(OnPageButtonClickLister onPageButtonClickLister) {
        this.mOnPageButtonClickLister = onPageButtonClickLister;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_first:
                firstButtonClick();
                break;
            case R.id.nav_last:
                lastButtonClick();
                break;
            case R.id.nav_previous:
                previousButtonClick();
                break;
            case R.id.nav_next:
                nextButtonClick();
                break;
        }
        refreshButtonState();
        if (mOnPageButtonClickLister != null) {
            mOnPageButtonClickLister.pageClick(mPage);
        }
    }

    private void refreshButtonState() {
        if (mPage == 1) {//第一页
            mFirstBtn.setEnabled(false);
            mPreviousBtn.setEnabled(false);
        } else {
            mFirstBtn.setEnabled(true);
            mPreviousBtn.setEnabled(true);
        }
        if (mPage < mTotalPage) { //当前页数比总页数少
            mLastBtn.setEnabled(true);
            mNextBtn.setEnabled(true);
        } else {
            mLastBtn.setEnabled(false);
            mNextBtn.setEnabled(false);
        }
        if (mTotalPage == 1) {
            mFirstBtn.setEnabled(false);
            mPreviousBtn.setEnabled(false);
            mLastBtn.setEnabled(false);
            mNextBtn.setEnabled(false);
        }
        mTotalPageTV.setText("页数：" + mPage+"/"+mTotalPage);
    }

    private void firstButtonClick() {
        mFirstBtn.setEnabled(false);
        mPreviousBtn.setEnabled(false);

        mLastBtn.setEnabled(true);
        mNextBtn.setEnabled(true);

        mPage = 1;
    }

    private void lastButtonClick() {
        mLastBtn.setEnabled(false);
        mNextBtn.setEnabled(false);

        mFirstBtn.setEnabled(true);
        mPreviousBtn.setEnabled(true);

        mPage = mTotalPage;
    }

    private void previousButtonClick() {
       mPage--;
    }

    private void nextButtonClick() {
        mPage++;
    }

    public interface OnPageButtonClickLister {
        void pageClick(int page);
    }
}
