package com.stkj.supermarket.pay.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.supermarket.R;
import com.stkj.supermarket.pay.model.WaitHistoryOrderInfo;

public class GoodsWaitHistoryOrderInfoViewHolder extends CommonRecyclerViewHolder<WaitHistoryOrderInfo> {

    public static final int EVENT_CLICK = 1;
    public static final int EVENT_LONG_CLICK = 2;

    private ShapeTextView tvOrder;
    private ShapeTextView tvOrderTime;

    public GoodsWaitHistoryOrderInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvOrderTime = (ShapeTextView) findViewById(R.id.tv_order_time);
        tvOrder = (ShapeTextView) findViewById(R.id.tv_order);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsWaitHistoryOrderInfoViewHolder.this, EVENT_CLICK, mData);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsWaitHistoryOrderInfoViewHolder.this, EVENT_LONG_CLICK, mData);
                return true;
            }
        });
    }

    @Override
    public void initData(WaitHistoryOrderInfo data) {
        tvOrderTime.setText(data.getFormatOrderCreateTime());
        tvOrder.setText(data.getTotalCount() + "件\n"
                + "¥" + data.getTotalPrice() + "\n"
                + data.getGoodsListName());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<WaitHistoryOrderInfo> {
        @Override
        public CommonRecyclerViewHolder<WaitHistoryOrderInfo> createViewHolder(View itemView) {
            return new GoodsWaitHistoryOrderInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_wait_order_history;
        }

        @Override
        public Class<WaitHistoryOrderInfo> getItemDataClass() {
            return WaitHistoryOrderInfo.class;
        }
    }
}