package com.stkj.cashier.pay.model;

/**
 * 餐厅时段信息
 */
public class CanteenCurrentTimeInfo {
    private int total;
    private String endOrder;
    private int takeMeal;
    private String end;
    private String feeType;
    private String begin;

    public CanteenCurrentTimeInfo() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getEndOrder() {
        return endOrder;
    }

    public void setEndOrder(String endOrder) {
        this.endOrder = endOrder;
    }

    public int getTakeMeal() {
        return takeMeal;
    }

    public void setTakeMeal(int takeMeal) {
        this.takeMeal = takeMeal;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }
}