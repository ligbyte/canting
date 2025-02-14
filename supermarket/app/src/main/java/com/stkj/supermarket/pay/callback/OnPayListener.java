package com.stkj.supermarket.pay.callback;

import androidx.annotation.Nullable;

import com.stkj.supermarket.pay.model.AddOrderRequest;
import com.stkj.supermarket.pay.model.AddOrderResult;
import com.stkj.supermarket.pay.model.ConsumeOrderRequest;

public interface OnPayListener {
    void onStartPay(AddOrderRequest addOrderRequest);

    void onPaySuccess(AddOrderRequest addOrderRequest, @Nullable AddOrderResult addOrderResult, @Nullable ConsumeOrderRequest consumeOrderRequest);

    void onPayError(String responseCode, AddOrderRequest addOrderRequest, @Nullable AddOrderResult addOrderResult, String msg);
}
