package com.stkj.supermarketmini.payment.model;

import android.text.TextUtils;

import com.stkj.supermarketmini.payment.data.PayConstants;

import java.util.List;

/**
 * 已支付订单
 */
public class PayHistoryOrderInfo {
    private String totalPrice;//总支付价格
    private String totalCount;//商品总数
    private String orderStatus;//订单状态
    private int orderType;//订单支付方式
    private int payType;//支付方式
    private String orderId;//订单id
    private String orderNumber;//订单编号
    private String formatOrderPayTime;

    private List<GoodsOrderListInfo> orderListInfoList;//商品列表

    public PayHistoryOrderInfo(String totalPrice, String totalCount, String orderStatus, int orderType, int payType, String orderId, String formatOrderPayTime, List<GoodsOrderListInfo> orderListInfoList) {
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.payType = payType;
        this.orderId = orderId;
        this.formatOrderPayTime = formatOrderPayTime;
        this.orderListInfoList = orderListInfoList;
    }

    public PayHistoryOrderInfo() {
    }

    public String getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getGoodsListName() {
        if (orderType == PayConstants.ORDER_TYPE_FAST_PAY) {
            return "快速收银";
        }
        if (orderListInfoList == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int size = orderListInfoList.size();
        for (int i = 0; i < size; i++) {
            GoodsOrderListInfo orderListInfo = orderListInfoList.get(i);
            builder.append(orderListInfo.getGoodsName())
                    .append(" x")
                    .append(orderListInfo.isWeightGoods() ? orderListInfo.getWeightGoodsCount() + "kg" : orderListInfo.getInputGoodsCountWithInt())
                    .append(";");
        }
        return builder.toString();
    }

    public List<GoodsOrderListInfo> getOrderListInfoList() {
        return this.orderListInfoList;
    }

    public void setOrderListInfoList(List<GoodsOrderListInfo> orderListInfoList) {
        this.orderListInfoList = orderListInfoList;
    }

    public int getPayType() {
        return this.payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderGoodsPic1() {
        if (orderListInfoList != null && orderListInfoList.size() > 0) {
            return orderListInfoList.get(0).getGoodsPic();
        }
        return "";
    }

    public String getOrderGoodsPic2() {
        if (orderListInfoList != null && orderListInfoList.size() > 1) {
            return orderListInfoList.get(1).getGoodsPic();
        }
        return "";
    }

    public int getOrderType() {
        return this.orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getFormatOrderPayTime() {
        return this.formatOrderPayTime;
    }

    public void setFormatOrderPayTime(String formatOrderPayTime) {
        this.formatOrderPayTime = formatOrderPayTime;
    }

    public boolean isPaySuccessOrder() {
        return TextUtils.equals(PayConstants.ORDER_SUCCESS_STATUS, orderStatus);
    }

}
