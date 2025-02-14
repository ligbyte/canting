package com.stkj.supermarketmini.goods.callback;

public interface OnGoodsCateSpecListener {

    void onGetCateSpecListEnd();

    default void onGetCateSpecError(String msg) {

    }
}
