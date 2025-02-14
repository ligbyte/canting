package com.stkj.supermarketmini.goods.model;

/**
 * 删除商品分类请求参数
 */
public class DelGoodsCateParams {
    private String id;
    private int type;

    public DelGoodsCateParams() {
    }

    public DelGoodsCateParams(String id, int type) {
        this.id = id;
        this.type = type;
    }
}
