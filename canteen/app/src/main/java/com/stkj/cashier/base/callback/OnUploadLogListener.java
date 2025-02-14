package com.stkj.cashier.base.callback;

public interface OnUploadLogListener {
    default void onUploadStart() {

    }

    void onUploadLogSuccess();

    void onUploadLogError(String msg);
}
