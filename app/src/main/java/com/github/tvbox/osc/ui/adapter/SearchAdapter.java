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
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.picasso.RoundTransformation;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.ImgUtil;
import com.github.tvbox.osc.util.MD5;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class SearchAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    public SearchAdapter() {
        super(Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0 ? R.layout.item_quick_search_lite : R.layout.item_search, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        // lite
        if (Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0) {
            helper.setText(R.id.tvName, item.name);
            helper.setText(R.id.tvSite, ApiConfig.get().getSource(item.sourceKey).getName());
            helper.setVisible(R.id.tvNote, item.note != null && !item.note.isEmpty());
            if (item.note != null && !item.note.isEmpty()) {
                helper.setText(R.id.tvNote, item.note);
            }
            TextView tvTyName = helper.getView(R.id.tvTyName);
            if (item.tyid > 0) {
                if (item.tyid == 1) {
                    tvTyName.setText("电影");
                } else if (item.tyid == 2) {
                    tvTyName.setText("电视剧");
                } else if (item.tyid == 3) {
                    tvTyName.setText("综艺");
                } else {
                    tvTyName.setText("动漫");
                }
                tvTyName.setVisibility(View.VISIBLE);
            } else {
                tvTyName.setVisibility(View.GONE);
            }
            FrameLayout tvquick = helper.getView(R.id.tvQuick);
            FrameLayout[] str = {tvquick};
            drawableFrameLayout(str, 1);
        } else {// with preview
            helper.setText(R.id.tvName, item.name);
            helper.setText(R.id.tvSite, ApiConfig.get().getSource(item.sourceKey).getName());
            helper.setVisible(R.id.tvNote, item.note != null && !item.note.isEmpty());
            if (item.note != null && !item.note.isEmpty()) {
                helper.setText(R.id.tvNote, item.note);
            }
            ImageView ivThumb = helper.getView(R.id.ivThumb);
            TextView tvSitez = helper.getView(R.id.tvSite);
            tvSitez.setBackgroundColor(getColorByAttrId(mContext, R.attr.color_theme_80));
            FrameLayout llgrid = helper.getView(R.id.llgrid);
            FrameLayout llgrids = helper.getView(R.id.llgrids);
            FrameLayout[] str = { llgrid };
            drawableFrameLayout(str, 9);
            if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 0) {
                LParams(llgrid, 210, 280);
                LParams(llgrids, 210,315);
            } else {
                LParams(llgrid, 160, 214);
                LParams(llgrids, 160,249);
            }
            //File -> Settings -> Build, Execution, Deployment -> Build Tools 展开 -> Gradle -> Gradle user home
            if (!TextUtils.isEmpty(item.pic)) {
                Picasso.get()
                        .load(item.pic)
                        .transform(new RoundTransformation(MD5.string2MD5(item.pic))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, 6), RoundTransformation.RoundType.ALL))
                        .placeholder(ImgUtil.createTextDrawable(item.name))
                        .noFade()
                        .error(R.drawable.img_loading_placeholder)
                        .into(ivThumb);
            } else {
                 ivThumb.setImageDrawable(ImgUtil.createTextDrawable(item.name));
            }
        }
    }
}