package com.yuyue.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppointmentVo {

    @JsonProperty("tIds")
    private  List<Integer> tIds;

    private Integer week;

    private String roomName;

    private String studentName;

    private String teacherName;
}
