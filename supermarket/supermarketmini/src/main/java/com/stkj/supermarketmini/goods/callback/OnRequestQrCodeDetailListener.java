package com.stkj.supermarketmini.goods.callback;

import com.stkj.supermarketmini.goods.model.GoodsQrCodeDetail;

public interface OnRequestQrCodeDetailListener {

    void onRequestDetailSuccess(GoodsQrCodeDetail data);

    void onRequestDetailError(String qrcode, String msg);
}
