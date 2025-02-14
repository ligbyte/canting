package com.stkj.cashier.pay.callback;

public interface OnConsumerModeListener {

    default void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {

    }

}
