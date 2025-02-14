package com.stkj.cashier.home.model;

/**
 * 首页Tab信息
 */
public class HomeTabInfo<T> {

    private int selectRes;
    private int unSelectRes;
    private boolean isSelect;
    private T extraInfo;

    public HomeTabInfo() {
    }

    public HomeTabInfo(int selectRes, int unSelectRes) {
        this.selectRes = selectRes;
        this.unSelectRes = unSelectRes;
    }

    public int getSelectRes() {
        return selectRes;
    }

    public void setSelectRes(int selectRes) {
        this.selectRes = selectRes;
    }

    public int getUnSelectRes() {
        return unSelectRes;
    }

    public void setUnSelectRes(int unSelectRes) {
        this.unSelectRes = unSelectRes;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public T getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(T extraInfo) {
        this.extraInfo = extraInfo;
    }
}
