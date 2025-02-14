package com.stkj.supermarket.home.ui.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.stkj.common.ui.adapter.CommonFragmentPageAdapter;
import com.stkj.supermarket.coupon.ui.fragment.TabCouponFragment;
import com.stkj.supermarket.goods.ui.fragment.TabGoodsFragment;
import com.stkj.supermarket.home.model.HomeMenuList;
import com.stkj.supermarket.home.model.HomeTabInfo;
import com.stkj.supermarket.pay.ui.fragment.TabPayFragment;
import com.stkj.supermarket.setting.ui.fragment.TabSettingFragment;

import java.util.List;

/**
 * 首页全部界面
 */
public class HomeTabPageAdapter extends CommonFragmentPageAdapter {

    public static final String TAB_PAYMENT_TAG = "cashier";
    public static final String TAB_GOODS_TAG = "product";
    public static final String TAB_COUPON_TAG = "discount";
    public static final String TAB_SETTING_TAG = "set";
    private List<HomeTabInfo<HomeMenuList.Menu>> homeTabInfoList;

    public HomeTabPageAdapter(@NonNull FragmentActivity fragmentActivity, List<HomeTabInfo<HomeMenuList.Menu>> tabInfoList) {
        super(fragmentActivity);
        homeTabInfoList = tabInfoList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        HomeTabInfo<HomeMenuList.Menu> homeTabInfo = homeTabInfoList.get(position);
        HomeMenuList.Menu extraInfo = homeTabInfo.getExtraInfo();
        String path = extraInfo.getPath();
        if (TextUtils.equals(path, TAB_PAYMENT_TAG)) {
            return new TabPayFragment();
        } else if (TextUtils.equals(path, TAB_GOODS_TAG)) {
            return new TabGoodsFragment();
        } else if (TextUtils.equals(path, TAB_COUPON_TAG)) {
            return new TabCouponFragment();
        } else {
            return new TabSettingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return homeTabInfoList == null ? 0 : homeTabInfoList.size();
    }

    public int findTabPageIndex(String path) {
        if (homeTabInfoList != null) {
            int size = homeTabInfoList.size();
            for (int i = 0; i < size; i++) {
                HomeMenuList.Menu extraInfo = homeTabInfoList.get(i).getExtraInfo();
                if (TextUtils.equals(path, extraInfo.getPath())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
