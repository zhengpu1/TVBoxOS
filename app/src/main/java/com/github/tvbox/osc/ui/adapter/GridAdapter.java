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
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.ImgUtils;
import com.orhanobut.hawk.Hawk;

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
            String pic = item.pic == null ? "" : item.pic.trim();
            if (!TextUtils.isEmpty(pic)) {
                if (ImgUtils.isBase64Image(pic)) {
                    ivThumb.setImageBitmap(ImgUtils.decodeBase64ToBitmap(pic));
                } else {
                    ImgUtils.load(pic, ivThumb, AutoSizeUtils.mm2px(mContext, 10), AutoSizeUtils.mm2px(mContext, 240), AutoSizeUtils.mm2px(mContext, 336), item.name);
                }
            } else {
                ivThumb.setImageDrawable(ImgUtils.createTextDrawable(item.name));
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
            String pic = item.picthumb == null ? "" : item.picthumb.trim();
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