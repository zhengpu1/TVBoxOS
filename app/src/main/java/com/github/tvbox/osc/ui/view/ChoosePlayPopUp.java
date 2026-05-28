package com.github.tvbox.osc.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ScreenUtils;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.cache.RoomDataManger;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.ui.activity.PlayActivity;
import com.github.tvbox.osc.ui.activity.PlayEvent;
import com.github.tvbox.osc.ui.adapter.SeriesAdapter;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class ChoosePlayPopUp extends BasePopupWindow {
    private SeriesAdapter seriesAdapter;
    private TvRecyclerView mGridView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    public ChoosePlayPopUp(Context context, int gg) {
        super(context);
        setContentView(R.layout.choose_play_layout);
        mGridView = findViewById(R.id.mGridView);
        seriesAdapter = new SeriesAdapter();
        mGridView.setHasFixedSize(true);
       //
        if (Build.VERSION.SDK_INT <= 16) {
            mGridView.setLayoutManager(new V7LinearLayoutManager(getContext(), 0, false));
        } else {
            mGridView.setLayoutManager(new V7GridLayoutManager(context, 10));
        }
        mGridView.setAdapter(seriesAdapter);
        mGridView.setFocusable(true);
        mGridView.setFocusableInTouchMode(true);
        mGridView.requestFocus();

        VodInfo vodInfo = App.getInstance().getVodInfo();
        final List<VodInfo.VodSeries> list = vodInfo != null && vodInfo.seriesMap != null
                ? vodInfo.seriesMap.get(vodInfo.playFlag)
                : new ArrayList<>();

        seriesAdapter.setOnItemClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            if (getContext() instanceof PlayActivity) {
                getContext().finish();
            }

            if (list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {
                    seriesAdapter.getData().get(j).selected = false;
                    seriesAdapter.notifyItemChanged(j);
                }
                if (vodInfo.playIndex != position) {
                    seriesAdapter.getData().get(position).selected = true;
                    seriesAdapter.notifyItemChanged(position);
                    vodInfo.playIndex = position;
                }
                seriesAdapter.getData().get(vodInfo.playIndex).selected = true;
                seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                insertVod(vodInfo);
                EventBus.getDefault().post(new PlayEvent(vodInfo));
                dismiss();
            }
        });

        setBackgroundColor(Color.TRANSPARENT);
        Paint pFont = new Paint();
        Rect rect = new Rect();
        int w = 1;
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).name;
            pFont.getTextBounds(name, 0, name.length(), rect);
            if (w < rect.width()) {
                w = rect.width();
            }
        }
        w += 32;
        int screenWidth = ScreenUtils.getScreenWidth() / 3;
        int offset = screenWidth / w;
        if (offset <= 1) offset = 1;
        if (offset >= 9) offset = 10;
        if (Build.VERSION.SDK_INT > 16) ((V7GridLayoutManager) mGridView.getLayoutManager()).setSpanCount(offset);
        seriesAdapter.setNewData(vodInfo != null && vodInfo.seriesMap != null ? vodInfo.seriesMap.get(vodInfo.playFlag) : null);
        mHandler.postDelayed(() -> {
            RecyclerView.LayoutManager layoutManager = mGridView.getLayoutManager();
            if (layoutManager != null) {
                int childCount = layoutManager.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = layoutManager.getChildAt(i);
                    if (child != null) {
                        child.setOnFocusChangeListener((v, hasFocus) -> {
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.postDelayed(dismissRunnable, 4000);
                        });
                    }
                }
            }
        }, 500);

        mGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mHandler.removeCallbacks(dismissRunnable);
                mHandler.postDelayed(dismissRunnable, 4000);
            }
        });

        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mHandler.removeCallbacks(dismissRunnable);
                mHandler.postDelayed(dismissRunnable, 4000);
                return false;
            }
        });

        mGridView.scrollToPositionWithOffset(vodInfo != null ? vodInfo.playIndex : 0, 0, true);
    }

    @Override
    public void showPopupWindow(View anchorView) {
        super.showPopupWindow(anchorView);
        getPopupWindow().setFocusable(true);
        mHandler.postDelayed(dismissRunnable, 4000);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void insertVod(VodInfo vodInfo) {
        try {
            vodInfo.playNote = vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).name;
        } catch (Throwable th) {
            vodInfo.playNote = "";
        }
        RoomDataManger.insertVodRecord(vodInfo.sourceKey, vodInfo);
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_HISTORY_REFRESH));
    }

    private final Runnable dismissRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };
} 