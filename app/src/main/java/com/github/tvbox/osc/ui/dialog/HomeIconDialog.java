package com.github.tvbox.osc.ui.dialog;

import static com.github.tvbox.osc.base.BaseActivity.drawableLinearLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;

/**
 * 描述
 *
 * @author pj567
 * @since 2020/12/27
 */
public class HomeIconDialog extends BaseDialog {
    private final TextView tvHomeSearch;
    private final TextView tvHomeMenu;
    private final TextView tvHistory;
    private final TextView tvHomeLive;
    private final TextView tvHomePush;
    private final TextView tvHomeDrive;
    private final TextView tvHomeCollect;
    private final LinearLayout tvelcon;

    @SuppressLint("MissingInflatedId")
    public HomeIconDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_homeoption);
        setCanceledOnTouchOutside(false);
        tvHomeSearch = findViewById(R.id.tvHomeSearch);
        tvHomeSearch.setText(Hawk.get(HawkConfig.HOME_SEARCH_POSITION, true) ? "图标" : "布局");
        tvHomeMenu = findViewById(R.id.tvHomeMenu);
        tvHomeMenu.setText(Hawk.get(HawkConfig.HOME_MENU_POSITION, true) ? "图标" : "布局");
        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setText(Hawk.get(HawkConfig.HOME_HISTORY_POSITION, true) ? "图标" : "布局");
        tvHomeLive = findViewById(R.id.tvHomeLive);
        tvHomeLive.setText(Hawk.get(HawkConfig.HOME_LIVE_POSITION, true) ? "图标" : "布局");
        tvHomePush = findViewById(R.id.tvHomePush);
        tvHomePush.setText(Hawk.get(HawkConfig.HOME_PUSH_POSITION, true) ? "图标" : "布局");
        tvHomeDrive = findViewById(R.id.tvHomeDrive);
        tvHomeDrive.setText(Hawk.get(HawkConfig.HOME_DRIVE_POSITION, true) ? "图标" : "布局");
        tvHomeCollect = findViewById(R.id.tvHomeCollect);
        tvHomeCollect.setText(Hawk.get(HawkConfig.HOME_COLLECT_POSITION, true) ? "图标" : "布局");
        tvelcon = findViewById(R.id.elcon);
        LinearLayout[] str = {findViewById(R.id.llSearch), findViewById(R.id.llMenu), findViewById(R.id.llHistory), findViewById(R.id.llLive), findViewById(R.id.llPush),
                findViewById(R.id.llSDrive), findViewById(R.id.llCollect)};
        LinearLayout[] stro = {tvelcon};
        drawableLinearLayout(str, 1);
        drawableLinearLayout(stro, 4);
        findViewById(R.id.llSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_SEARCH_POSITION, !Hawk.get(HawkConfig.HOME_SEARCH_POSITION, true));
                tvHomeSearch.setText(Hawk.get(HawkConfig.HOME_SEARCH_POSITION, true) ? "图标" : "布局");
            }
        });
        findViewById(R.id.llMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_MENU_POSITION, !Hawk.get(HawkConfig.HOME_MENU_POSITION, true));
                tvHomeMenu.setText(Hawk.get(HawkConfig.HOME_MENU_POSITION, true) ? "图标" : "布局");
            }
        });
        findViewById(R.id.llHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_HISTORY_POSITION, !Hawk.get(HawkConfig.HOME_HISTORY_POSITION, true));
                tvHistory.setText(Hawk.get(HawkConfig.HOME_HISTORY_POSITION, true) ? "图标" : "布局");
            }
        });
        findViewById(R.id.llLive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_LIVE_POSITION, !Hawk.get(HawkConfig.HOME_LIVE_POSITION, true));
                tvHomeLive.setText(Hawk.get(HawkConfig.HOME_LIVE_POSITION, true) ? "图标" : "布局");
            }
        });
        findViewById(R.id.llPush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_PUSH_POSITION, !Hawk.get(HawkConfig.HOME_PUSH_POSITION, true));
                tvHomePush.setText(Hawk.get(HawkConfig.HOME_PUSH_POSITION, true) ? "图标" : "布局");
            }
        });
        findViewById(R.id.llSDrive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_DRIVE_POSITION, !Hawk.get(HawkConfig.HOME_DRIVE_POSITION, true));
                tvHomeDrive.setText(Hawk.get(HawkConfig.HOME_DRIVE_POSITION, true) ? "图标" : "布局");
            }
        });
        findViewById(R.id.llCollect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.HOME_COLLECT_POSITION, !Hawk.get(HawkConfig.HOME_COLLECT_POSITION, true));
                tvHomeCollect.setText(Hawk.get(HawkConfig.HOME_COLLECT_POSITION, true) ? "图标" : "布局");
            }
        });
    }

}