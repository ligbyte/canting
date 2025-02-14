package com.stkj.cashiermini.login.callback;

public interface LoginCallback {
    void onLoginSuccess();

    default void onLoginError(String msg) {
    }
}
