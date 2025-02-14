package com.stkj.supermarketmini.payment.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.payment.model.GoodsOrderListInfo;

/**
 * 订单商品详细
 */
public class GoodsOrderHistoryDetailItemHolder extends CommonRecyclerViewHolder<GoodsOrderListInfo> {

    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsQrcode;
    private TextView tvGoodsSpecs;
    private TextView tvGoodsCount;

    public GoodsOrderHistoryDetailItemHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsQrcode = (TextView) findViewById(R.id.tv_goods_qrcode);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
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
        tvGoodsQrcode.setText("条码: " + data.getGoodsCode());
        tvGoodsSpecs.setText("规格: " + data.getGoodsSpecStr());
        if (goodsOrderListInfo.isWeightGoods()) {
            tvGoodsCount.setText("x" + goodsOrderListInfo.getWeightGoodsCount() + "kg");
        } else {
            tvGoodsCount.setText("x" + goodsOrderListInfo.getInputGoodsCountWithInt());
        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsOrderListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsOrderListInfo> createViewHolder(View itemView) {
            return new GoodsOrderHistoryDetailItemHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_order_history_detail;
        }

        @Override
        public Class<GoodsOrderListInfo> getItemDataClass() {
            return GoodsOrderListInfo.class;
        }
    }
}