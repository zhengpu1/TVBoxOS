package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.BaseActivity.drawableLinearLayout;

import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class SearchHistoryAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SearchHistoryAdapter() {
        super(R.layout.item_search_history, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, String name) {
        helper.setText(R.id.tvSeries, name);
        LinearLayout tvSeriesa = helper.getView(R.id.llhiSeries);
        LinearLayout[] str = {tvSeriesa};
        drawableLinearLayout(str, 1);
    }
}