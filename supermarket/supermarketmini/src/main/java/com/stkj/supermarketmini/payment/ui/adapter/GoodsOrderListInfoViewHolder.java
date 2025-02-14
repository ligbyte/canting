package com.stkj.supermarketmini.payment.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.common.StrikeThruTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.supermarketmini.base.utils.PriceUtils;
import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.login.helper.LoginHelper;
import com.stkj.supermarketmini.payment.model.GoodsOrderListInfo;

import java.util.List;

/**
 * 商品结算订单列表
 */
public class GoodsOrderListInfoViewHolder extends CommonRecyclerViewHolder<GoodsOrderListInfo> {
    public static final int EVENT_CLICK = 1;
    public static final int EVENT_DELETE = 2;
    public static final int EVENT_REFRESH_PRICE = 3;

    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsSpecs;
    private TextView tvGoodsPrice;
    private StrikeThruTextView tvGoodsOriginPrice;
    private ImageView ivCountMinus;
    private TextView tvGoodsCount;
    private ImageView ivCountPlus;
    private TextView tvGoodsTotalPrice;
    private TextView tvWeightGoodsCount;
    private LinearLayout llStandardGoodsCount;

    public GoodsOrderListInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        llStandardGoodsCount = (LinearLayout) findViewById(R.id.ll_standard_goods_count);
        tvWeightGoodsCount = (TextView) findViewById(R.id.tv_weight_goods_count);
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
        tvGoodsOriginPrice = (StrikeThruTextView) findViewById(R.id.tv_goods_origin_price);
        ivCountMinus = (ImageView) findViewById(R.id.iv_count_minus);
        tvGoodsCount = (TextView) findViewById(R.id.et_goods_count);
        tvGoodsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.setChange(true);
                CommonInputDialogFragment.build()
                        .setTitle("数量")
                        .setInputContent(tvGoodsCount.getText().toString())
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER)
                        .setNeedLimitNumber(true)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                try {
                                    int parseInt = Integer.parseInt(input);
                                    ivCountMinus.setEnabled(true);
                                    ivCountPlus.setEnabled(true);
                                    if (parseInt <= 0) {
                                        ivCountMinus.setEnabled(false);
                                    } else if (parseInt >= 9999) {
                                        ivCountPlus.setEnabled(false);
                                    }
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                tvGoodsCount.setText(input);
                                mData.setInputGoodsCount(input);
                                refreshStandardTotalPrice();
                                mDataAdapter.notifyCustomItemEventListener(GoodsOrderListInfoViewHolder.this, EVENT_REFRESH_PRICE, mData);
                            }
                        }).show(mContext);
            }
        });
        ivCountPlus = (ImageView) findViewById(R.id.iv_count_plus);
        tvGoodsTotalPrice = (TextView) findViewById(R.id.tv_goods_total_price);
        tvGoodsTotalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否有修改权限
                if (!LoginHelper.INSTANCE.hasPermissionChangePrice()) {
                    return;
                }
                mData.setChange(true);
                CommonInputDialogFragment.build()
                        .setTitle("小计")
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL)
                        .setInputContent(tvGoodsTotalPrice.getText().toString())
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                mData.setInputGoodsTotalPrice(input);
                                tvGoodsTotalPrice.setText(input);
                                mDataAdapter.notifyCustomItemEventListener(GoodsOrderListInfoViewHolder.this, EVENT_REFRESH_PRICE, mData);
                            }
                        }).show(mContext);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsOrderListInfoViewHolder.this, EVENT_CLICK, mData);
            }
        });
        ivCountMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newInputGoodsCount = mData.getInputGoodsCountWithInt() - 1;
                if (newInputGoodsCount <= 0) {
                    newInputGoodsCount = 0;
                    ivCountMinus.setEnabled(false);
                }
                String newCountStr = String.valueOf(newInputGoodsCount);
                mData.setInputGoodsCount(newCountStr);
                tvGoodsCount.setText(newCountStr);
                refreshStandardTotalPrice();
                mDataAdapter.notifyCustomItemEventListener(GoodsOrderListInfoViewHolder.this, EVENT_REFRESH_PRICE, mData);
            }
        });
        ivCountPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newInputGoodsCount = mData.getInputGoodsCountWithInt() + 1;
                if (newInputGoodsCount >= 1) {
                    ivCountMinus.setEnabled(true);
                    if (newInputGoodsCount >= 9999) {
                        newInputGoodsCount = 9999;
                        ivCountPlus.setEnabled(false);
                    }
                }
                String newCountStr = String.valueOf(newInputGoodsCount);
                mData.setInputGoodsCount(newCountStr);
                tvGoodsCount.setText(newCountStr);
                refreshStandardTotalPrice();
                mDataAdapter.notifyCustomItemEventListener(GoodsOrderListInfoViewHolder.this, EVENT_REFRESH_PRICE, mData);
            }
        });
        findViewById(R.id.iv_goods_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsOrderListInfoViewHolder.this, EVENT_DELETE, mData);
            }
        });
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
                GlideApp.with(mContext).load(picUrl).transform(new RoundedCorners(12)).placeholder(R.mipmap.icon_goods_default).into(ivGoodsPic);
            } else {
                ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
            }
        } else {
            ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
        }
        tvGoodsName.setText(data.getGoodsName());
        tvGoodsSpecs.setText("规格: " + data.getGoodsSpecStr());
        tvGoodsPrice.setText(data.getDiscountPrice());
        tvGoodsOriginPrice.setText(data.getGoodsUnitPrice());
        List<GoodsSaleListInfo.DiscountType> discountTypeList = data.getDiscountTypeList();
        tvGoodsOriginPrice.setStrikeThruText(discountTypeList != null && !discountTypeList.isEmpty());
        int inputGoodsCount = mData.getInputGoodsCountWithInt();
        int goodsType = data.getGoodsType();
        //称重商品
        if (goodsType == GoodsConstants.TYPE_GOODS_WEIGHT) {
            llStandardGoodsCount.setVisibility(View.GONE);
            tvWeightGoodsCount.setVisibility(View.VISIBLE);
            tvWeightGoodsCount.setText(mData.getWeightGoodsCount() + "kg");
            tvGoodsTotalPrice.setText(mData.getInputGoodsTotalPrice());
        } else {
            llStandardGoodsCount.setVisibility(View.VISIBLE);
            tvWeightGoodsCount.setVisibility(View.GONE);
            ivCountMinus.setEnabled(inputGoodsCount > 0);
            ivCountPlus.setEnabled(inputGoodsCount <= 9999);
            tvGoodsCount.setText(String.valueOf(inputGoodsCount));
            refreshStandardTotalPrice();
        }
    }

    private void refreshStandardTotalPrice() {
        double inputGoodsCount = mData.getInputGoodsCountWithInt();
        double goodsSalePrice = mData.getSalePrice();
        double totalPrice = BigDecimalUtils.mul(goodsSalePrice, inputGoodsCount);
        mData.setInputGoodsTotalPrice(String.valueOf(totalPrice));
        tvGoodsTotalPrice.setText("小计: ¥" + PriceUtils.formatPrice2(totalPrice));
        LogHelper.print("--refreshOrderListAndPrice--orderList: totalDiscountPrice: " + totalPrice);
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsOrderListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsOrderListInfo> createViewHolder(View itemView) {
            return new GoodsOrderListInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_order_list_info;
        }

        @Override
        public Class<GoodsOrderListInfo> getItemDataClass() {
            return GoodsOrderListInfo.class;
        }
    }


}
