package com.stkj.supermarketmini.payment.model;

public class OrderHistoryListRequestPageParams {

    //当前页码
    private int current = 1;
    //页面数量
    private int size = 10;

    public OrderHistoryListRequestPageParams() {
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void resetPage() {
        current = 1;
        size = 10;
    }
}
