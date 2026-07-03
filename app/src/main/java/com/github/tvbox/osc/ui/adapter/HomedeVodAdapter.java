package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.drawableFrameLayout;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.util.ImgUtils;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomedeVodAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {

    public HomedeVodAdapter() {
        super(R.layout.item_detail_vod, new ArrayList<>());
    }
    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        TextView tvNote = helper.getView(R.id.tvNote);
        if (item.note == null || item.note.isEmpty()) {
            tvNote.setVisibility(View.GONE);
        } else {
            tvNote.setText(item.note);
            tvNote.setVisibility(View.VISIBLE);
        }
        TextView tvIsend = helper.getView(R.id.tvIsend);
        if (item.isend == 2) {
            tvIsend.setText("VIP");
            tvIsend.setVisibility(View.VISIBLE);
        } else {
            tvIsend.setVisibility(View.GONE);
        }
        helper.setText(R.id.tvName, item.name);
        ImageView ivThumb = helper.getView(R.id.ivThumb);
        FrameLayout tvHotvod = helper.getView(R.id.Hotvod);
        FrameLayout[] str = { tvHotvod };
        drawableFrameLayout(str, 9);
        // 先 trim 再判断！顺序必须反过来！
        String pic = item.picthumb == null ? "" : item.picthumb.trim();
        if (!TextUtils.isEmpty(pic)) {
            if (ImgUtils.isBase64Image(pic)) {
                ivThumb.setImageBitmap(ImgUtils.decodeBase64ToBitmap(pic));
            } else {
                ImgUtils.load(pic, ivThumb, AutoSizeUtils.mm2px(mContext, 5), AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 180), item.name);
            }
        } else {
            ivThumb.setImageDrawable(ImgUtils.createTextDrawable(item.name));
        }
    }
}