package com.yuyue.backend.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserSaveToRedis {

    @TableId(type = IdType.AUTO)
    @JsonProperty("uId")
    private int uId;

    @JsonProperty("vId")
    private String vId;

    @JsonProperty("uName")
    private String uName;

    private String teacherName;

}
