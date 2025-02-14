package com.stkj.infocollect.setting.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.stkj.common.ui.adapter.CommonFragmentPageAdapter;
import com.stkj.infocollect.setting.model.SettingTabInfo;
import com.stkj.infocollect.setting.ui.fragment.TabDeviceSettingFragment;
import com.stkj.infocollect.setting.ui.fragment.TabFacePassSettingFragment;
import com.stkj.infocollect.setting.ui.fragment.TabServerAddressFragment;

import java.util.List;

/**
 * 设置页全部界面
 */
public class SettingTabPageAdapter extends CommonFragmentPageAdapter {

    private List<SettingTabInfo> settingTabInfoList;

    public SettingTabPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<SettingTabInfo> tabInfoList) {
        super(fragmentManager, lifecycle);
        settingTabInfoList = tabInfoList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        SettingTabInfo settingTabInfo = settingTabInfoList.get(position);
        int tabType = settingTabInfo.getTabType();
        if (tabType == SettingTabInfo.TAB_TYPE_DEVICE_SETTING) {
            return new TabDeviceSettingFragment();
        } else if (tabType == SettingTabInfo.TAB_TYPE_FACE_PASS) {
            return new TabFacePassSettingFragment();
        } else {
            return new TabServerAddressFragment();
        }
    }

    @Override
    public int getItemCount() {
        return settingTabInfoList == null ? 0 : settingTabInfoList.size();
    }
}
