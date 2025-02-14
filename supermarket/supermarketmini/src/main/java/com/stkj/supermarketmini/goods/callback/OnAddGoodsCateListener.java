package com.stkj.supermarketmini.goods.callback;

import com.stkj.supermarketmini.goods.model.GoodsCate;

/**
 * 添加商品分类回调
 */
public interface OnAddGoodsCateListener {
    void onAddCateSuccess(GoodsCate goodsCate);

    default void onAddCateError(String cateName, String msg) {

    }
}
