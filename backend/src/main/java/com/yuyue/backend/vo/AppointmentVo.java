package com.yuyue.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AppointmentVo {

    @JsonProperty("tId")
    private int tId;

    Integer week;

    String roomName;

}
