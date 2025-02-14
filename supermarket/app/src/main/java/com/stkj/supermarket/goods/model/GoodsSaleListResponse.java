package com.stkj.supermarket.goods.model;

import java.util.List;

public class GoodsSaleListResponse {
    public List<GoodsSaleListInfo> records;

    public List<GoodsSaleListInfo> getRecords() {
        return records;
    }

    public void setRecords(List<GoodsSaleListInfo> records) {
        this.records = records;
    }
}
