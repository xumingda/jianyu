package com.lte.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter;
import com.cleveroad.adaptivetablelayout.OnItemLongClickListener;
import com.cleveroad.adaptivetablelayout.ViewHolderImpl;
import com.lte.R;
import com.lte.data.table.ImsiDataTable;
import com.lte.ui.adapter.datasource.TableDataSource;

public class AnalyTableAdapter extends LinkedAdaptiveTableAdapter<ViewHolderImpl> {

    private final LayoutInflater mLayoutInflater;
    private TableDataSource<String, String, String, String> mTableDataSource;
    private final int mColumnWidth;
    private final int mRowHeight;
    private final int mHeaderHeight;
    private final int mHeaderWidth;

    private static CheckListener onCheckListener;

    public AnalyTableAdapter(Context context, TableDataSource<String, String, String, String> tableDataSource,CheckListener onCheckListener) {
        mLayoutInflater = LayoutInflater.from(context);
        mTableDataSource = tableDataSource;
        Resources res = context.getResources();
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width);
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height);
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height);
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width);
        this.onCheckListener = onCheckListener;
    }
    public void upDateDataSource(TableDataSource<String, String, String, String> tableDataSource){
        this.mTableDataSource = tableDataSource;
        notifyDataSetChanged();
    }
    @Override
    public int getRowCount() {
        return mTableDataSource.getRowsCount();
    }

    @Override
    public int getColumnCount() {
        return mTableDataSource.getColumnsCount();
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateItemViewHolder(@NonNull ViewGroup parent) {
        return new TestViewHolder(mLayoutInflater.inflate(R.layout.table_item_card, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.table_item_header_column, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateRowHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.table_item_header_row, parent, false));
    }

    @NonNull
    @Override
    public ViewHolderImpl onCreateLeftTopHeaderViewHolder(@NonNull ViewGroup parent) {
        return new TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.table_item_header_left_top, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderImpl viewHolder, int row, int column) {
        final TestViewHolder vh = (TestViewHolder) viewHolder;
        String itemData = mTableDataSource.getItemData(row, column); // skip headers

        if (TextUtils.isEmpty(itemData)) {
            itemData = "";
        }

        itemData = itemData.trim();
        vh.tvText.setVisibility(View.VISIBLE);
        vh.ivImage.setVisibility(View.VISIBLE);
        vh.tvText.setText(itemData);
    }

    @Override
    public void onBindHeaderColumnViewHolder(@NonNull ViewHolderImpl viewHolder, int column) {
        TestHeaderColumnViewHolder vh = (TestHeaderColumnViewHolder) viewHolder;
        vh.tvText.setText(mTableDataSource.getColumnHeaderData(column));  // skip left top header
    }

    @Override
    public void onBindHeaderRowViewHolder(@NonNull ViewHolderImpl viewHolder, int row) {
        TestHeaderRowViewHolder vh = (TestHeaderRowViewHolder) viewHolder;
        vh.tvText.setText(mTableDataSource.getItemData(row, 0));
    }

    @Override
    public void onBindLeftTopHeaderViewHolder(@NonNull ViewHolderImpl viewHolder) {
        TestHeaderLeftTopViewHolder vh = (TestHeaderLeftTopViewHolder) viewHolder;
        vh.tvText.setText(mTableDataSource.getFirstHeaderData());
    }

    @Override
    public int getColumnWidth(int column) {
        return mColumnWidth;
    }

    @Override
    public int getHeaderColumnHeight() {
        return mHeaderHeight;
    }

    @Override
    public int getRowHeight(int row) {
        return mRowHeight;
    }

    @Override
    public int getHeaderRowWidth() {
        return mHeaderWidth;
    }

    //------------------------------------- view holders ------------------------------------------


    private static class TestViewHolder extends ViewHolderImpl {
        TextView tvText;
        ImageView ivImage;

        private TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }
    public interface CheckListener{
        void onLongClick(View view, String text);
    }
    private static class TestHeaderColumnViewHolder extends ViewHolderImpl {
        TextView tvText;
        View vGradient;
        View vLine;

        private TestHeaderColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
            vGradient = itemView.findViewById(R.id.vGradient);
            vLine = itemView.findViewById(R.id.vLine);
        }
    }

    private static class TestHeaderRowViewHolder extends ViewHolderImpl {
        TextView tvText;

        TestHeaderRowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }

    private static class TestHeaderLeftTopViewHolder extends ViewHolderImpl {
        TextView tvText;

        private TestHeaderLeftTopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(R.id.tvText);
        }
    }
}
