package com.stkj.cashier.consumer.callback;

public interface OnInputNumberListener {

    void onConfirmNumber(String number);

    void onClickBack();

    default void onConfirmError(boolean hasInputNumber) {

    }
}
