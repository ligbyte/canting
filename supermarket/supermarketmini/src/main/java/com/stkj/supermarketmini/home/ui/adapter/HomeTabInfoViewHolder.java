package com.stkj.supermarketmini.home.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.utils.DensityUtil;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.home.model.HomeMenuInfo;
import com.stkj.supermarketmini.home.model.HomeTabInfo;

/**
 * 首页切换的tab
 */
public class HomeTabInfoViewHolder extends CommonRecyclerViewHolder<HomeTabInfo> {

    private ShapeLinearLayout sllTab;
    private ImageView ivTabIcon;
    private TextView tvTabName;


    public HomeTabInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        sllTab = (ShapeLinearLayout) findViewById(R.id.sll_tab);
        ivTabIcon = (ImageView) findViewById(R.id.iv_tab_icon);
        tvTabName = (TextView) findViewById(R.id.tv_tab_name);
    }

    @Override
    public void initData(HomeTabInfo data) {
        Object extraInfo = data.getExtraInfo();
        if (extraInfo instanceof HomeMenuInfo) {
            HomeMenuInfo homeMenuInfo = (HomeMenuInfo) extraInfo;
            tvTabName.setText(homeMenuInfo.getName());
        }

        ivTabIcon.setImageResource(data.getSelectRes());
        if (data.isSelect()) {
            sllTab.setRadius(DensityUtil.dip2px(mContext, 5));
            sllTab.setSolidColor(mContext.getColor(R.color.color_D3EAFF));
        } else {
            sllTab.setRadius(0);
            sllTab.setSolidColor(Color.TRANSPARENT);
        }
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(mItemView, mData);
            }
        });
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<HomeTabInfo> {
        @Override
        public CommonRecyclerViewHolder<HomeTabInfo> createViewHolder(View itemView) {
            return new HomeTabInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_home_tab_info;
        }

        @Override
        public Class<HomeTabInfo> getItemDataClass() {
            return HomeTabInfo.class;
        }
    }


}
