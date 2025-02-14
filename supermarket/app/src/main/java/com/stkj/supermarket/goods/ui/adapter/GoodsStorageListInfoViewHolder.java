package com.stkj.supermarket.goods.ui.adapter;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.model.CommonExpandItem;
import com.stkj.supermarket.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsInventoryListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;
import com.stkj.supermarket.goods.model.GoodsStorageListInfo;

/**
 * 入库商品已经入库库存列表
 */
public class GoodsStorageListInfoViewHolder extends CommonRecyclerViewHolder<GoodsStorageListInfo> {

    public static final int EVENT_CLICK = 1;
    public static final int EVENT_SET_DATE = 2;
    public static final int EVENT_DELETE_ITEM = 3;

    private ImageView ivGoodsPic;
    private TextView tvGoodsName;
    private TextView tvGoodsSpecs;
    private TextView tvGoodsInventory;
    private FrameLayout flGoodsProductDate;
    private ShapeTextView stvGoodsProductDate;
    private ImageView ivGoodsProductDateArrow;
    private ShapeEditText setGoodsExpireDate;
    private ShapeFrameLayout flGoodsExpireDate;
    private TextView stvExpireTag;
    private ImageView ivSaveStorageDelete;
    private ImageView ivGoodsExpireDateArrow;

    public GoodsStorageListInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        flGoodsProductDate = (FrameLayout) findViewById(R.id.fl_goods_product_date);
        stvGoodsProductDate = (ShapeTextView) findViewById(R.id.stv_goods_product_date);
        ivGoodsProductDateArrow = (ImageView) findViewById(R.id.iv_goods_product_date_arrow);
        setGoodsExpireDate = (ShapeEditText) findViewById(R.id.set_goods_expire_date);
        flGoodsExpireDate = (ShapeFrameLayout) findViewById(R.id.fl_goods_expire_date);
        stvExpireTag = (TextView) findViewById(R.id.stv_expire_tag);
        ivSaveStorageDelete = (ImageView) findViewById(R.id.iv_save_storage_delete);
        ivGoodsExpireDateArrow = (ImageView) findViewById(R.id.iv_goods_expire_date_arrow);
        ivGoodsPic = (ImageView) findViewById(R.id.iv_goods_pic);
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsSpecs = (TextView) findViewById(R.id.tv_goods_specs);
        tvGoodsInventory = (TextView) findViewById(R.id.tv_goods_inventory);
        stvGoodsProductDate = (ShapeTextView) findViewById(R.id.stv_goods_product_date);
        flGoodsProductDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已经入库商品不可点击
                if (mData.isHasSaveStorage()) {
                    return;
                }
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
                //已经入库商品不可点击
                if (mData.isHasSaveStorage()) {
                    return;
                }
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
        ivSaveStorageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsStorageListInfoViewHolder.this, EVENT_DELETE_ITEM, mData);
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
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsStorageListInfoViewHolder.this, EVENT_CLICK, mData);
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
        GoodsInventoryListInfo inventoryListInfo = goodsStorageListInfo.getInventoryListInfo();
        if (inventoryListInfo == null) {
            return;
        }
        GoodsSaleListInfo data = inventoryListInfo.getGoodsSaleListInfo();
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
        tvGoodsInventory.setText("数量: " + (inventoryListInfo.isWeightGoods() ? inventoryListInfo.getWeightGoodsCount() : inventoryListInfo.getStandardGoodsCount()));
        //生产日期
        String inputGoodsProductDate = goodsStorageListInfo.getInputGoodsProductDate();
        if (!TextUtils.isEmpty(inputGoodsProductDate)) {
            stvGoodsProductDate.setText(inputGoodsProductDate);
        } else {
            stvGoodsProductDate.setText(goodsStorageListInfo.isHasSaveStorage() ? "--" : "");
        }
        //保质期
        String setGoodsProductExpireDate = goodsStorageListInfo.getSetGoodsProductExpireDate();
        String setGoodsProductExpireTag = goodsStorageListInfo.getSetGoodsProductExpireTag();
        if (!TextUtils.isEmpty(setGoodsProductExpireDate)) {
            if (goodsStorageListInfo.isHasSaveStorage()) {
                //已经入库
                setGoodsExpireDate.setText(setGoodsProductExpireDate + setGoodsProductExpireTag);
            } else {
                setGoodsExpireDate.setText(setGoodsProductExpireDate);
            }
        } else {
            setGoodsExpireDate.setText(goodsStorageListInfo.isHasSaveStorage() ? "--" : "");
        }
        //是否已经入库
        if (goodsStorageListInfo.isHasSaveStorage()) {
            ivSaveStorageDelete.setVisibility(View.GONE);
            stvGoodsProductDate.setStrokeWidth(0);
            setGoodsExpireDate.setStrokeWidth(0);
            setGoodsExpireDate.setEnabled(false);
            flGoodsExpireDate.setVisibility(View.GONE);
            ivGoodsProductDateArrow.setVisibility(View.GONE);
        } else {
            ivSaveStorageDelete.setVisibility(View.VISIBLE);
            int dimension = (int) mContext.getResources().getDimension(com.stkj.common.R.dimen.dp_0_5);
            stvGoodsProductDate.setStrokeWidth(dimension);
            setGoodsExpireDate.setStrokeWidth(dimension);
            setGoodsExpireDate.setEnabled(true);
            flGoodsExpireDate.setVisibility(View.VISIBLE);
            ivGoodsProductDateArrow.setVisibility(View.VISIBLE);
        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<GoodsStorageListInfo> {
        @Override
        public CommonRecyclerViewHolder<GoodsStorageListInfo> createViewHolder(View itemView) {
            return new GoodsStorageListInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_storage_list_info;
        }

        @Override
        public Class<GoodsStorageListInfo> getItemDataClass() {
            return GoodsStorageListInfo.class;
        }
    }


}
