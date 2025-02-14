package com.stkj.supermarketmini.goods.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 商品信息校验
 */
public class GoodsCheckHelper {

    public static final SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat productDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean isValidDecimalNumber(String number) {
        try {
            Double.parseDouble(number);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isValidDate(String date) {
        try {
            parseDateFormat.parse(date);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String formatValidDate(String date) {
        try {
            Date parse = parseDateFormat.parse(date);
            return productDateFormat.format(parse);
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

}
