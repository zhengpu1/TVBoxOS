package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.drawableLinearLayout;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class SeriesAdapter extends BaseQuickAdapter<VodInfo.VodSeries, BaseViewHolder> {
    public SeriesAdapter() {
        super(R.layout.item_series, new ArrayList<>());
    }
    private void LParams(LinearLayout RIDD) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = AutoSizeUtils.mm2px(mContext,112);
        RIDD.setLayoutParams(params);
    }

    @Override
    protected void convert(BaseViewHolder helper, VodInfo.VodSeries item) {
        TextView tvSeries = helper.getView(R.id.tvSeries);
        LinearLayout tvSeriesa = helper.getView(R.id.tvSeriesdd);

        if (item.selected) {
            tvSeries.setTextColor(mContext.getResources().getColor(R.color.color_00FF0A));

        } else {
            tvSeries.setTextColor(mContext.getResources().getColor(R.color.color_FFF5E8DC));
        }
        helper.setText(R.id.tvSeries, item.name.replace("-",""));
        if(Hawk.get(HawkConfig.EPISODE_GROUPING, false)) {
            LParams(tvSeriesa);
        }
        LinearLayout[] str = {tvSeriesa};
        drawableLinearLayout(str, 1);
    }

}