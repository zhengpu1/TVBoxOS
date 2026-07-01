package com.github.tvbox.osc.ui.activity;

import static com.github.tvbox.osc.ui.activity.DetailActivity.isReverse;
import static com.github.tvbox.osc.ui.activity.DetailActivity.isSortToggle;
import static com.github.tvbox.osc.ui.activity.DetailActivity.mCurrentPlayIndex;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;

import com.github.catvod.crawler.Spider;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.api.DanmakuApi;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.base.baseActivity;
import com.github.tvbox.osc.bean.ParseBean;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.bean.Subtitle;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.cache.CacheManager;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.player.IjkMediaPlayer;
import com.github.tvbox.osc.player.MyVideoView;
import com.github.tvbox.osc.player.TrackInfo;
import com.github.tvbox.osc.player.TrackInfoBean;
import com.github.tvbox.osc.player.controller.VodController;
import com.github.tvbox.osc.player.danmu.DanmuLoadController;
import com.github.tvbox.osc.player.danmu.Parser;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.dialog.DanmuSettingDialog;
import com.github.tvbox.osc.ui.dialog.SearchSubtitleDialog;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.SubtitleDialog;
import com.github.tvbox.osc.util.AdBlocker;
import com.github.tvbox.osc.util.DanmuHelper;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.VideoParseRuler;
import com.github.tvbox.osc.util.XWalkUtils;
import com.github.tvbox.osc.util.parser.SuperParse;
import com.github.tvbox.osc.util.thunder.Jianpian;
import com.github.tvbox.osc.util.thunder.Thunder;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.ui.widget.DanmakuView;
import me.jessyan.autosize.AutoSize;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import xyz.doikki.videoplayer.player.AbstractPlayer;
import xyz.doikki.videoplayer.player.ProgressManager;

public class PlayActivity extends baseActivity {
    private MyVideoView mVideoView;
    private TextView mPlayLoadTip;
    private ImageView mPlayLoadErr;
    private ProgressBar mPlayLoading;
    private VodController mController;
    private SourceViewModel sourceViewModel;
    private Handler mHandler;
    private String videoURL;
    private ExecutorService danmuExecutor;
    private DanmakuView mDanmuView;
    private DanmakuContext mDanmakuContext;
    private String danmuText;
    private final AtomicInteger danmuLoadSeq = new AtomicInteger();
    public static boolean forceReplayToStart = false;
    private DanmuLoadController danmuLoadController;
    @Override
    protected int getLayoutResID() {
        return R.layout.activity_play;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_SET_DANMU_SETTINGS) {
            setDanmuViewSettings(event.obj instanceof Boolean && (Boolean) event.obj);
        }
    }
    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        initView();
        initDanmuView();
        initViewModel();
        initData();
    }

    private void initDanmuView() {
        mDanmuView = findViewById(R.id.danmaku);
        mDanmakuContext = DanmakuContext.create();
        mVideoView.setDanmuView(mDanmuView);
        setDanmuViewSettings(false);
    }

    private void setDanmuViewSettings(boolean reload) {
        if (mDanmuView == null || mDanmakuContext == null) return;
        if (!DanmuHelper.isOpen()) {
            releaseDanmuView();
            if (mController != null) mController.setHasDanmu(!TextUtils.isEmpty(danmuText));
            return;
        }
        HashMap<Integer, Integer> maxLines = new HashMap<>();
        int maxLine = DanmuHelper.getMaxLine();
        maxLines.put(BaseDanmaku.TYPE_FIX_TOP, maxLine);
        maxLines.put(BaseDanmaku.TYPE_SCROLL_RL, maxLine);
        maxLines.put(BaseDanmaku.TYPE_SCROLL_LR, maxLine);
        maxLines.put(BaseDanmaku.TYPE_FIX_BOTTOM, maxLine);
        mDanmakuContext.setMaximumLines(maxLines)
                .setScrollSpeedFactor(DanmuHelper.getSpeed())
                .setDanmakuTransparency(DanmuHelper.getAlpha())
                .setScaleTextSize(DanmuHelper.getSizeScale());
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDanmakuMargin(8);
        if (reload && !TextUtils.isEmpty(danmuText) && DanmuHelper.isOpen()) {
            prepareDanmu(danmuText);
        }
    }

    private void checkDanmu(String danmu) {
        danmuText = TextUtils.isEmpty(danmu) ? "" : danmu.trim();
        releaseDanmuView();
        boolean hasDanmu = !TextUtils.isEmpty(danmuText);
        mController.setHasDanmu(hasDanmu);
        if (!hasDanmu || !DanmuHelper.isOpen()) {
            if (mDanmuView != null) mDanmuView.setVisibility(View.GONE);
            return;
        }
        if (mDanmuView != null) mDanmuView.setVisibility(View.VISIBLE);
        prepareDanmu(danmuText);
    }

    private void prepareDanmu(String danmu) {
        if (TextUtils.isEmpty(danmu)) return;
        int seq = danmuLoadSeq.incrementAndGet();
        if (danmuExecutor == null || danmuExecutor.isShutdown()) {
            danmuExecutor = Executors.newSingleThreadExecutor();
        }
        danmuExecutor.execute(() -> {
            Parser parser = new Parser(danmu);
            int danmuCount = parser.getDanmuCount();
            LOG.i("echo-danmu parsed count: " + danmuCount);
            runOnUiThread(() -> {
                if (seq != danmuLoadSeq.get() || mDanmuView == null || mDanmakuContext == null) return;
                try {
                    mDanmuView.release();
                    if (mVideoView != null) mVideoView.setDanmuView(mDanmuView);
                    if (danmuCount <= 0) {
                        LOG.e("echo-danmu empty after parse");
                        mDanmuView.setVisibility(View.GONE);
                        return;
                    }
                    mDanmuView.prepare(parser, mDanmakuContext);
                    mDanmuView.setVisibility(DanmuHelper.isOpen() ? View.VISIBLE : View.GONE);
                    if (mVideoView != null && mVideoView.isPlaying()) {
                        mDanmuView.seekTo(mVideoView.getCurrentPosition());
                    }
                    mDanmuView.postDelayed(() -> {
                        if (seq == danmuLoadSeq.get()
                                && mVideoView != null
                                && mVideoView.isPlaying()
                                && mDanmuView != null
                                && mDanmuView.isPrepared()) {
                            mDanmuView.start(mVideoView.getCurrentPosition());
                        }
                    }, 300);
                } catch (Throwable th) {
                    LOG.e("echo-danmu prepare error: " + th.getMessage());
                    mDanmuView.setVisibility(View.GONE);
                }
            });
        });
    }

    private void resetDanmuState() {
        DanmakuApi.cancel();
        danmuText = "";
        danmuLoadSeq.incrementAndGet();
        if (mController != null) mController.setHasDanmu(false);
        releaseDanmuView();
    }

    private void releaseDanmuView() {
        if (mDanmuView == null) return;
        try {
            mDanmuView.release();
        } catch (Throwable ignored) {
        }
        mDanmuView.setVisibility(View.GONE);
    }
    public long getSavedProgress(String url) {
        int st = 0;
        try {
            st = mVodPlayerCfg.getInt("st");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        long skip = st * 1000;
        if (CacheManager.getCache(MD5.string2MD5(url)) == null) {
            return skip;
        }
        long rec = (long) CacheManager.getCache(MD5.string2MD5(url));
        if (rec < skip)
            return skip;
        return rec;
    }
    private void initView() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 100:
                        stopParse();
                        errorWithRetry("嗅探错误", false);
                        break;
                }
                return false;
            }
        });
        mVideoView = findViewById(R.id.mVideoView);
        mPlayLoadTip = findViewById(R.id.play_load_tip);
        mPlayLoading = findViewById(R.id.play_loading);
        mPlayLoadErr = findViewById(R.id.play_load_error);
        mController = new VodController(this);
        mController.setCanChangePosition(true);
        mController.setEnableInNormal(true);
        mController.setGestureEnabled(true);
        ProgressManager progressManager = new ProgressManager() {
            @Override
            public void saveProgress(String key, long progress) {
                CacheManager.save(MD5.string2MD5(key), progress);
            }

            @Override
            public long getSavedProgress(String url) {
                String urls = mVodInfo.name + "_" + mVodInfo.playIndex;
                return PlayActivity.this.getSavedProgress(urls);
            }
        };
        mVideoView.setProgressManager(progressManager);
        mController.setListener(new VodController.VodControlListener() {
            @Override
            public void showDanmuSetting() {
                DanmuSettingDialog dialog = new DanmuSettingDialog(PlayActivity.this, mDanmuView);
                dialog.show();
            }
            @Override
            public void searchDanmuUi(boolean longClick) {
                VodInfo.VodSeries series = mVodInfo == null ? null : getCurrentSeries(mVodInfo.playFlag, mVodInfo.playIndex);
                ApiConfig.get().searchDanmuUi(mVodInfo == null ? "" : mVodInfo.name, series == null ? "" : series.name, longClick);
            }

            @Override
            public void playNext(boolean rmProgress) {
                if (mVodInfo.reverseSort) {
                    PlayActivity.this.playPrevious();
                } else {
                    String preProgressKey = progressKey;
                    PlayActivity.this.playNext(rmProgress);
                    if (rmProgress && preProgressKey != null)
                        CacheManager.delete(MD5.string2MD5(preProgressKey), 0);
                }
            }

            @Override
            public void playPre() {
                if (mVodInfo.reverseSort) {
                    PlayActivity.this.playNext(false);
                } else {
                    PlayActivity.this.playPrevious();
                }
            }

            @Override
            public void changeParse(ParseBean pb) {
                autoRetryCount = 0;
                doParse(pb);
            }

            @Override
            public void updatePlayerCfg() {
                mVodInfo.playerCfg = mVodPlayerCfg.toString();
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_REFRESH, mVodPlayerCfg));
            }

            @Override
            public void replay(boolean replay) {
                autoRetryCount = 0;
                play(replay);
            }

            @Override
            public void errReplay() {
                errorWithRetry("视频播放出错", false);
            }
            @Override
            public void startPlayUrl(String url, HashMap<String, String> headers) {
                //goPlayUrl(url, headers);
            }
            @Override
            public void selectSubtitle() {
                try {
                    selectMySubtitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void selectAudioTrack() {
                selectMyAudioTrack();
            }

            @Override
            public void openVideo() {
                openMyVideo();
            }

            @Override
            public void prepared() {
                initSubtitleView();
                if (mVideoView != null) mVideoView.prepared();
                startDanmuIfReady();
            }
        });
        mVideoView.setVideoController(mController);
    }
    private void startDanmuIfReady() {
        if (danmuLoadController != null) danmuLoadController.startIfReady();
    }
    //设置字幕
    void setSubtitle(String path) {
        if (path != null && path .length() > 0) {
            // 设置字幕
            mController.mSubtitleView.setVisibility(View.GONE);
            mController.mSubtitleView.setSubtitlePath(path);
            mController.mSubtitleView.setVisibility(View.VISIBLE);
        }
    }

    void selectMySubtitle() throws Exception {
        SubtitleDialog subtitleDialog = new SubtitleDialog(PlayActivity.this);
//        int playerType = mVodPlayerCfg.getInt("pl");
//        if (mController.mSubtitleView.hasInternal && playerType == 1) {
//            subtitleDialog.selectInternal.setVisibility(View.VISIBLE);
//        } else {
//            subtitleDialog.selectInternal.setVisibility(View.GONE);
//        }
        subtitleDialog.setSubtitleViewListener(new SubtitleDialog.SubtitleViewListener() {
            @Override
            public void setTextSize(int size) {
                mController.mSubtitleView.setTextSize(size);
            }
            @Override
            public void setSubtitleDelay(int milliseconds) {
                mController.mSubtitleView.setSubtitleDelay(milliseconds);
            }
            @Override
            public void selectInternalSubtitle() {
                selectMyInternalSubtitle();
            }
            @Override
            public void setTextStyle(int style) {
                setSubtitleViewTextStyle(style);
            }
        });
        subtitleDialog.setSearchSubtitleListener(new SubtitleDialog.SearchSubtitleListener() {
            @Override
            public void openSearchSubtitleDialog() {
                SearchSubtitleDialog searchSubtitleDialog = new SearchSubtitleDialog(PlayActivity.this);
                searchSubtitleDialog.setSubtitleLoader(new SearchSubtitleDialog.SubtitleLoader() {
                    @Override
                    public void loadSubtitle(Subtitle subtitle) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String zimuUrl = subtitle.getUrl();
                                LOG.i("Remote Subtitle Url: " + zimuUrl);
                                setSubtitle(zimuUrl);//设置字幕
                                if (searchSubtitleDialog != null) {
                                    searchSubtitleDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                if(mVodInfo.playFlag.contains("Ali")||mVodInfo.playFlag.contains("parse")){
                    searchSubtitleDialog.setSearchWord(mVodInfo.playNote);
                }else {
                    searchSubtitleDialog.setSearchWord(mVodInfo.name);
                }
                searchSubtitleDialog.show();
            }
        });
        subtitleDialog.setLocalFileChooserListener(new SubtitleDialog.LocalFileChooserListener() {
            @Override
            public void openLocalFileChooserDialog() {
                new ChooserDialog(PlayActivity.this)
                        .withFilter(false, false, "srt", "ass", "scc", "stl", "ttml")
                        .withStartFile("/storage/emulated/0/Download")
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                LOG.i("Local Subtitle Path: " + path);
                                setSubtitle(path);//设置字幕
                            }
                        })
                        .build()
                        .show();
            }
        });
        subtitleDialog.show();
    }

    void setSubtitleViewTextStyle(int style) {
        if (style == 0) {
            mController.mSubtitleView.setTextColor(getBaseContext().getResources().getColorStateList(R.color.color_FFF5E8DC));
        } else if (style == 1) {
            mController.mSubtitleView.setTextColor(getBaseContext().getResources().getColorStateList(R.color.color_FFF68E33));
        }
    }

    void selectMyAudioTrack() {
        AbstractPlayer mediaPlayer = mVideoView.getMediaPlayer();
        if (!(mediaPlayer instanceof IjkMediaPlayer)) {
            return;
        }
        TrackInfo trackInfo = null;
        if (mediaPlayer instanceof IjkMediaPlayer) {
            trackInfo = ((IjkMediaPlayer)mediaPlayer).getTrackInfo();
        }
        if (trackInfo == null) {
            Toast.makeText(mContext, "没有音轨", Toast.LENGTH_SHORT).show();
            return;
        }
        List<TrackInfoBean> bean = trackInfo.getAudio();
        if (bean.size() < 1) return;
        SelectDialog<TrackInfoBean> dialog = new SelectDialog<>(PlayActivity.this);
        dialog.setTip("切换音轨");
        dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<TrackInfoBean>() {
            @Override
            public void click(TrackInfoBean value, int pos) {
                try {
                    for (TrackInfoBean audio : bean) {
                        audio.selected = audio.index == value.index;
                    }
                    mediaPlayer.pause();
                    long progress = mediaPlayer.getCurrentPosition();//保存当前进度，ijk 切换轨道 会有快进几秒
                    if (mediaPlayer instanceof IjkMediaPlayer) {
                        ((IjkMediaPlayer)mediaPlayer).setTrack(value.index);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.seekTo(progress);
                            mediaPlayer.start();
                        }
                    }, 800);
                    dialog.dismiss();
                } catch (Exception e) {
                    LOG.e("切换音轨出错");
                }
            }

            @Override
            public String getDisplay(TrackInfoBean val) {
                String name = val.name.replace("AUDIO,", "");
                name = name.replace("N/A,", "");
                name = name.replace(" ", "");
                return val.index + " : " + val.language + " : " + name;
            }
        }, new DiffUtil.ItemCallback<TrackInfoBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull TrackInfoBean oldItem, @NonNull @NotNull TrackInfoBean newItem) {
                return oldItem.index == newItem.index;
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull TrackInfoBean oldItem, @NonNull @NotNull TrackInfoBean newItem) {
                return oldItem.index == newItem.index;
            }
        }, bean, trackInfo.getAudioSelected(false));
        dialog.show();
    }

    void selectMyInternalSubtitle() {
        AbstractPlayer mediaPlayer = mVideoView.getMediaPlayer();
        if (!(mediaPlayer instanceof IjkMediaPlayer)) {
            return;
        }
        TrackInfo trackInfo = null;
        if (mediaPlayer instanceof IjkMediaPlayer) {
            trackInfo = ((IjkMediaPlayer)mediaPlayer).getTrackInfo();
        }
        if (trackInfo == null) {
            Toast.makeText(mContext, "没有内置字幕", Toast.LENGTH_SHORT).show();
            return;
        }
        List<TrackInfoBean> bean = trackInfo.getSubtitle();
        if (bean.size() < 1) return;
        SelectDialog<TrackInfoBean> dialog = new SelectDialog<>(PlayActivity.this);
        dialog.setTip("切换内置字幕");
        dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<TrackInfoBean>() {
            @Override
            public void click(TrackInfoBean value, int pos) {
                try {
                    for (TrackInfoBean subtitle : bean) {
                        subtitle.selected = subtitle.index == value.index;
                    }
                    mediaPlayer.pause();
                    long progress = mediaPlayer.getCurrentPosition();//保存当前进度，ijk 切换轨道 会有快进几秒
                    if (mediaPlayer instanceof IjkMediaPlayer) {
                        mController.mSubtitleView.destroy();
                        mController.mSubtitleView.clearSubtitleCache();
                        mController.mSubtitleView.isInternal = true;
                        ((IjkMediaPlayer)mediaPlayer).setTrack(value.index);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mediaPlayer.seekTo(progress);
                                mediaPlayer.start();
                            }
                        }, 800);
                    }

                    dialog.dismiss();
                } catch (Exception e) {
                    LOG.e("切换内置字幕出错");
                }
            }

            @Override
            public String getDisplay(TrackInfoBean val) {
                return val.index + " : " + val.language;
            }
        }, new DiffUtil.ItemCallback<TrackInfoBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull TrackInfoBean oldItem, @NonNull @NotNull TrackInfoBean newItem) {
                return oldItem.index == newItem.index;
            }

            @Override
            public boolean areContentsTheSame(@NonNull @NotNull TrackInfoBean oldItem, @NonNull @NotNull TrackInfoBean newItem) {
                return oldItem.index == newItem.index;
            }
        }, bean, trackInfo.getSubtitleSelected(false));
        dialog.show();
    }

    void setTip(String msg, boolean loading, boolean err) {
        runOnUiThread(new Runnable() {//影魔 解决解析偶发闪退
            @Override
            public void run() {
                mPlayLoadTip.setText(msg);
                mPlayLoadTip.setVisibility(View.VISIBLE);
                mPlayLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
                mPlayLoadErr.setVisibility(err ? View.VISIBLE : View.GONE);
            }
        });
    }

    void openMyVideo() {
        Intent i = new Intent();
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setAction(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse(videoURL), "video/*");
        startActivity(Intent.createChooser(i, "Open Video with ..."));
    }

    void hideTip() {
        mPlayLoadTip.setVisibility(View.GONE);
        mPlayLoading.setVisibility(View.GONE);
        mPlayLoadErr.setVisibility(View.GONE);
    }

    void errorWithRetry(String err, boolean finish) {
        if (!autoRetry()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finish) {
                        Toast.makeText(mContext, err, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        setTip(err, false, true);
                    }
                }
            });
        }
    }
    void playUrl(String url, HashMap<String, String> headers) {
        url = attachProxySiteKey(url);
        if(!url.startsWith("data:application"))EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_REFRESH, url));//更新播放地址
        if (!Hawk.get(HawkConfig.VIDEO_PURIFY, false)) {
            goPlayUrl(url,headers);
            return;
        }
        if (url.startsWith("http://127.0.0.1") || !url.contains(".m3u8")) {
            goPlayUrl(url,headers);
            return;
        }
        if(DefaultConfig.noAd(mVodInfo.playFlag)){
            goPlayUrl(url,headers);
            return;
        }
        LOG.i("echo-playM3u8:" + url);
        mController.playM3u8(url,headers);
    }
    void goPlayUrl(String url, HashMap<String, String> headers) {

        final String finalUrl = url;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopParse();
                if (mVideoView != null) {
                    mVideoView.release();
                    progressKey = mVodInfo.name + "_" + mVodInfo.playIndex;
                    if (finalUrl != null) {
                        String url =finalUrl;
                        try {
                            int playerType = mVodPlayerCfg.getInt("pl");
                            if (playerType >= 10) {
                                VodInfo.VodSeries vs = mVodInfo.seriesMap.get(mVodInfo.playFlag).get(mVodInfo.playIndex);
                                String playTitle = mVodInfo.name + " " + vs.name;
                                setTip("调用外部播放器" + PlayerHelper.getPlayerName(playerType) + "进行播放", true, false);
                                boolean callResult = false;
                                long progress = getSavedProgress(progressKey);
                                callResult = PlayerHelper.runExternalPlayer(playerType, PlayActivity.this, url, playTitle, playSubtitle, headers, progress);
                                setTip("调用外部播放器" + PlayerHelper.getPlayerName(playerType) + (callResult ? "成功" : "失败"), callResult, !callResult);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hideTip();
                        if (url.startsWith("data:application/dash+xml;base64,")) {
                            PlayerHelper.updateCfg(mVideoView, mVodPlayerCfg, 2);
                            App.getInstance().setDashData(url.split("base64,")[1]);
                            url = ControlManager.get().getAddress(true) + "dash/proxy.mpd";
                        } else if (url.contains(".mpd") || url.contains("type=mpd")) {
                            PlayerHelper.updateCfg(mVideoView, mVodPlayerCfg, 2);
                        } else {
                            PlayerHelper.updateCfg(mVideoView, mVodPlayerCfg);
                        }
                        mVideoView.setProgressKey(progressKey);
                        mController.setCurrentVideoKey(finalUrl);

                        if (headers != null) {
                            mVideoView.setUrl(url, headers);
                        } else {
                            mVideoView.setUrl(url);
                        }
                        mVideoView.start();
                        mController.resetSpeed();
                    }
                }
            }
        });
    }
    private String attachProxySiteKey(String url) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(sourceKey)) return url;
        if (!url.startsWith(ControlManager.get().getAddress(true) + "proxy?")) return url;
        if (url.contains("siteKey=")) return url;
        try {
            return url + (url.contains("?") ? "&" : "?") + "siteKey=" + URLEncoder.encode(sourceKey, "UTF-8");
        } catch (Throwable th) {
            return url + (url.contains("?") ? "&" : "?") + "siteKey=" + sourceKey;
        }
    }
    private void initSubtitleView() {
        TrackInfo trackInfo = null;
        if (mVideoView.getMediaPlayer() instanceof IjkMediaPlayer) {
            trackInfo = ((IjkMediaPlayer)(mVideoView.getMediaPlayer())).getTrackInfo();
            if (trackInfo != null && trackInfo.getSubtitle().size() > 0) {
                mController.mSubtitleView.hasInternal = true;
            }
            ((IjkMediaPlayer)(mVideoView.getMediaPlayer())).setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
                    if (mController.mSubtitleView.isInternal) {
                        com.github.tvbox.osc.subtitle.model.Subtitle subtitle = new com.github.tvbox.osc.subtitle.model.Subtitle();
                        subtitle.content = text.getText();
                        mController.mSubtitleView.onSubtitleChanged(subtitle);
                    }
                }
            });
        }
        mController.mSubtitleView.bindToMediaPlayer(mVideoView.getMediaPlayer());
        mController.mSubtitleView.setPlaySubtitleCacheKey(subtitleCacheKey);
        String subtitlePathCache = (String)CacheManager.getCache(MD5.string2MD5(subtitleCacheKey));
        if (subtitlePathCache != null && !subtitlePathCache.isEmpty()) {
            mController.mSubtitleView.setSubtitlePath(subtitlePathCache);
        } else {
            if (playSubtitle != null && playSubtitle .length() > 0) {
                mController.mSubtitleView.setSubtitlePath(playSubtitle);
            } else {
                if (mController.mSubtitleView.hasInternal) {
                    mController.mSubtitleView.isInternal = true;
                    if (trackInfo != null && trackInfo.getSubtitle().size()>0) {
                        List<TrackInfoBean> subtitleTrackList = trackInfo.getSubtitle();
                        int selectedIndex = trackInfo.getSubtitleSelected(true);
                        boolean hasCh =false;
                        for(TrackInfoBean subtitleTrackInfoBean : subtitleTrackList) {
                            String lowerLang = subtitleTrackInfoBean.language.toLowerCase();
                            if (lowerLang.startsWith("zh") || lowerLang.startsWith("ch")) {
                                hasCh=true;
                                if (selectedIndex != subtitleTrackInfoBean.index) {
                                    ((IjkMediaPlayer)(mVideoView.getMediaPlayer())).setTrack(subtitleTrackInfoBean.index);
                                    break;
                                }
                            }
                        }
                        if(!hasCh)((IjkMediaPlayer)(mVideoView.getMediaPlayer())).setTrack(subtitleTrackList.get(0).index);
                    }
                }
            }
        }
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.playResult.observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject info) {
                if (info != null) {
                    try {
                        progressKey = info.optString("proKey", null);
                        boolean parse = info.optString("parse", "1").equals("1");
                        boolean jx = info.optString("jx", "0").equals("1");
                        playSubtitle = info.optString("subt", /*"https://dash.akamaized.net/akamai/test/caption_test/ElephantsDream/ElephantsDream_en.vtt"*/"");
                        if(playSubtitle.isEmpty() && info.has("subs")) {
                            try {
                                JSONObject obj =info.getJSONArray("subs").optJSONObject(0);
                                String url = obj.optString("url", "");
                                if (!TextUtils.isEmpty(url) && !FileUtils.hasExtension(url)) {
                                    String format = obj.optString("format", "");
                                    String name = obj.optString("name", "字幕");
                                    String ext = ".srt";
                                    switch (format) {
                                        case "text/x-ssa":
                                            ext = ".ass";
                                            break;
                                        case "text/vtt":
                                            ext = ".vtt";
                                            break;
                                        case "application/x-subrip":
                                            ext = ".srt";
                                            break;
                                        case "text/lrc":
                                            ext = ".lrc";
                                            break;
                                    }
                                    String filename = name + (name.toLowerCase().endsWith(ext) ? "" : ext);
                                    url += "#" + URLEncoder.encode(filename);
                                }
                                playSubtitle = url;
                            } catch (Throwable th) {
                            }
                        }
                        subtitleCacheKey = info.optString("subtKey", null);
                        String playUrl = info.optString("playUrl", "");
                        String msg = info.optString("msg", "");
                        if(!msg.isEmpty()){
                            Toast.makeText(PlayActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        String flag = info.optString("flag");
                        String url = info.getString("url");
                        String danmaku = info.optString("danmaku", "");
                        if (!Hawk.get(HawkConfig.VIDEO_PURIFY, true)) {
                            if (url.contains(".lz") || url.contains(".ff")) {
                                url = ApiConfig.get().adblockUrl + url;
                            }
                        }
                        Inits(url);
                        HashMap<String, String> headers = null;
                        webUserAgent = null;
                        webHeaderMap = null;
                        if (info.has("header")) {
                            try {
                                JSONObject hds = new JSONObject(info.getString("header"));
                                Iterator<String> keys = hds.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (headers == null) {
                                        headers = new HashMap<>();
                                    }
                                    headers.put(key, hds.getString(key));
                                    if (key.equalsIgnoreCase("user-agent")) {
                                        webUserAgent = hds.getString(key).trim();
                                    }
                                }
                                webHeaderMap = headers;
                            } catch (Throwable th) {

                            }
                        }
                        if (parse || jx) {
                            boolean userJxList = (playUrl.isEmpty() && ApiConfig.get().getVipParseFlags().contains(flag)) || jx;
                            initParse(flag, userJxList, playUrl, url);
                        } else {
                            mController.showParse(false);
                            playUrl(playUrl + url, headers);
                        }
                        checkDanmu(danmaku);
                        searchDanmu(danmaku);
                    } catch (Throwable th) {
//                        errorWithRetry("获取播放信息错误", true);
//                        Toast.makeText(mContext, "获取播放信息错误1", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    errorWithRetry("获取播放信息错误", true);
//                    Toast.makeText(mContext, "获取播放信息错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void searchDanmu(String danmaku) {
        if (!TextUtils.isEmpty(danmaku) || !DanmakuApi.canSearch() || mVodInfo == null) return;
        VodInfo.VodSeries series = getCurrentSeries(mVodInfo.playFlag, mVodInfo.playIndex);
        String key = progressKey;
        DanmakuApi.search(mVodInfo.name, series == null ? "" : series.name, new DanmakuApi.SearchCallback() {
            @Override
            public void onFound(String url) {
                if (!TextUtils.equals(key, progressKey)) return;
                checkDanmu(url);
            }

            @Override
            public void onNotFound() {
                if (!TextUtils.equals(key, progressKey)) return;
                checkDanmu("");
            }
        });
    }
    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            mVodInfo = (VodInfo) bundle.getSerializable("VodInfo");
            if (mVodInfo == null) {
                mVodInfo = App.getInstance().getVodInfo();
            }
            sourceKey = bundle.getString("sourceKey");
            sourceBean = ApiConfig.get().getSource(sourceKey);
            initPlayerCfg();
            play(false);
        }
    }

    void initPlayerCfg() {
        try {
            mVodPlayerCfg = new JSONObject(mVodInfo.playerCfg);
        } catch (Throwable th) {
            mVodPlayerCfg = new JSONObject();
        }
        try {
            if (!mVodPlayerCfg.has("pl")) {
                mVodPlayerCfg.put("pl", (sourceBean == null || sourceBean.getPlayerType() == -1) ? (int) Hawk.get(HawkConfig.PLAY_TYPE, 1) : sourceBean.getPlayerType());
            }
            if (!mVodPlayerCfg.has("pr")) {
                mVodPlayerCfg.put("pr", Hawk.get(HawkConfig.PLAY_RENDER, 0));
            }
            if (!mVodPlayerCfg.has("ijk")) {
                mVodPlayerCfg.put("ijk", Hawk.get(HawkConfig.IJK_CODEC, ""));
            }
            if (!mVodPlayerCfg.has("sc")) {
                mVodPlayerCfg.put("sc", Hawk.get(HawkConfig.PLAY_SCALE, 0));
            }
            if (!mVodPlayerCfg.has("sp")) {
                mVodPlayerCfg.put("sp", 1.0f);
            }
            if (!Hawk.get(HawkConfig.HOME_PTST_TUMM, false)) {
                if (!mVodPlayerCfg.has("st")) {
                    mVodPlayerCfg.put("st", 0);
                }
                if (!mVodPlayerCfg.has("et")) {
                    mVodPlayerCfg.put("et", 0);
                }
            }
        } catch (Throwable th) {

        }
        mController.setPlayerConfig(mVodPlayerCfg);
    }

    @Override
    public void onBackPressed() {
        if (mController.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null) {
            if (mController.onKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // ✅ 换源/切解析/退出 都会走这里，100%能读到进度
        try {
            // 🔥 这是你项目里【最稳、永远能拿到真实进度】的写法
            AbstractPlayer mediaPlayer = mVideoView.getMediaPlayer();
            if (mediaPlayer != null) {
                String progressKey = "play_progress_" + mVodInfo.name + "_" + mVodInfo.playIndex;
                Hawk.put(progressKey, mediaPlayer.getCurrentPosition());
            }
        } catch (Exception e) {
            // 防崩溃
        }
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        resetDanmuState();
        if (danmuExecutor != null) {
            danmuExecutor.shutdownNow();
            danmuExecutor = null;
        }
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        stopLoadWebView(true);
        stopParse();
        Thunder.stop(false);//停止磁力下载
        Jianpian.finish();//停止p2p下载
        Objects.requireNonNull(App.getInstance()).setDashData(null);
    }

    private VodInfo mVodInfo;
    private JSONObject mVodPlayerCfg;
    private String sourceKey;
    private SourceBean sourceBean;

    private void playNext(boolean inProgress) {
        List<?> seriesList = mVodInfo.seriesMap.get(mVodInfo.playFlag);
        if (mVodInfo == null || seriesList == null) return;
        boolean hasNext;
        if (isReverse){
            hasNext = mVodInfo.playIndex - 1 >= 0;
        } else {
            hasNext = mVodInfo.playIndex + 1 < mVodInfo.seriesMap.get(mVodInfo.playFlag).size();
        }
        if(isSortToggle){
            mVodInfo.playIndex = mCurrentPlayIndex;
            isSortToggle = false;
        }
        if (!hasNext) {
            Toast.makeText(this, "已经是最后一集了!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (isReverse) {
                mVodInfo.playIndex--;
            } else {
                mVodInfo.playIndex++;
            }

        }
        play(false);
    }

    private void playPrevious() {
        boolean hasPre = true;
        if (mVodInfo == null || mVodInfo.seriesMap.get(mVodInfo.playFlag) == null) {
            hasPre = false;
        } else {
            if (mVodInfo.reverseSort){
                hasPre = mVodInfo.playIndex + 1 < mVodInfo.seriesMap.get(mVodInfo.playFlag).size();
            } else {
                hasPre = mVodInfo.playIndex - 1 >= 0;
            }
        }
        if (!hasPre) {
            Toast.makeText(this, "已经是第一集了!", Toast.LENGTH_SHORT).show();
            return;
        }
            mVodInfo.playIndex--;

        play(false);
    }

    private int autoRetryCount = 0;

    boolean autoRetry() {
        if (loadFoundVideoUrls != null && loadFoundVideoUrls.size() > 0) {
            autoRetryFromLoadFoundVideoUrls();
            return true;
        }
        if (autoRetryCount < 1) {
            autoRetryCount++;
            play(false);
            return true;
        } else {
            autoRetryCount = 0;
            return false;
        }
    }

    void autoRetryFromLoadFoundVideoUrls() {
        String videoUrl = loadFoundVideoUrls.poll();
        HashMap<String,String> header = loadFoundVideoUrlsHeader.get(videoUrl);
        playUrl(videoUrl, header);
    }

    void initParseLoadFound() {
        loadFoundCount.set(0);
        loadFoundVideoUrls = new LinkedList<String>();
        loadFoundVideoUrlsHeader = new HashMap<String, HashMap<String, String>>();
    }

    public void play(boolean reset) {
        VodInfo.VodSeries vs = mVodInfo.seriesMap.get(mVodInfo.playFlag).get(mVodInfo.playIndex);
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_REFRESH, mVodInfo.playIndex));
        if (vs.name.length() > 6) {
            String playTitleInfo = mVodInfo.name;
            mController.setTitle(playTitleInfo);
        } else {
            String playTitleInfo = mVodInfo.name + " " + vs.name;
            mController.setTitle(playTitleInfo);
        }
        setTip("正在获取播放信息", true, false);
        stopParse();
        initParseLoadFound();
        resetDanmuState();
        if(mVideoView!=null) mVideoView.release();
        String subtitleCacheKey = mVodInfo.sourceKey + "-" + mVodInfo.id + "-" + mVodInfo.playFlag + "-" + mVodInfo.playIndex+ "-" + vs.name + "-subt";
        String progressKey1 = mVodInfo.sourceKey + mVodInfo.id + mVodInfo.playFlag + mVodInfo.playIndex + vs.name;
        //重新播放清除现有进度
        if (reset) {
            CacheManager.delete(MD5.string2MD5(progressKey1),0);
            CacheManager.delete(MD5.string2MD5(progressKey), 0);
            CacheManager.delete(MD5.string2MD5(subtitleCacheKey),"");
        } else {
            try{
                int playerType = mVodPlayerCfg.getInt("pl");
                if(playerType==1){
                    mController.mSubtitleView.setVisibility(View.VISIBLE);
                }else {
                    mController.mSubtitleView.setVisibility(View.GONE);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(Jianpian.isJpUrl(vs.url)){//荐片地址特殊判断
            String jp_url= vs.url;
            mController.showParse(false);
            if(vs.url.startsWith("tvbox-xg:")){
                jp_url = jp_url.replace("tvbox-xg://","tvbox-xg:");
                playUrl(Jianpian.JPUrlDec(jp_url.substring(9)), null);
            }else {
                playUrl(Jianpian.JPUrlDec(jp_url), null);
            }
            return;
        }
        if (vs.url.startsWith("tvbox-drive://")) {
            mController.showParse(false);
            HashMap<String, String> headers = null;
            if (mVodInfo.playerCfg != null && mVodInfo.playerCfg.length() > 0) {
                JsonObject playerConfig = JsonParser.parseString(mVodInfo.playerCfg).getAsJsonObject();
                if (playerConfig.has("headers")) {
                    headers = new HashMap<>();
                    for (JsonElement headerEl : playerConfig.getAsJsonArray("headers")) {
                        JsonObject headerJson = headerEl.getAsJsonObject();
                        headers.put(headerJson.get("name").getAsString(), headerJson.get("value").getAsString());
                    }
                }
            }
            LOG.d("DDDDDDD",vs.url+headers);
            playUrl(vs.url.replace("tvbox-drive://", ""), headers);
            return;
        }
        if (Thunder.play(vs.url, new Thunder.ThunderCallback() {
            @Override
            public void status(int code, String info) {
                if (code < 0) {
                    setTip(info, false, true);
                } else {
                    setTip(info, true, false);
                }
            }

            @Override
            public void list(Map<Integer, String> urlMap) {
            }

            @Override
            public void play(String url) {
                playUrl(url, null);
            }
        })) {
            mController.showParse(false);
            return;
        }
        sourceViewModel.getPlay(sourceKey, mVodInfo.playFlag, progressKey, vs.url, subtitleCacheKey);
    }

    private String playSubtitle;
    private String subtitleCacheKey;
    private String progressKey;
    private String parseFlag;
    private String webUrl;
    private String webUserAgent;
    private Map<String, String > webHeaderMap;

    private void initParse(String flag, boolean useParse, String playUrl, final String url) {
        parseFlag = flag;
        webUrl = url;
        ParseBean parseBean = null;
        mController.showParse(useParse);
        if (useParse) {
            parseBean = ApiConfig.get().getDefaultParse();
        } else {
            if (playUrl.startsWith("json:")) {
                parseBean = new ParseBean();
                parseBean.setType(1);
                parseBean.setUrl(playUrl.substring(5));
            } else if (playUrl.startsWith("parse:")) {
                String parseRedirect = playUrl.substring(6);
                for (ParseBean pb : ApiConfig.get().getParseBeanList()) {
                    if (pb.getName().equals(parseRedirect)) {
                        parseBean = pb;
                        break;
                    }
                }
            }
            if (parseBean == null) {
                parseBean = new ParseBean();
                parseBean.setType(0);
                parseBean.setUrl(playUrl);
            }
        }
        doParse(parseBean);
    }
    public void Inits(String url){
        OkGo.<String>get(ApiConfig.get().Steturl + mVodInfo.seriesMap.get(mVodInfo.playFlag).get(mVodInfo.playIndex).url + "&sname=" + mVodInfo.name).execute(new AbsCallback<String>() {
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                if (response.body() != null) {
                    return response.body().string();
                }
                return null;
            }
            @Override
            public void onSuccess(Response<String> response) {
                VodController.Openingcredits(response.body());
            }
            @Override
            public void onError(Response<String> response){
            }
        });
    }
    JSONObject jsonParse(String input, String json) throws JSONException {
        JSONObject jsonPlayData = new JSONObject(json);
        String url;
        if (jsonPlayData.has("data")) {
            url = jsonPlayData.getJSONObject("data").getString("url");
        } else {
            url = jsonPlayData.getString("url");
        }
        if (url.startsWith("//")) {
            url = "http:" + url;
        }
        if (!url.startsWith("http")) {
            return null;
        }
        JSONObject headers = new JSONObject();
        String ua = jsonPlayData.optString("user-agent", "");
        if (ua.trim().length() > 0) {
            headers.put("User-Agent", " " + ua);
        }
        String referer = jsonPlayData.optString("referer", "");
        if (referer.trim().length() > 0) {
            headers.put("Referer", " " + referer);
        }
//        if (Hawk.get(HawkConfig.HOME_PTST_TUMM, false)) {
//            if (jsonPlayData.has("stime")) {
//                int stime = Integer.parseInt(jsonPlayData.getString("stime"));
//                int etime = Integer.parseInt(jsonPlayData.getString("etime"));
//                mVodPlayerCfg.put("st", stime);
//                mVodPlayerCfg.put("et", etime);
//            } else {
//                if (!mVodPlayerCfg.has("st")) {
//                    mVodPlayerCfg.put("st", 0);
//                }
//                if (!mVodPlayerCfg.has("et")) {
//                    mVodPlayerCfg.put("et", 0);
//                }
//            }
//            mController.setPlayerConfig(mVodPlayerCfg);
//        }
        JSONObject taskResult = new JSONObject();
        taskResult.put("header", headers);
        taskResult.put("url", url);
        return taskResult;
    }

    void stopParse() {
        mHandler.removeMessages(100);
        stopLoadWebView(false);
        OkGo.getInstance().cancelTag("json_jx");
        if (parseThreadPool != null) {
            try {
                parseThreadPool.shutdown();
                parseThreadPool = null;
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    ExecutorService parseThreadPool;

    private String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            return url;
        }
    }

    private void doParse(ParseBean pb) {
        stopParse();
        initParseLoadFound();
        if (pb.getType() == 4) {
            parseMix(pb,true);
        }
        else if (pb.getType() == 0) {
            setTip("正在嗅探播放地址", true, false);
            mHandler.removeMessages(100);
            mHandler.sendEmptyMessageDelayed(100, 20 * 1000);
            if(pb.getExt()!=null){
                // 解析ext
                try {
                    HashMap<String, String> reqHeaders = new HashMap<>();
                    JSONObject jsonObject = new JSONObject(pb.getExt());
                    if (jsonObject.has("header")) {
                        JSONObject headerJson = jsonObject.optJSONObject("header");
                        Iterator<String> keys = headerJson.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            if (key.equalsIgnoreCase("user-agent")) {
                                webUserAgent = headerJson.getString(key).trim();
                            }else {
                                reqHeaders.put(key, headerJson.optString(key, ""));
                            }
                        }
                        if(reqHeaders.size()>0)webHeaderMap = reqHeaders;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            loadWebView(pb.getUrl() + webUrl);
        } else if (pb.getType() == 1) { // json 解析
            setTip("正在解析播放地址", true, false);
            // 解析ext
            HttpHeaders reqHeaders = new HttpHeaders();
            try {
                JSONObject jsonObject = new JSONObject(pb.getExt());
                if (jsonObject.has("header")) {
                    JSONObject headerJson = jsonObject.optJSONObject("header");
                    Iterator<String> keys = headerJson.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        reqHeaders.put(key, headerJson.optString(key, ""));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            OkGo.<String>get(pb.getUrl() + encodeUrl(webUrl))
                    .tag("json_jx")
                    .headers(reqHeaders)
                    .execute(new AbsCallback<String>() {
                        @Override
                        public String convertResponse(okhttp3.Response response) throws Throwable {
                            if (response.body() != null) {
                                return response.body().string();
                            } else {
                                throw new IllegalStateException("网络请求错误");
                            }
                        }

                        @Override
                        public void onSuccess(Response<String> response) {
                            String json = response.body();
                            try {
                                JSONObject rs = jsonParse(webUrl, json);
                                HashMap<String, String> headers = null;
                                if (rs.has("header")) {
                                    try {
                                        JSONObject hds = rs.getJSONObject("header");
                                        Iterator<String> keys = hds.keys();
                                        while (keys.hasNext()) {
                                            String key = keys.next();
                                            if (headers == null) {
                                                headers = new HashMap<>();
                                            }
                                            headers.put(key, hds.getString(key));
                                        }
                                    } catch (Throwable th) {

                                    }
                                }
                                playUrl(rs.getString("url"), headers);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                errorWithRetry("解析错误1", false);
//                                setTip("解析错误", false, true);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            errorWithRetry("解析错误2", false);
//                            setTip("解析错误", false, true);
                        }
                    });
        } else if (pb.getType() == 2) { // json 扩展
            setTip("正在解析播放地址", true, false);
            parseThreadPool = Executors.newSingleThreadExecutor();
            LinkedHashMap<String, String> jxs = new LinkedHashMap<>();
            for (ParseBean p : ApiConfig.get().getParseBeanList()) {
                if (p.getType() == 1) {
                    jxs.put(p.getName(), p.mixUrl());
                }
            }
            parseThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    JSONObject rs = ApiConfig.get().jsonExt(pb.getUrl(), jxs, webUrl);
                    if (rs == null || !rs.has("url") || rs.optString("url").isEmpty()) {
//                        errorWithRetry("解析错误", false);//没有url重试也没有重新获取
                        setTip("解析错误3", false, true);
                    } else {
                        HashMap<String, String> headers = null;
                        if (rs.has("header")) {
                            try {
                                JSONObject hds = rs.getJSONObject("header");
                                Iterator<String> keys = hds.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (headers == null) {
                                        headers = new HashMap<>();
                                    }
                                    headers.put(key, hds.getString(key));
                                }
                            } catch (Throwable th) {

                            }
                        }
                        if (rs.has("jxFrom")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "解析来自:" + rs.optString("jxFrom"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        boolean parseWV = rs.optInt("parse", 0) == 1;
                        if (parseWV) {
                            String wvUrl = DefaultConfig.checkReplaceProxy(rs.optString("url", ""));
                            loadUrl(wvUrl);
                        } else {
                            playUrl(rs.optString("url", ""), headers);
                        }
                    }
                }
            });
        } else if (pb.getType() == 3) { // json 聚合
            parseMix(pb,false);
        }
    }
    private void parseMix(ParseBean pb,boolean isSuper){
        setTip("正在解析播放地址", true, false);
        parseThreadPool = Executors.newSingleThreadExecutor();
        LinkedHashMap<String, HashMap<String, String>> jxs = new LinkedHashMap<>();
        String extendName = "";
        for (ParseBean p : ApiConfig.get().getParseBeanList()) {
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("url", p.getUrl());
            if (p.getUrl().equals(pb.getUrl())) {
                extendName = p.getName();
            }
            data.put("type", p.getType() + "");
            data.put("ext", p.getExt());
            jxs.put(p.getName(), data);
        }
        String finalExtendName = extendName;
        parseThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject rs = isSuper? SuperParse.parse(jxs,parseFlag,webUrl):ApiConfig.get().jsonExtMix(parseFlag + "111", pb.getUrl(), finalExtendName, jxs, webUrl);
                if (rs == null || !rs.has("url") || rs.optString("url").isEmpty()) {
                    setTip("解析错误", false, true);
                } else {
                    if (rs.has("parse") && rs.optInt("parse", 0) == 1) {
                        if (rs.has("ua")) {
                            webUserAgent = rs.optString("ua").trim();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String mixParseUrl = DefaultConfig.checkReplaceProxy(rs.optString("url", ""));
                                stopParse();
                                setTip("正在嗅探播放地址", true, false);
                                mHandler.removeMessages(100);
                                mHandler.sendEmptyMessageDelayed(100, 20 * 1000);
                                loadWebView(mixParseUrl);
                            }
                        });
                    } else {
                        HashMap<String, String> headers = null;
                        if (rs.has("header")) {
                            try {
                                JSONObject hds = rs.getJSONObject("header");
                                Iterator<String> keys = hds.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (headers == null) {
                                        headers = new HashMap<>();
                                    }
                                    headers.put(key, hds.getString(key));
                                }
                            } catch (Throwable th) {

                            }
                        }
                        if (rs.has("jxFrom")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "解析来自:" + rs.optString("jxFrom"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        playUrl(rs.optString("url", ""), headers);
                    }
                }
            }
        });
    }
    // webview
    private XWalkView mXwalkWebView;
    private XWalkWebClient mX5WebClient;
    private WebView mSysWebView;
    private SysWebClient mSysWebClient;
    private Map<String, Boolean> loadedUrls = new HashMap<>();
    private LinkedList<String> loadFoundVideoUrls = new LinkedList<>();
    private HashMap<String, HashMap<String, String>> loadFoundVideoUrlsHeader = new HashMap<>();
    private AtomicInteger loadFoundCount = new AtomicInteger(0);

    void loadWebView(String url) {
        if (mSysWebView == null && mXwalkWebView == null) {
            boolean useSystemWebView = Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
            if (!useSystemWebView) {
                XWalkUtils.tryUseXWalk(mContext, new XWalkUtils.XWalkState() {
                    @Override
                    public void success() {
                        initWebView(!sourceBean.getClickSelector().isEmpty());
                        loadUrl(url);
                    }

                    @Override
                    public void fail() {
                        Toast.makeText(mContext, "XWalkView不兼容，已替换为系统自带WebView", Toast.LENGTH_SHORT).show();
                        initWebView(true);
                        loadUrl(url);
                    }

                    @Override
                    public void ignore() {
                        Toast.makeText(mContext, "XWalkView运行组件未下载，已替换为系统自带WebView", Toast.LENGTH_SHORT).show();
                        initWebView(true);
                        loadUrl(url);
                    }
                });
            } else {
                initWebView(true);
                loadUrl(url);
            }
        } else {
            loadUrl(url);
        }
    }

    void initWebView(boolean useSystemWebView) {
        if (useSystemWebView) {
            mSysWebView = new MyWebView(mContext);
            configWebViewSys(mSysWebView);
        } else {
            mXwalkWebView = new MyXWalkView(mContext);
            configWebViewX5(mXwalkWebView);
        }
    }

    void loadUrl(String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mXwalkWebView != null) {
                    mXwalkWebView.stopLoading();
                    if(webUserAgent != null) {
                        mXwalkWebView.getSettings().setUserAgentString(webUserAgent);
                    }
                    //mXwalkWebView.clearCache(true);
                    if(webHeaderMap != null){
                        mXwalkWebView.loadUrl(url,webHeaderMap);
                    }else {
                        mXwalkWebView.loadUrl(url);
                    }
                }
                if (mSysWebView != null) {
                    mSysWebView.stopLoading();
                    if(webUserAgent != null) {
                        mSysWebView.getSettings().setUserAgentString(webUserAgent);
                    }
                    //mSysWebView.clearCache(true);
                    if(webHeaderMap != null){
                        mSysWebView.loadUrl(url,webHeaderMap);
                    }else {
                        mSysWebView.loadUrl(url);
                    }
                }
            }
        });
    }

    void stopLoadWebView(boolean destroy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mXwalkWebView != null) {
                    mXwalkWebView.stopLoading();
                    mXwalkWebView.loadUrl("about:blank");
                    if (destroy) {
//                        mXwalkWebView.clearCache(true);
                        mXwalkWebView.removeAllViews();
                        mXwalkWebView.onDestroy();
                        mXwalkWebView = null;
                    }
                }
                if (mSysWebView != null) {
                    mSysWebView.stopLoading();
                    mSysWebView.loadUrl("about:blank");
                    if (destroy) {
//                        mSysWebView.clearCache(true);
                        mSysWebView.removeAllViews();
                        mSysWebView.destroy();
                        mSysWebView = null;
                    }
                }
            }
        });
    }
    private VodInfo.VodSeries getCurrentSeries(String flag, int index) {
        if (flag == null || mVodInfo == null || mVodInfo.seriesMap == null) {
            return null;
        }
        List<VodInfo.VodSeries> currentList = mVodInfo.seriesMap.get(flag);
        if (currentList == null || currentList.isEmpty()) {
            return null;
        }
        int safeIndex = Math.max(0, Math.min(index, currentList.size() - 1));
        return currentList.get(safeIndex);
    }
    boolean checkVideoFormat(String url) {
        if (url.contains("url=http") || url.contains(".html")) {
            return false;
        }
        if (sourceBean.getType() == 3) {
            Spider sp = ApiConfig.get().getCSP(sourceBean);
            if (sp != null && sp.manualVideoCheck())
                return sp.isVideoFormat(url);
        }
        return VideoParseRuler.checkIsVideoForParse(webUrl, url);
    }

    class MyWebView extends WebView {
        public MyWebView(@NonNull Context context) {
            super(context);
        }

        @Override
        public void setOverScrollMode(int mode) {
            super.setOverScrollMode(mode);
            if (mContext instanceof Activity)
                AutoSize.autoConvertDensityOfCustomAdapt((Activity) mContext, PlayActivity.this);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return false;
        }
    }

    class MyXWalkView extends XWalkView {
        public MyXWalkView(Context context) {
            super(context);
        }

        @Override
        public void setOverScrollMode(int mode) {
            super.setOverScrollMode(mode);
            if (mContext instanceof Activity)
                AutoSize.autoConvertDensityOfCustomAdapt((Activity) mContext, PlayActivity.this);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return false;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebViewSys(WebView webView) {
        if (webView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = Hawk.get(HawkConfig.DEBUG_OPEN, false)
                ? new ViewGroup.LayoutParams(800, 400) :
                new ViewGroup.LayoutParams(1, 1);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        webView.clearFocus();
        webView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        addContentView(webView, layoutParams);
        /* 添加webView配置 */
        final WebSettings settings = webView.getSettings();
        settings.setNeedInitialFocus(false);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Hawk.get(HawkConfig.DEBUG_OPEN, false)) {
            settings.setBlockNetworkImage(false);
        } else {
            settings.setBlockNetworkImage(true);
        }
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        /* 添加webView配置 */
        //设置编码
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUserAgentString(webView.getSettings().getUserAgentString());
        // settings.setUserAgentString(ANDROID_UA);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return false;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return true;
            }
        });
        mSysWebClient = new SysWebClient();
        webView.setWebViewClient(mSysWebClient);
        webView.setBackgroundColor(Color.BLACK);
    }

    private class SysWebClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted( view,  url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            String click=sourceBean.getClickSelector();
            LOG.i("onPageFinished url:" + url);
            if(!click.isEmpty()){
                String selector;
                if(click.contains(";")){
                    if(!url.contains(click.split(";")[0]))return;
                    selector=click.split(";")[1];
                }else {
                    selector=click.trim();
                }
                String js="$(\""+ selector+"\").click();";
                mSysWebView.loadUrl("javascript:"+js);
            }
        }

        WebResourceResponse checkIsVideo(String url, HashMap<String, String> headers) {
            if (url.endsWith("/favicon.ico")) {
                if (url.startsWith("http://127.0.0.1")) {
                    return new WebResourceResponse("image/x-icon", "UTF-8", null);
                }
                return null;
            }

            boolean isFilter = VideoParseRuler.isFilter(webUrl, url);
            if (isFilter) {
                LOG.i( "shouldInterceptLoadRequest filter:" + url);
                return null;
            }

            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }

            if (!ad) {
                if (checkVideoFormat(url)) {
                    loadFoundVideoUrls.add(url);
                    loadFoundVideoUrlsHeader.put(url, headers);
                    LOG.i("loadFoundVideoUrl:" + url );
                    if (loadFoundCount.incrementAndGet() == 1) {
                        url = loadFoundVideoUrls.poll();
                        mHandler.removeMessages(100);
                        String cookie = CookieManager.getInstance().getCookie(url);
                        if(!TextUtils.isEmpty(cookie))headers.put("Cookie", " " + cookie);//携带cookie
                        playUrl(url, headers);
                        stopLoadWebView(false);
                    }
                }
            }

            return ad || loadFoundCount.get() > 0 ?
                    AdBlocker.createEmptyResource() :
                    null;
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return null;
        }

        @Nullable
        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            LOG.i("shouldInterceptRequest url:" + url);
            HashMap<String, String> webHeaders = new HashMap<>();
            Map<String, String> hds = request.getRequestHeaders();
            if (hds != null && hds.keySet().size() > 0) {
                for (String k : hds.keySet()) {
                    if (k.equalsIgnoreCase("user-agent")
                            || k.equalsIgnoreCase("referer")
                            || k.equalsIgnoreCase("origin")) {
                        webHeaders.put(k," " + hds.get(k));
                    }
                }
            }
            return checkIsVideo(url, webHeaders);
        }

        @Override
        public void onLoadResource(WebView webView, String url) {
            super.onLoadResource(webView, url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebViewX5(XWalkView webView) {
        if (webView == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = Hawk.get(HawkConfig.DEBUG_OPEN, false)
                ? new ViewGroup.LayoutParams(800, 400) :
                new ViewGroup.LayoutParams(1, 1);
        webView.setFocusable(false);
        webView.setFocusableInTouchMode(false);
        webView.clearFocus();
        webView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        addContentView(webView, layoutParams);
        /* 添加webView配置 */
        final XWalkSettings settings = webView.getSettings();
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);

        if (Hawk.get(HawkConfig.DEBUG_OPEN, false)) {
            settings.setBlockNetworkImage(false);
        } else {
            settings.setBlockNetworkImage(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(false);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // settings.setUserAgentString(ANDROID_UA);

        webView.setBackgroundColor(Color.BLACK);
        webView.setUIClient(new XWalkUIClient(webView) {
            @Override
            public boolean onConsoleMessage(XWalkView view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
                return false;
            }

            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                return true;
            }

            @Override
            public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                return true;
            }

            @Override
            public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
                return true;
            }
        });
        mX5WebClient = new XWalkWebClient(webView);
        webView.setResourceClient(mX5WebClient);
    }

    private class XWalkWebClient extends XWalkResourceClient {
        public XWalkWebClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
            super.onDocumentLoadedInFrame(view, frameId);
        }

        @Override
        public void onLoadStarted(XWalkView view, String url) {
            super.onLoadStarted(view, url);
        }

        @Override
        public void onLoadFinished(XWalkView view, String url) {
            super.onLoadFinished(view, url);
        }

        @Override
        public void onProgressChanged(XWalkView view, int progressInPercent) {
            super.onProgressChanged(view, progressInPercent);
        }

        @Override
        public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
            String url = request.getUrl().toString();
            LOG.i("shouldInterceptLoadRequest url:" + url);
            // suppress favicon requests as we don't display them anywhere
            if (url.endsWith("/favicon.ico")) {
                if (url.startsWith("http://127.0.0.1")) {
                    return createXWalkWebResourceResponse("image/x-icon", "UTF-8", null);
                }
                return null;
            }

            boolean isFilter = VideoParseRuler.isFilter(webUrl, url);
            if (isFilter) {
                LOG.i( "shouldInterceptLoadRequest filter:" + url);
                return null;
            }

            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            if (!ad ) {
                if (checkVideoFormat(url)) {
                    HashMap<String, String> webHeaders = new HashMap<>();
                    Map<String, String> hds = request.getRequestHeaders();
                    if (hds != null && hds.keySet().size() > 0) {
                        for (String k : hds.keySet()) {
                            if (k.equalsIgnoreCase("user-agent")
                                    || k.equalsIgnoreCase("referer")
                                    || k.equalsIgnoreCase("origin")) {
                                webHeaders.put(k," " + hds.get(k));
                            }
                        }
                    }
                    loadFoundVideoUrls.add(url);
                    loadFoundVideoUrlsHeader.put(url, webHeaders);
                    LOG.i("loadFoundVideoUrl:" + url );
                    if (loadFoundCount.incrementAndGet() == 1) {
                        mHandler.removeMessages(100);
                        url = loadFoundVideoUrls.poll();
                        String cookie = CookieManager.getInstance().getCookie(url);
                        if(!TextUtils.isEmpty(cookie))webHeaders.put("Cookie", " " + cookie);//携带cookie
                        playUrl(url, webHeaders);
                        stopLoadWebView(false);
                    }
                }
            }
            return ad || loadFoundCount.get() > 0 ?
                    createXWalkWebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes())) :
                    null;
        }

        @Override
        public boolean shouldOverrideUrlLoading(XWalkView view, String s) {
            return false;
        }

        @Override
        public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
            callback.onReceiveValue(true);
        }
    }

}