package com.stkj.supermarketmini.payment.model;

import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;
import com.stkj.supermarketmini.login.helper.LoginHelper;
import com.stkj.supermarketmini.payment.data.PayConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加订单
 */
public class AddOrderRequest {

    private String deviceNo = "";//	设备编号		false
    private String totalPrice = "";//	总价		false
    private String settlePrice = "";//	结算价格		false
    private String discountPrice = "";//	活动优惠		false
    private String realPayPrice = "";//	实付金额		false
    private String changePriceType = "";//	调价类型（整单改价、抹角、抹分）		false
    private String changePriceDesc = "";//	调价说明		false
    private int payType;//	支付方式		false
    private int isReceipt;//	是否打印小票		false
    private int isFullReduction;//	是否满减		false
    private int isFactor;//	是否折扣		false
    private int isCut;//	是否直降		false
    private int goodsCount = 1;//	商品总量		false
    private String customerNo = "";//	职工编号		false
    private int orderType = PayConstants.ORDER_TYPE_NORMAL_PAY;//	订单类型（0 快速收银 1 非快速收银）
    private List<AddOrderGoodsDetail> orderDetailList;//商品列表

    public AddOrderRequest() {
        deviceNo = LoginHelper.INSTANCE.getMachineNumber();
    }

    public String getDeviceNo() {
        return LoginHelper.INSTANCE.getMachineNumber();
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(String settlePrice) {
        this.settlePrice = settlePrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getRealPayPrice() {
        return realPayPrice;
    }

    public void setRealPayPrice(String realPayPrice) {
        this.realPayPrice = realPayPrice;
    }

    public String getChangePriceType() {
        return changePriceType;
    }

    public void setChangePriceType(String changePriceType) {
        this.changePriceType = changePriceType;
    }

    public String getChangePriceDesc() {
        return changePriceDesc;
    }

    public void setChangePriceDesc(String changePriceDesc) {
        this.changePriceDesc = changePriceDesc;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(int isReceipt) {
        this.isReceipt = isReceipt;
    }

    public int getIsFullReduction() {
        return isFullReduction;
    }

    public void setIsFullReduction(int isFullReduction) {
        this.isFullReduction = isFullReduction;
    }

    public int getIsFactor() {
        return isFactor;
    }

    public void setIsFactor(int isFactor) {
        this.isFactor = isFactor;
    }

    public int getIsCut() {
        return isCut;
    }

    public void setIsCut(int isCut) {
        this.isCut = isCut;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public List<AddOrderGoodsDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<AddOrderGoodsDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public void addOrderGoodsInfo(GoodsOrderListInfo orderListInfo) {
        if (orderDetailList != null && orderListInfo != null) {
            //查询是否包含满减、折扣、直降
            GoodsSaleListInfo goodsSaleListInfo = orderListInfo.getGoodsSaleListInfo();
            if (goodsSaleListInfo != null) {
                List<GoodsSaleListInfo.DiscountType> discountTypeList = goodsSaleListInfo.getDiscountTypeList();
                if (discountTypeList != null && !discountTypeList.isEmpty()) {
                    for (GoodsSaleListInfo.DiscountType discountType : discountTypeList) {
                        int type = discountType.getType();
                        if (type == GoodsConstants.TYPE_DISCOUNT_TAG_ZHIJIANG) {
                            isCut = 1;//	是否直降
                        } else if (type == GoodsConstants.TYPE_DISCOUNT_TAG_ZHEKOU) {
                            isFactor = 1;//	是否折扣
                        } else if (type == GoodsConstants.TYPE_DISCOUNT_TAG_MANJIAN) {
                            isFullReduction = 1;//	是否满减
                        }
                    }
                }
            }
            //转换为请求订单商品
            orderDetailList.add(AddOrderGoodsDetail.convertOrderGoodsInfo(orderListInfo));
        }
    }

    /**
     * 转换支付列表商品到订单列表
     */
    public void addOrderGoodsInfoList(List<GoodsOrderListInfo> orderListInfoList) {
        if (orderListInfoList != null && !orderListInfoList.isEmpty()) {
            if (orderDetailList != null) {
                orderDetailList.clear();
            } else {
                orderDetailList = new ArrayList<>();
            }
            int size = orderListInfoList.size();
            for (int i = 0; i < size; i++) {
                addOrderGoodsInfo(orderListInfoList.get(i));
            }
        }
    }

    public void resetRequest() {
        deviceNo = LoginHelper.INSTANCE.getMachineNumber();
        totalPrice = "";//	总价		false
        settlePrice = "";//	结算价格		false
        discountPrice = "";//	活动优惠		false
        realPayPrice = "";//	实付金额		false
        changePriceType = "";//	调价类型（整单改价、抹角、抹分）		false
        changePriceDesc = "";//	调价说明		false
        payType = 0;//	支付方式		false
        isReceipt = 0;//	是否打印小票		false
        isFullReduction = 0;//	是否满减		false
        isFactor = 0;//	是否折扣		false
        isCut = 0;//	是否直降		false
        goodsCount = 1;//	商品总量		false
        customerNo = "";//	职工编号		false
        orderType = PayConstants.ORDER_TYPE_NORMAL_PAY;//	订单类型（0 快速收银 1 非快速收银）
        orderDetailList = null;//商品列表
    }
}
