package com.stkj.cashiermini.base.callback;

public interface QrCodeListener {
    void onScanResult(String result);

    default void onScanError() {

    }
}
