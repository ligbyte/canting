package com.stkj.supermarket.base.model;

public class CommonIdName {
    private String id;
    private String name;

    public CommonIdName() {
    }

    public CommonIdName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void reset() {
        this.id = "";
        this.name = "";
    }
}
