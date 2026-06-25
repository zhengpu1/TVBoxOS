package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.baseActivity.LParams;
import static com.github.tvbox.osc.base.baseActivity.drawableFrameLayout;
import static com.github.tvbox.osc.base.baseActivity.getColorByAttrId;
import static com.github.tvbox.osc.base.baseActivity.roundRadius;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.picasso.RoundTransformation;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.ImgUtil;
import com.github.tvbox.osc.util.MD5;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class GridAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    private boolean mShowList = false;
    private int width, height;

    public GridAdapter(boolean l) {
        super( l ? R.layout.item_list : R.layout.item_grid, new ArrayList<>());
        this.mShowList = l;
    }
    public static final List<String> SUBM = Arrays.asList("mp4", "mkv", "rmvb", "ts");
    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        if(this.mShowList) {
            FrameLayout tvquick = helper.getView(R.id.tvQuickk);
            FrameLayout[] str = {tvquick};
            drawableFrameLayout(str, 1);
            helper.setText(R.id.tvNote, item.note);
            helper.setText(R.id.tvName, item.name);
            TextView tvLang = helper.getView(R.id.tvLang);
            if (TextUtils.isEmpty(item.lang)) {
                tvLang.setVisibility(View.GONE);
             } else {
                tvLang.setText(item.lang);
                tvLang.setVisibility(View.VISIBLE);
            }

            ImageView ivThumb = helper.getView(R.id.ivThumb);
            TextView txtMediaName = helper.getView(R.id.txtMediaName);
            txtMediaName.setVisibility(View.GONE);
           String name = null;
            if(item.name.contains("."))name = item.name.substring(item.name.lastIndexOf(".") + 1);
            if(SUBM.contains(name)) {
                txtMediaName.setText(name);
                txtMediaName.setVisibility(View.VISIBLE);
                txtMediaName.setBackgroundColor(getColorByAttrId(mContext, R.attr.color_theme_70));
            }
                //由于部分电视机使用glide报错
                if (!TextUtils.isEmpty(item.pic)) {
                    item.pic = item.pic.trim();
                    if (ImgUtil.isBase64Image(item.pic)) {
                        // 如果是 Base64 图片，解码并设置
                        ivThumb.setImageBitmap(ImgUtil.decodeBase64ToBitmap(item.pic));
                    } else {
                        Picasso.get()
                                .load(DefaultConfig.checkReplaceProxy(item.pic))
                                .transform(new RoundTransformation(MD5.string2MD5(item.pic))
                                        .centerCorp(true)
                                        .override(AutoSizeUtils.mm2px(mContext, 180), AutoSizeUtils.mm2px(mContext, 120))
                                        .roundRadius(AutoSizeUtils.mm2px(mContext, roundRadius), RoundTransformation.RoundType.ALL))
                                .placeholder(ImgUtil.decodeBase64ToDrawable(ImgUtil.FOLDER))
                                .noFade()
                                .error(ImgUtil.createTextDrawable(item.name))
                                .into(ivThumb);
                    }
                } else {
                    ivThumb.setImageDrawable(ImgUtil.createTextDrawable(item.name));
                }
            return;
        }

        TextView tvIsend = helper.getView(R.id.tvIsend);
        if (item.isend == 2) {
            tvIsend.setText("VIP");
            tvIsend.setVisibility(View.VISIBLE);
        } else {
            tvIsend.setVisibility(View.GONE);
        }
        TextView tvLang = helper.getView(R.id.tvLang);
        tvLang.setVisibility(View.GONE);
        helper.setVisible(R.id.tvYear, false);
        helper.setText(R.id.tvName, item.name);
        helper.setText(R.id.tvActor, item.actor);
        ImageView ivThumb = helper.getView(R.id.ivThumb);
        FrameLayout llgrid = helper.getView(R.id.llgrid);
        FrameLayout llgrids = helper.getView(R.id.llgrids);
        FrameLayout[] str = { llgrid };
        drawableFrameLayout(str, 9);
        if(Hawk.get(HawkConfig.PIC_SIZE, false)) {
            helper.setText(R.id.tvNote, item.name);
            TextView tvNote = helper.getView(R.id.tvNote);
            TextView tvArea = helper.getView(R.id.tvArea);
            if (TextUtils.isEmpty(item.note)) {
                helper.setVisible(R.id.tvArea, false);
            } else {
                helper.setVisible(R.id.tvArea, true);
                helper.setText(R.id.tvArea, item.note);
            }
            llgrids.setVisibility(View.GONE);
            if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 1) {
                LParams(llgrid, 242, 155);
                tvNote.setTextSize(16);
                tvArea.setTextSize(12);
                tvArea.setPadding(5,12,5,0);
            } else {
                LParams(llgrid, 302, 170);
                tvNote.setTextSize(18);
                tvArea.setTextSize(14);
                tvArea.setPadding(5,15,5,0);
            }
            if (!TextUtils.isEmpty(item.picthumb)) {
                item.picthumb=item.picthumb.trim();
                Picasso.get()
                        .load(DefaultConfig.checkReplaceProxy(item.picthumb))
                        .transform(new RoundTransformation(MD5.string2MD5(item.picthumb))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, 6), RoundTransformation.RoundType.ALL))
                        .placeholder(ImgUtil.createTextDrawable(item.name))
                        .noFade()
                        .into(ivThumb);
            } else {
                 ivThumb.setImageDrawable(ImgUtil.createTextDrawable(item.name));
            }
        } else {
            if (TextUtils.isEmpty(item.note)) {
                helper.setVisible(R.id.tvNote, false);
            } else {
                helper.setVisible(R.id.tvNote, true);
                helper.setText(R.id.tvNote, item.note);
            }
            if (TextUtils.isEmpty(item.area)) {
                helper.setVisible(R.id.tvArea, false);
            } else {
                helper.setVisible(R.id.tvArea, true);
                helper.setText(R.id.tvArea, item.area);
            }
            llgrids.setVisibility(View.VISIBLE);
            if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 0) {
                LParams(llgrid, 232, 310);
                LParams(llgrids, 232,345);
            } else if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 1) {
                LParams(llgrid, 168, 220);
                LParams(llgrids, 168,255);
            } else {
                LParams(llgrid, 197, 260);
                LParams(llgrids, 197,295);
            }
            if (!TextUtils.isEmpty(item.pic)) {
                item.pic=item.pic.trim();
                Picasso.get()
                        .load(DefaultConfig.checkReplaceProxy(item.pic))
                        .transform(new RoundTransformation(MD5.string2MD5(item.pic))
                                .centerCorp(true)
                                .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                                .roundRadius(AutoSizeUtils.mm2px(mContext, 6), RoundTransformation.RoundType.ALL))
                        .placeholder(ImgUtil.createTextDrawable(item.name))
                        .noFade()
                        .into(ivThumb);
            } else {
                ivThumb.setImageDrawable(ImgUtil.createTextDrawable(item.name));
            }
        }
    }
}