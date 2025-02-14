package com.stkj.supermarketmini.base.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class RecyclerViewUtils {

    public static void scrollListToTop(RecyclerView recyclerView) {
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                linearLayoutManager.scrollToPositionWithOffset(0, 0);
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                gridLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                recyclerView.scrollToPosition(0);
            }
        }
    }

}
