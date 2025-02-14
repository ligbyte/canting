package com.stkj.supermarket.login.model;

import java.util.List;

public class UserInfo {
    private String token;
    private AccountInfo userInfo;

    public UserInfo() {
    }

    public String getToken() {
        if (token == null) {
            return "";
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AccountInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(AccountInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class AccountInfo {

        public static final String ACCOUNT_PERMISSION_CHANGE_PRICE = "changePrice";

        private String account;
        private String faceImg;
        private List<String> mobileButtonCodeList;

        public AccountInfo() {
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getFaceImg() {
            return faceImg;
        }

        public void setFaceImg(String faceImg) {
            this.faceImg = faceImg;
        }

        public List<String> getMobileButtonCodeList() {
            return mobileButtonCodeList;
        }

        public void setMobileButtonCodeList(List<String> mobileButtonCodeList) {
            this.mobileButtonCodeList = mobileButtonCodeList;
        }

    }
}
