package com.stkj.supermarketmini.payment.callback;

import androidx.annotation.Nullable;

import com.stkj.supermarketmini.payment.model.AddOrderRequest;
import com.stkj.supermarketmini.payment.model.AddOrderResult;
import com.stkj.supermarketmini.payment.model.ConsumeOrderRequest;

public interface OnPayListener {
    void onStartPay(AddOrderRequest addOrderRequest);

    void onPaySuccess(AddOrderRequest addOrderRequest, @Nullable AddOrderResult addOrderResult, @Nullable ConsumeOrderRequest consumeOrderRequest);

    void onPayError(String responseCode, AddOrderRequest addOrderRequest, @Nullable AddOrderResult addOrderResult, String msg);
}
