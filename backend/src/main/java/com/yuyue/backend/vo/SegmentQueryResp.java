package com.yuyue.backend.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SegmentQueryResp {
    /**
     * 时段id
     */
    @JsonProperty("tId")
    private int tId;

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

    /**
     * 时段状态（0：不可用  1： 未预约  2： 已预约）
     */
    private int status;

    private String studentName;

    private String teacherName;
}
