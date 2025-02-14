package com.stkj.supermarket.goods.model;

/**
 * 编辑商品请求参数
 */
public class GoodsEditBaseInfo {
    private String id;
    private String goodsName;
    // 商品类型（0 标准商品 1 称重商品)
    private int goodsType;
    //条形码
    private String goodsCode;
    //零售价
    private String goodsUnitPrice;
    //商品图片
    private String goodsImg;
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

    public GoodsEditBaseInfo() {
    }

    public GoodsEditBaseInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getGoodsNote() {
        return goodsNote;
    }

    public void setGoodsNote(String goodsNote) {
        this.goodsNote = goodsNote;
    }

    public String getGoodsSpec() {
        return goodsSpec;
    }

    public void setGoodsSpec(String goodsSpec) {
        this.goodsSpec = goodsSpec;
    }

    public String getGoodsSpecStr() {
        return goodsSpecStr;
    }

    public void setGoodsSpecStr(String goodsSpecStr) {
        this.goodsSpecStr = goodsSpecStr;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getGoodsCategoryStr() {
        return goodsCategoryStr;
    }

    public void setGoodsCategoryStr(String goodsCategoryStr) {
        this.goodsCategoryStr = goodsCategoryStr;
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
}
