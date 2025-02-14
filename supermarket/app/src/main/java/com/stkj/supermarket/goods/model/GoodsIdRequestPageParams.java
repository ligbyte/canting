package com.stkj.supermarket.goods.model;

public class GoodsIdRequestPageParams {

    //当前页码
    private int current = 1;
    //商品主键
    private String goodsId;
    //页面数量
    private int size = 10;

    public GoodsIdRequestPageParams() {
    }

    public GoodsIdRequestPageParams(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
