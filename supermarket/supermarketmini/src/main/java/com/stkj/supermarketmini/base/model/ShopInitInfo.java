package com.stkj.supermarketmini.base.model;

/**
 * 店铺基础信息
 */
public class ShopInitInfo {
    private String deviceId;
    private String shopId;
    private String domain;

    public ShopInitInfo() {
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
