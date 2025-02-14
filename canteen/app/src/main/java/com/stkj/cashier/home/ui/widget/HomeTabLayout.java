package com.stkj.cashier.home.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.cashier.home.model.HomeMenuList;
import com.stkj.cashier.home.model.HomeTabInfo;
import com.stkj.cashier.home.ui.adapter.HomeTabInfoViewHolder;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;

import java.util.List;

/**
 * 首页tab切换整体布局
 */
public class HomeTabLayout extends RecyclerView {

    private CommonRecyclerAdapter mAdapter;
    private int mLastSelectTab = -1;
    private OnTabChangeListener mOnTabChangeListener;
    private boolean enableTabClick = true;

    public HomeTabLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HomeTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        setItemAnimator(null);
        setLayoutManager(new LinearLayoutManager(context) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new CommonRecyclerAdapter(false);
        mAdapter.addViewHolderFactory(new HomeTabInfoViewHolder.Factory());
        mAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                if (enableTabClick) {
                    int itemIndex = mAdapter.findItemDataIndex(obj);
                    setCurrentTab(itemIndex, true);
                }
            }
        });
        setAdapter(mAdapter);
    }

    public void setAdapterItemChanged(int i) {
        mAdapter.notifyItemChanged(i);
    }

    public void setOnTabChangeListener(OnTabChangeListener mOnTabChangeListener) {
        this.mOnTabChangeListener = mOnTabChangeListener;
    }

    public void addTabList(List<HomeTabInfo<HomeMenuList.Menu>> tabInfoList) {
        mAdapter.removeAllData();
        mAdapter.addDataList(tabInfoList);
    }

    public HomeTabInfo getHomeTabInfo(int index) {
        Object data = mAdapter.getData(index);
        if (data instanceof HomeTabInfo) {
            return (HomeTabInfo) data;
        }
        return null;
    }

    public void notifyDataSetChangedAll() {
        mAdapter.notifyDataSetChanged();
    }

    public void setCurrentTab(int tabIndex) {
        setCurrentTab(tabIndex, false);
    }

    public void setCurrentTab(int tabIndex, boolean needCallback) {
        if (mLastSelectTab == tabIndex) {
            return;
        }
        changeTabSelectState(mLastSelectTab, false);
        mLastSelectTab = tabIndex;
        changeTabSelectState(mLastSelectTab, true);
        if (needCallback && mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabSelected(tabIndex);
        }
    }

    public void changeTabSelectState(int index, boolean isSelect) {
        HomeTabInfo data = (HomeTabInfo) mAdapter.getData(index);
        if (data != null) {
            data.setSelect(isSelect);
            mAdapter.notifyItemChanged(index);
        }
    }

    public void setEnableTabClick(boolean enableTabClick) {
        this.enableTabClick = enableTabClick;
    }

    public interface OnTabChangeListener {
        void onTabSelected(int tabIndex);
    }
}
