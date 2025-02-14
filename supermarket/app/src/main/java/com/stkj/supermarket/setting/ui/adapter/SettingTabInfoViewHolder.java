package com.stkj.supermarket.setting.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.supermarket.R;
import com.stkj.supermarket.setting.model.SettingTabInfo;

/**
 * 设置页顶部tab
 */
public class SettingTabInfoViewHolder extends CommonRecyclerViewHolder<SettingTabInfo> {

    private ShapeSelectTextView sstvTabName;

    public SettingTabInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        sstvTabName = (ShapeSelectTextView) findViewById(R.id.sstv_tab_name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(SettingTabInfo data) {
        sstvTabName.setShapeSelect(data.isSelected());
        sstvTabName.setText(data.getTabName());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<SettingTabInfo> {
        @Override
        public CommonRecyclerViewHolder<SettingTabInfo> createViewHolder(View itemView) {
            return new SettingTabInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_setting_tab_info;
        }

        @Override
        public Class<SettingTabInfo> getItemDataClass() {
            return SettingTabInfo.class;
        }
    }
}