package com.stkj.supermarketmini.goods.data;

import com.stkj.supermarketmini.R;
import com.stkj.supermarketmini.base.model.CommonExpandItem;

import java.util.ArrayList;
import java.util.List;

public class GoodsConstants {

    public static final String SPEC_WEIGHT_GOODS = "千克";

    public static final String INPUT_SEARCH_TYPE = "input";
    public static final String DIALOG_SEARCH_TYPE = "dialog";

    public static final int TYPE_DATE_DAY = 0;
    public static final int TYPE_DATE_MONTH = 1;
    public static final int TYPE_DATE_YEAR = 2;

    public static List<CommonExpandItem> getGoodsExpireSpecList() {
        List<CommonExpandItem> commonExpandItems = new ArrayList<>();
        commonExpandItems.add(new CommonExpandItem(TYPE_DATE_DAY, "天"));
        commonExpandItems.add(new CommonExpandItem(TYPE_DATE_MONTH, "月"));
        commonExpandItems.add(new CommonExpandItem(TYPE_DATE_YEAR, "年"));
        return commonExpandItems;
    }

    //标准
    public static final int TYPE_GOODS_STANDARD = 0;
    //称重
    public static final int TYPE_GOODS_WEIGHT = 1;

    public static final int[] GOODS_TYPE_LIST = new int[]{TYPE_GOODS_STANDARD, TYPE_GOODS_WEIGHT};

    public static String getGoodsTypeFromType(int type) {
        String goodsType = "标准商品";
        switch (type) {
            case TYPE_GOODS_STANDARD:
                goodsType = "标准商品";
                break;
            case TYPE_GOODS_WEIGHT:
                goodsType = "称重商品";
                break;
        }
        return goodsType;
    }

    public static List<CommonExpandItem> getGoodsTypeList() {
        List<CommonExpandItem> commonExpandItems = new ArrayList<>();
        commonExpandItems.add(new CommonExpandItem(TYPE_GOODS_STANDARD, "标准商品"));
        commonExpandItems.add(new CommonExpandItem(TYPE_GOODS_WEIGHT, "称重商品"));
        return commonExpandItems;
    }

    //直降
    public static final int TYPE_DISCOUNT_TAG_ZHIJIANG = 1;
    //折扣
    public static final int TYPE_DISCOUNT_TAG_ZHEKOU = 2;
    //满减
    public static final int TYPE_DISCOUNT_TAG_MANJIAN = 3;

    public static List<CommonExpandItem> getGoodsDiscountTagList() {
        List<CommonExpandItem> commonExpandItems = new ArrayList<>();
        commonExpandItems.add(new CommonExpandItem(TYPE_DISCOUNT_TAG_ZHIJIANG, "直降"));
        commonExpandItems.add(new CommonExpandItem(TYPE_DISCOUNT_TAG_ZHEKOU, "折扣"));
        commonExpandItems.add(new CommonExpandItem(TYPE_DISCOUNT_TAG_MANJIAN, "满减"));
        return commonExpandItems;
    }

    //库存排序
    public static final int TYPE_SORT_STORAGE = 0;
    //价格排序
    public static final int TYPE_SORT_EXPIRE = 1;
    //价格排序
    public static final int TYPE_SORT_PRICE = 2;

    public static int getExpireDayColor(int expireDay) {
        if (expireDay < 0) {
            return 0xffFF3C30;
        } else if (expireDay <= 30) {
            return 0xffFF8E1E;
        } else {
            return 0xff20D396;
        }
    }

    public static int getDiscountTagImage(int type) {
        if (type == TYPE_DISCOUNT_TAG_ZHIJIANG) {
            return R.mipmap.icon_tag_zhijiang;
        } else if (type == TYPE_DISCOUNT_TAG_ZHEKOU) {
            return R.mipmap.icon_tag_zhekou;
        } else if (type == TYPE_DISCOUNT_TAG_MANJIAN) {
            return R.mipmap.icon_tag_manjian;
        }
        return 0;
    }

}
