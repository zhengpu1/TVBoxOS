package com.github.tvbox.osc.ui.adapter;

import static com.github.tvbox.osc.base.BaseActivity.drawableTextView;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;

import java.util.ArrayList;

public class BackupAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public BackupAdapter() {
        super(R.layout.item_dialog_backup, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tvName, item);
        helper.addOnClickListener(R.id.tvName);
        TextView tvNamea = helper.getView(R.id.tvName);
        TextView[] str = {tvNamea};
        drawableTextView(str, 1);
    }
}