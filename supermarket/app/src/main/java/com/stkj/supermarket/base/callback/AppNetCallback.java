package com.stkj.supermarket.base.callback;

public interface AppNetCallback {
    default void onNetInitSuccess() {
    }

    default void onNetInitError(String message) {
    }
}
