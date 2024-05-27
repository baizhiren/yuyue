package com.yuyue.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Book {
    @TableId(type = IdType.AUTO)
    private int bookId;

    @JsonProperty("uId")
    private int uId;


    private Date bookTime;

    public static void main(String[] args) {
        System.out.println(Integer.numberOfLeadingZeros(5));
        System.out.println(4 << 3);
    }
}
