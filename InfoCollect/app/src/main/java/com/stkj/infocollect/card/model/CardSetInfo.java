package com.stkj.infocollect.card.model;

/**
 * 卡设置信息
 */
public class CardSetInfo {
    private String opencardFee;
    private String depositFee;
    private String replaceCardFee;

    public CardSetInfo() {
    }

    public String getOpencardFee() {
        return opencardFee == null ? "" : opencardFee;
    }

    public void setOpencardFee(String opencardFee) {
        this.opencardFee = opencardFee;
    }

    public String getDepositFee() {
        return depositFee == null ? "" : depositFee;
    }

    public void setDepositFee(String depositFee) {
        this.depositFee = depositFee;
    }

    public String getReplaceCardFee() {
        return replaceCardFee;
    }

    public void setReplaceCardFee(String replaceCardFee) {
        this.replaceCardFee = replaceCardFee;
    }
}
