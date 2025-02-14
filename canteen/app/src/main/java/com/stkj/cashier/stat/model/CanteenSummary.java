package com.stkj.cashier.stat.model;

import java.util.List;

/**
 * 餐厅统计
 */
public class CanteenSummary {
    private List<FeeTypeList> feeTypeList;

    private List<ConsumeMethodList> consumeMethodList;

    public CanteenSummary() {
    }

    public List<FeeTypeList> getFeeTypeList() {
        return feeTypeList;
    }

    public void setFeeTypeList(List<FeeTypeList> feeTypeList) {
        this.feeTypeList = feeTypeList;
    }

    public List<ConsumeMethodList> getConsumeMethodList() {
        return consumeMethodList;
    }

    public void setConsumeMethodList(List<ConsumeMethodList> consumeMethodList) {
        this.consumeMethodList = consumeMethodList;
    }

    public static class FeeTypeList {
        private int value;
        private String key;

        public FeeTypeList() {
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class ConsumeMethodList {
        private String key1;
        private float value;
        private String key;

        public ConsumeMethodList() {
        }

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}