package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.drawableLinearLayout;
import static com.github.tvbox.osc.base.baseActivity.getColorByAttrId;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.VodInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeriesFlagAdapter extends BaseQuickAdapter<VodInfo.VodSeriesFlag, BaseViewHolder> {
    private final Context context;

    public SeriesFlagAdapter(Context context) {
        super(R.layout.item_series_flag, new ArrayList<>());
        this.context = context;
    }

    private String cleanFlagName(String name) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("\uD83C\uDF38荷城茶秀接口\uD83C\uDF38", "");
        replacements.put("勿信视频内广告", "");
        replacements.put("(", "");
        replacements.put(")", "");
        replacements.put("点击换线路", "");
        replacements.put("播放列表", "");
        replacements.put("天天线路", "");
        replacements.put("open", "");
        replacements.put("relay", "海茹专线");
        replacements.put("hhm3u8", "海茹专线1");
        replacements.put("jinyingm3u8", "海茹专线2");
        replacements.put("bfzym3u8", "海茹专线3");
        replacements.put("p2paa", "海茹荐片");
        replacements.put("360zy", "海茹专线5");
        replacements.put("snm3u8", "海茹专线6");
        replacements.put("\uD83D\uDE80防失联备用公众号【叨观荐影】", "默认");

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            name = name.replace(entry.getKey(), entry.getValue());
        }
        return name.trim();
    }

    private String formatFlagName(String name) {
        if (name.contains("关注")) {
            return "海茹专线";
        }
        return name;
    }

    @Override
    protected void convert(BaseViewHolder helper, VodInfo.VodSeriesFlag item) {
        TextView tvSeries = helper.getView(R.id.tvSeriesFlag);
        LinearLayout tvSeriesLayout = helper.getView(R.id.tvSeriesd);
        View selectView = helper.getView(R.id.tvSeriesFlagSelect);
        if (item.name.contains("-")) {
            selectView.setVisibility(View.GONE);
            tvSeries.setTextSize(20);
            tvSeriesLayout.setPadding(10,1,10,4);
        }
        selectView.setVisibility(item.selected && !item.name.contains("-") ? View.VISIBLE : View.GONE);
        tvSeries.setTextColor(item.selected ?  mContext.getResources().getColor(R.color.color_FFF68E33) : mContext.getResources().getColor(R.color.color_80FFFFFF));
        selectView.setBackgroundColor(getColorByAttrId(context, R.attr.color_theme));
        item.name = formatFlagName(item.name);
        helper.setText(R.id.tvSeriesFlag, cleanFlagName(item.name));
        drawableLinearLayout(tvSeriesLayout, 3);
    }
}