package com.stkj.cashier.pay.model;

import java.util.Map;

/**
 * 本地消费记录
 */
public class LocalConsumerRecord {

    private ModifyBalanceResult balanceResult;
    private Map<String,String> payRequest;
    private int consumerMode;

    public LocalConsumerRecord() {
    }


    public LocalConsumerRecord(ModifyBalanceResult balanceResult, Map<String, String> payRequest, int consumerMode) {
        this.balanceResult = balanceResult;
        this.payRequest = payRequest;
        this.consumerMode = consumerMode;
    }

    public ModifyBalanceResult getBalanceResult() {
        return balanceResult;
    }

    public void setBalanceResult(ModifyBalanceResult balanceResult) {
        this.balanceResult = balanceResult;
    }

    public Map<String, String> getPayRequest() {
        return payRequest;
    }

    public void setPayRequest(Map<String, String> payRequest) {
        this.payRequest = payRequest;
    }

    public int getConsumerMode() {
        return consumerMode;
    }

    public void setConsumerMode(int consumerMode) {
        this.consumerMode = consumerMode;
    }
}
