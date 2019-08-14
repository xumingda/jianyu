package com.lte.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lte.R;
import com.lte.data.table.SceneTable;

import io.realm.RealmResults;

import static com.communication.utils.DateUtil.formatTime;

/**
 * Created by chenxiaojun on 2017/8/26.
 */

public class SceneDialogAdapter extends RealmRecyclerViewAdapter<SceneTable,SceneDialogAdapter.MViewHolder> {
    private final static String TAG = "FunctionAdapter";
    private LayoutInflater mInflater;
    private RealmResults<SceneTable> mList;
    private Context mContext;
    private CheckListener onCheckListener;
    private final RecyclerView recyclerView;

    public SceneDialogAdapter(Context context, RealmResults<SceneTable> list, CheckListener onCheckListener, RecyclerView recyclerView) {
        super(list,true,recyclerView);
        this.recyclerView = recyclerView;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = list;
        this.onCheckListener = onCheckListener;
    }

    public void upDataList(RealmResults<SceneTable> mList) {
        this.mList = mList;
        super.updateData(mList);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MViewHolder(mInflater.inflate(R.layout.item_type_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        final SceneTable sceneTable = mList.get(position);
        Log.d("DataManager","sceneTable :" + sceneTable.toString());
        holder.textView.setText(sceneTable.getName()+"("+formatTime(sceneTable.getmBeginMillseconds())+"-"+formatTime(sceneTable.getmEndMillseconds())+")");
        if (position == mList.size() - 1) {
            holder.bottom.setVisibility(View.VISIBLE);
        } else {
            holder.bottom.setVisibility(View.GONE);
        }
    }

    class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView textView;
        final View top;
        final View bottom;

        MViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_type_text);
            top = (View) itemView.findViewById(R.id.devider_top);
            bottom = (View) itemView.findViewById(R.id.devider_bottom);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                    if (onCheckListener != null && mList.size() > getAdapterPosition() && getAdapterPosition() >= 0) {
                        onCheckListener.onClick(v, mList.get(getAdapterPosition()));
                    }
        }
    }
    public interface CheckListener{
        void onClick(View view, SceneTable imsiData);
    }
}
