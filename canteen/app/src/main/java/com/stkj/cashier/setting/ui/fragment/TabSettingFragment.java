package com.stkj.cashier.setting.ui.fragment;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.stkj.cashier.R;
import com.stkj.cashier.setting.model.SettingTabInfo;
import com.stkj.cashier.setting.ui.adapter.SettingTabInfoViewHolder;
import com.stkj.cashier.setting.ui.adapter.SettingTabPageAdapter;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置页面
 */
public class TabSettingFragment extends BaseRecyclerFragment {

    private RecyclerView rvTopTab;
    private ViewPager2 vp2Content;
    private SettingTabInfo mCurrentTabInfo;

    @Override
    protected int getLayoutResId() {
        return com.stkj.cashier.R.layout.fragment_tab_setting;
    }

    @Override
    protected void initViews(View rootView) {
        rvTopTab = (RecyclerView) findViewById(R.id.rv_top_tab);
        vp2Content = (ViewPager2) findViewById(R.id.vp2_content);
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            initData();
        }
    }

    private void initData() {
        //添加设置tab
        List<SettingTabInfo> settingTabInfoList = new ArrayList<>();
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_SERVER_ADDRESS, SettingTabInfo.TAB_TYPE_SERVER_ADDRESS));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_DEVICE_SETTING, SettingTabInfo.TAB_TYPE_DEVICE_SETTING));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_WIFI_CONNECT, SettingTabInfo.TAB_TYPE_WIFI_CONNECT));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_PAYMENT_SETTING, SettingTabInfo.TAB_TYPE_PAYMENT_SETTING));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_VOICE_SETTING, SettingTabInfo.TAB_TYPE_VOICE_SETTING));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_FACE_PASS, SettingTabInfo.TAB_TYPE_FACE_PASS));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_NAME_RESTART_APP, SettingTabInfo.TAB_TYPE_RESTART_APP));
        CommonRecyclerAdapter tabInfoAdapter = new CommonRecyclerAdapter(false);
        tabInfoAdapter.addViewHolderFactory(new SettingTabInfoViewHolder.Factory());
        tabInfoAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                SettingTabInfo settingTabInfo = (SettingTabInfo) obj;
                //已经选中则不切换
                if (settingTabInfo.isSelected()) {
                    return;
                }
                //取消选中之前的
                if (mCurrentTabInfo != null) {
                    mCurrentTabInfo.setSelected(false);
                    tabInfoAdapter.notifyChangeItemData(mCurrentTabInfo);
                }
                //选中当前的
                settingTabInfo.setSelected(true);
                mCurrentTabInfo = settingTabInfo;
                int currentTabPos = settingTabInfoList.indexOf(mCurrentTabInfo);
                LogHelper.print("tabSetting changeTab onClickItemView " + currentTabPos);
                tabInfoAdapter.notifyChangeItemData(mCurrentTabInfo);
                vp2Content.setCurrentItem(currentTabPos, false);
            }
        });
        tabInfoAdapter.addDataList(settingTabInfoList);
        rvTopTab.setAdapter(tabInfoAdapter);
        rvTopTab.setItemAnimator(null);
        int offset = mResources.getDimensionPixelOffset(com.stkj.common.R.dimen.sp_11);
        int tabSize = settingTabInfoList.size();
        rvTopTab.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                RecyclerView.ViewHolder containingViewHolder = parent.findContainingViewHolder(view);
                if (containingViewHolder != null) {
                    int adapterPosition = containingViewHolder.getBindingAdapterPosition();
                    if (adapterPosition == (tabSize - 1)) {
                        outRect.set(0, 0, 0, 0);
                    } else {
                        outRect.set(0, 0, offset, 0);
                    }
                }
            }
        });
        mCurrentTabInfo = settingTabInfoList.get(0);
        mCurrentTabInfo.setSelected(true);
        SettingTabPageAdapter settingTabPageAdapter = new SettingTabPageAdapter(getChildFragmentManager(), getLifecycle(), settingTabInfoList);
        vp2Content.setAdapter(settingTabPageAdapter);
        vp2Content.setUserInputEnabled(false);
        vp2Content.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                LogHelper.print("tabSetting changeTab onPageSelected " + position);
                SettingTabInfo settingTabInfo = settingTabInfoList.get(position);
                if (mCurrentTabInfo == settingTabInfo) {
                    return;
                }
                //取消选中之前的
                if (mCurrentTabInfo != null) {
                    mCurrentTabInfo.setSelected(false);
                    tabInfoAdapter.notifyChangeItemData(mCurrentTabInfo);
                }
                //选中当前的
                mCurrentTabInfo = settingTabInfo;
                settingTabInfo.setSelected(true);
                tabInfoAdapter.notifyChangeItemData(mCurrentTabInfo);
            }
        });
    }
}
