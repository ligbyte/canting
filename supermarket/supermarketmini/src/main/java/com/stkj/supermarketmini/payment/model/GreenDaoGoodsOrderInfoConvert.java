package com.stkj.supermarketmini.payment.model;

import com.stkj.supermarketmini.base.utils.JacksonUtils;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

/**
 * 订单列表商品数据库转换器
 */
public class GreenDaoGoodsOrderInfoConvert implements PropertyConverter<List<GoodsOrderListInfo>, String> {
    @Override
    public List<GoodsOrderListInfo> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        return JacksonUtils.convertJsonArray(databaseValue, GoodsOrderListInfo.class);
    }

    @Override
    public String convertToDatabaseValue(List<GoodsOrderListInfo> orderListInfoList) {
        if (orderListInfoList == null) {
            return null;
        } else {
            return JacksonUtils.convertJsonString(orderListInfoList);
        }
    }
}
