package com.stkj.cashier.home.model;

/**
 * 心跳返回数据
 */
public class HeartBeatInfo {
    private String updateConfig;
    private String updateUserInfo;
    private int IsOpen;

    public HeartBeatInfo() {
    }

    public String getUpdateConfig() {
        return updateConfig;
    }

    public void setUpdateConfig(String updateConfig) {
        this.updateConfig = updateConfig;
    }

    public String getUpdateUserInfo() {
        return updateUserInfo;
    }

    public void setUpdateUserInfo(String updateUserInfo) {
        this.updateUserInfo = updateUserInfo;
    }

    public int getIsOpen() {
        return IsOpen;
    }

    public void setIsOpen(int isOpen) {
        IsOpen = isOpen;
    }
}
