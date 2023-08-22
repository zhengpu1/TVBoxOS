package com.github.tvbox.osc.ui.dialog;

import static com.github.tvbox.osc.base.BaseActivity.drawableLinearLayout;
import static com.github.tvbox.osc.base.BaseActivity.drawableTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;

import org.jetbrains.annotations.NotNull;

public class TipDialog extends BaseDialog {

    public TipDialog(@NonNull @NotNull Context context, String tip, String left, String right, OnListener listener) {
        super(context);
        setContentView(R.layout.dialog_tip);
        setCanceledOnTouchOutside(false);
        TextView tipInfo = findViewById(R.id.tipInfo);
        TextView leftBtn = findViewById(R.id.leftBtn);
        TextView rightBtn = findViewById(R.id.rightBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout dongbg = findViewById(R.id.lltipInfo);
        tipInfo.setText(tip);
        leftBtn.setText(left);
        rightBtn.setText(right);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.left();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.right();
            }
        });
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                listener.cancel();
            }
        });
        LinearLayout[] strl = {dongbg};
        drawableLinearLayout(strl, 4);
        TextView[] str = {leftBtn, rightBtn};
        drawableTextView(str, 1);
    }

    public interface OnListener {
        void left();

        void right();

        void cancel();
    }
}
