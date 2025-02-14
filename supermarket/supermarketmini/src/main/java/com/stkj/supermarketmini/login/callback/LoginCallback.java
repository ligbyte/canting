package com.stkj.supermarketmini.login.callback;

public interface LoginCallback {
    void onLoginSuccess();

    default void onLoginError(String msg) {
    }
}
