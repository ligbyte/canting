package com.stkj.supermarket.goods.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.common.StrikeThruTextView;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.supermarket.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.supermarket.base.utils.CommonDialogUtils;
import com.stkj.supermarket.base.utils.PriceUtils;
import com.stkj.supermarket.goods.model.GoodsInventoryListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;

import java.util.List;

/**
 * 商品入库库存列表
 */
public class GoodsInventoryListInfoViewHolder extends CommonRecyclerViewHolder<GoodsInventoryListInfo> {
    public static final int EVENT_CLICK = 1;
    public static final int EVENT_SELECTOR = 2;
    public static final int EVENT_REFRESH_PRICE = 3;
    public static final int EVENT_DELETE_ITEM = 4;

    private ImageView ivGoodsSelect;
    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsSpecs;
    private TextView tvGoodsInventory;
    private TextView tvGoodsPrice;
    private StrikeThruTextView tvGoodsOriginPrice;
    private ShapeTextView tvGoodsWholesalePrice;
    private ImageView ivCountMinus;
    private TextView tvGoodsCount;
    private ImageView ivCountPlus;
    private TextView tvGoodsTotalPrice;

    public GoodsInventoryListInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivGoodsSelect = (ImageView) findViewById(R.id.iv_goods_select);
        ivGoodsSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selector = !mData.isSelected();
                if (selector) {
                    //选中状态，如果进货价和数量没有直接提示
                    if (!mData.hasGoodsCount()) {
                        CommonDialogUtils.showTipsDialog(mContext, "商品入库数量不能为0");
                        return;
                    }
                    if (!mData.hasGoodsInitPrice()) {
                        CommonDialogUtils.showTipsDialog(mContext, "商品进货价不能为0");
                        return;
                    }
                }
                mData.setSelected(selector);
                notifyItemDataChange();
                mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_SELECTOR, mData);
            }
        });
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        tvGoodsInventory = (TextView) findViewById(R.id.tv_goods_inventory);
        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);
        tvGoodsOriginPrice = (StrikeThruTextView) findViewById(R.id.tv_goods_origin_price);
        tvGoodsWholesalePrice = (ShapeTextView) findViewById(R.id.set_goods_wholesale_price);
        tvGoodsWholesalePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("进货价")
                        .setInputContent(tvGoodsWholesalePrice.getText().toString())
                        .setInputType(CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                tvGoodsWholesalePrice.setText(input);
                                mData.setInputGoodsInitPrice(input);
                                refreshTotalPrice();
                            }
                        }).show(mContext);
            }
        });
        ivCountMinus = (ImageView) findViewById(R.id.iv_count_minus);
        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
        tvGoodsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonInputDialogFragment.build()
                        .setTitle("数量")
                        .setInputContent(tvGoodsCount.getText().toString())
                        .setInputType(mData.isWeightGoods() ? CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL : CommonInputDialogFragment.INPUT_TYPE_NUMBER)
                        .setNeedLimitNumber(true)
                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                            @Override
                            public void onInputEnd(String input) {
                                try {
                                    double parseDouble = Double.parseDouble(input);
                                    ivCountMinus.setEnabled(true);
                                    ivCountPlus.setEnabled(true);
                                    if (parseDouble <= 0) {
                                        ivCountMinus.setEnabled(false);
                                    } else if (parseDouble >= 9999) {
                                        ivCountPlus.setEnabled(false);
                                    }
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                tvGoodsCount.setText(input);
                                if (mData.isWeightGoods()) {
                                    mData.setWeightGoodsCount(input);
                                } else {
                                    mData.setStandardGoodsCount(input);
                                }
                                refreshTotalPrice();
                            }
                        }).show(mContext);
            }
        });
        ivCountPlus = (ImageView) findViewById(R.id.iv_count_plus);
        tvGoodsTotalPrice = (TextView) findViewById(R.id.et_goods_total_price);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_CLICK, mData);
            }
        });
        ivCountMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double newInputGoodsCount = mData.isWeightGoods() ? BigDecimalUtils.sub(mData.getWeightGoodsCountWithDouble(), 1) : mData.getStandardGoodsCountWithInt() - 1;
                if (newInputGoodsCount <= 0) {
                    newInputGoodsCount = 0;
                    ivCountMinus.setEnabled(false);
                    //库存为 0 取消选中状态
                    mData.setSelected(false);
                    ivGoodsSelect.setSelected(false);
                    mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_SELECTOR, mData);
                }
                String newCountStr;
                if (mData.isWeightGoods()) {
                    newCountStr = String.valueOf(newInputGoodsCount);
                    mData.setWeightGoodsCount(newCountStr);
                } else {
                    newCountStr = String.valueOf((int) newInputGoodsCount);
                    mData.setStandardGoodsCount(newCountStr);
                }
                tvGoodsCount.setText(newCountStr);
                refreshTotalPrice();
            }
        });
        ivCountPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double newInputGoodsCount = mData.isWeightGoods() ? BigDecimalUtils.add(mData.getWeightGoodsCountWithDouble(), 1) : mData.getStandardGoodsCountWithInt() + 1;
                if (newInputGoodsCount >= 1) {
                    ivCountMinus.setEnabled(true);
                    if (newInputGoodsCount >= 9999) {
                        newInputGoodsCount = 9999;
                        ivCountPlus.setEnabled(false);
                    }
                }
                String newCountStr;
                if (mData.isWeightGoods()) {
                    newCountStr = String.valueOf(newInputGoodsCount);
                    mData.setWeightGoodsCount(newCountStr);
                } else {
                    newCountStr = String.valueOf((int) newInputGoodsCount);
                    mData.setStandardGoodsCount(newCountStr);
                }
                tvGoodsCount.setText(newCountStr);
                refreshTotalPrice();
            }
        });
        mItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mData.isSelected()) {
                    CommonDialogUtils.showTipsDialog(mContext, "当前商品已经选中，请先取消选中");
                    return true;
                }
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("确认删除当前要入库的商品吗？")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                mDataAdapter.removeData(mData);
                                mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_DELETE_ITEM, mData);
                            }
                        })
                        .setRightNavTxt("取消").show(mContext);
                return true;
            }
        });
    }

    @Override
    public void initData(GoodsInventoryListInfo goodsInventoryListInfo) {
        ivGoodsSelect.setSelected(goodsInventoryListInfo.isSelected());
        GoodsSaleListInfo data = goodsInventoryListInfo.getGoodsSaleListInfo();
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
        tvGoodsInventory.setText(data.getGoodsRealStock());
        tvGoodsPrice.setText(data.getDiscountPrice());
        tvGoodsOriginPrice.setText(data.getGoodsUnitPrice());
        tvGoodsWholesalePrice.setText(goodsInventoryListInfo.getInputGoodsInitPrice());
        List<GoodsSaleListInfo.DiscountType> discountTypeList = data.getDiscountTypeList();
        tvGoodsOriginPrice.setStrikeThruText(discountTypeList != null && !discountTypeList.isEmpty());
        if (mData.isWeightGoods()) {
            double inputGoodsCount = mData.getWeightGoodsCountWithDouble();
            ivCountMinus.setEnabled(inputGoodsCount > 0);
            ivCountPlus.setEnabled(inputGoodsCount <= 9999);
            tvGoodsCount.setText(String.valueOf(inputGoodsCount));
        } else {
            int inputGoodsCount = mData.getStandardGoodsCountWithInt();
            ivCountMinus.setEnabled(inputGoodsCount > 0);
            ivCountPlus.setEnabled(inputGoodsCount <= 9999);
            tvGoodsCount.setText(String.valueOf(inputGoodsCount));
        }
        refreshTotalPrice();
    }

    private void refreshTotalPrice() {
        double inputGoodsCount = mData.isWeightGoods() ? mData.getWeightGoodsCountWithDouble() : mData.getStandardGoodsCountWithInt();
        double inputGoodsInitPrice = mData.getInputGoodsInitPriceWithDouble();
        double totalPrice = BigDecimalUtils.mul(inputGoodsInitPrice, inputGoodsCount);
        tvGoodsTotalPrice.setText("小计: ¥ " + PriceUtils.formatPrice2(totalPrice));
        //商品被选中时 需要刷新右侧入库的商品列表头部总价格
        if (mData.isSelected()) {
            mDataAdapter.notifyCustomItemEventListener(this, EVENT_REFRESH_PRICE, mData);
        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsInventoryListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsInventoryListInfo> createViewHolder(View itemView) {
            return new GoodsInventoryListInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_inventory_list_info;
        }

        @Override
        public Class<GoodsInventoryListInfo> getItemDataClass() {
            return GoodsInventoryListInfo.class;
        }
    }


}
