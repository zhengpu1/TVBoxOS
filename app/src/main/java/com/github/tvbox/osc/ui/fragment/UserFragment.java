package com.github.tvbox.osc.ui.fragment;

import static com.github.tvbox.osc.base.BaseActivity.drawableLinearLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.cache.RoomDataManger;
import com.github.tvbox.osc.event.ServerEvent;
import com.github.tvbox.osc.ui.activity.CollectActivity;
import com.github.tvbox.osc.ui.activity.DetailActivity;
import com.github.tvbox.osc.ui.activity.DriveActivity;
import com.github.tvbox.osc.ui.activity.FastSearchActivity;
import com.github.tvbox.osc.ui.activity.HistoryActivity;
import com.github.tvbox.osc.ui.activity.LivePlayActivity;
import com.github.tvbox.osc.ui.activity.PushActivity;
import com.github.tvbox.osc.ui.activity.SearchActivity;
import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.ui.adapter.HomeHotVodAdapter;
import com.github.tvbox.osc.ui.adapter.HomeHotVodAdaptera;
import com.github.tvbox.osc.ui.adapter.HomeHotVodAdapterc;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.UA;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author pj567
 * @date :2021/3/9
 * @description:
 */
public class UserFragment extends BaseLazyFragment implements View.OnClickListener {
    public static int tyssy;
    public static LinearLayout tvDrive;
    public static LinearLayout tvDrives;
    public static LinearLayout tvLive;
    public static LinearLayout tvLives;
    public static LinearLayout tvSearch;
    public static LinearLayout tvSetting;
    public static LinearLayout tvHistory;
    public static LinearLayout tvHistorys;
    public static LinearLayout tvCollect;
    public static LinearLayout tvCollects;
    public static LinearLayout tvPush;
    public static TextView tvtxt;
    private HomeHotVodAdapter homeHotVodAdapter;
    private HomeHotVodAdaptera homeHotVodAdaptera;
    private HomeHotVodAdapterc homeHotVodAdapterc;
    private List<Movie.Video> homeSourceRec;
    public static TvRecyclerView tvHotList1;
    public static TvRecyclerView tvHotList4;
    public static TvRecyclerView tvHotList2;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    public static UserFragment newInstance(List<Movie.Video> recVod) {
        return new UserFragment().setArguments(recVod);
    }

    public UserFragment setArguments(List<Movie.Video> recVod) {
        this.homeSourceRec = recVod;
        return this;
    }

    @Override
    protected void onFragmentResume() {
        // takagen99: Initialize Icon Placement
        super.onFragmentResume();
        LinearLayout[] strl = {tvSearch, tvSetting, tvHistory, tvLive, tvPush, tvDrive, tvCollect, tvHistorys, tvLives, tvDrives, tvCollects};
        drawableLinearLayout(strl, 3);
        LinearLayout[] strls = {tvHistorys, tvLives, tvDrives, tvCollects};
        drawableLinearLayout(strls, 10);
        if (Hawk.get(HawkConfig.HOME_REC, 0) == 2) {
            List<VodInfo> allVodRecord = RoomDataManger.getAllVodRecord(30);
            List<Movie.Video> vodList = new ArrayList<>();
            for (VodInfo vodInfo : allVodRecord) {
                Movie.Video vod = new Movie.Video();
                vod.id = vodInfo.id;
                vod.sourceKey = vodInfo.sourceKey;
                vod.name = vodInfo.name;
                vod.pic = vodInfo.pic;
                if (vodInfo.playNote != null && !vodInfo.playNote.isEmpty())
                    vod.note = "上次看到" + vodInfo.playNote;
                vodList.add(vod);
            }
            homeHotVodAdapter.setNewData(vodList);
            homeHotVodAdaptera.setNewData(vodList);
            homeHotVodAdapterc.setNewData(vodList);
        }
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_user;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        tvDrive = findViewById(R.id.tvDrive);
        tvDrives = findViewById(R.id.tvDrives);
        tvLive = findViewById(R.id.tvLive);
        tvLives = findViewById(R.id.tvLives);
        tvSearch = findViewById(R.id.tvSearch);
        tvSetting = findViewById(R.id.tvSetting);
        tvCollect = findViewById(R.id.tvFavorite);
        tvCollects = findViewById(R.id.tvFavorites);
        tvHistory = findViewById(R.id.tvHistory);
        tvHistorys = findViewById(R.id.tvHistorys);
        tvtxt = findViewById(R.id.tvtxt);
        tvPush = findViewById(R.id.tvPush);
        tvDrive.setOnClickListener(this);
        tvDrives.setOnClickListener(this);
        tvLive.setOnClickListener(this);
        tvLives.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvHistory.setOnClickListener(this);
        tvHistorys.setOnClickListener(this);
        tvPush.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        tvCollects.setOnClickListener(this);
        tvDrive.setOnFocusChangeListener(focusChangeListener);;
        tvLive.setOnFocusChangeListener(focusChangeListener);
        tvDrives.setOnFocusChangeListener(focusChangeListeners);;
        tvLives.setOnFocusChangeListener(focusChangeListeners);
        tvSearch.setOnFocusChangeListener(focusChangeListener);
        tvSetting.setOnFocusChangeListener(focusChangeListener);
        tvHistory.setOnFocusChangeListener(focusChangeListener);
        tvHistorys.setOnFocusChangeListener(focusChangeListeners);
        tvPush.setOnFocusChangeListener(focusChangeListener);
        tvCollect.setOnFocusChangeListener(focusChangeListener);
        tvCollects.setOnFocusChangeListener(focusChangeListeners);
        tvHotList1 = findViewById(R.id.tvHotList1);
        tvHotList1.setHasFixedSize(true);
        tvHotList1.setLayoutManager(new V7GridLayoutManager(this.mContext, !ApiConfig.get().souyeUrl.isEmpty() ? 5 : 6));
        tvHotList4 = findViewById(R.id.tvHotList4);
        tvHotList4.setHasFixedSize(true);
        tvHotList4.setLayoutManager(new V7GridLayoutManager(this.mContext, 4));
        tvHotList2 = findViewById(R.id.tvHotList2);
        homeHotVodAdapter = new HomeHotVodAdapter();
        homeHotVodAdaptera = new HomeHotVodAdaptera();
        homeHotVodAdapterc = new HomeHotVodAdapterc();
        homeHotVodAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
                    mActivity.startActivity(newIntent);
                }
            }
        });
        homeHotVodAdaptera.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
                    mActivity.startActivity(newIntent);
                }
            }
        });

        homeHotVodAdaptera.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (ApiConfig.get().getSourceBeanList().isEmpty()) return true;
                Movie.Video vod = ((Movie.Video) adapter.getItem(position));
                Bundle bundle = new Bundle();
                bundle.putString("title", vod.name);
                bundle.putString("pic", vod.pic);
                jumpActivity(FastSearchActivity.class, bundle);
                return true;
            }
        });

        homeHotVodAdapterc.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                tyssy = position;
                if (position < 2) return 2;
                return 1;
            }
        });
        homeHotVodAdapterc.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ApiConfig.get().getSourceBeanList().isEmpty())
                    return;
                Movie.Video vod = ((Movie.Video) adapter.getItem(position));
                if (vod != null) {
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
                        mActivity.startActivity(newIntent);
                    }
                }
            }
        });

        homeHotVodAdapterc.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (ApiConfig.get().getSourceBeanList().isEmpty()) return true;
                Movie.Video vod = ((Movie.Video) adapter.getItem(position));
                Bundle bundle = new Bundle();
                bundle.putString("title", vod.name);
                bundle.putString("pic", vod.pic);
                jumpActivity(FastSearchActivity.class, bundle);
                return true;
            }
        });

        homeHotVodAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (ApiConfig.get().getSourceBeanList().isEmpty()) return true;
                Movie.Video vod = ((Movie.Video) adapter.getItem(position));
                Bundle bundle = new Bundle();
                bundle.putString("title", vod.name);
                bundle.putString("pic", vod.pic);
                jumpActivity(FastSearchActivity.class, bundle);
                return true;
            }
        });
        tvHotList4.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });
        tvHotList4.setAdapter(homeHotVodAdapterc);
        initHomeHotVod3(homeHotVodAdapterc);
        tvHotList1.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });
        tvHotList1.setAdapter(homeHotVodAdaptera);
        initHomeHotVod2(homeHotVodAdaptera);
        tvHotList2.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });

        tvHotList2.setAdapter(homeHotVodAdapter);
        initHomeHotVod(homeHotVodAdapter);
        if(Hawk.get(HawkConfig.HOME_REC_STYLE, false)){
            tvHotList4.setVisibility(View.GONE);
            tvHotList1.setVisibility(View.GONE);
            tvDrives.setVisibility(View.GONE);
            tvCollects.setVisibility(View.GONE);
            tvHistorys.setVisibility(View.GONE);
            tvLives.setVisibility(View.GONE);
            tvtxt.setVisibility(View.GONE);
            tvHotList2.setVisibility(View.VISIBLE);
            tvSearch.setVisibility(View.VISIBLE);
            tvSetting.setVisibility(View.GONE);
            tvHistory.setVisibility(View.VISIBLE);
            tvLive.setVisibility(View.VISIBLE);
            tvPush.setVisibility(View.VISIBLE);
            tvDrive.setVisibility(View.VISIBLE);
            tvCollect.setVisibility(View.VISIBLE);
        }else {
            tvHotList4.setVisibility(View.VISIBLE);
            tvHotList1.setVisibility(View.VISIBLE);
            tvHistorys.setVisibility(View.VISIBLE);
            tvtxt.setVisibility(View.VISIBLE);
            tvLives.setVisibility(View.VISIBLE);
            tvDrives.setVisibility(View.VISIBLE);
            tvCollects.setVisibility(View.VISIBLE);
            tvHotList2.setVisibility(View.GONE);
            tvSearch.setVisibility(View.GONE);
            tvSetting.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            tvLive.setVisibility(View.GONE);
            tvPush.setVisibility(View.GONE);
            tvDrive.setVisibility(View.GONE);
            tvCollect.setVisibility(View.GONE);
        }

    }
    private void initHomeHotVod3(HomeHotVodAdapterc adapter) {
        setDouBanDatas(adapter);
    }
    private void setDouBanDatas(HomeHotVodAdapterc adapter) {
        try {
            String doubanUrl = ApiConfig.get().souyeUrl;
            String Referer = "";
            if (doubanUrl.isEmpty()){
                doubanUrl = "https://api.web.360kan.com/v1/block?blockid=503";
                Referer = "https://www.360kan.com/";
            }
            String finalDoubanUrl = doubanUrl;
            OkGo.<String>get(finalDoubanUrl)
                    .headers("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36")
                    .headers("Referer",Referer)
                    .execute(new AbsCallback<String>() {
                        public void onSuccess(Response<String> response) {
                            String netJson = response.body();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalDoubanUrl.contains("hrystv.")) {
                                        adapter.setNewData(loadHotss(netJson));
                                    } else {
                                        adapter.setNewData(loadHotssk(netJson));
                                    }
                                }
                            });
                        }
                        @Override
                        public String convertResponse(okhttp3.Response response) throws Throwable {
                            return response.body().string();
                        }
                    });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
    private void initHomeHotVod(HomeHotVodAdapter adapter) {
        if (Hawk.get(HawkConfig.HOME_REC, 0) == 1) {
            if (homeSourceRec != null) {
                adapter.setNewData(homeSourceRec);
                return;
            }
        } else if (Hawk.get(HawkConfig.HOME_REC, 0) == 2) {
            return;
        }
        setDouBanData(adapter);
    }

    private void setDouBanData(HomeHotVodAdapter adapter) {
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DATE);
            String today = String.format("%d%d%d", year, month, day);
            String requestDay = Hawk.get("home_hot_day", "");
            if (requestDay.equals(today)) {
                String json = Hawk.get("home_hot", "");
                if (!json.isEmpty()) {
                    ArrayList<Movie.Video> hotMovies = loadHots(json);
                    if (hotMovies != null && hotMovies.size() > 0) {
                        adapter.setNewData(hotMovies);
                        return;
                    }
                }
            }
            String doubanUrl = "https://movie.douban.com/j/new_search_subjects?sort=U&range=0,10&tags=&playable=1&start=0&year_range=" + year + "," + year;
            OkGo.<String>get(doubanUrl)
                    .headers("User-Agent", UA.randomOne())
                    .execute(new AbsCallback<String>() {
                        public void onSuccess(Response<String> response) {
                            String netJson = response.body();
                            Hawk.put("home_hot_day", today);
                            Hawk.put("home_hot", netJson);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setNewData(loadHots(netJson));
                                }
                            });
                        }
                            @Override
                            public String convertResponse(okhttp3.Response response) throws Throwable {
                                return response.body().string();
                            }
                        });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
    private void initHomeHotVod2(HomeHotVodAdaptera adapter) {
        if (Hawk.get(HawkConfig.HOME_REC, 0) == 1) {
            if (homeSourceRec != null) {
                adapter.setNewData(homeSourceRec);
                return;
            }
        } else if (Hawk.get(HawkConfig.HOME_REC, 0) == 2) {
            return;
        }
        setDouBanDataa(adapter);
    }

    private void setDouBanDataa(HomeHotVodAdaptera adapter) {
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DATE);
            @SuppressLint("DefaultLocale") String today = String.format("%d%d%d", year, month, day);
            String requestDay = Hawk.get("home_hot_day", "");
            if (requestDay.equals(today)) {
                String json = Hawk.get("home_hot", "");
                if (!json.isEmpty()) {
                    ArrayList<Movie.Video> hotMovies = loadHots(json);
                    if (hotMovies != null && hotMovies.size() > 0) {
                        adapter.setNewData(hotMovies);
                        return;
                    }
                }
            }
            String doubanUrl = "https://movie.douban.com/j/new_search_subjects?sort=U&range=0,10&tags=&playable=1&start=0&year_range=" + year + "," + year;
            OkGo.<String>get(doubanUrl)
                    .headers("User-Agent", UA.randomOne())
                    .execute(new AbsCallback<String>() {
                        public void onSuccess(Response<String> response) {
                            String netJson = response.body();
                            Hawk.put("home_hot_day", today);
                            Hawk.put("home_hot", netJson);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setNewData(loadHots(netJson));
                                }
                            });
                        }

                        @Override
                        public String convertResponse(okhttp3.Response response) throws Throwable {
                            return response.body().string();
                        }
                    });
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private ArrayList<Movie.Video> loadHots(String json) {
        ArrayList<Movie.Video> result = new ArrayList<>();
        try {
            JsonObject infoJson = new Gson().fromJson(json, JsonObject.class);
            JsonArray array = infoJson.getAsJsonArray("data");
            for (JsonElement ele : array) {
                JsonObject obj = (JsonObject) ele;
                Movie.Video vod = new Movie.Video();
                vod.name = obj.get("title").getAsString();
                vod.note = obj.get("rate").getAsString();
                vod.pic = obj.get("cover").getAsString()+"@User-Agent="+ UA.randomOne()+"@Referer=https://www.douban.com/";
                result.add(vod);
            }
        } catch (Throwable th) {

        }
        return result;
    }
    private ArrayList<Movie.Video> loadHotss(String json) {
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
                vod.isend = Integer.parseInt(obj.get("vod_isend").getAsString());
                vod.pic = obj.get("vod_pic").getAsString();
                vod.picthumb = obj.get("vod_pic_thumb").getAsString();
                vod.picslide = obj.get("vod_pic_slide").getAsString();
                vod.sourceKey = "htv_hrys";
                result.add(vod);
            }
        } catch (Throwable th) {

        }
        return result;
    }
    private ArrayList<Movie.Video> loadHotssk(String json) {
        ArrayList<Movie.Video> result = new ArrayList<>();
        try {
            JsonObject infoJson = new Gson().fromJson(json, JsonObject.class);
            JsonArray array = infoJson.getAsJsonObject("data").getAsJsonArray("lists");
            for (int i = 0; i < 6; i++) {
                JsonObject obj = (JsonObject) array.get(i);
                Movie.Video vod = new Movie.Video();
                JsonArray pict = obj.getAsJsonArray("pic_lists");
                vod.name = obj.get("title").getAsString();
                vod.note = obj.get("upinfo").getAsString();
                for (JsonElement pic : pict) {
                    JsonObject pics = (JsonObject) pic;
                    vod.picthumb = pics.get("url").getAsString();
                    vod.picslide = pics.get("url").getAsString();
                }
                result.add(vod);
            }
        } catch (Throwable th) {

        }
        return result;
    }
    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            TextView tView = v.findViewById(R.id.tView);
            if (hasFocus) {
                tView.setTextColor(getResources().getColor(R.color.color_FFF5E8DC));
                v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }else {
                tView.setTextColor(getResources().getColor(R.color.color_80FFFFFF));
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }
        }
    };

    private final View.OnFocusChangeListener focusChangeListeners = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }
        }
    };

    @Override
    public void onClick(View v) {
        FastClickCheckUtil.check(v);
        if (v.getId() == R.id.tvLive) {
            jumpActivity(LivePlayActivity.class);
        } else if (v.getId() == R.id.tvSearch) {
            jumpActivity(SearchActivity.class);
        } else if (v.getId() == R.id.tvSetting) {
            jumpActivity(SettingActivity.class);
        } else if (v.getId() == R.id.tvHistory) {
            jumpActivity(HistoryActivity.class);
        } else if (v.getId() == R.id.tvPush) {
            jumpActivity(PushActivity.class);
        } else if (v.getId() == R.id.tvFavorite) {
            jumpActivity(CollectActivity.class);
        } else if(v.getId() == R.id.tvDrive) {
            jumpActivity(DriveActivity.class);
        }else if(v.getId() == R.id.tvHistorys) {
            jumpActivity(HistoryActivity.class);
        }else if(v.getId() == R.id.tvLives) {
            jumpActivity(LivePlayActivity.class);
        }else if(v.getId() == R.id.tvFavorites) {
            jumpActivity(CollectActivity.class);
        }else if(v.getId() == R.id.tvDrives) {
            jumpActivity(DriveActivity.class);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void server(ServerEvent event) {
        if (event.type == ServerEvent.SERVER_CONNECTION) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}