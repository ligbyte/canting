package com.stkj.infocollect.setting.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.infocollect.R;
import com.stkj.infocollect.setting.model.SettingTabInfo;

/**
 * 设置页左侧tab
 */
public class SettingTabInfoViewHolder extends CommonRecyclerViewHolder<SettingTabInfo> {

    private ShapeLinearLayout sllTab;
    private ImageView ivTabIcon;
    private TextView tvTabName;

    public SettingTabInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        sllTab = (ShapeLinearLayout) findViewById(R.id.sll_tab);
        ivTabIcon = (ImageView) findViewById(R.id.iv_tab_icon);
        tvTabName = (TextView) findViewById(R.id.tv_tab_name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(SettingTabInfo data) {
        tvTabName.setText(data.getTabName());
        ivTabIcon.setImageResource(data.getTabIcon());
        sllTab.setSolidColor(data.isSelected() ? Color.WHITE : Color.TRANSPARENT);
        ivTabIcon.setSelected(data.isSelected());
        tvTabName.setSelected(data.isSelected());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<SettingTabInfo> {
        @Override
        public CommonRecyclerViewHolder<SettingTabInfo> createViewHolder(View itemView) {
            return new SettingTabInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return com.stkj.infocollect.R.layout.item_setting_tab_info;
        }

        @Override
        public Class<SettingTabInfo> getItemDataClass() {
            return SettingTabInfo.class;
        }
    }
}