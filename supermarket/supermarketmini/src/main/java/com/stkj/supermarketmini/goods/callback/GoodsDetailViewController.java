package com.stkj.supermarketmini.goods.callback;

import com.stkj.supermarketmini.goods.model.GoodsEditBaseInfo;
import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;

public interface GoodsDetailViewController {

    GoodsEditBaseInfo getGoodsDetailEditInfo();

    void setGoodsDetailEditMode();

    void setGoodsQrCodeInfo(GoodsQrCodeDetail goodsQrCodeInfo);

}
