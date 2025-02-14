package com.stkj.supermarket.goods.model;

/**
 * 添加商品分类请求参数
 */
public class AddGoodsCateParams {
    private int type;
    private String name;

    public AddGoodsCateParams() {
    }

    public AddGoodsCateParams(String name, int goodsType) {
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
