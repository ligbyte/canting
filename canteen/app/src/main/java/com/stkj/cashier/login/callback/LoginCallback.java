package com.stkj.cashier.login.callback;

public interface LoginCallback {
    void onLoginSuccess();

    default void onLoginError(String msg) {
    }
}
