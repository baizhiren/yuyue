package com.yuyue.backend.exception;

import io.renren.common.exception.RRException;

public enum MakeAppointmentErrorEnum {

    USER_NOT_LOGIN(2001, "用户未登录"),
    SEGMENT_ALREADY_BOOKED(2002, "时段已被预约"),
    DATABASE_UPDATE_ERROR(2003, "预约成功，但插入预约数据异常"),
    REACH_MAX_BOOK_LIMIT(2004, "超过预约次数上限"),
    SEGMENT_NOT_EXIST(2005, "时段不存在"),
    NAME_EMPTY(2006, "用户信息不能为空");

    private final int code;
    private final String message;

    MakeAppointmentErrorEnum(int code, String message) {
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
