package com.stkj.supermarketmini.payment.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.payment.data.PayConstants;
import com.stkj.supermarketmini.payment.model.PayHistoryOrderInfo;

public class GoodsPayHistoryOrderInfoViewHolder extends CommonRecyclerViewHolder<PayHistoryOrderInfo> {
    private TextView tvGoodsNameList;
    private TextView tvOrderId;
    private TextView tvOrderPayTime;
    private TextView tvOrderPrice;
    private ImageView ivPayType;
    private TextView tvOrderStatus;

    public GoodsPayHistoryOrderInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvGoodsNameList = (TextView) findViewById(R.id.tv_goods_name_list);
        tvOrderId = (TextView) findViewById(R.id.tv_order_id);
        tvOrderPayTime = (TextView) findViewById(R.id.tv_order_pay_time);
        tvOrderPrice = (TextView) findViewById(R.id.tv_order_price);
        ivPayType = (ImageView) findViewById(R.id.iv_pay_type);
        tvOrderStatus = (TextView) findViewById(R.id.tv_order_status);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(PayHistoryOrderInfo data) {
        tvGoodsNameList.setText(data.getGoodsListName());
        tvOrderId.setText("订单编号: " + data.getOrderNumber());
        tvOrderPayTime.setText("支付时间: " + data.getFormatOrderPayTime());
        int fontSize = mContext.getResources().getDimensionPixelSize(com.stkj.common.R.dimen.sp_9);
        SpanUtils.with(tvOrderPrice)
                .append("¥")
                .setFontSize(fontSize)
                .append(data.getTotalPrice())
                .create();
        if (TextUtils.equals(PayConstants.ORDER_SUCCESS_STATUS, data.getOrderStatus())) {
            ivPayType.setVisibility(View.VISIBLE);
            ivPayType.setImageResource(PayConstants.getPayTypeRes(data.getPayType()));
            tvOrderStatus.setText("已支付");
        } else {
            ivPayType.setVisibility(View.GONE);
            tvOrderStatus.setText("未支付");
        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<PayHistoryOrderInfo> {
        @Override
        public CommonRecyclerViewHolder<PayHistoryOrderInfo> createViewHolder(View itemView) {
            return new GoodsPayHistoryOrderInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_pay_order_history;
        }

        @Override
        public Class<PayHistoryOrderInfo> getItemDataClass() {
            return PayHistoryOrderInfo.class;
        }
    }
}