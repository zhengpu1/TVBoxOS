package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.LParams;
import static com.github.tvbox.osc.base.baseActivity.drawableFrameLayout;
import static com.github.tvbox.osc.base.baseActivity.getColorByAttrId;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.cache.VodCollect;
import com.github.tvbox.osc.picasso.RoundTransformation;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.MD5;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class CollectAdapter extends BaseQuickAdapter<VodCollect, BaseViewHolder> {
    public CollectAdapter() {
        super(R.layout.item_grid, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, VodCollect item) {
        FrameLayout tvDel = helper.getView(R.id.delFrameLayout);
        if (HawkConfig.hotVodDelete) {
            tvDel.setVisibility(View.VISIBLE);
        } else {
            tvDel.setVisibility(View.GONE);
        }
        helper.setVisible(R.id.tvLang, false);
        helper.setVisible(R.id.tvIsend, false);
        helper.setVisible(R.id.tvArea, false);
        helper.setVisible(R.id.tvNote, false);
        TextView tvYear = helper.getView(R.id.tvYear);
        if (ApiConfig.get().getSource(item.sourceKey)!=null) {
            tvYear.setText(ApiConfig.get().getSource(item.sourceKey).getName());
            tvYear.setVisibility(View.VISIBLE);
            tvYear.setBackgroundColor(getColorByAttrId(mContext, R.attr.color_theme_80));
        } else {
            tvYear.setVisibility(View.GONE);
        }
        helper.setText(R.id.tvName, item.name);
        ImageView ivThumb = helper.getView(R.id.ivThumb);
        FrameLayout llgrid = helper.getView(R.id.llgrid);
        FrameLayout llgrids = helper.getView(R.id.llgrids);
        FrameLayout[] str = { llgrid };
        drawableFrameLayout(str, 9);
        llgrids.setVisibility(View.VISIBLE);
        if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 0) {
            LParams(llgrid, 235, 310);
            LParams(llgrids,235, 345);
        } else if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 1) {
            LParams(llgrid, 170, 220);
            LParams(llgrids, 170,255);
        } else {
            LParams(llgrid, 195, 260);
            LParams(llgrids, 195,295);
        }
        //由于部分电视机使用glide报错
        if (!TextUtils.isEmpty(item.pic)) {
                Picasso.get()
                        .load(DefaultConfig.checkReplaceProxy(item.pic))
                        .transform(new RoundTransformation(MD5.string2MD5(item.pic + "position=" + helper.getLayoutPosition()))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, 6), RoundTransformation.RoundType.ALL))
                        .placeholder(R.drawable.img_loading_placeholder)
                        .noFade()
                        .error(R.drawable.img_loading_placeholder)
                        .into(ivThumb);
            } else {
               ivThumb.setImageResource(R.drawable.img_loading_placeholder);
            }
    }
}