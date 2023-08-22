package com.github.tvbox.osc.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.DefaultConfig;

import org.jetbrains.annotations.NotNull;

public class AboutDialog extends BaseDialog {
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public AboutDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_about);
        TextView tvVersions = findViewById(R.id.tvVersions);
        tvVersions.setText("当前版本：HRTV" + DefaultConfig.getAppVersionName(context));

    }
}