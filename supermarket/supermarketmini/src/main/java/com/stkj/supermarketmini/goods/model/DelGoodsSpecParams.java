package com.stkj.supermarketmini.goods.model;

/**
 * 删除商品规格请求参数
 */
public class DelGoodsSpecParams {
    private String id;
    private int type;

    public DelGoodsSpecParams() {
    }

    public DelGoodsSpecParams(String id, int type) {
        this.id = id;
        this.type = type;
    }
}
