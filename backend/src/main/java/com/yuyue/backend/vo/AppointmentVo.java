package com.yuyue.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AppointmentVo {

    @JsonProperty("tIds")
    List<Integer> tIds;

    Integer week;

    String roomName;

}
