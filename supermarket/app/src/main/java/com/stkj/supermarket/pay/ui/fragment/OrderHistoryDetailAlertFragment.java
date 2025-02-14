package com.stkj.supermarket.pay.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.supermarket.R;
import com.stkj.supermarket.pay.model.GoodsOrderListInfo;
import com.stkj.supermarket.pay.model.PayHistoryOrderInfo;
import com.stkj.supermarket.pay.model.WaitHistoryOrderInfo;
import com.stkj.supermarket.pay.ui.adapter.GoodsOrderHistoryDetailItemHolder;

import java.util.List;

public class OrderHistoryDetailAlertFragment extends BaseRecyclerFragment {

    private RecyclerView rvOrderHistoryGoods;
    private boolean hasOrderPay;
    private WaitHistoryOrderInfo waitHistoryOrderInfo;
    private PayHistoryOrderInfo payHistoryOrderInfo;
    private OnConfirmOrderListener onConfirmOrderListener;

    @Override
    protected void initViews(View rootView) {
        rvOrderHistoryGoods = (RecyclerView) findViewById(R.id.rv_order_history_goods);
        CommonRecyclerAdapter commonRecyclerAdapter = new CommonRecyclerAdapter(false);
        commonRecyclerAdapter.addViewHolderFactory(new GoodsOrderHistoryDetailItemHolder.Factory());
        rvOrderHistoryGoods.setAdapter(commonRecyclerAdapter);
        List<GoodsOrderListInfo> orderListInfoList = null;
        if (payHistoryOrderInfo != null) {
            orderListInfoList = payHistoryOrderInfo.getOrderListInfoList();
        }
        if (waitHistoryOrderInfo != null) {
            orderListInfoList = waitHistoryOrderInfo.getOrderListInfoList();
        }
        if (orderListInfoList != null) {
            commonRecyclerAdapter.addDataList(orderListInfoList);
        }
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OrderHistoryDetailAlertFragment.this);
            }
        });
        ShapeTextView leftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
        leftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OrderHistoryDetailAlertFragment.this);
                if (onConfirmOrderListener != null) {
                    if (payHistoryOrderInfo != null) {
                        onConfirmOrderListener.onConfirmPayOrder(payHistoryOrderInfo);
                    } else if (waitHistoryOrderInfo != null) {
                        onConfirmOrderListener.onConfirmWaitOrder(waitHistoryOrderInfo);
                    }
                }
            }
        });
        if (hasOrderPay) {
            leftBt.setText("重新下单");
        } else {
            leftBt.setText("去支付");
        }
        findViewById(R.id.stv_right_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), OrderHistoryDetailAlertFragment.this);
            }
        });
    }

    public void setHasOrderPay(boolean hasOrderPay) {
        this.hasOrderPay = hasOrderPay;
    }

    public void setPayHistoryOrderInfo(PayHistoryOrderInfo payHistoryOrderInfo) {
        this.payHistoryOrderInfo = payHistoryOrderInfo;
    }

    public void setWaitHistoryOrderInfo(WaitHistoryOrderInfo waitHistoryOrderInfo) {
        this.waitHistoryOrderInfo = waitHistoryOrderInfo;
    }

    public void setOnConfirmOrderListener(OnConfirmOrderListener onConfirmOrderListener) {
        this.onConfirmOrderListener = onConfirmOrderListener;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_alert_order_goods_history_detail;
    }

    public interface OnConfirmOrderListener {
        void onConfirmPayOrder(PayHistoryOrderInfo payHistoryOrderInfo);

        void onConfirmWaitOrder(WaitHistoryOrderInfo waitHistoryOrderInfo);
    }
}
