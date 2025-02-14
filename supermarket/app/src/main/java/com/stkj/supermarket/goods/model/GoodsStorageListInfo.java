package com.stkj.supermarket.goods.model;

/**
 * 已勾选的入库商品
 */
public class GoodsStorageListInfo {

    private String inputGoodsProductDate;
    private String inputGoodsProductExpireDays;
    private String setGoodsProductExpireDate;
    private String setGoodsProductExpireTag = "天";
    private GoodsInventoryListInfo inventoryListInfo;

    public GoodsStorageListInfo() {
    }

    public GoodsStorageListInfo(GoodsInventoryListInfo inventoryListInfo) {
        this.inventoryListInfo = inventoryListInfo;
    }

    public String getInputGoodsProductDate() {
        return inputGoodsProductDate == null ? "" : inputGoodsProductDate;
    }

    public void setInputGoodsProductDate(String inputGoodsProductDate) {
        this.inputGoodsProductDate = inputGoodsProductDate;
    }

    public String getInputGoodsProductExpireDays() {
        return inputGoodsProductExpireDays == null ? "" : inputGoodsProductExpireDays;
    }

    public void setInputGoodsProductExpireDays(String inputGoodsProductExpireDays) {
        this.inputGoodsProductExpireDays = inputGoodsProductExpireDays;
    }

    public GoodsInventoryListInfo getInventoryListInfo() {
        return inventoryListInfo;
    }

    public void setInventoryListInfo(GoodsInventoryListInfo inventoryListInfo) {
        this.inventoryListInfo = inventoryListInfo;
    }

    public String getSourceStorageId() {
        if (inventoryListInfo != null) {
            return inventoryListInfo.getStorageId();
        }
        return "";
    }

    public String getSetGoodsProductExpireDate() {
        return setGoodsProductExpireDate;
    }

    public void setSetGoodsProductExpireDate(String setGoodsProductExpireDate) {
        this.setGoodsProductExpireDate = setGoodsProductExpireDate;
    }

    public String getSetGoodsProductExpireTag() {
        return setGoodsProductExpireTag;
    }

    public void setSetGoodsProductExpireTag(String setGoodsProductExpireTag) {
        this.setGoodsProductExpireTag = setGoodsProductExpireTag;
    }

    //是否已经入库
    private boolean hasSaveStorage;

    public boolean isHasSaveStorage() {
        return hasSaveStorage;
    }

    public void setHasSaveStorage(boolean hasSaveStorage) {
        this.hasSaveStorage = hasSaveStorage;
    }

    public String getGoodsId() {
        if (inventoryListInfo != null) {
            return inventoryListInfo.getGoodsId();
        }
        return "";
    }
}
