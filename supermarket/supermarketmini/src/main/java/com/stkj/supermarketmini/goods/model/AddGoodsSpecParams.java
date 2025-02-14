package com.stkj.supermarketmini.goods.model;

/**
 * 添加商品规格请求参数
 */
public class AddGoodsSpecParams {
    private int type;
    private String name;

    public AddGoodsSpecParams() {
    }

    public AddGoodsSpecParams(String name, int goodsType) {
        this.type = goodsType;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
