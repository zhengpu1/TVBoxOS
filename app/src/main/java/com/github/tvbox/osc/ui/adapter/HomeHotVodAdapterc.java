package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.LParams;
import static com.github.tvbox.osc.base.baseActivity.drawableFrameLayout;
import static com.github.tvbox.osc.ui.fragment.UserFragment.tyssy;

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
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.MD5;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomeHotVodAdapterc extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {

    public HomeHotVodAdapterc() {
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
        drawableFrameLayout(str, 10);
        if (tyssy < 2){
            LParams(tvHotvods, 607, 260);
            if (!TextUtils.isEmpty(item.picslide)) { item.picthumb = item.picslide; }
            if (TextUtils.isEmpty(item.picthumb)) { item.picthumb = item.pic; }
            if (!TextUtils.isEmpty(item.picthumb)) {
                item.picthumb=item.picthumb.trim();
                Picasso.get()
                        .load(DefaultConfig.checkReplaceProxy(item.picthumb.replace("480_270","610_260")))
                        .transform(new RoundTransformation(MD5.string2MD5(item.picthumb.replace("480_270","610_260")))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, 610), AutoSizeUtils.mm2px(mContext, 260))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, 8), RoundTransformation.RoundType.ALL))
                        .placeholder(R.drawable.img_loading_placeholder)
                        .noFade()
                        .error(R.drawable.img_loading_placeholder)
                        .into(ivThumb);
            } else {
                ivThumb.setImageResource(R.drawable.img_loading_placeholder);
            }
        } else {
            LParams(tvHotvods, 298, 170);
            if (TextUtils.isEmpty(item.picthumb)) { item.picthumb = item.pic; }
            if (!TextUtils.isEmpty(item.picthumb)) {
                item.picthumb=item.picthumb.trim();
                Picasso.get()
                        .load(DefaultConfig.checkReplaceProxy(item.picthumb))
                        .transform(new RoundTransformation(MD5.string2MD5(item.picthumb))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 180))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, 8), RoundTransformation.RoundType.ALL))
                        .placeholder(R.drawable.img_loading_placeholder)
                        .noFade()
                        .error(R.drawable.img_loading_placeholder)
                        .into(ivThumb);
            } else {
                ivThumb.setImageResource(R.drawable.img_loading_placeholder);
            }
        }
    }
}