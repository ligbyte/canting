package com.stkj.supermarket.login.callback;

public interface LoginCallback {
    void onLoginSuccess();

    default void onLoginError(String msg) {
    }
}
