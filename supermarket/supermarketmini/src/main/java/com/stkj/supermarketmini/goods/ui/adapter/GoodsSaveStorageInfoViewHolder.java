package com.stkj.supermarketmini.goods.ui.adapter;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.CommonExpandItem;
import com.stkj.supermarketmini.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.supermarketmini.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarketmini.base.utils.PriceUtils;
import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.model.GoodsStorageListInfo;

/**
 * 商品入库列表
 */
public class GoodsSaveStorageInfoViewHolder extends CommonRecyclerViewHolder<GoodsStorageListInfo> {
    public static final int EVENT_CLICK = 1;
    public static final int EVENT_DELETE = 2;
    public static final int EVENT_REFRESH_PRICE = 3;

    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsSpecs;
    private ShapeTextView tvGoodsWholesalePrice;
    private ImageView ivCountMinus;
    private TextView tvGoodsCount;
    private ImageView ivCountPlus;
    private TextView tvGoodsTotalPrice;
    private FrameLayout flGoodsProductDate;
    private ShapeTextView stvGoodsProductDate;
    private ShapeEditText setGoodsExpireDate;
    private ShapeFrameLayout flGoodsExpireDate;
    private TextView stvExpireTag;
    private ImageView ivGoodsExpireDateArrow;

    public GoodsSaveStorageInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        flGoodsProductDate = (FrameLayout) findViewById(R.id.fl_goods_product_date);
        stvGoodsProductDate = (ShapeTextView) findViewById(R.id.stv_goods_product_date);
        setGoodsExpireDate = (ShapeEditText) findViewById(R.id.set_goods_expire_date);
        flGoodsExpireDate = (ShapeFrameLayout) findViewById(R.id.fl_goods_expire_date);
        stvExpireTag = (TextView) findViewById(R.id.stv_expire_tag);
        ivGoodsExpireDateArrow = (ImageView) findViewById(R.id.iv_goods_expire_date_arrow);
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
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
                mDataAdapter.notifyCustomItemEventListener(GoodsSaveStorageInfoViewHolder.this, EVENT_CLICK, mData);
            }
        });
        ivCountMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double newInputGoodsCount = mData.isWeightGoods() ? BigDecimalUtils.sub(mData.getWeightGoodsCountWithDouble(), 1) : mData.getStandardGoodsCountWithInt() - 1;
                if (newInputGoodsCount <= 0) {
                    newInputGoodsCount = 0;
                    ivCountMinus.setEnabled(false);
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
        findViewById(R.id.iv_goods_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsSaveStorageInfoViewHolder.this, EVENT_DELETE, mData);
            }
        });
        flGoodsProductDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, DatePickerDialog.THEME_HOLO_LIGHT);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        stvGoodsProductDate.setText(date);
                        mData.setInputGoodsProductDate(date);
                    }
                });
                datePickerDialog.show();
            }
        });
        flGoodsExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGoodsExpireDateArrow.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(mContext);
                commonExpandListPopWindow.setWidth(flGoodsExpireDate.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsExpireDateArrow.setSelected(false);
                        stvExpireTag.setText(commonExpandItem.getName());
                        mData.setSetGoodsProductExpireTag(commonExpandItem.getName());
                        refreshGoodsProductExpireDays();
                    }
                });
                commonExpandListPopWindow.setExpandItemList(GoodsConstants.getGoodsExpireSpecList());
                commonExpandListPopWindow.showAsDropDown(flGoodsExpireDate);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivGoodsExpireDateArrow.setSelected(false);
                    }
                });
            }
        });
        setGoodsExpireDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshGoodsProductExpireDays();
            }
        });
    }

    /**
     * 刷新商品保质期时间
     */
    private void refreshGoodsProductExpireDays() {
        String expireDateStr = setGoodsExpireDate.getText().toString();
        try {
            mData.setSetGoodsProductExpireDate(expireDateStr);
            if (!TextUtils.isEmpty(expireDateStr)) {
                String expireTag = stvExpireTag.getText().toString();
                float aFloat = Float.parseFloat(expireDateStr);
                if (TextUtils.equals("年", expireTag)) {
                    aFloat = aFloat * 365;
                } else if (TextUtils.equals("月", expireTag)) {
                    aFloat = aFloat * 30;
                }
                int totalDay = (int) aFloat;
                mData.setInputGoodsProductExpireDays(String.valueOf(totalDay));
            } else {
                mData.setInputGoodsProductExpireDays("");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData(GoodsStorageListInfo goodsStorageListInfo) {
        GoodsSaleListInfo data = goodsStorageListInfo.getGoodsSaleListInfo();
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
        tvGoodsWholesalePrice.setText(goodsStorageListInfo.getInputGoodsInitPrice());
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
        mDataAdapter.notifyCustomItemEventListener(this, EVENT_REFRESH_PRICE, mData);
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsStorageListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsStorageListInfo> createViewHolder(View itemView) {
            return new GoodsSaveStorageInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_save_storage_list_info;
        }

        @Override
        public Class<GoodsStorageListInfo> getItemDataClass() {
            return GoodsStorageListInfo.class;
        }
    }


}
