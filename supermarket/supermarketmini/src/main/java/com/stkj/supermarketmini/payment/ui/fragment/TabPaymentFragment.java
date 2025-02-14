package com.stkj.supermarketmini.payment.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.stkj.common.log.LogHelper;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.widget.linelayout.LineFrameLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.common.utils.FragmentUtils;
import com.stkj.common.utils.SpanUtils;
import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.callback.QrCodeListener;
import com.stkj.supermarketmini.base.ui.fragment.CommonAlertFragment;
import com.stkj.supermarketmini.base.ui.fragment.QrCodeAlertFragment;
import com.stkj.supermarketmini.base.utils.CommonDialogUtils;
import com.stkj.supermarketmini.base.utils.PriceUtils;
import com.stkj.supermarketmini.goods.callback.GoodsAutoSearchListener;
import com.stkj.supermarketmini.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.goods.ui.weight.GoodsAutoSearchLayout;
import com.stkj.supermarketmini.payment.callback.OnPayListener;
import com.stkj.supermarketmini.payment.data.PayConstants;
import com.stkj.supermarketmini.payment.helper.PayHelper;
import com.stkj.supermarketmini.payment.model.AddOrderRequest;
import com.stkj.supermarketmini.payment.model.AddOrderResult;
import com.stkj.supermarketmini.payment.model.ConsumeOrderRequest;
import com.stkj.supermarketmini.payment.model.GoodsOrderListInfo;
import com.stkj.supermarketmini.payment.model.PayHistoryOrderInfo;
import com.stkj.supermarketmini.payment.model.WaitHistoryOrderInfo;
import com.stkj.supermarketmini.payment.ui.adapter.GoodsOrderListInfoViewHolder;
import com.stkj.supermarketmini.payment.ui.dialog.SelectPayTypeDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

/**
 * 收银管理页
 */
public class TabPaymentFragment extends BaseRecyclerFragment implements OnPayListener, OrderHistoryDetailAlertFragment.OnConfirmOrderListener {

    private RecyclerView rvGoodsOrderList;
    private GoodsAutoSearchLayout goodsAutoSearch;
    //订单商品列表
    private CommonRecyclerAdapter mGoodsOrderList;
    private LineFrameLayout flGoPay;
    private ShapeTextView stvGoToPay;
    private TextView orderAllPrice;
    private LinearLayout llOrderListEmpty;
    private ShapeTextView stvScan;
    //请求参数
    private AddOrderRequest mAddOrderRequest = new AddOrderRequest();

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_payment;
    }

    @Override
    protected void initViews(View rootView) {
        llOrderListEmpty = (LinearLayout) findViewById(R.id.ll_order_list_empty);
        stvScan = (ShapeTextView) findViewById(R.id.stv_scan);
        stvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScanGoodsCode();
            }
        });
        findViewById(R.id.iv_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScanGoodsCode();
            }
        });
        findViewById(R.id.iv_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderHistoryListAlertFragment historyListAlertFragment = new OrderHistoryListAlertFragment();
                historyListAlertFragment.setOnConfirmOrderListener(TabPaymentFragment.this);
                mActivity.addContentPlaceHolderFragment(historyListAlertFragment);
            }
        });
        rvGoodsOrderList = (RecyclerView) findViewById(R.id.rv_content);
        goodsAutoSearch = (GoodsAutoSearchLayout) findViewById(R.id.gsl_layout);
        flGoPay = (LineFrameLayout) findViewById(R.id.fl_go_pay);
        stvGoToPay = (ShapeTextView) findViewById(R.id.stv_go_to_pay);
        orderAllPrice = (TextView) findViewById(R.id.order_all_price);
        mGoodsOrderList = new CommonRecyclerAdapter(false);
        mGoodsOrderList.addViewHolderFactory(new GoodsOrderListInfoViewHolder.Factory());
        mGoodsOrderList.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onCustomItemEvent(CommonRecyclerAdapter commonRecyclerAdapter, CommonRecyclerViewHolder viewHolder, int eventId, Object obj) {
                if (eventId == GoodsOrderListInfoViewHolder.EVENT_CLICK) {

                } else if (eventId == GoodsOrderListInfoViewHolder.EVENT_DELETE) {
                    mGoodsOrderList.removeData(obj);
                    refreshOrderListAndPrice();
                } else if (eventId == GoodsOrderListInfoViewHolder.EVENT_REFRESH_PRICE) {
                    refreshOrderListAndPrice();
                }
            }
        });
        rvGoodsOrderList.setAdapter(mGoodsOrderList);
        goodsAutoSearch.setGoodsAutoSearchListener(this, new GoodsAutoSearchListener() {

            @Override
            public void onStartGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo) {
                showLoadingDialog();
            }

            @Override
            public void onSuccessGetGoodsItemDetail(GoodsSaleListInfo saleListInfo) {
                hideLoadingDialog();
                //正在支付中，不添加商品
                PayHelper payHelper = getPayHelper();
                if (payHelper.isPaying()) {
                    return;
                }
                //称重商品跳转到称重页面
                if (saleListInfo.isWeightGoods()) {
                    CommonDialogUtils.showTipsDialog(mActivity, "暂不支持称重商品结算");
                    return;
                }
                llOrderListEmpty.setVisibility(View.GONE);
                //判断当前是否包含,包含数量+1(非称重商品)
                List<Object> dataList = mGoodsOrderList.getDataList();
                int size = dataList.size();
                for (int i = 0; i < size; i++) {
                    GoodsOrderListInfo orderListInfo = (GoodsOrderListInfo) dataList.get(i);
                    if (!orderListInfo.isWeightGoods()) {
                        if (TextUtils.equals(saleListInfo.getId(), orderListInfo.getGoodsId())) {
                            //相同商品价格和数量更新
                            int lastGoodsCount = orderListInfo.getInputGoodsCountWithInt();
                            orderListInfo.setInputGoodsCount(String.valueOf(lastGoodsCount + 1));
                            double totalPrice = BigDecimalUtils.mul(orderListInfo.getSalePrice(), (lastGoodsCount + 1));
                            orderListInfo.setInputGoodsTotalPrice(String.valueOf(totalPrice));
                            mGoodsOrderList.notifyItemChanged(i);
                            refreshOrderListAndPrice();
                            return;
                        }
                    }
                }
                //不包含当前商品时，直接添加到订单列表
                mGoodsOrderList.addFirstData(new GoodsOrderListInfo(saleListInfo, "1.0", "1", saleListInfo.getDiscountPrice()));
                //刷新价格
                refreshOrderListAndPrice();
                //滚动列表到头部
                rvGoodsOrderList.scrollToPosition(0);
            }

            @Override
            public void onErrorGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo, String msg) {
                hideLoadingDialog();
                CommonDialogUtils.showTipsDialog(mActivity, msg);
            }

            @Override
            public void onSearchGoodsList(String key, List<GoodsIdBaseListInfo> goodsIdBaseListInfoList) {
            }
        });
        RxView.clicks(stvGoToPay).throttleFirst(1000, TimeUnit.MILLISECONDS).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Unit>() {
            @Override
            protected void onSuccess(Unit unit) {
                if (mGoodsOrderList.isEmptyData()) {
                    CommonDialogUtils.showTipsDialog(mActivity, "支付列表为空");
                    return;
                }
                double parsePrice = PriceUtils.parsePrice(mAddOrderRequest.getRealPayPrice());
                if (parsePrice <= 0) {
                    CommonDialogUtils.showTipsDialog(mActivity, "支付金额为0");
                    return;
                }
                //添加商品列表订单信息
                List<Object> dataList = mGoodsOrderList.getDataList();
                List<GoodsOrderListInfo> orderListInfoList = new ArrayList<>();
                for (Object obj : dataList) {
                    orderListInfoList.add((GoodsOrderListInfo) obj);
                }
                mAddOrderRequest.addOrderGoodsInfoList(orderListInfoList);
                goToPay();
            }
        });
    }

    /**
     * 刷新订单列表底部价格
     */
    private void refreshOrderListAndPrice() {
        List<Object> dataList = mGoodsOrderList.getDataList();
        if (dataList.isEmpty()) {
            llOrderListEmpty.setVisibility(View.VISIBLE);
            flGoPay.setVisibility(View.GONE);
            orderAllPrice.setText("");
            setOrderRealPayPrice("0.00");
            return;
        }
        llOrderListEmpty.setVisibility(View.GONE);
        flGoPay.setVisibility(View.VISIBLE);
        double totalOriginPrice = 0;
        double totalDiscountPrice = 0;
        double totalPrice = 0;
        int totalCount = 0;
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            GoodsOrderListInfo goodsOrderListInfo = (GoodsOrderListInfo) obj;
            //最终价格
            double goodsTotalPriceWithDouble = goodsOrderListInfo.getInputGoodsTotalPriceWithDouble();
            totalPrice = BigDecimalUtils.add(totalPrice, goodsTotalPriceWithDouble);
            //原始价格
            totalOriginPrice = BigDecimalUtils.add(totalOriginPrice, goodsOrderListInfo.getGoodsTotalOriginPrice());
            //折扣价格
            totalDiscountPrice = BigDecimalUtils.add(totalDiscountPrice, goodsOrderListInfo.getGoodsTotalDiscountPrice());
            //商品数量
            GoodsSaleListInfo goodsSaleListInfo = goodsOrderListInfo.getGoodsSaleListInfo();
            if (goodsSaleListInfo.isWeightGoods()) {
                //称重商品表示一件商品
                totalCount = totalCount + 1;
            } else {
                totalCount = totalCount + goodsOrderListInfo.getInputGoodsCountWithInt();
            }
        }
        String formatTotalPrice = PriceUtils.formatPrice(String.valueOf(totalPrice));
        String formatDiscountPrice = PriceUtils.formatPrice(String.valueOf(totalDiscountPrice));
        String formatTotalOriginPrice = PriceUtils.formatPrice(String.valueOf(totalOriginPrice));
        String formatTotalDiscountPrice = "0.00";
        if (totalOriginPrice > totalPrice) {
            double tempPrice = BigDecimalUtils.sub(totalOriginPrice, totalPrice);
            formatTotalDiscountPrice = PriceUtils.formatPrice(String.valueOf(tempPrice));
        }
        LogHelper.print("--refreshOrderListAndPrice--tabPay: totalPrice:" + formatTotalPrice + " totalOriginPrice: " + formatTotalOriginPrice + " totalDiscountPrice: " + totalDiscountPrice);
        int redColor = mResources.getColor(R.color.color_FF3C30);
        int blueColor = mResources.getColor(R.color.color_0087FA);
        int grayColor = mResources.getColor(R.color.color_999999);
        int priceFontSize = mResources.getDimensionPixelSize(com.stkj.common.R.dimen.sp_19);
        SpanUtils.with(orderAllPrice).append("¥ ").setForegroundColor(redColor).append(formatTotalPrice).setFontSize(priceFontSize).setForegroundColor(redColor).setBold().append("  共计" + totalCount + "件\n").setForegroundColor(blueColor).append("(总价：¥" + formatTotalOriginPrice + ",  活动优惠：-¥" + formatTotalDiscountPrice + ")").setForegroundColor(grayColor).create();
        //设置原始价格
        mAddOrderRequest.setTotalPrice(formatTotalPrice);
        //设置折扣价格
        mAddOrderRequest.setSettlePrice(formatDiscountPrice);
        //设置活动优惠
        mAddOrderRequest.setDiscountPrice(formatTotalDiscountPrice);
        //设置商品数据
        mAddOrderRequest.setGoodsCount(totalCount);
        //右侧显示的价格
        setOrderRealPayPrice(formatTotalPrice);
    }

    private void setOrderRealPayPrice(String formatTotalPrice) {
        mAddOrderRequest.setRealPayPrice(formatTotalPrice);
    }

    /**
     * 显示商品搜索
     */
    private void showScanGoodsCode() {
        QrCodeAlertFragment qrCodeAlertFragment = new QrCodeAlertFragment();
        qrCodeAlertFragment.setQrCodeListener(new QrCodeListener() {
            @Override
            public void onScanResult(String result) {
                qrCodeAlertFragment.dismiss();
                goodsAutoSearch.autoSearch(result, true);
            }
        });
        qrCodeAlertFragment.show(mActivity);
    }

    /**
     * 清理付款订单信息
     */
    private void clearPayOrderInfo() {
        //清空订单商品列表和金额
        mGoodsOrderList.removeAllData();
        refreshOrderListAndPrice();
        //清除请求参数
        mAddOrderRequest.resetRequest();
    }

    private PayHelper getPayHelper() {
        return mActivity.getWeakRefHolder(PayHelper.class);
    }

    private void goToPay() {
        SelectPayTypeDialog selectPayTypeDialog = new SelectPayTypeDialog();
        selectPayTypeDialog.setLeftNavClickListener(new CommonAlertFragment.OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), selectPayTypeDialog);
                int payType = selectPayTypeDialog.getPayType();
                showScanOrderPay(payType);
            }
        });
        selectPayTypeDialog.setRightNavClickListener(new CommonAlertFragment.OnSweetClickListener() {
            @Override
            public void onClick() {
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), selectPayTypeDialog);
            }
        });
        mActivity.addContentPlaceHolderFragment(selectPayTypeDialog);
    }

    private void showScanOrderPay(int payType) {
        mAddOrderRequest.setPayType(payType);
        if (payType == PayConstants.PAY_TYPE_THIRD) {
            //聚合支付
            QrCodeAlertFragment qrCodeAlertFragment = new QrCodeAlertFragment();
            qrCodeAlertFragment.setQrCodeListener(new QrCodeListener() {
                @Override
                public void onScanResult(String result) {
                    qrCodeAlertFragment.dismiss();
                    PayHelper payHelper = getPayHelper();
                    payHelper.setOnPayListener(TabPaymentFragment.this);
                    payHelper.pay(mAddOrderRequest, result);
                }
            });
            qrCodeAlertFragment.show(mActivity);
        } else if (payType == PayConstants.PAY_TYPE_CASH) {
            //现金
            PayHelper payHelper = getPayHelper();
            payHelper.setOnPayListener(TabPaymentFragment.this);
            payHelper.pay(mAddOrderRequest, "");
        } else if (payType == PayConstants.PAY_TYPE_QRCODE) {
            //职工码
            QrCodeAlertFragment qrCodeAlertFragment = new QrCodeAlertFragment();
            qrCodeAlertFragment.setQrCodeListener(new QrCodeListener() {
                @Override
                public void onScanResult(String result) {
                    qrCodeAlertFragment.dismiss();
                    mAddOrderRequest.setCustomerNo(result);
                    PayHelper payHelper = getPayHelper();
                    payHelper.setOnPayListener(TabPaymentFragment.this);
                    payHelper.pay(mAddOrderRequest, "");
                }
            });
            qrCodeAlertFragment.show(mActivity);
        }
    }

    @Override
    public void onConfirmPayOrder(PayHistoryOrderInfo payHistoryOrderInfo) {
        addConfirmOrderGoodList(payHistoryOrderInfo.getOrderListInfoList());
    }

    @Override
    public void onConfirmWaitOrder(WaitHistoryOrderInfo waitHistoryOrderInfo) {
        //删除本地数据库该订单 todo
//        getHistoryOrderHelper().deleteWaitOrderHistory(waitHistoryOrderInfo);
//        mWaitHistoryOrderList.removeData(waitHistoryOrderInfo);
        addConfirmOrderGoodList(waitHistoryOrderInfo.getOrderListInfoList());
    }

    /**
     * 重新支付或者下单（历史订单、历史挂单）
     */
    private void addConfirmOrderGoodList(List<GoodsOrderListInfo> historyOrderInfo) {
        clearPayOrderInfo();
        mGoodsOrderList.addDataList(historyOrderInfo);
        rvGoodsOrderList.scrollToPosition(0);
        refreshOrderListAndPrice();
    }

    @Override
    public void onStartPay(AddOrderRequest addOrderRequest) {
        showLoadingDialog("支付中,请稍等");
    }

    @Override
    public void onPaySuccess(AddOrderRequest addOrderRequest, @Nullable AddOrderResult addOrderResult, @Nullable ConsumeOrderRequest consumeOrderRequest) {
        String orderNumber = "";
        if (addOrderResult != null) {
            orderNumber = addOrderResult.getId();
        }
        hideLoadingDialog();
        String formatPrice2 = PriceUtils.formatPrice2(addOrderRequest.getRealPayPrice());
        CommonDialogUtils.showTipsDialog(mActivity, "支付成功" + formatPrice2 + "元\n订单编号: " + orderNumber);
        clearPayOrderInfo();
    }

    @Override
    public void onPayError(String responseCode, AddOrderRequest addOrderRequest, @Nullable AddOrderResult addOrderResult, String msg) {
        hideLoadingDialog();
        CommonDialogUtils.showTipsDialog(mActivity, "支付失败:" + msg);
    }

}
