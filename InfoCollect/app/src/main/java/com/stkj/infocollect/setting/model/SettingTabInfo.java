package com.stkj.infocollect.setting.model;

public class SettingTabInfo {

    public static final int TAB_TYPE_SERVER_ADDRESS = 1;
    public static final String TAB_NAME_SERVER_ADDRESS = "服务器地址";
    public static final int TAB_TYPE_DEVICE_SETTING = 2;
    public static final String TAB_NAME_DEVICE_SETTING = "本机设置";
    public static final int TAB_TYPE_FACE_PASS = 3;
    public static final String TAB_NAME_FACE_PASS = "人脸识别";

    private String tabName;
    private int tabIcon;
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

    public SettingTabInfo(int tabType,String tabName, int tabIcon) {
        this.tabName = tabName;
        this.tabIcon = tabIcon;
        this.tabType = tabType;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getTabIcon() {
        return tabIcon;
    }

    public void setTabIcon(int tabIcon) {
        this.tabIcon = tabIcon;
    }
}
