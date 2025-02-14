package com.stkj.supermarketmini.payment.model;

import com.stkj.supermarketmini.base.utils.PriceUtils;
import com.stkj.supermarketmini.goods.data.GoodsConstants;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单的商品详情
 */
public class AddOrderGoodsDetail {

    private String goodsId;
    //    private int goodsType;
    private String goodsName;
    private String goodsUnitPrice;
    private String goodsCount;
    private String totalPrice;
    private String settlePrice;
    private String payPrice;
    private int isChange;
    private String discountUnitPrice;
    private List<AddOrderGoodsDiscount> goodsDiscountList;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

//    public int getGoodsType() {
//        return goodsType;
//    }
//
//    public void setGoodsType(int goodsType) {
//        this.goodsType = goodsType;
//    }
//
//    public boolean isWeightGoods() {
//        return goodsType == GoodsConstants.TYPE_GOODS_WEIGHT;
//    }

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

    public int getIsChange() {
        return isChange;
    }

    public void setIsChange(int isChange) {
        this.isChange = isChange;
    }

    public String getDiscountUnitPrice() {
        return discountUnitPrice;
    }

    public void setDiscountUnitPrice(String discountUnitPrice) {
        this.discountUnitPrice = discountUnitPrice;
    }

    public List<AddOrderGoodsDiscount> getGoodsDiscountList() {
        return goodsDiscountList;
    }

    public void setGoodsDiscountList(List<AddOrderGoodsDiscount> goodsDiscountList) {
        this.goodsDiscountList = goodsDiscountList;
    }

    public static class AddOrderGoodsDiscount {
        private String orderId;
        private String goodsId;
        private String discountId;
        private String discountRule;
        private String beginDate;
        private String endDate;
        private int type;
        private String discountPrice;
        private String sortOrder;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getDiscountId() {
            return discountId;
        }

        public void setDiscountId(String discountId) {
            this.discountId = discountId;
        }

        public String getDiscountRule() {
            return discountRule;
        }

        public void setDiscountRule(String discountRule) {
            this.discountRule = discountRule;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDiscountPrice() {
            return discountPrice;
        }

        public void setDiscountPrice(String discountPrice) {
            this.discountPrice = discountPrice;
        }

        public String getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

    public static AddOrderGoodsDetail convertOrderGoodsInfo(GoodsOrderListInfo orderListInfo) {
        AddOrderGoodsDetail addOrderGoodsDetail = new AddOrderGoodsDetail();
        GoodsSaleListInfo saleListInfo = orderListInfo.getGoodsSaleListInfo();
        addOrderGoodsDetail.setGoodsId(saleListInfo.getId());
        addOrderGoodsDetail.setGoodsName(saleListInfo.getGoodsName());
//        orderGoodsDetail.setOrderId();
        addOrderGoodsDetail.setGoodsUnitPrice(saleListInfo.getGoodsUnitPrice());
        addOrderGoodsDetail.setGoodsCount(orderListInfo.isWeightGoods() ? orderListInfo.getWeightGoodsCount() : orderListInfo.getInputGoodsCount());
        addOrderGoodsDetail.setTotalPrice(PriceUtils.formatPrice(String.valueOf(orderListInfo.getGoodsTotalOriginPrice())));
        addOrderGoodsDetail.setSettlePrice(PriceUtils.formatPrice(String.valueOf(orderListInfo.getGoodsTotalDiscountPrice())));
        addOrderGoodsDetail.setPayPrice(orderListInfo.getInputGoodsTotalPrice());
        addOrderGoodsDetail.setIsChange(orderListInfo.isChange() ? 1 : 0);
        addOrderGoodsDetail.setDiscountUnitPrice(saleListInfo.getDiscountPrice());
        List<GoodsSaleListInfo.DiscountType> discountTypeList = saleListInfo.getDiscountTypeList();

        if (discountTypeList != null && !discountTypeList.isEmpty()) {
            List<AddOrderGoodsDiscount> addOrderGoodsDiscountList = new ArrayList<>();
            for (GoodsSaleListInfo.DiscountType discountType : discountTypeList) {
                AddOrderGoodsDiscount addOrderGoodsDiscount = new AddOrderGoodsDiscount();
//                addOrderGoodsDiscount.setOrderId();
                addOrderGoodsDiscount.setGoodsId(saleListInfo.getId());
                addOrderGoodsDiscount.setDiscountId(discountType.getId());
                addOrderGoodsDiscount.setDiscountRule(discountType.getDiscountRule());
                addOrderGoodsDiscount.setBeginDate(discountType.getBeginDate());
                addOrderGoodsDiscount.setEndDate(discountType.getEndDate());
                addOrderGoodsDiscount.setType(discountType.getType());
//                addOrderGoodsDiscount.setDiscountPrice();
//                addOrderGoodsDiscount.setSortOrder();
                addOrderGoodsDiscountList.add(addOrderGoodsDiscount);
            }
            addOrderGoodsDetail.setGoodsDiscountList(addOrderGoodsDiscountList);
        }
        return addOrderGoodsDetail;
    }
}
