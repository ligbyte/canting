package com.stkj.supermarketmini.base.callback;

public interface QrCodeListener {
    void onScanResult(String result);

    default void onScanError() {

    }
}
