package com.stkj.supermarket.goods.ui.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.model.CommonExpandItem;
import com.stkj.supermarket.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarket.goods.callback.OnGoodsFilterListener;
import com.stkj.supermarket.goods.data.GoodsConstants;

/**
 * 商品筛选弹窗
 */
public class GoodsFilterAlertFragment extends BaseRecyclerFragment {

    private ImageView ivClose;
    private FrameLayout flGoodsType;
    private ShapeTextView stvGoodsType;
    private ImageView ivGoodsSpecArrow;
    private FrameLayout flGoodsDiscountsTag;
    private ShapeTextView stvGoodsDiscountsTag;
    private ImageView ivGoodsDiscountsTagArrow;
    private LinearLayout flGoodsPrice;
    private ShapeEditText setGoodsPriceStart;
    private ShapeEditText setGoodsPriceEnd;
    private LinearLayout flGoodsStorage;
    private ShapeEditText setGoodsStorageStart;
    private ShapeEditText setGoodsStorageEnd;
    private ShapeTextView stvLeftBt;
    private ShapeTextView stvRightBt;
    private ShapeTextView stvResetBt;
    private OnGoodsFilterListener goodsFilterListener;
    private int goodsType = -1;
    private int discountType = -1;
    private boolean isSelectExpire;

    private ImageView ivExpireDaysLeft;
    private TextView stvExpireDaysLeft;
    private ImageView ivExpiredGoodsRight;
    private TextView stvExpiredGoodsRight;
    private LinearLayout flGoodsExpireDays;
    private ShapeEditText setGoodsExpireDaysStart;
    private ShapeEditText setGoodsExpireDaysEnd;


    public void setGoodsFilterListener(OnGoodsFilterListener goodsFilterListener) {
        this.goodsFilterListener = goodsFilterListener;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_goods_filter_dialog;
    }

    @Override
    protected void initViews(View rootView) {
        ivExpireDaysLeft = (ImageView) findViewById(R.id.iv_expire_days_left);
        stvExpireDaysLeft = (TextView) findViewById(R.id.stv_expire_days_left);
        ivExpiredGoodsRight = (ImageView) findViewById(R.id.iv_expired_goods_right);
        stvExpiredGoodsRight = (TextView) findViewById(R.id.stv_expired_goods_right);
        flGoodsExpireDays = (LinearLayout) findViewById(R.id.fl_goods_expire_days);
        setGoodsExpireDaysStart = (ShapeEditText) findViewById(R.id.set_goods_expire_days_start);
        setGoodsExpireDaysEnd = (ShapeEditText) findViewById(R.id.set_goods_expire_days_end);
        View.OnClickListener expireLeftSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectExpireGoods();
            }
        };
        ivExpireDaysLeft.setOnClickListener(expireLeftSelectListener);
        stvExpireDaysLeft.setOnClickListener(expireLeftSelectListener);
        View.OnClickListener expireRightSelectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectExpireGoods();
            }
        };
        ivExpiredGoodsRight.setOnClickListener(expireRightSelectListener);
        stvExpiredGoodsRight.setOnClickListener(expireRightSelectListener);
        View.OnClickListener backListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsFilterAlertFragment.this);
            }
        };
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(backListener);
        stvGoodsType = (ShapeTextView) findViewById(R.id.stv_goods_type);
        ivGoodsSpecArrow = (ImageView) findViewById(R.id.iv_goods_spec_arrow);
        stvGoodsDiscountsTag = (ShapeTextView) findViewById(R.id.stv_goods_discounts_tag);
        ivGoodsDiscountsTagArrow = (ImageView) findViewById(R.id.iv_goods_discounts_tag_arrow);
        flGoodsPrice = (LinearLayout) findViewById(R.id.fl_goods_price);
        setGoodsPriceStart = (ShapeEditText) findViewById(R.id.set_goods_price_start);
        setGoodsPriceEnd = (ShapeEditText) findViewById(R.id.set_goods_price_end);
        flGoodsStorage = (LinearLayout) findViewById(R.id.fl_goods_storage);
        setGoodsStorageStart = (ShapeEditText) findViewById(R.id.set_goods_storage_start);
        setGoodsStorageEnd = (ShapeEditText) findViewById(R.id.set_goods_storage_end);
        stvLeftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
        stvRightBt = (ShapeTextView) findViewById(R.id.stv_right_bt);
        stvRightBt.setOnClickListener(backListener);
        flGoodsType = (FrameLayout) findViewById(R.id.fl_goods_type);
        flGoodsType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGoodsSpecArrow.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(flGoodsType.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsSpecArrow.setSelected(false);
                        stvGoodsType.setText(commonExpandItem.getName());
                        goodsType = commonExpandItem.getTypeInt();
                    }
                });
                commonExpandListPopWindow.setExpandItemList(GoodsConstants.getGoodsTypeList());
                commonExpandListPopWindow.showAsDropDown(flGoodsType);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivGoodsSpecArrow.setSelected(false);
                    }
                });
            }
        });
        flGoodsDiscountsTag = (FrameLayout) findViewById(R.id.fl_goods_discounts_tag);
        flGoodsDiscountsTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGoodsDiscountsTagArrow.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(flGoodsDiscountsTag.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsDiscountsTagArrow.setSelected(false);
                        stvGoodsDiscountsTag.setText(commonExpandItem.getName());
                        discountType = commonExpandItem.getTypeInt();
                    }
                });
                commonExpandListPopWindow.setExpandItemList(GoodsConstants.getGoodsDiscountTagList());
                commonExpandListPopWindow.showAsDropDown(flGoodsDiscountsTag);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivGoodsDiscountsTagArrow.setSelected(false);
                    }
                });
            }
        });
        stvResetBt = (ShapeTextView) findViewById(R.id.stv_reset_bt);
        stvResetBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stvGoodsType.setText("");
                goodsType = -1;
                stvGoodsDiscountsTag.setText("");
                discountType = -1;
                setGoodsPriceStart.setText("");
                setGoodsPriceEnd.setText("");
                setGoodsStorageStart.setText("");
                setGoodsStorageEnd.setText("");
                isSelectExpire = false;
                setGoodsExpireDaysStart.setText("");
                setGoodsExpireDaysEnd.setText("");
                unSelectExpireGoods();
            }
        });
        stvLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goodsFilterListener != null) {
                    String goodsTypeStr = goodsType == -1 ? "" : String.valueOf(goodsType);
                    String discountTypeStr = discountType == -1 ? "" : String.valueOf(discountType);
                    String minPrice = setGoodsPriceStart.getText().toString();
                    String maxPrice = setGoodsPriceEnd.getText().toString();
                    String minStock = setGoodsStorageStart.getText().toString();
                    String maxStock = setGoodsStorageEnd.getText().toString();
                    String minExpire = setGoodsExpireDaysStart.getText().toString();
                    String maxExpire = setGoodsExpireDaysEnd.getText().toString();
                    goodsFilterListener.onFilter(goodsTypeStr, discountTypeStr, minPrice, maxPrice, minStock, maxStock, minExpire, maxExpire, isSelectExpire);
                }
            }
        });
        unSelectExpireGoods();
    }

    /**
     * 选择已过期商品
     */
    private void selectExpireGoods() {
        isSelectExpire = true;
        flGoodsExpireDays.setVisibility(View.GONE);
        ivExpireDaysLeft.setSelected(false);
        ivExpiredGoodsRight.setSelected(true);
    }

    /**
     * 选择设置保质期时间
     */
    private void unSelectExpireGoods() {
        isSelectExpire = false;
        flGoodsExpireDays.setVisibility(View.VISIBLE);
        ivExpireDaysLeft.setSelected(true);
        ivExpiredGoodsRight.setSelected(false);
    }

}
