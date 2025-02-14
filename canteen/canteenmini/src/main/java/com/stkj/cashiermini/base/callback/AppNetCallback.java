package com.stkj.cashiermini.base.callback;

public interface AppNetCallback {
    default void onNetInitSuccess(String machineNumber) {
    }

    default void onNetInitError(String message) {
    }
}
