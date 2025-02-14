package com.stkj.supermarket.goods.model;

/**
 * 包含商品 id 的基础信息
 */
public class GoodsIdBaseListInfo extends GoodsBaseInfo {

    private String id;

    public GoodsIdBaseListInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
