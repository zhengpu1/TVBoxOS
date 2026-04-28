package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.roundRadius;

import android.text.TextUtils;
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
import com.github.tvbox.osc.util.ImgUtil;
import com.github.tvbox.osc.util.MD5;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class FastSearchAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    public FastSearchAdapter() {
        super(Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0 ? R.layout.item_list : R.layout.item_search, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        if (Hawk.get(HawkConfig.SEARCH_VIEW, 0) == 0) {
            helper.setText(R.id.tvNote, item.note);
            helper.setText(R.id.tvName, item.name);
            ImageView ivThumb = helper.getView(R.id.ivThumb);
            //由于部分电视机使用glide报错
            if (!TextUtils.isEmpty(item.pic)) {
                item.pic=item.pic.trim();
                if(ImgUtil.isBase64Image(item.pic)){
                    // 如果是 Base64 图片，解码并设置
                    ivThumb.setImageBitmap(ImgUtil.decodeBase64ToBitmap(item.pic));
                }else {
                    Picasso.get()
                            .load(DefaultConfig.checkReplaceProxy(item.pic))
                            .transform(new RoundTransformation(MD5.string2MD5(item.pic))
                                    .centerCorp(true)
                                    .override(AutoSizeUtils.mm2px(mContext, ImgUtil.defaultWidth), AutoSizeUtils.mm2px(mContext, ImgUtil.defaultHeight))
                                    .roundRadius(AutoSizeUtils.mm2px(mContext, roundRadius), RoundTransformation.RoundType.ALL))
                            .placeholder(ImgUtil.decodeBase64ToDrawable(ImgUtil.FOLDER))
                            .noFade()
                            .error(ImgUtil.createTextDrawable(item.name))
                            .into(ivThumb);
                }
            } else {
//                ivThumb.setImageResource(R.drawable.img_loading_placeholder);
                ivThumb.setImageDrawable(ImgUtil.createTextDrawable(item.name));
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

            if (!TextUtils.isEmpty(item.pic)) {
                Picasso.get()
                        .load(item.pic)
                        .transform(new RoundTransformation(MD5.string2MD5(item.pic))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, ImgUtil.defaultWidth), AutoSizeUtils.mm2px(mContext, ImgUtil.defaultHeight))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, roundRadius), RoundTransformation.RoundType.ALL))
                        .placeholder(ImgUtil.decodeBase64ToDrawable(ImgUtil.FOLDER))
                        .noFade()
                        .error(R.drawable.img_loading_placeholder)
                        .into(ivThumb);
            } else {
                ivThumb.setImageResource(R.drawable.img_loading_placeholder);
            }

        }
    }
}