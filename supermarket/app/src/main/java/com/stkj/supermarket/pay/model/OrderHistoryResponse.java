package com.stkj.supermarket.pay.model;

import com.stkj.supermarket.goods.data.GoodsConstants;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryResponse {

    private List<GoodsHistory> orderInfoDetailDtos;
    private String id;
    private String orderNo;
    private String realPayPrice;
    private String goodsCount;
    private int payType;
    private int orderType;
    private String payStatus;
    private String createTime;

    public static class GoodsHistory {
        private String goodsId;
        // 商品类型（0 标准商品 1 称重商品)
        private int goodsType;
        private String goodsCode;
        private String goodsName;
        //商品图片
        private String goodsImg;
        private int goodsSpec;
        private String goodsSpecStr;
        private String goodsUnitPrice;
        private String goodsCount;
        private String totalPrice;
        private String settlePrice;
        private String payPrice;
        private String discountUnitPrice;
        private List<GoodsSaleListInfo.DiscountType> orderInfoDiscountDtos;

        public GoodsHistory() {
        }

        public List<GoodsSaleListInfo.DiscountType> getOrderInfoDiscountDtos() {
            return orderInfoDiscountDtos;
        }

        public void setOrderInfoDiscountDtos(List<GoodsSaleListInfo.DiscountType> orderInfoDiscountDtos) {
            this.orderInfoDiscountDtos = orderInfoDiscountDtos;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsCode() {
            return goodsCode;
        }

        public void setGoodsCode(String goodsCode) {
            this.goodsCode = goodsCode;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsImg() {
            return goodsImg;
        }

        public void setGoodsImg(String goodsImg) {
            this.goodsImg = goodsImg;
        }

        public int getGoodsSpec() {
            return goodsSpec;
        }

        public void setGoodsSpec(int goodsSpec) {
            this.goodsSpec = goodsSpec;
        }

        public String getGoodsSpecStr() {
            if (goodsType == GoodsConstants.TYPE_GOODS_WEIGHT) {
                return GoodsConstants.SPEC_WEIGHT_GOODS;
            }
            return goodsSpecStr;
        }

        public void setGoodsSpecStr(String goodsSpecStr) {
            this.goodsSpecStr = goodsSpecStr;
        }

        public String getGoodsUnitPrice() {
            return goodsUnitPrice;
        }

        public void setGoodsUnitPrice(String goodsUnitPrice) {
            this.goodsUnitPrice = goodsUnitPrice;
        }

        public String getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(String goodsCount) {
            this.goodsCount = goodsCount;
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

        public String getPayPrice() {
            return payPrice;
        }

        public void setPayPrice(String payPrice) {
            this.payPrice = payPrice;
        }

        public String getDiscountUnitPrice() {
            return discountUnitPrice;
        }

        public void setDiscountUnitPrice(String discountUnitPrice) {
            this.discountUnitPrice = discountUnitPrice;
        }

        public int getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(int goodsType) {
            this.goodsType = goodsType;
        }
    }

    public PayHistoryOrderInfo convertPayHistoryOrderInfo() {
        PayHistoryOrderInfo payHistoryOrderInfo = new PayHistoryOrderInfo();
        payHistoryOrderInfo.setTotalPrice(realPayPrice);
        payHistoryOrderInfo.setTotalCount(goodsCount);
        payHistoryOrderInfo.setOrderStatus(payStatus);
        payHistoryOrderInfo.setOrderType(orderType);
        payHistoryOrderInfo.setPayType(payType);
        payHistoryOrderInfo.setOrderId(id);
        payHistoryOrderInfo.setOrderNumber(orderNo);
        payHistoryOrderInfo.setFormatOrderPayTime(createTime);
        if (orderInfoDetailDtos != null && !orderInfoDetailDtos.isEmpty()) {
            List<GoodsOrderListInfo> orderListInfoList = new ArrayList<>();
            for (GoodsHistory goodsHistory : orderInfoDetailDtos) {
                GoodsSaleListInfo saleListInfo = new GoodsSaleListInfo();
                saleListInfo.setId(goodsHistory.getGoodsId());
                saleListInfo.setGoodsType(goodsHistory.getGoodsType());
                saleListInfo.setGoodsCode(goodsHistory.getGoodsCode());
                saleListInfo.setGoodsName(goodsHistory.getGoodsName());
                saleListInfo.setGoodsImg(goodsHistory.getGoodsImg());
                saleListInfo.setGoodsSpec(String.valueOf(goodsHistory.getGoodsSpec()));
                saleListInfo.setGoodsSpecStr(goodsHistory.getGoodsSpecStr());
                saleListInfo.setGoodsUnitPrice(goodsHistory.getGoodsUnitPrice());
                saleListInfo.setDiscountPrice(goodsHistory.getDiscountUnitPrice());
                saleListInfo.setDiscountTypeList(goodsHistory.getOrderInfoDiscountDtos());
                GoodsOrderListInfo orderListInfo = new GoodsOrderListInfo(saleListInfo, goodsHistory.getGoodsCount(), goodsHistory.getGoodsCount(), goodsHistory.getPayPrice());
                orderListInfoList.add(orderListInfo);
            }
            payHistoryOrderInfo.setOrderListInfoList(orderListInfoList);
        }
        return payHistoryOrderInfo;
    }
}
