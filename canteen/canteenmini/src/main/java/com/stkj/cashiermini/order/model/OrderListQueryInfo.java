package com.stkj.cashiermini.order.model;

public class OrderListQueryInfo {
    private int pageIndex;
    private int pageSize;

    public OrderListQueryInfo() {
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void resetDefaultData() {
        pageIndex = 1;
        pageSize = 20;
    }
}
