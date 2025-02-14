package com.stkj.cashiermini.home.ui.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.stkj.cashiermini.home.model.HomeMenuInfo;
import com.stkj.cashiermini.home.model.HomeTabInfo;
import com.stkj.cashiermini.order.ui.fragment.TabOrderFragment;
import com.stkj.cashiermini.setting.ui.fragment.TabSettingFragment;
import com.stkj.common.ui.adapter.CommonFragmentPageAdapter;

import java.util.List;

/**
 * 首页全部界面
 */
public class HomeTabPageAdapter extends CommonFragmentPageAdapter {

    public static final String TAB_ORDER_TAG = "订单";
    public static final String TAB_SETTING_TAG = "设置";
    private List<HomeTabInfo<HomeMenuInfo>> homeTabInfoList;

    public HomeTabPageAdapter(@NonNull FragmentActivity fragmentActivity, List<HomeTabInfo<HomeMenuInfo>> tabInfoList) {
        super(fragmentActivity);
        homeTabInfoList = tabInfoList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        HomeTabInfo<HomeMenuInfo> homeTabInfo = homeTabInfoList.get(position);
        HomeMenuInfo extraInfo = homeTabInfo.getExtraInfo();
        String path = extraInfo.getName();
        if (TextUtils.equals(path, TAB_ORDER_TAG)) {
            return new TabOrderFragment();
        }
        return new TabSettingFragment();
    }

    @Override
    public int getItemCount() {
        return homeTabInfoList == null ? 0 : homeTabInfoList.size();
    }

    public int findTabPageIndex(String path) {
        if (homeTabInfoList != null) {
            int size = homeTabInfoList.size();
            for (int i = 0; i < size; i++) {
                HomeMenuInfo extraInfo = homeTabInfoList.get(i).getExtraInfo();
                if (TextUtils.equals(path, extraInfo.getName())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
