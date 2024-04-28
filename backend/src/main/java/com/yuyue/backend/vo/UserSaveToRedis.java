package com.yuyue.backend.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserSaveToRedis {

    @JsonProperty("uId")
    private int uId;

    @JsonProperty("uName")
    private String uName;

    private String teacherName;

    private int bookCount;

    private int status;
}
