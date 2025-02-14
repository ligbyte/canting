package com.stkj.cashier.setting.model;

public class SettingTabInfo {

    public static final int TAB_TYPE_SERVER_ADDRESS = 1;
    public static final String TAB_NAME_SERVER_ADDRESS = "服务器地址";
    public static final int TAB_TYPE_DEVICE_SETTING = 2;
    public static final String TAB_NAME_DEVICE_SETTING = "本机设置";
    public static final int TAB_TYPE_WIFI_CONNECT = 3;
    public static final String TAB_NAME_WIFI_CONNECT = "wifi连接";
    public static final int TAB_TYPE_PAYMENT_SETTING = 4;
    public static final String TAB_NAME_PAYMENT_SETTING = "消费设置";
    public static final int TAB_TYPE_VOICE_SETTING = 5;
    public static final String TAB_NAME_VOICE_SETTING = "语音设置";
    public static final int TAB_TYPE_FACE_PASS = 6;
    public static final String TAB_NAME_FACE_PASS = "人脸识别";
    public static final int TAB_TYPE_RESTART_APP = 7;
    public static final String TAB_NAME_RESTART_APP = "重启软件";

    private String tabName;
    private int tabType;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getTabType() {
        return tabType;
    }

    public void setTabType(int tabType) {
        this.tabType = tabType;
    }

    public SettingTabInfo() {
    }

    public SettingTabInfo(String tabName, int tabType) {
        this.tabName = tabName;
        this.tabType = tabType;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }
}
