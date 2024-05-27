package com.yuyue.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CancelBookVo {

    private Integer week;

    private String roomName;

    private Integer bookId;

}
