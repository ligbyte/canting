package com.stkj.cashier.setting.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import com.stkj.cashier.setting.model.SettingTabInfo;
import com.stkj.cashier.setting.ui.fragment.TabDeviceSettingFragment;
import com.stkj.cashier.setting.ui.fragment.TabFacePassSettingFragment;
import com.stkj.cashier.setting.ui.fragment.TabPaymentSettingFragment;
import com.stkj.cashier.setting.ui.fragment.TabRestartAppFragment;
import com.stkj.cashier.setting.ui.fragment.TabServerAddressFragment;
import com.stkj.cashier.setting.ui.fragment.TabVoiceSettingFragment;
import com.stkj.cashier.setting.ui.fragment.TabWifiSettingFragment;
import com.stkj.common.ui.adapter.CommonFragmentPageAdapter;

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
        if (tabType == SettingTabInfo.TAB_TYPE_SERVER_ADDRESS) {
            return new TabServerAddressFragment();
        } else if (tabType == SettingTabInfo.TAB_TYPE_DEVICE_SETTING) {
            return new TabDeviceSettingFragment();
        } else if (tabType == SettingTabInfo.TAB_TYPE_WIFI_CONNECT) {
            return new TabWifiSettingFragment();
        } else if (tabType == SettingTabInfo.TAB_TYPE_PAYMENT_SETTING) {
            return new TabPaymentSettingFragment();
        } else if (tabType == SettingTabInfo.TAB_TYPE_VOICE_SETTING) {
            return new TabVoiceSettingFragment();
        } else if (tabType == SettingTabInfo.TAB_TYPE_FACE_PASS) {
            return new TabFacePassSettingFragment();
        } else {
            return new TabRestartAppFragment();
        }
    }

    @Override
    public int getItemCount() {
        return settingTabInfoList == null ? 0 : settingTabInfoList.size();
    }
}
