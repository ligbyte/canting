package com.stkj.supermarket.goods.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsBatchListInfo;

public class GoodsDetailBatchInfoViewHolder extends CommonRecyclerViewHolder<GoodsBatchListInfo> {

    public static final String GOODS_BATCH_TOTAL_COUNT = "batch_total_count";

    private TextView tvGoodsBatchNumber;
    private TextView tvGoodsStorageTime;
    private TextView tvGoodsProductDate;
    private TextView tvGoodsInitPrice;
    private TextView tvGoodsStorageCount;
    private TextView tvGoodsExpireDays;

    public GoodsDetailBatchInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvGoodsBatchNumber = (TextView) findViewById(R.id.tv_goods_batch_number);
        tvGoodsStorageTime = (TextView) findViewById(R.id.tv_goods_storage_time);
        tvGoodsProductDate = (TextView) findViewById(R.id.tv_goods_product_date);
        tvGoodsInitPrice = (TextView) findViewById(R.id.tv_goods_init_price);
        tvGoodsStorageCount = (TextView) findViewById(R.id.tv_goods_storage_count);
        tvGoodsExpireDays = (TextView) findViewById(R.id.tv_goods_expire_days);
    }

    @Override
    public void initData(GoodsBatchListInfo data) {
        //减去商品详情头部
        int adapterPosition = getDataPosition() - 1;
        int batchTotalCount = getBatchTotalCount();
        if (batchTotalCount != 0) {
            SpanUtils.with(tvGoodsBatchNumber)
                    .append(String.valueOf(batchTotalCount - adapterPosition))
                    .setForegroundColor(mContext.getResources().getColor(R.color.color_259AFE))
                    .setFontSize((int) mContext.getResources().getDimension(com.stkj.common.R.dimen.sp_9))
                    .append("  (批次号: " + data.getBatchNo() + ")")
                    .create();
        } else {
            tvGoodsBatchNumber.setText(data.getBatchNo());
        }
        tvGoodsStorageTime.setText(data.getInDate());
        tvGoodsInitPrice.setText(data.getInUnitPrice());
        tvGoodsStorageCount.setText(data.getInCount());
        String productDate = data.getProductDate();
        if (TextUtils.isEmpty(productDate)) {
            tvGoodsProductDate.setText("--");
        } else {
            tvGoodsProductDate.setText(productDate);
        }
        //保质期天数
        String expireDays = data.getExpireDays();
        //剩余天数
        String expireDay = data.getExpireDay();
        if (TextUtils.isEmpty(expireDays)) {
            tvGoodsExpireDays.setText("--");
        } else {
            int days = 0;
            try {
                days = Integer.parseInt(expireDay);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            SpanUtils expireDaysSpan = SpanUtils.with(tvGoodsExpireDays)
                    .append(expireDays + " 天");
            if (days < 0) {
                expireDaysSpan.append("(已过期" + Math.abs(days) + "天)");
            } else {
                expireDaysSpan.append("(剩余" + days + "天)");
            }
            expireDaysSpan.setForegroundColor(GoodsConstants.getExpireDayColor(days)).create();
        }
    }

    private int getBatchTotalCount() {
        Object adapterPrivateData = mDataAdapter.getAdapterPrivateData(GOODS_BATCH_TOTAL_COUNT);
        if (adapterPrivateData instanceof Integer) {
            return (Integer) adapterPrivateData;
        }
        return 0;
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsBatchListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsBatchListInfo> createViewHolder(View itemView) {
            return new GoodsDetailBatchInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_detail_batch_info;
        }

        @Override
        public Class<GoodsBatchListInfo> getItemDataClass() {
            return GoodsBatchListInfo.class;
        }
    }
}
