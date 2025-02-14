package com.stkj.supermarket.pay.model;

import com.stkj.supermarket.goods.model.GoodsSaleListInfo;

public class GoodsWeightItemInfo {

    private boolean isSelect;

    private GoodsSaleListInfo saleListInfo;

    public GoodsWeightItemInfo(GoodsSaleListInfo saleListInfo) {
        this.saleListInfo = saleListInfo;
    }

    public GoodsSaleListInfo getSaleListInfo() {
        return saleListInfo;
    }

    public String getGoodsName() {
        if (saleListInfo != null) {
            return saleListInfo.getGoodsName();
        }
        return "";
    }

    public String getGoodsId() {
        if (saleListInfo != null) {
            return saleListInfo.getId();
        }
        return "";
    }

    public double getGoodsDiscountPrice() {
        if (saleListInfo == null) {
            return 0;
        }
        double discountPrice = 0;
        String discountPriceStr = saleListInfo.getDiscountPrice();
        try {
            discountPrice = Double.parseDouble(discountPriceStr);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return discountPrice;
    }

    public void setSaleListInfo(GoodsSaleListInfo saleListInfo) {
        this.saleListInfo = saleListInfo;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
