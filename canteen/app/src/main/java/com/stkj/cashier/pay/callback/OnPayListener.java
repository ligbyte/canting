package com.stkj.cashier.pay.callback;

import androidx.annotation.Nullable;

import com.stkj.cashier.pay.model.ModifyBalanceResult;

import java.util.Map;

public interface OnPayListener {
    void onStartPay(Map<String, String> payRequest);

    void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult);

    void onPayError(String responseCode, Map<String, String> payRequest, @Nullable ModifyBalanceResult modifyBalanceResult, String msg);
}
