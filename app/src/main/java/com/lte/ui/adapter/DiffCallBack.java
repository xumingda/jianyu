package com.lte.ui.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.lte.data.StationInfo;
import com.lte.utils.Constants;

import java.util.List;

/**
 * 介绍：核心类 用来判断 新旧Item是否相等
 * 作者：zhangxutong
 * 邮箱：zhangxutong@imcoming.com
 * 时间： 2016/9/12.
 */

public class DiffCallBack extends DiffUtil.Callback {


    private List<StationInfo> mOldDatas;
    private List<StationInfo> mNewDatas;
    private final String Tag = "DiffCallBack";

    public DiffCallBack(List<StationInfo> mOldDatas, List<StationInfo> mNewDatas) {
        this.mOldDatas = mOldDatas;
        this.mNewDatas = mNewDatas;
        Log.d("DiffCallBack","mOldDatas :" + mOldDatas.size() +"mNewDatas :" +mNewDatas.size());
    }

    //老数据集size
    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    //新数据集size
    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     * 被DiffUtil调用，用来判断 两个对象是否是相同的Item。
     * For example, if your items have unique ids, this method should check their id equality.
     * 例如，如果你的Item有唯一的id字段，这个方法就 判断id是否相等。
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     */
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        StationInfo beanOld = mOldDatas.get(oldItemPosition);
        StationInfo beanNew = mNewDatas.get(newItemPosition);
        Log.d("getChangePayload","areItemsTheSame" +TextUtils.equals(beanNew.getIp(), beanOld.getIp()));
        return TextUtils.equals(beanNew.getIp(), beanOld.getIp());
    }

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * DiffUtil用返回的信息（true false）来检测当前item的内容是否发生了变化
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * DiffUtil 用这个方法替代equals方法去检查是否相等。
     * so that you can change its behavior depending on your UI.
     * 所以你可以根据你的UI去改变它的返回值
     * For example, if you are using DiffUtil with a
     * {@link RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * 例如，如果你用RecyclerView.Adapter 配合DiffUtil使用，你需要返回Item的视觉表现是否相同。
     * This method is called only if {@link #areItemsTheSame(int, int)} returns
     * {@code true} for these items.
     * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     *                        oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        StationInfo beanOld = mOldDatas.get(oldItemPosition);
        StationInfo beanNew = mNewDatas.get(newItemPosition);
        Log.d(Tag,"beanOld "+beanOld.toString()
        +"beanNew"+beanNew.toString());
        return beanNew.isConfigDBM() == beanOld.isConfigDBM() && beanOld.getConnectionStatus() == beanNew.getConnectionStatus()
                && beanNew.getConfigState() == beanOld.getConfigState() && beanNew.getCpu_tem()== beanOld.getCpu_tem()
                &&beanNew.getCpu_use() == beanOld.getCpu_use() && beanNew.getRom_use() == beanOld.getRom_use()
                &&beanNew.getDBM() == beanOld.getDBM() && TextUtils.equals(beanNew.getSoft_state(),beanOld.getSoft_state())
                &&beanNew.getTem() == beanOld.getTem()&&beanNew.getFreq() == beanOld.getFreq() && beanNew.getPci() == beanOld.getPci()
                &&beanNew.getTAC() == beanOld.getTAC() && beanNew.getRSSI() == beanOld.getRSSI();
    }

    /**
     * When {@link #areItemsTheSame(int, int)} returns {@code true} for two items and
     * {@link #areContentsTheSame(int, int)} returns false for them, DiffUtil
     * calls this method to get a payload about the change.
     * <p>
     * 当{@link #areItemsTheSame(int, int)} 返回true，且{@link #areContentsTheSame(int, int)} 返回false时，DiffUtils会回调此方法，
     * 去得到这个Item（有哪些）改变的payload。
     * <p>
     * For example, if you are using DiffUtil with {@link RecyclerView}, you can return the
     * particular field that changed in the item and your
     * {@link RecyclerView.ItemAnimator ItemAnimator} can use that
     * information to run the correct animation.
     * <p>
     * 例如，如果你用RecyclerView配合DiffUtils，你可以返回  这个Item改变的那些字段，
     * {@link RecyclerView.ItemAnimator ItemAnimator} 可以用那些信息去执行正确的动画
     * <p>
     * Default implementation returns {@code null}.\
     * 默认的实现是返回null
     *
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list
     * @return A payload object that represents the change between the two items.
     * 返回 一个 代表着新老item的改变内容的 payload对象，
     */
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //实现这个方法 就能成为文艺青年中的文艺青年
        // 定向刷新中的部分更新
        // 效率最高
        //只是没有了ItemChange的白光一闪动画，（反正我也觉得不太重要）
        StationInfo beanOld = mOldDatas.get(oldItemPosition);
        StationInfo beanNew = mNewDatas.get(newItemPosition);
        Bundle bundle = new Bundle();
        Log.d("getChangePayload",beanNew.getName()+"---" +beanOld.getName());
        if (beanNew.isConfigDBM() != beanOld.isConfigDBM()) {
            bundle.putBoolean(Constants.DATA, beanNew.isConfigDBM());
        }
        if (beanOld.getConnectionStatus() != beanNew.getConnectionStatus()) {
            bundle.putInt(Constants.STATE, beanNew.getConnectionStatus().ordinal());
        }
        if (beanOld.getConfigState() != beanNew.getConfigState()) {
            bundle.putInt(Constants.CONFIG_STATE, beanNew.getConfigState().ordinal());
        }
        if (beanOld.getCpu_tem() != beanNew.getCpu_tem()) {
            bundle.putInt(Constants.CPU_TEM, beanNew.getCpu_tem());
        }
        if (beanOld.getCpu_use() != beanNew.getCpu_use()) {
            bundle.putInt(Constants.CPU_USE, beanNew.getCpu_use());
        }
        if (beanOld.getRom_use() != beanNew.getRom_use()) {
            bundle.putInt(Constants.ROM_UES, beanNew.getRom_use());
        }
        if (beanOld.getTem() != beanNew.getTem()) {
            bundle.putInt(Constants.TEM, beanNew.getTem());
        }
        if (beanOld.getFreq() != beanNew.getFreq()) {
            bundle.putInt(Constants.FREQ, beanNew.getFreq());
        }
        if (beanOld.getPci() != beanNew.getPci()) {
            bundle.putInt(Constants.PCI, beanNew.getPci());
        }
        if (beanOld.getTAC() != beanNew.getTAC()) {
            bundle.putInt(Constants.TAC, beanNew.getTAC());
        }
        if (beanOld.getRSSI() != beanNew.getRSSI()) {
            bundle.putInt(Constants.RSSI, beanNew.getRSSI());
        }
        if (!TextUtils.equals(beanNew.getSoft_state(),beanOld.getSoft_state())) {
            bundle.putString(Constants.SOFT_STATE, beanNew.getSoft_state());
        }
        if (bundle.size() == 0) {
            return null;
        }
        return bundle;
    }

}
