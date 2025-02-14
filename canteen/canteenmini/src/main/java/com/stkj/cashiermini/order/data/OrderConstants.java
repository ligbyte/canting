package com.stkj.cashiermini.order.data;

import android.text.TextUtils;

public class OrderConstants {

    public static String getPayTypeStr(String consumeMethod) {
        String payType = "无";
        if (TextUtils.equals("10", consumeMethod)) {
            payType = "刷脸消费";
        } else if (TextUtils.equals("20", consumeMethod)) {
            payType = "刷卡消费";
        } else if (TextUtils.equals("30", consumeMethod)) {
            payType = "二维码消费";
        } else if (TextUtils.equals("40", consumeMethod)) {
            payType = "支付宝消费";
        } else if (TextUtils.equals("50", consumeMethod)) {
            payType = "微信消费";
        }
        return payType;
    }

    public static String getFeeTypeStr(String feeType) {
        String feeTypeStr = "无";
        if (TextUtils.equals("1", feeType)) {
            feeTypeStr = "早餐";
        } else if (TextUtils.equals("2", feeType)) {
            feeTypeStr = "午餐";
        } else if (TextUtils.equals("3", feeType)) {
            feeTypeStr = "晚餐";
        }
        return feeTypeStr;
    }

}
