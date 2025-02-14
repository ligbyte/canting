package com.stkj.supermarket.home.model;

import java.util.List;

public class HomeMenuList {

    private String code;
    private List<Menu> children;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    public static class Menu {
        private String path;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Menu() {
        }

        public Menu(String path, String name) {
            this.path = path;
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
