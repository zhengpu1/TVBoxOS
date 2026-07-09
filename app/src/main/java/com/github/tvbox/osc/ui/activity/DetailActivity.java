package com.github.tvbox.osc.ui.activity;

import static com.blankj.utilcode.util.SnackbarUtils.dismiss;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.base.baseActivity;
import com.github.tvbox.osc.bean.AbsXml;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.cache.CacheManager;
import com.github.tvbox.osc.cache.RoomDataManger;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.picasso.RoundTransformation;
import com.github.tvbox.osc.ui.adapter.HomedeVodAdapter;
import com.github.tvbox.osc.ui.adapter.QuickSearchAdapter;
import com.github.tvbox.osc.ui.adapter.SeriesAdapter;
import com.github.tvbox.osc.ui.adapter.SeriesFlagAdapter;
import com.github.tvbox.osc.ui.dialog.DevDialog;
import com.github.tvbox.osc.ui.dialog.QuickSearchDialog;
import com.github.tvbox.osc.ui.fragment.PlayFragment;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.EpisodeGroupHelper;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.GridSpaceDecoration;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.HistoryHelper;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.util.RemoteConfig;
import com.github.tvbox.osc.util.SearchHelper;
import com.github.tvbox.osc.util.SubtitleHelper;
import com.github.tvbox.osc.util.TVInstance;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.jessyan.autosize.utils.AutoSizeUtils;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * @author pj567
 * @date :2020/12/22
 * &#064;description:
 */

public class DetailActivity extends baseActivity {
    private FragmentContainerView llPlayerFragmentContainer;
    private View llPlayerFragmentContainerBlock;
    private PlayFragment playFragment = null;
    private ImageView ivThumb;
    private ImageView ivThumbs;
    private ImageView ivThumbsK;
    private TextView tvName;
    private TextView tvYear;
    private TextView tvSite;
    private TextView tvArea;
    private TextView tvWeekday;
    private LinearLayout tvSeriesGroup;
    private TextView tvLang;
    private TextView tvType;
    private TextView tvActor;
    private LinearLayout tvWad;
    private TextView tvScore;
    private TextView tvDouban;
    private TextView tvWriter;
    private TextView tvNote;
    private TextView tvDirector;
    private TextView tvDes;
    private LinearLayout tvPlay;
    private LinearLayout tvSort;
    private ImageView tvSort1;
    private TextView tvSort2;
    private LinearLayout tvPush;
    private TextView tvPushs;
    private TextView tvPlays;
    public String firstsourceKey;
    private LinearLayout tvCollect;
    private int GroupIndex = 0;
    public static int GroupCountt;
    public static int insfCount;
    private LinearLayout llaSite;
    private ImageView tvCollect1;
    private TextView tvCollect2;
    private TextView tvWntj;
    private TextView tvWntjs;
    private TvRecyclerView mGridViewFlag;
    private TvRecyclerView mNumberClassification;
    private TvRecyclerView mGridView;
    private LinearLayout mEmptyPlayList;
    private SourceViewModel sourceViewModel;
    public static Movie.Video mVideo;
    private TextView tvSeriesSort;
    public VodInfo vodInfo;
    private List<List<VodInfo.VodSeries>> uu;
    private SeriesFlagAdapter seriesFlagAdapter;
    public static SeriesFlagAdapter seriesFlagAdaptera;
    private SeriesFlagAdapter mClassifyAdapter;//集数分类adapter
    private BaseQuickAdapter<String, BaseViewHolder> seriesGroupAdapter;
    private SeriesAdapter seriesAdapter;
    public String vodId;
    public String sourceKey;
    public static boolean seriesSelect = false;
    public static boolean seriesSelectf = false;
    private HomedeVodAdapter homedeVodAdapter;
    private QuickSearchAdapter searchAdapter;
    public static TvRecyclerView tvHotList3;
    public static TvRecyclerView tvHotList5;
    private V7GridLayoutManager mGridViewLayoutMgr = null;
    private HashMap<String, String> mCheckSources = null;
    private LinkedHashMap<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>> mapData = new LinkedHashMap<>();//分组
    boolean showPreview = Hawk.get(HawkConfig.SHOW_PREVIEW, true);
    private final ArrayList<String> seriesGroupOptions = new ArrayList<>();

    private int thread = Integer.parseInt(HistoryHelper.getHistoryNumNamex(Hawk.get(HawkConfig.XIANC_NUM, 0)));
    private int LOCK_PLAY_INDEX  = 0; // 类顶部声明
    public static int mCurrentPlayIndex = 0;
    public static boolean isReverse;
    public static int offsets;
    public static boolean isSortToggle = false;
    @Override
    protected int getLayoutResID() {
        return R.layout.activity_detail;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        initView();
        initViewModel();
        initData();
    }
    private String preFlag = "";

    private void initCheckedSourcesForSearch() {
        mCheckSources = SearchHelper.getSourcesForSearch();
    }
    private int maxRetryCount = 10;        // 最大换源次数a
    private int maxRetryCount1 = 30; // 最大换源次数
    private int currentRetryCount = 0;     // 当前换源次数
    public static int currentRetryCount1 = 0;     // 当前换源次数
    private boolean isAutoSwitching = false; // 是否正在换源中
    private Handler autoSwitchHandler = new Handler(Looper.getMainLooper());
    private static final long RETRY_DELAY = 1500; // 重试延迟1.5秒
    public static int TEMP_PLAY_INDEX = 0;
    @SuppressLint("WrongViewCast")
    private void initView() {
        LinearLayout llLayout = findViewById(R.id.llLayout);
        View llPlayerPlace = findViewById(R.id.previewPlayerPlace);
        llPlayerFragmentContainer = findViewById(R.id.previewPlayer);
        llPlayerFragmentContainerBlock = findViewById(R.id.previewPlayerBlock);
        ivThumb = findViewById(R.id.ivThumb);
        ivThumbs = findViewById(R.id.ivThumbs);
        ivThumbsK = findViewById(R.id.ivThumbsK);
        llPlayerPlace.setVisibility(showPreview ? View.VISIBLE : View.GONE);
        ivThumb.setVisibility(!showPreview ? View.VISIBLE : View.GONE);
        tvName = findViewById(R.id.tvName);
        tvYear = findViewById(R.id.tvYear);
        tvSite = findViewById(R.id.tvSite);
        tvArea = findViewById(R.id.tvArea);
        tvWeekday = findViewById(R.id.tvWeekday);
        tvSeriesSort = findViewById(R.id.mSeriesSortTv);
        tvLang = findViewById(R.id.tvLang);
        tvType = findViewById(R.id.tvType);
        tvWntj = findViewById(R.id.tvWntj);
        tvWntjs = findViewById(R.id.tvWntjs);
        tvScore = findViewById(R.id.tvScore);
        tvDouban = findViewById(R.id.tvDouban);
        tvWriter = findViewById(R.id.tvWriter);
        tvNote = findViewById(R.id.tvNote);
        tvActor = findViewById(R.id.tvActor);
        tvWad = findViewById(R.id.tvWad);
        tvDirector = findViewById(R.id.tvDirector);
        tvDes = findViewById(R.id.tvDes);
        tvPlay = findViewById(R.id.tvPlay);
        tvSort = findViewById(R.id.tvSort);
        tvSort1 = findViewById(R.id.tvSort1);
        tvSort2 = findViewById(R.id.tvSort2);
        tvPushs = findViewById(R.id.tvPushs);
        tvPush = findViewById(R.id.tvPush);
        tvPush.setVisibility(View.VISIBLE);
        tvCollect = findViewById(R.id.tvCollect);
        tvCollect1 = findViewById(R.id.tvCollect1);
        tvCollect2 = findViewById(R.id.tvCollect2);
        tvPlays = findViewById(R.id.tvPlays);
        llaSite = findViewById(R.id.llaSite);
        mEmptyPlayList = findViewById(R.id.mEmptyPlaylist);
        mGridView = findViewById(R.id.mGridViewk);
        mGridView.setHasFixedSize(true);
        tvHotList5 = findViewById(R.id.tvHotList5);
        tvHotList5.setLayoutManager(new V7GridLayoutManager(this.mContext, 5));
        homedeVodAdapter = new HomedeVodAdapter();
        searchAdapter = new QuickSearchAdapter();
        mGridViewLayoutMgr = new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 10 : 11);
        mGridView.setLayoutManager(mGridViewLayoutMgr);
        int space = getResources().getDimensionPixelSize(R.dimen.vs_12);
        mGridView.addItemDecoration(new GridSpaceDecoration(space));
        seriesAdapter = new SeriesAdapter();
        mGridView.setAdapter(seriesAdapter);
        mGridView.setFocusable(true);
        mGridView.setFocusableInTouchMode(false);
        mGridView.setScrollBarFadeDuration(500);
        mNumberClassification = findViewById(R.id.mNumberClassification);
        mNumberClassification.setHasFixedSize(true);
        mNumberClassification.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        mClassifyAdapter = new SeriesFlagAdapter(mContext);
        mNumberClassification.setAdapter(mClassifyAdapter);
        mGridViewFlag = findViewById(R.id.mGridViewFlag);
        mGridViewFlag.setHasFixedSize(true);
        mGridViewFlag.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        seriesFlagAdapter = new SeriesFlagAdapter(mContext);
        seriesFlagAdaptera =  new SeriesFlagAdapter(mContext);
        mGridViewFlag.setAdapter(seriesFlagAdapter);
        tvHotList5.setAdapter(homedeVodAdapter);
        tvHotList5.setVisibility(View.VISIBLE);
        tvHotList3 = findViewById(R.id.tvHotList3);
        tvWeekday.setVisibility(View.GONE);
        tvPlay.setVisibility(View.VISIBLE);
        tvHotList3.setVisibility(View.GONE);
        tvHotList3.setAdapter(searchAdapter);
        tvWntj.setVisibility(View.GONE);
        tvHotList3.setVisibility(View.GONE);
        tvSeriesSort.setVisibility(View.GONE);
        tvSeriesGroup = findViewById(R.id.mSeriesGroupTv);
        // 初始化视图
        initViews();
        // 设置点击事件

        setClickListeners();
       // vodInfo.isReverse = false;
        preFlag = "";
        if (showPreview) {
            playFragment = new PlayFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.previewPlayer, playFragment).commitNowAllowingStateLoss();
           // getSupportFragmentManager().beginTransaction().show(playFragment).commitAllowingStateLoss();
            tvPlays.setText(!Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false) ? "手动换源" :"全屏播放");
        } else {
            tvPlays.setText("全屏播放");
        }
        isReverse = false;
        seriesGroupAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_series_group, seriesGroupOptions) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                TextView tvSeries = helper.getView(R.id.tvSeriesGroup);
                LinearLayout tvSeriesg = helper.getView(R.id.tvSeriesG);
                drawableLinearLayout(tvSeriesg, 5);
                tvSeries.setText(item);
            }
        };
        homedeVodAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (ApiConfig.get().getSourceBeanList().isEmpty())
                return;
            Movie.Video vod = ((Movie.Video) adapter.getItem(position));
            if (vod.id != null && !vod.id.isEmpty()) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", vod.id);
                intent.putExtra("sourceKey", vod.sourceKey);
                finish();
                startActivity(intent);
            } else {
                Intent newIntent;
                newIntent = new Intent(mContext, SearchActivity.class);
                newIntent.putExtra("title", vod.name);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);
            }
        });
        homedeVodAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            Movie.Video video = homedeVodAdapter.getData().get(position);
            if (video != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", video.id);
                bundle.putString("sourceKey", video.sourceKey);
                bundle.putString("title", video.name);
                bundle.putString("pic", video.pic);
                jumpActivity(FastSearchActivity.class, bundle);
            }
            return true;
        });
        searchAdapter.setNewData(new ArrayList<>());
            tvSort2.setText("快速搜索");
            tvSort1.setImageResource(R.drawable.ic_zoom_in);
            tvSort.setOnClickListener(v -> {
                startQuickSearch();
                QuickSearchDialog quickSearchDialog = new QuickSearchDialog(DetailActivity.this);
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, quickSearchData));
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, quickSearchWord));
                quickSearchDialog.show();
                if (pauseRunnable != null && pauseRunnable.size() > 0) {
                    searchExecutorService = Executors.newFixedThreadPool(5);
                    for (Runnable runnable : pauseRunnable) {
                        searchExecutorService.execute(runnable);
                    }
                    pauseRunnable.clear();
                    pauseRunnable = null;
                }
                quickSearchDialog.setOnDismissListener(dialog -> {
                    try {
                        if (searchExecutorService != null) {
                            pauseRunnable = searchExecutorService.shutdownNow();
                            searchExecutorService = null;
                        }
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                });
            });

        tvSeriesSort.setOnClickListener(v -> {
                if (vodInfo == null || vodInfo.seriesMap.isEmpty()) {
                    return;
                }
            isReverse = !isReverse;

                vodInfo.reverseSort = !vodInfo.reverseSort;
                if (Hawk.get(HawkConfig.EPISODE_GROUPING, false)) {
                    toggleSortOrderk();
                } else {
                    toggleSortOrder();
                }
                updateSortUI();

            });
       tvPush.setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            DevDialog dialog = new DevDialog(mContext);
            dialog.show();
        });
        tvPlay.setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            if(!Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false) && showPreview) {
            tvPlays.setText("手动换源");
                Switchtoplaybacksitek();
            }else{
            tvPlays.setText("全屏播放");
            if (showPreview) {
                toggleFullPreview();
            } else {
                jumpToPlay();
            }
            }
        });
        tvCollect.setOnClickListener(v -> {
            String text1 = tvCollect2.getText().toString();
            if ("加入收藏".equals(text1)) {
                RoomDataManger.insertVodCollect(sourceKey, vodInfo);
                Toast.makeText(DetailActivity.this, "已加入收藏夹", Toast.LENGTH_SHORT).show();
                tvCollect2.setText("取消收藏");
                tvCollect1.setImageResource(R.drawable.ic_shoucb);
            } else {
                RoomDataManger.deleteVodCollect(sourceKey, vodInfo);
                Toast.makeText(DetailActivity.this, "已移除收藏夹", Toast.LENGTH_SHORT).show();
                tvCollect2.setText("加入收藏");
                tvCollect1.setImageResource(R.drawable.ic_shouca);
            }
        });



        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = true;
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = true;
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = false;
            }
        });
        mGridViewFlag.setOnItemListener(new TvRecyclerView.OnItemListener() {
            private void refresh(View itemView, int position) {
                if (position == -1) {
                    return;
                }
                String newFlag = seriesFlagAdapter.getData().get(position).name;
                if (vodInfo != null && !vodInfo.playFlag.equals(newFlag)) {
                    for (int i = 0; i < vodInfo.seriesFlags.size(); i++) {
                        VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(i);
                        if (flag.name.equals(vodInfo.playFlag)) {
                            flag.selected = false;
                            List<VodInfo.VodSeries> vodSeries = vodInfo.seriesMap.get(vodInfo.playFlag);
                            seriesFlagAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(position);
                    flag.selected = true;
                    if (vodInfo.seriesMap.get(vodInfo.playFlag).size() > vodInfo.playIndex) {
                        vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).selected = false;
                    }
                    vodInfo.playFlag = newFlag;
                    seriesFlagAdapter.notifyItemChanged(position);
                    refreshList();
                }
            }

            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelectf = true;
                refresh(itemView, position);

            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                seriesSelectf = false;
                refresh(itemView, position);
                jumpToPlay();

            }
        });
        seriesAdapter.setOnItemClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
                // 当前分组的剧集
                List<VodInfo.VodSeries> currentGroup = seriesAdapter.getData();
                VodInfo.VodSeries clicked = currentGroup.get(position);
                List<VodInfo.VodSeries> allData = vodInfo.seriesMap.get(vodInfo.playFlag);
                boolean reload = false;
                // 找到全局索引
                List<VodInfo.VodSeries> allSeries = vodInfo.seriesMap.get(vodInfo.playFlag);
                int globalIndex = allSeries.indexOf(clicked);
                VodInfo.VodSeries clickItem = seriesAdapter.getData().get(position);
                int clickPlayIndex = allData.indexOf(clickItem);
                //解决倒叙不刷新
                if (vodInfo.playIndex != clickPlayIndex) {
                    seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                    clickItem.selected = true;
                    seriesAdapter.notifyItemChanged(position);
                    reload = true;
                }
                mGridView.postDelayed(() -> {
                    scrollToPlayPosAndRequestFoucuse(vodInfo.playIndex);
                }, 0);
                //解决当前集不刷新的BUG
                if (!preFlag.isEmpty() && !vodInfo.playFlag.equals(preFlag)) {
                    reload = true;
                }
                // 更新全局选中
                for (VodInfo.VodSeries s : allSeries) s.selected = false;
                clicked.selected = true;
                vodInfo.playIndex = globalIndex;
                //选集全屏 想选集不全屏的注释下面一行
                if (showPreview && !fullWindows && Hawk.get(HawkConfig.PLAYBACK_PLAYS, false)) toggleFullPreview();
                if (!showPreview || reload) jumpToPlay();
            }
        });
        mNumberClassification.setOnItemListener(new TvRecyclerView.OnItemListener() {
            public void refresh(View itemView, int position) {
                VodInfo.VodSeriesFlag item = mClassifyAdapter.getItem(position);
                if (item != null) {
                    if (item.selected) {
                        return;
                    }
                }

                // 🔥 先把 上一个选中的 selected 重置为 false（和你给的代码风格完全一样）
                for (int i = 0; i < mClassifyAdapter.getData().size(); i++) {
                    VodInfo.VodSeriesFlag flag = mClassifyAdapter.getData().get(i);
                    if (flag.selected) {
                        flag.selected = false;
                        mClassifyAdapter.notifyItemChanged(i);
                        break;
                    }
                }

                // 🔥 选中当前点击的 position
                VodInfo.VodSeriesFlag currItem = mClassifyAdapter.getData().get(position);
                currItem.selected = true;
                mClassifyAdapter.notifyItemChanged(position);
                GroupIndex = position;

                mNumberClassification.setSelection(position);
                List<VodInfo.VodSeries> vodSeries = mapData.get(item);
                if (vodSeries != null) {
                    seriesAdapter.setNewData(vodSeries);
                }
            }

            // 必须实现的3个方法（完整不缺）
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                // 空实现即可，不影响功能
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {

                refresh(itemView, position);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

                refresh(itemView, position);
            }
        });

        if (Hawk.get(HawkConfig.PLAYBACK_PLAY, false)) {
            if (showPreview && !fullWindows) toggleFullPreview();
        }
      /*  if (!Hawk.get(HawkConfig.PLAYBACK_PLAY, false)) {
            new Handler().postDelayed(() -> {
                 if (showPreview && !fullWindows) toggleFullPreview();
            }, 10000);
        }*/
        tvName.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) DetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
            String cpContent =  Objects.requireNonNull(vodInfo.seriesMap.get(vodInfo.playFlag)).get(vodInfo.playIndex).url;
            ClipData clipData = ClipData.newPlainText(null, cpContent + "#" + vodInfo.name);
            clipboard.setPrimaryClip(clipData);
            Toast.makeText(DetailActivity.this, "已复制" + cpContent + "#" + vodInfo.name, Toast.LENGTH_SHORT).show();
        });
        setLoadSir(llLayout);
    }
    private void setDouBanDatask(HomedeVodAdapter adapter) {
        try {
            // 1. 先读缓存，有缓存直接显示，不请求网络
            Object cacheObj = CacheManager.getCache("home_tuij_hot_" + mVideo.name);
            if (cacheObj != null && cacheObj instanceof String) {
                String cacheJson = (String) cacheObj;
                runOnUiThread(() -> {
                    try {
                        boolean byx = false;
                        adapter.setNewData(loadHotsK(cacheJson, byx));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return; // 有缓存直接结束，不发网络请求
            }
            OkGo.<String>get(RemoteConfig.checkUpdateA("TuijUrl") + mVideo.name)
                    .execute(new AbsCallback<String>() {
                        @Override
                        public String convertResponse(okhttp3.Response response) throws Throwable {
                            return response.body().string();
                        }
                        @Override
                        public void onSuccess(Response<String> response) {
                            String netJson = response.body();
                            // 保存缓存
                            CacheManager.save("home_tuij_hot_" + mVideo.name, netJson);
                            runOnUiThread(() -> {
                                // 统一解析（核心在这里！）
                                String finalJson = getFinalJson(netJson);
                                adapter.setNewData(loadHotsK(finalJson, true));
                            });
                        }
                        @Override
                        public void onError(Response<String> response) {
                            Log.e("最终测试", "请求失败 = " + response.getException().getMessage());
                        }
                    });

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
    private void initHomeHotVod3(HomedeVodAdapter adapter) {
        new android.os.Handler().postDelayed(() -> {
            setDouBanDatask(adapter);
        }, 200);
    }
    private String getFinalJson(String netJson) {
        if (mVideo.vodlist != null) {
            // 本地有数据 → 用本地数据构造JSON
            return "{\"code\":200,\"msg\":\"success\",\"list\":" + mVideo.vodlist + "}";
        } else {
            // 本地无数据 → 用接口/缓存原始JSON
            return netJson;
        }
    }
    private ArrayList<Movie.Video> loadHotsK(String json, boolean by) {
        ArrayList<Movie.Video> result = new ArrayList<>();
        JsonArray array;
        try {
            JsonObject infoJson = new Gson().fromJson(json, JsonObject.class);
            array = infoJson.getAsJsonArray("list");
            int sites = 7;
            for (SourceBean sb : ApiConfig.get().getSourceBeanList()) {
                if (sb.getKey().equals("htv_hrys")) sites = 1;
            }
            for (JsonElement ele : array) {
                JsonObject obj = (JsonObject) ele;
                Movie.Video vod = new Movie.Video();
                if (!obj.get("vod_name").getAsString().isEmpty()) {
                    tvWntjs.setText("相关影片推荐");
                }
                if(sites == 1) {
                    vod.id = obj.get("vod_id").getAsString();
                    vod.sourceKey = "htv_hrys";
                }
                vod.name = obj.get("vod_name").getAsString();
                vod.note = obj.get("vod_remarks").getAsString();
                vod.area = obj.get("vod_area").getAsString();
                vod.score = obj.get("vod_score").getAsString();
                vod.tid = Integer.parseInt(obj.get("type_id").getAsString());
                vod.tyid = Integer.parseInt(obj.get("type_id_1").getAsString());
                vod.isend = Integer.parseInt(obj.get("vod_isend").getAsString());
                vod.picthumb = obj.get("vod_pic_thumb").getAsString();
                vod.pic = obj.get("vod_pic").getAsString();

                result.add(vod);
            }
        } catch (Throwable th) {

        }
        return result;
    }
    private List<Runnable> pauseRunnable = null;
    private void jumpToPlay() {
        if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
            preFlag = vodInfo.playFlag;
            //更新播放地址
            LOG.i("播放地址：",vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).url);
            Bundle bundle = new Bundle();
            startStatusCheck();
            //保存历史
            insertVod(firstsourceKey, vodInfo);
            bundle.putString("sourceKey", sourceKey);
            // bundle.putSerializable("VodInfo", vodInfo);
            App.getInstance().setVodInfo(vodInfo);
            if (showPreview) {
                if (previewVodInfo == null) {
                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        oos.writeObject(vodInfo);
                        oos.flush();
                        oos.close();
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
                        previewVodInfo = (VodInfo) ois.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (previewVodInfo != null) {
                    previewVodInfo.playerCfg = vodInfo.playerCfg;
                    previewVodInfo.playFlag = vodInfo.playFlag;
                    previewVodInfo.playIndex = vodInfo.playIndex;
                    previewVodInfo.seriesMap = vodInfo.seriesMap;
                    //  bundle.putSerializable("VodInfo", previewVodInfo);
                    App.getInstance().setVodInfo(previewVodInfo);
                }
                playFragment.setData(bundle);
            } else {
                jumpActivity(PlayActivity.class, bundle);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void play(PlayEvent playEvent) {
        vodInfo = playEvent.getVodInfo();
        seriesAdapter.notifyDataSetChanged();
        jumpToPlay();
    }
    private void setTextShow(TextView view, String tag, String info) {
        if (info == null || info.trim().isEmpty()) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        view.setText(Html.fromHtml(getHtml(tag, info)));
    }

    private String removeHtmlTag(String info) {
        if (info == null)
            return "";
        return info.replaceAll("\\<.*?\\>", "").replaceAll("\\s", "");
    }
    void refreshList() {
        int seriesSize = vodInfo.seriesMap.get(vodInfo.playFlag).size();
        if (seriesSize > 0 && vodInfo.playIndex >= seriesSize) {
            // 越界了，不要设为0，而是设为最后一集！
            vodInfo.playIndex = seriesSize - 1;
        }
        List<VodInfo.VodSeries> vodSeries = vodInfo.seriesMap.get(vodInfo.playFlag);
        int size = vodSeries.size();
        insfCount = size;
        GroupCountt = size > 2500 ? 300 : size > 1500 ? 200 : size > 1000 ? 150 : size > 500 ? 100 : size > 300 ? 50 : size > 50 ? 30 : 20;
        vodInfo.playGroupCount = GroupCountt;

        // 确保选中状态正确
        if (vodInfo.seriesMap.get(vodInfo.playFlag) != null) {
            boolean hasSelected = false;
            for (VodInfo.VodSeries s : vodInfo.seriesMap.get(vodInfo.playFlag)) {
                if (s.selected) {
                    hasSelected = true;
                    break;
                }
            }
            if (!hasSelected) {
                vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).selected = true;
            }
        }

        // 计算列数
        Paint pFont = new Paint();
        Rect rect = new Rect();
        List<VodInfo.VodSeries> list = vodInfo.seriesMap.get(vodInfo.playFlag);
        int listSize = list.size();
        int w = 1;
        for(int i = 0; i < listSize; ++i){
            String name = list.get(i).name;
            pFont.getTextBounds(name, 0, name.length(), rect);
            if(w < rect.width()) w = rect.width();
        }
        w += 32;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth() / 3;
        int offset = screenWidth / w;
        if (offset <= 2) offset = 2;
        if (offset >= 9) offset = 10;
        offsets = offset;
        mGridViewLayoutMgr.setSpanCount(offset);
        loadVodInfo();
    }
    private void loadVodInfo() {
        List<VodInfo.VodSeries> allSeries = vodInfo.seriesMap.get(vodInfo.playFlag);
        if (allSeries.size() > 30 ) {
            tvSeriesSort.setVisibility(Hawk.get(HawkConfig.EPISODE_GROUPING, false) ? View.GONE : View.VISIBLE);
        }
        if (allSeries.size() > 20 && Hawk.get(HawkConfig.EPISODE_GROUPING, false)) {
            handleClassifyData();
        } else {
            mNumberClassification.setVisibility(View.GONE);
            seriesAdapter.setNewData(allSeries);
        }
    }

    private void handleClassifyData() {
        List<VodInfo.VodSeries> allSeries = vodInfo.seriesMap.get(vodInfo.playFlag);
        // 你可以根据剧集数量动态调整分组数
        int groupSize = 20;
        EpisodeGroupHelper.GroupResult result = EpisodeGroupHelper.groupEpisodes(allSeries, GroupCountt);

        // 更新分组Adapter
        mClassifyAdapter.setNewData(result.groupFlags);
        mNumberClassification.setVisibility(View.VISIBLE);
        // 默认显示第一个分组
        seriesAdapter.setNewData(result.groupMap.get(result.groupFlags.get(0)));

        // 保存分组数据
        mapData.clear();
        mapData.putAll(result.groupMap);
    }
    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.detailResult.observe(this, this::handleDetailResult);
    }

    private void handleDetailResult(AbsXml absXml) {
        if (absXml == null || absXml.movie == null || absXml.movie.videoList == null || absXml.movie.videoList.isEmpty()) {
            showEmpty();
            llPlayerFragmentContainer.setVisibility(View.GONE);
            llPlayerFragmentContainerBlock.setVisibility(View.GONE);
            return;
        }

        if (!TextUtils.isEmpty(absXml.msg) && !absXml.msg.equals("数据列表")) {
            Toast.makeText(DetailActivity.this, absXml.msg, Toast.LENGTH_SHORT).show();
            showEmpty();
            return;
        }

        mVideo = absXml.movie.videoList.get(0);
        initVodInfo(mVideo);
        updateUI(mVideo);
        // loadThumbnail(mVideo);
        handleSeriesData();
        showSuccess();
    }

    private void initVodInfo(Movie.Video video) {
        video.id = vodId;
        if (TextUtils.isEmpty(video.name))video.name = vod_name;
        if (TextUtils.isEmpty(video.name))video.name = "TVBox";
        if (vodInfo == null) {
            vodInfo = new VodInfo();
        }
        vodInfo.setVideo(video);
        vodInfo.sourceKey = video.sourceKey;

       // CacheManager.save("vod_info_" + vodId, vodInfo);
    }

    private void updateUI(Movie.Video video) {
        tvName.setText(video.name);
        tvNote.setText(video.note);
        sourceKey = video.sourceKey;
        tvScore.setText(TextUtils.isEmpty(video.score) ? "6.8分" : video.score + "分");
        TVInstance.INSTANCE.setVodInfo(vodInfo);

        setTextShow(tvSite, "8", ApiConfig.get().getSource(video.sourceKey).getName());
        setTextShow(tvYear, "•", video.year == 0 ? "" : String.valueOf(video.year));
        setTextShow(tvArea, "•", video.area);
        setTextShow(tvLang, "•", video.lang);
        setTextShow(tvType, "类型", !firstsourceKey.equals(sourceKey)
                ? "[" + ApiConfig.get().getSource(sourceKey).getName() + "] 解析"
                : video.type);
        String doubanScore = video.douban;
        if (doubanScore != null && !doubanScore.isEmpty() && !doubanScore.equals("0")) {
            setTextShow(tvDouban, "豆瓣", getOneDecimal(Double.parseDouble(doubanScore)));
        } else {
            setTextShow(tvDouban, "豆瓣", "5.0");
        }

        tvWad.setVisibility(!TextUtils.isEmpty(video.writer) || !TextUtils.isEmpty(video.actor) || !TextUtils.isEmpty(video.director)
                ? View.VISIBLE
                : View.GONE);
        setTextShow(tvWriter, "编剧：", removeHtmlTag(video.writer));
        setTextShow(tvActor, "演员：", removeHtmlTag(video.actor));
        setTextShow(tvDirector, "导演：", removeHtmlTag(video.director));
        setTextShow(tvDes, "简介：", removeHtmlTag(video.des));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            initHomeHotVod3(homedeVodAdapter);
        }, 1000); // 5秒后执行
        if (!TextUtils.isEmpty(mVideo.pic)) {
            Picasso.get()
                    .load(DefaultConfig.checkReplaceProxy(mVideo.pic))
                    .transform(new RoundTransformation(MD5.string2MD5(mVideo.pic))
                            .centerCorp(true)
                            .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                            .roundRadius(AutoSizeUtils.mm2px(mContext, 10), RoundTransformation.RoundType.ALL))
                    .placeholder(R.drawable.img_loading_placeholder)
                    .noFade()
                    .error(R.drawable.img_loading_placeholder)
                    .into(ivThumb);
        } else {
            ivThumb.setImageResource(R.drawable.img_loading_placeholder);
        }
        // 读取快速搜索缓存 ← 我加的
        List<Movie.Video> cacheSearchData = (List<Movie.Video>) CacheManager.getCache("vod_souzd_" + mVideo.name);
        if (cacheSearchData != null && !cacheSearchData.isEmpty()) {
            quickSearchData.clear();
            quickSearchData.addAll(cacheSearchData);
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, quickSearchData));
        }
        // 如果没有缓存才去搜索
        if (quickSearchData == null || quickSearchData.isEmpty()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startQuickSearch();
            }, 8000); // 5秒后执行

        }
    }
    public static String getOneDecimal(double d) {
        return String.format("%.1f", d);
    }
    private void handleSeriesData() {
        if (vodInfo.seriesMap == null || vodInfo.seriesMap.isEmpty()) {
            mGridViewFlag.setVisibility(View.GONE);
            mGridView.setVisibility(View.GONE);
            tvPlay.setVisibility(View.GONE);
            mEmptyPlayList.setVisibility(View.VISIBLE);
            return;
        }

        mGridViewFlag.setVisibility(View.VISIBLE);
        mGridView.setVisibility(View.VISIBLE);
        mEmptyPlayList.setVisibility(View.GONE);

        VodInfo vodInfoRecord = RoomDataManger.getVodInfo(sourceKey, vodId);
        if (vodInfoRecord != null) {
            vodInfo.playIndex = Math.max(vodInfoRecord.playIndex, 0);
            vodInfo.playFlag = vodInfoRecord.playFlag;
            vodInfo.playerCfg = vodInfoRecord.playerCfg;
            vodInfo.reverseSort = vodInfoRecord.reverseSort;
        } else {
            vodInfo.playIndex = LOCK_PLAY_INDEX;
            vodInfo.playFlag = null;
            vodInfo.playerCfg = "";
            vodInfo.reverseSort = false;
        }

        if (vodInfo.reverseSort) {
            List<VodInfo.VodSeries> allData = vodInfo.seriesMap.get(vodInfo.playFlag);
            // 反转数据
            Collections.reverse(allData);
        }

        if (vodInfo.playFlag == null || !vodInfo.seriesMap.containsKey(vodInfo.playFlag)) {
            vodInfo.playFlag = (String) vodInfo.seriesMap.keySet().toArray()[0];
        }

        int flagScrollTo = 0;
        for (int j = 0; j < vodInfo.seriesFlags.size(); j++) {
            VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(j);
            if (flag.name.equals(vodInfo.playFlag)) {
                flagScrollTo = j;
                flag.selected = true;
            } else {
                flag.selected = false;
            }
        }

        seriesFlagAdapter.setNewData(vodInfo.seriesFlags);
        mGridViewFlag.scrollToPosition(flagScrollTo);

        refreshList();
        if (showPreview) {
            jumpToPlay();
            llPlayerFragmentContainer.setVisibility(View.VISIBLE);
            llPlayerFragmentContainerBlock.setVisibility(View.VISIBLE);
            toggleSubtitleTextSize();
        }
        // 🔥 兜底：再给一次焦点，防止第一次没生效
        mGridView.postDelayed(() -> {
            // 正确计算 组内位置
            int groupStartPosition = GroupIndex * 20;
            int innerIndex = vodInfo.playIndex - groupStartPosition;

            // 安全范围判断，绝不崩溃
            if (innerIndex >= 0 && innerIndex < seriesAdapter.getData().size()) {
                scrollToPlayPosAndRequestFoucuse(innerIndex);
            }
        }, 200);
    }
    private String getHtml(String label, String content) {
        // 处理空内容
        if (content == null) {
            content = "";
        }

        // 根据 label 进行不同处理
        switch (label) {
            case "类型":
                content = content.replace(",", " • ").replace("/", "•");
                return " • " + content + " ";
            case "8":
                return content + " ";
            default:
                if (label.contains("豆瓣")) {
                    return "&nbsp;" + content;
                }
                if (label.contains("演员") || label.contains("导演")) {
                    content = content.replace(",", " / ");
                    return label + content;
                }
                if (label.length() == 1) {
                    return label + " " + content + " ";
                }
                return label + content + " ";
        }
    }
    private String  vod_picture="";
    private String  vod_name = "";
    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            vod_name = bundle.getString("title", "");
            vod_picture = bundle.getString("picture", "");
            String id = bundle.getString("id", null);
            String sourceKey = bundle.getString("sourceKey", "");
            // ====================== 【完美缓存读取：界面100%显示】 ======================
          /*  VodInfo cacheInfo = (VodInfo) CacheManager.getCache("vod_info_" + id);
            if (cacheInfo != null) {
                Log.i("VodInfo缓存", "成功读取 = " + cacheInfo.name);
                this.vodInfo = cacheInfo;
                this.vodId = id;
                this.sourceKey = sourceKey;
                this.firstsourceKey = sourceKey;

                // 把缓存的 VodInfo 完整塞给 mVideo → 界面就有数据了！
                mVideo = new Movie.Video();
                mVideo.id = id;
                mVideo.sourceKey = sourceKey;
                mVideo.name = cacheInfo.name;
                mVideo.note = cacheInfo.note;
                mVideo.pic = cacheInfo.pic;
                mVideo.picthumb = cacheInfo.picthumb;
                mVideo.year = cacheInfo.year;
                mVideo.area = cacheInfo.area;
                mVideo.lang = cacheInfo.lang;
                mVideo.type = cacheInfo.type;
                mVideo.douban = cacheInfo.douban;
                mVideo.writer = cacheInfo.writer;
                mVideo.actor = cacheInfo.actor;
                mVideo.director = cacheInfo.director;
                mVideo.des = cacheInfo.des;
                mVideo.score = cacheInfo.score;
                mVideo.vodlist = cacheInfo.vodlist;

                // 刷新界面
                showLoading();
                new Handler(Looper.getMainLooper()).post(() -> {
                    updateUI(mVideo);
                    handleSeriesData();
                    showSuccess();
                });
                return;
            }*/

            // 无缓存才请求网络
            loadDetail(id, sourceKey);
        }
    }
    private void initData999() {
        Hawk.put("sqlCache", true); // ← 就加这一句
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            loadDetail(bundle.getString("id", null), bundle.getString("sourceKey", ""));
        }
    }
    private void loadDetail(String vid, String key) {
        if (vid != null) {
            vodId = vid;
            sourceKey = key;
            firstsourceKey = key;
            showLoading();
            sourceViewModel.getDetail(sourceKey, vodId);

            boolean isVodCollect = RoomDataManger.isVodCollect(sourceKey, vodId);
            if (isVodCollect) {
                tvCollect2.setText("取消收藏");
                tvCollect1.setImageResource(R.drawable.ic_shoucb);
            } else {
                tvCollect2.setText("加入收藏");
                tvCollect1.setImageResource(R.drawable.ic_shouca);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(@NonNull RefreshEvent event) {
        if (event.obj == null) return;

        switch (event.type) {
            case RefreshEvent.TYPE_REFRESH:
                handleRefreshEvent(event);
                break;
            case RefreshEvent.TYPE_QUICK_SEARCH_SELECT:
                handleQuickSearchSelectEvent(event);
                break;
            case RefreshEvent.TYPE_QUICK_SEARCH_WORD_CHANGE:
                handleQuickSearchWordChangeEvent(event);
                break;
            case RefreshEvent.TYPE_QUICK_SEARCH_RESULT:
                handleQuickSearchResultEvent(event);
                break;
            case RefreshEvent.TYPE_QUICK_SEARCH:
                    handleQuickSearchEvent(event);
                break;
            case RefreshEvent.TYPE_SWITCH_SOURCE:
                onSwitchSourceEvent(event);
                break;
        }
    }

    private void handleRefreshEvent(RefreshEvent event) {
        if (event.obj instanceof Integer) {
            int freshIndex = (int) event.obj;
            handleFreshIndexEvent(freshIndex);
        } else if (event.obj instanceof JSONObject) {
            vodInfo.playerCfg = event.obj.toString();
            insertVod(firstsourceKey, vodInfo); // 保存历史
        }
    }


    private void handleQuickSearchSelectEvent(RefreshEvent event) {
        Movie.Video video = (Movie.Video) event.obj;
        // 保存位置
        LOCK_PLAY_INDEX = vodInfo.playIndex;
        loadDetail(video.id, video.sourceKey);
    }

    private void handleQuickSearchWordChangeEvent(RefreshEvent event) {
        String word = (String) event.obj;
        switchSearchWord(word);
    }

    private void handleQuickSearchResultEvent(RefreshEvent event) {
        try {
                searchData(event.obj == null ? null : (AbsXml) event.obj);
        } catch (Exception e) {
            searchData(null);
        }
    }

    private void handleQuickSearchEvent(RefreshEvent event) {
        List<Movie.Video> data = (List<Movie.Video>) event.obj;
        searchAdapter.setNewData(data);
    }
    public void onSwitchSourceEvent(RefreshEvent event) {

        if(Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false)) {
            Toast.makeText(App.getInstance(),(CharSequence) event.obj, Toast.LENGTH_SHORT).show();
            startAutoSourceSwitch();
        }

    }
    private void handleFreshIndexEvent(int freshIndex) {
        int mGroupIndex = (int) Math.floor(freshIndex / (GroupCountt + 0.0f));
        VodInfo.VodSeriesFlag itemS = mClassifyAdapter.getItem(mGroupIndex);

        if (Hawk.get(HawkConfig.EPISODE_GROUPING, false) && mGroupIndex > 0) {
            handleGroupedEpisodeSelection(freshIndex, mGroupIndex, itemS);
        } else {
            handleUngroupedEpisodeSelection(freshIndex, itemS);
        }
            vodInfo.playIndex = freshIndex;
        insertVod(firstsourceKey, vodInfo); // 保存历史
    }
    private void handleGroupedEpisodeSelection(int freshIndex, int mGroupIndex, VodInfo.VodSeriesFlag itemS) {
        List<VodInfo.VodSeries> allData = vodInfo.seriesMap.get(vodInfo.playFlag);
        if (allData != null) {
            for (VodInfo.VodSeries allItem : allData) {
                allItem.selected = false;
            }
        }

        mClassifyAdapter.getData().get(GroupIndex).selected = false;
        mClassifyAdapter.notifyItemChanged(GroupIndex);
        mClassifyAdapter.getData().get(mGroupIndex).selected = true;
        mClassifyAdapter.notifyItemChanged(mGroupIndex);
        List<VodInfo.VodSeries> vodSeries = mapData.get(itemS);
        if (vodSeries != null) {
            seriesAdapter.setNewData(vodSeries);
        }
        GroupIndex = mGroupIndex;
        mNumberClassification.setSelection(mGroupIndex);
        vodInfo.seriesMap.get(vodInfo.playFlag).get(freshIndex).selected = true;
        seriesAdapter.notifyItemChanged(freshIndex);
        seriesAdapter.notifyDataSetChanged();
    }



    private void handleUngroupedEpisodeSelection(int freshIndex, VodInfo.VodSeriesFlag itemS) {
        if (insfCount == GroupCountt) {
            seriesAdapter.setNewData(vodInfo.seriesMap.get(vodInfo.playFlag));
        }

        if (Hawk.get(HawkConfig.EPISODE_GROUPING, false) && insfCount != GroupCountt) {
            List<VodInfo.VodSeries> vodSeries = mapData.get(itemS);
            if (vodSeries != null) {
                seriesAdapter.setNewData(vodSeries);
            }
        }

        for (int j = 0; j < seriesAdapter.getData().size(); j++) {
            seriesAdapter.getData().get(j).selected = false;
            seriesAdapter.notifyItemChanged(j);
        }

        seriesAdapter.getData().get(freshIndex).selected = true;
        seriesAdapter.notifyItemChanged(freshIndex);
    }
    private String searchTitle = "";
    private boolean hadQuickStart = false;
    private final List<Movie.Video> quickSearchData = new ArrayList<>();

    private final List<String> quickSearchWord = new ArrayList<>();
    private ExecutorService searchExecutorService = null;

    private void switchSearchWord(String word) {
        OkGo.getInstance().cancelTag("quick_search");
        quickSearchData.clear();
        searchTitle = word;
      //  searchResult();
    }
    private void startQuickSearch() {
        initCheckedSourcesForSearch();
        if (hadQuickStart)
            return;
        hadQuickStart = true;
        OkGo.getInstance().cancelTag("quick_search");
        quickSearchWord.clear();
        searchTitle = mVideo.name;
        quickSearchData.clear();
        quickSearchWord.addAll(SearchHelper.splitWords(searchTitle));

        searchResult();
    }

    private void searchResult() {
        try {
            if (searchExecutorService != null) {
                searchExecutorService.shutdownNow();
                searchExecutorService = null;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        searchExecutorService = Executors.newFixedThreadPool(5);
        List<SourceBean> searchRequestList = new ArrayList<>();
        searchRequestList.addAll(ApiConfig.get().getSearchSourceBeanList());
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        searchRequestList.remove(home);
        searchRequestList.add(0, home);

        ArrayList<String> siteKey = new ArrayList<>();
        for (SourceBean bean : searchRequestList) {
            if (!bean.isSearchable() || !bean.isQuickSearch()) {
                continue;
            }
            if (mCheckSources != null && !mCheckSources.containsKey(bean.getKey())) {
                continue;
            }
            siteKey.add(bean.getKey());
        }
        for (String key : siteKey) {
            searchExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    sourceViewModel.getQuickSearch(key, searchTitle);
                }
            });
        }
    }

    private boolean matchSearchResult(String name, String searchTitle) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(searchTitle)) return false;
        searchTitle = searchTitle.trim();
        String[] arr = searchTitle.split("\\s+");
        int matchNum = 0;
        for(String one : arr) {
            if (name.contains(one)) matchNum++;
        }
        return matchNum == arr.length ? true : false;
    }
    private void searchData(AbsXml absXml) {
        if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
            List<Movie.Video> data = new ArrayList<>();
            for (Movie.Video video : absXml.movie.videoList) {
                // 去除当前相同的影片
                if (video.sourceKey.equals(sourceKey) && video.id.equals(vodId)) {
                    continue;
                }
                if (video.name.equals(mVideo.name)) {
                    data.add(video);
                    if (matchSearchResult(video.name, searchTitle))
                        data.add(video);
                }
            }
            quickSearchData.addAll(removeDuplicationByHashSet(data));
            // 保存缓存
            CacheManager.save("vod_souzd_" + mVideo.name, quickSearchData);
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, quickSearchData));
        }
    }
    public static List removeDuplicationByHashSet(List<Movie.Video> list) {
        HashSet set = new HashSet(list);
        list.clear();
        list.addAll(set);
        return list;
    }
    private void insertVod(String sourceKey, VodInfo vodInfo) {
        try {
            vodInfo.playNote = vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).name;
        } catch (Throwable th) {
            vodInfo.playNote = "";
        }
        RoomDataManger.insertVodRecord(sourceKey, vodInfo);
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_HISTORY_REFRESH));
    }

    @Override
    public void onBackPressed() {
        if (fullWindows) {
            if (playFragment.onBackPressed())
                return;
            toggleFullPreview();
            mGridView.requestFocus();
            return;
        }

        super.onBackPressed();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && playFragment != null && fullWindows) {
            if (playFragment.dispatchKeyEvent(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    // preview
    VodInfo previewVodInfo = null;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStatusCheck();
        seriesSelect = false;
        // 新增：移除autoSwitchHandler的所有任务
        if (autoSwitchHandler != null) {
            autoSwitchHandler.removeCallbacksAndMessages(null);
        }
        try {
            if (searchExecutorService != null) {
                searchExecutorService.shutdownNow();
                searchExecutorService = null;
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
        OkGo.getInstance().cancelTag("fenci");
        OkGo.getInstance().cancelTag("detail");
        OkGo.getInstance().cancelTag("quick_search");
        TVInstance.INSTANCE.setVodInfo(null);
        EventBus.getDefault().unregister(this);
    }
    public boolean fullWindows = false;
    ViewGroup.LayoutParams windowsPreview = null;
    ViewGroup.LayoutParams windowsFull = null;

    public void toggleFullPreview() {
        if (windowsPreview == null) {
            windowsPreview = llPlayerFragmentContainer.getLayoutParams();
        }
        if (windowsFull == null) {
            windowsFull = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        fullWindows = !fullWindows;
        llPlayerFragmentContainer.setLayoutParams(fullWindows ? windowsFull : windowsPreview);
        llPlayerFragmentContainerBlock.setVisibility(fullWindows ? View.GONE : View.VISIBLE);
        mGridView.setVisibility(fullWindows ? View.GONE : View.VISIBLE);
        mGridViewFlag.setVisibility(fullWindows ? View.GONE : View.VISIBLE);
        mNumberClassification.setVisibility(fullWindows ? View.GONE : View.VISIBLE);

        //全屏下禁用详情页几个按键的焦点 防止上键跑过来
        tvPlay.setFocusable(!fullWindows);
        tvSort.setFocusable(!fullWindows);
        tvCollect.setFocusable(!fullWindows);
        tvPush.setFocusable(!fullWindows);
        tvName.setFocusable(!fullWindows);
        if (!fullWindows && vodInfo != null) {//恢复焦点
            scrollToPlayPosAndRequestFoucuse(vodInfo.playIndex);
        }
        if (playFragment.mController != null) {
            playFragment.mController.setIsFullScreen(fullWindows);
        }
        toggleSubtitleTextSize();
    }
    private void colorTextViewx(TextView str, @NonNull LinearLayout esdrs, ImageView kgf) {
        kgf.setImageResource(R.drawable.expand);
        GradientDrawable gradientDrawable = new GradientDrawable();
        if (Hawk.get(HawkConfig.THEME_SELECT, 0) >= 3) {
            gradientDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext, 5));
        }else{
            gradientDrawable.setCornerRadius(AutoSizeUtils.mm2px(mContext, 0));
        }
        //gradientDrawable.setAlpha(95);//设置透明度
        gradientDrawable.setColor(mContext.getResources().getColor( R.color.color_theme_h));
        gradientDrawable.setStroke(2, mContext.getResources().getColor(R.color.color_theme_s));
        esdrs.setBackground(gradientDrawable);
        str.setTextColor(mContext.getResources().getColor(R.color.color_theme_s));
    }
    private void scrollToPlayPosAndRequestFoucuse(int freshIndex) {
        if (mGridView == null || mGridView.getLayoutManager() == null) return;

        // 先滚动到位置
        mGridView.scrollToPosition(freshIndex);
        // 延迟120ms，确保View渲染完成，再强制请求焦点
        new Handler().postDelayed(() -> {
            View requestView = mGridView.getLayoutManager().findViewByPosition(freshIndex);
            if (requestView != null) {
                requestView.requestFocus(); // 强制显示焦点框
            }
        }, 120);
    }
    private void toggleSortOrderk() {
        List<VodInfo.VodSeries> currentGroup = seriesAdapter.getData();
        int totalCount = currentGroup.size();
        int oldPos = -1;

        // 找到当前选中
        for (int i = 0; i < currentGroup.size(); i++) {
            if (currentGroup.get(i).selected) {
                oldPos = i;
                break;
            }
        }

        // 倒序
        vodInfo.reverse();

        // 计算新位置
        int newPos = oldPos;
        if (oldPos != -1) newPos = totalCount - 1 - oldPos;

        // 清空选中
        for (VodInfo.VodSeries s : currentGroup) s.selected = false;

        // 重新选中
        if (newPos >= 0 && newPos < currentGroup.size()) {
            currentGroup.get(newPos).selected = true;
            int finalNewPos1 = newPos;
            scrollToPlayPosAndRequestFoucuse(finalNewPos1);
        }
        seriesAdapter.setNewData(currentGroup);
        seriesAdapter.notifyDataSetChanged();

        // 修复焦点
        if (newPos != -1) {
            int finalNewPos = newPos;
            mGridView.post(() -> {
                mGridView.setSelection(finalNewPos);
                mGridView.scrollToPosition(finalNewPos);
                View v = mGridView.getLayoutManager().findViewByPosition(finalNewPos);
                if (v != null) v.requestFocus();
            });
        }
    }
    private void toggleSortOrder() {
        List<VodInfo.VodSeries> allData = vodInfo.seriesMap.get(vodInfo.playFlag);
        int oldIndex = vodInfo.playIndex;
        int totalCount = allData.size();
        // 反转数据
        vodInfo.reverse();

        // 反转分组标签
        if (!CollectionUtils.isEmpty(mClassifyAdapter.getData())) {
            Collections.reverse(mClassifyAdapter.getData());
            mClassifyAdapter.notifyDataSetChanged();
            GroupIndex = mClassifyAdapter.getData().size() - 1 - GroupIndex;
            mNumberClassification.setSelection(GroupIndex);
        }
        // 计算新索引
        int newIndex = totalCount - 1 - oldIndex;

        for (int i = 0; i < allData.size(); i++) {
            allData.get(i).selected = (i == newIndex);
        }
        vodInfo.playIndex = newIndex;
        // 更新selected状态
        for (int i = 0; i < allData.size(); i++) {
            allData.get(i).selected = (i == newIndex);
            if(i == newIndex) seriesSelect = false;
        }
            scrollToPlayPosAndRequestFoucuse(newIndex);
        // 更新Adapter
        seriesAdapter.setNewData(allData);
        seriesAdapter.notifyDataSetChanged();
    }
    private void initViews() {
        LinearLayout[] layouts = { tvPush, tvSort, tvCollect, tvPlay};
        drawableLinearLayout(layouts, 5);
        drawableTextView(tvSeriesSort, 3);
    }
    private void setClickListeners() {
        llPlayerFragmentContainerBlock.setOnClickListener(view -> toggleFullPreview()); // 点击全屏
    }
    private void setClickListeners999() {
        // 点击 → 切换播放源（和你参考代码逻辑完全一致）
        llPlayerFragmentContainerBlock.setOnClickListener(view -> Switchtoplaybacksitek());

        // 原有搜索逻辑保持不变
        searchAdapter.setOnItemClickListener((adapter, view, position) -> {
            Movie.Video video = searchAdapter.getData().get(position);
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_SELECT, video));
            dismiss();
        });
    }
    private void updateSortUI() {
        tvSeriesSort.setText(isReverse?"倒序":"正序");
    }
    void toggleSubtitleTextSize() {
        int subtitleTextSize  = SubtitleHelper.getTextSize(this);
        if (!fullWindows) {
            subtitleTextSize *= 0.6;
        }
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SUBTITLE_SIZE_CHANGE, subtitleTextSize));
    }
    // 状态检查相关
    private static final long PLAY_TIMEOUT = 5_000; // 30秒
    private Handler statusCheckHandler = new Handler(Looper.getMainLooper());
    private Runnable statusCheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (playFragment.getPlayer() != null) {
                // 🔥 核心：不是暂停状态 + 没有在播放 → 才换源
                boolean isPaused = playFragment.getPlayer().getCurrentPlayState()
                        == VideoView.STATE_PAUSED;

                boolean isPlaying = playFragment.getPlayer().isPlaying();

                if (!isPlaying && !isPaused && Hawk.get(HawkConfig.FORBID_JAR_DIALOG, false)) {
                    startAutoSourceSwitch();
                    return;
                }
            }
            // 继续循环检查
            statusCheckHandler.postDelayed(this, PLAY_TIMEOUT);
        }
    };

    // 开始检查
    private void startStatusCheck() {
        stopStatusCheck();
        statusCheckHandler.postDelayed(statusCheckRunnable, PLAY_TIMEOUT);
    }

    // 停止检查
    private void stopStatusCheck() {
        statusCheckHandler.removeCallbacks(statusCheckRunnable);
    }
    public void startAutoSourceSwitch() {
        if (quickSearchData == null || quickSearchData.isEmpty())startQuickSearch();
        // 1. 状态检查
        if (!canSwitchSource()) {
            return;
        }
        // 2. 更新状态
        updateSwitchState();
        // 3. 执行换源
        if (isSeriesFlagAvailable()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                switchSeriesFlag();
            }, 2000); // 5秒后执行

        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                switchPlaybackSite();
                handleNoMoreFlags();
            }, 5000); // 5秒后执行

        }
    }

    // 修改原有的换源方法
    private void Switchtoplaybacksitek() {
        List<SourceBean> sourceBeans = ApiConfig.get().getSearchSourceBeanList();
        List<String> sourceKeys = new ArrayList<>();

        // 过滤可用源
        for (SourceBean bean : sourceBeans) {
            if (bean.isSearchable() && bean.isQuickSearch()) {
                sourceKeys.add(bean.getKey());
            }
        }

        if (quickSearchData == null || quickSearchData.isEmpty()) {
            return;
        }

        int currentIndex = -1;
        String currentSourceKey = sourceKey;

        // 查找当前播放源的索引
        for (int i = 0; i < quickSearchData.size(); i++) {
            if (quickSearchData.get(i).sourceKey.equals(currentSourceKey)) {
                currentIndex = i;
                break;
            }
        }
        // 查找下一个可用源
        int nextIndex = findNextAvailableSource(currentIndex);
        if (nextIndex != -1) {
            switchToSource(nextIndex);
        } else {
            Toast.makeText(mContext, "未找到可用播放源", Toast.LENGTH_SHORT).show();
        }
    }
    private int findNextAvailableSource(int currentIndex) {
        if (quickSearchData == null || quickSearchData.isEmpty())
            return -1;
        int size = quickSearchData.size();
        // 下一个 = 当前+1，如果到最后一个就回到 0（循环切换）
        Log.e("AutoSwitch222", String.valueOf((currentIndex + 1) % size));
        return (currentIndex + 1) % size;
    }
    // 检查源是否可用
    private boolean isSourceAvailable(Movie.Video video, List<String> sourceKeys) {
        if (video == null || video.sourceKey == null) return false;

        // 检查源是否在可用源列表中
        return sourceKeys.contains(video.sourceKey);
    }
    // 切换到指定源
    private void switchToSource(int index) {
        try {
            Movie.Video video = quickSearchData.get(index);
            String sourceName = ApiConfig.get().getSource(video.sourceKey).getName();
            // 发送切换事件
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_SELECT, video));

            // 显示切换提示
            Toast.makeText(mContext, "已切换到: " + sourceName, Toast.LENGTH_SHORT).show();

            // 记录新的源key
            sourceKey = video.sourceKey;
        } catch (Exception e) {
            Log.e("AutoSwitch", "切换源失败", e);
        }
    }

    private int findCurrentFlagIndex() {
        for (int i = 0; i < vodInfo.seriesFlags.size(); i++) {
            VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(i);
            if (flag.name.equals(vodInfo.playFlag)) {
                flag.selected = false;
                seriesFlagAdapter.notifyItemChanged(i);
                return i;
            }
        }
        return -1;
    }
    private boolean canSwitchSource() {
        if (isAutoSwitching) {
            Log.w("AutoSwitch", "换源进行中，请稍候");
            return false;
        }

        if (currentRetryCount >= maxRetryCount) {
            Toast.makeText(this, "已尝试所有播放源，请手动切换其他线路", Toast.LENGTH_SHORT).show();
            resetAutoSwitch();
            return false;
        }

        maxRetryCount1 = quickSearchData.size();
        if (currentRetryCount1 >= maxRetryCount1) {
            Toast.makeText(this, "超时切换其他线路", Toast.LENGTH_SHORT).show();
            resetAutoSwitchs();
            return false;
        }

        return true;
    }

    private void updateSwitchState() {
        isAutoSwitching = true;
        currentRetryCount++;
    }

    public boolean isSeriesFlagAvailable() {
        return vodInfo != null &&
                vodInfo.seriesFlags != null &&
                vodInfo.seriesFlags.size() > 0;
    }

    public void switchSeriesFlag() {
        try {
            int currentIndex = findCurrentFlagIndex();
            if (canSwitchToNextFlag(currentIndex)) {
                switchToNextFlag(currentIndex);
            } else {
                switchPlaybackSite();
            }
        } catch (Exception e) {
            Log.e("AutoSwitch", "切换线路失败", e);
            handleSwitchError();
        } finally {
            isAutoSwitching = false;
        }
    }
    private boolean canSwitchToNextFlag(int currentIndex) {
        return currentIndex != -1 &&
                currentIndex + 1 < vodInfo.seriesFlags.size();
    }

    private void switchToNextFlag(int currentIndex) {
        int nextIndex = currentIndex + 1;
        VodInfo.VodSeriesFlag nextFlag = vodInfo.seriesFlags.get(nextIndex);

        // 更新UI和状态
        nextFlag.selected = true;
        vodInfo.playFlag = nextFlag.name;
        seriesFlagAdapter.notifyItemChanged(nextIndex);
        showSwitchProgress();
        showSwitchProgresss();
        // 刷新并播放
        refreshList();
        showSwitchSuccess(nextFlag.name);
        jumpToPlay();
    }
    public void switchPlaybackSite() {

        autoSwitchHandler.postDelayed(() -> {
            try {
                currentRetryCount1 ++;
                Switchtoplaybacksitek();
            } catch (Exception e) {
                Log.e("AutoSwitch", "切换播放源失败", e);
                handleSwitchError();
            } finally {
                isAutoSwitching = false;
            }
        }, RETRY_DELAY);

        showSwitchProgresss();
    }

    private void showSwitchSuccess(String sourceName) {
        Toast.makeText(this,
                String.format("已切换到播放源: %s", sourceName),
                Toast.LENGTH_SHORT
        ).show();
    }
    private void showSwitchProgress() {
        maxRetryCount = vodInfo.seriesFlags.size();
        String msg = String.format("正在切换其它播放源 %d/%d",currentRetryCount, maxRetryCount);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void showSwitchProgresss() {
        maxRetryCount1 = quickSearchData.size();
        String msg = String.format("正在切换站点播放源 %d/%d",currentRetryCount1, maxRetryCount1);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void handleNoMoreFlags() {
        Log.w("AutoSwitch", "没有更多可用线路");
        Toast.makeText(this, "切换站点播放源", Toast.LENGTH_SHORT).show();
        resetAutoSwitchs();
    }

    private void handleSwitchError() {
        Toast.makeText(this, "切换播放源失败，请稍后重试", Toast.LENGTH_SHORT).show();
        resetAutoSwitch();
    }

    private void resetAutoSwitch() {
        currentRetryCount = 0;
        isAutoSwitching = false;
        if (autoSwitchHandler != null) {
            autoSwitchHandler.removeCallbacksAndMessages(null);
        }
    }
    private void resetAutoSwitchs() {
        currentRetryCount1 = 0;
        isAutoSwitching = false;
        if (autoSwitchHandler != null) {
            autoSwitchHandler.removeCallbacksAndMessages(null);
        }
    }
    void ensurePlayFragment() {
        if (playFragment != null) return;
        playFragment = new PlayFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.previewPlayer, playFragment).commitNowAllowingStateLoss();
    }
}