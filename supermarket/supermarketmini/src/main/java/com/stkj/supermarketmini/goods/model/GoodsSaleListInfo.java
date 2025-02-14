package com.stkj.supermarketmini.goods.model;

import com.stkj.supermarketmini.goods.data.GoodsConstants;

import java.util.List;

/**
 * 商品库存列表
 */
public class GoodsSaleListInfo extends GoodsBaseInfo {

    private String id;
    private String goodsRealStock;
    private String inAvgPrice;
    private String discountPrice;
    private List<DiscountType> discountTypeList;
    private String saleMonthPrice;
    private String saleDayPrice;
    private String saleDayCount;
    private String saleMonthCount;
    private String saleSumCount;
    private String saleSumPrice;
    private String expireDay;
    private String goodsFirstChar;

    public GoodsSaleListInfo() {
    }

    public String getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(String expireDay) {
        this.expireDay = expireDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoodsRealStock() {
        double realStock = 0;
        try {
            realStock = Double.parseDouble(goodsRealStock);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (getGoodsType() == GoodsConstants.TYPE_GOODS_WEIGHT) {
            return String.valueOf(realStock);
        }
        return String.valueOf((int) realStock);
    }

    public void setGoodsRealStock(String goodsRealStock) {
        this.goodsRealStock = goodsRealStock;
    }

    public String getInAvgPrice() {
        double realInAvgPrice = 0;
        try {
            realInAvgPrice = Double.parseDouble(inAvgPrice);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return String.valueOf(realInAvgPrice);
    }

    public void setInAvgPrice(String inAvgPrice) {
        this.inAvgPrice = inAvgPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public List<DiscountType> getDiscountTypeList() {
        return discountTypeList;
    }

    public void setDiscountTypeList(List<DiscountType> discountTypeList) {
        this.discountTypeList = discountTypeList;
    }

    public String getSaleMonthPrice() {
        return saleMonthPrice;
    }

    public void setSaleMonthPrice(String saleMonthPrice) {
        this.saleMonthPrice = saleMonthPrice;
    }

    public String getSaleDayPrice() {
        return saleDayPrice;
    }

    public void setSaleDayPrice(String saleDayPrice) {
        this.saleDayPrice = saleDayPrice;
    }

    public String getSaleDayCount() {
        return saleDayCount;
    }

    public void setSaleDayCount(String saleDayCount) {
        this.saleDayCount = saleDayCount;
    }

    public String getSaleMonthCount() {
        return saleMonthCount;
    }

    public void setSaleMonthCount(String saleMonthCount) {
        this.saleMonthCount = saleMonthCount;
    }

    public String getSaleSumCount() {
        return saleSumCount;
    }

    public void setSaleSumCount(String saleSumCount) {
        this.saleSumCount = saleSumCount;
    }

    public String getSaleSumPrice() {
        return saleSumPrice;
    }

    public void setSaleSumPrice(String saleSumPrice) {
        this.saleSumPrice = saleSumPrice;
    }

    public static class DiscountType {
        private int type;
        private String discountRule;
        private String beginDate;
        private String endDate;
        private String id;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public String getGoodsFirstChar() {
        return goodsFirstChar;
    }

    public void setGoodsFirstChar(String goodsFirstChar) {
        this.goodsFirstChar = goodsFirstChar;
    }
}
