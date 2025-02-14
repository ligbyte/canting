package com.stkj.supermarket.goods.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeFrameLayout;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.base.model.CommonExpandItem;
import com.stkj.supermarket.base.ui.fragment.CommonAlertFragment;
import com.stkj.supermarket.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.supermarket.goods.data.GoodsConstants;

/**
 * 输入商品保质期弹窗
 */
public class GoodsExpireDateAlertFragment extends CommonAlertFragment {

    private ShapeEditText setGoodsExpireDate;
    private ShapeFrameLayout flGoodsExpireDate;
    private TextView stvExpireTag;
    private ImageView ivGoodsExpireDateArrow;
    private OnInputExpireDaysListener onInputExpireDaysListener;

    @Override
    protected void initAlertContentView(View contentView) {
        setGoodsExpireDate = (ShapeEditText) findViewById(R.id.set_goods_expire_date);
        flGoodsExpireDate = (ShapeFrameLayout) findViewById(R.id.fl_goods_expire_date);
        stvExpireTag = (TextView) findViewById(R.id.stv_expire_tag);
        ivGoodsExpireDateArrow = (ImageView) findViewById(R.id.iv_goods_expire_date_arrow);
        flGoodsExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivGoodsExpireDateArrow.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(flGoodsExpireDate.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsExpireDateArrow.setSelected(false);
                        stvExpireTag.setText(commonExpandItem.getName());
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
        setLeftNavClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsExpireDateAlertFragment.this);
                if (onInputExpireDaysListener != null) {
                    String expireDateStr = setGoodsExpireDate.getText().toString();
                    String expireTag = stvExpireTag.getText().toString();
                    int totalDay = 0;
                    if (!TextUtils.isEmpty(expireDateStr)) {
                        try {
                            float aFloat = Float.parseFloat(expireDateStr);
                            if (TextUtils.equals("年", expireTag)) {
                                aFloat = aFloat * 365;
                            } else if (TextUtils.equals("月", expireTag)) {
                                aFloat = aFloat * 30;
                            }
                            totalDay = (int) aFloat;
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        onInputExpireDaysListener.onInputExpireDays(expireDateStr + expireTag, totalDay);
                    } else {
                        onInputExpireDaysListener.onInputExpireDays("", 0);
                    }
                }
            }
        });
        setRightNavClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsExpireDateAlertFragment.this);
            }
        });
        setCloseClickListener(new OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), GoodsExpireDateAlertFragment.this);
            }
        });
    }

    @Override
    protected String getAlertTitle() {
        return "商品保质期";
    }

    @Override
    protected int getAlertContentLayResId() {
        return R.layout.fragment_expire_date_alert;
    }

    public void setOnInputExpireDaysListener(OnInputExpireDaysListener onInputExpireDaysListener) {
        this.onInputExpireDaysListener = onInputExpireDaysListener;
    }

    public interface OnInputExpireDaysListener {
        void onInputExpireDays(String expireDate, int expireDays);
    }

}
