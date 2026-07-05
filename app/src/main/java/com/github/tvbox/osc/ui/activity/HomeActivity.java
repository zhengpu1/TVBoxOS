package com.github.tvbox.osc.ui.activity;

import static com.github.tvbox.osc.util.RemoteConfig.checkUpdatess;
import static com.github.tvbox.osc.util.RemoteConfig.isFirstStart;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager.widget.ViewPager;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.base.baseActivity;
import com.github.tvbox.osc.bean.AbsSortXml;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.MovieSort;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.adapter.HomePageAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.adapter.SortAdapter;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.TipDialog;
import com.github.tvbox.osc.ui.dialog.UpdateDialog;
import com.github.tvbox.osc.ui.fragment.GridFragment;
import com.github.tvbox.osc.ui.fragment.UserFragment;
import com.github.tvbox.osc.ui.tv.widget.DefaultTransformer;
import com.github.tvbox.osc.ui.tv.widget.FixedSpeedScroller;
import com.github.tvbox.osc.ui.tv.widget.ViewObj;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.util.ScreenUtils;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomeActivity extends baseActivity {
    private LinearLayout topLayout;
    private LinearLayout contentLayout;
    private TextView tvDate;
    private TextView tvName;
    private ImageView tvLogo;
    private LinearLayout tvFind;
    private ImageView tvStyle;
    private LinearLayout tvMenu;
    private LinearLayout tvAPush;
    private TvRecyclerView mGridView;
    private ViewPager mViewPager;
    private SourceViewModel sourceViewModel;
    private SortAdapter sortAdapter;
    private HomePageAdapter pageAdapter;
    private View currentView;
    private List<BaseLazyFragment> fragments = new ArrayList<>();
    public static List<BaseLazyFragment> fragmentsa = new ArrayList<>();
    private boolean isDownOrUp = false;
    private boolean sortChange = false;
    private int currentSelected = 0;
    private int sortFocused = 0;
    public static int sortFocuseda = 0;
    public View sortFocusView = null;
    private Handler mHandler = new Handler();
    private long mExitTime = 0;
    private TextView tViewd;
    private TextView tViewk;
    private TextView tViewc;
    private String loadingSourceKey;
    private String previousHomeName;
    private boolean homeSortLoading = false;
    private boolean refreshHomeRec = false;
    View lastView = null;
    private Runnable mRunnable = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void run() {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat = new SimpleDateFormat("MM月dd日 EE HH:mm");
            tvDate.setText(timeFormat.format(date));
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_home;
    }

    boolean useCacheConfig = false;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        ControlManager.get().startServer();
        initView();
        initViewModel();
        useCacheConfig = false;
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            useCacheConfig = bundle.getBoolean("useCache", false);
        }
        initData();
    }

    private void initView() {
        this.topLayout = findViewById(R.id.topLayout);
        this.tvFind = findViewById(R.id.tvFind);
        this.tvStyle = findViewById(R.id.tvStyle);
        this.tvMenu = findViewById(R.id.tvMenu);
        this.tvAPush = findViewById(R.id.tvAPush);
        this.tvDate = findViewById(R.id.tvDate);
        this.tvName = findViewById(R.id.tvName);
        this.tvLogo = findViewById(R.id.tvLogo);
        this.contentLayout = findViewById(R.id.contentLayout);
        this.mGridView = findViewById(R.id.mGridView);
        this.mViewPager = findViewById(R.id.mViewPager);
        this.tViewd = findViewById(R.id.tViewd);
        this.tViewk = findViewById(R.id.tViewk);
        this.tViewc = findViewById(R.id.tViewc);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mGridView.getLayoutManager() == null || mGridView.getLayoutManager().findViewByPosition(position) == null) {
                    return;
                }
                View curView = mGridView.getLayoutManager().findViewByPosition(position);
                mGridView.onFocusChange(curView, true);
                curView.requestFocus();
                if (lastView != null) {
                    lastView.clearFocus();
                    lastView.setSelected(false);
                    lastView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                    TextView textView = lastView.findViewById(R.id.tvTitle);
                    textView.getPaint().setFakeBoldText(false);
                  //  textView.setTextColor(HomeActivity.this.getResources().getColor(R.color.color_BBFFFFFF));
                    lastView.findViewById(R.id.tvFilter).setVisibility(View.GONE);
                }
                lastView = curView;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        this.sortAdapter = new SortAdapter();
        this.mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        this.mGridView.setSpacingWithMargins(0, AutoSizeUtils.dp2px(this.mContext, 5.0f));
        this.mGridView.setAdapter(this.sortAdapter);
        if (!ScreenUtils.isTvOrBox(mContext)) {
            tvAPush.setVisibility(View.GONE);
        }
        ImageView[] strls = { tvStyle };
        drawableImageView(strls, 8);
        LinearLayout[] strlsa = {tvMenu, tvAPush, tvFind};
        drawableLinearLayout(strlsa, 3);
        this.mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            public void onItemPreSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                if (view != null && !HomeActivity.this.isDownOrUp) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = view.findViewById(R.id.tvTitle);
                            textView.getPaint().setFakeBoldText(false);
                            if (sortFocused == p) {
                                view.animate().scaleX(1.02f).scaleY(1.02f).setInterpolator(new BounceInterpolator()).setDuration(400).start();
                            } else {
                                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(400).start();
                                view.findViewById(R.id.tvFilter).setVisibility(View.GONE);
                                view.findViewById(R.id.tvFilterColor).setVisibility(View.GONE);
                            }
                            textView.invalidate();
                        }

                        public int p = position;
                    }, 10);
                }
            }

            public void onItemSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                if (view != null) {
                    fragmentsa = fragments;
                    sortFocuseda = sortFocused;
                    HomeActivity.this.currentView = view;
                    HomeActivity.this.isDownOrUp = false;
                    HomeActivity.this.sortChange = true;
                    view.animate().scaleX(1.1f).scaleY(1.1f).setInterpolator(new BounceInterpolator()).setDuration(400).start();
                    TextView textView = view.findViewById(R.id.tvTitle);
                    textView.getPaint().setFakeBoldText(true);
                    textView.invalidate();
                    MovieSort.SortData sortData = sortAdapter.getItem(position);
                    if (sortData != null && !sortData.filters.isEmpty()) {
                        showFilterIcon(sortData.filterSelectCount());
                    }
                    HomeActivity.this.sortFocusView = view;
                    HomeActivity.this.sortFocused = position;
                    mHandler.removeCallbacks(mDataRunnable);
                    mHandler.postDelayed(mDataRunnable, 200);
                }
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (itemView != null && currentSelected == position) {
                    BaseLazyFragment baseLazyFragment = fragments.get(currentSelected);
                    if ((baseLazyFragment instanceof GridFragment) && !sortAdapter.getItem(position).filters.isEmpty()) {// 弹出筛选
                        if (Hawk.get(HawkConfig.FILTER_LOCATION, false)) {
                            ((GridFragment) baseLazyFragment).toggleFilterBar();
                        } else {
                            ((GridFragment) baseLazyFragment).showFilter();
                        }
                    } else if (baseLazyFragment instanceof UserFragment) {
                        showSiteSwitch();
                    }
                }
            }
        });
        this.mGridView.setOnInBorderKeyEventListener((direction, view) -> {
            if(direction == View.FOCUS_UP){
                BaseLazyFragment baseLazyFragment = fragments.get(sortFocused);
                if (baseLazyFragment instanceof UserFragment) {
                    refreshHomeSort();
                    return true;
                }
                if ((baseLazyFragment instanceof GridFragment) ) {// 弹出筛选
                    ((GridFragment) baseLazyFragment).forceRefresh();
                    return true;
                }
            }
            if (direction != View.FOCUS_DOWN) {
                return false;
            }
            isDownOrUp = true;
            BaseLazyFragment baseLazyFragment = fragments.get(sortFocused);
            if (!(baseLazyFragment instanceof GridFragment)) {
                return false;
            }
            if (!((GridFragment) baseLazyFragment).isLoad()) {
                return true;
            }
            return false;
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if(dataInitOk && jarInitOk){
                    String jar=ApiConfig.get().getHomeSourceBean().getJar();
                    String jarUrl=!jar.isEmpty()?jar:ApiConfig.get().getSpider();
                    String jarSource = jarUrl.split(";md5;")[0];
                    File cspCacheDir = new File(FileUtils.getFilePath() + "/csp/" + MD5.string2MD5(jarSource) + ".jar");
                    File jarCacheDir = new File(FileUtils.getCachePath() + "/jar/" + MD5.string2MD5(jarSource) + ".jar");
                    File jarFullCacheDir = new File(FileUtils.getCachePath() + "/jar/" + MD5.string2MD5(jarUrl) + ".jar");
                    Toast.makeText(mContext, "缓存已清除", Toast.LENGTH_LONG).show();
                    new Thread(() -> {
                        try {
                            FileUtils.deleteFile(cspCacheDir);
                            FileUtils.deleteFile(jarCacheDir);
                            FileUtils.deleteFile(jarFullCacheDir);
                            FileUtils.clearSpiderCacheFiles();
                            ApiConfig.get().clearSpiderCache();
                            refreshHome();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                }else {
                    jumpActivity(SettingActivity.class);
                }
            }
        });;
        tvName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(dataInitOk && jarInitOk){
                    reloadHome();
                }else {
                    jumpActivity(SettingActivity.class);
                }
                return true;
            }
        });
        tvLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = mContext.getCacheDir();
                FileUtils.recursiveDelete(dir);
                Toast.makeText(HomeActivity.this, "缓存已清除", Toast.LENGTH_SHORT).show();
            }
        });
        tvLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(dataInitOk && jarInitOk){
                    reloadHome();
                }else {
                    jumpActivity(SettingActivity.class);
                }
                return true;
            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
            }
        });
        tvFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpActivity(SearchActivity.class);
            }
        });
        tvAPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpActivity(PushActivity.class);
            }
        });
        tvStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Hawk.put(HawkConfig.HOME_REC_STYLE, !Hawk.get(HawkConfig.HOME_REC_STYLE, false));
                    tvMenu.setVisibility(View.VISIBLE);
                    if (Hawk.get(HawkConfig.HOME_REC_STYLE, false)) {
                        UserFragment.tvHotList1.setVisibility(View.GONE);
                        UserFragment.tvHotList4.setVisibility(View.GONE);
                        UserFragment.tvHotList2.setVisibility(View.VISIBLE);
                        UserFragment.tvSearch.setVisibility(View.VISIBLE);
                        UserFragment.tvSetting.setVisibility(View.GONE);
                        UserFragment.tvHistory.setVisibility(View.VISIBLE);
                        UserFragment.tvLive.setVisibility(View.VISIBLE);
                        UserFragment.tvPush.setVisibility(View.VISIBLE);
                        UserFragment.tvDrive.setVisibility(View.VISIBLE);
                        UserFragment.tvCollect.setVisibility(View.VISIBLE);
                        UserFragment.tvHistorys.setVisibility(View.GONE);
                        UserFragment.tvLives.setVisibility(View.GONE);
                        UserFragment.tvDrives.setVisibility(View.GONE);
                        UserFragment.tvCollects.setVisibility(View.GONE);
                        UserFragment.tvtxt.setVisibility(View.GONE);
                        tvFind.setVisibility(View.GONE);
                        tvAPush.setVisibility(View.GONE);
                        tvStyle.setImageResource(R.drawable.hm_left_right);
                       // reloadHome();
                    } else {
                        UserFragment.tvHotList1.setVisibility(View.VISIBLE);
                        UserFragment.tvHotList4.setVisibility(View.VISIBLE);
                        tvFind.setVisibility(View.VISIBLE);
                          //  tvAPush.setVisibility(View.VISIBLE);
                        tvAPush.setVisibility(ScreenUtils.isTvOrBox(mContext) ? View.GONE : View.VISIBLE);
                        UserFragment.tvHotList2.setVisibility(View.GONE);
                        UserFragment.tvSearch.setVisibility(View.GONE);
                        UserFragment.tvSetting.setVisibility(View.GONE);
                        UserFragment.tvHistory.setVisibility(View.GONE);
                        UserFragment.tvLive.setVisibility(View.GONE);
                        UserFragment.tvPush.setVisibility(View.GONE);
                        UserFragment.tvDrive.setVisibility(View.GONE);
                        UserFragment.tvCollect.setVisibility(View.GONE);
                        tvStyle.setImageResource(R.drawable.hm_up_down);
                        UserFragment.tvHistorys.setVisibility(View.VISIBLE);
                        UserFragment.tvLives.setVisibility(View.VISIBLE);
                        UserFragment.tvDrives.setVisibility(View.VISIBLE);
                        UserFragment.tvCollects.setVisibility(View.VISIBLE);
                        UserFragment.tvtxt.setVisibility(View.VISIBLE);
                       // reloadHome();
                    }
                } catch (Exception ex) {
                }
            }
        });
        tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpActivity(SettingActivity.class);
            }
        });
        setLoadSir(this.contentLayout);
    }
    private boolean skipNextUpdate = false;
    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, new Observer<AbsSortXml>() {
            @Override
            public void onChanged(AbsSortXml absXml) {
                if (skipNextUpdate) {
                    skipNextUpdate = false;
                    return;
                }
                if (!homeSortLoading && loadingSourceKey == null) {
                    return;
                }
                if (absXml != null && absXml.sourceKey != null && loadingSourceKey != null && !loadingSourceKey.equals(absXml.sourceKey)) {
                    return;
                }
                SourceBean home = ApiConfig.get().getHomeSourceBean();
                showSuccess();
                clearHomePages();
                List<MovieSort.SortData> newSortData;
                if (absXml != null && absXml.classes != null && absXml.classes.sortList != null) {
                    newSortData = DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), absXml.classes.sortList, true);
                } else {
                    newSortData = DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), new ArrayList<>(), true);
                }
                updateSortData(newSortData);
                initViewPager(absXml);
                updateHomeRec(absXml);
                if (home != null && home.getName() != null && !home.getName().isEmpty()) tvName.setText(home.getName());
                tvName.clearAnimation();
                homeSortLoading = false;
                loadingSourceKey = null;
                previousHomeName = null;
            }
        });
    }
    private void initViewModel999() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, new Observer<AbsSortXml>() {
            @Override
            public void onChanged(AbsSortXml absXml) {
                showSuccess();
                if (absXml != null && absXml.classes != null && absXml.classes.sortList != null) {
                    sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), absXml.classes.sortList, true));
                } else {
                    sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), new ArrayList<>(), true));
                }
                initViewPager(absXml);
            }
        });
    }

    private boolean dataInitOk = false;
    private boolean searchSpiderWarmStarted = false;

    private boolean jarInitOk = false;
    boolean HomeShow = Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false);
    private TipDialog mConfigErrorDialog;
    private void initData() {
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        if (HomeShow) {
            if (home != null && home.getName() != null && !home.getName().isEmpty()) {
                tvName.setText(home.getName());
                tvLogo.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
            }
        }
        if (dataInitOk && jarInitOk) {
            showLoading();
            warmSearchSpidersOnce();
            loadHomeSort(false);
            sourceViewModel.getSort(ApiConfig.get().getHomeSourceBean().getKey());
            if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                LOG.e("有存储权限");
            } else {
                LOG.e("无存储权限");
            }
            if (!useCacheConfig && Hawk.get(HawkConfig.HOME_LIVE, false)) {
                jumpActivity(LivePlayActivity.class);
            }
            return;
        }
        if (Hawk.get(HawkConfig.HOME_REC_STYLE, false)) {
            tvStyle.setImageResource(R.drawable.hm_left_right);
            tvFind.setVisibility(View.GONE);
            tvAPush.setVisibility(View.GONE);
        } else {
            tvStyle.setImageResource(R.drawable.hm_up_down);
            tvFind.setVisibility(View.VISIBLE);
            if (!ScreenUtils.isTvOrBox(mContext)) {
                tvAPush.setVisibility(View.VISIBLE);
            }
        }
        showLoading();
        if (dataInitOk && !jarInitOk) {
            if (!ApiConfig.get().getSpider().isEmpty()) {
                ApiConfig.get().loadJar(useCacheConfig, ApiConfig.get().getSpider(), new ApiConfig.LoadConfigCallback() {
                    @Override
                    public void success() {
                        jarInitOk = true;
                        mHandler.postDelayed(() -> {
                            if (checkUpdatess(mContext) && isFirstStart(mContext)) {
                                new Handler().postDelayed(() -> {
                                    UpdateDialog.checkUpdate(mContext, false);
                                }, 300);
                            }
                            initData();
                        }, 50);
                    }

                    @Override
                    public void retry() {

                    }

                    @Override
                    public void error(String msg) {
                        jarInitOk = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ("".equals(msg))
                                    Toast.makeText(HomeActivity.this, "jar加载错误", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                                initData();
                            }
                        },50);
                    }

                });
            }
            return;
        }
        ApiConfig.get().loadConfig(useCacheConfig, new ApiConfig.LoadConfigCallback() {
            TipDialog dialog = null;

            @Override
            public void retry() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                });
            }

            @Override
            public void success() {
                dataInitOk = true;
                if (ApiConfig.get().getSpider().isEmpty()) {
                    jarInitOk = true;
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 50);
            }

            @Override
            public void error(String msg) {
                if (msg.equalsIgnoreCase("-1")) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dataInitOk = true;
                            jarInitOk = true;
                            initData();
                        }
                    });
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog == null)
                            dialog = new TipDialog(HomeActivity.this, msg, "重试", "取消", new TipDialog.OnListener() {
                                @Override
                                public void left() {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            initData();
                                            dialog.hide();
                                        }
                                    });
                                }

                                @Override
                                public void right() {
                                    dataInitOk = true;
                                    jarInitOk = true;
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            initData();
                                            dialog.hide();
                                        }
                                    });
                                }

                                @Override
                                public void cancel() {
                                    dataInitOk = true;
                                    jarInitOk = true;
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            initData();
                                            dialog.hide();
                                        }
                                    });
                                }
                            });
                        if (!dialog.isShowing())
                            dialog.show();
                    }
                });
            }
        }, this);
    }
    private void warmSearchSpidersOnce() {
        if (searchSpiderWarmStarted) return;
        searchSpiderWarmStarted = true;
        ApiConfig.get().warmSearchSpiders();
    }

    private void loadHomeSort(boolean keepCurrentContent) {
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        homeSortLoading = keepCurrentContent;
        if (keepCurrentContent && home != null && home.getName() != null && !home.getName().isEmpty()) {
            previousHomeName = tvName.getText() == null ? null : tvName.getText().toString();
            tvName.setText(home.getName());
        }
        tvNameAnimation();
        if (home == null) {
            loadingSourceKey = null;
            if (!keepCurrentContent) showLoading();
            sourceViewModel.getSort(null);
            return;
        }
        loadingSourceKey = home.getKey();
        if (!keepCurrentContent) {
            showLoading();
        }
        sourceViewModel.getSort(loadingSourceKey);
    }

    private void initViewPager(AbsSortXml absXml) {
        if (sortAdapter.getData().size() > 0) {
            for (MovieSort.SortData data : sortAdapter.getData()) {
                if (data.id.equals("my0")) {
                    if (Hawk.get(HawkConfig.HOME_REC, HawkConfig.DEFAULT_HOME_REC) == 1 && absXml != null && absXml.videoList != null && absXml.videoList.size() > 0) {
                        fragments.add(UserFragment.newInstance(absXml.videoList));
                    } else {
                        fragments.add(UserFragment.newInstance(null));
                    }
                } else {
                    fragments.add(GridFragment.newInstance(data));
                }
            }
            pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
            try {
                Field field = ViewPager.class.getDeclaredField("mScroller");
                field.setAccessible(true);
                FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator());
                field.set(mViewPager, scroller);
                scroller.setmDuration(300);
            } catch (Exception e) {
            }
            mViewPager.setPageTransformer(true, new DefaultTransformer());
            mViewPager.setAdapter(pageAdapter);
            mViewPager.setCurrentItem(currentSelected, false);
        }
    }
    private void clearHomePages() {
        mHandler.removeCallbacks(mDataRunnable);
        currentSelected = 0;
        sortFocused = 0;
        sortChange = false;
        sortFocusView = null;
        currentView = null;
        if (pageAdapter != null) {
            mViewPager.setAdapter(null);
            pageAdapter.removeAll();
            pageAdapter = null;
        } else if (!fragments.isEmpty()) {
            fragments.clear();
        }
    }

    private void updateSortData(List<MovieSort.SortData> newSortData) {
        if (newSortData == null) {
            newSortData = new ArrayList<>();
        }
        List<MovieSort.SortData> oldSortData = sortAdapter.getData();
        if (oldSortData.isEmpty()
                || newSortData.isEmpty()
                || oldSortData.get(0) == null
                || newSortData.get(0) == null
                || !"my0".equals(oldSortData.get(0).id)
                || !"my0".equals(newSortData.get(0).id)) {
            sortAdapter.setNewData(newSortData);
            return;
        }
        int oldTailCount = oldSortData.size() - 1;
        if (oldTailCount > 0) {
            oldSortData.subList(1, oldSortData.size()).clear();
            sortAdapter.notifyItemRangeRemoved(1, oldTailCount);
        }
        if (newSortData.size() > 1) {
            oldSortData.addAll(newSortData.subList(1, newSortData.size()));
            sortAdapter.notifyItemRangeInserted(1, newSortData.size() - 1);
        }
    }

    @Override
    public void onBackPressed() {
        if (homeSortLoading) {
            cancelHomeSortLoading();
            return;
        }
        int i;
        if (this.fragments.size() <= 0 || this.sortFocused >= this.fragments.size() || (i = this.sortFocused) < 0) {
            exit();
            return;
        }
        BaseLazyFragment baseLazyFragment = this.fragments.get(i);
        if (baseLazyFragment instanceof GridFragment) {
            View view = this.sortFocusView;
            GridFragment grid = (GridFragment) baseLazyFragment;
            if (grid.restoreView()) {
                return;
            }// 还原上次保存的UI内容
            if (view != null && !view.isFocused()) {
                this.sortFocusView.requestFocus();
            } else if (this.sortFocused != 0) {
                this.mGridView.setSelection(0);
            } else {
                exit();
            }
        } else {
            exit();
        }
    }

    private void exit() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            EventBus.getDefault().unregister(this);
            AppManager.getInstance().appExit(0);
            ControlManager.get().stopServer();
            finish();
            super.onBackPressed();
        } else {
            mExitTime = System.currentTimeMillis();
            Toast.makeText(mContext, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        if (Hawk.get(HawkConfig.HOME_SHOW_SOURCE, false)) {
            if (home != null && home.getName() != null && !home.getName().isEmpty()) {
                tvName.setText(home.getName());
                tvName.setVisibility(View.VISIBLE);
                tvLogo.setVisibility(View.GONE);
            }
        } else {
            tvLogo.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
        }
        mHandler.post(mRunnable);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) throws IOException {
        if (event.type == RefreshEvent.TYPE_PUSH_URL) {
            if (ApiConfig.get().getSource("push_agent") != null) {
                Intent newIntent = new Intent(mContext, DetailActivity.class);
                newIntent.putExtra("id", (String) event.obj);
                newIntent.putExtra("sourceKey", "push_agent");
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                HomeActivity.this.startActivity(newIntent);
            }
        } else if (event.type == RefreshEvent.TYPE_FILTER_CHANGE) {
            if (currentView != null) {
                showFilterIcon((int) event.obj);
            }
        } else if (event.type == RefreshEvent.RE_LOAD_HOME_DATA) {
            dataInitOk = false;
            jarInitOk = false;
            initData();
        } else if (event.type == RefreshEvent.ALI_TOKEN) {
            Toast.makeText(mContext, "已获取到alitoken,重新加载数据", Toast.LENGTH_SHORT).show();
            Hawk.put(HawkConfig.ALI_TOKEN, event.obj + "");

            crFilewriteData(event.obj + "");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(App.getInstance(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                jumpActivity(HomeActivity.class);
            }, 1000);
        }
    }
    private void crFilewriteData(String msg){
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File saveFile = new File(root + "/hrtv/");
        if (!saveFile.exists()) { saveFile.mkdirs(); }
        FileUtils.writeSimple(msg.getBytes(), new File(saveFile, "tok.txt"));
    }

    private void showFilterIcon(int count) {
        boolean visible = count > 0;
        currentView.findViewById(R.id.tvFilterColor).setVisibility(visible ? View.VISIBLE : View.GONE);
        currentView.findViewById(R.id.tvFilter).setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private Runnable mDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (sortChange) {
                sortChange = false;
                if (sortFocused != currentSelected) {
                    currentSelected = sortFocused;
                    mViewPager.setCurrentItem(sortFocused, false);
                    if (sortFocused == 0) {
                        changeTop(false);
                    } else {
                        changeTop(true);
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (topHide < 0)
            return false;
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                showSiteSwitch();
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {

        }
        return super.dispatchKeyEvent(event);
    }
    TipDialog tipDialog = null;
    byte topHide = 0;

    private void changeTop(boolean hide) {
        ViewObj viewObj = new ViewObj(topLayout, (ViewGroup.MarginLayoutParams) topLayout.getLayoutParams());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                topHide = (byte) (hide ? 1 : 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (hide && topHide == 0) {
            animatorSet.playTogether(ObjectAnimator.ofObject(viewObj, "marginTop", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 10.0f),
                            AutoSizeUtils.mm2px(this.mContext, 0.0f)),
                    ObjectAnimator.ofObject(viewObj, "height", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 50.0f),
                            AutoSizeUtils.mm2px(this.mContext, 1.0f)),
                    ObjectAnimator.ofFloat(this.topLayout, "alpha", 1.0f, 0.0f));
            animatorSet.setDuration(200);
            animatorSet.start();
            return;
        }
        if (!hide && topHide == 1) {
            animatorSet.playTogether(ObjectAnimator.ofObject(viewObj, "marginTop", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 0.0f),
                            AutoSizeUtils.mm2px(this.mContext, 10.0f)),
                    ObjectAnimator.ofObject(viewObj, "height", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 1.0f),
                            AutoSizeUtils.mm2px(this.mContext, 50.0f)),
                    ObjectAnimator.ofFloat(this.topLayout, "alpha", 0.0f, 1.0f));
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AppManager.getInstance().appExit(0);
        ControlManager.get().stopServer();
    }
    private SelectDialog<SourceBean> mSiteSwitchDialog;
    void showSiteSwitch() {
        if (isActivityUnavailable()) return;
        List<SourceBean> sites = ApiConfig.get().getSourceBeanList();
        if (sites.isEmpty()) return;
        int select = sites.indexOf(ApiConfig.get().getHomeSourceBean());
        if (select < 0 || select >= sites.size()) select = 0;
        if (mSiteSwitchDialog == null) {
            mSiteSwitchDialog = new SelectDialog<>(HomeActivity.this);
            TvRecyclerView tvRecyclerView = mSiteSwitchDialog.findViewById(R.id.list);
            // 根据 sites 数量动态计算列数
            int spanCount = (int) Math.floor(sites.size() / 20.0);
            spanCount = Math.min(spanCount, 2);
            tvRecyclerView.setLayoutManager(new V7GridLayoutManager(mSiteSwitchDialog.getContext(), spanCount + 1));
            // 设置对话框宽度
            ConstraintLayout cl_root = mSiteSwitchDialog.findViewById(R.id.cl_root);
            ViewGroup.LayoutParams clp = cl_root.getLayoutParams();
            clp.width = AutoSizeUtils.mm2px(mSiteSwitchDialog.getContext(), 380 + 200 * spanCount);
            mSiteSwitchDialog.setTip("请选择首页数据源");
        }
        mSiteSwitchDialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<SourceBean>() {
            @Override
            public void click(SourceBean value, int pos) {
                dismissSiteSwitchDialog();
                ApiConfig.get().setSourceBean(value);
                refreshHome(false);
            }
            @Override
            public String getDisplay(SourceBean val) {
                return val.getName();
            }
        }, new DiffUtil.ItemCallback<SourceBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull SourceBean oldItem, @NonNull SourceBean newItem) {
                return oldItem == newItem;
            }
            @Override
            public boolean areContentsTheSame(@NonNull SourceBean oldItem, @NonNull SourceBean newItem) {
                return oldItem.getKey().equals(newItem.getKey());
            }
        }, sites, select);
        if (!mSiteSwitchDialog.isShowing())
            mSiteSwitchDialog.show();
    }
    // 公共静态方法：全局清空缓存+历史+数据库
    public static void clearAllCacheAndHistory(Context context) {
        try {
            SQLiteDatabase.releaseMemory();
            SQLiteDatabase db = null;
            try {
                db = context.openOrCreateDatabase("tvbox.v3.db", Context.MODE_PRIVATE, null);
                db.close();
            } catch (Exception e) {}

            new Thread(() -> {
                try {
                    File cacheDir = new File(FileUtils.getCachePath());
                    if (cacheDir.exists()) {
                        FileUtils.recursiveDelete(cacheDir);
                    }
                    File externalCache = context.getExternalCacheDir();
                    if (externalCache != null) {
                        FileUtils.recursiveDelete(externalCache);
                    }

                    try {
                        String dbName = "tvbox.v3.db";
                        File dbFile = context.getDatabasePath(dbName);
                        if (dbFile != null && dbFile.exists()) dbFile.delete();

                        File dbJournal = new File(dbFile.getParent(), dbName + "-journal");
                        File dbWal = new File(dbFile.getParent(), dbName + "-wal");
                        File dbShm = new File(dbFile.getParent(), dbName + "-shm");

                        if (dbJournal.exists()) dbJournal.delete();
                        if (dbWal.exists()) dbWal.delete();
                        if (dbShm.exists()) dbShm.delete();
                    } catch (Exception e) {}

                } catch (Exception e) {}

                ((android.app.Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "重置成功，正在重启", Toast.LENGTH_LONG).show();
                    restartApp(context);
                });
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 静态重启APP
    public static void restartApp(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        } catch (Exception e) {}
    }
    public void onClickClearCache(View v) {
        FastClickCheckUtil.check(v);
        clearAllCacheAndHistory(this);
    }
    void reloadHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("useCache", true);
        intent.putExtras(bundle);
        HomeActivity.this.startActivity(intent);
    }
    private boolean isActivityUnavailable() {
        return isFinishing() || (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed());
    }

    private void dismissHomeDialogs() {
        dismissConfigErrorDialog();
        dismissSiteSwitchDialog();
    }

    private void dismissConfigErrorDialog() {
        if (mConfigErrorDialog != null) {
            if (mConfigErrorDialog.isShowing()) {
                mConfigErrorDialog.dismiss();
            }
            mConfigErrorDialog = null;
        }
    }

    private void dismissSiteSwitchDialog() {
        if (mSiteSwitchDialog != null) {
            if (mSiteSwitchDialog.isShowing()) {
                mSiteSwitchDialog.dismiss();
            }
            mSiteSwitchDialog = null;
        }
    }
    private void refreshEmpty()
    {
        skipNextUpdate=true;
        showSuccess();
        cancelHomeSortLoading();
        clearHomePages();
        sortAdapter.setNewData(DefaultConfig.adjustSort(ApiConfig.get().getHomeSourceBean().getKey(), new ArrayList<>(), true));
        initViewPager(null);
        tvName.clearAnimation();
    }
    private void refreshHome()
    {
        refreshHome(true);
    }
    private void cancelHomeSortLoading() {
        homeSortLoading = false;
        loadingSourceKey = null;
        tvName.clearAnimation();
        if (previousHomeName != null && !previousHomeName.isEmpty()) {
            tvName.setText(previousHomeName);
        }
        previousHomeName = null;
    }
    private void refreshHome(final boolean restart)
    {
        if (Thread.currentThread() != android.os.Looper.getMainLooper().getThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    refreshHome(restart);
                }
            });
            return;
        }
        if (isActivityUnavailable()) {
            return;
        }
        dismissHomeDialogs();
        if (!restart) {
            loadHomeSort(true);
            return;
        }
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("useCache", true);
        intent.putExtras(bundle);
        HomeActivity.this.startActivity(intent);
    }
    private void refreshHomeSort() {
        refreshHomeRec = true;
        if (UserFragment.homeHotVodAdapter != null) {
            UserFragment.homeHotVodAdapter.setNewData(new ArrayList<Movie.Video>());
        }
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        if (home != null) {
            SourceViewModel.clearSortCache(home.getKey());
        }
        refreshHome(false);
    }

    private void updateHomeRec(AbsSortXml absXml) {
        if (!refreshHomeRec) return;
        refreshHomeRec = false;
        if (Hawk.get(HawkConfig.HOME_REC, HawkConfig.DEFAULT_HOME_REC) != 1) return;
        if (absXml == null || absXml.videoList == null || UserFragment.homeHotVodAdapter == null) return;
        UserFragment.homeHotVodAdapter.setNewData(absXml.videoList);
    }

    private void tvNameAnimation()
    {
        tvName.clearAnimation();
        AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(500);
        blinkAnimation.setStartOffset(20);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        tvName.startAnimation(blinkAnimation);
    }
}
