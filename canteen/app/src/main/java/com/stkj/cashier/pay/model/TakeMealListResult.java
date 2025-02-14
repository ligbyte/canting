package com.stkj.cashier.pay.model;

import com.stkj.cashier.base.model.BaseNetResponse;

import java.util.List;

/**
 * 取餐订单列表
 */
public class TakeMealListResult extends BaseNetResponse<List<TakeMealListItem>> {

    private String Card_Number;
    private String Full_Name;
    private String User_Tel;
    private String User_Face;
    private String Balance;
    private String orderNumber;

    public TakeMealListResult() {
    }

    public String getCard_Number() {
        return Card_Number;
    }

    public void setCard_Number(String card_Number) {
        Card_Number = card_Number;
    }

    public String getFull_Name() {
        return Full_Name;
    }

    public void setFull_Name(String full_Name) {
        Full_Name = full_Name;
    }

    public String getUser_Tel() {
        return User_Tel;
    }

    public void setUser_Tel(String user_Tel) {
        User_Tel = user_Tel;
    }

    public String getUser_Face() {
        return User_Face;
    }

    public void setUser_Face(String user_Face) {
        User_Face = user_Face;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}