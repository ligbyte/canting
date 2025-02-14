package com.stkj.supermarketmini.goods.model;

public class ErrorCheckGoodsBaseInfo {

    private GoodsBaseInfo goodsBaseInfo;
    private String errorMsg;

    public ErrorCheckGoodsBaseInfo(GoodsBaseInfo goodsBaseInfo, String errorMsg) {
        this.goodsBaseInfo = goodsBaseInfo;
        this.errorMsg = errorMsg;
    }

    public GoodsBaseInfo getGoodsBaseInfo() {
        return goodsBaseInfo;
    }

    public void setGoodsBaseInfo(GoodsBaseInfo goodsBaseInfo) {
        this.goodsBaseInfo = goodsBaseInfo;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
