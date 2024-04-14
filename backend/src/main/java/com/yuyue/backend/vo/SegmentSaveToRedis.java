package com.yuyue.backend.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SegmentSaveToRedis {

    /**
     * 会议室名
     */
    @JsonProperty("rName")
    private String rName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

}
