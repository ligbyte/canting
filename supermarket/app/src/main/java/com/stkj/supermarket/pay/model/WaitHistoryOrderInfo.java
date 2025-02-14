package com.stkj.supermarket.pay.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.List;

/**
 * 挂单订单
 */
@Entity
public class WaitHistoryOrderInfo {
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id = null;
    private String totalPrice;
    private String totalCount;
    private long orderCreateTime;
    private String formatOrderCreateTime;
    @Convert(columnType = String.class, converter = GreenDaoGoodsOrderInfoConvert.class)
    private List<GoodsOrderListInfo> orderListInfoList;

    public WaitHistoryOrderInfo(String totalPrice, String totalCount, long orderCreateTime, String formatOrderCreateTime, List<GoodsOrderListInfo> orderListInfoList) {
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
        this.orderCreateTime = orderCreateTime;
        this.formatOrderCreateTime = formatOrderCreateTime;
        this.orderListInfoList = orderListInfoList;
    }

    @Generated(hash = 1175796357)
    public WaitHistoryOrderInfo(Long id, String totalPrice, String totalCount, long orderCreateTime, String formatOrderCreateTime,
                                List<GoodsOrderListInfo> orderListInfoList) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.totalCount = totalCount;
        this.orderCreateTime = orderCreateTime;
        this.formatOrderCreateTime = formatOrderCreateTime;
        this.orderListInfoList = orderListInfoList;
    }

    @Generated(hash = 1010139106)
    public WaitHistoryOrderInfo() {
    }

    public String getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public long getOrderCreateTime() {
        return this.orderCreateTime;
    }

    public void setOrderCreateTime(long orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public void setFormatOrderCreateTime(String formatOrderCreateTime) {
        this.formatOrderCreateTime = formatOrderCreateTime;
    }

    public String getFormatOrderCreateTime() {
        return this.formatOrderCreateTime;
    }

    public String getGoodsListName() {
        if (orderListInfoList == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int size = orderListInfoList.size();
        for (int i = 0; i < size; i++) {
            builder.append(orderListInfoList.get(i).getGoodsName())
                    .append(";");
        }
        return builder.toString();
    }

    public boolean isCurrentDayOrder() {
        long dayInterval = 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - orderCreateTime <= dayInterval;
    }

    public List<GoodsOrderListInfo> getOrderListInfoList() {
        return this.orderListInfoList;
    }

    public void setOrderListInfoList(List<GoodsOrderListInfo> orderListInfoList) {
        this.orderListInfoList = orderListInfoList;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
