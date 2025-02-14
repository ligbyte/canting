package com.stkj.cashiermini.order.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashiermini.R;
import com.stkj.cashiermini.order.data.OrderConstants;
import com.stkj.cashiermini.order.model.OrderListInfo;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;

/**
 * 订单列表
 */
public class OrderListInfoItemViewHolder extends CommonRecyclerViewHolder<OrderListInfo> {

    public static final int EVENT_CLICK = 1;
    private TextView tvUserName;
    private TextView tvUserPhone;
    private TextView tvPayPrice;
    private TextView tvPayStatus;
//    private TextView tvPayType;
    private TextView tvPayDate;
    private ShapeSelectTextView stvRefund;

    public OrderListInfoItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        tvPayPrice = (TextView) findViewById(R.id.tv_pay_price);
        tvPayStatus = (TextView) findViewById(R.id.tv_pay_status);
//        tvPayType = (TextView) findViewById(R.id.tv_pay_type);
        stvRefund = (ShapeSelectTextView) findViewById(R.id.stv_refund);
        tvPayDate = (TextView) findViewById(R.id.tv_pay_date);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(OrderListInfoItemViewHolder.this, EVENT_CLICK, mData);
            }
        });
    }

    @Override
    public void initData(OrderListInfo data) {
        tvUserName.setText(data.getFull_Name());
        tvUserPhone.setText(data.getUser_Tel());
        tvPayPrice.setText("¥ " + data.getBizAmount());
        if (TextUtils.equals("0", data.getStatus())) {
            tvPayStatus.setText("支付成功");
        } else {
            tvPayStatus.setText("支付失败(状态码" + data.getStatus() + ")");
        }
        tvPayDate.setText(data.getBizDate());
        stvRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//        tvPayType.setText(OrderConstants.getPayTypeStr(data.getConsumeMethod()));
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<OrderListInfo> {
        @Override
        public CommonRecyclerViewHolder<OrderListInfo> createViewHolder(View itemView) {
            return new OrderListInfoItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_order_list_info;
        }

        @Override
        public Class<OrderListInfo> getItemDataClass() {
            return OrderListInfo.class;
        }
    }


}
