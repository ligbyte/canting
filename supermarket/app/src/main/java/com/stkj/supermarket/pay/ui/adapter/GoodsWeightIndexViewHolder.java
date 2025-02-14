package com.stkj.supermarket.pay.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.supermarket.R;
import com.stkj.supermarket.pay.model.GoodsWeightIndexInfo;

public class GoodsWeightIndexViewHolder extends CommonRecyclerViewHolder<GoodsWeightIndexInfo> {

    private TextView tvIndexTitle;

    public GoodsWeightIndexViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvIndexTitle = (TextView) findViewById(R.id.tv_index_title);
    }

    @Override
    public void initData(GoodsWeightIndexInfo data) {
        tvIndexTitle.setText(data.getTitleIndex());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsWeightIndexInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsWeightIndexInfo> createViewHolder(View itemView) {
            return new GoodsWeightIndexViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_weight_index_title;
        }

        @Override
        public Class<GoodsWeightIndexInfo> getItemDataClass() {
            return GoodsWeightIndexInfo.class;
        }
    }
}