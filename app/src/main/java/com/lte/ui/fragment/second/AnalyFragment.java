package com.lte.ui.fragment.second;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.lte.R;
import com.lte.ui.widget.CustomViewPager;
import com.lte.ui.widget.PagerSlidingTabStrip;

import java.util.HashMap;
import java.util.Map;

import me.yokeyword.fragmentation.SupportFragment;


public class AnalyFragment extends SupportFragment implements View.OnClickListener {
    private Context context;
    private View mView = null;
    private PagerSlidingTabStrip titleIndicator;
    private CustomViewPager pager;
    private LocalAdapter adapter;
    public static final int INDEX_FOLLOW =0;
    public static final int INDEX_COLLISION =1;
    public static final int INDEX_STATISTIC =2;
    public static final int INDEX_HISTORY_DATA=3;
    private Map<Integer,Fragment> viewMaps = new HashMap<Integer,Fragment>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_analy_view, container, false);
        titleIndicator = (PagerSlidingTabStrip) mView.findViewById(R.id.title_indicator);
        pager = (CustomViewPager) mView.findViewById(R.id.viewpager);

        adapter = new LocalAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);
        titleIndicator.setViewPager(pager);
        setTabsValue(titleIndicator);
        return mView;
    }

    private void setTabsValue(PagerSlidingTabStrip titleIndicator) {
        DisplayMetrics dm  = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        titleIndicator.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        titleIndicator.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        titleIndicator.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        titleIndicator.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm));
        // 设置Tab标题文字的大小
        titleIndicator.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, dm));
        // 设置Tab Indicator的颜色
        titleIndicator.setIndicatorColor(getResources().getColor(R.color.colorPrimary));
        titleIndicator.setTextColor(getResources().getColor(R.color.tittle_text_color_normal));
        // 设置选中Tab文字的颜色
        titleIndicator.setSelectedTextColor(getResources().getColor(R.color.colorPrimary));
        // 取消点击Tab时的背景色
        titleIndicator.setTabBackground(0);
        titleIndicator.setTabSubtextColor(0xff999999, 14);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static AnalyFragment newInstance() {
        return new AnalyFragment();
    }

    private class LocalAdapter extends FragmentStatePagerAdapter {

        public LocalAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case INDEX_FOLLOW:
                    if(viewMaps.get(INDEX_FOLLOW)==null){
                        viewMaps.put(INDEX_FOLLOW, new AnalyFollowFragment());
                    }
                    break;
                case INDEX_COLLISION:
                    if(viewMaps.get(INDEX_COLLISION)==null){
                        viewMaps.put(INDEX_COLLISION,  new AnalyCollisionFragment());
                    }
                    break;
                case INDEX_STATISTIC:
                    if(viewMaps.get(INDEX_STATISTIC)==null){
                        viewMaps.put(INDEX_STATISTIC, new AnalyStatisticFragment());
                    }
                    break;
                case INDEX_HISTORY_DATA:
                    if(viewMaps.get(INDEX_HISTORY_DATA)==null){
                        viewMaps.put(INDEX_HISTORY_DATA, new AnalyHistoryFragment());
                    }
                    break;
            }
            return viewMaps.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title=null;
            switch (position) {
                case INDEX_FOLLOW:
                    title = "伴随分析";
                    break;
                case INDEX_COLLISION:
                    title = "碰撞分析";
                    break;
                case INDEX_STATISTIC:
                    title = "统计分析";
                    break;
                case INDEX_HISTORY_DATA:
                    title = "历史数据";
                    break;
            }
            return title;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
