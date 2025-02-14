package com.stkj.cashier.pay.model;

import java.util.List;

/**
 * 取餐订单
 */
public class TakeMealListItem {
    private String Card_Number;
    private String orderNumber;
    private List<FoodList> foodList;
    private String Full_Name;
    private String User_Tel;
    private String takeCode;
    private String User_Face;
    private boolean itemCancel;
    private int takeType;

    public TakeMealListItem() {
    }

    public String getCard_Number() {
        return Card_Number;
    }

    public void setCard_Number(String card_Number) {
        Card_Number = card_Number;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<FoodList> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<FoodList> foodList) {
        this.foodList = foodList;
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

    public String getTakeCode() {
        return takeCode;
    }

    public void setTakeCode(String takeCode) {
        this.takeCode = takeCode;
    }

    public String getUser_Face() {
        return User_Face;
    }

    public void setUser_Face(String user_Face) {
        User_Face = user_Face;
    }

    public boolean isItemCancel() {
        return itemCancel;
    }

    public void setItemCancel(boolean itemCancel) {
        this.itemCancel = itemCancel;
    }

    public int getTakeType() {
        return takeType;
    }

    public void setTakeType(int takeType) {
        this.takeType = takeType;
    }

    public static class FoodList {
        private String Number;
        private String Time_dining;
        private String Dish_name;
        private String Meal_Amount;
        private String Image;

        public FoodList() {
        }

        public String getNumber() {
            return Number;
        }

        public void setNumber(String number) {
            Number = number;
        }

        public String getTime_dining() {
            return Time_dining;
        }

        public void setTime_dining(String time_dining) {
            Time_dining = time_dining;
        }

        public String getDish_name() {
            return Dish_name;
        }

        public void setDish_name(String dish_name) {
            Dish_name = dish_name;
        }

        public String getMeal_Amount() {
            return Meal_Amount;
        }

        public void setMeal_Amount(String meal_Amount) {
            Meal_Amount = meal_Amount;
        }

        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
        }
    }
}
