package com.stkj.supermarketmini.goods.model;

public class AddGoodsResultWrapper {
    private GoodsBaseInfo goodsBaseInfo;
    private int code;
    private String msg;

    public AddGoodsResultWrapper(GoodsBaseInfo goodsBaseInfo, int code, String msg) {
        this.goodsBaseInfo = goodsBaseInfo;
        this.code = code;
        this.msg = msg;
    }

    public static AddGoodsResultWrapper newSuccess(GoodsBaseInfo goodsBaseInfo) {
        return new AddGoodsResultWrapper(goodsBaseInfo, 200, "");
    }

    public static AddGoodsResultWrapper newError(GoodsBaseInfo goodsBaseInfo, String msg) {
        return new AddGoodsResultWrapper(goodsBaseInfo, 0, msg);
    }

    public GoodsBaseInfo getGoodsBaseInfo() {
        return goodsBaseInfo;
    }

    public void setGoodsBaseInfo(GoodsBaseInfo goodsBaseInfo) {
        this.goodsBaseInfo = goodsBaseInfo;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isAddSuccess() {
        return code == 200;
    }
}
