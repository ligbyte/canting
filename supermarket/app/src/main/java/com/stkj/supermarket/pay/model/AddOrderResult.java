package com.stkj.supermarket.pay.model;

/**
 * 添加订单回调结果
 */
public class AddOrderResult {

    private String id;
    private String orderNo;

    public AddOrderResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
