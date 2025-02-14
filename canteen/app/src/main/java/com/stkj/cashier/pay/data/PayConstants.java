package com.stkj.cashier.pay.data;

import android.text.TextUtils;

import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.common.log.LogHelper;
import com.stkj.common.utils.TimeUtils;

import java.util.Random;

public class PayConstants {

    /**
     * 消费账单类型
     */
    public static final String RECORD_TYPE_REFUND = "3";
    public static final String RECORD_TYPE_CONSUMER = "1";

    /**
     * 支付中状态
     */
    public static final String PAY_PROCESSING_STATUS = "10009";

    /**
     * 10-固定模式 40-菜品模式 50-时段模式 60-单价模式 80-菜品订餐 150-订餐模式 160-水控实时模式 161-水控预扣费模务
     * 扣费模式
     */
    public static final int DEDUCTION_TYPE_FIXED = 10;
    public static final int DEDUCTION_TYPE_MEAL = 40;
    public static final int DEDUCTION_TYPE_NUMBER = 50;
    public static final int DEDUCTION_TYPE_AMOUNT = 60;

    /**
     * 支付方式
     */
    //人脸
    public static final int PAY_TYPE_FACE = 10;
    //刷卡
    public static final int PAY_TYPE_IC_CARD = 20;
    //扫码支付（职工码、微信等）
    public static final int PAY_TYPE_QRCODE = 30;

    //金额模式
    public static final int CONSUMER_AMOUNT_MODE = 0;
    //按次模式
    public static final int CONSUMER_NUMBER_MODE = 1;
    //取餐模式
    public static final int CONSUMER_TAKE_MODE = 2;
    //送餐模式
    public static final int CONSUMER_SEND_MODE = 3;
    //称重模式
    public static final int CONSUMER_WEIGHT_MODE = 4;
    //定额模式
    public static final int CONSUMER_QUOTA_MODE = 5;

    /**
     * 餐别类型
     */
    //早餐
    public static final String FEE_TYPE_BREAKFAST = "1";
    //午餐
    public static final String FEE_TYPE_LUNCH = "2";
    //晚餐
    public static final String FEE_TYPE_DINNER = "3";

    public static String getConsumerModeStr(int mode) {
        if (mode == CONSUMER_AMOUNT_MODE) {
            return "金额模式";
        } else if (mode == CONSUMER_NUMBER_MODE) {
            return "按次模式";
        } else if (mode == CONSUMER_TAKE_MODE) {
            return "取餐模式";
        } else if (mode == CONSUMER_SEND_MODE) {
            return "送餐模式";
        } else if (mode == CONSUMER_WEIGHT_MODE) {
            return "称重模式";
        }else if (mode == CONSUMER_QUOTA_MODE) {
            return "定额模式";
        }
        return "金额模式";
    }

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
        if (TextUtils.equals(FEE_TYPE_BREAKFAST, feeType)) {
            feeTypeStr = "早餐";
        } else if (TextUtils.equals(FEE_TYPE_LUNCH, feeType)) {
            feeTypeStr = "午餐";
        } else if (TextUtils.equals(FEE_TYPE_DINNER, feeType)) {
            feeTypeStr = "晚餐";
        }
        return feeTypeStr;
    }

    /**
     * 创建一个随机订单号
     */
    public static String createOrderNumber() {
        int randoms = new Random().nextInt(9000) + 1000;
        String machineNumber = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
        String machineEndTag = "";
        if (!TextUtils.isEmpty(machineNumber) && machineNumber.length() >= 5) {
            machineEndTag = machineNumber.substring(machineNumber.length() - 5);
        }
        String orderNumber = "ZGXF" + machineEndTag + (System.currentTimeMillis() / 1000) + randoms;
        LogHelper.print("--createOrderNumber--orderNumber: " + orderNumber);
        return orderNumber;
    }

    public static int randomNumber(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

}
