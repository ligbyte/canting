package com.stkj.common.printer.bean;

public class Dish {
    private String name;
    private String note;
    private double price;
    private int count;

    public Dish(String name, String note, double price, int count) {
        this.name = name;
        this.note = note;
        this.price = price;
        this.count = count;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

