package com.stkj.supermarket.goods.callback;

public interface OnGoodsFilterListener {
    void onFilter(String goodsType, String discountType, String minPrice, String maxPrice, String minStock, String maxStock, String expireMin, String expireMax, boolean isExpire);
}
