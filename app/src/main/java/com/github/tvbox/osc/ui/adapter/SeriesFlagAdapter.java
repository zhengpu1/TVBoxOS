package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.BaseActivity.drawableLinearLayout;
import static com.github.tvbox.osc.base.BaseActivity.getColorByAttrId;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.VodInfo;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class SeriesFlagAdapter extends BaseQuickAdapter<VodInfo.VodSeriesFlag, BaseViewHolder> {
    public SeriesFlagAdapter() {
        super(R.layout.item_series_flag, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, VodInfo.VodSeriesFlag item) {
        TextView tvSeries = helper.getView(R.id.tvSeriesFlag);
        LinearLayout tvSeriesf = helper.getView(R.id.tvSeriesd);
        View select = helper.getView(R.id.tvSeriesFlagSelect);
        if (item.name.contains("-")) {
            select.setVisibility(View.GONE);
            tvSeries.setTextSize(20);
            tvSeriesf.setPadding(10,1,10,4);
        }
        if (item.selected) {
            if (item.name.contains("-")) {
                select.setVisibility(View.GONE);
            } else {
                select.setVisibility(View.VISIBLE);
            }
            tvSeries.setTextColor(getColorByAttrId(mContext, R.attr.color_theme));
        } else {
            select.setVisibility(View.GONE);
            tvSeries.setTextColor(mContext.getResources().getColor(R.color.color_80FFFFFF));
        }
        helper.setText(R.id.tvSeriesFlag, item.name);
        LinearLayout[] str = {tvSeriesf};
        drawableLinearLayout(str, 2);
    }
}