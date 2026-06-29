package com.github.tvbox.osc.player.controller;

import static com.github.tvbox.osc.base.baseActivity.getColorByAttrId;
import static xyz.doikki.videoplayer.util.PlayerUtils.stringForTime;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.bean.IJKCode;
import com.github.tvbox.osc.bean.ParseBean;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.server.RemoteServer;
import com.github.tvbox.osc.subtitle.widget.SimpleSubtitleView;
import com.github.tvbox.osc.ui.adapter.ParseAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.dialog.SelectDialoga;
import com.github.tvbox.osc.ui.dialog.SpeedPlayDialog;
import com.github.tvbox.osc.ui.view.ChoosePlayPopUp;
import com.github.tvbox.osc.ui.view.PlayerMoreFucPop;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.M3u8;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.ScreenUtils;
import com.github.tvbox.osc.util.SubtitleHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.PlayerUtils;

public class VodController extends BaseController {
    public VodController(@NonNull @NotNull Context context) {
        super(context);
        mHandlerCallback = msg -> {
            switch (msg.what) {
                case 1000:
                    mProgressRoot.setVisibility(VISIBLE);
                    break;
                case 1001:
                    mProgressRoot.setVisibility(GONE);
                    break;
                case 1002:
                    updateDanmuSearchUiBtn();
                    mBottomRoot.setVisibility(VISIBLE);
                    mTopRoot1.setVisibility(VISIBLE);
                    mTopRoot2.setVisibility(VISIBLE);
                    mPlayTitle.setVisibility(GONE);
                    showNowTime(true);
                    mPlayPauseTime.setVisibility(VISIBLE);
                    mBack.setVisibility(ScreenUtils.isTvOrBox(context) ? INVISIBLE : VISIBLE);

                    mNextBtn.requestFocus();
                    if (isKeyUp) {
                        isKeyUp = false;
                    }
                    break;
                case 1003:
                    mBottomRoot.setVisibility(GONE);
                    mTopRoot1.setVisibility(GONE);
                    mTopRoot2.setVisibility(GONE);
                    showNowTime(false);
                    mPlayPauseTime.setVisibility(VISIBLE);
                    mPlayLoadNetSpeed.setVisibility(View.GONE);
                    if (Hawk.get(HawkConfig.SHOW_SSPREVIEW, false)) {
                        mBack.setVisibility(VISIBLE);
                    } else {
                        mBack.setVisibility(GONE);
                    }
                    break;
                case 1004:
                    if (isInPlaybackState()) {
                        try {
                            float speed = (float) mPlayerConfig.getDouble("sp");
                            mControlWrapper.setSpeed(speed);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        mHandler.sendEmptyMessageDelayed(1004, 100);
                    break;
            }
        };
        Setprogressbar(mSeekBars,context);
        setProgressBar(mSeekBar,context);

    }

    SeekBar mSeekBar;
    SeekBar mSeekBars;
    TextView mCurrentTime;
    TextView mTotalTime;
    boolean mIsDragging;
    LinearLayout mProgressRoot;
    TextView mProgressText;
    ImageView mProgressIcon;
    LinearLayout mBottomRoot;
    LinearLayout mTopRoot1;
    LinearLayout mTopRoot2;
    LinearLayout mParseRoot;
    LinearLayout mPauseBtn;
    LinearLayout mBack;
    ImageView mPauseImg;
    TvRecyclerView mGridView;
    TextView mPlayTitle;
    TextView mPlayTitle1;
    TextView mPlayLoadNetSpeedRightTop;
    TextView mNextBtn;
    TextView mPreBtn;
    ImageView tvLogo;
    public static TextView mPlayerScaleBtn;
    public static TextView mPlayerSpeedBtn;
    public static TextView mPlayerBtn;
    public static TextView mPlayerIJKBtn;
    TextView mPlayerRetry;
    TextView mPlayrefresh;
    public static TextView mPlayerTimeStartBtn;
    public static boolean myHandless = false;
    public static TextView mPlayerTimeSkipBtn;
    TextView mPlayPauseTime;
    TextView mPlayLoadNetSpeed;
    TextView mVideoSize;
    public SimpleSubtitleView mSubtitleView;
    ImageView mZimuBtn;
    boolean showTime;
    boolean mIsFullScreen = false;
    ImageView mBackBimg;
    public static ImageView mAudioTrackBtn;
    public static TextView mLandscapePortraitBtn;
    private PlayerMoreFucPop mPlayerMoreFuc;
    private TextView mMiniProgressTextView;
    private long tempSeekProgress = 0;
    public static long PLAY_LAST_POSITION = 0;
    TextView mDanmuSettingBtn;
    private boolean hasDanmu = false;
    Handler myHandle;
    Runnable myRunnable;
    int myHandleSeconds = 6000;
    int videoPlayState = 0;
    TextView mDanmuSearchUiBtn;

    // ======================【记忆播放】======================
    private String currentVideoKey = "";

    public void setCurrentVideoKey(String key) {
        this.currentVideoKey = key;
    }

    public void saveProgress() {
        if (mControlWrapper == null || currentVideoKey.isEmpty()) return;
        long pos = mControlWrapper.getCurrentPosition();
        long duration = mControlWrapper.getDuration();
        if (duration > 0 && pos > 0 && pos < duration - 5000) {
            Hawk.put("progress_" + currentVideoKey, pos);
            // 缓存当前进度 给换源用
            tempSeekProgress = pos;
        }
    }
    // 换源前保存当前播放位置
    public void setTempSeekProgress(long progress){
        this.tempSeekProgress = progress;
    }
    public long getTempSeekProgress(){
        return tempSeekProgress;
    }
    public long getLastProgress() {
        if (currentVideoKey == null || currentVideoKey.isEmpty()) return 0;
        return Hawk.get("progress_" + currentVideoKey, 0L);
    }

    private void clearProgress() {
        if (currentVideoKey == null || currentVideoKey.isEmpty()) return;
        Hawk.delete("progress_" + currentVideoKey);
    }
    // ======================================================

    private void showNowTime(boolean forceShow) {
        handleLastedTime();
        if (forceShow) {
            tvLogo.setVisibility(View.VISIBLE);
            return;
        }
        mPlayPauseTime.setVisibility(View.VISIBLE);
        if (showTime) {
            tvLogo.setVisibility(View.VISIBLE);
        } else {
            tvLogo.setVisibility(View.GONE);
        }
    }

    private void handleLastedTime() {
        Date date = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        mPlayPauseTime.setText(timeFormat.format(date));
        if (mTopRoot1.getVisibility() == View.VISIBLE) {
            mPlayPauseTime.setAlpha(1);
            mMiniProgressTextView.setAlpha(1);
        } else {
            mPlayPauseTime.setAlpha(0.7f);
            mMiniProgressTextView.setAlpha(0.7f);
        }
    }

    private final Runnable myRunnable2 = new Runnable() {
        @Override
        public void run() {
            handleLastedTime();
            String speed = PlayerHelper.getDisplaySpeed(mControlWrapper.getTcpSpeed());
            mPlayLoadNetSpeedRightTop.setText(speed);
            mPlayLoadNetSpeed.setText(speed);
            String width = Integer.toString(mControlWrapper.getVideoSize()[0]);
            String height = Integer.toString(mControlWrapper.getVideoSize()[1]);
            mVideoSize.setText(width + " X " + height);
            mSeekBars.setVisibility(mShowMiniProgress ? View.VISIBLE : View.GONE);

            // ============== 核心修改在这里 ==============
            // 退出全屏时，直接强制隐藏，不读取 showTime
            if (!mIsFullScreen) {
                mMiniProgressTextView.setVisibility(View.GONE);
            } else {
                // 只有全屏时，才按配置显示
                boolean showTime = Hawk.get(HawkConfig.VIDEO_SHOW_TIME, false);
                mMiniProgressTextView.setVisibility(showTime ? View.VISIBLE : View.GONE);
            }

            // 继续循环定时
            mHandler.postDelayed(this, 1000);
        }
    };;
    public void setIsFullScreen(boolean isFullScreen) {
        mIsFullScreen = isFullScreen; // 这个状态必须更新

        if (mPlayPauseTime == null) return;

        if (!isFullScreen) {
            // 退出全屏
            mPlayPauseTime.setVisibility(View.GONE);
            tvLogo.setVisibility(View.GONE);
        } else {
            // 进入全屏
            mPlayPauseTime.setVisibility(View.VISIBLE);
            boolean showTime = Hawk.get(HawkConfig.VIDEO_SHOW_TIME, false);
            tvLogo.setVisibility(showTime ? View.VISIBLE : View.GONE);
        }

        // 主动触发一次 UI 刷新，立刻生效，不用等1秒
        mHandler.removeCallbacks(myRunnable2);
        mHandler.post(myRunnable2);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(myRunnable2);
        saveProgress();
    }

    @Override
    protected void initView() {
        super.initView();
        EventBus.getDefault().register(this);
        mCurrentTime = findViewById(R.id.curr_time);
        mTotalTime = findViewById(R.id.total_time);
        mPlayTitle = findViewById(R.id.tv_info_name);
        mPlayTitle1 = findViewById(R.id.tv_info_name1);
        mPlayLoadNetSpeedRightTop = findViewById(R.id.tv_play_load_net_speed_right_top);
        mSeekBar = findViewById(R.id.seekBar);
        mSeekBars = findViewById(R.id.seekBars);
        mProgressRoot = findViewById(R.id.tv_progress_container);
        mProgressIcon = findViewById(R.id.tv_progress_icon);
        mProgressText = findViewById(R.id.tv_progress_text);
        mBottomRoot = findViewById(R.id.bottom_container);
        mTopRoot1 = findViewById(R.id.tv_top_l_container);
        mTopRoot2 = findViewById(R.id.tv_top_r_container);
        mParseRoot = findViewById(R.id.parse_root);
        mGridView = findViewById(R.id.mGridView);
        tvLogo = findViewById(R.id.tvLogo);
        mPlayerRetry = findViewById(R.id.play_retry);
        mPlayrefresh = findViewById(R.id.play_refresh);
        mNextBtn = findViewById(R.id.play_next);
        mPreBtn = findViewById(R.id.play_pre);
        mPauseBtn = findViewById(R.id.play_pause);
        mPauseImg = findViewById(R.id.play_pauseImg);
        mPlayerScaleBtn = findViewById(R.id.play_scale);
        mPlayerScaleBtn.setVisibility(View.GONE);
        mPlayerSpeedBtn = findViewById(R.id.play_speed);
        mPlayerBtn = findViewById(R.id.play_player);
        mPlayerIJKBtn = findViewById(R.id.play_ijk);
        mMiniProgressTextView = findViewById(R.id.tiny_progress);
        mShowMiniProgress = Hawk.get(HawkConfig.HOME_PWET_TUMM, false);
        showTime = Hawk.get(HawkConfig.VIDEO_SHOW_TIME, false);
        mPlayerTimeStartBtn = findViewById(R.id.play_time_start);
        mPlayerTimeSkipBtn = findViewById(R.id.play_time_end);
        mPlayPauseTime = findViewById(R.id.tv_sys_time);
        mPlayLoadNetSpeed = findViewById(R.id.tv_play_load_net_speed);
        mVideoSize = findViewById(R.id.tv_videosize);
        mSubtitleView = findViewById(R.id.subtitle_view);
        mBack = findViewById(R.id.tvBackButton);
        mBackBimg = findViewById(R.id.tvBackBimg);
        mZimuBtn = findViewById(R.id.zimu_select);
        updateDanmuSearchUiBtn();
        mDanmuSettingBtn = findViewById(R.id.danmu_setting);
        mDanmuSearchUiBtn = findViewById(R.id.danmu_search_ui);
        mLandscapePortraitBtn = findViewById(R.id.landscape_portrait);
        TextView choosePlay = findViewById(R.id.choose_play);
        choosePlay.setOnClickListener(v -> {
            myHandless = true;
            choosePlay(v);
        });

        findViewById(R.id.more_fuc).setOnClickListener(v ->
                mPlayerMoreFuc.setOnItemClickListener(textView -> {
                            switch (textView.getId()) {
                                case R.id.audio_track_select:
                                    setAudioTrack();
                                    break;
                                case R.id.tvIjkCachePlay:
                                    toggleHawkConfig(HawkConfig.IJK_CACHE_PLAY, "IJK缓存开", "IJK缓存关", textView);
                                    break;
                                case R.id.tvPtst:
                                    toggleHawkConfig(HawkConfig.HOME_PTST_TUMM, "自动片头片尾开", "自动片头片尾关", textView);
                                    break;
                                case R.id.play_scale:
                                    showScaleDialog();
                                    break;
                                case R.id.time_show:
                                    toggleTimeShow(textView);
                                    break;
                                case R.id.tiny_progress:
                                    toggleMiniProgress(textView);
                                    break;
                            }
                        })
                        .setPopupGravity(Gravity.END)
                        .showPopupWindow());
        initSubtitleInfo();
        myHandle = new Handler();
        myRunnable = () -> hideBottom();
        mPlayPauseTime.post(() -> mHandler.post(myRunnable2));

        mSeekBars.setVisibility(mShowMiniProgress ? View.VISIBLE : View.GONE);
        mPlayPauseTime.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);
        Hawk.put(HawkConfig.SHOW_SSPREVIEW, false);

        // ======================【修复布局管理器】======================
        mGridView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // ============================================================

        ParseAdapter parseAdapter = new ParseAdapter();
        parseAdapter.setOnItemClickListener((adapter, view, position) -> {
            ParseBean parseBean = parseAdapter.getItem(position);
            int currentDefault = parseAdapter.getData().indexOf(ApiConfig.get().getDefaultParse());
            parseAdapter.notifyItemChanged(currentDefault);
            ApiConfig.get().setDefaultParse(parseBean);
            parseAdapter.notifyItemChanged(position);
            listener.changeParse(parseBean);
            hideBottom();
        });
        mGridView.setAdapter(parseAdapter);
        parseAdapter.setNewData(ApiConfig.get().getParseBeanList());
        mParseRoot.setVisibility(View.VISIBLE);
        mPlayPauseTime.setVisibility(View.VISIBLE);
        tvLogo.setVisibility(View.GONE);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (!fromUser) return;
                long duration = mControlWrapper.getDuration();
                long newPosition = (duration * progress) / seekBar.getMax();
                if (mCurrentTime != null)
                    mCurrentTime.setText(stringForTime((int) newPosition));
                mMiniProgressTextView.setText(stringForTime((int) newPosition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsDragging = true;
                mControlWrapper.stopProgress();
                mControlWrapper.stopFadeOut();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                long duration = mControlWrapper.getDuration();
                long newPosition = (duration * seekBar.getProgress()) / seekBar.getMax();
                mControlWrapper.seekTo((int) newPosition);
                mIsDragging = false;
                mControlWrapper.startProgress();
                mControlWrapper.startFadeOut();
            }
        });

        mSeekBars.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                long duration = mControlWrapper.getDuration();
                long newPosition = (duration * progress) / seekBar.getMax();
                if (mCurrentTime != null)
                    mCurrentTime.setText(stringForTime((int) newPosition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsDragging = true;
                mControlWrapper.stopProgress();
                mControlWrapper.stopFadeOut();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandle.removeCallbacks(myRunnable);
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                long duration = mControlWrapper.getDuration();
                long newPosition = (duration * seekBar.getProgress()) / seekBar.getMax();
                mControlWrapper.seekTo((int) newPosition);
                mIsDragging = false;
                mControlWrapper.startProgress();
                mControlWrapper.startFadeOut();
            }
        });

        mPlayerRetry.setOnClickListener(v -> {
            listener.replay(true);
            hideBottom();
        });

        mPlayrefresh.setOnClickListener(v -> {
            listener.replay(false);
            hideBottom();
        });

        mPauseBtn.setOnClickListener(v -> togglePlay());

        mNextBtn.setOnClickListener(v -> {
            listener.playNext(false);
            hideBottom();
        });

        mPreBtn.setOnClickListener(v -> {
            listener.playPre();
            hideBottom();
        });

        mBack.setOnClickListener(v -> {
            boolean showPreview = !Hawk.get(HawkConfig.SHOW_SSPREVIEW, false);
            Hawk.put(HawkConfig.SHOW_SSPREVIEW, showPreview);
            mBackBimg.setImageResource(showPreview ? R.drawable.ic_lock_alt : R.drawable.ic_lock_alt_open);
            if (showPreview) hideBottom();
            else showBottom();
        });

        mPlayerSpeedBtn.setOnClickListener(v -> {
            if (mActivity == null || mActivity.isFinishing()) return;
            double defaultSpeed = mPlayerConfig.optDouble("sp", 1.0);
            new SpeedPlayDialog().showSpeedSelectDialog(mActivity, defaultSpeed, value -> {
                setSpeedVideoView((TextView) v, value);
                return null;
            });
        });

        mPlayerSpeedBtn.setOnLongClickListener(v -> {
            try {
                mPlayerConfig.put("sp", 1.0f);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
                mControlWrapper.setSpeed(1.0f);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        mPlayerBtn.setOnClickListener(v -> {
            try {
                int playerType = mPlayerConfig.getInt("pl");
                ArrayList<Integer> players = PlayerHelper.getExistPlayerTypes();
                int defaultPos = 0;
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i) == playerType) defaultPos = i;
                }
                SelectDialoga<Integer> dialog = new SelectDialoga<>(mActivity);
                dialog.setTip("请选择播放器");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        try {
                            int newType = players.get(pos);
                            mPlayerConfig.put("pl", newType);
                            updatePlayerCfgView();
                            listener.updatePlayerCfg();
                            listener.replay(false);
                            hideBottom();
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public String getDisplay(Integer val) {
//                         直接通过 播放器类型 获取名称，不需要走 list.get()
                        return PlayerHelper.getPlayerName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
                        return oldItem.equals(newItem);
                    }
                    @Override
                    public boolean areContentsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
                        return oldItem.equals(newItem);
                    }
                }, players, defaultPos);
                dialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mPlayerIJKBtn.setOnClickListener(v -> {
            try {
                String ijk = mPlayerConfig.getString("ijk");
                List<IJKCode> codecs = ApiConfig.get().getIjkCodes();
                for (int i = 0; i < codecs.size(); i++) {
                    if (ijk.equals(codecs.get(i).getName())) {
                        ijk = codecs.get((i + 1) % codecs.size()).getName();
                        break;
                    }
                }
                mPlayerConfig.put("ijk", ijk);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
                listener.replay(false);
                hideBottom();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mPlayerTimeStartBtn.setOnClickListener(v -> {
            try {
                int current = (int) mControlWrapper.getCurrentPosition();
                int duration = (int) mControlWrapper.getDuration();
                if (current > duration / 2) return;
                mPlayerConfig.put("st", current / 1000);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mPlayerTimeStartBtn.setOnLongClickListener(v -> {
            try {
                mPlayerConfig.put("st", 0);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        mPlayerTimeSkipBtn.setOnClickListener(v -> {
            try {
                int current = (int) mControlWrapper.getCurrentPosition();
                int duration = (int) mControlWrapper.getDuration();
                if (current < duration / 2) return;
                mPlayerConfig.put("et", (duration - current) / 1000);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        mPlayerTimeSkipBtn.setOnLongClickListener(v -> {
            try {
                mPlayerConfig.put("et", 0);
                updatePlayerCfgView();
                listener.updatePlayerCfg();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        mZimuBtn.setOnClickListener(v -> {
            listener.selectSubtitle();
            hideBottom();
        });

        mZimuBtn.setOnLongClickListener(v -> {
            mSubtitleView.setVisibility(View.GONE);
            mSubtitleView.destroy();
            mSubtitleView.clearSubtitleCache();
            mSubtitleView.isInternal = false;
            Toast.makeText(getContext(), "字幕已关闭", Toast.LENGTH_SHORT).show();
            return true;
        });

        mLandscapePortraitBtn.setOnClickListener(v -> {
            setLandscapePortrait();
            hideBottom();
        });
        mDanmuSettingBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                listener.showDanmuSetting();
            }
        });
        mDanmuSearchUiBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.searchDanmuUi(false);
                hideBottom();
            }
        });
        mNextBtn.setNextFocusLeftId(R.id.play_time_start);
    }

    private void setSpeedVideoView(TextView textView, double speed) {
        try {
            mPlayerConfig.put("sp", speed);
            updatePlayerCfgView();
            textView.setText("x" + speed);
            listener.updatePlayerCfg();
            mControlWrapper.setSpeed((float) speed);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAudioTrack() {
        listener.selectAudioTrack();
        hideBottom();
    }

    private void choosePlay(View view) {
        ChoosePlayPopUp playPopUp = new ChoosePlayPopUp(mActivity, 8);
        playPopUp.setPopupGravity(Gravity.TOP).showPopupWindow(view);
    }

    private void choosePlay() {
        ChoosePlayPopUp playPopUp = new ChoosePlayPopUp(mActivity, 9);
        playPopUp.setPopupGravity(Gravity.BOTTOM).showPopupWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_OPENING_AND_ENDING) {
            if (event.obj == null) return;
            Openingcredits((String) event.obj);
        }
    }

    public static void Openingcredits(String body) {
        if (ApiConfig.get().Steturl.isEmpty() || body.isEmpty()) return;
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            if (Hawk.get(HawkConfig.HOME_PTST_TUMM, false)) {
                mPlayerConfig.put("st", json.get("stime").getAsString());
                mPlayerConfig.put("et", json.get("etime").getAsString());
                updatePlayerCfgView();
                listener.updatePlayerCfg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideLiveAboutBtn() {
        if (mControlWrapper != null && mControlWrapper.getDuration() == 0) {
            mPlayerSpeedBtn.setVisibility(View.GONE);
            mPlayerTimeStartBtn.setVisibility(View.GONE);
            mPlayerTimeSkipBtn.setVisibility(View.GONE);
            mNextBtn.setNextFocusLeftId(R.id.zimu_select);
        } else {
            mPlayerSpeedBtn.setVisibility(View.VISIBLE);
            mPlayerTimeStartBtn.setVisibility(View.VISIBLE);
            mPlayerTimeSkipBtn.setVisibility(View.VISIBLE);
            mNextBtn.setNextFocusLeftId(R.id.play_time_start);
        }
    }

    public void initLandscapePortraitBtnInfo() {
        if (mControlWrapper != null && mActivity != null) {
            int w = mControlWrapper.getVideoSize()[0];
            int h = mControlWrapper.getVideoSize()[1];
            double screen = ScreenUtils.getSqrt(mActivity);
            if (screen < 10 && w < h) {
                mLandscapePortraitBtn.setVisibility(View.VISIBLE);
                mLandscapePortraitBtn.setText("竖屏");
            }
        }
    }

    void setLandscapePortrait() {
        int ori = mActivity.getRequestedOrientation();
        if (ori == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || ori == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            mLandscapePortraitBtn.setText("横屏");
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            mLandscapePortraitBtn.setText("竖屏");
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    void initSubtitleInfo() {
        int size = SubtitleHelper.getTextSize(mActivity);
        mSubtitleView.setTextSize(size);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.player_vod_control_view;
    }

    public void showParse(boolean userJxList) {
        mParseRoot.setVisibility(userJxList ? View.VISIBLE : View.GONE);
    }

    private static JSONObject mPlayerConfig = null;

    public void setPlayerConfig(JSONObject playerCfg) {
        this.mPlayerConfig = playerCfg;
        updatePlayerCfgView();
        mPlayerMoreFuc = new PlayerMoreFucPop(getContext(), mPlayerConfig);
    }

    public static void updatePlayerCfgView(){
        int pl = mPlayerConfig.optInt("pl", 0);
        mPlayerBtn.setText(PlayerHelper.getPlayerName(pl));
        mPlayerScaleBtn.setText(PlayerHelper.getScaleName(mPlayerConfig.optInt("sc", 0)));
        mPlayerIJKBtn.setText(mPlayerConfig.optString("ijk", "默认"));
        mPlayerIJKBtn.setVisibility(pl == 1 ? View.VISIBLE : View.GONE);
        mPlayerSpeedBtn.setText(mPlayerConfig.optDouble("sp", 1.0) + "X");
        mPlayerTimeStartBtn.setText(PlayerUtils.stringForTime(mPlayerConfig.optInt("st", 0) * 1000));
        mPlayerTimeSkipBtn.setText(PlayerUtils.stringForTime(mPlayerConfig.optInt("et", 0) * 1000));
    }
    private String getPlayerShortName(int playerType) {
        String playerName = PlayerHelper.getPlayerName(playerType);
        return playerName;
    }

    private String getCodecShortName(String codecName) {
        if ("硬解码".equals(codecName)) {
            return "硬解";
        }
        if ("软解码".equals(codecName)) {
            return "软解";
        }
        return codecName;
    }

    public void setTitle(String playTitleInfo) {
        mPlayTitle.setText(playTitleInfo);
        mPlayTitle1.setText(playTitleInfo);
    }

    public void setUrlTitle(String playTitleInfo) {
        mPlayTitle.setText(playTitleInfo);
    }

    public void resetSpeed() {
        skipEnd = true;
        mHandler.removeMessages(1004);
        mHandler.sendEmptyMessageDelayed(1004, 100);
    }
    public void setHasDanmu(boolean hasDanmu) {
        this.hasDanmu = hasDanmu;
        updateDanmuBtn();
    }

    public void updateDanmuBtn() {
       // if (mDanmuSettingBtn == null) return;
        mDanmuSettingBtn.setVisibility(VISIBLE);
    }
    public void updateDanmuSearchUiBtn() {
        if (mDanmuSearchUiBtn == null) return;
        mDanmuSearchUiBtn.setVisibility(VISIBLE);
    }
    public interface VodControlListener {
        void playNext(boolean rmProgress);
        void playPre();
        void openVideo();
        void prepared();
        void changeParse(ParseBean pb);
        void updatePlayerCfg();
        void replay(boolean replay);
        void errReplay();
        void selectSubtitle();
        void showDanmuSetting();

        void searchDanmuUi(boolean longClick);

        void startPlayUrl(String url, HashMap<String, String> headers);
        void selectAudioTrack();

    }

    private static VodControlListener listener;

    public void setListener(VodControlListener listener) {
        this.listener = listener;
    }

    private boolean skipEnd = true;

    @Override
    protected void setProgress(int duration, int position) {
        if (mIsDragging) return;
        super.setProgress(duration, position);

        if (skipEnd && position > 0 && duration > 0) {
            try {
                int et = mPlayerConfig.getInt("et");
                if (et > 0 && position + et * 1000 >= duration) {
                    skipEnd = false;
                    listener.playNext(true);
                    clearProgress();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String curr = PlayerUtils.stringForTime(position);
        String total = PlayerUtils.stringForTime(duration);
        mCurrentTime.setText(curr);
        mTotalTime.setText(total);
        mMiniProgressTextView.setText(curr + "/" + total);
      // mMiniProgressTextView.setVisibility(mShowMiniProgress ? View.VISIBLE : View.GONE);

        if (duration > 0) {
            mSeekBar.setEnabled(true);
            mSeekBar.setProgress((int) (position * 1.0 / duration * mSeekBar.getMax()));
            mSeekBars.setEnabled(true);
            mSeekBars.setProgress((int) (position * 1.0 / duration * mSeekBars.getMax()));
        } else {
            mSeekBar.setEnabled(false);
            mSeekBars.setEnabled(false);
        }

        int buffer = mControlWrapper.getBufferedPercentage();
        if (buffer >= 95) {
            mSeekBar.setSecondaryProgress(mSeekBar.getMax());
            mSeekBars.setSecondaryProgress(mSeekBars.getMax());
        } else {
            mSeekBar.setSecondaryProgress(buffer * 10);
            mSeekBars.setSecondaryProgress(buffer * 10);
        }
    }

    private boolean simSlideStart = false;
    private int simSeekPosition = 0;
    private long simSlideOffset = 0;

    public void tvSlideStop() {
        if (!simSlideStart) return;
        mControlWrapper.seekTo(simSeekPosition);
        if (!mControlWrapper.isPlaying()) mControlWrapper.start();
        simSlideStart = false;
        simSeekPosition = 0;
        simSlideOffset = 0;
    }

    public void tvSlideStart(int dir) {
        int duration = (int) mControlWrapper.getDuration();
        if (duration <= 0) return;
        if (!simSlideStart) simSlideStart = true;
        simSlideOffset += 10000L * dir;
        int curr = (int) mControlWrapper.getCurrentPosition();
        int pos = (int) (curr + simSlideOffset);
        pos = Math.max(0, Math.min(pos, duration));
        updateSeekUI(curr, pos, duration);
        simSeekPosition = pos;
    }

    @Override
    protected void updateSeekUI(int curr, int seekTo, int duration) {
        super.updateSeekUI(curr, seekTo, duration);
        mProgressIcon.setImageResource(seekTo > curr ? R.drawable.play_ffwd : R.drawable.play_rewind);
        mProgressText.setText(PlayerUtils.stringForTime(seekTo) + " / " + PlayerUtils.stringForTime(duration));
        mHandler.sendEmptyMessage(1000);
        mHandler.removeMessages(1001);
        mHandler.sendEmptyMessageDelayed(1001, 1000);
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        videoPlayState = playState;
        switch (playState) {
            case VideoView.STATE_IDLE:
                break;
            case VideoView.STATE_PLAYING:
                mPauseImg.setImageResource(R.drawable.v_pause);
                initLandscapePortraitBtnInfo();
                startProgress();
                break;
            case VideoView.STATE_PAUSED:
                mPauseImg.setImageResource(R.drawable.v_play);
                break;
            case VideoView.STATE_ERROR:
                listener.errReplay();
                break;
            case VideoView.STATE_PREPARED:
                mPlayLoadNetSpeed.setVisibility(GONE);
                hideLiveAboutBtn();
                listener.prepared();
                break;
            case VideoView.STATE_BUFFERED:
                mPlayLoadNetSpeed.setVisibility(View.GONE);
                break;
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_BUFFERING:
                if(mProgressRoot.getVisibility()==GONE)mPlayLoadNetSpeed.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                listener.playNext(true);
                clearProgress();
                break;
        }
    }

    private boolean isBottomVisible() {
        return mBottomRoot.getVisibility() == View.VISIBLE;
    }

    void showBottom() {
        mHandler.removeMessages(1003);
        mHandler.sendEmptyMessage(1002);
    }

    void hideBottom() {
        mHandler.removeMessages(1002);
        mHandler.sendEmptyMessage(1003);
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        myHandle.removeCallbacks(myRunnable);
        if (super.onKeyEvent(event)) return true;
        int key = event.getKeyCode();
        int action = event.getAction();
        if (isBottomVisible()) {
            myHandle.postDelayed(myRunnable, myHandleSeconds);
            return super.dispatchKeyEvent(event);
        }
        boolean canPlay = isInPlaybackState();
        if (action == KeyEvent.ACTION_DOWN) {
            if (key == KeyEvent.KEYCODE_DPAD_RIGHT || key == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (canPlay) {
                    tvSlideStart(key == KeyEvent.KEYCODE_DPAD_RIGHT ? 1 : -1);
                    return true;
                }
            } else if (key == KeyEvent.KEYCODE_DPAD_CENTER || key == KeyEvent.KEYCODE_ENTER) {
                if (canPlay) {
                    togglePlay();
                    return true;
                }
            } else if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
                showBottom();
                myHandle.postDelayed(myRunnable, myHandleSeconds);
                return true;
            } else if (key == KeyEvent.KEYCODE_MENU) {
                choosePlay();
                return true;
            }
        } else if (action == KeyEvent.ACTION_UP) {
            if (key == KeyEvent.KEYCODE_DPAD_RIGHT || key == KeyEvent.KEYCODE_DPAD_LEFT) {
                tvSlideStop();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean fromLongPress;
    private float speed_old = 1.0f;

    @Override
    public void onLongPress(MotionEvent e) {
        if (videoPlayState != VideoView.STATE_PAUSED) {
            fromLongPress = true;
            try {
                speed_old = (float) mPlayerConfig.getDouble("sp");
                mControlWrapper.setSpeed(3.0f);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP && fromLongPress) {
            fromLongPress = false;
            mControlWrapper.setSpeed(speed_old);
        }
        return super.onTouchEvent(e);
    }

    public void Setprogressbark(SeekBar ars, Context context) {
        Resources res = context.getResources();
        LayerDrawable progressDrawable = (LayerDrawable) res.getDrawable(R.drawable.shape_player_control_vod_seek_thumba);
        ClipDrawable proDrawable = new ClipDrawable(new ColorDrawable(getColorByAttrId(context, R.attr.color_theme_80)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        progressDrawable.setDrawableByLayerId(android.R.id.progress, proDrawable);
        Rect bounds = ars.getProgressDrawable().getBounds();
        ars.setProgressDrawable(progressDrawable);
        ars.getProgressDrawable().setBounds(bounds);
    }

    public void Setprogressbar(SeekBar ars, Context context) {
        LayerDrawable layerDrawable = (LayerDrawable) ars.getProgressDrawable();
        ClipDrawable clipDrawable = (ClipDrawable) layerDrawable.findDrawableByLayerId(android.R.id.progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            clipDrawable.setDrawable(new ColorDrawable(getColorByAttrId(context, R.attr.color_theme_80)));
        } else {
            try {
                Method setDrawableMethod = ClipDrawable.class.getMethod("setDrawable", Drawable.class);
                setDrawableMethod.invoke(clipDrawable, new ColorDrawable(getColorByAttrId(context, R.attr.color_theme_80)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ars.setProgressDrawable(clipDrawable);
    }

    public void setProgressBar(SeekBar seekBar, Context context) {
        Resources res = context.getResources();
        Drawable background = new ColorDrawable(context.getResources().getColor(R.color.color_22FFFFFF));
        ClipDrawable secondary = new ClipDrawable(new ColorDrawable(getColorByAttrId(context, R.attr.color_theme_90)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        ClipDrawable progress = new ClipDrawable(new ColorDrawable(getColorByAttrId(context, R.attr.color_theme_80)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        LayerDrawable layer = new LayerDrawable(new Drawable[]{background, secondary, progress});
        layer.setId(0, android.R.id.background);
        layer.setId(1, android.R.id.secondaryProgress);
        layer.setId(2, android.R.id.progress);
        seekBar.setProgressDrawable(layer);
        GradientDrawable thumb = new GradientDrawable();
        thumb.setShape(GradientDrawable.OVAL);
        thumb.setColor(getColorByAttrId(context, R.attr.color_theme_80));
        thumb.setStroke(4, getColorByAttrId(context, R.attr.color_theme_h));
        thumb.setSize(25, 25);
        seekBar.setThumb(thumb);
    }

    public static void customizeSeekBar(SeekBar seekBar, Context context) {
        GradientDrawable thumb = new GradientDrawable();
        thumb.setShape(GradientDrawable.OVAL);
        thumb.setColor(Color.WHITE);
        thumb.setStroke(4, Color.parseColor("#FFF68E33"));
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, context.getResources().getDisplayMetrics());
        thumb.setSize(size, size);
        GradientDrawable progress = new GradientDrawable();
        progress.setColor(Color.parseColor("#FFF68E33"));
        GradientDrawable track = new GradientDrawable();
        track.setColor(Color.LTGRAY);
        LayerDrawable layer = new LayerDrawable(new Drawable[]{track, progress});
        seekBar.setProgressDrawable(layer);
        seekBar.setThumb(thumb);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        myHandle.removeCallbacks(myRunnable);
        if (!isBottomVisible()) {
            showBottom();
            myHandle.postDelayed(myRunnable, myHandleSeconds);
        } else {
            hideBottom();
        }
        return true;
    }

    private void toggleHawkConfig(String key, String on, String off, TextView tv) {
        boolean b = Hawk.get(key, false);
        Hawk.put(key, !b);
        tv.setText(b ? off : on);
    }

    private void showScaleDialog() {
        try {
            int sc = mPlayerConfig.getInt("sc");
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 6; i++) list.add(i);
            SelectDialoga<Integer> dialog = new SelectDialoga<>(mActivity);
            dialog.setTip("请选择画面比例");
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                @Override
                public void click(Integer value, int pos) {
                    try {
                        mPlayerConfig.put("sc", value);
                        mControlWrapper.setScreenScaleType(value);
                        updatePlayerCfgView();
                        listener.updatePlayerCfg();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public String getDisplay(Integer val) {
                    return PlayerHelper.getScaleName(val);
                }
            }, new DiffUtil.ItemCallback<Integer>() {
                @Override
                public boolean areItemsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
                    return oldItem.equals(newItem);
                }
                @Override
                public boolean areContentsTheSame(@NonNull Integer oldItem, @NonNull Integer newItem) {
                    return oldItem.equals(newItem);
                }
            }, list, sc);
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toggleTimeShow(TextView textView) {
        showTime = !showTime;
        Hawk.put(HawkConfig.VIDEO_SHOW_TIME, showTime);
        textView.setText(showTime ? "屏显开" : "屏显关");
        tvLogo.setVisibility(showTime ? View.VISIBLE : View.GONE);

        // 【唯一修改】延迟 100ms 再刷新进度，避开弹窗重绘，彻底不闪
        textView.postDelayed(() -> {
            mControlWrapper.startProgress();
        }, 100);
    }

    private void toggleMiniProgress(TextView textView) {
        mShowMiniProgress = !mShowMiniProgress;
        textView.setText(mShowMiniProgress ? "迷你进度开" : "迷你进度关");
        Hawk.put(HawkConfig.HOME_PWET_TUMM, mShowMiniProgress);
        mSeekBars.setVisibility(mShowMiniProgress ? View.VISIBLE : View.GONE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected void destroy() {
        super.destroy();
        myHandle.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onBackPressed() {
        if (super.onBackPressed()) {
            EventBus.getDefault().unregister(this);
            return true;
        }
        if (isBottomVisible()) {
            hideBottom();
            return true;
        }
        return Hawk.get(HawkConfig.SHOW_SSPREVIEW, false);
    }

    private boolean isPaused = false;
    private boolean isKeyUp = false;
    private boolean mShowMiniProgress;

    public void playM3u8(final String url, final HashMap<String, String> headers) {
        if(url.contains("url=")){
            listener.startPlayUrl(url, headers);
            return;
        }
        OkGo.getInstance().cancelTag("m3u8-1");
        OkGo.getInstance().cancelTag("m3u8-2");
        final HttpHeaders h = new HttpHeaders();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                h.put(entry.getKey(), entry.getValue());
            }
        }

        setCurrentVideoKey(url);

        OkGo.<String>get(url).tag("m3u8-1").headers(h).execute(new AbsCallback<String>() {
            @Override
            public void onSuccess(Response<String> response) {
                String content = response.body();
                if (!content.startsWith("#EXTM3U")) {
                    listener.startPlayUrl(url, headers);
                    return;
                }
                String forward = extractForwardUrl(url, content);
                if (forward.isEmpty()) {
                    processM3u8Content(url, content, headers);
                } else {
                    fetchAndProcessForwardUrl(forward, headers, h, url);
                }
            }
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return response.body().string();
            }
            @Override
            public void onError(Response<String> response) {
                listener.startPlayUrl(url, headers);
            }
        });
    }

    private String extractForwardUrl(String base, String content) {
        String[] lines = content.split("\\r?\\n", 50);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("#EXT-X-STREAM-INF")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String target = lines[j].trim();
                    if (!target.startsWith("#") && (target.endsWith(".m3u8") || target.contains(".m3u8?"))) {
                        try {
                            return new URL(new URL(base), target).toString();
                        } catch (MalformedURLException e) {
                            return target;
                        }
                    }
                }
            }
        }
        return "";
    }

    private void processM3u8Content(String url, String content, HashMap<String, String> headers) {
        String base = url.substring(0, url.lastIndexOf('/') + 1);
        RemoteServer.m3u8Content = M3u8.purify(base, content);
        if (RemoteServer.m3u8Content == null) {
            listener.startPlayUrl(url, headers);
        } else {
            listener.startPlayUrl(ControlManager.get().getAddress(true) + "proxyM3u8", headers);
            Toast.makeText(getContext(), "已移除广告 " + M3u8.currentAdCount + " 条", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAndProcessForwardUrl(String forward, HashMap<String, String> headers, HttpHeaders h, String fallback) {
        OkGo.<String>get(forward).tag("m3u8-2").headers(h).execute(new AbsCallback<String>() {
            @Override
            public void onSuccess(Response<String> response) {
                processM3u8Content(forward, response.body(), headers);
            }
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return response.body().string();
            }
            @Override
            public void onError(Response<String> response) {
                listener.startPlayUrl(fallback, headers);
            }
        });
    }
}