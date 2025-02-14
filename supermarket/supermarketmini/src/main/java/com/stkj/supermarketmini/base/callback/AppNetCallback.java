package com.stkj.supermarketmini.base.callback;

public interface AppNetCallback {
    default void onNetInitSuccess(String machineNumber) {
    }

    default void onNetInitError(String message) {
    }
}
