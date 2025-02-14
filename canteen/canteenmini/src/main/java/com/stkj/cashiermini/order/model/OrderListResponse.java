package com.stkj.cashiermini.order.model;

import java.util.List;

/**
 * 历史订单
 */
public class OrderListResponse {

    private List<OrderListInfo> Results;
    private int totalCount;
    private int totalPage;
    private int pageIndex;
    private int pageSize;

    public OrderListResponse() {
    }

    public List<OrderListInfo> getResults() {
        return Results;
    }

    public void setResults(List<OrderListInfo> results) {
        Results = results;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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
}