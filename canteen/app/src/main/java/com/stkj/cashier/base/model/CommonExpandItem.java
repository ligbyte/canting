package com.stkj.cashier.base.model;

public class CommonExpandItem {
    private String type;
    private String name;
    private boolean allowEdit;
    private boolean allowDel;

    public CommonExpandItem(int type, String name) {
        this.type = String.valueOf(type);
        this.name = name;
    }

    public CommonExpandItem(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public CommonExpandItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public int getTypeInt() {
        int type = 0;
        try {
            type = Integer.parseInt(getType());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return type;
    }

    public Double getTypeDouble() {
        double type = 0;
        try {
            type = Double.parseDouble(getType());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    public boolean isAllowEdit() {
        return allowEdit;
    }

    public void setAllowDel(boolean allowDel) {
        this.allowDel = allowDel;
    }

    public boolean isAllowDel() {
        return allowDel;
    }
}
