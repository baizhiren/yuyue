package com.yuyue.backend.tool;

import com.google.gson.Gson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    public static Date stringToDate(String strDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);

        // 将LocalDateTime转换为Date
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println(new Gson().toJson(date));
        return date;
    }

    public static Date LocalToDate(LocalDateTime localDateTime){
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));

        // 将ZonedDateTime转换为Instant
        Instant instant = zonedDateTime.toInstant();

        // 从Instant转换为Date
        Date date = Date.from(instant);

        return date;

    }





    public static void main(String[] args) {
        stringToDate("2024-03-25 10:00:00");
    }





}
