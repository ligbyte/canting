package com.stkj.supermarket.pay.model;

import java.util.Objects;

public class GoodsWeightIndexInfo {
    private String titleIndex;

    public GoodsWeightIndexInfo(String titleIndex) {
        this.titleIndex = titleIndex;
    }

    public String getTitleIndex() {
        return titleIndex;
    }

    public void setTitleIndex(String titleIndex) {
        this.titleIndex = titleIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsWeightIndexInfo that = (GoodsWeightIndexInfo) o;
        return Objects.equals(titleIndex, that.titleIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleIndex);
    }
}
