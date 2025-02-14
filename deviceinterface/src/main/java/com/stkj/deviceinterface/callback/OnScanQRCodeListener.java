package com.stkj.deviceinterface.callback;

public interface OnScanQRCodeListener {

    void onScanQrCode(String data);

    void onScanQRCodeError(String message);
}
