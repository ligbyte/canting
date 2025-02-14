package com.stkj.supermarket.consumer.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.device.DeviceManager;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.pay.model.GoodsOrderListInfo;

public class GoodsConsumerViewHolder extends CommonRecyclerViewHolder<GoodsOrderListInfo> {

    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsSpecs;
    private TextView tvGoodsCount;
    private TextView tvGoodsPrice;

    public GoodsConsumerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
    }

    @Override
    public void initData(GoodsOrderListInfo goodsOrderListInfo) {
        GoodsSaleListInfo data = goodsOrderListInfo.getGoodsSaleListInfo();
        if (data == null) {
            return;
        }
        String goodsImg = data.getGoodsImg();
        String[] split = goodsImg.split(",");
        if (split.length > 0) {
            String picUrl = split[0];
            if (!TextUtils.isEmpty(picUrl)) {
                GlideApp.with(mContext).load(picUrl).placeholder(R.mipmap.icon_goods_default).into(ivGoodsPic);
            } else {
                ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
            }
        } else {
            ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
        }
        tvGoodsName.setText(data.getGoodsName());
        tvGoodsSpecs.setText("规格: " + data.getGoodsSpecStr());
        int inputGoodsCount = mData.getInputGoodsCountWithInt();
        int goodsType = data.getGoodsType();
        //称重商品
        if (goodsType == GoodsConstants.TYPE_GOODS_WEIGHT) {
            tvGoodsCount.setText(mData.getWeightGoodsCount() + "kg");
        } else {
            tvGoodsCount.setText(String.valueOf(inputGoodsCount));
        }
        tvGoodsPrice.setText(mData.getInputGoodsTotalPrice());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsOrderListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsOrderListInfo> createViewHolder(View itemView) {
            return new GoodsConsumerViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            int consumeLayRes = DeviceManager.INSTANCE.getDeviceInterface().getConsumeLayRes();
            if (consumeLayRes == 1) {
                return R.layout.item_consumer_goods_list_info_s1;
            } else if (consumeLayRes == 2) {
                return R.layout.item_consumer_goods_list_info_s2;
            }
            return R.layout.item_consumer_goods_list_info;
        }

        @Override
        public Class<GoodsOrderListInfo> getItemDataClass() {
            return GoodsOrderListInfo.class;
        }
    }
}