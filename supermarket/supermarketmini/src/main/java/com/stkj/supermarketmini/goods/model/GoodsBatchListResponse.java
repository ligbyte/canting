package com.stkj.supermarketmini.goods.model;

import java.util.List;

public class GoodsBatchListResponse {
    public int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GoodsBatchListInfo> records;

    public List<GoodsBatchListInfo> getRecords() {
        return records;
    }

    public void setRecords(List<GoodsBatchListInfo> records) {
        this.records = records;
    }
}
