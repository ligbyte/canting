package com.stkj.supermarket.goods.callback;

/**
 * 删除商品规格回调
 */
public interface OnDelGoodsSpecListener {
    void onDelSpecSuccess(String specName);

    default void onDelSpecError(String specName, String msg) {

    }
}
