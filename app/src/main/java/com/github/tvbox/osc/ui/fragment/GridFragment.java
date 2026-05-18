package com.github.tvbox.osc.ui.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.AbsXml;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.bean.MovieSort;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.ui.activity.DetailActivity;
import com.github.tvbox.osc.ui.activity.FastSearchActivity;
import com.github.tvbox.osc.ui.activity.SearchActivity;
import com.github.tvbox.osc.ui.adapter.GridAdapter;
import com.github.tvbox.osc.ui.adapter.GridFilterKVAdapter;
import com.github.tvbox.osc.ui.dialog.GridFilterDialog;
import com.github.tvbox.osc.ui.tv.widget.LoadMoreView;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class GridFragment extends BaseLazyFragment {
    private MovieSort.SortData sortData = null;
    private TvRecyclerView mGridView;
    private SourceViewModel sourceViewModel;
    private GridFilterDialog gridFilterDialog;
    private GridAdapter gridAdapter;
    public int page = 1;
    private int maxPage = 1;
    private boolean isLoad = false;
    private boolean isTop = true;
    private View focusedView = null;
    private class GridInfo{
        public String sortID="";
        public TvRecyclerView mGridView;
        public GridAdapter gridAdapter;
        public int page = 1;
        public int maxPage = 1;
        public boolean isLoad = false;
        public View focusedView= null;
    }
    Stack<GridInfo> mGrids = new Stack<GridInfo>(); //ui栈

    public static GridFragment newInstance(MovieSort.SortData sortData) {
        return new GridFragment().setArguments(sortData);
    }

    public GridFragment setArguments(MovieSort.SortData sortData) {
        this.sortData = sortData;
        if (sortData == null) {
            this.sortData = new MovieSort.SortData();
        }
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_grid;
    }

    @Override
    protected void init() {
        initView();
        initViewModel();
        initData();
    }

    private void changeView(String id,Boolean isFolder){
        if(isFolder){
            this.sortData.flag ="1"; // 修改sortData.flag
        }else {
            this.sortData.flag ="2"; // 修改sortData.flag
        }
        initView();
        if (sortData == null) {
            return;
        }
        this.sortData.id = id; // 修改sortData.id为新的ID
        initViewModel();
        initData();
    }
    public boolean isFolederMode(){ return (getUITag() =='1'); }
    // 获取当前页面UI的显示模式 ‘0’ 正常模式 '1' 文件夹模式 '2' 显示缩略图的文件夹模式
    public char getUITag(){
        System.out.println(sortData);
        return (sortData == null || sortData.flag == null || sortData.flag.length() ==0 ) ?  '0' : sortData.flag.charAt(0);
    }
    // 是否允许聚合搜索 sortData.flag的第二个字符为‘1’时允许聚搜
    public boolean enableFastSearch(){  return sortData.flag == null || sortData.flag.length() < 2 || (sortData.flag.charAt(1) == '1'); }
    // 保存当前页面
    private void saveCurrentView(){
        if(this.mGridView == null) return;
        GridInfo info = new GridInfo();
        info.sortID = this.sortData.id;
        info.mGridView = this.mGridView;
        info.gridAdapter = this.gridAdapter;
        info.page = this.page;
        info.maxPage = this.maxPage;
        info.isLoad = this.isLoad;
        info.focusedView = this.focusedView;
        this.mGrids.push(info);
    }
    // 丢弃当前页面，将页面还原成上一个保存的页面
    public boolean restoreView(){
        if(mGrids.empty()) return false;
        this.showSuccess();
        ((ViewGroup) mGridView.getParent()).removeView(this.mGridView); // 重父窗口移除当前控件
        GridInfo info = mGrids.pop();// 还原上次保存的控件
        this.sortData.id = info.sortID;
        this.mGridView = info.mGridView;
        this.gridAdapter = info.gridAdapter;
        this.page = info.page;
        this.maxPage = info.maxPage;
        this.isLoad = info.isLoad;
        this.focusedView = info.focusedView;
        this.mGridView.setVisibility(View.VISIBLE);
//        if(this.focusedView != null){ this.focusedView.requestFocus(); }
        if(mGridView != null) mGridView.requestFocus();
        return true;
    }
    // 更改当前页面
    private void createView(){
        this.saveCurrentView(); // 保存当前页面
        if(mGridView == null){ // 从layout中拿view
            mGridView = findViewById(R.id.mGridView);
        }else{ // 复制当前view
            TvRecyclerView v3 = new TvRecyclerView(this.mContext);
            v3.setSpacingWithMargins(10,10);
            v3.setLayoutParams(mGridView.getLayoutParams());
            v3.setPadding(mGridView.getPaddingLeft(), mGridView.getPaddingTop(), mGridView.getPaddingRight(), mGridView.getPaddingBottom());
            v3.setClipToPadding(mGridView.getClipToPadding());
            ((ViewGroup) mGridView.getParent()).addView(v3);
            mGridView.setVisibility(View.GONE);
            mGridView = v3;
            mGridView.setVisibility(View.VISIBLE);
        }
        mGridView.setHasFixedSize(true);
            gridAdapter = new GridAdapter(isFolederMode());
        this.page =1;
        this.maxPage =1;
        this.isLoad = false;
    }

    private void initView() {
        this.createView();
        mGridView.setAdapter(gridAdapter);
        if(isFolederMode()){
            mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        }else{
            if(Hawk.get(HawkConfig.PIC_SIZE, false)) {
                if(Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 0) {
                    mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 4 : 5));
                } else if(Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 1) {
                    mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 5 : 6));
                } else{
                    mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 4 : 5));
                }
            } else {
                if(Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 0) {
                    mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 5 : 6));
                } else if(Hawk.get(HawkConfig.HOME_LSTIMG, 0) == 1) {
                    mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 7 : 8));
                } else{
                    mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, isBaseOnWidth() ? 6 : 7));
                }
            }
        }
        // 延迟执行或确保视图已准备好
        mGridView.post(new Runnable() {
            @Override
            public void run() {
                setFilterDialogData();
            }
        });
        gridAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                gridAdapter.setEnableLoadMore(true);
                sourceViewModel.getList(sortData, page);
            }
        }, mGridView);
        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.findViewById(R.id.tvName).setSelected(false);
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                itemView.findViewById(R.id.tvName).setSelected(true);
                itemView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {

            }
        });
        mGridView.setOnInBorderKeyEventListener(new TvRecyclerView.OnInBorderKeyEventListener() {
            @Override
            public boolean onInBorderKeyEvent(int direction, View focused) {
                if (direction == View.FOCUS_UP) {
                }
                return false;
            }
        });
        gridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                Movie.Video video = gridAdapter.getData().get(position);
                if (video != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", video.id);
                    bundle.putString("sourceKey", video.sourceKey);
                    bundle.putString("title", video.name);
                    if(("12".indexOf(getUITag()) != -1) && (video.tag.equals("folder") || video.tag.equals("cover"))){
                        focusedView = view;
                        changeView(video.id,video.tag.equals("folder"));
                    }
//                    else if(homeSourceBean.isQuickSearch() && Hawk.get(HawkConfig.EPISODE_GROUPING, false) && enableFastSearch()){
//                        jumpActivity(FastSearchActivity.class, bundle);
//                    }
                    else{
                        if(video.id.isEmpty() || video.id.startsWith("msearch:")){
                            jumpActivity(SearchActivity.class, bundle);
                        }else {
                            jumpActivity(DetailActivity.class, bundle);
                        }
                    }

                }
            }
        });
        gridAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                Movie.Video video = gridAdapter.getData().get(position);
                if (video != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", video.id);
                    bundle.putString("sourceKey", video.sourceKey);
                    bundle.putString("title", video.name);
                    bundle.putString("pic", video.pic);
                    jumpActivity(FastSearchActivity.class, bundle);
                }
                return true;
            }
        });
        gridAdapter.setLoadMoreView(new LoadMoreView());
        setLoadSir2(mGridView);
    }

    private void initViewModel() {
        if(sourceViewModel != null) { return;}
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.listResult.observe(this, new Observer<AbsXml>() {
            @Override
            public void onChanged(AbsXml absXml) {
//                if(mGridView != null) mGridView.requestFocus();java.lang.NoClassDefFoundError: Failed resolution of: Lcom/google/zxing/EncodeHintType;
                if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
                    if (page == 1) {
                        showSuccess();
                        isLoad = true;
                        gridAdapter.setNewData(absXml.movie.videoList);
                    } else {
                        gridAdapter.addData(absXml.movie.videoList);
                    }
                    page++;
                    maxPage = absXml.movie.pagecount;

                    if (maxPage>0 && page > maxPage) {
                        gridAdapter.loadMoreEnd();
                        gridAdapter.setEnableLoadMore(false);
                        if(page>2)Toast.makeText(getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                    } else {
                        gridAdapter.loadMoreComplete();
                        gridAdapter.setEnableLoadMore(true);
                    }
                } else {
                    if(page == 1){
                        showEmpty();
                        Toast.makeText(getContext(), "没有更多了", Toast.LENGTH_SHORT).show();
                    }
                    gridAdapter.setEnableLoadMore(false);
                }
            }
        });
    }

    public boolean isLoad() {
        return isLoad || !mGrids.empty(); //如果有缓存页的话也可以认为是加载了数据的
    }

    public void initData() {
        showLoading();
        isLoad = false;
        scrollTop();
        toggleFilterColor();
        sourceViewModel.getList(sortData, page);
    }

    private void toggleFilterColor() {
        if (sortData.filters != null && !sortData.filters.isEmpty()) {
            int count = sortData.filterSelectCount();
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_FILTER_CHANGE, count));
        }
    }
    public void setFilterDialogData() {
        Context context = getContext();
        // 1. 动态添加筛选条
        LinearLayout filterRoot = findViewById(R.id.filterRoot);
        if(filterRoot ==null) return;
        filterRoot.removeAllViews(); // 防止重复添加
        LayoutInflater inflater = LayoutInflater.from(context);
        assert context != null;
        final int defaultColor = ContextCompat.getColor(context, R.color.color_FFFFFF);
        @SuppressLint("Recycle") TypedArray a = getContext().obtainStyledAttributes(R.styleable.themeColor);
        final int selectedColor = a.getColor(R.styleable.themeColor_color_theme, 0);

        // ========== 修复核心 1 ==========
        if (sortData == null) {
            sortData = new MovieSort.SortData();
        }
        // ========== 修复核心 2：filters 判空 + 初始化 ==========
        if (sortData.filters == null) {
            sortData.filters = new ArrayList<>();
        }

        // 遍历过滤条件数据
        for (MovieSort.SortFilter filter : sortData.filters) {
            View line = inflater.inflate(R.layout.item_grid_filter, filterRoot, false);

            TvRecyclerView gridView = line.findViewById(R.id.mFilterKv);
            gridView.setHasFixedSize(true);
            GridFilterKVAdapter adapter = new GridFilterKVAdapter();
            gridView.setAdapter(adapter);
            // 添加横向间距，30dp
            gridView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            int verticalSpace = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 7, context.getResources().getDisplayMetrics()); // 20dp 行间距
            int horizontalSpace = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 7, context.getResources().getDisplayMetrics()); // 30dp 列间距

            gridView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                           @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    int spanCount = 0;
                    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                    }
                    outRect.right = horizontalSpace;
                    if (position >= spanCount) {
                        outRect.top = verticalSpace;
                    }
                }
            });
            final String key = filter.key;
            final ArrayList<String> values = new ArrayList<>(filter.values.keySet());
            final ArrayList<String> keys = new ArrayList<>(filter.values.values());
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                View previousSelectedView = null;
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    // ========== 修复核心 3：filterSelect 也判空 ==========
                    if (sortData.filterSelect == null) {
                        sortData.filterSelect = new HashMap<>();
                    }
                    String currentSelection = sortData.filterSelect.get(key);
                    String newSelection = keys.get(position);
                    if (currentSelection == null || !currentSelection.equals(newSelection)) {
                        sortData.filterSelect.put(key, newSelection);
                        updateViewStyle(view, selectedColor, true);
                        if (previousSelectedView != null) {
                            updateViewStyle(previousSelectedView, defaultColor, false);
                        }
                        previousSelectedView = view;
                    } else {
                        sortData.filterSelect.remove(key);
                        if (previousSelectedView != null) {
                            updateViewStyle(previousSelectedView, defaultColor, false);
                        }
                        previousSelectedView = null;
                    }
                    forceRefresh();
                }
                private void updateViewStyle(View view, int color, boolean isBold) {
                    TextView valueTv = view.findViewById(R.id.filterValue);
                    valueTv.getPaint().setFakeBoldText(isBold);
                    valueTv.setTextColor(color);
                }
            });
            adapter.setNewData(values);
            filterRoot.addView(line);
        }
    }
    public void toggleFilterBar() {
        final LinearLayout filterRoot = getView().findViewById(R.id.filterRoot);
        if (filterRoot == null) return;

        if (filterRoot.getVisibility() != View.VISIBLE) {
            // 先显示，初始位置在上方
            filterRoot.setVisibility(View.VISIBLE);
            filterRoot.setAlpha(1f);
            filterRoot.setTranslationY(-filterRoot.getHeight() == 0 ? -200 : -filterRoot.getHeight());
            filterRoot.post(() -> {
                int height = filterRoot.getHeight();
                filterRoot.setTranslationY(-height);
                ObjectAnimator animator = ObjectAnimator.ofFloat(filterRoot, "translationY", -height, 0);
                animator.setDuration(1000);
                animator.setInterpolator(new android.view.animation.OvershootInterpolator(0f)); // 弹性下滑
                animator.start();
            });
        } else {
            // 向上滑回去并隐藏
            int height = filterRoot.getHeight();
            ObjectAnimator animator = ObjectAnimator.ofFloat(filterRoot, "translationY", 0, -height);
            animator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    filterRoot.setVisibility(View.GONE);
                }
            });
            animator.setDuration(500);
            animator.setInterpolator(new android.view.animation.AnticipateInterpolator(1.2f)); // 弹性上收

            animator.start();
        }
    }
    public boolean isTop() {
        return isTop;
    }

    public void scrollTop() {
        isTop = true;
        mGridView.scrollToPosition(0);
    }

    public void showFilter() {
        if (!sortData.filters.isEmpty() && gridFilterDialog == null) {
            gridFilterDialog = new GridFilterDialog(mContext);
            gridFilterDialog.setData(sortData);
//            gridFilterDialog.setOnDismiss(new GridFilterDialog.Callback() {
//                @Override
//                public void change() {
//                    page = 1;
//                    initData();
//                }
//            });
        }
        if (gridFilterDialog != null)
            gridFilterDialog.show();
    }
    public void forceRefresh() {
        page = 1;
        initData();
    }
}