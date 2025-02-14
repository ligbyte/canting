package com.stkj.supermarketmini.goods.callback;

public interface OnScanQRCodeListener {

    void onScanQrCode(String data);

    void onScanQRCodeError(String message);
}
