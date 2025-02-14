package com.stkj.supermarket.goods.model;

/**
 * 保存商品入库请求参数
 */
public class GoodsSaveStorageInfo {

    //商品主键
    private String goodsId;
    //入库数量
    private String inCount;
    //进货单价
    private String inUnitPrice;
    private String productDate;
    //有效天数
    private String expireDays;

    public GoodsSaveStorageInfo() {
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getInCount() {
        return inCount;
    }

    public void setInCount(String inCount) {
        this.inCount = inCount;
    }

    public String getInUnitPrice() {
        return inUnitPrice == null ? "" : inUnitPrice;
    }

    public void setInUnitPrice(String inUnitPrice) {
        this.inUnitPrice = inUnitPrice;
    }

    public String getProductDate() {
        return productDate == null ? "" : productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getExpireDays() {
        return expireDays == null ? "" : expireDays;
    }

    public void setExpireDays(String expireDays) {
        this.expireDays = expireDays;
    }
}
