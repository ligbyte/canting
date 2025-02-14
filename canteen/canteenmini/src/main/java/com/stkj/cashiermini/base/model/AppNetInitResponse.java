package com.stkj.cashiermini.base.model;

import android.text.TextUtils;

public class AppNetInitResponse {
    private String Code;
    private String Message;
    private ShopInitInfo Data;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public ShopInitInfo getData() {
        return Data;
    }

    public void setData(ShopInitInfo data) {
        Data = data;
    }

    public boolean isSuccess() {
        return TextUtils.equals(Code, "200");
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "Code='" + Code + '\'' +
                ", Message='" + Message + '\'' +
                '}';
    }
}
