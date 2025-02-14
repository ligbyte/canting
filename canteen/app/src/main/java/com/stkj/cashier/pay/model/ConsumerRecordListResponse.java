package com.stkj.cashier.pay.model;

import java.util.List;

/**
 * 历史订单
 */
public class ConsumerRecordListResponse {

    private List<ConsumerRecordListInfo> Results;
    private int totalCount;
    private int totalPage;
    private int pageIndex;
    private int pageSize;

    public ConsumerRecordListResponse() {
    }

    public List<ConsumerRecordListInfo> getResults() {
        return Results;
    }

    public void setResults(List<ConsumerRecordListInfo> results) {
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