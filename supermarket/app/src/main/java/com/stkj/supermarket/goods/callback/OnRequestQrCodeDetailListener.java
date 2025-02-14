package com.stkj.supermarket.goods.callback;

import com.stkj.supermarket.goods.model.GoodsQrCodeDetail;

public interface OnRequestQrCodeDetailListener {

    void onRequestDetailSuccess(GoodsQrCodeDetail data);

    void onRequestDetailError(String qrcode, String msg);
}
