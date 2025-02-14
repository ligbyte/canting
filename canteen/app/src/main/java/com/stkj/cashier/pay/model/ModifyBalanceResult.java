package com.stkj.cashier.pay.model;

/**
 * 支付金额结果
 */
public class ModifyBalanceResult {

    private String balance;
    private String consumption_Mone;
    private String full_name;
    private String bill_count;
    private String customerNo;
    private String payNo;

    public ModifyBalanceResult() {
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getConsumption_Mone() {
        return consumption_Mone == null ? "" : consumption_Mone;
    }

    public void setConsumption_Mone(String consumption_Mone) {
        this.consumption_Mone = consumption_Mone;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getBill_count() {
        return bill_count == null ? "" : bill_count;
    }

    public void setBill_count(String bill_count) {
        this.bill_count = bill_count;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }
}