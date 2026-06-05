package com.github.tvbox.osc.util;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//网格均分间距Decoration，水平垂直都是spacePx，边缘不留边距，完美适配V7GridLayoutManager
public class GridSpaceDecoration extends RecyclerView.ItemDecoration {
    private final int spacePx;
    public GridSpaceDecoration(int spacePx) {
        this.spacePx = spacePx;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanCount = layoutManager.getSpanCount();
        int pos = parent.getChildAdapterPosition(view);
        int col = pos % spanCount;

        outRect.left = spacePx - col * spacePx / spanCount;
        outRect.right = (col + 1) * spacePx / spanCount;
        if(pos >= spanCount) outRect.top = spacePx;
    }
}