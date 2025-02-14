package com.stkj.supermarketmini.goods.model;

import com.stkj.supermarketmini.base.excel.annotation.ExcelReadCell;
import com.stkj.supermarketmini.goods.data.GoodsConstants;

/**
 * 添加商品请求参数
 */
public class GoodsBaseInfo {
    @ExcelReadCell(name = "名称")
    private String goodsName;
    // 商品类型（0 标准商品 1 称重商品)
    private int goodsType;
    //条形码
    @ExcelReadCell(name = "条码")
    private String goodsCode;
    //零售价
    @ExcelReadCell(name = "零售价")
    private String goodsUnitPrice;
    //商品图片
    private String goodsImg;
    //初始库存
    @ExcelReadCell(name = "库存")
    private String goodsInitStock;
    //生产日期
    @ExcelReadCell(name = "生产日期")
    private String productDate;
    //进货价格
    @ExcelReadCell(name = "进货价")
    private String goodsInitPrice;
    //有效天数
    @ExcelReadCell(name = "保质期(天)")
    private String expireDays;
    //备注
    @ExcelReadCell(name = "备注")
    private String goodsNote;
    //销售规格id
    private String goodsSpec;
    @ExcelReadCell(name = "规格")
    private String goodsSpecStr;
    //商品分类id
    private String goodsCategory;
    @ExcelReadCell(name = "类别")
    private String goodsCategoryStr;
    //最低库存
    @ExcelReadCell(name = "最低库存")
    private String goodsMinStock;
    //商品损耗率
    @ExcelReadCell(name = "损耗率(百分比)")
    private String goodsLossRate;

    public GoodsBaseInfo() {
    }

    public String getGoodsSpec() {
        return goodsSpec == null ? "" : goodsSpec;
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
        return goodsCategory == null ? "" : goodsCategory;
    }

    public void setGoodsCategory(String goodsCategory) {
        this.goodsCategory = goodsCategory;
    }

    public String getGoodsMinStock() {
        return goodsMinStock == null ? "" : goodsMinStock;
    }

    public void setGoodsMinStock(String goodsMinStock) {
        this.goodsMinStock = goodsMinStock;
    }

    public String getGoodsLossRate() {
        return goodsLossRate == null ? "" : goodsLossRate;
    }

    public void setGoodsLossRate(String goodsLossRate) {
        this.goodsLossRate = goodsLossRate;
    }

    public String getGoodsName() {
        return goodsName == null ? "" : goodsName;
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
        return goodsCode == null ? "" : goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGoodsUnitPrice() {
        return goodsUnitPrice == null ? "" : goodsUnitPrice;
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
        return goodsInitStock == null ? "" : goodsInitStock;
    }

    public void setGoodsInitStock(String goodsInitStock) {
        this.goodsInitStock = goodsInitStock;
    }

    public String getProductDate() {
        return productDate == null ? "" : productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getGoodsInitPrice() {
        return goodsInitPrice == null ? "" : goodsInitPrice;
    }

    public void setGoodsInitPrice(String goodsInitPrice) {
        this.goodsInitPrice = goodsInitPrice;
    }

    public String getExpireDays() {
        return expireDays == null ? "" : expireDays;
    }

    public void setExpireDays(String expireDays) {
        this.expireDays = expireDays;
    }

    public String getGoodsNote() {
        return goodsNote == null ? "" : goodsNote;
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

    @Override
    public String toString() {
        return "GoodsBaseInfo{" +
                "goodsName='" + goodsName + '\'' +
                ", goodsType=" + goodsType +
                ", goodsCode='" + goodsCode + '\'' +
                ", goodsUnitPrice='" + goodsUnitPrice + '\'' +
                ", goodsImg='" + goodsImg + '\'' +
                ", goodsInitStock='" + goodsInitStock + '\'' +
                ", productDate='" + productDate + '\'' +
                ", goodsInitPrice='" + goodsInitPrice + '\'' +
                ", expireDays='" + expireDays + '\'' +
                ", goodsNote='" + goodsNote + '\'' +
                ", goodsSpec='" + goodsSpec + '\'' +
                ", goodsSpecStr='" + goodsSpecStr + '\'' +
                ", goodsCategory='" + goodsCategory + '\'' +
                ", goodsCategoryStr='" + goodsCategoryStr + '\'' +
                ", goodsMinStock='" + goodsMinStock + '\'' +
                ", goodsLossRate='" + goodsLossRate + '\'' +
                '}';
    }
}
