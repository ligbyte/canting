package com.stkj.supermarket.goods.callback;

public interface OnGoodsCateSpecListener {

    void onGetCateSpecListEnd();

    default void onGetCateSpecError(String msg) {

    }
}
