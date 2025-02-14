package com.stkj.infocollect.setting.model;

/**
 * 刷新支付方式
 */
public class RefreshPayType {
    private boolean payTypeFace;
    private boolean payTypeCard;
    private boolean payTypeScan;

    public RefreshPayType() {
    }

    public RefreshPayType(boolean payTypeFace, boolean payTypeCard, boolean payTypeScan) {
        this.payTypeFace = payTypeFace;
        this.payTypeCard = payTypeCard;
        this.payTypeScan = payTypeScan;
    }

    public boolean isPayTypeFace() {
        return payTypeFace;
    }

    public void setPayTypeFace(boolean payTypeFace) {
        this.payTypeFace = payTypeFace;
    }

    public boolean isPayTypeCard() {
        return payTypeCard;
    }

    public void setPayTypeCard(boolean payTypeCard) {
        this.payTypeCard = payTypeCard;
    }

    public boolean isPayTypeScan() {
        return payTypeScan;
    }

    public void setPayTypeScan(boolean payTypeScan) {
        this.payTypeScan = payTypeScan;
    }
}
