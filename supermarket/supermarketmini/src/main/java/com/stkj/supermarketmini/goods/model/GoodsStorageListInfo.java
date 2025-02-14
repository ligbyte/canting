package com.stkj.supermarketmini.goods.model;

import com.stkj.common.utils.BigDecimalUtils;

/**
 * 入库商品
 */
public class GoodsStorageListInfo {

    private String inputGoodsProductDate;
    private String inputGoodsProductExpireDays;
    private String setGoodsProductExpireDate;
    private String setGoodsProductExpireTag = "天";
    //输入的新的进货价
    private String inputGoodsInitPrice;
    //输入标准商品进货数
    private String standardGoodsCount;
    //输入称重商品进货数
    private String weightGoodsCount;
    private GoodsSaleListInfo goodsSaleListInfo;

    public GoodsStorageListInfo(GoodsSaleListInfo goodsSaleListInfo, String inputGoodsInitPrice, String standardGoodsCount, String weightGoodsCount) {
        this.goodsSaleListInfo = goodsSaleListInfo;
        this.inputGoodsInitPrice = inputGoodsInitPrice;
        this.standardGoodsCount = standardGoodsCount;
        this.weightGoodsCount = weightGoodsCount;
    }

    public String getInputGoodsProductDate() {
        return inputGoodsProductDate == null ? "" : inputGoodsProductDate;
    }

    public void setInputGoodsProductDate(String inputGoodsProductDate) {
        this.inputGoodsProductDate = inputGoodsProductDate;
    }

    public String getInputGoodsProductExpireDays() {
        return inputGoodsProductExpireDays == null ? "" : inputGoodsProductExpireDays;
    }

    public void setInputGoodsProductExpireDays(String inputGoodsProductExpireDays) {
        this.inputGoodsProductExpireDays = inputGoodsProductExpireDays;
    }

    public String getSetGoodsProductExpireDate() {
        return setGoodsProductExpireDate;
    }

    public void setSetGoodsProductExpireDate(String setGoodsProductExpireDate) {
        this.setGoodsProductExpireDate = setGoodsProductExpireDate;
    }

    public String getSetGoodsProductExpireTag() {
        return setGoodsProductExpireTag;
    }

    public void setSetGoodsProductExpireTag(String setGoodsProductExpireTag) {
        this.setGoodsProductExpireTag = setGoodsProductExpireTag;
    }

    public String getInputGoodsInitPrice() {
        return inputGoodsInitPrice == null ? "" : inputGoodsInitPrice;
    }

    public double getInputGoodsInitPriceWithDouble() {
        double inputGoodsInitPrice = 0;
        try {
            inputGoodsInitPrice = Double.parseDouble(this.inputGoodsInitPrice);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return inputGoodsInitPrice;
    }

    public void setInputGoodsInitPrice(String inputGoodsInitPrice) {
        this.inputGoodsInitPrice = inputGoodsInitPrice;
    }

    public String getStandardGoodsCount() {
        return standardGoodsCount;
    }

    public double getWeightGoodsCountWithDouble() {
        double weightGoodsCount = 0;
        try {
            weightGoodsCount = Double.parseDouble(this.weightGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return weightGoodsCount;
    }

    public int getStandardGoodsCountWithInt() {
        int standardGoodsCount = 0;
        try {
            standardGoodsCount = Integer.parseInt(this.standardGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return standardGoodsCount;
    }

    public void setStandardGoodsCount(String standardGoodsCount) {
        this.standardGoodsCount = standardGoodsCount;
    }

    public String getWeightGoodsCount() {
        return weightGoodsCount;
    }

    public void setWeightGoodsCount(String weightGoodsCount) {
        this.weightGoodsCount = weightGoodsCount;
    }

    public GoodsSaleListInfo getGoodsSaleListInfo() {
        return goodsSaleListInfo;
    }

    public void setGoodsSaleListInfo(GoodsSaleListInfo goodsSaleListInfo) {
        this.goodsSaleListInfo = goodsSaleListInfo;
    }

    public String getGoodsId() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getId();
        }
        return "";
    }

    public String getGoodsName() {
        if (goodsSaleListInfo != null) {
            return goodsSaleListInfo.getGoodsName();
        }
        return "";
    }

    /**
     * 是否入库数量大于0
     */
    public boolean hasGoodsCount() {
        if (goodsSaleListInfo != null) {
            boolean weightGoods = goodsSaleListInfo.isWeightGoods();
            if (weightGoods) {
                return getWeightGoodsCountWithDouble() > 0;
            } else {
                return getStandardGoodsCountWithInt() > 0;
            }
        }
        return false;
    }

    /**
     * 是否输入入库价格大于0
     */
    public boolean hasGoodsInitPrice() {
        if (goodsSaleListInfo != null) {
            return getInputGoodsInitPriceWithDouble() > 0;
        }
        return false;
    }

    /**
     * 是否是称重商品
     */
    public boolean isWeightGoods() {
        return goodsSaleListInfo != null && goodsSaleListInfo.isWeightGoods();
    }

    /**
     * 增加已入库的实时库存
     */
    public void refreshGoodsRealStock() {
        if (goodsSaleListInfo == null) {
            return;
        }
        String oldGoodsRealStock = goodsSaleListInfo.getGoodsRealStock();
        try {
            String newRealStock = "";
            if (isWeightGoods()) {
                double parseDouble = Double.parseDouble(oldGoodsRealStock);
                parseDouble = BigDecimalUtils.add(parseDouble, getWeightGoodsCountWithDouble());
                newRealStock = String.valueOf(parseDouble);
            } else {
                int parseInt = Integer.parseInt(oldGoodsRealStock);
                parseInt = parseInt + getStandardGoodsCountWithInt();
                newRealStock = String.valueOf(parseInt);
            }
            goodsSaleListInfo.setGoodsRealStock(newRealStock);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
