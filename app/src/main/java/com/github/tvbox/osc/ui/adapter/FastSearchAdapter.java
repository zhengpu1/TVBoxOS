package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.BaseActivity.LParams;
import static com.github.tvbox.osc.base.BaseActivity.drawableFrameLayout;
import static com.github.tvbox.osc.base.BaseActivity.getColorByAttrId;

import android.text.TextUtils;
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
import com.github.tvbox.osc.util.MD5;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class FastSearchAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    public FastSearchAdapter() {
        super(R.layout.item_search, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
//        LOG.i("PPPPP",wBpic);
//        if(item.pic.contains("gtimg.com") || !item.pic.startsWith("http")){
//            if(wBpic != null)item.pic = wBpic;
//        }
        // with preview
        helper.setText(R.id.tvName, item.name.replace("[资料夹]","").replace("[影片]",""));
        helper.setText(R.id.tvSite, ApiConfig.get().getSource(item.sourceKey).getName());
        helper.setVisible(R.id.tvNote, item.note != null && !item.note.isEmpty());
        if (item.note != null && !item.note.isEmpty()) {
            helper.setText(R.id.tvNote, item.note);
        }
        TextView tvSitez = helper.getView(R.id.tvSite);
        tvSitez.setBackgroundColor(getColorByAttrId(mContext, R.attr.color_theme_80));
        ImageView ivThumb = helper.getView(R.id.ivThumb);
        FrameLayout llgrid = helper.getView(R.id.llgrid);
        FrameLayout llgrids = helper.getView(R.id.llgrids);
        FrameLayout[] str = { llgrid };
        drawableFrameLayout(str, 9);
        if (Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 1) {
            LParams(llgrid, 170, 220);
            LParams(llgrids, 170,255);
        } else {
            LParams(llgrid, 200, 260);
            LParams(llgrids, 200,295);
        }
        if (!TextUtils.isEmpty(item.pic)) {
            Picasso.get()
                    .load(item.pic)
                    .transform(new RoundTransformation(MD5.string2MD5(item.pic))
                            .centerCorp(true)
                            .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 350))
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