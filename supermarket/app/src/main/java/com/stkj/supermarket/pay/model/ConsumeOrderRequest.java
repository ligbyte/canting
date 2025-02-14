package com.stkj.supermarket.pay.model;

import com.stkj.supermarket.base.device.DeviceManager;

/**
 * 支付订单
 */
public class ConsumeOrderRequest {

    private String deviceNo = DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber();
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
    //通联支付
    private String payType = "";

    public String getDeviceNo() {
        return deviceNo;
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

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
