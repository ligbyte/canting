package com.stkj.supermarketmini.payment.model;

import java.util.List;

public class OrderHistoryListResponse {

    private List<OrderHistoryResponse> records;

    public OrderHistoryListResponse() {
    }

    public List<OrderHistoryResponse> getRecords() {
        return records;
    }

    public void setRecords(List<OrderHistoryResponse> records) {
        this.records = records;
    }
}
