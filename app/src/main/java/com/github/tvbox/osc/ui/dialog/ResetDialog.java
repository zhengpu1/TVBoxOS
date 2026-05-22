package com.github.tvbox.osc.ui.dialog;

import static com.github.tvbox.osc.base.baseActivity.drawableTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.DefaultConfig;

import org.jetbrains.annotations.NotNull;

/**
 * 描述
 *
 * @author pj567
 * @since 2020/12/27
 */
public class ResetDialog extends BaseDialog {
    private final TextView tvYes;
    private final TextView tvNo;

    @SuppressLint("MissingInflatedId")
    public ResetDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_reset);
        setCanceledOnTouchOutside(true);
        tvYes = findViewById(R.id.btnConfirm);
        tvNo = findViewById(R.id.btnCancel);
        TextView[] str = { tvYes, tvNo};
        drawableTextView(str, 1);
        tvYes.setOnClickListener(v -> DefaultConfig.resetApp(tvYes.getContext()));
        tvNo.setOnClickListener(v -> ResetDialog.this.dismiss());
    }
}
