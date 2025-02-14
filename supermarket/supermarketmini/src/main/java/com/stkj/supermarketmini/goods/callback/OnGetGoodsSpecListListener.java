package com.stkj.supermarketmini.goods.callback;

import com.stkj.supermarketmini.goods.model.GoodsSpec;

import java.util.List;

public interface OnGetGoodsSpecListListener {
    void onGetSpecListSuccess(int goodsType, List<GoodsSpec> goodsSpecList);

    default void onGetSpecListError(int goodsType, String msg) {

    }
}
