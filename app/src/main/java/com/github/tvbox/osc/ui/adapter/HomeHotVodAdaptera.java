package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.LParams;
import static com.github.tvbox.osc.base.baseActivity.drawableFrameLayout;

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
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.ImgUtils;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomeHotVodAdaptera extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {

    public HomeHotVodAdaptera() {
        super(R.layout.item_user_hot_vod2, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        TextView tvRate = helper.getView(R.id.tvRate);
        if (Hawk.get(HawkConfig.HOME_REC, 0) == 2){
            tvRate.setText(ApiConfig.get().getSource(item.sourceKey).getName());
        }else if(Hawk.get(HawkConfig.HOME_REC, 0) == 0){
            tvRate.setText("豆瓣热播");
        }else {
            tvRate.setVisibility(View.GONE);
        }

       TextView tvNote = helper.getView(R.id.tvNote);
        if (item.note == null || item.note.isEmpty()) {
            tvNote.setVisibility(View.GONE);
        } else {
            tvNote.setText(item.note);
            tvNote.setVisibility(View.VISIBLE);
            tvNote.setTextSize(14);
        }
        TextView tvIsend = helper.getView(R.id.tvIsend);
        if (item.isend == 2) {
            tvIsend.setText("VIP");
            tvIsend.setVisibility(View.VISIBLE);
        } else {
            tvIsend.setVisibility(View.GONE);
        }
       TextView tvArea = helper.getView(R.id.tvArea);
        if (TextUtils.isEmpty(item.area)) {
            tvArea.setVisibility(View.GONE);
        } else {
            tvArea.setText(item.area);
            tvArea.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tvName, item.name);
        ImageView ivThumb = helper.getView(R.id.ivThumb);
        FrameLayout tvHotvods = helper.getView(R.id.Hotvods);
        FrameLayout[] str = { tvHotvods };

        if (TextUtils.isEmpty(item.picthumb)) {
            drawableFrameLayout(str, 10);
            LParams(tvHotvods, 197, 260);
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
            drawableFrameLayout(str, 10);
            LParams(tvHotvods, 242, 155);
            String pic = item.pic == null ? "" : item.pic.trim();
            if (!TextUtils.isEmpty(pic)) {
                if (ImgUtils.isBase64Image(pic)) {
                    ivThumb.setImageBitmap(ImgUtils.decodeBase64ToBitmap(pic));
                } else {
                    ImgUtils.load(pic, ivThumb, AutoSizeUtils.mm2px(mContext, 8), AutoSizeUtils.mm2px(mContext, 180), AutoSizeUtils.mm2px(mContext, 180), item.name);
                }
            } else {
                ivThumb.setImageDrawable(ImgUtils.createTextDrawable(item.name));
            }
        }
    }
}