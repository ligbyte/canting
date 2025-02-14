package com.stkj.supermarket.coupon.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.common.StrikeThruTextView;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;

import java.util.List;

/**
 * 商品优惠列表
 */
public class GoodsCouponListInfoViewHolder extends CommonRecyclerViewHolder<GoodsSaleListInfo> {

    public static final int EVENT_CLICK = 1;
    public static final int EVENT_LONG_CLICK = 2;
    private ImageView ivGoodsSelect;
    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsSpecs;
    private TextView tvGoodsQrcode;
    private TextView tvGoodsInventory;
    private TextView tvGoodsWholesalePrice;
    private TextView tvGoodsPrice;
    private TextView tvGoodsExpireDay;
    private StrikeThruTextView tvGoodsOriginPrice;
    private ImageView tvGoodsDiscountTag1;
    private ImageView tvGoodsDiscountTag2;

    public GoodsCouponListInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivGoodsSelect = (ImageView) findViewById(R.id.iv_goods_select);
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        tvGoodsQrcode = (TextView) findViewById(R.id.tv_goods_qrcode);
        tvGoodsInventory = (TextView) findViewById(R.id.tv_goods_inventory);
        tvGoodsWholesalePrice = (TextView) findViewById(R.id.tv_goods_wholesale_price);
        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
        tvGoodsOriginPrice = (StrikeThruTextView) findViewById(R.id.tv_goods_origin_price);
        tvGoodsDiscountTag1 = (ImageView) findViewById(R.id.tv_goods_discount_tag1);
        tvGoodsDiscountTag2 = (ImageView) findViewById(R.id.tv_goods_discount_tag2);
        tvGoodsExpireDay = (TextView) findViewById(R.id.tv_goods_expire_day);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsCouponListInfoViewHolder.this, EVENT_CLICK, mData);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsCouponListInfoViewHolder.this, EVENT_LONG_CLICK, mData);
                return true;
            }
        });
    }

    @Override
    public void initData(GoodsSaleListInfo data) {
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
        tvGoodsQrcode.setText(data.getGoodsCode());
        tvGoodsInventory.setText(data.getGoodsRealStock());
        tvGoodsWholesalePrice.setText(data.getInAvgPrice());
        tvGoodsPrice.setText(data.getDiscountPrice());
        tvGoodsOriginPrice.setText(data.getGoodsUnitPrice());
        List<GoodsSaleListInfo.DiscountType> discountTypeList = data.getDiscountTypeList();
        if (discountTypeList != null && !discountTypeList.isEmpty()) {
            tvGoodsOriginPrice.setStrikeThruText(true);
            GoodsSaleListInfo.DiscountType discountType1 = discountTypeList.get(0);
            tvGoodsDiscountTag1.setVisibility(View.VISIBLE);
            tvGoodsDiscountTag1.setImageResource(GoodsConstants.getDiscountTagImage(discountType1.getType()));
            if (discountTypeList.size() > 1) {
                GoodsSaleListInfo.DiscountType discountType2 = discountTypeList.get(1);
                tvGoodsDiscountTag2.setVisibility(View.VISIBLE);
                tvGoodsDiscountTag2.setImageResource(GoodsConstants.getDiscountTagImage(discountType2.getType()));
            }
        } else {
            tvGoodsOriginPrice.setStrikeThruText(false);
            tvGoodsDiscountTag1.setVisibility(View.GONE);
            tvGoodsDiscountTag2.setVisibility(View.GONE);
        }
        String expireDay = data.getExpireDay();
        try {
            int days = Integer.parseInt(expireDay);
            int color = GoodsConstants.getExpireDayColor(days);
            SpanUtils expireDaysSpan = SpanUtils.with(tvGoodsExpireDay);
            if (days < 0) {
                expireDaysSpan.append("已过期" + Math.abs(days) + "天");
            } else {
                expireDaysSpan.append("剩余" + days + "天");
            }
            expireDaysSpan.setForegroundColor(color).create();
        } catch (Throwable e) {
            e.printStackTrace();
            tvGoodsExpireDay.setText("--");
        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsSaleListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsSaleListInfo> createViewHolder(View itemView) {
            return new GoodsCouponListInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_coupon_list_info;
        }

        @Override
        public Class<GoodsSaleListInfo> getItemDataClass() {
            return GoodsSaleListInfo.class;
        }
    }


}
