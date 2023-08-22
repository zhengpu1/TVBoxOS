package com.github.tvbox.osc.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.activity.DetailActivity;

import org.jetbrains.annotations.NotNull;

public class DevDialog extends BaseDialog {
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public DevDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_dev);
        TextView tvVersions = findViewById(R.id.tvDes);
        tvVersions.setText(DetailActivity.mVideo.des);
    }
}