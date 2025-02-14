package com.stkj.supermarket.goods.model;

/**
 * 商品条码信息
 */
public class GoodsQrCodeDetail {
    private String code;
    private String img;
    private String goodsName;
    private String spec;

    public GoodsQrCodeDetail() {
    }

    public String getCode() {
        return code == null ? "" : code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImg() {
        return img == null ? "" : img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGoodsName() {
        return goodsName == null ? "" : goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSpec() {
        return spec == null ? "" : spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

}
