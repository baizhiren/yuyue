package com.yuyue.backend.constant;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
public enum RoomStatus {
    NOT_AVAILABLE(0, "不可预约"),
    NOT_BOOK(1, "未预约"),
    BOOKED(2, "已预约");

    int code;
    String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
