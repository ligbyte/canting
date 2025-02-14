package com.stkj.supermarketmini.goods.model;

public class GoodsBatchListInfo {
    //批次号
    private String batchNo;
    //入库数量
    private String inCount;
    //入库时间
    private String inDate;
    //生产日期
    private String productDate;
    //进货单价
    private String inUnitPrice;
    //保质期
    private String expireDays;
    //剩余天数
    private String expireDay;
    //失效日期
    private String expireDate;

    public GoodsBatchListInfo() {
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getInCount() {
        return inCount;
    }

    public void setInCount(String inCount) {
        this.inCount = inCount;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getInUnitPrice() {
        return inUnitPrice;
    }

    public void setInUnitPrice(String inUnitPrice) {
        this.inUnitPrice = inUnitPrice;
    }

    public String getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(String expireDays) {
        this.expireDays = expireDays;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(String expireDay) {
        this.expireDay = expireDay;
    }
}
