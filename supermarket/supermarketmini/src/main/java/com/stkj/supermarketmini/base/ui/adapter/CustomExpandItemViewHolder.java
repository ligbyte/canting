package com.stkj.supermarketmini.base.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.linelayout.LineLinearLayout;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.CustomExpandItem;

/**
 * 自定义下拉选择 item
 */
public class CustomExpandItemViewHolder extends CommonRecyclerViewHolder<CustomExpandItem> {

    private LineLinearLayout lineLinearLayout;

    public CustomExpandItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        lineLinearLayout = (LineLinearLayout) itemView;
    }

    @Override
    public void initData(CustomExpandItem data) {
        lineLinearLayout.setLineBottom(getDataPosition() != (mDataAdapter.getItemCount() - 1));
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(CustomExpandItemViewHolder.this, CommonExpandItemViewHolder.EVENT_CLICK, mData);
            }
        });
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<CustomExpandItem> {
        @Override
        public CommonRecyclerViewHolder<CustomExpandItem> createViewHolder(View itemView) {
            return new CustomExpandItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_custom_expand_list;
        }

        @Override
        public Class<CustomExpandItem> getItemDataClass() {
            return CustomExpandItem.class;
        }
    }

}
