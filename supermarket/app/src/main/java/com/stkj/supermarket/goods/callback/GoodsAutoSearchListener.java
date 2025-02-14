package com.stkj.supermarket.goods.callback;

import com.stkj.supermarket.goods.model.GoodsIdBaseListInfo;
import com.stkj.supermarket.goods.model.GoodsSaleListInfo;

import java.util.List;

public interface GoodsAutoSearchListener {
    void onStartGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo);

    void onSuccessGetGoodsItemDetail(GoodsSaleListInfo saleListInfo);

    void onErrorGetGoodsItemDetail(GoodsIdBaseListInfo goodsIdBaseListInfo, String msg);

    void onSearchGoodsList(String key, List<GoodsIdBaseListInfo> goodsIdBaseListInfoList);
}
