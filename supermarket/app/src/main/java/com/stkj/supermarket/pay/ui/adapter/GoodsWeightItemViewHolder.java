package com.stkj.supermarket.pay.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectRatioLinearLayout;
import com.stkj.supermarket.R;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.pay.model.GoodsWeightItemInfo;

public class GoodsWeightItemViewHolder extends CommonRecyclerViewHolder<GoodsWeightItemInfo> {

    public static final int EVENT_SELECTOR_ITEM = 1;

    private TextView sstvGoodsName;
    private TextView sstvGoodsPrice;
    private ShapeSelectRatioLinearLayout rflGoods;

    public GoodsWeightItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        rflGoods = (ShapeSelectRatioLinearLayout) findViewById(R.id.rfl_goods);
        sstvGoodsName = (TextView) findViewById(R.id.sstv_goods_name);
        sstvGoodsPrice = (TextView) findViewById(R.id.sstv_goods_price);
        rflGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsWeightItemViewHolder.this, EVENT_SELECTOR_ITEM, mData);
            }
        });
    }

    @Override
    public void initData(GoodsWeightItemInfo data) {
        rflGoods.setShapeSelect(data.isSelect());
        boolean select = data.isSelect();
        rflGoods.setShapeSelect(select);
        sstvGoodsName.setSelected(select);
        sstvGoodsPrice.setSelected(select);
        GoodsSaleListInfo saleListInfo = data.getSaleListInfo();
        if (saleListInfo == null) {
            return;
        }
        sstvGoodsName.setText(saleListInfo.getGoodsName());
        sstvGoodsPrice.setText("¥" + saleListInfo.getDiscountPrice() + "/kg");
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsWeightItemInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsWeightItemInfo> createViewHolder(View itemView) {
            return new GoodsWeightItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return com.stkj.supermarket.R.layout.item_goods_weight;
        }

        @Override
        public Class<GoodsWeightItemInfo> getItemDataClass() {
            return GoodsWeightItemInfo.class;
        }
    }
}