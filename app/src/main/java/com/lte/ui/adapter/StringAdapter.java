package com.lte.ui.adapter;

import com.github.library.BaseQuickAdapter;
import com.github.library.BaseViewHolder;
import com.lte.R;
import com.lte.ui.activity.QueryMobileActivity;

import java.util.List;

public class StringAdapter extends BaseQuickAdapter<String, BaseViewHolder>{

    public StringAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_type_text,item);
    }
}
