package com.github.tvbox.osc.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.CollectionUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.bean.AbsXml;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.cache.RoomDataManger;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.picasso.RoundTransformation;
import com.github.tvbox.osc.ui.adapter.HomedeVodAdapter;
import com.github.tvbox.osc.ui.adapter.SeriesAdapter;
import com.github.tvbox.osc.ui.adapter.SeriesFlagAdapter;
import com.github.tvbox.osc.ui.dialog.DevDialog;
import com.github.tvbox.osc.ui.dialog.PushDialog;
import com.github.tvbox.osc.ui.dialog.QuickSearchDialog;
import com.github.tvbox.osc.ui.fragment.PlayFragment;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.HistoryHelper;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.util.ScreenUtils;
import com.github.tvbox.osc.util.SearchHelper;
import com.github.tvbox.osc.util.SubtitleHelper;
import com.github.tvbox.osc.util.TVInstance;
import com.github.tvbox.osc.util.VodInfoClassifyUtil;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */

public class DetailActivity extends BaseActivity {
    private LinearLayout llLayout;
    private FragmentContainerView llPlayerFragmentContainer;
    private View llPlayerFragmentContainerBlock;
    private View llPlayerPlace;
    private PlayFragment playFragment = null;
    private ImageView ivThumb;
    private TextView tvName;
    private TextView tvYear;
    private TextView tvSite;
    private TextView tvArea;
    private TextView tvWeekday;
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
    public String firstsourceKey;
    private LinearLayout tvQuickSearch;
    private LinearLayout tvCollect;
    private LinearLayout llaSite;
    private ImageView tvCollect1;
    private TextView tvCollect2;
    private TextView tvWntj;
    private TvRecyclerView mGridViewFlag;
    private TvRecyclerView mNumberClassification;
    private TvRecyclerView mGridView;
    private TvRecyclerView mSeriesGroupView;
    private LinearLayout mEmptyPlayList;
    private SourceViewModel sourceViewModel;
    public static Movie.Video mVideo;
    private VodInfo vodInfo;
    private SeriesFlagAdapter seriesFlagAdapter;
    private SeriesFlagAdapter mClassifyAdapter;//集数分类adapter
    private BaseQuickAdapter<String, BaseViewHolder> seriesGroupAdapter;
    private SeriesAdapter seriesAdapter;
    public String vodId;
    public String sourceKey;
    public static int selectss;
    boolean seriesSelect = false;
    private View seriesFlagFocus = null;
    private boolean isReverse;
    //private String preFlag="";
    private boolean firstReverse;
    private HomedeVodAdapter homedeVodAdapter;
    public static TvRecyclerView tvHotList3;
    private V7GridLayoutManager mGridViewLayoutMgr = null;
    private HashMap<String, String> mCheckSources = null;
    private LinkedHashMap<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>> mapData = new LinkedHashMap<>();//分组
    private VodInfo.VodSeriesFlag mLastSelectedVodSeriesFlag = null;
    boolean showPreview = Hawk.get(HawkConfig.SHOW_PREVIEW, true);
    private final ArrayList<String> seriesGroupOptions = new ArrayList<>();
    private View currentSeriesGroupView;
    private int GroupCount;
    private int thread = Integer.parseInt(HistoryHelper.getHistoryNumNamex(Hawk.get(HawkConfig.XIANC_NUM, 0)));

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

    private List<Runnable> pauseRunnable = null;

    @SuppressLint("WrongViewCast")
    private void initView() {
        llLayout = findViewById(R.id.llLayout);
        llPlayerPlace = findViewById(R.id.previewPlayerPlace);
        llPlayerFragmentContainer = findViewById(R.id.previewPlayer);
        llPlayerFragmentContainerBlock = findViewById(R.id.previewPlayerBlock);
        ivThumb = findViewById(R.id.ivThumb);
        llPlayerPlace.setVisibility(showPreview ? View.VISIBLE : View.GONE);
        ivThumb.setVisibility(!showPreview ? View.VISIBLE : View.GONE);
        tvName = findViewById(R.id.tvName);
        tvYear = findViewById(R.id.tvYear);
        tvSite = findViewById(R.id.tvSite);
        tvArea = findViewById(R.id.tvArea);
        tvWeekday = findViewById(R.id.tvWeekday);
        tvLang = findViewById(R.id.tvLang);
        tvType = findViewById(R.id.tvType);
        tvWntj = findViewById(R.id.tvWntj);
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
        tvPush = findViewById(R.id.tvPush);
        if (ScreenUtils.isTv(mContext)) {
            tvPush.setVisibility(View.GONE);
        } else {
            tvPush.setVisibility(View.VISIBLE);
        }
        tvCollect = findViewById(R.id.tvCollect);
        tvCollect1 = findViewById(R.id.tvCollect1);
        tvCollect2 = findViewById(R.id.tvCollect2);
        llaSite = findViewById(R.id.llaSite);
        tvQuickSearch = findViewById(R.id.tvQuickSearch);
        mEmptyPlayList = findViewById(R.id.mEmptyPlaylist);
        if (Hawk.get(HawkConfig.EPISODE_GROUPING, false)) {
            tvSort.setVisibility(View.GONE);
            mGridView = findViewById(R.id.mGridViews);
        } else {
            mGridView = findViewById(R.id.mGridViewk);
        }
        mGridView.setHasFixedSize(true);
        //mGridView.setHasFixedSize(false);
        tvHotList3 = findViewById(R.id.tvHotList3);
        tvHotList3.setLayoutManager(new V7GridLayoutManager(this.mContext, 5));
        homedeVodAdapter = new HomedeVodAdapter();
        this.mGridViewLayoutMgr = new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 10 : 11);
        mGridView.setLayoutManager(mGridViewLayoutMgr);
        seriesAdapter = new SeriesAdapter();
        mGridView.setAdapter(seriesAdapter);

        mNumberClassification = findViewById(R.id.mNumberClassification);
        mNumberClassification.setHasFixedSize(true);
        mNumberClassification.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        mClassifyAdapter = new SeriesFlagAdapter();
        mNumberClassification.setAdapter(mClassifyAdapter);


        mGridViewFlag = findViewById(R.id.mGridViewFlag);
        mGridViewFlag.setHasFixedSize(true);
        mGridViewFlag.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        seriesFlagAdapter = new SeriesFlagAdapter();
        mGridViewFlag.setAdapter(seriesFlagAdapter);
        isReverse = false;
        firstReverse = false;
        preFlag = "";
        if (showPreview) {
            playFragment = new PlayFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.previewPlayer, playFragment).commit();
            getSupportFragmentManager().beginTransaction().show(playFragment).commitAllowingStateLoss();
            tvPlay.setVisibility(View.VISIBLE);
            tvPlay.requestFocus();
        }
        seriesGroupAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_series_group, seriesGroupOptions) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                TextView tvSeries = helper.getView(R.id.tvSeriesGroup);
                LinearLayout tvSeriesg = helper.getView(R.id.tvSeriesG);
                LinearLayout strsg[] = { tvSeriesg };
                drawableLinearLayout(strsg, 2);
                tvSeries.setText(item);
            }
        };
        homedeVodAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ApiConfig.get().getSourceBeanList().isEmpty())
                    return;
                Movie.Video vod = ((Movie.Video) adapter.getItem(position));
                if (vod.id != null && !vod.id.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", vod.id);
                    bundle.putString("sourceKey", vod.sourceKey);
                    jumpActivity(DetailActivity.class, bundle);
                } else {
                    Intent newIntent;
                    newIntent = new Intent(mContext, SearchActivity.class);
                    newIntent.putExtra("title", vod.name);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(newIntent);
                }
            }
        });
        tvHotList3.setAdapter(homedeVodAdapter);
        LinearLayout[] strs = { tvPush, tvQuickSearch, tvSort, tvCollect, tvPlay };
        drawableLinearLayout(strs, 5);
        TextView tvPushs = findViewById(R.id.tvPushs);
        TextView tvPlays = findViewById(R.id.tvPlays);
        TextView tvQuickSearchs = findViewById(R.id.tvQuickSearchs);
        colorTextView(tvSort2,tvSort);
        colorTextView(tvQuickSearchs,tvQuickSearch);
        colorTextView(tvCollect2,tvCollect);
        colorTextView(tvPushs,tvPush);
        colorTextView(tvPlays,tvPlay);
        llPlayerFragmentContainerBlock.setOnClickListener((view -> toggleFullPreview()));
        tvHotList3.setVisibility(View.VISIBLE);
        tvWntj.setVisibility(View.VISIBLE);

        tvSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vodInfo != null && vodInfo.seriesMap.size() > 0) {
                    vodInfo.reverseSort = !vodInfo.reverseSort;
                    isReverse = !isReverse;
                    vodInfo.reverse();
                    vodInfo.playIndex = (vodInfo.seriesMap.get(vodInfo.playFlag).size() - 1) - vodInfo.playIndex;
                    String text = tvSort2.getText().toString();
                    if ("正序显示".equals(text)) {
                        tvSort1.setImageResource(R.drawable.ic_sort_down);
                        tvSort2.setText("倒序显示");
                    } else {
                        tvSort1.setImageResource(R.drawable.ic_sort_up);
                        tvSort2.setText("正序显示");
                    }
//                    insertVod(sourceKey, vodInfo);
                    firstReverse = true;
                    if (!CollectionUtils.isEmpty(mClassifyAdapter.getData())) {
                        int newTarget = mClassifyAdapter.getData().size() - 1 - mNumberClassification.getSelectedPosition();
                        mNumberClassification.setSelection(newTarget);
                        Collections.reverse(mClassifyAdapter.getData());
                        mClassifyAdapter.notifyDataSetChanged();
                    }
                    seriesAdapter.notifyDataSetChanged();
                }
            }
        });
        tvPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushDialog pushDialog = new PushDialog(mContext);
                pushDialog.show();
            }
        });
        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (showPreview) {
                    toggleFullPreview();
                    if (firstReverse) {
                        jumpToPlay();
                        firstReverse = false;
                    }
                } else {
                    jumpToPlay();
                }
            }
        });

        tvQuickSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuickSearch();
                QuickSearchDialog quickSearchDialog = new QuickSearchDialog(DetailActivity.this);
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, quickSearchData));
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, quickSearchWord));
                quickSearchDialog.show();
                if (pauseRunnable != null && pauseRunnable.size() > 0) {
                    searchExecutorService = Executors.newFixedThreadPool(thread);
                    for (Runnable runnable : pauseRunnable) {
                        searchExecutorService.execute(runnable);
                    }
                    pauseRunnable.clear();
                    pauseRunnable = null;
                }
                quickSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        try {
                            if (searchExecutorService != null) {
                                pauseRunnable = searchExecutorService.shutdownNow();
                                searchExecutorService = null;
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                });
            }
        });
        tvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tvCollect2.getText().toString();
                if ("加入收藏".equals(text)) {
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
            }
        });
        tvDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                DevDialog dialog = new DevDialog(mContext);
                dialog.show();
            }
        });
        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = false;
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                seriesSelect = true;
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
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
                            seriesFlagAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(position);
                    flag.selected = true;
                    // clean pre flag select status
                    if (vodInfo.seriesMap.get(vodInfo.playFlag).size() > vodInfo.playIndex) {
                        vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).selected = false;
                    }
                    vodInfo.playFlag = newFlag;
                    seriesFlagAdapter.notifyItemChanged(position);
                    refreshList();
                }
                seriesFlagFocus = itemView;
            }

            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                refresh(itemView, position);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                refresh(itemView, position);
                jumpToPlay();
                firstReverse = false;
            }
        });

        seriesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                List<VodInfo.VodSeries> allData = vodInfo.seriesMap.get(vodInfo.playFlag);
                if (vodInfo != null && allData.size() > 0) {
                    boolean reload = false;
                    //清理以前选中的剧集
                    for (VodInfo.VodSeries allItem : allData) {
                        allItem.selected = false;
                    }
//                    seriesAdapter.notifyDataSetChanged();
                    VodInfo.VodSeries clickItem = seriesAdapter.getData().get(position);
                    int clickPlayIndex = allData.indexOf(clickItem);
                    //解决倒叙不刷新
                    if (vodInfo.playIndex != clickPlayIndex) {
                        seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                        clickItem.selected = true;
                        seriesAdapter.notifyItemChanged(position);
                        vodInfo.playIndex = clickPlayIndex;
                        reload = true;
                    }
                    //解决当前集不刷新的BUG
                    if (!preFlag.isEmpty() && !vodInfo.playFlag.equals(preFlag)) {
                        reload = true;
                    }

                    seriesAdapter.getData().get(position).selected = true;
                    seriesAdapter.notifyItemChanged(position);
//                    //选集全屏 想选集不全屏的注释下面一行
                    if (showPreview && !fullWindows) toggleFullPreview();
                    if (!showPreview || reload) {
                        jumpToPlay();
                        firstReverse = false;
                    }
                }
            }
        });
        mClassifyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VodInfo.VodSeriesFlag item = mClassifyAdapter.getItem(position);
                if (item != null) {
                    if (item.selected) {//如果点击的已经是选中的了，return
                        return;
                    }
                    item.selected = true;
                    mClassifyAdapter.notifyItemChanged(position);
                }
                if (mLastSelectedVodSeriesFlag != null) {
                    mLastSelectedVodSeriesFlag.selected = false;
                    int index = mClassifyAdapter.getData().indexOf(mLastSelectedVodSeriesFlag);
                    mClassifyAdapter.notifyItemChanged(index);
                }
                selectss = position;
                mLastSelectedVodSeriesFlag = item;
               // mNumberClassification.scrollToPosition(position);
                mNumberClassification.setSelection(position);
                List<VodInfo.VodSeries> vodSeries = mapData.get(item);
                if (vodSeries != null) {
                    seriesAdapter.setNewData(vodSeries);
                }
            }
        });
        if (Hawk.get(HawkConfig.PLAYBACK_PLAY, false)) {
            if (showPreview && !fullWindows) toggleFullPreview();
        }
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) DetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                String cpContent = "视频ID：" + vodId + "视频名称：" + mVideo.name + "，图片地址：" + (mVideo == null ? "" : mVideo.pic + "，URL：" + Objects.requireNonNull(vodInfo.seriesMap.get(vodInfo.playFlag)).get(vodInfo.playIndex).url);
                ClipData clipData = ClipData.newPlainText(null, cpContent);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(DetailActivity.this, "已复制" + cpContent, Toast.LENGTH_SHORT).show();
            }
        });
      setLoadSir(llLayout);
    }
    private void initHomeHotVod3(HomedeVodAdapter adapter) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setNewData(loadHots(mVideo.vodlist));
            }
        });
    }
    private ArrayList<Movie.Video> loadHots(String json) {
        json = "{\"code\":200,\"msg\":\"success\",\"data\":" + json + "}";
        ArrayList<Movie.Video> result = new ArrayList<>();
        try {
            JsonObject infoJson = new Gson().fromJson(json, JsonObject.class);
            JsonArray array = infoJson.getAsJsonArray("data");
            for (JsonElement ele : array) {
                JsonObject obj = (JsonObject) ele;
                Movie.Video vod = new Movie.Video();
                vod.id = obj.get("vod_id").getAsString();
                vod.name = obj.get("vod_name").getAsString();
                vod.note = obj.get("vod_remarks").getAsString();
                vod.area = obj.get("vod_area").getAsString();
                vod.score = obj.get("vod_score").getAsString();
                vod.tid = Integer.parseInt(obj.get("type_id").getAsString());
                vod.tyid = Integer.parseInt(obj.get("type_id_1").getAsString());
                vod.isend = Integer.parseInt(obj.get("vod_isend").getAsString());
                vod.picthumb = obj.get("vod_pic_thumb").getAsString();
                vod.pic = obj.get("vod_pic").getAsString();
                vod.sourceKey = "htv_hrys";
                result.add(vod);
            }
        } catch (Throwable th) {

        }
        return result;
    }

    private void onGridViewFocusChange(View view, boolean hasFocus) {
        if (llPlayerFragmentContainerBlock.getVisibility() != View.VISIBLE) return;
        llPlayerFragmentContainerBlock.setFocusable(!hasFocus);
    }
//    private void initCheckedSourcesForSearch() {
//        mCheckSources = SearchHelper.getSourcesForSearch();
//    }

//    private List<Runnable> pauseRunnable = null;

    private void jumpToPlay() {
        if (vodInfo != null && vodInfo.seriesMap.get(vodInfo.playFlag).size() > 0) {
            preFlag = vodInfo.playFlag;
            Bundle bundle = new Bundle();
            //保存历史
            insertVod(firstsourceKey, vodInfo);
            //insertVod(sourceKey, vodInfo);
            bundle.putString("sourceKey", sourceKey);
            bundle.putSerializable("VodInfo", vodInfo);
//            App.getInstance().setVodInfo(vodInfo);
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
 //                   bundle.putSerializable("VodInfo", previewVodInfo);
                    App.getInstance().setVodInfo(previewVodInfo);
                }
                playFragment.setData(bundle);
            } else {
                jumpActivity(PlayActivity.class, bundle);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pushVod(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_PUSH_VOD) {
            if (event.obj != null) {
                List<String> data = (List<String>) event.obj;
                OkGo.getInstance().cancelTag("pushVod");
                OkGo.<String>post("http://" + data.get(0) + ":" + data.get(1) + "/action")
                        .tag("pushVod")
                        .params("id", vodId)
                        .params("sourceKey", sourceKey)
                        .params("do", "mirror")
                        .execute(new AbsCallback<String>() {
                            @Override
                            public String convertResponse(okhttp3.Response response) throws Throwable {
                                if (response.body() != null) {
                                    return response.body().string();
                                } else {
                                    Toast.makeText(DetailActivity.this, "推送失败，填的地址可能不对", Toast.LENGTH_SHORT).show();
                                    throw new IllegalStateException("网络请求错误");
                                }
                            }

                            @Override
                            public void onSuccess(Response<String> response) {
                                String r = response.body();
                                if ("mirrored".equals(r))
                                    Toast.makeText(DetailActivity.this, "推送成功", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(DetailActivity.this, "推送失败，远端tvbox版本不支持", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                Toast.makeText(DetailActivity.this, "推送失败，填的地址可能不对", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void play(PlayEvent playEvent) {
        vodInfo = playEvent.vodInfo;
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
        if (vodInfo.seriesMap.get(vodInfo.playFlag).size() <= vodInfo.playIndex) {
            vodInfo.playIndex = 0;
        }
        int setSeries = vodInfo.seriesMap.get(vodInfo.playFlag).size();
        if (setSeries < 2) {
            tvSort.setVisibility(View.GONE);
        }
        if (vodInfo.seriesMap.get(vodInfo.playFlag) != null) {
            boolean canSelect = true;
            for (int j = 0; j < vodInfo.seriesMap.get(vodInfo.playFlag).size(); j++) {
                if(vodInfo.seriesMap.get(vodInfo.playFlag).get(j).selected){
                    canSelect = false;
                    break;
                }
            }
            if(canSelect)vodInfo.seriesMap.get(vodInfo.playFlag).get(vodInfo.playIndex).selected = true;
        }

        Paint pFont = new Paint();
//        pFont.setTypeface(Typeface.DEFAULT );
        Rect rect = new Rect();

        List<VodInfo.VodSeries> list = vodInfo.seriesMap.get(vodInfo.playFlag);
        int listSize = list.size();
        int w = 1;
        for(int i =0; i < listSize; ++i){
            String name = list.get(i).name;
            pFont.getTextBounds(name, 0, name.length(), rect);
            if(w < rect.width()){
                w = rect.width();
            }
        }
        w += 32;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth()/3;
        int offset = screenWidth/w;
        if(!Hawk.get(HawkConfig.EPISODE_GROUPING, false)) {
            if (offset <= 2) offset = 2;
            if (offset > 6) offset = 10;
        } else {
            offset = 10;
        }
        this.mGridViewLayoutMgr.setSpanCount(offset);
        loadVodInfo();
    }
    private void loadVodInfo() {
            if (VodInfoClassifyUtil.checkVodInfoNeedClassify(vodInfo.seriesMap.get(vodInfo.playFlag)) && Hawk.get(HawkConfig.EPISODE_GROUPING, false)) {
                 mapData = VodInfoClassifyUtil.getClassifyData(vodInfo.seriesMap.get(vodInfo.playFlag));
                List<VodInfo.VodSeries> data = new ArrayList<>();
                Iterator<Map.Entry<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>>> iterator = mapData.entrySet().iterator();
                List<VodInfo.VodSeriesFlag> classifyData = new ArrayList<>();
                while (iterator.hasNext()) {
                    Map.Entry<VodInfo.VodSeriesFlag, List<VodInfo.VodSeries>> next = iterator.next();
                    VodInfo.VodSeriesFlag key = next.getKey();
                    classifyData.add(key);
                    if (data.isEmpty()) {//默认第一个分类里的数据
                        mLastSelectedVodSeriesFlag = key;
                        data.addAll(next.getValue());
                    }
                }
                mClassifyAdapter.setNewData(classifyData);
                seriesAdapter.setNewData(data);
                mNumberClassification.setVisibility(View.VISIBLE);
            } else {
                mNumberClassification.setVisibility(View.GONE);
                seriesAdapter.setNewData(vodInfo.seriesMap.get(vodInfo.playFlag));
            }
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.detailResult.observe(this, new Observer<AbsXml>() {
            @Override
            public void onChanged(AbsXml absXml) {
                if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
                    showSuccess();
                    if(!TextUtils.isEmpty(absXml.msg) && !absXml.msg.equals("数据列表")){
                        Toast.makeText(DetailActivity.this, absXml.msg, Toast.LENGTH_SHORT).show();
                        showEmpty();
                        return;
                    }
                    mVideo = absXml.movie.videoList.get(0);

                    mVideo.id = vodId;
                    if (TextUtils.isEmpty(mVideo.name))mVideo.name = "TVBox";

                    vodInfo = new VodInfo();
                    vodInfo.setVideo(mVideo);
                    vodInfo.sourceKey = mVideo.sourceKey;
                    tvName.setText(mVideo.name);
                    tvNote.setText(mVideo.note);
                    sourceKey = mVideo.sourceKey;
                    if (!TextUtils.isEmpty(mVideo.score)) {
                        tvScore.setText(mVideo.score + "分");
                    } else {
                        tvScore.setText(mVideo.score);
                    }
                    TVInstance.INSTANCE.setVodInfo(vodInfo);
                    //setTextShow(tvSite, "", ApiConfig.get().getSource(mVideo.sourceKey).getName());
                    setTextShow(tvYear, "", mVideo.year == 0 ? "" : String.valueOf(mVideo.year));
                    setTextShow(tvArea, "•", mVideo.area);
                   // setTextShow(tvLang, "•", mVideo.lang);
                    if (!firstsourceKey.equals(sourceKey)) {
                        setTextShow(tvType, "类型", "[" + ApiConfig.get().getSource(sourceKey).getName() + "] 解析");
                    } else {
                        setTextShow(tvType, "类型", mVideo.type);
                    }
                    setTextShow(tvDouban, "豆瓣", mVideo.douban);
                    if (!TextUtils.isEmpty(mVideo.writer) || !TextUtils.isEmpty(mVideo.actor) || !TextUtils.isEmpty(mVideo.director)) {
                        tvWad.setVisibility(View.VISIBLE);
                    } else {
                        tvWad.setVisibility(View.GONE);
                    }
                    setTextShow(tvWriter, "编剧：", mVideo.writer);
                    setTextShow(tvActor, "演员：", mVideo.actor);
                    setTextShow(tvDirector, "导演：", mVideo.director);
                    setTextShow(tvDes, "简介：", removeHtmlTag(mVideo.des));
                    initHomeHotVod3(homedeVodAdapter);
                    if (!TextUtils.isEmpty(mVideo.pic)) {
                        Picasso.get()
                                .load(DefaultConfig.checkReplaceProxy(mVideo.pic))
                                .transform(new RoundTransformation(MD5.string2MD5(mVideo.pic + mVideo.name))
                                        .centerCorp(true)
                                        .override(AutoSizeUtils.mm2px(mContext, 300), AutoSizeUtils.mm2px(mContext, 400))
                                        .roundRadius(AutoSizeUtils.mm2px(mContext, 0), RoundTransformation.RoundType.ALL))
                                .placeholder(R.drawable.img_loading_placeholder)
                                .error(R.drawable.img_loading_placeholder)
                                .into(ivThumb);
                    } else {
                        ivThumb.setImageResource(R.drawable.img_loading_placeholder);
                    }

                    if (vodInfo.seriesMap != null && vodInfo.seriesMap.size() > 0) {
                        mGridViewFlag.setVisibility(View.VISIBLE);
                        mGridView.setVisibility(View.VISIBLE);
                       // tvPlay.setVisibility(View.VISIBLE);
                        mEmptyPlayList.setVisibility(View.GONE);

                        VodInfo vodInfoRecord = RoomDataManger.getVodInfo(sourceKey, vodId);
                        // 读取历史记录
                        if (vodInfoRecord != null) {
                            vodInfo.playIndex = Math.max(vodInfoRecord.playIndex, 0);
                            vodInfo.playFlag = vodInfoRecord.playFlag;
                            vodInfo.playerCfg = vodInfoRecord.playerCfg;
                            vodInfo.reverseSort = vodInfoRecord.reverseSort;
                        } else {
                            vodInfo.playIndex = 0;
                            vodInfo.playFlag = null;
                            vodInfo.playerCfg = "";
                            vodInfo.reverseSort = false;
                        }

                        if (vodInfo.reverseSort) {
                            vodInfo.reverse();
                        }

                        if (vodInfo.playFlag == null || !vodInfo.seriesMap.containsKey(vodInfo.playFlag))
                            vodInfo.playFlag = (String) vodInfo.seriesMap.keySet().toArray()[0];

                        int flagScrollTo = 0;
                        for (int j = 0; j < vodInfo.seriesFlags.size(); j++) {
                            VodInfo.VodSeriesFlag flag = vodInfo.seriesFlags.get(j);
                            if (flag.name.equals(vodInfo.playFlag)) {
                                flagScrollTo = j;
                                flag.selected = true;
                            } else
                                flag.selected = false;
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
                        // startQuickSearch();
                    } else {
                        mGridViewFlag.setVisibility(View.GONE);
                        mGridView.setVisibility(View.GONE);

                        tvPlay.setVisibility(View.GONE);
                        mEmptyPlayList.setVisibility(View.VISIBLE);
                    }
                } else {
                    showEmpty();
                    llPlayerFragmentContainer.setVisibility(View.GONE);
                    llPlayerFragmentContainerBlock.setVisibility(View.GONE);
                }
            }
        });
    }

    private String getHtml(String label, String content) {
        if (content == null) {
            content = "";
        }
        if (label.contains("豆瓣")) {
            return "<font color=\"#FF6600\">&nbsp;" + content + "</font>";
        }
        if (label.equals("类型")) {
            content = content.replace(","," • ");
            content = content.replace("/","•");
            return " • " + content;
        }
        if (label.contains("演员")) {
            content = content.replace(","," / ");
            return label + content;
        }
        if (label.length() == 1) {
            return label + " " + content + " ";
        }
        return label +  content + " ";
    }

    private void initData() {
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
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_REFRESH) {
            if (event.obj != null) {
                if (event.obj instanceof Integer) {
                    int freshIndex = (int) event.obj;
                    //% 30;
                    if (Hawk.get(HawkConfig.EPISODE_GROUPING, false) && freshIndex > 9) {
                        List<VodInfo.VodSeries> allData = vodInfo.seriesMap.get(vodInfo.playFlag);
                        if (allData != null) {
                            for (VodInfo.VodSeries allItem : allData) {
                                allItem.selected = false;
                            }
                        }
                        vodInfo.seriesMap.get(vodInfo.playFlag).get(freshIndex).selected = true;
                        seriesAdapter.notifyDataSetChanged();
                    } else {
                        for (int j = 0; j < seriesAdapter.getData().size(); j++) {
                            seriesAdapter.getData().get(j).selected = false;
                            seriesAdapter.notifyItemChanged(j);
                        }
                        seriesAdapter.getData().get(freshIndex).selected = true;
                        seriesAdapter.notifyItemChanged(freshIndex);
                    }
                    scrollToPlayPosAndRequestFoucuse(freshIndex);
                    vodInfo.playIndex = (int) event.obj;
                    //保存历史
                    insertVod(firstsourceKey, vodInfo);
                    //insertVod(sourceKey, vodInfo);
                } else if (event.obj instanceof JSONObject) {
                    vodInfo.playerCfg = ((JSONObject) event.obj).toString();
                    //保存历史
                    insertVod(firstsourceKey, vodInfo);
                    //insertVod(sourceKey, vodInfo);
                }

            }
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_SELECT) {
            if (event.obj != null) {
                Movie.Video video = (Movie.Video) event.obj;
                loadDetail(video.id, video.sourceKey);
            }
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_WORD_CHANGE) {
            if (event.obj != null) {
                String word = (String) event.obj;
                switchSearchWord(word);
            }
        } else if (event.type == RefreshEvent.TYPE_QUICK_SEARCH_RESULT) {
            try {
                searchData(event.obj == null ? null : (AbsXml) event.obj);
            } catch (Exception e) {
                searchData(null);
            }
        }
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
        searchResult();
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
        // 分词
        OkGo.<String>get("http://api.pullword.com/get.php?source=" + URLEncoder.encode(searchTitle) + "&param1=0&param2=0&json=1")
                .tag("fenci")
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
                            for (JsonElement je : new Gson().fromJson(json, JsonArray.class)) {
                                quickSearchWord.add(je.getAsJsonObject().get("t").getAsString());
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                        List<String> words = new ArrayList<>(new HashSet<>(quickSearchWord));
                        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH_WORD, words));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

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
        searchExecutorService = Executors.newFixedThreadPool(thread);
        List<SourceBean> searchRequestList = new ArrayList<>();
        searchRequestList.addAll(ApiConfig.get().getSourceBeanList());
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        searchRequestList.remove(home);
        searchRequestList.add(0, home);

        ArrayList<String> siteKey = new ArrayList<>();
        for (SourceBean bean : searchRequestList) {
            if (!bean.isSearchable() || !bean.isQuickSearch()) {
                continue;
            }
          /*  if (mCheckSources != null && !mCheckSources.containsKey(bean.getKey())) {
                continue;
            }*/
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
                if (matchSearchResult(video.name, searchTitle))
                    data.add(video);
            }
            quickSearchData.addAll(data);
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_QUICK_SEARCH, data));
        }
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
        if (seriesSelect) {
            if (seriesFlagFocus != null && !seriesFlagFocus.isFocused()) {
                seriesFlagFocus.requestFocus();
                return;
            }
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
        tvQuickSearch.setFocusable(!fullWindows);
        tvPush.setFocusable(!fullWindows);
        tvDes.setFocusable(!fullWindows);
        tvName.setFocusable(!fullWindows);
//        if (playFragment.mController != null) {
//            playFragment.mController.setIsFullScreen(fullWindows);
//        }
        if (!fullWindows && vodInfo != null) {//恢复焦点
            scrollToPlayPosAndRequestFoucuse(vodInfo.playIndex);
        }
        toggleSubtitleTextSize();
    }
    private void scrollToPlayPosAndRequestFoucuse(int freshIndex) {
        if (mGridView != null && mGridView.getLayoutManager() != null) {
            mGridView.getLayoutManager().scrollToPosition(freshIndex);
            new Handler().postDelayed(() -> {
                View requestView = mGridView.getLayoutManager().findViewByPosition(freshIndex);
                if (requestView != null) {
                    requestView.requestFocus();
                }
            }, 300);

        }
    }
    void toggleSubtitleTextSize() {
        int subtitleTextSize  = SubtitleHelper.getTextSize(this);
        if (!fullWindows) {
            subtitleTextSize *= 0.6;
        }
        EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SUBTITLE_SIZE_CHANGE, subtitleTextSize));
    }
}
