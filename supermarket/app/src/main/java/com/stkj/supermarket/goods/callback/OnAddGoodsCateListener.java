package com.stkj.supermarket.goods.callback;

import com.stkj.supermarket.goods.model.GoodsCate;

/**
 * 添加商品分类回调
 */
public interface OnAddGoodsCateListener {
    void onAddCateSuccess(GoodsCate goodsCate);

    default void onAddCateError(String cateName, String msg) {

    }
}
