package com.yuyue.backend.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus {
    ACTIVE(1, "激活"),
    FORBIDDEN(2, "禁用"),
    LOGOUT(3, "已注销");

    int code;
    String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
