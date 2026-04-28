package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.drawableLinearLayout;
import static com.github.tvbox.osc.base.baseActivity.getColorByAttrId;

import android.content.Context;
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
public class SeriesAdapters extends BaseQuickAdapter<VodInfo.VodSeries, BaseViewHolder> {
    private Context context;

    public SeriesAdapters(Context context) {
        super(R.layout.item_series_flag, new ArrayList<>());
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, VodInfo.VodSeries item) {
        TextView tvSeries = helper.getView(R.id.tvSeries);
        LinearLayout tvSeriesa = helper.getView(R.id.tvSeriesdd);
            if (item.selected) {
                tvSeries.setTextColor(getColorByAttrId(context, R.attr.color_theme));
            } else {
                tvSeries.setTextColor(context.getResources().getColor(R.color.color_80FFFFFF));
            }
        helper.setText(R.id.tvSeries, item.name.replace("-",""));
        LinearLayout[] str = {tvSeriesa};
        drawableLinearLayout(str, 1);
    }

}