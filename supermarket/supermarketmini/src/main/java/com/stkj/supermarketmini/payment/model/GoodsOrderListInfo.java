package com.stkj.supermarketmini.payment.model;

import com.stkj.common.utils.BigDecimalUtils;
import com.stkj.supermarketmini.goods.model.GoodsSaleListInfo;

/**
 * 商品订单信息
 */
public class GoodsOrderListInfo {

    //称重商品重量
    private String weightGoodsCount;
    private String inputGoodsCount;
    private String inputGoodsTotalPrice;
    private GoodsSaleListInfo goodsSaleListInfo;
    private boolean isChange;

    public GoodsOrderListInfo() {
    }

    public GoodsOrderListInfo(GoodsSaleListInfo goodsSaleListInfo, String weightGoodsCount, String inputGoodsCount, String inputGoodsTotalPrice) {
        this.goodsSaleListInfo = goodsSaleListInfo;
        this.weightGoodsCount = weightGoodsCount;
        this.inputGoodsCount = inputGoodsCount;
        this.inputGoodsTotalPrice = inputGoodsTotalPrice;
    }

    public GoodsSaleListInfo getGoodsSaleListInfo() {
        return goodsSaleListInfo;
    }

    public void setGoodsSaleListInfo(GoodsSaleListInfo goodsSaleListInfo) {
        this.goodsSaleListInfo = goodsSaleListInfo;
    }

    public String getInputGoodsCount() {
        return inputGoodsCount;
    }

    public int getInputGoodsCountWithInt() {
        int inputGoodsCount = 0;
        try {
            inputGoodsCount = (int) Double.parseDouble(this.inputGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return inputGoodsCount;
    }

    public void setInputGoodsCount(String inputGoodsCount) {
        this.inputGoodsCount = inputGoodsCount;
    }

    public String getInputGoodsTotalPrice() {
        return inputGoodsTotalPrice;
    }

    public void setInputGoodsTotalPrice(String inputGoodsTotalPrice) {
        this.inputGoodsTotalPrice = inputGoodsTotalPrice;
    }

    public double getInputGoodsTotalPriceWithDouble() {
        double inputGoodsTotalPrice = 0;
        try {
            inputGoodsTotalPrice = Double.parseDouble(this.inputGoodsTotalPrice);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return inputGoodsTotalPrice;
    }

    public double getSalePrice() {
        if (goodsSaleListInfo != null) {
            String discountPrice = goodsSaleListInfo.getDiscountPrice();
            double salePrice = 0;
            try {
                salePrice = Double.parseDouble(discountPrice);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return salePrice;
        }
        return 0;
    }

    public double getGoodsTotalOriginPrice() {
        double goodsTotalOriginPrice = 0;
        String goodsUnitPrice = goodsSaleListInfo.getGoodsUnitPrice();
        try {
            double unitPrice = Double.parseDouble(goodsUnitPrice);
            double inputGoodsCount = Double.parseDouble(isWeightGoods() ? this.weightGoodsCount : this.inputGoodsCount);
            goodsTotalOriginPrice = BigDecimalUtils.mul(unitPrice, inputGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return goodsTotalOriginPrice;
    }

    public double getGoodsTotalDiscountPrice() {
        double goodsTotalDiscountPrice = 0;
        String discountPriceStr = goodsSaleListInfo.getDiscountPrice();
        try {
            double discountPrice = Double.parseDouble(discountPriceStr);
            double inputGoodsCount = Double.parseDouble(isWeightGoods() ? this.weightGoodsCount : this.inputGoodsCount);
            goodsTotalDiscountPrice = BigDecimalUtils.mul(discountPrice, inputGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return goodsTotalDiscountPrice;
    }

    public String getWeightGoodsCount() {
        return weightGoodsCount;
    }

    public void setWeightGoodsCount(String weightGoodsCount) {
        this.weightGoodsCount = weightGoodsCount;
    }

    /**
     * 是否是称重商品
     */
    public boolean isWeightGoods() {
        return goodsSaleListInfo != null && goodsSaleListInfo.isWeightGoods();
    }

    public String getGoodsName() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getGoodsName();
        }
        return "";
    }

    public String getGoodsCode() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getGoodsCode();
        }
        return "";
    }


    public String getGoodsPic() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getGoodsImg();
        }
        return "";
    }

    public String getGoodsId() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getId();
        }
        return "";
    }

    public int getGoodsType() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getGoodsType();
        }
        return 0;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public boolean isChange() {
        return isChange;
    }
}
