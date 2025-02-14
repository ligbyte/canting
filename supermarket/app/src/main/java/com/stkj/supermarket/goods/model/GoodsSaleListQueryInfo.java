package com.stkj.supermarket.goods.model;

import com.stkj.supermarket.goods.data.GoodsConstants;

/**
 * 商品销售列表查询
 */
public class GoodsSaleListQueryInfo {

    //当前页码
    private int current = 1;
    //每页条数
    private int size = 10;
    //优惠类型（1 直降 2 折扣 3 满减)
    private String discountType = "";
    //有效期最大值
    private String expireMax = "";
    //有效期最小值
    private String expireMin = "";
    //实时库存最大值
    private String goodsRealStockMax = "";
    //实时库存最小值
    private String goodsRealStockMin = "";
    //商品类型
    private String goodsType = "";
    //商品零售价最大值
    private String goodsUnitPriceMax = "";
    //商品零售价最小值
    private String goodsUnitPriceMin = "";
    //搜索类型 搜索框(input) 弹窗筛选(dialog)
    private String searchType = GoodsConstants.INPUT_SEARCH_TYPE;
    //排序字段，字段驼峰名称，如：userName
    //库存排序（0 正序 1 倒叙）
    private String countSort = "";
    //有效期排序（0 正序 1 倒叙）
    private String expireSort = "";
    //售价排序（0 正序 1 倒叙）
    private String priceSort = "";
    //搜索关键字
    private String searchKey = "";
    //是否查过期（1 查过期 其他不查）
    private String expired = "";

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getDiscountType() {
        return discountType == null ? "" : discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getExpireMax() {
        return expireMax == null ? "" : expireMax;
    }

    public void setExpireMax(String expireMax) {
        this.expireMax = expireMax;
    }

    public String getExpireMin() {
        return expireMin == null ? "" : expireMin;
    }

    public void setExpireMin(String expireMin) {
        this.expireMin = expireMin;
    }

    public String getGoodsRealStockMax() {
        return goodsRealStockMax == null ? "" : goodsRealStockMax;
    }

    public void setGoodsRealStockMax(String goodsRealStockMax) {
        this.goodsRealStockMax = goodsRealStockMax;
    }

    public String getGoodsRealStockMin() {
        return goodsRealStockMin == null ? "" : goodsRealStockMin;
    }

    public void setGoodsRealStockMin(String goodsRealStockMin) {
        this.goodsRealStockMin = goodsRealStockMin;
    }

    public String getGoodsType() {
        return goodsType == null ? "" : goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsUnitPriceMax() {
        return goodsUnitPriceMax == null ? "" : goodsUnitPriceMax;
    }

    public void setGoodsUnitPriceMax(String goodsUnitPriceMax) {
        this.goodsUnitPriceMax = goodsUnitPriceMax;
    }

    public String getGoodsUnitPriceMin() {
        return goodsUnitPriceMin == null ? "" : goodsUnitPriceMin;
    }

    public void setGoodsUnitPriceMin(String goodsUnitPriceMin) {
        this.goodsUnitPriceMin = goodsUnitPriceMin;
    }

    public String getSearchType() {
        return searchType == null ? GoodsConstants.INPUT_SEARCH_TYPE : searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getCountSort() {
        return countSort == null ? "" : countSort;
    }

    public void setCountSort(String countSort) {
        this.countSort = countSort;
    }

    public String getExpireSort() {
        return expireSort == null ? "" : expireSort;
    }

    public void setExpireSort(String expireSort) {
        this.expireSort = expireSort;
    }

    public String getPriceSort() {
        return priceSort == null ? "" : priceSort;
    }

    public void setPriceSort(String priceSort) {
        this.priceSort = priceSort;
    }

    public String getSearchKey() {
        return searchKey == null ? "" : searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getExpired() {
        return expired == null ? "" : expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public void resetDefaultData() {
        //当前页码
        current = 1;
        //每页条数
        size = 10;
        //优惠类型（1 直降 2 折扣 3 满减)
        discountType = "";
        //有效期最大值
        expireMax = "";
        //有效期最小值
        expireMin = "";
        //实时库存最大值
        goodsRealStockMax = "";
        //实时库存最小值
        goodsRealStockMin = "";
        //商品类型
        goodsType = "";
        //商品零售价最大值
        goodsUnitPriceMax = "";
        //商品零售价最小值
        goodsUnitPriceMin = "";
        //搜索类型 搜索框(input) 弹窗筛选(dialog)
        searchType = GoodsConstants.INPUT_SEARCH_TYPE;
        //库存排序（0 正序 1 倒叙）
        countSort = "";
        //有效期排序（0 正序 1 倒叙）
        expireSort = "";
        //售价排序（0 正序 1 倒叙）
        priceSort = "";
        //搜索关键字
        searchKey = "";
        //是否查过期（1 查过期 其他不查）
        expired = "";
    }
}
