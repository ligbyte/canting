package com.stkj.cashier.pay.model;

/**
 * 订单信息
 */
public class ConsumerRecordListInfo {
    private String Card_Number;
    private String bizDate;
    private String Full_Name;
    private String bizAmount;
    private String status;
    private String Id;
    private String User_Tel;
    private String feeType;
    private String type;
    private String consumeMethod;

    public ConsumerRecordListInfo() {
    }

    public String getCard_Number() {
        return Card_Number;
    }

    public void setCard_Number(String card_Number) {
        Card_Number = card_Number;
    }

    public String getBizDate() {
        return bizDate;
    }

    public void setBizDate(String bizDate) {
        this.bizDate = bizDate;
    }

    public String getFull_Name() {
        return Full_Name;
    }

    public void setFull_Name(String full_Name) {
        Full_Name = full_Name;
    }

    public String getBizAmount() {
        return bizAmount;
    }

    public void setBizAmount(String bizAmount) {
        this.bizAmount = bizAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUser_Tel() {
        return User_Tel;
    }

    public void setUser_Tel(String user_Tel) {
        User_Tel = user_Tel;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getConsumeMethod() {
        return consumeMethod;
    }

    public void setConsumeMethod(String consumeMethod) {
        this.consumeMethod = consumeMethod;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
