package com.yuyue.backend.exception;

import io.renren.common.exception.RRException;
import lombok.Data;

public enum CancelBookErrorEnum {

    SEGMENT_NOT_VALID(3001,"时段不存在"),
    SEGMENT_ALREADY_EXPIRE(3002,"该时段已过期"),
    SEGMENT_NOT_BELONG_CURRENT_USER(3003,"该时段不属于本用户"),
    SEGMENT_NOT_BOOK(3004,"该时段还没有被预约"),
    REDIS_KEY_DELETE_ERROR(3005,"redis key 删除错误"),
    UPDATE_SEGMENT_TABLE_ERROR(3006,"修改SEGMENT表错误"),
    DELETE_BOOK_TABLE_ERROR(3007,"删除BOOK表错误"),
    DELETE_BOOK_AND_SEGMENT_TABLE_ERROR(3008, "删除BOOK_SEGMENT表错误");

    private final int code;
    private final String message;

    CancelBookErrorEnum(int code, String message) {
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
