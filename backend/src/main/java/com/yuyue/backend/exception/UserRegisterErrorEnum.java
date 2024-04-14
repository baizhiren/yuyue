package com.yuyue.backend.exception;

import io.renren.common.exception.RRException;

public enum UserRegisterErrorEnum {

    USER_ALREADY_EXIST(1001, "用户已存在"),
    WeChatCodeError(1002, "微信code错误");

    private final int code;
    private final String message;

    UserRegisterErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 将枚举转换为自定义异常
     */
    public RRException toException() {
        return new RRException(this.message, this.code);
    }
}