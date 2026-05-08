package com.github.tvbox.osc.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.core.view.ViewGroupKt;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.ScreenUtils;
import com.orhanobut.hawk.Hawk;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import razerdp.basepopup.BasePopupWindow;
import razerdp.util.animation.AnimationHelper;
import razerdp.util.animation.TranslationConfig;

@SuppressLint("ClickableViewAccessibility")
public class PlayerMoreFucPop extends BasePopupWindow {

    private TextView mIdmDownLoad;
    private TextView mIdmDownLoadk;
    private TextView mShowTime;
    private OnItemClickListener onClick;
    private TextView mScaleBtn;
    private TextView mTinyProgress;
    private TextView mAudioTrack;
    private TextView mLandscapePortraitBtn;
    private final DismissRunnable dismissRunnable;
    private final Handler mHandler;

    public PlayerMoreFucPop(Context context, JSONObject playConfig) {
        super(context);
        dismissRunnable = new DismissRunnable();
        mHandler = new Handler(Looper.getMainLooper());

        setContentView(R.layout.player_more_fuc_pop);
        setBackgroundColor(Color.TRANSPARENT);

        mScaleBtn = findViewById(R.id.play_scale);
        // mScaleBtn.setVisibility(View.GONE);
        mTinyProgress = findViewById(R.id.tiny_progress);
        mAudioTrack = findViewById(R.id.audio_track_select);
        mLandscapePortraitBtn = findViewById(R.id.landscape_portrait);
        mIdmDownLoad = findViewById(R.id.tvPtst);
        mIdmDownLoadk = findViewById(R.id.tvIjkCachePlay);
        // mIdmDownLoad.setVisibility(View.GONE);
        mShowTime = findViewById(R.id.time_show);

        mLandscapePortraitBtn.setOnClickListener(v -> setLandscapePortrait());

        if (playConfig != null) {
            try {
                mScaleBtn.setText(PlayerHelper.getScaleName(playConfig.getInt("sc")));
                mAudioTrack.setVisibility(playConfig.getInt("pl") == 1 ? View.VISIBLE : View.GONE);
                initLandscapePortraitBtnInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mScaleBtn.setOnClickListener(v -> {
        if (onClick != null) {
            onClick.onItemClick((TextView) v);
        }
    });

        // 迷你进度
        mTinyProgress.setText(Hawk.get(HawkConfig.HOME_PWET_TUMM, false) ? "迷你进度开" : "迷你进度关");
        mTinyProgress.setOnClickListener(v -> {
        if (onClick != null) {
            onClick.onItemClick((TextView) v);
        }
        Hawk.put(HawkConfig.HOME_PWET_TUMM, !Hawk.get(HawkConfig.HOME_PWET_TUMM, false));
    });

        mAudioTrack.setOnClickListener(v -> {
        if (onClick != null) {
            onClick.onItemClick((TextView) v);
        }
    });

        mIdmDownLoad.setOnClickListener(v -> {
        if (onClick != null) {
            onClick.onItemClick((TextView) v);
        }
    });

        mIdmDownLoadk.setOnClickListener(v -> {
        if (onClick != null) {
            onClick.onItemClick((TextView) v);
        }
    });

        // 屏显
        mShowTime.setText(Hawk.get(HawkConfig.VIDEO_SHOW_TIME, false) ? "屏显开" : "屏显关");
        mShowTime.setOnClickListener(v -> {
        if (onClick != null) {
            onClick.onItemClick((TextView) v);
        }
        boolean isOpen = Hawk.get(HawkConfig.VIDEO_SHOW_TIME, false);
        Hawk.put(HawkConfig.VIDEO_SHOW_TIME, !isOpen);
        mShowTime.setText(!isOpen ? "屏显开" : "屏显关");
    });

        initLandscapePortraitBtnInfo();

        View contentView = getContentView();
        if (contentView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) contentView;
            ViewGroupKt.forEach(group, child -> {
                child.setOnTouchListener((View v, MotionEvent event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mHandler.postDelayed(dismissRunnable, 3000);
                }
                return false;
            });
                return Unit.INSTANCE;
            });
        }
    }

    public PlayerMoreFucPop setOnItemClickListener(OnItemClickListener listener) {
        this.onClick = listener;
        return this;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationHelper.asAnimation().withTranslation(TranslationConfig.FROM_RIGHT).toShow();
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationHelper.asAnimation().withTranslation(TranslationConfig.TO_RIGHT).toDismiss();
    }

    private void initLandscapePortraitBtnInfo() {
        double screenSqrt = ScreenUtils.getSqrt(getContext());
        if (screenSqrt < 20.0) {
            mLandscapePortraitBtn.setVisibility(View.VISIBLE);
            mLandscapePortraitBtn.setText("竖屏");
        }
    }

    private void setLandscapePortrait() {
        int requestedOrientation = getContext().getResources().getConfiguration().orientation;
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
            requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE ||
            requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {

            mLandscapePortraitBtn.setText("横屏");
            getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        } else if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
            requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT ||
            requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {

            mLandscapePortraitBtn.setText("竖屏");
            getContext().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    private void setTimeShowOrDismiss(TextView timeShow) {
        boolean isTimeShowOpen = Hawk.get(HawkConfig.VIDEO_SHOW_TIME, false);
        if (isTimeShowOpen) {
            timeShow.setText("屏显开");
            timeShow.setTag(1);
        } else {
            timeShow.setText("屏显关");
            timeShow.setTag(2);
        }
    }

    @Override
    public boolean onDispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mHandler.removeCallbacksAndMessages(null);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            mHandler.postDelayed(dismissRunnable, 3000);
        }
        return super.onDispatchKeyEvent(event);
    }

    @Override
    public void onShowing() {
        super.onShowing();
        mHandler.postDelayed(this::dismiss, 3000);
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        mHandler.removeCallbacksAndMessages(null);
    }

    public interface OnItemClickListener {
        void onItemClick(TextView view);
    }

    class DismissRunnable implements Runnable {
        @Override
        public void run() {
            dismiss();
        }
    }
}