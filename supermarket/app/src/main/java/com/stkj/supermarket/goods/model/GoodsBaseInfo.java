package com.stkj.supermarket.goods.model;

import com.stkj.supermarket.goods.data.GoodsConstants;

/**
 * 添加商品请求参数
 */
public class GoodsBaseInfo {
    private String goodsName;
    // 商品类型（0 标准商品 1 称重商品)
    private int goodsType;
    //条形码
    private String goodsCode;
    //零售价
    private String goodsUnitPrice;
    //商品图片
    private String goodsImg;
    //初始库存
    private String goodsInitStock;
    //生产日期
    private String productDate;
    //进货价格
    private String goodsInitPrice;
    //有效天数
    private String expireDays;
    //备注
    private String goodsNote;
    //销售规格id
    private String goodsSpec;
    private String goodsSpecStr;
    //商品分类id
    private String goodsCategory;
    private String goodsCategoryStr;
    //最低库存
    private String goodsMinStock;
    //商品损耗率
    private String goodsLossRate;

    public GoodsBaseInfo() {
    }

    public String getGoodsSpec() {
        return goodsSpec;
    }

    public void setGoodsSpec(String goodsSpec) {
        this.goodsSpec = goodsSpec;
    }

    public String getGoodsSpecStr() {
        if (isWeightGoods()) {
            return GoodsConstants.SPEC_WEIGHT_GOODS;
        }
        return goodsSpecStr == null ? "" : goodsSpecStr;
    }

    public void setGoodsSpecStr(String goodsSpecStr) {
        this.goodsSpecStr = goodsSpecStr;
    }

    public String getGoodsCategoryStr() {
        return goodsCategoryStr == null ? "" : goodsCategoryStr;
    }

    public void setGoodsCategoryStr(String goodsCategoryStr) {
        this.goodsCategoryStr = goodsCategoryStr;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getGoodsMinStock() {
        return goodsMinStock;
    }

    public void setGoodsMinStock(String goodsMinStock) {
        this.goodsMinStock = goodsMinStock;
    }

    public String getGoodsLossRate() {
        return goodsLossRate;
    }

    public void setGoodsLossRate(String goodsLossRate) {
        this.goodsLossRate = goodsLossRate;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(int goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsUnitPrice() {
        return goodsUnitPrice;
    }

    public void setGoodsUnitPrice(String goodsUnitPrice) {
        this.goodsUnitPrice = goodsUnitPrice;
    }

    public String getGoodsImg() {
        return goodsImg == null ? "" : goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getGoodsInitStock() {
        return goodsInitStock;
    }

    public void setGoodsInitStock(String goodsInitStock) {
        this.goodsInitStock = goodsInitStock;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getGoodsInitPrice() {
        return goodsInitPrice;
    }

    public void setGoodsInitPrice(String goodsInitPrice) {
        this.goodsInitPrice = goodsInitPrice;
    }

    public String getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(String expireDays) {
        this.expireDays = expireDays;
    }

    public String getGoodsNote() {
        return goodsNote;
    }

    public void setGoodsNote(String goodsNote) {
        this.goodsNote = goodsNote;
    }

    public boolean isStandardGoods() {
        return goodsType == GoodsConstants.TYPE_GOODS_STANDARD;
    }

    public boolean isWeightGoods() {
        return goodsType == GoodsConstants.TYPE_GOODS_WEIGHT;
    }
}
