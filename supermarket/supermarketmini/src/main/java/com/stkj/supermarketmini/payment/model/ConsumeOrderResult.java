package com.stkj.supermarketmini.payment.model;

/**
 * 消费回调结果
 */
public class ConsumeOrderResult {

    private String orderId;
    private String payNo;
    private String payStatus;

    public ConsumeOrderResult() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
}
