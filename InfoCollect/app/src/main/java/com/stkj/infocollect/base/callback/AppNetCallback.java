package com.stkj.infocollect.base.callback;

public interface AppNetCallback {
    default void onNetInitSuccess() {
    }

    default void onNetInitError(String message) {
    }
}
