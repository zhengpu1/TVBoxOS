package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.drawableFrameLayout;

import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.ImgUtils;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class FastSearchAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    public FastSearchAdapter() {
        super(Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0 ? R.layout.item_list : R.layout.item_search, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        if (Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0) {
            FrameLayout tvquick = helper.getView(R.id.tvQuickk);
            FrameLayout[] str = {tvquick};
            drawableFrameLayout(str, 1);
            helper.setText(R.id.tvNote, item.note);
            helper.setText(R.id.tvName, item.name);
            ImageView ivThumb = helper.getView(R.id.ivThumb);
            String pic = item.pic == null ? "" : item.pic.trim();
            if (!TextUtils.isEmpty(pic)) {
                if (ImgUtils.isBase64Image(pic)) {
                    ivThumb.setImageBitmap(ImgUtils.decodeBase64ToBitmap(pic));
                } else {
                    ImgUtils.load(pic, ivThumb, AutoSizeUtils.mm2px(mContext, 10), AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400), item.name);
                }
            } else {
                ivThumb.setImageDrawable(ImgUtils.createTextDrawable(item.name));
            }
        } else {
            helper.setText(R.id.tvName, item.name.replace("[资料夹]", "").replace("[影片]", ""));
            helper.setText(R.id.tvSite, ApiConfig.get().getSource(item.sourceKey).getName());
            helper.setVisible(R.id.tvNote, item.note != null && !item.note.isEmpty());
            if (item.note != null && !item.note.isEmpty()) {
                helper.setText(R.id.tvNote, item.note);
            }
            TextView tvSitez = helper.getView(R.id.tvSite);

            ImageView ivThumb = helper.getView(R.id.ivThumb);
            FrameLayout tvquickk = helper.getView(R.id.tvQuickk);
            FrameLayout[] str = {tvquickk};
            drawableFrameLayout(str, 1);
            String pic = item.pic == null ? "" : item.pic.trim();
            if (!TextUtils.isEmpty(pic)) {
                if (ImgUtils.isBase64Image(pic)) {
                    ivThumb.setImageBitmap(ImgUtils.decodeBase64ToBitmap(pic));
                } else {
                    ImgUtils.load(pic, ivThumb, AutoSizeUtils.mm2px(mContext, 10), AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400), item.name);
                }
            } else {
                ivThumb.setImageDrawable(ImgUtils.createTextDrawable(item.name));
            }

        }
    }
}