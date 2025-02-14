package com.stkj.supermarket.pay.data;

import com.stkj.supermarket.R;
import com.stkj.supermarket.setting.data.PaymentSettingMMKV;

public class PayConstants {

    /**
     * 支付状态
     */
    public static final String ORDER_SUCCESS_STATUS = "1";
    public static final String ORDER_UNKNOWN_STATUS = "0";
    public static final String ORDER_FAIL_STATUS = "2";

    /**
     * 支付方式
     */
    //人脸
    public static final int PAY_TYPE_FACE = 10;
    //刷卡
    public static final int PAY_TYPE_IC_CARD = 20;
    //职工码
    public static final int PAY_TYPE_QRCODE = 30;
    //微信(聚合支付)
    public static final int PAY_TYPE_THIRD = 50;
    //现金
    public static final int PAY_TYPE_CASH = 60;

    public static int getPayTypeRes(int payType) {
        if (payType == PAY_TYPE_THIRD) {
            boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
            return switchTongLianPay ? R.mipmap.icon_pay_juhe_circle : R.mipmap.icon_pay_weixin_circle;
        } else if (payType == PAY_TYPE_IC_CARD) {
            return R.mipmap.icon_pay_card_circle;
        } else if (payType == PAY_TYPE_CASH) {
            return R.mipmap.icon_pay_cash_circle;
        } else if (payType == PAY_TYPE_FACE) {
            return R.mipmap.icon_pay_face_circle;
        } else if (payType == PAY_TYPE_QRCODE) {
            return R.mipmap.icon_pay_qrcode_circle;
        }
        return 0;
    }

    public static String getPayTypeStr(int payType) {
        if (payType == PAY_TYPE_THIRD) {
            boolean switchTongLianPay = PaymentSettingMMKV.getSwitchTongLianPay();
            return switchTongLianPay ? "聚合支付" : "微信支付";
        } else if (payType == PAY_TYPE_IC_CARD) {
            return "餐卡支付";
        } else if (payType == PAY_TYPE_CASH) {
            return "现金支付";
        } else if (payType == PAY_TYPE_FACE) {
            return "刷脸支付";
        } else if (payType == PAY_TYPE_QRCODE) {
            return "职工码支付";
        }
        return "";
    }


    //整单改价
    public static final int CHAGE_ORDER_PRICE = 0;
    //抹角
    public static final int CHAGE_MO_JIAO = 1;
    //抹分
    public static final int CHAGE_MO_FEN = 2;

    public static String getChangePriceTypeDesc(int changeType) {
        if (changeType == CHAGE_ORDER_PRICE) {
            return "整单改价";
        } else if (changeType == CHAGE_MO_JIAO) {
            return "抹角";
        } else if (changeType == CHAGE_MO_FEN) {
            return "抹分";
        }
        return "";
    }

    //快速收银
    public static final int ORDER_TYPE_FAST_PAY = 0;
    //普通收银
    public static final int ORDER_TYPE_NORMAL_PAY = 1;

}
