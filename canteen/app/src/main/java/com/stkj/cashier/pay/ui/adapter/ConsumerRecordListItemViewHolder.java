package com.stkj.cashier.pay.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashier.R;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.ConsumerRecordListInfo;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;

/**
 * 消费账单列表
 */
public class ConsumerRecordListItemViewHolder extends CommonRecyclerViewHolder<ConsumerRecordListInfo> {

    public static final int EVENT_CLICK = 1;
    private TextView tvUserName;
    private TextView tvUserPhone;
    private TextView tvPayPrice;
    private TextView tvPayDate;
    private TextView tvPaySuccess;
    private ShapeLinearLayout sllPayWait;

    public ConsumerRecordListItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserPhone = (TextView) findViewById(R.id.tv_user_phone);
        tvPayPrice = (TextView) findViewById(R.id.tv_pay_price);
        tvPaySuccess = (TextView) findViewById(R.id.tv_pay_success);
        sllPayWait = (ShapeLinearLayout) findViewById(R.id.sll_pay_wait);
        tvPayDate = (TextView) findViewById(R.id.tv_pay_date);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(ConsumerRecordListItemViewHolder.this, EVENT_CLICK, mData);
            }
        });
    }

    @Override
    public void initData(ConsumerRecordListInfo data) {
        tvUserName.setText(data.getFull_Name());
        tvUserPhone.setText(data.getUser_Tel());
        tvPayPrice.setText("¥ " + data.getBizAmount());
        if (TextUtils.equals(PayConstants.RECORD_TYPE_REFUND, data.getType())) {
            tvPaySuccess.setText("已退款");
            tvPaySuccess.setVisibility(View.VISIBLE);
            sllPayWait.setVisibility(View.GONE);
        } else if (TextUtils.equals("0", data.getStatus())) {
            tvPaySuccess.setText("支付成功");
            tvPaySuccess.setVisibility(View.VISIBLE);
            sllPayWait.setVisibility(View.GONE);
        } else {
            tvPaySuccess.setVisibility(View.GONE);
            sllPayWait.setVisibility(View.VISIBLE);
        }
        tvPayDate.setText(data.getBizDate());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<ConsumerRecordListInfo> {
        @Override
        public CommonRecyclerViewHolder<ConsumerRecordListInfo> createViewHolder(View itemView) {
            return new ConsumerRecordListItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_consumer_record_list_info;
        }

        @Override
        public Class<ConsumerRecordListInfo> getItemDataClass() {
            return ConsumerRecordListInfo.class;
        }
    }


}
