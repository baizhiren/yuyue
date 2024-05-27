package com.yuyue.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RecordResp {

    private int bookId;

    /**
     预约时间
     */
    private Date bookTime;

    /**
     * 会议室名
     */
    private String roomName;

    private Integer week;

    private List<Segment> segments;
}


