package com.stkj.cashier.stat.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashier.R;
import com.stkj.cashier.pay.data.PayConstants;
import com.stkj.cashier.pay.model.ConsumerRecordListInfo;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 统计页面消费账单列表
 */
public class StatConsumerRecordListItemViewHolder extends CommonRecyclerViewHolder<ConsumerRecordListInfo> {

    public static final int EVENT_CLICK = 1;
    private TextView tvName;
    private TextView tvAccount;
    private TextView tvFeeType;
    private TextView tvAmount;
    private TextView tvPayType;
    private TextView tvTime;

    public StatConsumerRecordListItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tvFeeType = (TextView) findViewById(R.id.tv_fee_type);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvPayType = (TextView) findViewById(R.id.tv_pay_type);
        tvTime = (TextView) findViewById(R.id.tv_time);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(StatConsumerRecordListItemViewHolder.this, EVENT_CLICK, mData);
            }
        });
    }

    @Override
    public void initData(ConsumerRecordListInfo data) {
        tvName.setText(data.getFull_Name());
        tvAccount.setText(data.getUser_Tel());
        tvFeeType.setText(PayConstants.getFeeTypeStr(data.getFeeType()));
        tvAmount.setText(data.getBizAmount());
        tvPayType.setText(PayConstants.getPayTypeStr(data.getConsumeMethod()));
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTime.setText(data.getBizDate());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<ConsumerRecordListInfo> {
        @Override
        public CommonRecyclerViewHolder<ConsumerRecordListInfo> createViewHolder(View itemView) {
            return new StatConsumerRecordListItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_stat_consumer_record_list;
        }

        @Override
        public Class<ConsumerRecordListInfo> getItemDataClass() {
            return ConsumerRecordListInfo.class;
        }
    }


}
