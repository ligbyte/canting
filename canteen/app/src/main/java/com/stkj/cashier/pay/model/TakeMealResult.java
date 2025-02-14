package com.stkj.cashier.pay.model;

/**
 * 出餐结果
 */
public class TakeMealResult {
    private String Card_Number;
    private String Full_Name;
    private String Balance;
    private String orderNumber;

    public TakeMealResult() {
    }

    public String getCard_Number() {
        return Card_Number;
    }

    public void setCard_Number(String card_Number) {
        Card_Number = card_Number;
    }

    public String getFull_Name() {
        return Full_Name;
    }

    public void setFull_Name(String full_Name) {
        Full_Name = full_Name;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}