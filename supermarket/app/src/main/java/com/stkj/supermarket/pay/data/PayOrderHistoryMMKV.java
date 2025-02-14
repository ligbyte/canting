package com.stkj.supermarket.pay.data;

import android.text.TextUtils;

import com.stkj.supermarket.base.utils.JacksonUtils;
import com.stkj.supermarket.pay.model.PayHistoryOrderInfo;
import com.tencent.mmkv.MMKV;

public class PayOrderHistoryMMKV {

    public static final String MMKV_NAME = "pay_order_history";
    public static final String KEY_LAST_ORDER_HISTORY = "last_order_history";

    public static PayHistoryOrderInfo getLastOrderHistory() {
        MMKV mmkv = getMMKV();
        String string = mmkv.getString(KEY_LAST_ORDER_HISTORY, "");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        PayHistoryOrderInfo payHistoryOrderInfo = JacksonUtils.convertJsonObject(string, PayHistoryOrderInfo.class);
        return payHistoryOrderInfo;
    }

    public static void putLastOrderHistory(PayHistoryOrderInfo historyOrderInfo) {
        MMKV mmkv = getMMKV();
        mmkv.putString(KEY_LAST_ORDER_HISTORY, JacksonUtils.convertJsonString(historyOrderInfo));
    }

    public static MMKV getMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }
}
