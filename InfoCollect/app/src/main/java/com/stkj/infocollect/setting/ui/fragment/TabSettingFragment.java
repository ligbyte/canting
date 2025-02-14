package com.stkj.infocollect.setting.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.infocollect.BuildConfig;
import com.stkj.infocollect.R;
import com.stkj.infocollect.setting.model.SettingTabInfo;
import com.stkj.infocollect.setting.ui.adapter.SettingTabInfoViewHolder;
import com.stkj.infocollect.setting.ui.adapter.SettingTabPageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置页面
 */
public class TabSettingFragment extends BaseRecyclerFragment {

    private ImageView ivBackSetting;
    private RecyclerView rvLeftTab;
    private ViewPager2 vp2Content;
    private SettingTabInfo mCurrentTabInfo;

    @Override
    protected int getLayoutResId() {
        return com.stkj.infocollect.R.layout.fragment_tab_setting;
    }

    @Override
    protected void initViews(View rootView) {
        ivBackSetting = (ImageView) findViewById(R.id.iv_back_setting);
        ivBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), TabSettingFragment.this);
            }
        });
        rvLeftTab = (RecyclerView) findViewById(R.id.rv_left_tab);
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
        if (BuildConfig.DEBUG) {
            settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_TYPE_SERVER_ADDRESS, SettingTabInfo.TAB_NAME_SERVER_ADDRESS, R.drawable.selector_tab_server));
        }
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_TYPE_DEVICE_SETTING, SettingTabInfo.TAB_NAME_DEVICE_SETTING, R.drawable.selector_tab_setting));
        settingTabInfoList.add(new SettingTabInfo(SettingTabInfo.TAB_TYPE_FACE_PASS, SettingTabInfo.TAB_NAME_FACE_PASS, R.drawable.selector_tab_facepass));
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
        rvLeftTab.setItemAnimator(null);
        rvLeftTab.setAdapter(tabInfoAdapter);
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
