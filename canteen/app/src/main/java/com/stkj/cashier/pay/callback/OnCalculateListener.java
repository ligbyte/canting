package com.stkj.cashier.pay.callback;

public interface OnCalculateListener {

    void onConfirmMoney(String payMoney);

    default void onClickDisableConfirm() {

    }

}
