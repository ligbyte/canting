package com.stkj.supermarket.goods.ui.fragment;

import android.view.View;

import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.ui.fragment.CommonAlertFragment;

public class GoodsStorageAlertFragment extends CommonAlertFragment {

    private OnSweetClickListener extraCloseListener;

    public void setExtraCloseListener(OnSweetClickListener extraCloseListener) {
        this.extraCloseListener = extraCloseListener;
    }

    @Override
    protected void initAlertContentView(View contentView) {
        setCloseClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsStorageAlertFragment.this);
                if (extraCloseListener != null) {
                    extraCloseListener.onClick();
                }
            }
        });
    }

    @Override
    protected String getAlertTitle() {
        return "新增商品保存入库";
    }

    @Override
    protected String getRightNavText() {
        return "查看详情";
    }

    @Override
    protected String getLeftNavText() {
        return "继续添加";
    }

    @Override
    protected int getAlertContentLayResId() {
        return R.layout.fragment_alert_goods_storage;
    }
}
