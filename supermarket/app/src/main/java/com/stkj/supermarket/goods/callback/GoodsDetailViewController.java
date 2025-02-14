package com.stkj.supermarket.goods.callback;

import com.stkj.supermarket.goods.model.GoodsEditBaseInfo;
import com.stkj.supermarket.goods.model.GoodsQrCodeDetail;

public interface GoodsDetailViewController {

    GoodsEditBaseInfo getGoodsDetailEditInfo();

    void setGoodsDetailEditMode();

    void setGoodsQrCodeInfo(GoodsQrCodeDetail goodsQrCodeInfo);

}
