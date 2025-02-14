package com.stkj.supermarketmini.payment.model;

import com.stkj.supermarketmini.login.helper.LoginHelper;

/**
 * 支付订单
 */
public class ConsumeOrderRequest {

    private String deviceNo = "";
    //职工卡号
    private String cardNumber = "";
    //支付费用
    private String money = "";
    //消费方式
    private String consumptionType = "";
    //三方付款码
    private String authCode = "";
    //订单id
    private String orderId = "";

    public ConsumeOrderRequest() {
        deviceNo = LoginHelper.INSTANCE.getMachineNumber();
    }

    public String getDeviceNo() {
        return LoginHelper.INSTANCE.getMachineNumber();
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getConsumptionType() {
        return consumptionType;
    }

    public void setConsumptionType(String consumptionType) {
        this.consumptionType = consumptionType;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
