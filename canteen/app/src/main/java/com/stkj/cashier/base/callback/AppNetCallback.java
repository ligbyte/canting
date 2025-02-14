package com.stkj.cashier.base.callback;

public interface AppNetCallback {
    default void onNetInitSuccess() {
    }

    default void onNetInitError(String message) {
    }
}
